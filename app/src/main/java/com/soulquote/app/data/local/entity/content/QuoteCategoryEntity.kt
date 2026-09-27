package com.soulquote.app.data.local.entity.content

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quote_categories")
data class QuoteCategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val slug: String,
    val description: String? = null,
    val iconName: String? = null,
    val sortOrder: Int = 0
)
