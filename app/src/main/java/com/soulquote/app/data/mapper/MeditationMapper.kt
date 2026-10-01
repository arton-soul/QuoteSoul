package com.soulquote.app.data.mapper

import com.soulquote.app.data.local.entity.content.MeditationCategoryEntity
import com.soulquote.app.data.local.entity.content.MeditationEntity
import com.soulquote.app.domain.model.Meditation
import com.soulquote.app.domain.model.MeditationCategory

fun MeditationEntity.toDomain(isDownloaded: Boolean = false, isFavorite: Boolean = false): Meditation {
    return Meditation(
        id = id,
        title = title,
        description = description,
        categoryId = category,
        durationSeconds = durationSeconds,
        instructor = instructor,
        audioUrl = audioUrl,
        localFileName = localFileName,
        version = version,
        checksum = checksum,
        sizeBytes = sizeBytes,
        meditationStartSeconds = meditationStartSeconds,
        youtubeUrl = youtubeUrl,
        isActive = active,
        isDownloaded = isDownloaded,
        isFavorite = isFavorite
    )
}

fun MeditationCategoryEntity.toDomain(): MeditationCategory {
    return MeditationCategory(
        id = id,
        name = name,
        slug = slug,
        description = description,
        iconName = iconName,
        sortOrder = sortOrder
    )
}

