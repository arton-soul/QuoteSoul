package com.soulquote.app.domain.model

data class UserSettings(
    val dailyQuoteEnabled: Boolean = true,
    val dailyQuoteHour: Int = 7,
    val dailyQuoteMinute: Int = 0,
    val meditationReminderEnabled: Boolean = false,
    val meditationReminderHour: Int = 20,
    val meditationReminderMinute: Int = 0,
    val notificationSound: String = "default",
    val vibrationEnabled: Boolean = true,
    val themeMode: String = "system",
    val ambientAudioVolume: Float = 0.5f,
    val ambientAutoPlay: Boolean = false
)

data class MeditationHistoryItem(
    val id: String,
    val meditationId: String,
    val completedAt: Long,
    val durationListenedSeconds: Int,
    val completed: Boolean
)
