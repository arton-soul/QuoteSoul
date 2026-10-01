package com.soulquote.app.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.soulquote.app.R
import com.soulquote.app.presentation.MainActivity

class NotificationHelper(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannels()
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val mindfulBellUri = Uri.parse(
                "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.mindful_bell}"
            )
            val dailyQuoteChannel = NotificationChannel(
                CHANNEL_DAILY_QUOTE_ID,
                context.getString(R.string.channel_daily_quote_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_daily_quote_desc)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setSound(mindfulBellUri, audioAttributes)
            }

            val zenBowlUri = Uri.parse(
                "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.zen_singing_bowl}"
            )
            val meditationChannel = NotificationChannel(
                CHANNEL_MEDITATION_ID,
                context.getString(R.string.channel_meditation_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_meditation_desc)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setSound(zenBowlUri, audioAttributes)
            }

            val ratingChannel = NotificationChannel(
                CHANNEL_RATING_ID,
                "Apresiasi & Rating SoulQuote",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi apresiasi dan ajakan rating aplikasi SoulQuote di Play Store"
            }

            val systemNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            systemNotificationManager.createNotificationChannel(dailyQuoteChannel)
            systemNotificationManager.createNotificationChannel(meditationChannel)
            systemNotificationManager.createNotificationChannel(ratingChannel)
        }
    }

    fun playBellSound() {
        try {
            val player = MediaPlayer.create(context, R.raw.mindful_bell)
            player?.apply {
                setOnCompletionListener { mp ->
                    try { mp.release() } catch (_: Exception) {}
                }
                start()
            }
        } catch (_: Exception) {
        }
    }

    fun playZenBowlSound() {
        try {
            val player = MediaPlayer.create(context, R.raw.zen_singing_bowl)
            player?.apply {
                setOnCompletionListener { mp ->
                    try { mp.release() } catch (_: Exception) {}
                }
                start()
            }
        } catch (_: Exception) {
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            notificationManager.areNotificationsEnabled()
        }
    }

    fun showDailyQuoteNotification(
        quoteId: String,
        quoteText: String,
        author: String
    ) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_QUOTE_ID, quoteId)
            putExtra(EXTRA_NAV_TARGET, "home")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_DAILY_QUOTE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mindfulBellUri = Uri.parse(
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.mindful_bell}"
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_DAILY_QUOTE_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("SoulQuote • Daily Reflection")
            .setContentText("\"$quoteText\" — $author")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("\"$quoteText\"\n\n— $author")
                    .setSummaryText("Daily Inspiration")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(mindfulBellUri)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ID_DAILY_QUOTE, notification)
        } catch (_: SecurityException) {
        }
    }

    fun showMeditationReminder(
        title: String = "SoulQuote • Mindfulness Moment",
        message: String = "Take a moment to pause, breathe, and center yourself."
    ) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAV_TARGET, "meditation")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_MEDITATION,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val zenBowlUri = Uri.parse(
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.zen_singing_bowl}"
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_MEDITATION_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
                    .setSummaryText("Meditation Practice")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(zenBowlUri)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ID_MEDITATION, notification)
        } catch (_: SecurityException) {
        }
    }

    fun showRatingNotification(
        title: String = "SoulQuote • Beri Rating di Play Store",
        message: String = "Bagikan pengalaman damaimu bersama SoulQuote dengan memberikan ulasan di Google Play Store."
    ) {
        if (!hasNotificationPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAV_TARGET, "rate_app")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_RATING,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_RATING_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
                    .setSummaryText("Apresiasi SoulQuote")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ID_RATING, notification)
        } catch (_: SecurityException) {
        }
    }

    fun cancelRatingNotification() {
        try {
            notificationManager.cancel(NOTIFICATION_ID_RATING)
        } catch (_: Exception) {
        }
    }

    companion object {
        const val CHANNEL_DAILY_QUOTE_ID = "channel_daily_quote_v2"
        const val CHANNEL_MEDITATION_ID = "channel_meditation_v2"
        const val CHANNEL_RATING_ID = "channel_rating_v1"

        const val NOTIFICATION_ID_DAILY_QUOTE = 1001
        const val NOTIFICATION_ID_MEDITATION = 1002
        const val NOTIFICATION_ID_RATING = 1003

        const val EXTRA_QUOTE_ID = "extra_quote_id"
        const val EXTRA_NAV_TARGET = "extra_nav_target"
    }
}
