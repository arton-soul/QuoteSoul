package com.soulquote.app.data.local.entity.content

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quotes",
    indices = [
        Index(value = ["category"]),
        Index(value = ["active"]),
        Index(value = ["priority"])
    ]
)
data class QuoteEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val author: String,
    val category: String,
    val tags: String? = null,
    val language: String = "en",
    val active: Boolean = true,
    val source: String? = null,
    val attribution: String? = null,
    val priority: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
