package com.soulquote.app.data.local.entity.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_quotes")
data class UserQuoteEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val author: String = "Pribadi",
    val category: String = "custom",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
