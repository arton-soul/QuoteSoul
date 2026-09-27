package com.soulquote.app.data.local.entity.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingEntity(
    @PrimaryKey
    val key: String,
    val value: String
)

@Entity(
    tableName = "downloaded_audio",
    indices = [Index(value = ["assetId"], unique = true)]
)
data class DownloadedAudioEntity(
    @PrimaryKey
    val id: String,
    val assetId: String,
    val assetType: String,
    val localPath: String,
    val downloadedAt: Long = System.currentTimeMillis(),
    val fileSize: Long,
    val checksum: String
)
