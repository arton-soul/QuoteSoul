package com.soulquote.app.core.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.soulquote.app.SoulQuoteApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MeditationReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_MEDITATION_REMINDER) return

        val pendingResult = goAsync()
        val app = context.applicationContext as? SoulQuoteApp ?: run {
            pendingResult.finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appContainer = app.appContainer
                appContainer.notificationHelper.showMeditationReminder()

                val settings = appContainer.userRepository.getUserSettings().firstOrNull()
                if (settings?.meditationReminderEnabled == true) {
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

    companion object {
        const val ACTION_MEDITATION_REMINDER = "com.soulquote.app.action.MEDITATION_REMINDER"
    }
}