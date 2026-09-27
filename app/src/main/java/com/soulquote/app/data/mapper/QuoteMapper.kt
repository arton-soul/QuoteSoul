package com.soulquote.app.data.mapper

import com.soulquote.app.data.local.entity.content.QuoteCategoryEntity
import com.soulquote.app.data.local.entity.content.QuoteEntity
import com.soulquote.app.domain.model.Quote
import com.soulquote.app.domain.model.QuoteCategory

fun QuoteEntity.toDomain(isFavorite: Boolean = false): Quote {
    return Quote(
        id = id,
        text = text,
        author = author,
        categoryId = category,
        tags = tags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
        language = language,
        isActive = active,
        source = source,
        attribution = attribution,
        priority = priority,
        isFavorite = isFavorite,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Quote.toEntity(): QuoteEntity {
    return QuoteEntity(
        id = id,
        text = text,
        author = author,
        category = categoryId,
        tags = tags.joinToString(","),
        language = language,
        active = isActive,
        source = source,
        attribution = attribution,
        priority = priority,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun QuoteCategoryEntity.toDomain(): QuoteCategory {
    return QuoteCategory(
        id = id,
        name = name,
        slug = slug,
        description = description,
        iconName = iconName,
        sortOrder = sortOrder
    )
}

fun QuoteCategory.toEntity(): QuoteCategoryEntity {
    return QuoteCategoryEntity(
        id = id,
        name = name,
        slug = slug,
        description = description,
        iconName = iconName,
        sortOrder = sortOrder
    )
}
