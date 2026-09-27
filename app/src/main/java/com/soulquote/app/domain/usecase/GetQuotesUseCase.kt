package com.soulquote.app.domain.usecase

import com.soulquote.app.domain.model.Quote
import com.soulquote.app.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.Flow

class GetQuotesUseCase(
    private val quoteRepository: QuoteRepository
) {
    operator fun invoke(categoryId: String? = null): Flow<List<Quote>> {
        return if (categoryId.isNullOrBlank() || categoryId == "all") {
            quoteRepository.getAllQuotes()
        } else {
            quoteRepository.getQuotesByCategory(categoryId)
        }
    }
}

class GetFavoriteQuotesUseCase(
    private val quoteRepository: QuoteRepository
) {
    operator fun invoke(): Flow<List<Quote>> {
        return quoteRepository.getFavoriteQuotes()
    }
}
