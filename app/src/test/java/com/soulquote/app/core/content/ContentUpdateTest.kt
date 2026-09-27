package com.soulquote.app.core.content

import com.soulquote.app.data.remote.model.ContentManifest
import com.soulquote.app.data.remote.model.ContentPackage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

class ContentUpdateTest {

    private val sampleManifestJson = """
        {
          "manifestVersion": 1,
          "contentVersion": 4,
          "minAppVersion": 1,
          "releasedAt": "2026-09-27T15:00:00Z",
          "packageUrl": "asset://seed/content_update_v4.json",
          "packageSizeBytes": 3483,
          "packageChecksumSha256": "4DD56BA53A468631BD87EEA9612F4041EDC326F8144B7749F9A347E301E96AA3",
          "changelog": "Penambahan 5 kutipan inspirasi keheningan batin oleh Bunda Arsaningsih.",
          "quotesCount": 5,
          "meditationsCount": 0
        }
    """.trimIndent()

    private val samplePackageJson = """
        {
          "contentVersion": 4,
          "categories": [
            {
              "id": "keheningan_hati",
              "name": "Keheningan Hati",
              "slug": "keheningan-hati",
              "description": "Ketenangan dan kedamaian batin.",
              "iconName": "spa",
              "sortOrder": 6
            }
          ],
          "quotes": [
            {
              "id": "quote_v4_001",
              "text": "Dalam keheningan terdalam, jiwa menemukan kembali rumahnya yang sejati.",
              "author": "Bunda Arsaningsih",
              "category": "keheningan_hati",
              "source": "Buku Keheningan Jiwa",
              "tags": "keheningan, batin, jiwa",
              "priority": 100
            }
          ],
          "templates": [],
          "meditationCategories": [],
          "meditations": []
        }
    """.trimIndent()

    @Test
    fun testManifestParsing() {
        val manifest = ContentManifest.fromJson(sampleManifestJson)
        assertEquals(1, manifest.manifestVersion)
        assertEquals(4, manifest.contentVersion)
        assertEquals(1, manifest.minAppVersion)
        assertEquals("asset://seed/content_update_v4.json", manifest.packageUrl)
        assertEquals(3483L, manifest.packageSizeBytes)
        // Checksum should be normalized to lowercase
        assertEquals("4dd56ba53a468631bd87eea9612f4041edc326f8144b7749f9a347e301e96aa3", manifest.packageChecksumSha256)
        assertEquals(5, manifest.quotesCount)
        assertEquals(0, manifest.meditationsCount)
        assertTrue(manifest.changelog.contains("Bunda Arsaningsih"))
    }

    @Test
    fun testPackageParsing() {
        val pkg = ContentPackage.fromJson(samplePackageJson)
        assertEquals(4, pkg.contentVersion)
        assertEquals(1, pkg.categories.size)
        assertEquals("keheningan_hati", pkg.categories[0].id)
        assertEquals("Keheningan Hati", pkg.categories[0].name)

        assertEquals(1, pkg.quotes.size)
        val quote = pkg.quotes[0]
        assertEquals("quote_v4_001", quote.id)
        assertEquals("Dalam keheningan terdalam, jiwa menemukan kembali rumahnya yang sejati.", quote.text)
        assertEquals("Bunda Arsaningsih", quote.author)
        assertEquals("keheningan_hati", quote.category)
        assertEquals("Buku Keheningan Jiwa", quote.source)
        assertTrue(quote.tags?.contains("keheningan") == true)
        assertEquals(100, quote.priority)
    }

    @Test
    fun testSha256Calculation() {
        val tempFile = File.createTempFile("test_sha", ".txt")
        try {
            tempFile.writeText("SoulQuote Test Data")
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest("SoulQuote Test Data".toByteArray()).joinToString("") { "%02x".format(it) }

            // Compute file sha
            val fileDigest = MessageDigest.getInstance("SHA-256")
            tempFile.inputStream().use { input ->
                val buf = ByteArray(1024)
                var read: Int
                while (input.read(buf).also { read = it } != -1) {
                    fileDigest.update(buf, 0, read)
                }
            }
            val fileHash = fileDigest.digest().joinToString("") { "%02x".format(it) }

            assertEquals(hash, fileHash)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun testUpdateCheckLogic() {
        val manifest = ContentManifest.fromJson(sampleManifestJson)
        val currentVersion = 3

        val isNewer = manifest.contentVersion > currentVersion
        assertTrue(isNewer)

        val sameVersion = 4
        val isNotNewer = manifest.contentVersion > sameVersion
        assertTrue(!isNotNewer)
    }
}
