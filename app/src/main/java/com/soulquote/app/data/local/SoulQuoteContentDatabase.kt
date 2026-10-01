package com.soulquote.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.soulquote.app.data.local.dao.AppConfigDao
import com.soulquote.app.data.local.dao.MeditationDao
import com.soulquote.app.data.local.dao.QuoteDao
import com.soulquote.app.data.local.dao.TemplateDao
import com.soulquote.app.data.local.entity.content.AppConfigEntity
import com.soulquote.app.data.local.entity.content.MeditationCategoryEntity
import com.soulquote.app.data.local.entity.content.MeditationEntity
import com.soulquote.app.data.local.entity.content.QuoteCategoryEntity
import com.soulquote.app.data.local.entity.content.QuoteEntity
import com.soulquote.app.data.local.entity.content.TemplateEntity

@Database(
    entities = [
        QuoteEntity::class,
        QuoteCategoryEntity::class,
        MeditationEntity::class,
        MeditationCategoryEntity::class,
        TemplateEntity::class,
        AppConfigEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class SoulQuoteContentDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
    abstract fun meditationDao(): MeditationDao
    abstract fun templateDao(): TemplateDao
    abstract fun appConfigDao(): AppConfigDao

    companion object {
        const val DATABASE_NAME = "soulquote_content.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `meditations` ADD COLUMN `meditationStartSeconds` INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE `meditations` ADD COLUMN `youtubeUrl` TEXT DEFAULT NULL")
            }
        }

        @Volatile
        private var INSTANCE: SoulQuoteContentDatabase? = null

        fun getInstance(context: Context): SoulQuoteContentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoulQuoteContentDatabase::class.java,
                    DATABASE_NAME
                ).addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

