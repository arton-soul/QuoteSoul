package com.soulquote.app.data

import com.soulquote.app.data.local.entity.user.UserSettingEntity
import com.soulquote.app.data.mapper.toEntityList
import com.soulquote.app.data.mapper.toUserSettings
import com.soulquote.app.domain.model.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRepositoryTest {

    @Test
    fun userSettings_roundTripMapping_preservesAllValues() {
        val original = UserSettings(
            dailyQuoteEnabled = true,
            dailyQuoteHour = 6,
            dailyQuoteMinute = 30,
            meditationReminderEnabled = true,
            meditationReminderHour = 21,
            meditationReminderMinute = 15,
            notificationSound = "mindful_bell",
            vibrationEnabled = false,
            themeMode = "dark",
            ambientAudioVolume = 0.8f,
            ambientAutoPlay = true
        )

        val entityList = original.toEntityList()
        val mappedBack = entityList.toUserSettings()

        assertEquals(original.dailyQuoteEnabled, mappedBack.dailyQuoteEnabled)
        assertEquals(original.dailyQuoteHour, mappedBack.dailyQuoteHour)
        assertEquals(original.dailyQuoteMinute, mappedBack.dailyQuoteMinute)
        assertEquals(original.meditationReminderEnabled, mappedBack.meditationReminderEnabled)
        assertEquals(original.meditationReminderHour, mappedBack.meditationReminderHour)
        assertEquals(original.meditationReminderMinute, mappedBack.meditationReminderMinute)
        assertEquals(original.notificationSound, mappedBack.notificationSound)
        assertEquals(original.vibrationEnabled, mappedBack.vibrationEnabled)
        assertEquals(original.themeMode, mappedBack.themeMode)
        assertEquals(original.ambientAudioVolume, mappedBack.ambientAudioVolume, 0.001f)
        assertEquals(original.ambientAutoPlay, mappedBack.ambientAutoPlay)
    }

    @Test
    fun userSettings_defaultsWhenEmpty() {
        val emptyEntities = emptyList<UserSettingEntity>()
        val defaultSettings = emptyEntities.toUserSettings()

        assertTrue(defaultSettings.dailyQuoteEnabled)
        assertEquals(7, defaultSettings.dailyQuoteHour)
        assertEquals(0, defaultSettings.dailyQuoteMinute)
        assertEquals("system", defaultSettings.themeMode)
    }
}
