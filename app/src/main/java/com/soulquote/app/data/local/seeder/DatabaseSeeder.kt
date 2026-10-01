package com.soulquote.app.data.local.seeder

import android.content.Context
import com.soulquote.app.data.local.SoulQuoteContentDatabase
import com.soulquote.app.data.local.entity.content.AppConfigEntity
import com.soulquote.app.data.local.entity.content.MeditationCategoryEntity
import com.soulquote.app.data.local.entity.content.MeditationEntity
import com.soulquote.app.data.local.entity.content.QuoteCategoryEntity
import com.soulquote.app.data.local.entity.content.QuoteEntity
import com.soulquote.app.data.local.entity.content.TemplateEntity
import com.soulquote.app.BuildConfig
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

    private fun JSONObject.getNullableInt(key: String): Int? {
        return if (has(key) && !isNull(key)) getInt(key) else null
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

            val medCategoriesArray = json.optJSONArray("meditationCategories")
            val medCategories = mutableListOf<MeditationCategoryEntity>()
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

            val meditationsArray = json.optJSONArray("meditations")
            val meditations = mutableListOf<MeditationEntity>()
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

            if (categories.isNotEmpty()) {
                database.quoteDao().insertCategories(categories)
            }
            if (quotes.isNotEmpty()) {
                database.quoteDao().insertQuotes(quotes)
            }
            if (templates.isNotEmpty()) {
                database.templateDao().insertTemplates(templates)
            }
            if (medCategories.isNotEmpty()) {
                database.meditationDao().insertCategories(medCategories)
            }
            if (meditations.isNotEmpty()) {
                database.meditationDao().insertMeditations(meditations)
            }

            database.appConfigDao().setConfigValue(
                AppConfigEntity("content_version", contentVersion.toString())
            )

            val existingRemoteUrl = database.appConfigDao().getConfigValue("remote_manifest_url")
            if (existingRemoteUrl.isNullOrBlank()) {
                database.appConfigDao().setConfigValue(
                    AppConfigEntity("remote_manifest_url", BuildConfig.DEFAULT_REMOTE_MANIFEST_URL)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
