package com.soulquote.app.core.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.soulquote.app.SoulQuoteApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

class DailyQuoteNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_DAILY_QUOTE) return

        val pendingResult = goAsync()
        val app = context.applicationContext as? SoulQuoteApp ?: run {
            pendingResult.finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appContainer = app.appContainer
                val todayEpochDay = LocalDate.now().toEpochDay()
                val quote = appContainer.quoteRepository.getDailyQuote(todayEpochDay)
                if (quote != null) {
                    appContainer.notificationHelper.showDailyQuoteNotification(
                        quoteId = quote.id,
                        quoteText = quote.text,
                        author = quote.author
                    )
                }

                // Sync home screen AppWidget with today's quote
                com.soulquote.app.core.widget.DailyQuoteWidgetProvider.notifyWidgetUpdate(context)

                // Reschedule for next day if setting is still active
                val settings = appContainer.userRepository.getUserSettings().firstOrNull()
                if (settings?.dailyQuoteEnabled == true) {
                    appContainer.notificationScheduler.scheduleDailyQuote(
                        hour = settings.dailyQuoteHour,
                        minute = settings.dailyQuoteMinute
                    )
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_DAILY_QUOTE = "com.soulquote.app.action.DAILY_QUOTE_NOTIFICATION"
    }
}
