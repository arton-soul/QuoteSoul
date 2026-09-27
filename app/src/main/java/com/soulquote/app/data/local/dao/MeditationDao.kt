package com.soulquote.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.soulquote.app.data.local.entity.content.MeditationCategoryEntity
import com.soulquote.app.data.local.entity.content.MeditationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeditationDao {
    @Query("SELECT * FROM meditations WHERE active = 1 ORDER BY title ASC")
    fun getAllMeditations(): Flow<List<MeditationEntity>>

    @Query("SELECT * FROM meditations WHERE active = 1 AND category = :categoryId ORDER BY title ASC")
    fun getMeditationsByCategory(categoryId: String): Flow<List<MeditationEntity>>

    @Query("SELECT * FROM meditations WHERE id = :id")
    suspend fun getMeditationById(id: String): MeditationEntity?

    @Query("SELECT COUNT(*) FROM meditations WHERE active = 1")
    suspend fun getActiveMeditationCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeditations(meditations: List<MeditationEntity>)

    @Query("SELECT * FROM meditation_categories ORDER BY sortOrder ASC, name ASC")
    fun getAllCategories(): Flow<List<MeditationCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<MeditationCategoryEntity>)

    @Query("DELETE FROM meditations")
    suspend fun clearMeditations()

    @Query("DELETE FROM meditation_categories")
    suspend fun clearCategories()

    @Transaction
    suspend fun replaceAllMeditations(
        meditations: List<MeditationEntity>,
        categories: List<MeditationCategoryEntity>
    ) {
        clearMeditations()
        clearCategories()
        insertCategories(categories)
        insertMeditations(meditations)
    }
}
