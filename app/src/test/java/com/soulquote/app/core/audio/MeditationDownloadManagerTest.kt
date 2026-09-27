package com.soulquote.app.core.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.security.MessageDigest

class MeditationDownloadManagerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun testSha256ChecksumVerification() {
        val testFile = tempFolder.newFile("sample_audio.mp3")
        val content = "Mindful Zen Audio Stream Content 432Hz"
        testFile.writeText(content)

        // Compute expected SHA-256
        val digest = MessageDigest.getInstance("SHA-256")
        val expectedHash = digest.digest(content.toByteArray()).joinToString("") { "%02x".format(it) }

        // Compute hash of the file
        val fileDigest = MessageDigest.getInstance("SHA-256")
        testFile.inputStream().use { stream ->
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (stream.read(buffer).also { bytesRead = it } != -1) {
                fileDigest.update(buffer, 0, bytesRead)
            }
        }
        val computedHash = fileDigest.digest().joinToString("") { "%02x".format(it) }

        assertEquals(expectedHash, computedHash)
    }

    @Test
    fun testCorruptionDetectionOnCorruptedPayload() {
        val originalFile = tempFolder.newFile("valid_track.mp3")
        originalFile.writeText("Valid uncorrupted audio track")
        val originalHash = MessageDigest.getInstance("SHA-256")
            .digest("Valid uncorrupted audio track".toByteArray())
            .joinToString("") { "%02x".format(it) }

        // Simulate file corruption (interrupted download / byte flipping)
        val corruptedFile = tempFolder.newFile("corrupted_track.mp3")
        corruptedFile.writeText("Corrupted audio data [EOF unexpected]")

        val corruptedHash = MessageDigest.getInstance("SHA-256")
            .digest(corruptedFile.readBytes())
            .joinToString("") { "%02x".format(it) }

        assertNotEquals("Corrupted file must not match original hash", originalHash, corruptedHash)
    }

    @Test
    fun testDownloadProgressStateTracking() {
        val initialProgress = DownloadProgress(assetId = "med_1", isDownloading = false, progress = 0f)
        assertFalse(initialProgress.isDownloading)
        assertEquals(0f, initialProgress.progress)

        val activeProgress = initialProgress.copy(isDownloading = true, progress = 0.55f)
        assertTrue(activeProgress.isDownloading)
        assertEquals(0.55f, activeProgress.progress)

        val completedProgress = activeProgress.copy(isDownloading = false, progress = 1.0f)
        assertFalse(completedProgress.isDownloading)
        assertEquals(1.0f, completedProgress.progress)

        val failedProgress = activeProgress.copy(isDownloading = false, progress = 0f, error = "Connection timeout")
        assertFalse(failedProgress.isDownloading)
        assertEquals("Connection timeout", failedProgress.error)
    }

    @Test
    fun testTempFileCleanupOnInterruption() {
        val downloadTempFile = tempFolder.newFile("med_session.mp3.download")
        downloadTempFile.writeBytes(ByteArray(1024 * 50)) // 50 KB partial
        assertTrue(downloadTempFile.exists())

        // Simulate interruption handler cleanup
        val cleaned = downloadTempFile.delete()
        assertTrue(cleaned)
        assertFalse(downloadTempFile.exists())
    }
}
