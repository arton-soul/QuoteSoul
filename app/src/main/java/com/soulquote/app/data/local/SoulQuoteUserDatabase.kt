package com.soulquote.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.soulquote.app.data.local.dao.DownloadedAudioDao
import com.soulquote.app.data.local.dao.FavoriteDao
import com.soulquote.app.data.local.dao.MeditationHistoryDao
import com.soulquote.app.data.local.dao.UserSettingDao
import com.soulquote.app.data.local.entity.user.DownloadedAudioEntity
import com.soulquote.app.data.local.entity.user.FavoriteEntity
import com.soulquote.app.data.local.entity.user.MeditationHistoryEntity
import com.soulquote.app.data.local.entity.user.UserSettingEntity

@Database(
    entities = [
        FavoriteEntity::class,
        MeditationHistoryEntity::class,
        UserSettingEntity::class,
        DownloadedAudioEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class SoulQuoteUserDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun meditationHistoryDao(): MeditationHistoryDao
    abstract fun userSettingDao(): UserSettingDao
    abstract fun downloadedAudioDao(): DownloadedAudioDao

    companion object {
        const val DATABASE_NAME = "soulquote_user.db"

        @Volatile
        private var INSTANCE: SoulQuoteUserDatabase? = null

        fun getInstance(context: Context): SoulQuoteUserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoulQuoteUserDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
