package com.soulquote.app.presentation.ambient

import com.soulquote.app.domain.model.AmbientPreset
import com.soulquote.app.domain.model.AmbientSound

data class AmbientTrackUiModel(
    val sound: AmbientSound,
    val isPlaying: Boolean = false,
    val volume: Float = 0.7f
)

data class AmbientMixerUiState(
    val tracks: List<AmbientTrackUiModel> = emptyList(),
    val presets: List<AmbientPreset> = emptyList(),
    val masterVolume: Float = 1.0f,
    val isMasterPlaying: Boolean = false,
    val activeCount: Int = 0,
    val sleepTimerMinutes: Int? = null,
    val remainingSeconds: Int? = null
) {
    val formattedRemainingTime: String
        get() {
            val secs = remainingSeconds ?: return ""
            val m = secs / 60
            val s = secs % 60
            return "%02d:%02d".format(m, s)
        }
}
