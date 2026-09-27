package com.soulquote.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
    version = 1,
    exportSchema = true
)
abstract class SoulQuoteContentDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
    abstract fun meditationDao(): MeditationDao
    abstract fun templateDao(): TemplateDao
    abstract fun appConfigDao(): AppConfigDao

    companion object {
        const val DATABASE_NAME = "soulquote_content.db"

        @Volatile
        private var INSTANCE: SoulQuoteContentDatabase? = null

        fun getInstance(context: Context): SoulQuoteContentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoulQuoteContentDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
