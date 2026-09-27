package com.soulquote.app.data.local.entity.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    indices = [Index(value = ["quoteId"], unique = true)]
)
data class FavoriteEntity(
    @PrimaryKey
    val id: String,
    val quoteId: String,
    val favoritedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "meditation_history",
    indices = [
        Index(value = ["meditationId"]),
        Index(value = ["completedAt"])
    ]
)
data class MeditationHistoryEntity(
    @PrimaryKey
    val id: String,
    val meditationId: String,
    val completedAt: Long = System.currentTimeMillis(),
    val durationListenedSeconds: Int,
    val completed: Boolean
)
