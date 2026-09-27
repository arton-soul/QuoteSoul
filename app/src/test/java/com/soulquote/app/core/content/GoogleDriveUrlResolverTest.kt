package com.soulquote.app.core.content

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GoogleDriveUrlResolverTest {

    @Test
    fun testExtractFileIdFromShareUrl() {
        val shareUrl = "https://drive.google.com/file/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/view?usp=sharing"
        val fileId = GoogleDriveUrlResolver.extractFileId(shareUrl)
        assertEquals("1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms", fileId)
    }

    @Test
    fun testExtractFileIdFromViewUrl() {
        val viewUrl = "https://drive.google.com/file/d/1sB8_abcdef12345-xyz/view"
        val fileId = GoogleDriveUrlResolver.extractFileId(viewUrl)
        assertEquals("1sB8_abcdef12345-xyz", fileId)
    }

    @Test
    fun testExtractFileIdFromOpenIdUrl() {
        val openUrl = "https://drive.google.com/open?id=1AbCdEfGhIjKlMnOpQrStUvWxYz_12345"
        val fileId = GoogleDriveUrlResolver.extractFileId(openUrl)
        assertEquals("1AbCdEfGhIjKlMnOpQrStUvWxYz_12345", fileId)
    }

    @Test
    fun testExtractFileIdFromRawId() {
        val rawId = "1AbCdEfGhIjKlMnOpQrStUvWxYz_12345"
        val fileId = GoogleDriveUrlResolver.extractFileId(rawId)
        assertEquals("1AbCdEfGhIjKlMnOpQrStUvWxYz_12345", fileId)
    }

    @Test
    fun testNonDriveUrlsReturnNull() {
        assertNull(GoogleDriveUrlResolver.extractFileId("https://example.com/files/update.json"))
        assertNull(GoogleDriveUrlResolver.extractFileId("asset://seed/content_update_v4.json"))
        assertNull(GoogleDriveUrlResolver.extractFileId(""))
    }

    @Test
    fun testResolveDirectDownloadUrl() {
        val shareUrl = "https://drive.google.com/file/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/view?usp=sharing"
        val resolved = GoogleDriveUrlResolver.resolveDirectDownloadUrl(shareUrl)
        assertEquals(
            "https://drive.google.com/uc?export=download&id=1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms",
            resolved
        )

        // Non-drive URL should remain unchanged
        val normalUrl = "https://cdn.example.com/update_v4.json"
        assertEquals(normalUrl, GoogleDriveUrlResolver.resolveDirectDownloadUrl(normalUrl))

        val assetUrl = "asset://seed/content_update_v4.json"
        assertEquals(assetUrl, GoogleDriveUrlResolver.resolveDirectDownloadUrl(assetUrl))
    }

    @Test
    fun testDetectQuotaExceeded() {
        val quotaHtml = "<html><body><h1>Google Drive - Download quota exceeded for this file</h1></body></html>"
        assertTrue(GoogleDriveUrlResolver.isQuotaExceededResponse(quotaHtml))

        val indonesianQuotaHtml = "Maaf, berkas ini telah melampaui kuota unduhan saat ini."
        assertTrue(GoogleDriveUrlResolver.isQuotaExceededResponse(indonesianQuotaHtml))

        val normalJson = "{\"manifestVersion\": 1, \"contentVersion\": 4}"
        assertFalse(GoogleDriveUrlResolver.isQuotaExceededResponse(normalJson))
    }

    @Test
    fun testExtractConfirmToken() {
        val warningHtml = "<a id=\"uc-download-link\" href=\"/uc?export=download&id=123&confirm=t_8F\">Download anyway</a>"
        assertTrue(GoogleDriveUrlResolver.isVirusScanWarning(warningHtml))
        val token = GoogleDriveUrlResolver.extractConfirmToken(warningHtml)
        assertNotNull(token)
        assertEquals("t_8F", token)
    }
}
