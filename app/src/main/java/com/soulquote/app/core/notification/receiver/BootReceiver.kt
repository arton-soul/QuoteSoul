package com.soulquote.app.core.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.soulquote.app.SoulQuoteApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_MY_PACKAGE_REPLACED) {
            return
        }

        val pendingResult = goAsync()
        val app = context.applicationContext as? SoulQuoteApp ?: run {
            pendingResult.finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appContainer = app.appContainer
                val settings = appContainer.userRepository.getUserSettings().firstOrNull() ?: return@launch

                if (settings.dailyQuoteEnabled) {
                    appContainer.notificationScheduler.scheduleDailyQuote(
                        hour = settings.dailyQuoteHour,
                        minute = settings.dailyQuoteMinute
                    )
                }

                if (settings.meditationReminderEnabled) {
                    appContainer.notificationScheduler.scheduleMeditationReminder(
                        hour = settings.meditationReminderHour,
                        minute = settings.meditationReminderMinute
                    )
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}