package com.soulquote.app.domain.usecase

import com.soulquote.app.domain.model.Quote
import com.soulquote.app.domain.model.QuoteCategory
import com.soulquote.app.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.Flow

class ToggleFavoriteUseCase(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(quoteId: String, isFavorite: Boolean) {
        quoteRepository.toggleFavorite(quoteId, isFavorite)
    }
}

class GetRandomQuoteUseCase(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(): Quote? {
        return quoteRepository.getRandomQuote()
    }
}

class GetCategoriesUseCase(
    private val quoteRepository: QuoteRepository
) {
    operator fun invoke(): Flow<List<QuoteCategory>> {
        return quoteRepository.getAllCategories()
    }
}
