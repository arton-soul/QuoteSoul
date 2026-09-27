package com.soulquote.app.domain.repository

import com.soulquote.app.domain.model.MeditationHistoryItem
import com.soulquote.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserSettings(): Flow<UserSettings>
    suspend fun updateUserSettings(settings: UserSettings)
    suspend fun saveSetting(key: String, value: String)
    suspend fun getSetting(key: String, defaultValue: String): String
    fun getMeditationHistory(): Flow<List<MeditationHistoryItem>>
    suspend fun recordMeditationHistory(item: MeditationHistoryItem)
    suspend fun getDownloadedAudioIds(): Set<String>
    suspend fun registerDownloadedAudio(assetId: String, assetType: String, path: String, size: Long, checksum: String)
    suspend fun removeDownloadedAudio(assetId: String)
}
