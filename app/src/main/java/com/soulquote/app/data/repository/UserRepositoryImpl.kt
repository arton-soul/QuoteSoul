package com.soulquote.app.data.repository

import com.soulquote.app.data.local.dao.DownloadedAudioDao
import com.soulquote.app.data.local.dao.MeditationHistoryDao
import com.soulquote.app.data.local.dao.UserSettingDao
import com.soulquote.app.data.local.entity.user.DownloadedAudioEntity
import com.soulquote.app.data.local.entity.user.UserSettingEntity
import com.soulquote.app.data.mapper.toDomain
import com.soulquote.app.data.mapper.toEntity
import com.soulquote.app.data.mapper.toEntityList
import com.soulquote.app.data.mapper.toUserSettings
import com.soulquote.app.domain.model.MeditationHistoryItem
import com.soulquote.app.domain.model.UserSettings
import com.soulquote.app.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class UserRepositoryImpl(
    private val userSettingDao: UserSettingDao,
    private val meditationHistoryDao: MeditationHistoryDao,
    private val downloadedAudioDao: DownloadedAudioDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {

    override fun getUserSettings(): Flow<UserSettings> {
        return userSettingDao.getAllSettings().map { list ->
            list.toUserSettings()
        }.flowOn(ioDispatcher)
    }

    override suspend fun updateUserSettings(settings: UserSettings) = withContext(ioDispatcher) {
        userSettingDao.saveSettings(settings.toEntityList())
    }

    override suspend fun saveSetting(key: String, value: String) = withContext(ioDispatcher) {
        userSettingDao.saveSetting(UserSettingEntity(key = key, value = value))
    }

    override suspend fun getSetting(key: String, defaultValue: String): String = withContext(ioDispatcher) {
        userSettingDao.getSettingValue(key) ?: defaultValue
    }

    override fun getMeditationHistory(): Flow<List<MeditationHistoryItem>> {
        return meditationHistoryDao.getHistory().map { list ->
            list.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override suspend fun recordMeditationHistory(item: MeditationHistoryItem) = withContext(ioDispatcher) {
        meditationHistoryDao.recordHistory(item.toEntity())
    }

    override suspend fun getDownloadedAudioIds(): Set<String> = withContext(ioDispatcher) {
        downloadedAudioDao.getAllDownloadedAssetIds().toSet()
    }

    override suspend fun registerDownloadedAudio(
        assetId: String,
        assetType: String,
        path: String,
        size: Long,
        checksum: String
    ) = withContext(ioDispatcher) {
        downloadedAudioDao.insertDownloadedAudio(
            DownloadedAudioEntity(
                id = UUID.randomUUID().toString(),
                assetId = assetId,
                assetType = assetType,
                localPath = path,
                fileSize = size,
                checksum = checksum
            )
        )
    }

    override suspend fun removeDownloadedAudio(assetId: String) = withContext(ioDispatcher) {
        downloadedAudioDao.deleteByAssetId(assetId)
    }
}
