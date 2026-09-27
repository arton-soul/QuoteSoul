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

    @Query("DELETE FROM favorites WHERE quoteId = :quoteId")
    suspend fun removeFavorite(quoteId: String)
}

@Dao
interface MeditationHistoryDao {
    @Query("SELECT * FROM meditation_history ORDER BY completedAt DESC")
    fun getHistory(): Flow<List<MeditationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordHistory(item: MeditationHistoryEntity)

    @Query("SELECT COUNT(*) FROM meditation_history WHERE completed = 1")
    suspend fun getCompletedSessionCount(): Int
}
