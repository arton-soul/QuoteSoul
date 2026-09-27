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
}
