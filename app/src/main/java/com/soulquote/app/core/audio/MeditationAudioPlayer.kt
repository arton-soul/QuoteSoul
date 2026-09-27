package com.soulquote.app.core.audio

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.soulquote.app.domain.model.Meditation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PlayerPlaybackState(
    val currentMeditationId: String? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val isBuffering: Boolean = false,
    val isCompleted: Boolean = false,
    val errorMessage: String? = null
)

class MeditationAudioPlayer(private val context: Context) {

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private var progressTickerJob: Job? = null

    private var exoPlayer: ExoPlayer? = null

    private val _playbackState = MutableStateFlow(PlayerPlaybackState())
    val playbackState: StateFlow<PlayerPlaybackState> = _playbackState.asStateFlow()

    private var onCompletionCallback: ((String) -> Unit)? = null

    fun setOnCompletionListener(listener: (String) -> Unit) {
        onCompletionCallback = listener
    }

    private fun getOrCreatePlayer(): ExoPlayer {
        return exoPlayer ?: ExoPlayer.Builder(context).build().apply {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
                .setUsage(C.USAGE_MEDIA)
                .build()
            setAudioAttributes(audioAttributes, true) // Automatically handle audio focus & ducking

            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = isPlaying
                    )
                    if (isPlaying) {
                        startProgressTicker()
                    } else {
                        stopProgressTicker()
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> {
                            _playbackState.value = _playbackState.value.copy(isBuffering = true)
                        }
                        Player.STATE_READY -> {
                            val duration = duration.coerceAtLeast(0L)
                            _playbackState.value = _playbackState.value.copy(
                                isBuffering = false,
                                durationMs = duration
                            )
                        }
                        Player.STATE_ENDED -> {
                            _playbackState.value = _playbackState.value.copy(
                                isPlaying = false,
                                isCompleted = true,
                                currentPositionMs = _playbackState.value.durationMs
                            )
                            stopProgressTicker()
                            val id = _playbackState.value.currentMeditationId
                            if (id != null) {
                                onCompletionCallback?.invoke(id)
                            }
                        }
                        Player.STATE_IDLE -> {
                            _playbackState.value = _playbackState.value.copy(isBuffering = false)
                        }
                    }
                }
            })
            exoPlayer = this
        }
    }

    fun play(meditation: Meditation, audioUri: Uri) {
        val player = getOrCreatePlayer()
        val currentId = _playbackState.value.currentMeditationId

        if (currentId == meditation.id && player.playbackState != Player.STATE_ENDED) {
            if (!player.isPlaying) {
                player.play()
            }
            return
        }

        val mediaItem = MediaItem.fromUri(audioUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        _playbackState.value = PlayerPlaybackState(
            currentMeditationId = meditation.id,
            isPlaying = true,
            currentPositionMs = 0L,
            durationMs = (meditation.durationSeconds * 1000L).coerceAtLeast(0L),
            playbackSpeed = player.playbackParameters.speed,
            isBuffering = true,
            isCompleted = false
        )
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun resume() {
        exoPlayer?.play()
    }

    fun togglePlayPause() {
        val player = exoPlayer ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            if (player.playbackState == Player.STATE_ENDED) {
                player.seekTo(0)
            }
            player.play()
        }
    }

    fun seekTo(positionMs: Long) {
        val player = exoPlayer ?: return
        val validPos = positionMs.coerceIn(0L, _playbackState.value.durationMs.coerceAtLeast(positionMs))
        player.seekTo(validPos)
        _playbackState.value = _playbackState.value.copy(currentPositionMs = validPos)
    }

    fun seekRelative(offsetMs: Long) {
        val player = exoPlayer ?: return
        val target = (player.currentPosition + offsetMs).coerceIn(0L, player.duration.coerceAtLeast(0L))
        player.seekTo(target)
        _playbackState.value = _playbackState.value.copy(currentPositionMs = target)
    }

    fun setPlaybackSpeed(speed: Float) {
        val player = exoPlayer ?: return
        player.playbackParameters = PlaybackParameters(speed)
        _playbackState.value = _playbackState.value.copy(playbackSpeed = speed)
    }

    fun stop() {
        stopProgressTicker()
        exoPlayer?.stop()
        _playbackState.value = PlayerPlaybackState()
    }

    fun release() {
        stopProgressTicker()
        exoPlayer?.release()
        exoPlayer = null
        _playbackState.value = PlayerPlaybackState()
    }

    private fun startProgressTicker() {
        stopProgressTicker()
        progressTickerJob = mainScope.launch {
            while (isActive) {
                exoPlayer?.let { player ->
                    if (player.isPlaying) {
                        _playbackState.value = _playbackState.value.copy(
                            currentPositionMs = player.currentPosition.coerceAtLeast(0L),
                            durationMs = player.duration.coerceAtLeast(_playbackState.value.durationMs)
                        )
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTicker() {
        progressTickerJob?.cancel()
        progressTickerJob = null
    }
}
