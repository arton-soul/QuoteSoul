package com.soulquote.app.core.backup

import com.soulquote.app.data.local.backup.FavoriteBackupDto
import com.soulquote.app.data.local.backup.MeditationHistoryBackupDto
import com.soulquote.app.data.local.backup.UserDataBackup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserDataBackupTest {

    @Test
    fun testSerializationAndDeserialization() {
        val backup = UserDataBackup(
            backupVersion = 1,
            appVersion = 1,
            appName = "SoulQuote",
            exportedAt = 1759000000000L,
            favorites = listOf(
                FavoriteBackupDto(id = "fav_1", quoteId = "quote_v4_001", favoritedAt = 1759000100000L),
                FavoriteBackupDto(id = "fav_2", quoteId = "quote_002", favoritedAt = 1759000200000L)
            ),
            meditationHistory = listOf(
                MeditationHistoryBackupDto(
                    id = "hist_1",
                    meditationId = "med_breathe_01",
                    completedAt = 1759000300000L,
                    durationListenedSeconds = 300,
                    completed = true
                )
            ),
            settings = mapOf(
                "daily_quote_enabled" to "true",
                "vibration_enabled" to "false"
            ),
            checksumSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        )

        val jsonStr = backup.toJsonString()
        assertNotNull(jsonStr)
        assertTrue(jsonStr.contains("SoulQuote"))
        assertTrue(jsonStr.contains("quote_v4_001"))
        assertTrue(jsonStr.contains("med_breathe_01"))

        val restored = UserDataBackup.fromJsonString(jsonStr)
        assertEquals(backup.backupVersion, restored.backupVersion)
        assertEquals(backup.appName, restored.appName)
        assertEquals(backup.exportedAt, restored.exportedAt)
        assertEquals(backup.checksumSha256, restored.checksumSha256)

        assertEquals(2, restored.favorites.size)
        assertEquals("quote_v4_001", restored.favorites[0].quoteId)
        assertEquals("fav_1", restored.favorites[0].id)

        assertEquals(1, restored.meditationHistory.size)
        assertEquals("med_breathe_01", restored.meditationHistory[0].meditationId)
        assertEquals(300, restored.meditationHistory[0].durationListenedSeconds)
        assertTrue(restored.meditationHistory[0].completed)

        assertEquals(2, restored.settings.size)
        assertEquals("true", restored.settings["daily_quote_enabled"])
        assertEquals("false", restored.settings["vibration_enabled"])
    }

    @Test
    fun testEmptyBackupSerialization() {
        val emptyBackup = UserDataBackup(
            backupVersion = 1,
            appVersion = 1,
            appName = "SoulQuote",
            exportedAt = 1759000000000L,
            favorites = emptyList(),
            meditationHistory = emptyList(),
            settings = emptyMap(),
            checksumSha256 = "empty_checksum"
        )

        val jsonStr = emptyBackup.toJsonString()
        val restored = UserDataBackup.fromJsonString(jsonStr)

        assertEquals("SoulQuote", restored.appName)
        assertEquals(0, restored.favorites.size)
        assertEquals(0, restored.meditationHistory.size)
        assertEquals(0, restored.settings.size)
    }

    @Test
    fun testUserDataStatsAndRestoreResult() {
        val stats = UserDataStats(favoritesCount = 5, historyCount = 2, settingsCount = 4)
        assertEquals(5, stats.favoritesCount)
        assertEquals(2, stats.historyCount)
        assertEquals(4, stats.settingsCount)

        val result = RestoreResult(favoritesRestored = 5, historyRestored = 2, settingsRestored = 4)
        assertEquals(5, result.favoritesRestored)
        assertEquals(2, result.historyRestored)
        assertEquals(4, result.settingsRestored)
    }
}
