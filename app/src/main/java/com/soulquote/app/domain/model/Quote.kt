package com.soulquote.app.domain.model

data class Quote(
    val id: String,
    val text: String,
    val author: String,
    val categoryId: String,
    val tags: List<String> = emptyList(),
    val language: String = "en",
    val isActive: Boolean = true,
    val source: String? = null,
    val attribution: String? = null,
    val priority: Int = 0,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)