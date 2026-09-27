package com.soulquote.app

import android.app.Application

import com.soulquote.app.core.di.AppContainer
import com.soulquote.app.core.di.DefaultAppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SoulQuoteApp : Application() {
    lateinit var appContainer: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
        appContainer.notificationHelper.createNotificationChannels()

        applicationScope.launch {
            appContainer.databaseSeeder.seedIfNecessary()

            val settings = appContainer.userRepository.getUserSettings().firstOrNull()
            if (settings?.dailyQuoteEnabled == true) {
                appContainer.notificationScheduler.scheduleDailyQuote(
                    hour = settings.dailyQuoteHour,
                    minute = settings.dailyQuoteMinute
                )
            }
            if (settings?.meditationReminderEnabled == true) {
                appContainer.notificationScheduler.scheduleMeditationReminder(
                    hour = settings.meditationReminderHour,
                    minute = settings.meditationReminderMinute
                )
            }
        }
    }
}
