package com.soulquote.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.soulquote.app.data.local.entity.user.FavoriteEntity
import com.soulquote.app.data.local.entity.user.MeditationHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY favoritedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT quoteId FROM favorites")
    fun getFavoriteQuoteIdsFlow(): Flow<List<String>>

    @Query("SELECT quoteId FROM favorites")
    suspend fun getFavoriteQuoteIds(): List<String>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE quoteId = :quoteId)")
    suspend fun isFavorite(quoteId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE quoteId = :quoteId)")
    fun isFavoriteFlow(quoteId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(favorites: List<FavoriteEntity>)

    @Query("SELECT * FROM favorites ORDER BY favoritedAt DESC")
    suspend fun getAllFavoritesList(): List<FavoriteEntity>

    @Query("DELETE FROM favorites WHERE quoteId = :quoteId")
    suspend fun removeFavorite(quoteId: String)

    @Query("DELETE FROM favorites")
    suspend fun clearFavorites()
}

@Dao
interface MeditationHistoryDao {
    @Query("SELECT * FROM meditation_history ORDER BY completedAt DESC")
    fun getHistory(): Flow<List<MeditationHistoryEntity>>

    @Query("SELECT * FROM meditation_history ORDER BY completedAt DESC")
    suspend fun getAllHistoryList(): List<MeditationHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordHistory(item: MeditationHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryList(items: List<MeditationHistoryEntity>)

    @Query("SELECT COUNT(*) FROM meditation_history WHERE completed = 1")
    suspend fun getCompletedSessionCount(): Int

    @Query("DELETE FROM meditation_history")
    suspend fun clearHistory()
}
