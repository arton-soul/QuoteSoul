package com.soulquote.app.data.local.entity.content

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meditations",
    indices = [
        Index(value = ["category"]),
        Index(value = ["active"])
    ]
)
data class MeditationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String? = null,
    val category: String,
    val durationSeconds: Int,
    val instructor: String? = null,
    val audioUrl: String,
    val localFileName: String,
    val version: Int = 1,
    val checksum: String,
    val sizeBytes: Long,
    val meditationStartSeconds: Int? = null,
    val youtubeUrl: String? = null,
    val active: Boolean = true
)

@Entity(tableName = "meditation_categories")
data class MeditationCategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val slug: String,
    val description: String? = null,
    val iconName: String? = null,
    val sortOrder: Int = 0
)

