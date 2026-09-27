package com.soulquote.app.data.remote.model

import org.json.JSONObject

data class ContentManifest(
    val manifestVersion: Int = 1,
    val contentVersion: Int,
    val minAppVersion: Int = 1,
    val releasedAt: String = "",
    val packageUrl: String,
    val packageSizeBytes: Long = 0L,
    val packageChecksumSha256: String,
    val changelog: String = "",
    val quotesCount: Int = 0,
    val meditationsCount: Int = 0
) {
    companion object {
        fun fromJson(jsonStr: String): ContentManifest {
            val json = JSONObject(jsonStr)
            return ContentManifest(
                manifestVersion = json.optInt("manifestVersion", 1),
                contentVersion = json.getInt("contentVersion"),
                minAppVersion = json.optInt("minAppVersion", 1),
                releasedAt = json.optString("releasedAt", ""),
                packageUrl = json.getString("packageUrl"),
                packageSizeBytes = json.optLong("packageSizeBytes", 0L),
                packageChecksumSha256 = json.getString("packageChecksumSha256").trim().lowercase(),
                changelog = json.optString("changelog", ""),
                quotesCount = json.optInt("quotesCount", 0),
                meditationsCount = json.optInt("meditationsCount", 0)
            )
        }
    }
}
