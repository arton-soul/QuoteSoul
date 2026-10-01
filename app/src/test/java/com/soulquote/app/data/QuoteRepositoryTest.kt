package com.soulquote.app.data

import com.soulquote.app.data.local.dao.FavoriteDao
import com.soulquote.app.data.local.dao.QuoteDao
import com.soulquote.app.data.local.entity.content.QuoteCategoryEntity
import com.soulquote.app.data.local.entity.content.QuoteEntity
import com.soulquote.app.data.local.entity.user.FavoriteEntity
import com.soulquote.app.data.repository.QuoteRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuoteRepositoryTest {

    private lateinit var fakeQuoteDao: FakeQuoteDao
    private lateinit var fakeFavoriteDao: FakeFavoriteDao
    private lateinit var repository: QuoteRepositoryImpl

    @Before
    fun setup() {
        fakeQuoteDao = FakeQuoteDao()
        fakeFavoriteDao = FakeFavoriteDao()
        repository = QuoteRepositoryImpl(
            quoteDao = fakeQuoteDao,
            favoriteDao = fakeFavoriteDao
        )
    }

    @Test
    fun getDailyQuote_returnsDeterministicQuoteForSameDate() = runBlocking {
        val quotes = listOf(
            QuoteEntity(id = "1", text = "Quote 1", author = "Author 1", category = "stoic"),
            QuoteEntity(id = "2", text = "Quote 2", author = "Author 2", category = "stoic"),
            QuoteEntity(id = "3", text = "Quote 3", author = "Author 3", category = "mindful")
        )
        fakeQuoteDao.activeQuotes.addAll(quotes)

        val dateDay1 = 20000L
        val dailyQuoteDay1A = repository.getDailyQuote(dateDay1)
        val dailyQuoteDay1B = repository.getDailyQuote(dateDay1)

        assertNotNull(dailyQuoteDay1A)
        assertNotNull(dailyQuoteDay1B)
        assertEquals(dailyQuoteDay1A?.id, dailyQuoteDay1B?.id)

        // Day 2 should produce a predictably rotated quote
        val dateDay2 = 20001L
        val dailyQuoteDay2 = repository.getDailyQuote(dateDay2)
        assertNotNull(dailyQuoteDay2)
        assertEquals("3", dailyQuoteDay1A?.id)
        assertEquals("1", dailyQuoteDay2?.id)
    }

    @Test
    fun getAllQuotes_combinesWithFavoritesCorrectly() = runBlocking {
        val quote1 = QuoteEntity(id = "q1", text = "Text 1", author = "A1", category = "c1")
        val quote2 = QuoteEntity(id = "q2", text = "Text 2", author = "A2", category = "c1")
        fakeQuoteDao.quotesFlow.value = listOf(quote1, quote2)
        fakeFavoriteDao.favoriteIdsFlow.value = listOf("q1")

        val result = repository.getAllQuotes().first()

        assertEquals(2, result.size)
        val domainQ1 = result.find { it.id == "q1" }
        val domainQ2 = result.find { it.id == "q2" }

        assertTrue(domainQ1?.isFavorite == true)
        assertFalse(domainQ2?.isFavorite == true)
    }

    @Test
    fun toggleFavorite_updatesFavoriteState() = runBlocking {
        repository.toggleFavorite("q_new", true)
        assertTrue(repository.isFavorite("q_new"))

        repository.toggleFavorite("q_new", false)
        assertFalse(repository.isFavorite("q_new"))
    }

    @Test
    fun testDeleteNonBundaQuotes() = runBlocking {
        val quotes = listOf(
            QuoteEntity("1", "Text 1", "Bunda Arsaningsih", "mindfulness"),
            QuoteEntity("2", "Text 2", "Marcus Aurelius", "stoicism"),
            QuoteEntity("3", "Text 3", "Bunda Arsaningsih", "inner_peace")
        )
        fakeQuoteDao.insertQuotes(quotes)
        assertEquals(3, fakeQuoteDao.getActiveQuoteCount())

        fakeQuoteDao.deleteNonBundaQuotes()
        assertEquals(2, fakeQuoteDao.getActiveQuoteCount())
        assertTrue(fakeQuoteDao.getActiveQuotesList().all { it.author == "Bunda Arsaningsih" })
    }

    private class FakeQuoteDao : QuoteDao {
        val activeQuotes = mutableListOf<QuoteEntity>()
        val quotesFlow = MutableStateFlow<List<QuoteEntity>>(emptyList())
        val categoriesFlow = MutableStateFlow<List<QuoteCategoryEntity>>(emptyList())

        override fun getAllQuotes(): Flow<List<QuoteEntity>> = quotesFlow
        override fun getQuotesByCategory(categoryId: String): Flow<List<QuoteEntity>> = quotesFlow
        override suspend fun getQuoteById(id: String): QuoteEntity? = activeQuotes.find { it.id == id }
        override suspend fun getRandomQuote(): QuoteEntity? = activeQuotes.firstOrNull()
        override suspend fun getActiveQuotesList(): List<QuoteEntity> = activeQuotes
        override suspend fun getActiveQuoteCount(): Int = activeQuotes.size
        override suspend fun insertQuotes(quotes: List<QuoteEntity>) { activeQuotes.addAll(quotes) }
        override suspend fun insertQuote(quote: QuoteEntity) { activeQuotes.add(quote) }
        override fun getAllCategories(): Flow<List<QuoteCategoryEntity>> = categoriesFlow
        override suspend fun insertCategories(categories: List<QuoteCategoryEntity>) {}
        override suspend fun clearQuotes() { activeQuotes.clear() }
        override suspend fun clearCategories() {}
        override suspend fun deleteNonBundaQuotes() {
            activeQuotes.removeAll { it.author != "Bunda Arsaningsih" }
        }
    }

    private class FakeFavoriteDao : FavoriteDao {
        val favoriteIds = mutableSetOf<String>()
        val favoriteIdsFlow = MutableStateFlow<List<String>>(emptyList())

        override fun getAllFavorites(): Flow<List<FavoriteEntity>> = MutableStateFlow(emptyList())
        override fun getFavoriteQuoteIdsFlow(): Flow<List<String>> = favoriteIdsFlow
        override suspend fun getFavoriteQuoteIds(): List<String> = favoriteIds.toList()
        override suspend fun isFavorite(quoteId: String): Boolean = favoriteIds.contains(quoteId)
        override fun isFavoriteFlow(quoteId: String): Flow<Boolean> = MutableStateFlow(favoriteIds.contains(quoteId))
        override suspend fun addFavorite(favorite: FavoriteEntity) {
            favoriteIds.add(favorite.quoteId)
            favoriteIdsFlow.value = favoriteIds.toList()
        }
        override suspend fun insertFavorites(favorites: List<FavoriteEntity>) {
            favorites.forEach { favoriteIds.add(it.quoteId) }
            favoriteIdsFlow.value = favoriteIds.toList()
        }
        override suspend fun getAllFavoritesList(): List<FavoriteEntity> {
            return favoriteIds.map { FavoriteEntity("fav_$it", it) }
        }
        override suspend fun removeFavorite(quoteId: String) {
            favoriteIds.remove(quoteId)
            favoriteIdsFlow.value = favoriteIds.toList()
        }
        override suspend fun clearFavorites() {
            favoriteIds.clear()
            favoriteIdsFlow.value = emptyList()
        }
    }
}
