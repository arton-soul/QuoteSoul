package com.soulquote.app.core.localization

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val localeTag: String
) {
    INDONESIAN("in", "Bahasa Indonesia", "Bahasa Indonesia (Default)", "in-ID"),
    ENGLISH("en", "English", "English", "en-US");

    companion object {
        val DEFAULT = INDONESIAN

        fun fromCode(code: String?): AppLanguage {
            if (code == null) return DEFAULT
            val normalized = code.trim().lowercase()
            return when {
                normalized == "en" -> ENGLISH
                normalized == "in" || normalized == "id" -> INDONESIAN
                else -> DEFAULT
            }
        }
    }
}
