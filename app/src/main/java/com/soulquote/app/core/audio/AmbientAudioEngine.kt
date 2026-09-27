package com.soulquote.app.core.audio

import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import androidx.media3.common.AudioAttributes as Media3AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.soulquote.app.domain.model.AmbientPreset
import com.soulquote.app.domain.model.AmbientSound
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AmbientEngineSnapshot(
    val activeTrackVolumes: Map<String, Float> = emptyMap(),
    val isMasterPlaying: Boolean = false,
    val masterVolume: Float = 1.0f,
    val sleepTimerMinutes: Int? = null,
    val remainingSeconds: Int? = null
)

class AmbientAudioEngine(
    private val context: Context
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val engineScope = CoroutineScope(Dispatchers.Main + Job())

    private val players = mutableMapOf<String, ExoPlayer>()
    private val activeTrackVolumes = mutableMapOf<String, Float>()
    private var masterVolume: Float = 1.0f
    private var isMasterPlaying: Boolean = false
    private var isDucked: Boolean = false

    private var sleepTimerJob: Job? = null
    private var currentTimerMinutes: Int? = null
    private var remainingTimerSeconds: Int? = null
    private var originalMasterVolumeBeforeFade: Float = 1.0f

    private val _snapshot = MutableStateFlow(AmbientEngineSnapshot())
    val snapshot: StateFlow<AmbientEngineSnapshot> = _snapshot.asStateFlow()

    private var audioFocusRequest: AudioFocusRequest? = null

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                pauseAll()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                pauseAll()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                isDucked = true
                applyAllVolumes()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                isDucked = false
                applyAllVolumes()
                if (isMasterPlaying && activeTrackVolumes.isNotEmpty()) {
                    resumeAllPlayers()
                }
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val playbackAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(playbackAttributes)
                .setAcceptsDelayedFocusGain(false)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()
            audioFocusRequest = request
            audioManager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        }
    }

    fun toggleSound(sound: AmbientSound, defaultVolume: Float = 0.7f) {
        val soundId = sound.id
        if (activeTrackVolumes.containsKey(soundId)) {
            removeSound(soundId)
        } else {
            addSound(sound, defaultVolume)
        }
        updateSnapshot()
    }

    fun setSoundVolume(sound: AmbientSound, volume: Float) {
        val soundId = sound.id
        if (volume <= 0.01f) {
            if (activeTrackVolumes.containsKey(soundId)) {
                removeSound(soundId)
            }
        } else {
            if (!activeTrackVolumes.containsKey(soundId)) {
                addSound(sound, volume)
            } else {
                activeTrackVolumes[soundId] = volume
                applyTrackVolume(soundId)
            }
        }
        updateSnapshot()
    }

    private fun addSound(sound: AmbientSound, volume: Float) {
        activeTrackVolumes[sound.id] = volume
        val player = getOrCreatePlayer(sound)
        applyTrackVolume(sound.id)

        if (!isMasterPlaying) {
            isMasterPlaying = true
            requestAudioFocus()
        }

        if (isMasterPlaying) {
            player.play()
        }
    }

    private fun removeSound(soundId: String) {
        activeTrackVolumes.remove(soundId)
        players[soundId]?.let { player ->
            player.stop()
            player.release()
            players.remove(soundId)
        }

        if (activeTrackVolumes.isEmpty()) {
            isMasterPlaying = false
            abandonAudioFocus()
            cancelSleepTimer()
        }
    }

    private fun getOrCreatePlayer(sound: AmbientSound): ExoPlayer {
        return players.getOrPut(sound.id) {
            ExoPlayer.Builder(context).build().apply {
                val media3AudioAttributes = Media3AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build()
                setAudioAttributes(media3AudioAttributes, false)
                repeatMode = Player.REPEAT_MODE_ONE

                val uri = Uri.parse(
                    "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${sound.rawResId}"
                )
                setMediaItem(MediaItem.fromUri(uri))
                prepare()
            }
        }
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        applyAllVolumes()
        updateSnapshot()
    }

    fun toggleMasterPlayPause() {
        if (isMasterPlaying) {
            pauseAll()
        } else {
            if (activeTrackVolumes.isEmpty()) {
                // If nothing was selected, start default rain sound
                val defaultSound = AmbientSound.DEFAULT_SOUNDS.first()
                addSound(defaultSound, 0.7f)
            } else {
                resumeAll()
            }
        }
        updateSnapshot()
    }

    fun playAll() {
        if (!isMasterPlaying) {
            if (activeTrackVolumes.isEmpty()) {
                val defaultSound = AmbientSound.DEFAULT_SOUNDS.first()
                addSound(defaultSound, 0.7f)
            } else {
                resumeAll()
            }
            updateSnapshot()
        }
    }

    private fun resumeAll() {
        requestAudioFocus()
        isMasterPlaying = true
        resumeAllPlayers()
    }

    private fun resumeAllPlayers() {
        players.values.forEach { player ->
            if (!player.isPlaying) {
                player.play()
            }
        }
    }

    fun pauseAll() {
        isMasterPlaying = false
        players.values.forEach { it.pause() }
        abandonAudioFocus()
        updateSnapshot()
    }

    fun stopAll() {
        isMasterPlaying = false
        players.values.forEach {
            it.stop()
            it.release()
        }
        players.clear()
        activeTrackVolumes.clear()
        abandonAudioFocus()
        cancelSleepTimer()
        updateSnapshot()
    }

    fun applyPreset(preset: AmbientPreset) {
        stopAll()
        val allSounds = AmbientSound.DEFAULT_SOUNDS.associateBy { it.id }

        preset.soundVolumes.forEach { (soundId, volume) ->
            allSounds[soundId]?.let { sound ->
                addSound(sound, volume)
            }
        }
        isMasterPlaying = true
        requestAudioFocus()
        resumeAllPlayers()
        updateSnapshot()
    }

    private fun applyTrackVolume(soundId: String) {
        val trackVol = activeTrackVolumes[soundId] ?: 0f
        val duckMultiplier = if (isDucked) 0.25f else 1.0f
        val effectiveVol = (trackVol * masterVolume * duckMultiplier).coerceIn(0f, 1f)
        players[soundId]?.volume = effectiveVol
    }

    private fun applyAllVolumes() {
        activeTrackVolumes.keys.forEach { soundId ->
            applyTrackVolume(soundId)
        }
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        if (minutes == null || minutes <= 0) {
            currentTimerMinutes = null
            remainingTimerSeconds = null
            updateSnapshot()
            return
        }

        currentTimerMinutes = minutes
        val totalSecs = minutes * 60
        remainingTimerSeconds = totalSecs
        originalMasterVolumeBeforeFade = masterVolume
        updateSnapshot()

        sleepTimerJob = engineScope.launch {
            var secsLeft = totalSecs
            while (secsLeft > 0) {
                delay(1000)
                secsLeft--
                remainingTimerSeconds = secsLeft

                // In the final 10 seconds, gently fade out
                if (secsLeft in 1..10) {
                    val fadeRatio = secsLeft / 10f
                    masterVolume = (originalMasterVolumeBeforeFade * fadeRatio).coerceIn(0f, 1f)
                    applyAllVolumes()
                }

                updateSnapshot()
            }

            // Timer reached 0: pause all, restore volume
            pauseAll()
            masterVolume = originalMasterVolumeBeforeFade
            applyAllVolumes()
            currentTimerMinutes = null
            remainingTimerSeconds = null
            updateSnapshot()
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        if (currentTimerMinutes != null && masterVolume != originalMasterVolumeBeforeFade) {
            masterVolume = originalMasterVolumeBeforeFade
            applyAllVolumes()
        }
        currentTimerMinutes = null
        remainingTimerSeconds = null
        updateSnapshot()
    }

    private fun updateSnapshot() {
        _snapshot.value = AmbientEngineSnapshot(
            activeTrackVolumes = activeTrackVolumes.toMap(),
            isMasterPlaying = isMasterPlaying,
            masterVolume = masterVolume,
            sleepTimerMinutes = currentTimerMinutes,
            remainingSeconds = remainingTimerSeconds
        )
    }

    fun release() {
        stopAll()
        sleepTimerJob?.cancel()
    }
}
