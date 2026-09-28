package com.soulquote.app.data.mapper

import com.soulquote.app.data.local.entity.user.MeditationHistoryEntity
import com.soulquote.app.data.local.entity.user.UserSettingEntity
import com.soulquote.app.domain.model.MeditationHistoryItem
import com.soulquote.app.domain.model.UserSettings

fun MeditationHistoryEntity.toDomain(): MeditationHistoryItem {
    return MeditationHistoryItem(
        id = id,
        meditationId = meditationId,
        completedAt = completedAt,
        durationListenedSeconds = durationListenedSeconds,
        completed = completed
    )
}

fun MeditationHistoryItem.toEntity(): MeditationHistoryEntity {
    return MeditationHistoryEntity(
        id = id,
        meditationId = meditationId,
        completedAt = completedAt,
        durationListenedSeconds = durationListenedSeconds,
        completed = completed
    )
}

fun List<UserSettingEntity>.toUserSettings(): UserSettings {
    val map = associate { it.key to it.value }
    return UserSettings(
        dailyQuoteEnabled = map["dailyQuoteEnabled"]?.toBooleanStrictOrNull() ?: true,
        dailyQuoteHour = map["dailyQuoteHour"]?.toIntOrNull() ?: 7,
        dailyQuoteMinute = map["dailyQuoteMinute"]?.toIntOrNull() ?: 0,
        meditationReminderEnabled = map["meditationReminderEnabled"]?.toBooleanStrictOrNull() ?: false,
        meditationReminderHour = map["meditationReminderHour"]?.toIntOrNull() ?: 20,
        meditationReminderMinute = map["meditationReminderMinute"]?.toIntOrNull() ?: 0,
        notificationSound = map["notificationSound"] ?: "default",
        vibrationEnabled = map["vibrationEnabled"]?.toBooleanStrictOrNull() ?: true,
        themeMode = map["themeMode"] ?: "system",
        ambientAudioVolume = map["ambientAudioVolume"]?.toFloatOrNull() ?: 0.5f,
        ambientAutoPlay = map["ambientAutoPlay"]?.toBooleanStrictOrNull() ?: false,
        language = map["language"] ?: "in"
    )
}

fun UserSettings.toEntityList(): List<UserSettingEntity> {
    return listOf(
        UserSettingEntity("dailyQuoteEnabled", dailyQuoteEnabled.toString()),
        UserSettingEntity("dailyQuoteHour", dailyQuoteHour.toString()),
        UserSettingEntity("dailyQuoteMinute", dailyQuoteMinute.toString()),
        UserSettingEntity("meditationReminderEnabled", meditationReminderEnabled.toString()),
        UserSettingEntity("meditationReminderHour", meditationReminderHour.toString()),
        UserSettingEntity("meditationReminderMinute", meditationReminderMinute.toString()),
        UserSettingEntity("notificationSound", notificationSound),
        UserSettingEntity("vibrationEnabled", vibrationEnabled.toString()),
        UserSettingEntity("themeMode", themeMode),
        UserSettingEntity("ambientAudioVolume", ambientAudioVolume.toString()),
        UserSettingEntity("ambientAutoPlay", ambientAutoPlay.toString()),
        UserSettingEntity("language", language)
    )
}
