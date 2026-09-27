package com.soulquote.app.core.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.soulquote.app.R
import com.soulquote.app.SoulQuoteApp
import com.soulquote.app.domain.model.Quote
import com.soulquote.app.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailyQuoteWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId, pickRandom = false)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val appWidgetManager = AppWidgetManager.getInstance(context)

        when (intent.action) {
            ACTION_REFRESH_WIDGET -> {
                val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
                if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    updateWidget(context, appWidgetManager, widgetId, pickRandom = true)
                } else {
                    val allIds = appWidgetManager.getAppWidgetIds(
                        ComponentName(context, DailyQuoteWidgetProvider::class.java)
                    )
                    for (id in allIds) {
                        updateWidget(context, appWidgetManager, id, pickRandom = true)
                    }
                }
            }
            ACTION_SYNC_WIDGET -> {
                val allIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, DailyQuoteWidgetProvider::class.java)
                )
                for (id in allIds) {
                    updateWidget(context, appWidgetManager, id, pickRandom = false)
                }
            }
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        pickRandom: Boolean
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appContainer = (context.applicationContext as? SoulQuoteApp)?.appContainer
                val quoteRepository = appContainer?.quoteRepository

                val todayEpochDay = java.time.LocalDate.now().toEpochDay()
                val quote: Quote? = if (pickRandom) {
                    quoteRepository?.getRandomQuote()
                } else {
                    quoteRepository?.getDailyQuote(todayEpochDay) ?: quoteRepository?.getRandomQuote()
                }

                val views = RemoteViews(context.packageName, R.layout.widget_daily_quote)

                if (quote != null) {
                    views.setTextViewText(R.id.tv_widget_quote_text, "\"${quote.text}\"")
                    views.setTextViewText(R.id.tv_widget_author, "— ${quote.author}")
                    views.setTextViewText(R.id.tv_widget_tag, quote.categoryId.replaceFirstChar { it.uppercase() })

                    // Click entire widget to open quote in app via deep link
                    val clickIntent = Intent(Intent.ACTION_VIEW, Uri.parse("soulquote://quote/${quote.id}")).apply {
                        setClass(context, MainActivity::class.java)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    val clickPendingIntent = PendingIntent.getActivity(
                        context,
                        appWidgetId,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_root, clickPendingIntent)
                } else {
                    views.setTextViewText(R.id.tv_widget_quote_text, "\"Heningkan pikiran, temukan kedamaian dalam dirimu.\"")
                    views.setTextViewText(R.id.tv_widget_author, "— SoulQuote")
                    views.setTextViewText(R.id.tv_widget_tag, "Mindful")
                }

                // Click refresh button to cycle to next quote
                val refreshIntent = Intent(context, DailyQuoteWidgetProvider::class.java).apply {
                    action = ACTION_REFRESH_WIDGET
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                }
                val refreshPendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId + 1000,
                    refreshIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.btn_widget_refresh, refreshPendingIntent)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                // Fallback graceful handling
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.soulquote.app.ACTION_REFRESH_WIDGET"
        const val ACTION_SYNC_WIDGET = "com.soulquote.app.ACTION_SYNC_WIDGET"

        fun notifyWidgetUpdate(context: Context) {
            val intent = Intent(context, DailyQuoteWidgetProvider::class.java).apply {
                action = ACTION_SYNC_WIDGET
            }
            context.sendBroadcast(intent)
        }
    }
}
