package com.soulquote.app.domain

import com.soulquote.app.domain.model.Quote
import com.soulquote.app.domain.model.QuoteCategory
import com.soulquote.app.domain.repository.QuoteRepository
import com.soulquote.app.domain.usecase.GetDailyQuoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class GetDailyQuoteUseCaseTest {

    @Test
    fun getDailyQuote_returnsRepositoryDailyQuote() = runBlocking {
        val expectedQuote = Quote(
            id = "daily_1",
            text = "Knowledge speaks, but wisdom listens.",
            author = "Jimi Hendrix",
            categoryId = "inner_peace"
        )

        val fakeRepo = object : FakeQuoteRepository() {
            override suspend fun getDailyQuote(dateEpochDay: Long): Quote = expectedQuote
        }

        val useCase = GetDailyQuoteUseCase(fakeRepo)
        val result = useCase(epochDay = 12345L)

        assertNotNull(result)
        assertEquals("daily_1", result?.id)
        assertEquals("Knowledge speaks, but wisdom listens.", result?.text)
    }

    @Test
    fun getDailyQuote_fallsBackToRandomQuoteWhenDailyNull() = runBlocking {
        val fallbackQuote = Quote(
            id = "random_1",
            text = "Be the change you wish to see.",
            author = "Mahatma Gandhi",
            categoryId = "mindfulness"
        )

        val fakeRepo = object : FakeQuoteRepository() {
            override suspend fun getDailyQuote(dateEpochDay: Long): Quote? = null
            override suspend fun getRandomQuote(): Quote = fallbackQuote
        }

        val useCase = GetDailyQuoteUseCase(fakeRepo)
        val result = useCase(epochDay = 99999L)

        assertNotNull(result)
        assertEquals("random_1", result?.id)
    }

    private open class FakeQuoteRepository : QuoteRepository {
        override fun getAllQuotes(): Flow<List<Quote>> = emptyFlow()
        override fun getQuotesByCategory(categoryId: String): Flow<List<Quote>> = emptyFlow()
        override fun getFavoriteQuotes(): Flow<List<Quote>> = emptyFlow()
        override suspend fun getQuoteById(id: String): Quote? = null
        override suspend fun getRandomQuote(): Quote? = null
        override suspend fun getDailyQuote(dateEpochDay: Long): Quote? = null
        override fun getAllCategories(): Flow<List<QuoteCategory>> = emptyFlow()
        override suspend fun toggleFavorite(quoteId: String, isFavorite: Boolean) {}
        override suspend fun isFavorite(quoteId: String): Boolean = false
    }
}
