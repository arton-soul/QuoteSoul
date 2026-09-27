package com.soulquote.app.domain.usecase

import com.soulquote.app.domain.model.Quote
import com.soulquote.app.domain.repository.QuoteRepository
import java.time.LocalDate

class GetDailyQuoteUseCase(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(epochDay: Long = LocalDate.now().toEpochDay()): Quote? {
        return quoteRepository.getDailyQuote(epochDay) ?: quoteRepository.getRandomQuote()
    }
}
