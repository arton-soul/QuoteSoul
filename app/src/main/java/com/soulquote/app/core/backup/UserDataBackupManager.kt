package com.soulquote.app.core.backup

import android.content.Context
import androidx.room.withTransaction
import com.soulquote.app.BuildConfig
import com.soulquote.app.data.local.SoulQuoteUserDatabase
import com.soulquote.app.data.local.backup.FavoriteBackupDto
import com.soulquote.app.data.local.backup.MeditationHistoryBackupDto
import com.soulquote.app.data.local.backup.UserDataBackup
import com.soulquote.app.data.local.entity.user.FavoriteEntity
import com.soulquote.app.data.local.entity.user.MeditationHistoryEntity
import com.soulquote.app.data.local.entity.user.UserSettingEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserDataStats(
    val favoritesCount: Int,
    val historyCount: Int,
    val settingsCount: Int
)

data class RestoreResult(
    val favoritesRestored: Int,
    val historyRestored: Int,
    val settingsRestored: Int
)

class UserDataBackupManager(
    private val context: Context,
    private val userDatabase: SoulQuoteUserDatabase
) {
    private val backupDir: File by lazy {
        File(context.cacheDir, "backup").apply {
            if (!exists()) mkdirs()
        }
    }

    suspend fun getUserDataStats(): UserDataStats = withContext(Dispatchers.IO) {
        val favorites = userDatabase.favoriteDao().getAllFavoritesList().size
        val history = userDatabase.meditationHistoryDao().getAllHistoryList().size
        val settings = userDatabase.userSettingDao().getAllSettingsList().size
        UserDataStats(
            favoritesCount = favorites,
            historyCount = history,
            settingsCount = settings
        )
    }

    suspend fun exportUserData(): UserDataBackup = withContext(Dispatchers.IO) {
        val favoriteEntities = userDatabase.favoriteDao().getAllFavoritesList()
        val historyEntities = userDatabase.meditationHistoryDao().getAllHistoryList()
        val settingEntities = userDatabase.userSettingDao().getAllSettingsList()

        val favorites = favoriteEntities.map {
            FavoriteBackupDto(id = it.id, quoteId = it.quoteId, favoritedAt = it.favoritedAt)
        }
        val history = historyEntities.map {
            MeditationHistoryBackupDto(
                id = it.id,
                meditationId = it.meditationId,
                completedAt = it.completedAt,
                durationListenedSeconds = it.durationListenedSeconds,
                completed = it.completed
            )
        }
        val settings = settingEntities.associate { it.key to it.value }

        // Compute checksum of combined contents
        val contentDigest = MessageDigest.getInstance("SHA-256")
        val contentSummary = buildString {
            favorites.forEach { append("${it.quoteId};") }
            history.forEach { append("${it.meditationId}:${it.completedAt};") }
            settings.forEach { append("${it.key}=${it.value};") }
        }
        val checksum = contentDigest.digest(contentSummary.toByteArray()).joinToString("") { "%02x".format(it) }

        UserDataBackup(
            backupVersion = 1,
            appVersion = BuildConfig.VERSION_CODE,
            appName = "SoulQuote",
            exportedAt = System.currentTimeMillis(),
            favorites = favorites,
            meditationHistory = history,
            settings = settings,
            checksumSha256 = checksum
        )
    }

    suspend fun createBackupFile(): Result<File> = withContext(Dispatchers.IO) {
        try {
            val backup = exportUserData()
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(backupDir, "soulquote_backup_$timestamp.json")
            file.writeText(backup.toJsonString())
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreUserData(
        backupJson: String,
        replaceExisting: Boolean = false
    ): Result<RestoreResult> = withContext(Dispatchers.IO) {
        try {
            val backup = UserDataBackup.fromJsonString(backupJson)

            if (backup.appName != "SoulQuote") {
                return@withContext Result.failure(
                    IllegalArgumentException("Berkas cadangan tidak valid (bukan berkas SoulQuote)")
                )
            }

            if (backup.backupVersion > 1) {
                return@withContext Result.failure(
                    IllegalStateException("Versi berkas cadangan (${backup.backupVersion}) lebih baru dari yang didukung.")
                )
            }

            userDatabase.withTransaction {
                if (replaceExisting) {
                    userDatabase.favoriteDao().clearFavorites()
                    userDatabase.meditationHistoryDao().clearHistory()
                    userDatabase.userSettingDao().clearSettings()
                }

                if (backup.favorites.isNotEmpty()) {
                    val favorites = backup.favorites.map {
                        FavoriteEntity(id = it.id, quoteId = it.quoteId, favoritedAt = it.favoritedAt)
                    }
                    userDatabase.favoriteDao().insertFavorites(favorites)
                }

                if (backup.meditationHistory.isNotEmpty()) {
                    val history = backup.meditationHistory.map {
                        MeditationHistoryEntity(
                            id = it.id,
                            meditationId = it.meditationId,
                            completedAt = it.completedAt,
                            durationListenedSeconds = it.durationListenedSeconds,
                            completed = it.completed
                        )
                    }
                    userDatabase.meditationHistoryDao().insertHistoryList(history)
                }

                if (backup.settings.isNotEmpty()) {
                    val settings = backup.settings.map {
                        UserSettingEntity(key = it.key, value = it.value)
                    }
                    userDatabase.userSettingDao().saveSettings(settings)
                }
            }

            Result.success(
                RestoreResult(
                    favoritesRestored = backup.favorites.size,
                    historyRestored = backup.meditationHistory.size,
                    settingsRestored = backup.settings.size
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
