package com.soulquote.app.data.repository

import com.soulquote.app.data.local.dao.DownloadedAudioDao
import com.soulquote.app.data.local.dao.MeditationDao
import com.soulquote.app.data.mapper.toDomain
import com.soulquote.app.domain.model.Meditation
import com.soulquote.app.domain.model.MeditationCategory
import com.soulquote.app.domain.repository.MeditationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MeditationRepositoryImpl(
    private val meditationDao: MeditationDao,
    private val downloadedAudioDao: DownloadedAudioDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MeditationRepository {

    override fun getAllMeditations(): Flow<List<Meditation>> {
        return meditationDao.getAllMeditations().map { list ->
            val downloadedIds = downloadedAudioDao.getAllDownloadedAssetIds().toSet()
            list.map { it.toDomain(isDownloaded = downloadedIds.contains(it.id)) }
        }.flowOn(ioDispatcher)
    }

    override fun getMeditationsByCategory(categoryId: String): Flow<List<Meditation>> {
        return meditationDao.getMeditationsByCategory(categoryId).map { list ->
            val downloadedIds = downloadedAudioDao.getAllDownloadedAssetIds().toSet()
            list.map { it.toDomain(isDownloaded = downloadedIds.contains(it.id)) }
        }.flowOn(ioDispatcher)
    }

    override suspend fun getMeditationById(id: String): Meditation? = withContext(ioDispatcher) {
        val entity = meditationDao.getMeditationById(id) ?: return@withContext null
        val isDownloaded = downloadedAudioDao.getDownloadedAudioByAssetId(id) != null
        entity.toDomain(isDownloaded = isDownloaded)
    }

    override fun getAllCategories(): Flow<List<MeditationCategory>> {
        return meditationDao.getAllCategories().map { list ->
            list.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }
}
