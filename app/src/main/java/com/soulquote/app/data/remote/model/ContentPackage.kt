package com.soulquote.app.data.remote.model

import com.soulquote.app.data.local.entity.content.MeditationCategoryEntity
import com.soulquote.app.data.local.entity.content.MeditationEntity
import com.soulquote.app.data.local.entity.content.QuoteCategoryEntity
import com.soulquote.app.data.local.entity.content.QuoteEntity
import com.soulquote.app.data.local.entity.content.TemplateEntity
import org.json.JSONObject

data class ContentPackage(
    val contentVersion: Int,
    val categories: List<QuoteCategoryEntity> = emptyList(),
    val quotes: List<QuoteEntity> = emptyList(),
    val templates: List<TemplateEntity> = emptyList(),
    val meditationCategories: List<MeditationCategoryEntity> = emptyList(),
    val meditations: List<MeditationEntity> = emptyList()
) {
    companion object {
        private fun JSONObject.getNullableString(key: String): String? {
            return if (has(key) && !isNull(key)) getString(key) else null
        }

        private fun JSONObject.getNullableInt(key: String): Int? {
            return if (has(key) && !isNull(key)) getInt(key) else null
        }

        fun fromJson(jsonStr: String): ContentPackage {
            val json = JSONObject(jsonStr)
            val contentVersion = json.optInt("contentVersion", 1)

            val categories = mutableListOf<QuoteCategoryEntity>()
            val categoriesArray = json.optJSONArray("categories")
            if (categoriesArray != null) {
                for (i in 0 until categoriesArray.length()) {
                    val obj = categoriesArray.getJSONObject(i)
                    categories.add(
                        QuoteCategoryEntity(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            slug = obj.getString("slug"),
                            description = obj.getNullableString("description"),
                            iconName = obj.getNullableString("iconName"),
                            sortOrder = obj.optInt("sortOrder", i)
                        )
                    )
                }
            }

            val quotes = mutableListOf<QuoteEntity>()
            val quotesArray = json.optJSONArray("quotes")
            if (quotesArray != null) {
                for (i in 0 until quotesArray.length()) {
                    val obj = quotesArray.getJSONObject(i)
                    quotes.add(
                        QuoteEntity(
                            id = obj.getString("id"),
                            text = obj.getString("text"),
                            author = obj.getString("author"),
                            category = obj.getString("category"),
                            tags = obj.getNullableString("tags"),
                            language = obj.optString("language", "en"),
                            active = obj.optBoolean("active", true),
                            source = obj.getNullableString("source"),
                            attribution = obj.getNullableString("attribution"),
                            priority = obj.optInt("priority", 0)
                        )
                    )
                }
            }

            val templates = mutableListOf<TemplateEntity>()
            val templatesArray = json.optJSONArray("templates")
            if (templatesArray != null) {
                for (i in 0 until templatesArray.length()) {
                    val obj = templatesArray.getJSONObject(i)
                    templates.add(
                        TemplateEntity(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            previewAsset = obj.getNullableString("previewAsset"),
                            backgroundType = obj.getString("backgroundType"),
                            backgroundValue = obj.getString("backgroundValue"),
                            fontName = obj.getString("fontName"),
                            fontSizeSp = obj.getInt("fontSizeSp"),
                            textColorHex = obj.getString("textColorHex"),
                            alignment = obj.getString("alignment"),
                            overlayType = obj.getNullableString("overlayType"),
                            overlayOpacity = obj.optDouble("overlayOpacity", 0.0).toFloat(),
                            active = obj.optBoolean("active", true),
                            version = obj.optInt("version", 1)
                        )
                    )
                }
            }

            val medCategories = mutableListOf<MeditationCategoryEntity>()
            val medCategoriesArray = json.optJSONArray("meditationCategories")
            if (medCategoriesArray != null) {
                for (i in 0 until medCategoriesArray.length()) {
                    val obj = medCategoriesArray.getJSONObject(i)
                    medCategories.add(
                        MeditationCategoryEntity(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            slug = obj.getString("slug"),
                            description = obj.getNullableString("description"),
                            iconName = obj.getNullableString("iconName"),
                            sortOrder = obj.optInt("sortOrder", i)
                        )
                    )
                }
            }

            val meditations = mutableListOf<MeditationEntity>()
            val meditationsArray = json.optJSONArray("meditations")
            if (meditationsArray != null) {
                for (i in 0 until meditationsArray.length()) {
                    val obj = meditationsArray.getJSONObject(i)
                    meditations.add(
                        MeditationEntity(
                            id = obj.getString("id"),
                            title = obj.getString("title"),
                            description = obj.getNullableString("description"),
                            category = obj.getString("category"),
                            durationSeconds = obj.getInt("durationSeconds"),
                            instructor = obj.getNullableString("instructor"),
                            audioUrl = obj.getString("audioUrl"),
                            localFileName = obj.getString("localFileName"),
                            version = obj.optInt("version", 1),
                            checksum = obj.getString("checksum"),
                            sizeBytes = obj.getLong("sizeBytes"),
                            meditationStartSeconds = obj.getNullableInt("meditationStartSeconds"),
                            youtubeUrl = obj.getNullableString("youtubeUrl"),
                            active = obj.optBoolean("active", true)
                        )
                    )
                }
            }

            return ContentPackage(
                contentVersion = contentVersion,
                categories = categories,
                quotes = quotes,
                templates = templates,
                meditationCategories = medCategories,
                meditations = meditations
            )
        }
    }
}
