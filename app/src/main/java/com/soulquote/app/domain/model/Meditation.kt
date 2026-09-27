package com.soulquote.app.domain.model

data class Meditation(
    val id: String,
    val title: String,
    val description: String?,
    val categoryId: String,
    val durationSeconds: Int,
    val instructor: String?,
    val audioUrl: String,
    val localFileName: String,
    val version: Int = 1,
    val checksum: String,
    val sizeBytes: Long,
    val isActive: Boolean = true,
    val isDownloaded: Boolean = false,
    val isFavorite: Boolean = false
)

data class MeditationCategory(
    val id: String,
    val name: String,
    val slug: String,
    val description: String? = null,
    val iconName: String? = null,
    val sortOrder: Int = 0
)
