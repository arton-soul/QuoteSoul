package com.soulquote.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.soulquote.app.data.local.entity.user.DownloadedAudioEntity
import com.soulquote.app.data.local.entity.user.UserSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingDao {
    @Query("SELECT * FROM user_settings")
    fun getAllSettings(): Flow<List<UserSettingEntity>>

    @Query("SELECT value FROM user_settings WHERE `key` = :key")
    suspend fun getSettingValue(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSetting(setting: UserSettingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: List<UserSettingEntity>)
}

@Dao
interface DownloadedAudioDao {
    @Query("SELECT * FROM downloaded_audio ORDER BY downloadedAt DESC")
    fun getAllDownloadedAudio(): Flow<List<DownloadedAudioEntity>>

    @Query("SELECT assetId FROM downloaded_audio")
    suspend fun getAllDownloadedAssetIds(): List<String>

    @Query("SELECT * FROM downloaded_audio WHERE assetId = :assetId LIMIT 1")
    suspend fun getDownloadedAudioByAssetId(assetId: String): DownloadedAudioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloadedAudio(entity: DownloadedAudioEntity)

    @Query("DELETE FROM downloaded_audio WHERE assetId = :assetId")
    suspend fun deleteByAssetId(assetId: String)
}
