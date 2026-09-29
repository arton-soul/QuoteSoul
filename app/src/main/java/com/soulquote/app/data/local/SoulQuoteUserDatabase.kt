package com.soulquote.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.soulquote.app.data.local.dao.DownloadedAudioDao
import com.soulquote.app.data.local.dao.FavoriteDao
import com.soulquote.app.data.local.dao.JournalDao
import com.soulquote.app.data.local.dao.MeditationHistoryDao
import com.soulquote.app.data.local.dao.UserSettingDao
import com.soulquote.app.data.local.dao.UserQuoteDao
import com.soulquote.app.data.local.entity.user.DownloadedAudioEntity
import com.soulquote.app.data.local.entity.user.FavoriteEntity
import com.soulquote.app.data.local.entity.user.JournalEntryEntity
import com.soulquote.app.data.local.entity.user.MeditationHistoryEntity
import com.soulquote.app.data.local.entity.user.UserQuoteEntity
import com.soulquote.app.data.local.entity.user.UserSettingEntity

@Database(
    entities = [
        FavoriteEntity::class,
        MeditationHistoryEntity::class,
        UserSettingEntity::class,
        DownloadedAudioEntity::class,
        JournalEntryEntity::class,
        UserQuoteEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class SoulQuoteUserDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun meditationHistoryDao(): MeditationHistoryDao
    abstract fun userSettingDao(): UserSettingDao
    abstract fun downloadedAudioDao(): DownloadedAudioDao
    abstract fun journalDao(): JournalDao
    abstract fun userQuoteDao(): UserQuoteDao

    companion object {
        const val DATABASE_NAME = "soulquote_user.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `journal_entries` (
                        `id` TEXT NOT NULL,
                        `date` TEXT NOT NULL,
                        `mood` TEXT NOT NULL,
                        `reflectionPrompt` TEXT NOT NULL,
                        `content` TEXT NOT NULL,
                        `quoteId` TEXT,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_journal_entries_date` ON `journal_entries` (`date`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_journal_entries_createdAt` ON `journal_entries` (`createdAt`)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_quotes` (
                        `id` TEXT NOT NULL,
                        `text` TEXT NOT NULL,
                        `author` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_quotes_createdAt` ON `user_quotes` (`createdAt`)")
            }
        }

        @Volatile
        private var INSTANCE: SoulQuoteUserDatabase? = null

        fun getInstance(context: Context): SoulQuoteUserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoulQuoteUserDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
