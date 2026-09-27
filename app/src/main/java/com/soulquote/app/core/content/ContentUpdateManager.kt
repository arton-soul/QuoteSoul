package com.soulquote.app.core.content

import android.content.Context
import androidx.room.withTransaction
import com.soulquote.app.BuildConfig
import com.soulquote.app.data.local.SoulQuoteContentDatabase
import com.soulquote.app.data.local.entity.content.AppConfigEntity
import com.soulquote.app.data.remote.model.ContentManifest
import com.soulquote.app.data.remote.model.ContentPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

data class ContentStats(
    val contentVersion: Int,
    val totalQuotes: Int,
    val totalMeditations: Int
)

sealed class UpdateCheckResult {
    data class UpdateAvailable(val manifest: ContentManifest) : UpdateCheckResult()
    data class UpToDate(val currentVersion: Int) : UpdateCheckResult()
    data class AppUpdateRequired(val minAppVersion: Int) : UpdateCheckResult()
    data class Error(val message: String) : UpdateCheckResult()
}

sealed class UpdateProgress {
    object Idle : UpdateProgress()
    object Checking : UpdateProgress()
    data class Downloading(val progress: Float) : UpdateProgress()
    object VerifyingChecksum : UpdateProgress()
    object Validating : UpdateProgress()
    object ApplyingDatabaseTransaction : UpdateProgress()
    data class Success(
        val newVersion: Int,
        val totalQuotes: Int,
        val totalMeditations: Int
    ) : UpdateProgress()
    data class Failed(val errorMessage: String) : UpdateProgress()
}

class ContentUpdateManager(
    private val context: Context,
    private val database: SoulQuoteContentDatabase
) {
    private val stagingDir: File by lazy {
        File(context.cacheDir, "staging").apply {
            if (!exists()) mkdirs()
        }
    }

    suspend fun getCurrentContentVersion(): Int = withContext(Dispatchers.IO) {
        database.appConfigDao().getConfigValue("content_version")?.toIntOrNull() ?: 1
    }

    suspend fun getContentStats(): ContentStats = withContext(Dispatchers.IO) {
        val version = getCurrentContentVersion()
        val quotes = database.quoteDao().getActiveQuoteCount()
        val meditations = database.meditationDao().getActiveMeditationCount()
        ContentStats(
            contentVersion = version,
            totalQuotes = quotes,
            totalMeditations = meditations
        )
    }

    suspend fun checkForUpdates(manifestJsonOverride: String? = null): UpdateCheckResult = withContext(Dispatchers.IO) {
        try {
            val manifestJson = manifestJsonOverride ?: loadDefaultManifestJson()
            if (manifestJson == null) {
                return@withContext UpdateCheckResult.UpToDate(getCurrentContentVersion())
            }

            val manifest = ContentManifest.fromJson(manifestJson)
            val currentVersion = getCurrentContentVersion()

            if (manifest.minAppVersion > BuildConfig.VERSION_CODE) {
                return@withContext UpdateCheckResult.AppUpdateRequired(manifest.minAppVersion)
            }

            if (manifest.contentVersion > currentVersion) {
                UpdateCheckResult.UpdateAvailable(manifest)
            } else {
                UpdateCheckResult.UpToDate(currentVersion)
            }
        } catch (e: Exception) {
            UpdateCheckResult.Error(e.message ?: "Gagal memeriksa pembaruan konten.")
        }
    }

    private fun loadDefaultManifestJson(): String? {
        return try {
            context.assets.open("seed/content_manifest.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            null
        }
    }

    fun applyUpdate(
        manifest: ContentManifest,
        customInputStreamProvider: (suspend () -> InputStream)? = null
    ): Flow<UpdateProgress> = flow {
        emit(UpdateProgress.Downloading(0f))

        val stagingFile = File(stagingDir, "content_update_v${manifest.contentVersion}.tmp")
        try {
            // 1. Download to staging
            val inputStream: InputStream = if (customInputStreamProvider != null) {
                customInputStreamProvider()
            } else if (manifest.packageUrl.startsWith("http://") || manifest.packageUrl.startsWith("https://")) {
                val url = URL(manifest.packageUrl)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 15000
                    readTimeout = 15000
                    instanceFollowRedirects = true
                }
                conn.inputStream
            } else if (manifest.packageUrl.startsWith("asset://")) {
                val assetPath = manifest.packageUrl.removePrefix("asset://")
                context.assets.open(assetPath)
            } else {
                // Fallback check assets
                context.assets.open("seed/content_update_v4.json")
            }

            val outputStream = FileOutputStream(stagingFile)
            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalRead = 0L
            val expectedSize = if (manifest.packageSizeBytes > 0) manifest.packageSizeBytes else 50000L

            inputStream.use { input ->
                outputStream.use { output ->
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        val progress = (totalRead.toFloat() / expectedSize).coerceIn(0.1f, 0.95f)
                        emit(UpdateProgress.Downloading(progress))
                    }
                }
            }

            emit(UpdateProgress.Downloading(1.0f))

            // 2. Checksum Verification (SHA-256)
            emit(UpdateProgress.VerifyingChecksum)
            val actualChecksum = calculateSha256(stagingFile)
            val expectedChecksum = manifest.packageChecksumSha256.trim().lowercase()

            if (expectedChecksum.isNotBlank() && actualChecksum != expectedChecksum) {
                stagingFile.delete()
                emit(UpdateProgress.Failed("Integritas berkas gagal: Checksum SHA-256 tidak cocok ($actualChecksum != $expectedChecksum)"))
                return@flow
            }

            // 3. Validation & Parsing
            emit(UpdateProgress.Validating)
            val packageJson = stagingFile.readText()
            val contentPackage = ContentPackage.fromJson(packageJson)

            if (contentPackage.contentVersion < manifest.contentVersion) {
                stagingFile.delete()
                emit(UpdateProgress.Failed("Versi paket (${contentPackage.contentVersion}) tidak sesuai dengan manifes (${manifest.contentVersion})"))
                return@flow
            }

            // 4. Atomic Database Transaction with Rollback Protection
            emit(UpdateProgress.ApplyingDatabaseTransaction)
            database.withTransaction {
                if (contentPackage.categories.isNotEmpty()) {
                    database.quoteDao().insertCategories(contentPackage.categories)
                }
                if (contentPackage.quotes.isNotEmpty()) {
                    database.quoteDao().insertQuotes(contentPackage.quotes)
                }
                if (contentPackage.templates.isNotEmpty()) {
                    database.templateDao().insertTemplates(contentPackage.templates)
                }
                if (contentPackage.meditationCategories.isNotEmpty()) {
                    database.meditationDao().insertCategories(contentPackage.meditationCategories)
                }
                if (contentPackage.meditations.isNotEmpty()) {
                    database.meditationDao().insertMeditations(contentPackage.meditations)
                }

                database.appConfigDao().setConfigValue(
                    AppConfigEntity("content_version", manifest.contentVersion.toString())
                )
            }

            // 5. Cleanup & Success
            stagingFile.delete()
            val totalQuotes = database.quoteDao().getActiveQuoteCount()
            val totalMeditations = database.meditationDao().getActiveMeditationCount()

            emit(
                UpdateProgress.Success(
                    newVersion = manifest.contentVersion,
                    totalQuotes = totalQuotes,
                    totalMeditations = totalMeditations
                )
            )
        } catch (e: Exception) {
            stagingFile.delete()
            emit(UpdateProgress.Failed("Gagal menerapkan pembaruan: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    fun calculateSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
