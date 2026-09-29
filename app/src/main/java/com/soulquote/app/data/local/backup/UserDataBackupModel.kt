package com.soulquote.app.data.local.backup

import org.json.JSONArray
import org.json.JSONObject

data class FavoriteBackupDto(
    val id: String,
    val quoteId: String,
    val favoritedAt: Long
)

data class MeditationHistoryBackupDto(
    val id: String,
    val meditationId: String,
    val completedAt: Long,
    val durationListenedSeconds: Int,
    val completed: Boolean
)

data class JournalEntryBackupDto(
    val id: String,
    val date: String,
    val mood: String,
    val reflectionPrompt: String,
    val content: String,
    val quoteId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class UserQuoteBackupDto(
    val id: String,
    val text: String,
    val author: String = "Pribadi",
    val category: String = "custom",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class UserDataBackup(
    val backupVersion: Int = 1,
    val appVersion: Int = 1,
    val appName: String = "SoulQuote",
    val exportedAt: Long = System.currentTimeMillis(),
    val favorites: List<FavoriteBackupDto> = emptyList(),
    val meditationHistory: List<MeditationHistoryBackupDto> = emptyList(),
    val journalEntries: List<JournalEntryBackupDto> = emptyList(),
    val userQuotes: List<UserQuoteBackupDto> = emptyList(),
    val settings: Map<String, String> = emptyMap(),
    val checksumSha256: String = ""
) {
    fun toJsonString(): String {
        val root = JSONObject()
        root.put("backupVersion", backupVersion)
        root.put("appVersion", appVersion)
        root.put("appName", appName)
        root.put("exportedAt", exportedAt)

        val dataObj = JSONObject()

        val favArray = JSONArray()
        for (f in favorites) {
            val obj = JSONObject()
            obj.put("id", f.id)
            obj.put("quoteId", f.quoteId)
            obj.put("favoritedAt", f.favoritedAt)
            favArray.put(obj)
        }
        dataObj.put("favorites", favArray)

        val histArray = JSONArray()
        for (h in meditationHistory) {
            val obj = JSONObject()
            obj.put("id", h.id)
            obj.put("meditationId", h.meditationId)
            obj.put("completedAt", h.completedAt)
            obj.put("durationListenedSeconds", h.durationListenedSeconds)
            obj.put("completed", h.completed)
            histArray.put(obj)
        }
        dataObj.put("meditationHistory", histArray)

        val journalArray = JSONArray()
        for (j in journalEntries) {
            val obj = JSONObject()
            obj.put("id", j.id)
            obj.put("date", j.date)
            obj.put("mood", j.mood)
            obj.put("reflectionPrompt", j.reflectionPrompt)
            obj.put("content", j.content)
            if (j.quoteId != null) {
                obj.put("quoteId", j.quoteId)
            }
            obj.put("createdAt", j.createdAt)
            obj.put("updatedAt", j.updatedAt)
            journalArray.put(obj)
        }
        dataObj.put("journalEntries", journalArray)

        val uqArray = JSONArray()
        for (u in userQuotes) {
            val obj = JSONObject()
            obj.put("id", u.id)
            obj.put("text", u.text)
            obj.put("author", u.author)
            obj.put("category", u.category)
            obj.put("createdAt", u.createdAt)
            obj.put("updatedAt", u.updatedAt)
            uqArray.put(obj)
        }
        dataObj.put("userQuotes", uqArray)

        val setObj = JSONObject()
        for ((k, v) in settings) {
            setObj.put(k, v)
        }
        dataObj.put("settings", setObj)

        root.put("data", dataObj)
        root.put("checksumSha256", checksumSha256)

        return root.toString(2)
    }

    companion object {
        fun fromJsonString(jsonStr: String): UserDataBackup {
            val root = JSONObject(jsonStr)
            val backupVersion = root.optInt("backupVersion", 1)
            val appVersion = root.optInt("appVersion", 1)
            val appName = root.optString("appName", "SoulQuote")
            val exportedAt = root.optLong("exportedAt", System.currentTimeMillis())
            val checksumSha256 = root.optString("checksumSha256", "")

            val dataObj = root.optJSONObject("data") ?: JSONObject()

            val favorites = mutableListOf<FavoriteBackupDto>()
            val favArray = dataObj.optJSONArray("favorites")
            if (favArray != null) {
                for (i in 0 until favArray.length()) {
                    val obj = favArray.getJSONObject(i)
                    favorites.add(
                        FavoriteBackupDto(
                            id = obj.getString("id"),
                            quoteId = obj.getString("quoteId"),
                            favoritedAt = obj.optLong("favoritedAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            val history = mutableListOf<MeditationHistoryBackupDto>()
            val histArray = dataObj.optJSONArray("meditationHistory")
            if (histArray != null) {
                for (i in 0 until histArray.length()) {
                    val obj = histArray.getJSONObject(i)
                    history.add(
                        MeditationHistoryBackupDto(
                            id = obj.getString("id"),
                            meditationId = obj.getString("meditationId"),
                            completedAt = obj.optLong("completedAt", System.currentTimeMillis()),
                            durationListenedSeconds = obj.optInt("durationListenedSeconds", 0),
                            completed = obj.optBoolean("completed", true)
                        )
                    )
                }
            }

            val journalList = mutableListOf<JournalEntryBackupDto>()
            val journalArray = dataObj.optJSONArray("journalEntries")
            if (journalArray != null) {
                for (i in 0 until journalArray.length()) {
                    val obj = journalArray.getJSONObject(i)
                    journalList.add(
                        JournalEntryBackupDto(
                            id = obj.getString("id"),
                            date = obj.getString("date"),
                            mood = obj.optString("mood", "CALM"),
                            reflectionPrompt = obj.optString("reflectionPrompt", ""),
                            content = obj.optString("content", ""),
                            quoteId = if (obj.has("quoteId") && !obj.isNull("quoteId")) obj.getString("quoteId") else null,
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            val userQuotes = mutableListOf<UserQuoteBackupDto>()
            val uqArray = dataObj.optJSONArray("userQuotes")
            if (uqArray != null) {
                for (i in 0 until uqArray.length()) {
                    val obj = uqArray.getJSONObject(i)
                    userQuotes.add(
                        UserQuoteBackupDto(
                            id = obj.getString("id"),
                            text = obj.getString("text"),
                            author = obj.optString("author", "Pribadi"),
                            category = obj.optString("category", "custom"),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                }
            }

            val settings = mutableMapOf<String, String>()
            val setObj = dataObj.optJSONObject("settings")
            if (setObj != null) {
                val keys = setObj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    settings[key] = setObj.optString(key, "")
                }
            }

            return UserDataBackup(
                backupVersion = backupVersion,
                appVersion = appVersion,
                appName = appName,
                exportedAt = exportedAt,
                favorites = favorites,
                meditationHistory = history,
                journalEntries = journalList,
                userQuotes = userQuotes,
                settings = settings,
                checksumSha256 = checksumSha256
            )
        }
    }
}
