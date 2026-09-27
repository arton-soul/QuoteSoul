package com.soulquote.app.presentation.meditation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.soulquote.app.core.audio.MeditationAudioPlayer
import com.soulquote.app.core.audio.MeditationDownloadManager
import com.soulquote.app.core.di.AppContainer
import com.soulquote.app.domain.model.Meditation
import com.soulquote.app.domain.repository.MeditationRepository
import com.soulquote.app.domain.repository.UserRepository
import com.soulquote.app.domain.usecase.RecordMeditationSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MeditationViewModel(
    private val meditationRepository: MeditationRepository,
    private val userRepository: UserRepository,
    private val audioPlayer: MeditationAudioPlayer,
    private val downloadManager: MeditationDownloadManager,
    private val recordSessionUseCase: RecordMeditationSessionUseCase
) : ViewModel() {

    private val _catalogUiState = MutableStateFlow(MeditationCatalogUiState(isLoading = true))
    val catalogUiState: StateFlow<MeditationCatalogUiState> = _catalogUiState.asStateFlow()

    private val _playerUiState = MutableStateFlow(MeditationPlayerUiState())
    val playerUiState: StateFlow<MeditationPlayerUiState> = _playerUiState.asStateFlow()

    init {
        loadData()
        observePlayerState()
        observeDownloadStates()
        setupCompletionListener()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                meditationRepository.getAllCategories(),
                meditationRepository.getAllMeditations()
            ) { categories, meditations ->
                val downloadedIds = userRepository.getDownloadedAudioIds()
                val updatedMeditations = meditations.map { med ->
                    val isLocallyCached = downloadManager.isDownloadedLocally(med)
                    med.copy(isDownloaded = isLocallyCached || downloadedIds.contains(med.id))
                }
                Pair(categories, updatedMeditations)
            }.collect { (categories, meditations) ->
                _catalogUiState.update { state ->
                    state.copy(
                        categories = categories,
                        meditations = meditations,
                        isLoading = false
                    )
                }

                // Also update current playing meditation if already loaded
                val currentId = _playerUiState.value.currentMeditation?.id
                if (currentId != null) {
                    val updatedCurrent = meditations.find { it.id == currentId }
                    if (updatedCurrent != null) {
                        _playerUiState.update { it.copy(currentMeditation = updatedCurrent) }
                    }
                }
            }
        }
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            audioPlayer.playbackState.collect { playback ->
                _playerUiState.update { current ->
                    current.copy(
                        isPlaying = playback.isPlaying,
                        currentPositionMs = playback.currentPositionMs,
                        durationMs = if (playback.durationMs > 0) playback.durationMs else current.durationMs,
                        playbackSpeed = playback.playbackSpeed,
                        isBuffering = playback.isBuffering,
                        isCompleted = playback.isCompleted
                    )
                }
            }
        }
    }

    private fun observeDownloadStates() {
        viewModelScope.launch {
            downloadManager.downloadStates.collect { states ->
                val currentMed = _playerUiState.value.currentMeditation
                if (currentMed != null) {
                    val progress = states[currentMed.id]
                    if (progress != null) {
                        _playerUiState.update {
                            it.copy(
                                isDownloading = progress.isDownloading,
                                downloadProgress = progress.progress
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setupCompletionListener() {
        audioPlayer.setOnCompletionListener { meditationId ->
            viewModelScope.launch {
                val med = _catalogUiState.value.meditations.find { it.id == meditationId }
                val duration = med?.durationSeconds ?: 300
                recordSessionUseCase(
                    meditationId = meditationId,
                    durationSeconds = duration,
                    completed = true
                )
                _catalogUiState.update { it.copy(statusMessage = "Meditasi selesai. Selamat menikmati ketenangan jiwa 🙏") }
            }
        }
    }

    fun selectCategory(categoryId: String) {
        _catalogUiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun setSearchQuery(query: String) {
        _catalogUiState.update { it.copy(searchQuery = query) }
    }

    fun playMeditation(meditation: Meditation) {
        val audioUri = downloadManager.getAudioUri(meditation)
        val isLocallyDownloaded = downloadManager.isDownloadedLocally(meditation)

        _playerUiState.update {
            it.copy(
                currentMeditation = meditation,
                isDownloaded = isLocallyDownloaded,
                durationMs = meditation.durationSeconds * 1000L,
                currentPositionMs = 0L,
                isCompleted = false,
                isPlayerExpanded = true
            )
        }

        audioPlayer.play(meditation, audioUri)
    }

    fun togglePlayPause() {
        audioPlayer.togglePlayPause()
    }

    fun seekTo(positionMs: Long) {
        audioPlayer.seekTo(positionMs)
    }

    fun seekRelative(offsetMs: Long) {
        audioPlayer.seekRelative(offsetMs)
    }

    fun setPlaybackSpeed(speed: Float) {
        audioPlayer.setPlaybackSpeed(speed)
    }

    fun expandPlayer() {
        _playerUiState.update { it.copy(isPlayerExpanded = true) }
    }

    fun collapsePlayer() {
        _playerUiState.update { it.copy(isPlayerExpanded = false) }
    }

    fun stopPlayback() {
        audioPlayer.stop()
        _playerUiState.update { it.copy(currentMeditation = null, isPlayerExpanded = false) }
    }

    fun downloadAudio(meditation: Meditation) {
        viewModelScope.launch {
            _catalogUiState.update { it.copy(statusMessage = "Mengunduh audio meditasi...") }
            val result = downloadManager.downloadMeditation(meditation)
            result.fold(
                onSuccess = {
                    _catalogUiState.update { it.copy(statusMessage = "Audio berhasil disimpan untuk offline!") }
                    _playerUiState.update { it.copy(isDownloaded = true) }
                    refreshMeditations()
                },
                onFailure = { err ->
                    _catalogUiState.update { it.copy(statusMessage = "Gagal mengunduh: ${err.localizedMessage}") }
                }
            )
        }
    }

    fun deleteDownload(meditation: Meditation) {
        viewModelScope.launch {
            val deleted = downloadManager.deleteMeditationAudio(meditation)
            if (deleted) {
                _catalogUiState.update { it.copy(statusMessage = "Audio offline berhasil dihapus.") }
                _playerUiState.update { it.copy(isDownloaded = false) }
                refreshMeditations()
            }
        }
    }

    private fun refreshMeditations() {
        viewModelScope.launch {
            val list = _catalogUiState.value.meditations.map { med ->
                med.copy(isDownloaded = downloadManager.isDownloadedLocally(med))
            }
            _catalogUiState.update { it.copy(meditations = list) }
        }
    }

    fun clearStatusMessage() {
        _catalogUiState.update { it.copy(statusMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}

class MeditationViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeditationViewModel::class.java)) {
            val recordUseCase = RecordMeditationSessionUseCase(appContainer.userRepository)
            return MeditationViewModel(
                meditationRepository = appContainer.meditationRepository,
                userRepository = appContainer.userRepository,
                audioPlayer = appContainer.meditationAudioPlayer,
                downloadManager = appContainer.meditationDownloadManager,
                recordSessionUseCase = recordUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
