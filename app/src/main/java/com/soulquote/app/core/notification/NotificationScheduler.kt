package com.soulquote.app.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.soulquote.app.core.notification.receiver.DailyQuoteNotificationReceiver
import com.soulquote.app.core.notification.receiver.MeditationReminderReceiver
import java.util.Calendar

class NotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun scheduleDailyQuote(hour: Int, minute: Int) {
        val triggerMillis = calculateNextTriggerMillis(hour, minute)
        val pendingIntent = getDailyQuotePendingIntent(PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        scheduleAlarm(triggerMillis, pendingIntent)
    }

    fun cancelDailyQuote() {
        val pendingIntent = getDailyQuotePendingIntent(PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun scheduleMeditationReminder(hour: Int, minute: Int) {
        val triggerMillis = calculateNextTriggerMillis(hour, minute)
        val pendingIntent = getMeditationPendingIntent(PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        scheduleAlarm(triggerMillis, pendingIntent)
    }

    fun cancelMeditationReminder() {
        val pendingIntent = getMeditationPendingIntent(PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun scheduleAlarm(triggerMillis: Long, pendingIntent: PendingIntent) {
        if (alarmManager == null) return

        try {
            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }

            if (canScheduleExact) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        }
    }

    private fun getDailyQuotePendingIntent(flags: Int): PendingIntent {
        val intent = Intent(context, DailyQuoteNotificationReceiver::class.java).apply {
            action = DailyQuoteNotificationReceiver.ACTION_DAILY_QUOTE
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_DAILY_QUOTE,
            intent,
            flags
        )
    }

    private fun getMeditationPendingIntent(flags: Int): PendingIntent {
        val intent = Intent(context, MeditationReminderReceiver::class.java).apply {
            action = MeditationReminderReceiver.ACTION_MEDITATION_REMINDER
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_MEDITATION,
            intent,
            flags
        )
    }

    companion object {
        const val REQUEST_CODE_DAILY_QUOTE = 2001
        const val REQUEST_CODE_MEDITATION = 2002

        fun calculateNextTriggerMillis(
            hour: Int,
            minute: Int,
            currentMillis: Long = System.currentTimeMillis()
        ): Long {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = currentMillis
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (calendar.timeInMillis <= currentMillis) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            return calendar.timeInMillis
        }
    }
}
