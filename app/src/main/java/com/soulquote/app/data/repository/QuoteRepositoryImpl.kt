package com.soulquote.app.data.repository

import com.soulquote.app.data.local.dao.FavoriteDao
import com.soulquote.app.data.local.dao.QuoteDao
import com.soulquote.app.data.local.entity.user.FavoriteEntity
import com.soulquote.app.data.mapper.toDomain
import com.soulquote.app.domain.model.Quote
import com.soulquote.app.domain.model.QuoteCategory
import com.soulquote.app.domain.repository.QuoteRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.abs

class QuoteRepositoryImpl(
    private val quoteDao: QuoteDao,
    private val favoriteDao: FavoriteDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : QuoteRepository {

    override fun getAllQuotes(): Flow<List<Quote>> {
        return combine(
            quoteDao.getAllQuotes(),
            favoriteDao.getFavoriteQuoteIdsFlow()
        ) { quoteEntities, favoriteIds ->
            val favSet = favoriteIds.toSet()
            quoteEntities.map { entity ->
                entity.toDomain(isFavorite = favSet.contains(entity.id))
            }
        }.flowOn(ioDispatcher)
    }

    override fun getQuotesByCategory(categoryId: String): Flow<List<Quote>> {
        return combine(
            quoteDao.getQuotesByCategory(categoryId),
            favoriteDao.getFavoriteQuoteIdsFlow()
        ) { quoteEntities, favoriteIds ->
            val favSet = favoriteIds.toSet()
            quoteEntities.map { entity ->
                entity.toDomain(isFavorite = favSet.contains(entity.id))
            }
        }.flowOn(ioDispatcher)
    }

    override fun getFavoriteQuotes(): Flow<List<Quote>> {
        return combine(
            quoteDao.getAllQuotes(),
            favoriteDao.getFavoriteQuoteIdsFlow()
        ) { quoteEntities, favoriteIds ->
            val favSet = favoriteIds.toSet()
            quoteEntities.filter { favSet.contains(it.id) }
                .map { it.toDomain(isFavorite = true) }
        }.flowOn(ioDispatcher)
    }

    override suspend fun getQuoteById(id: String): Quote? = withContext(ioDispatcher) {
        val entity = quoteDao.getQuoteById(id) ?: return@withContext null
        val isFav = favoriteDao.isFavorite(id)
        entity.toDomain(isFavorite = isFav)
    }

    override suspend fun getRandomQuote(): Quote? = withContext(ioDispatcher) {
        val entity = quoteDao.getRandomQuote() ?: return@withContext null
        val isFav = favoriteDao.isFavorite(entity.id)
        entity.toDomain(isFavorite = isFav)
    }

    override suspend fun getDailyQuote(dateEpochDay: Long): Quote? = withContext(ioDispatcher) {
        val activeQuotes = quoteDao.getActiveQuotesList()
        if (activeQuotes.isEmpty()) return@withContext null

        val index = (abs(dateEpochDay) % activeQuotes.size).toInt()
        val selectedEntity = activeQuotes[index]
        val isFav = favoriteDao.isFavorite(selectedEntity.id)
        selectedEntity.toDomain(isFavorite = isFav)
    }

    override fun getAllCategories(): Flow<List<QuoteCategory>> {
        return quoteDao.getAllCategories().map { list ->
            list.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override suspend fun toggleFavorite(quoteId: String, isFavorite: Boolean) = withContext(ioDispatcher) {
        if (isFavorite) {
            favoriteDao.addFavorite(
                FavoriteEntity(
                    id = UUID.randomUUID().toString(),
                    quoteId = quoteId
                )
            )
        } else {
            favoriteDao.removeFavorite(quoteId)
        }
    }

    override suspend fun isFavorite(quoteId: String): Boolean = withContext(ioDispatcher) {
        favoriteDao.isFavorite(quoteId)
    }
}
