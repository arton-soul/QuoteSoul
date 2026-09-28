package com.soulquote.app.core.audio

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.soulquote.app.R
import com.soulquote.app.core.content.DriveContentClient
import com.soulquote.app.core.content.GoogleDriveUrlResolver
import com.soulquote.app.domain.model.Meditation
import com.soulquote.app.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest

data class DownloadProgress(
    val assetId: String,
    val isDownloading: Boolean = false,
    val progress: Float = 0f,
    val error: String? = null
)

class MeditationDownloadManager(
    private val context: Context,
    private val userRepository: UserRepository
) {

    private val audioDir: File
        get() = (context.getExternalFilesDir("audio") ?: File(context.filesDir, "audio")).apply {
            if (!exists()) mkdirs()
        }

    private val _downloadStates = MutableStateFlow<Map<String, DownloadProgress>>(emptyMap())
    val downloadStates: StateFlow<Map<String, DownloadProgress>> = _downloadStates.asStateFlow()

    fun isDownloadedLocally(meditation: Meditation): Boolean {
        // 1. Check external files directory (primary storage for media)
        val extAudioDir = context.getExternalFilesDir("audio")
        if (extAudioDir != null) {
            val extFile = File(extAudioDir, meditation.localFileName)
            if (extFile.exists() && extFile.length() > 0 && extFile.canRead()) {
                return true
            }
        }

        // 2. Check internal files directory (fallback)
        val internalFile = File(context.filesDir, "audio/${meditation.localFileName}")
        return internalFile.exists() && internalFile.length() > 0 && internalFile.canRead()
    }

    fun getAudioUri(meditation: Meditation): Uri {
        // 1. If downloaded in external files dir, play local file
        val extAudioDir = context.getExternalFilesDir("audio")
        if (extAudioDir != null) {
            val extFile = File(extAudioDir, meditation.localFileName)
            if (extFile.exists() && extFile.length() > 0 && extFile.canRead()) {
                return Uri.fromFile(extFile)
            }
        }

        // 2. If downloaded in internal files dir, play local file
        val internalFile = File(context.filesDir, "audio/${meditation.localFileName}")
        if (internalFile.exists() && internalFile.length() > 0 && internalFile.canRead()) {
            return Uri.fromFile(internalFile)
        }

        // 3. If not downloaded locally, stream directly from remote URL (Google Drive, etc.)
        if (meditation.audioUrl.isNotBlank() && !meditation.audioUrl.contains("assets.soulquote.dearyoti.com")) {
            val resolvedUrl = GoogleDriveUrlResolver.resolveDirectDownloadUrl(meditation.audioUrl)
            return Uri.parse(resolvedUrl)
        }

        // 4. Fallback to bundled offline ambient track
        return Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.meditation_bell_ambient}")
    }

    suspend fun downloadMeditation(meditation: Meditation): Result<File> = withContext(Dispatchers.IO) {
        val targetFile = File(audioDir, meditation.localFileName)
        val tempFile = File(audioDir, "${meditation.localFileName}.download")

        updateState(meditation.id, isDownloading = true, progress = 0.05f)

        try {
            // If already downloaded and readable, register and return
            if (targetFile.exists() && targetFile.length() > 0 && targetFile.canRead()) {
                userRepository.registerDownloadedAudio(
                    assetId = meditation.id,
                    assetType = "meditation",
                    path = targetFile.absolutePath,
                    size = targetFile.length(),
                    checksum = meditation.checksum
                )
                updateState(meditation.id, isDownloading = false, progress = 1.0f)
                return@withContext Result.success(targetFile)
            }

            // Attempt download using DriveContentClient with redirect and virus-scan token support
            var downloadSuccess = false
            try {
                val driveClient = DriveContentClient(
                    context = context,
                    connectTimeoutMs = 15000,
                    readTimeoutMs = 30000,
                    maxRetries = 2
                )
                val dlResult = driveClient.downloadPackageToFile(
                    urlOrId = meditation.audioUrl,
                    targetFile = tempFile,
                    expectedSizeBytes = meditation.sizeBytes
                ) { progress ->
                    val clampedProgress = progress.coerceIn(0.1f, 0.9f)
                    updateState(meditation.id, isDownloading = true, progress = clampedProgress)
                }
                downloadSuccess = dlResult.isSuccess && tempFile.exists() && tempFile.length() > 0
            } catch (_: Exception) {
                downloadSuccess = false
            }

            if (!downloadSuccess || !tempFile.exists() || tempFile.length() == 0L) {
                tempFile.delete()
                val errorMsg = "Gagal mengunduh audio dari server. Periksa koneksi internet."
                updateState(meditation.id, isDownloading = false, progress = 0f, error = errorMsg)
                return@withContext Result.failure(java.io.IOException(errorMsg))
            }

            updateState(meditation.id, isDownloading = true, progress = 0.95f)

            // Calculate SHA-256 Checksum
            val calculatedChecksum = computeSha256(tempFile)

            // Atomically rename .download to target file
            if (targetFile.exists()) {
                targetFile.delete()
            }
            if (!tempFile.renameTo(targetFile)) {
                tempFile.copyTo(targetFile, overwrite = true)
                tempFile.delete()
            }

            // Register in User Database
            userRepository.registerDownloadedAudio(
                assetId = meditation.id,
                assetType = "meditation",
                path = targetFile.absolutePath,
                size = targetFile.length(),
                checksum = calculatedChecksum
            )

            updateState(meditation.id, isDownloading = false, progress = 1.0f)
            Result.success(targetFile)
        } catch (e: Exception) {
            tempFile.delete()
            updateState(meditation.id, isDownloading = false, progress = 0f, error = e.localizedMessage)
            Result.failure(e)
        }
    }

    suspend fun deleteMeditationAudio(meditation: Meditation): Boolean = withContext(Dispatchers.IO) {
        try {
            // 1. Delete from external files dir
            val extDir = context.getExternalFilesDir("audio")
            if (extDir != null) {
                val extFile = File(extDir, meditation.localFileName)
                if (extFile.exists()) {
                    extFile.delete()
                }
            }

            // 2. Delete from internal files dir
            val internalFile = File(context.filesDir, "audio/${meditation.localFileName}")
            if (internalFile.exists()) {
                internalFile.delete()
            }

            // 3. Clean legacy Download file if present
            try {
                val pubDownload = File(
                    android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS),
                    meditation.localFileName
                )
                if (pubDownload.exists()) {
                    pubDownload.delete()
                }
            } catch (_: Exception) {}

            userRepository.removeDownloadedAudio(meditation.id)
            updateState(meditation.id, isDownloading = false, progress = 0f)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun updateState(id: String, isDownloading: Boolean, progress: Float, error: String? = null) {
        val current = _downloadStates.value.toMutableMap()
        current[id] = DownloadProgress(
            assetId = id,
            isDownloading = isDownloading,
            progress = progress,
            error = error
        )
        _downloadStates.value = current
    }

    private fun computeSha256(file: File): String {
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
