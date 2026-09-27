package com.soulquote.app.core.di

import android.content.Context
import com.soulquote.app.data.local.SoulQuoteContentDatabase
import com.soulquote.app.data.local.SoulQuoteUserDatabase
import com.soulquote.app.data.local.seeder.DatabaseSeeder
import com.soulquote.app.data.repository.MeditationRepositoryImpl
import com.soulquote.app.data.repository.QuoteRepositoryImpl
import com.soulquote.app.data.repository.UserRepositoryImpl
import com.soulquote.app.domain.repository.MeditationRepository
import com.soulquote.app.domain.repository.QuoteRepository
import com.soulquote.app.domain.repository.UserRepository

interface AppContainer {
    val contentDatabase: SoulQuoteContentDatabase
    val userDatabase: SoulQuoteUserDatabase
    val quoteRepository: QuoteRepository
    val meditationRepository: MeditationRepository
    val userRepository: UserRepository
    val databaseSeeder: DatabaseSeeder
    val notificationHelper: com.soulquote.app.core.notification.NotificationHelper
    val notificationScheduler: com.soulquote.app.core.notification.NotificationScheduler
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val contentDatabase: SoulQuoteContentDatabase by lazy {
        SoulQuoteContentDatabase.getInstance(context)
    }

    override val userDatabase: SoulQuoteUserDatabase by lazy {
        SoulQuoteUserDatabase.getInstance(context)
    }

    override val quoteRepository: QuoteRepository by lazy {
        QuoteRepositoryImpl(
            quoteDao = contentDatabase.quoteDao(),
            favoriteDao = userDatabase.favoriteDao()
        )
    }

    override val meditationRepository: MeditationRepository by lazy {
        MeditationRepositoryImpl(
            meditationDao = contentDatabase.meditationDao(),
            downloadedAudioDao = userDatabase.downloadedAudioDao()
        )
    }

    override val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            userSettingDao = userDatabase.userSettingDao(),
            meditationHistoryDao = userDatabase.meditationHistoryDao(),
            downloadedAudioDao = userDatabase.downloadedAudioDao()
        )
    }

    override val databaseSeeder: DatabaseSeeder by lazy {
        DatabaseSeeder(
            context = context,
            database = contentDatabase
        )
    }

    override val notificationHelper: com.soulquote.app.core.notification.NotificationHelper by lazy {
        com.soulquote.app.core.notification.NotificationHelper(context)
    }

    override val notificationScheduler: com.soulquote.app.core.notification.NotificationScheduler by lazy {
        com.soulquote.app.core.notification.NotificationScheduler(context)
    }
}
