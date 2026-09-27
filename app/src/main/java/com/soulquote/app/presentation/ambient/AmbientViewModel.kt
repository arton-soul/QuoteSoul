package com.soulquote.app.presentation.ambient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.soulquote.app.core.audio.AmbientAudioEngine
import com.soulquote.app.core.di.AppContainer
import com.soulquote.app.domain.model.AmbientPreset
import com.soulquote.app.domain.model.AmbientSound
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AmbientViewModel(
    private val ambientAudioEngine: AmbientAudioEngine
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    val uiState: StateFlow<AmbientMixerUiState> = ambientAudioEngine.snapshot
        .map { snapshot ->
            val trackUiModels = AmbientSound.DEFAULT_SOUNDS.map { sound ->
                val activeVol = snapshot.activeTrackVolumes[sound.id]
                AmbientTrackUiModel(
                    sound = sound,
                    isPlaying = activeVol != null && snapshot.isMasterPlaying,
                    volume = activeVol ?: 0.7f
                )
            }
            AmbientMixerUiState(
                tracks = trackUiModels,
                presets = AmbientPreset.DEFAULT_PRESETS,
                masterVolume = snapshot.masterVolume,
                isMasterPlaying = snapshot.isMasterPlaying,
                activeCount = snapshot.activeTrackVolumes.size,
                sleepTimerMinutes = snapshot.sleepTimerMinutes,
                remainingSeconds = snapshot.remainingSeconds
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AmbientMixerUiState(
                tracks = AmbientSound.DEFAULT_SOUNDS.map { AmbientTrackUiModel(sound = it) },
                presets = AmbientPreset.DEFAULT_PRESETS
            )
        )

    fun toggleSound(sound: AmbientSound) {
        ambientAudioEngine.toggleSound(sound)
    }

    fun setSoundVolume(sound: AmbientSound, volume: Float) {
        ambientAudioEngine.setSoundVolume(sound, volume)
    }

    fun toggleMasterPlayPause() {
        ambientAudioEngine.toggleMasterPlayPause()
    }

    fun setMasterVolume(volume: Float) {
        ambientAudioEngine.setMasterVolume(volume)
    }

    fun stopAll() {
        ambientAudioEngine.stopAll()
    }

    fun applyPreset(preset: AmbientPreset) {
        ambientAudioEngine.applyPreset(preset)
    }

    fun setSleepTimer(minutes: Int?) {
        ambientAudioEngine.setSleepTimer(minutes)
    }

    fun cancelSleepTimer() {
        ambientAudioEngine.cancelSleepTimer()
    }
}

class AmbientViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AmbientViewModel::class.java)) {
            return AmbientViewModel(
                ambientAudioEngine = appContainer.ambientAudioEngine
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
