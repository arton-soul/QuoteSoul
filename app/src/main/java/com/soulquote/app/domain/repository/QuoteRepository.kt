package com.soulquote.app.domain.repository

import com.soulquote.app.domain.model.Quote
import com.soulquote.app.domain.model.QuoteCategory
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {
    fun getAllQuotes(): Flow<List<Quote>>
    fun getQuotesByCategory(categoryId: String): Flow<List<Quote>>
    fun getFavoriteQuotes(): Flow<List<Quote>>
    suspend fun getQuoteById(id: String): Quote?
    suspend fun getRandomQuote(): Quote?
    suspend fun getDailyQuote(dateEpochDay: Long): Quote?
    fun getAllCategories(): Flow<List<QuoteCategory>>
    suspend fun toggleFavorite(quoteId: String, isFavorite: Boolean)
    suspend fun isFavorite(quoteId: String): Boolean
}
