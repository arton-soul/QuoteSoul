package com.soulquote.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.soulquote.app.data.local.entity.content.QuoteCategoryEntity
import com.soulquote.app.data.local.entity.content.QuoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes WHERE active = 1 ORDER BY priority DESC, createdAt DESC")
    fun getAllQuotes(): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE active = 1 AND category = :categoryId ORDER BY priority DESC, createdAt DESC")
    fun getQuotesByCategory(categoryId: String): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE id = :id")
    suspend fun getQuoteById(id: String): QuoteEntity?

    @Query("SELECT * FROM quotes WHERE active = 1 ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuote(): QuoteEntity?

    @Query("SELECT * FROM quotes WHERE active = 1 ORDER BY id ASC")
    suspend fun getActiveQuotesList(): List<QuoteEntity>

    @Query("SELECT COUNT(*) FROM quotes WHERE active = 1")
    suspend fun getActiveQuoteCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<QuoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity)

    @Query("SELECT * FROM quote_categories ORDER BY sortOrder ASC, name ASC")
    fun getAllCategories(): Flow<List<QuoteCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<QuoteCategoryEntity>)

    @Query("DELETE FROM quotes")
    suspend fun clearQuotes()

    @Query("DELETE FROM quote_categories")
    suspend fun clearCategories()

    @Query("DELETE FROM quotes WHERE author != 'Bunda Arsaningsih'")
    suspend fun deleteNonBundaQuotes()

    @Transaction
    suspend fun replaceAllQuotes(quotes: List<QuoteEntity>, categories: List<QuoteCategoryEntity>) {
        clearQuotes()
        clearCategories()
        insertCategories(categories)
        insertQuotes(quotes)
    }
}
