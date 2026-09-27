package com.soulquote.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.soulquote.app.core.di.AppContainer
import com.soulquote.app.core.notification.NotificationHelper
import com.soulquote.app.core.notification.NotificationScheduler
import com.soulquote.app.domain.model.UserSettings
import com.soulquote.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val notificationScheduler: NotificationScheduler,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = userRepository.getUserSettings()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserSettings()
        )

    fun setDailyQuoteEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            val updated = current.copy(dailyQuoteEnabled = enabled)
            userRepository.updateUserSettings(updated)

            if (enabled) {
                notificationScheduler.scheduleDailyQuote(updated.dailyQuoteHour, updated.dailyQuoteMinute)
            } else {
                notificationScheduler.cancelDailyQuote()
            }
        }
    }

    fun setDailyQuoteTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            val current = userSettings.value
            val updated = current.copy(dailyQuoteHour = hour, dailyQuoteMinute = minute)
            userRepository.updateUserSettings(updated)

            if (updated.dailyQuoteEnabled) {
                notificationScheduler.scheduleDailyQuote(hour, minute)
            }
        }
    }

    fun setMeditationReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            val updated = current.copy(meditationReminderEnabled = enabled)
            userRepository.updateUserSettings(updated)

            if (enabled) {
                notificationScheduler.scheduleMeditationReminder(updated.meditationReminderHour, updated.meditationReminderMinute)
            } else {
                notificationScheduler.cancelMeditationReminder()
            }
        }
    }

    fun setMeditationReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            val current = userSettings.value
            val updated = current.copy(meditationReminderHour = hour, meditationReminderMinute = minute)
            userRepository.updateUserSettings(updated)

            if (updated.meditationReminderEnabled) {
                notificationScheduler.scheduleMeditationReminder(hour, minute)
            }
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val current = userSettings.value
            val updated = current.copy(vibrationEnabled = enabled)
            userRepository.updateUserSettings(updated)
        }
    }

    fun triggerTestDailyQuoteNotification() {
        notificationHelper.showDailyQuoteNotification(
            quoteId = "test_quote",
            quoteText = "Kedamaian jiwa dimulai saat pikiran tenang dan hati penuh syukur.",
            author = "Bunda Arsaningsih"
        )
    }

    fun triggerTestMeditationNotification() {
        notificationHelper.showMeditationReminder(
            title = "SoulQuote • Mindfulness Moment",
            message = "Luangkan sejenak waktu untuk bernapas dalam dan menenangkan pikiran."
        )
    }

    fun hasNotificationPermission(): Boolean {
        return notificationHelper.hasNotificationPermission()
    }
}

class SettingsViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                userRepository = appContainer.userRepository,
                notificationScheduler = appContainer.notificationScheduler,
                notificationHelper = appContainer.notificationHelper
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
