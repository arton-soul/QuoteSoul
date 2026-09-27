package com.soulquote.app.core.content

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

sealed class DriveNetworkException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NoInternetConnection : DriveNetworkException("Tidak ada koneksi internet. Periksa Wi-Fi atau data seluler.")
    class StorageQuotaExceeded : DriveNetworkException("Batas kuota unduhan server Google Drive tercapai untuk berkas ini. Silakan coba lagi nanti.")
    class Timeout(message: String = "Waktu koneksi ke server Google Drive habis (timeout).") : DriveNetworkException(message)
    class FileNotFound(fileIdOrUrl: String) : DriveNetworkException("Berkas tidak ditemukan di server Google Drive: $fileIdOrUrl")
    class HttpError(val statusCode: Int, message: String) : DriveNetworkException("Server mengembalikan kode HTTP $statusCode: $message")
}

class DriveContentClient(
    private val context: Context,
    private val connectTimeoutMs: Int = 15000,
    private val readTimeoutMs: Int = 30000,
    private val maxRetries: Int = 3
) {

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun fetchManifestString(urlOrId: String): Result<String> = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable()) {
            return@withContext Result.failure(DriveNetworkException.NoInternetConnection())
        }

        val directUrl = GoogleDriveUrlResolver.resolveDirectDownloadUrl(urlOrId)
        executeWithRetry("fetchManifest") {
            downloadStringInternal(directUrl)
        }
    }

    suspend fun downloadPackageToFile(
        urlOrId: String,
        targetFile: File,
        expectedSizeBytes: Long = 0L,
        onProgress: ((Float) -> Unit)? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable()) {
            return@withContext Result.failure(DriveNetworkException.NoInternetConnection())
        }

        val directUrl = GoogleDriveUrlResolver.resolveDirectDownloadUrl(urlOrId)
        executeWithRetry("downloadPackage") {
            downloadFileInternal(directUrl, targetFile, expectedSizeBytes, onProgress)
        }
    }

    private suspend fun <T> executeWithRetry(
        actionName: String,
        block: suspend () -> T
    ): Result<T> {
        var lastException: Throwable? = null
        var delayMs = 1000L

        for (attempt in 1..maxRetries) {
            try {
                return Result.success(block())
            } catch (e: DriveNetworkException.NoInternetConnection) {
                return Result.failure(e)
            } catch (e: DriveNetworkException.StorageQuotaExceeded) {
                return Result.failure(e)
            } catch (e: DriveNetworkException.FileNotFound) {
                return Result.failure(e)
            } catch (e: DriveNetworkException.HttpError) {
                if (e.statusCode in 400..499 && e.statusCode != 408 && e.statusCode != 429) {
                    return Result.failure(e)
                }
                lastException = e
            } catch (e: SocketTimeoutException) {
                lastException = DriveNetworkException.Timeout("Waktu koneksi habis pada percobaan $attempt ($actionName)")
            } catch (e: ConnectException) {
                lastException = DriveNetworkException.Timeout("Gagal menyambung ke server pada percobaan $attempt ($actionName)")
            } catch (e: UnknownHostException) {
                lastException = DriveNetworkException.Timeout("Server tidak ditemukan (DNS lookup gagal) pada percobaan $attempt")
            } catch (e: IOException) {
                lastException = e
            } catch (e: Exception) {
                return Result.failure(e)
            }

            if (attempt < maxRetries) {
                delay(delayMs)
                delayMs *= 2
            }
        }

        return Result.failure(lastException ?: IOException("Gagal menyelesaikan operasi $actionName setelah $maxRetries percobaan."))
    }

    private fun openConnectionWithRedirects(initialUrl: String, maxRedirects: Int = 5): HttpURLConnection {
        var currentUrl = initialUrl
        var redirects = 0
        var cookies: String? = null

        while (redirects < maxRedirects) {
            val url = URL(currentUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = connectTimeoutMs
                readTimeout = readTimeoutMs
                instanceFollowRedirects = false // Handle manually to preserve cookies/queries
                setRequestProperty("User-Agent", "Mozilla/5.0 (Android; SoulQuote-Content-Engine/1.0)")
                if (cookies != null) {
                    setRequestProperty("Cookie", cookies)
                }
            }

            val status = connection.responseCode

            // Extract cookies if any
            val setCookie = connection.headerFields["Set-Cookie"]
            if (!setCookie.isNullOrEmpty()) {
                cookies = setCookie.joinToString("; ") { it.substringBefore(";") }
            }

            if (status == HttpURLConnection.HTTP_MOVED_TEMP ||
                status == HttpURLConnection.HTTP_MOVED_PERM ||
                status == HttpURLConnection.HTTP_SEE_OTHER ||
                status == 307 || status == 308
            ) {
                val newUrl = connection.getHeaderField("Location")
                connection.disconnect()
                if (newUrl != null) {
                    currentUrl = if (newUrl.startsWith("http://") || newUrl.startsWith("https://")) {
                        newUrl
                    } else {
                        URL(url, newUrl).toString()
                    }
                    redirects++
                    continue
                }
            }

            return connection
        }

        throw IOException("Terlalu banyak pengalihan (redirects) saat mengakses: $initialUrl")
    }

    private fun downloadStringInternal(directUrl: String): String {
        val conn = openConnectionWithRedirects(directUrl)
        try {
            val status = conn.responseCode
            if (status == 404) {
                throw DriveNetworkException.FileNotFound(directUrl)
            }
            if (status == 403 || status == 429) {
                throw DriveNetworkException.StorageQuotaExceeded()
            }
            if (status !in 200..299) {
                throw DriveNetworkException.HttpError(status, conn.responseMessage ?: "HTTP Error")
            }

            val content = conn.inputStream.bufferedReader().use { it.readText() }

            if (GoogleDriveUrlResolver.isQuotaExceededResponse(content)) {
                throw DriveNetworkException.StorageQuotaExceeded()
            }

            if (GoogleDriveUrlResolver.isVirusScanWarning(content)) {
                val confirmToken = GoogleDriveUrlResolver.extractConfirmToken(content)
                if (confirmToken != null) {
                    val confirmedUrl = "$directUrl&confirm=$confirmToken"
                    return downloadStringInternal(confirmedUrl)
                }
            }

            return content
        } finally {
            conn.disconnect()
        }
    }

    private fun downloadFileInternal(
        directUrl: String,
        targetFile: File,
        expectedSizeBytes: Long,
        onProgress: ((Float) -> Unit)?
    ): File {
        var conn = openConnectionWithRedirects(directUrl)
        try {
            var status = conn.responseCode
            if (status == 404) {
                throw DriveNetworkException.FileNotFound(directUrl)
            }
            if (status == 403 || status == 429) {
                throw DriveNetworkException.StorageQuotaExceeded()
            }
            if (status !in 200..299) {
                throw DriveNetworkException.HttpError(status, conn.responseMessage ?: "HTTP Error")
            }

            val contentType = conn.contentType ?: ""
            // Check if returned content is HTML instead of expected JSON or binary
            if (contentType.contains("text/html", ignoreCase = true)) {
                val htmlContent = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()

                if (GoogleDriveUrlResolver.isQuotaExceededResponse(htmlContent)) {
                    throw DriveNetworkException.StorageQuotaExceeded()
                }

                if (GoogleDriveUrlResolver.isVirusScanWarning(htmlContent)) {
                    val token = GoogleDriveUrlResolver.extractConfirmToken(htmlContent) ?: "t"
                    val confirmedUrl = "$directUrl&confirm=$token"
                    conn = openConnectionWithRedirects(confirmedUrl)
                    status = conn.responseCode
                    if (status !in 200..299) {
                        throw DriveNetworkException.HttpError(status, "Gagal mengunduh file terkonfirmasi")
                    }
                }
            }

            val contentLength = conn.contentLengthLong
            val totalExpected = if (expectedSizeBytes > 0) expectedSizeBytes else if (contentLength > 0) contentLength else 50000L

            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalRead = 0L

            FileOutputStream(targetFile).use { output ->
                conn.inputStream.use { input ->
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        val progress = (totalRead.toFloat() / totalExpected).coerceIn(0.05f, 0.98f)
                        onProgress?.invoke(progress)
                    }
                }
            }

            onProgress?.invoke(1.0f)
            return targetFile
        } finally {
            conn.disconnect()
        }
    }
}
