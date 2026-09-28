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

    fun getLocalizedName(strings: com.soulquote.app.core.localization.AppStrings): String {
        return when (this) {
            PEACEFUL -> strings.moodPeaceful
            GRATEFUL -> strings.moodGrateful
            CALM -> strings.moodCalm
            ENERGETIC -> strings.moodEnergetic
            TIRED -> strings.moodTired
            ANXIOUS -> strings.moodAnxious
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
