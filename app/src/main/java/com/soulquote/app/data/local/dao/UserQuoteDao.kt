package com.soulquote.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.soulquote.app.data.local.entity.user.UserQuoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserQuoteDao {

    @Query("SELECT * FROM user_quotes ORDER BY createdAt DESC")
    fun getAllUserQuotes(): Flow<List<UserQuoteEntity>>

    @Query("SELECT * FROM user_quotes ORDER BY createdAt DESC")
    suspend fun getAllUserQuotesList(): List<UserQuoteEntity>

    @Query("SELECT * FROM user_quotes WHERE id = :id LIMIT 1")
    suspend fun getUserQuoteById(id: String): UserQuoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserQuote(userQuote: UserQuoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUserQuotes(userQuotes: List<UserQuoteEntity>)

    @Update
    suspend fun updateUserQuote(userQuote: UserQuoteEntity)

    @Query("DELETE FROM user_quotes WHERE id = :id")
    suspend fun deleteUserQuote(id: String)

    @Query("DELETE FROM user_quotes")
    suspend fun deleteAllUserQuotes()

    @Query("SELECT COUNT(*) FROM user_quotes")
    suspend fun getUserQuotesCount(): Int

    @Query("SELECT COUNT(*) FROM user_quotes")
    fun getUserQuotesCountFlow(): Flow<Int>
}
