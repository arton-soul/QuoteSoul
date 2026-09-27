package com.soulquote.app.domain.model

data class QuoteCategory(
    val id: String,
    val name: String,
    val slug: String,
    val description: String? = null,
    val iconName: String? = null,
    val sortOrder: Int = 0
)
