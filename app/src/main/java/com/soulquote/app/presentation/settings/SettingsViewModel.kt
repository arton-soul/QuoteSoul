package com.soulquote.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.soulquote.app.core.content.ContentStats
import com.soulquote.app.core.content.ContentUpdateManager
import com.soulquote.app.core.content.UpdateCheckResult
import com.soulquote.app.core.content.UpdateProgress
import com.soulquote.app.core.di.AppContainer
import com.soulquote.app.core.notification.NotificationHelper
import com.soulquote.app.core.notification.NotificationScheduler
import com.soulquote.app.data.remote.model.ContentManifest
import com.soulquote.app.domain.model.UserSettings
import com.soulquote.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContentUpdateUiState(
    val currentVersion: Int = 3,
    val totalQuotes: Int = 72,
    val totalMeditations: Int = 4,
    val isChecking: Boolean = false,
    val updateAvailable: ContentManifest? = null,
    val isUpdating: Boolean = false,
    val updateProgress: Float = 0f,
    val updateStepDescription: String? = null,
    val statusMessage: String? = null,
    val isUpToDate: Boolean = false
)

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val notificationScheduler: NotificationScheduler,
    private val notificationHelper: NotificationHelper,
    private val contentUpdateManager: ContentUpdateManager
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = userRepository.getUserSettings()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserSettings()
        )

    private val _contentUpdateUiState = MutableStateFlow(ContentUpdateUiState())
    val contentUpdateUiState: StateFlow<ContentUpdateUiState> = _contentUpdateUiState.asStateFlow()

    init {
        refreshContentStats()
    }

    fun refreshContentStats() {
        viewModelScope.launch {
            val stats = contentUpdateManager.getContentStats()
            _contentUpdateUiState.update {
                it.copy(
                    currentVersion = stats.contentVersion,
                    totalQuotes = stats.totalQuotes,
                    totalMeditations = stats.totalMeditations
                )
            }
        }
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            _contentUpdateUiState.update {
                it.copy(
                    isChecking = true,
                    statusMessage = null,
                    isUpToDate = false,
                    updateAvailable = null
                )
            }
            when (val result = contentUpdateManager.checkForUpdates()) {
                is UpdateCheckResult.UpdateAvailable -> {
                    _contentUpdateUiState.update {
                        it.copy(
                            isChecking = false,
                            updateAvailable = result.manifest,
                            statusMessage = "Pembaruan v${result.manifest.contentVersion} tersedia!"
                        )
                    }
                }
                is UpdateCheckResult.UpToDate -> {
                    _contentUpdateUiState.update {
                        it.copy(
                            isChecking = false,
                            isUpToDate = true,
                            statusMessage = "Konten sudah versi terbaru (v${result.currentVersion})"
                        )
                    }
                }
                is UpdateCheckResult.AppUpdateRequired -> {
                    _contentUpdateUiState.update {
                        it.copy(
                            isChecking = false,
                            statusMessage = "Pembaruan aplikasi diperlukan (versi minimal ${result.minAppVersion})"
                        )
                    }
                }
                is UpdateCheckResult.Error -> {
                    _contentUpdateUiState.update {
                        it.copy(
                            isChecking = false,
                            statusMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun applyContentUpdate() {
        val manifest = _contentUpdateUiState.value.updateAvailable ?: return
        viewModelScope.launch {
            _contentUpdateUiState.update {
                it.copy(
                    isUpdating = true,
                    updateProgress = 0f,
                    updateStepDescription = "Memulai pengunduhan paket...",
                    statusMessage = null
                )
            }
            contentUpdateManager.applyUpdate(manifest).collect { progress ->
                when (progress) {
                    is UpdateProgress.Downloading -> {
                        _contentUpdateUiState.update {
                            it.copy(
                                updateProgress = progress.progress,
                                updateStepDescription = "Mengunduh paket konten (${(progress.progress * 100).toInt()}%)..."
                            )
                        }
                    }
                    is UpdateProgress.VerifyingChecksum -> {
                        _contentUpdateUiState.update {
                            it.copy(
                                updateStepDescription = "Memverifikasi integritas checksum SHA-256..."
                            )
                        }
                    }
                    is UpdateProgress.Validating -> {
                        _contentUpdateUiState.update {
                            it.copy(
                                updateStepDescription = "Memvalidasi struktur paket data..."
                            )
                        }
                    }
                    is UpdateProgress.ApplyingDatabaseTransaction -> {
                        _contentUpdateUiState.update {
                            it.copy(
                                updateStepDescription = "Menyimpan ke basis data lokal (Atomic Transaction)..."
                            )
                        }
                    }
                    is UpdateProgress.Success -> {
                        _contentUpdateUiState.update {
                            it.copy(
                                isUpdating = false,
                                updateAvailable = null,
                                isUpToDate = true,
                                currentVersion = progress.newVersion,
                                totalQuotes = progress.totalQuotes,
                                totalMeditations = progress.totalMeditations,
                                updateStepDescription = null,
                                statusMessage = "Pembaruan v${progress.newVersion} berhasil diterapkan! Total ${progress.totalQuotes} kutipan tersedia."
                            )
                        }
                    }
                    is UpdateProgress.Failed -> {
                        _contentUpdateUiState.update {
                            it.copy(
                                isUpdating = false,
                                updateStepDescription = null,
                                statusMessage = progress.errorMessage
                            )
                        }
                    }
                    is UpdateProgress.Idle, is UpdateProgress.Checking -> {}
                }
            }
        }
    }

    fun dismissStatusMessage() {
        _contentUpdateUiState.update { it.copy(statusMessage = null) }
    }

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
                notificationHelper = appContainer.notificationHelper,
                contentUpdateManager = appContainer.contentUpdateManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
