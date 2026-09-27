package com.soulquote.app.data.local.seeder

import android.content.Context
import com.soulquote.app.data.local.SoulQuoteContentDatabase
import com.soulquote.app.data.local.entity.content.AppConfigEntity
import com.soulquote.app.data.local.entity.content.QuoteCategoryEntity
import com.soulquote.app.data.local.entity.content.QuoteEntity
import com.soulquote.app.data.local.entity.content.TemplateEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DatabaseSeeder(
    private val context: Context,
    private val database: SoulQuoteContentDatabase
) {
    private fun JSONObject.getNullableString(key: String): String? {
        return if (has(key) && !isNull(key)) getString(key) else null
    }

    suspend fun seedIfNecessary() = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("seed/initial_content.json")
                .bufferedReader()
                .use { it.readText() }

            val json = JSONObject(jsonString)
            val contentVersion = json.optInt("contentVersion", 1)

            val currentVersion = database.appConfigDao().getConfigValue("content_version")?.toIntOrNull() ?: 0
            if (currentVersion >= contentVersion) {
                return@withContext
            }

            val categoriesArray = json.optJSONArray("categories")
            val categories = mutableListOf<QuoteCategoryEntity>()
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

            val quotesArray = json.optJSONArray("quotes")
            val quotes = mutableListOf<QuoteEntity>()
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

            val templatesArray = json.optJSONArray("templates")
            val templates = mutableListOf<TemplateEntity>()
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

            if (categories.isNotEmpty()) {
                database.quoteDao().insertCategories(categories)
            }
            if (quotes.isNotEmpty()) {
                database.quoteDao().insertQuotes(quotes)
            }
            if (templates.isNotEmpty()) {
                database.templateDao().insertTemplates(templates)
            }

            database.appConfigDao().setConfigValue(
                AppConfigEntity("content_version", contentVersion.toString())
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
