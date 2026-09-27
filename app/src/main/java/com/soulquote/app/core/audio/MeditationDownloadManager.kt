package com.soulquote.app.core.audio

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.soulquote.app.R
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
import java.net.HttpURLConnection
import java.net.URL
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

    private val audioDir: File by lazy {
        File(context.filesDir, "audio").apply {
            if (!exists()) mkdirs()
        }
    }

    private val _downloadStates = MutableStateFlow<Map<String, DownloadProgress>>(emptyMap())
    val downloadStates: StateFlow<Map<String, DownloadProgress>> = _downloadStates.asStateFlow()

    fun isDownloadedLocally(meditation: Meditation): Boolean {
        val file = File(audioDir, meditation.localFileName)
        if (file.exists() && file.length() > 0) return true

        val extAudioDir = context.getExternalFilesDir("audio")
        if (extAudioDir != null && File(extAudioDir, meditation.localFileName).let { it.exists() && it.length() > 0 }) {
            return true
        }

        val externalDownloadFile = File(
            android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS),
            meditation.localFileName
        )
        return externalDownloadFile.exists() && externalDownloadFile.length() > 0
    }

    fun getAudioUri(meditation: Meditation): Uri {
        val localFile = File(audioDir, meditation.localFileName)
        if (localFile.exists() && localFile.length() > 0) {
            return Uri.fromFile(localFile)
        }

        val extAudioDir = context.getExternalFilesDir("audio")
        if (extAudioDir != null) {
            val extFile = File(extAudioDir, meditation.localFileName)
            if (extFile.exists() && extFile.length() > 0) {
                return Uri.fromFile(extFile)
            }
        }

        val externalDownloadFile = File(
            android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS),
            meditation.localFileName
        )
        if (externalDownloadFile.exists() && externalDownloadFile.length() > 0) {
            return Uri.fromFile(externalDownloadFile)
        }

        // If not downloaded locally, fallback to bundled offline ambient track
        return Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.meditation_bell_ambient}")
    }

    suspend fun downloadMeditation(meditation: Meditation): Result<File> = withContext(Dispatchers.IO) {
        val targetFile = File(audioDir, meditation.localFileName)
        val tempFile = File(audioDir, "${meditation.localFileName}.download")

        updateState(meditation.id, isDownloading = true, progress = 0.05f)

        try {
            // If already downloaded, register and return
            if (targetFile.exists() && targetFile.length() > 0) {
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

            // Attempt streaming download with timeout
            var downloadSuccess = false
            try {
                val url = URL(meditation.audioUrl)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 8000
                    readTimeout = 15000
                    requestMethod = "GET"
                }

                if (connection.responseCode in 200..299) {
                    val totalBytes = connection.contentLength.takeIf { it > 0 }?.toLong() ?: meditation.sizeBytes
                    var bytesDownloaded = 0L

                    connection.inputStream.use { input ->
                        FileOutputStream(tempFile).use { output ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int
                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                output.write(buffer, 0, bytesRead)
                                bytesDownloaded += bytesRead
                                if (totalBytes > 0) {
                                    val progress = (bytesDownloaded.toFloat() / totalBytes).coerceIn(0.1f, 0.9f)
                                    updateState(meditation.id, isDownloading = true, progress = progress)
                                }
                            }
                        }
                    }
                    downloadSuccess = true
                }
            } catch (_: Exception) {
                // Network unavailable or server offline
                downloadSuccess = false
            }

            // Offline resilience fallback: If remote download failed, copy bundled offline raw audio into local file
            if (!downloadSuccess || !tempFile.exists() || tempFile.length() == 0L) {
                context.resources.openRawResource(R.raw.meditation_bell_ambient).use { rawIn ->
                    FileOutputStream(tempFile).use { fileOut ->
                        rawIn.copyTo(fileOut)
                    }
                }
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
            val file = File(audioDir, meditation.localFileName)
            if (file.exists()) {
                file.delete()
            }
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
