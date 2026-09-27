package com.soulquote.app.domain.model

enum class MoodType(val emoji: String, val displayName: String) {
    PEACEFUL("🕊️", "Damai"),
    GRATEFUL("🙏", "Bersyukur"),
    CALM("🌿", "Tenang"),
    ENERGETIC("⚡", "Bersemangat"),
    TIRED("🌙", "Lelah"),
    ANXIOUS("🌧️", "Gelisah");

    companion object {
        fun fromString(value: String): MoodType {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: CALM
        }
    }
}

data class JournalEntry(
    val id: String,
    val date: String,
    val mood: MoodType,
    val reflectionPrompt: String,
    val content: String,
    val quoteId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
