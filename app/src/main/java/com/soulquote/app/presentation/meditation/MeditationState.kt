package com.soulquote.app.presentation.meditation

import com.soulquote.app.domain.model.Meditation
import com.soulquote.app.domain.model.MeditationCategory

data class MeditationCatalogUiState(
    val categories: List<MeditationCategory> = emptyList(),
    val selectedCategoryId: String = "all",
    val meditations: List<Meditation> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val statusMessage: String? = null
) {
    val filteredMeditations: List<Meditation>
        get() {
            var list = if (selectedCategoryId == "all") {
                meditations
            } else {
                meditations.filter { it.categoryId == selectedCategoryId }
            }
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.trim().lowercase()
                list = list.filter {
                    it.title.lowercase().contains(q) ||
                    (it.description?.lowercase()?.contains(q) == true) ||
                    (it.instructor?.lowercase()?.contains(q) == true)
                }
            }
            return list
        }
}

data class MeditationPlayerUiState(
    val currentMeditation: Meditation? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val isBuffering: Boolean = false,
    val isCompleted: Boolean = false,
    val isDownloaded: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val isPlayerExpanded: Boolean = false
) {
    val progressFraction: Float
        get() = if (durationMs > 0L) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    val formattedCurrentTime: String
        get() = formatTime(currentPositionMs)

    val formattedDurationTime: String
        get() = formatTime(durationMs)

    private fun formatTime(ms: Long): String {
        val totalSecs = (ms / 1000).coerceAtLeast(0)
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        return "%02d:%02d".format(mins, secs)
    }
}
