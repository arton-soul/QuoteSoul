package com.soulquote.app.data.local.entity.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_entries",
    indices = [
        Index(value = ["date"]),
        Index(value = ["createdAt"])
    ]
)
data class JournalEntryEntity(
    @PrimaryKey
    val id: String,
    val date: String, // Format: YYYY-MM-DD
    val mood: String, // PEACEFUL, GRATEFUL, CALM, ENERGETIC, TIRED, ANXIOUS
    val reflectionPrompt: String,
    val content: String,
    val quoteId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
