package com.soulquote.app.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.soulquote.app.domain.model.JournalEntry
import com.soulquote.app.domain.model.MindfulStreakInfo
import com.soulquote.app.domain.model.MoodType
import com.soulquote.app.domain.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class JournalViewModel(
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val todayDateString = LocalDate.now().toString()

    val streakInfo: StateFlow<MindfulStreakInfo> = journalRepository.getMindfulStreakInfo()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MindfulStreakInfo(
                currentStreak = 0,
                longestStreak = 0,
                totalMindfulDays = 0,
                totalSessions = 0,
                totalMeditationMinutes = 0,
                totalJournalCount = 0,
                practicedToday = false,
                badges = emptyList()
            )
        )

    val todayEntry: StateFlow<JournalEntry?> = journalRepository.getJournalEntryByDate(todayDateString)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val recentEntries: StateFlow<List<JournalEntry>> = journalRepository.getAllJournalEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedMood = MutableStateFlow<MoodType>(MoodType.CALM)
    val selectedMood: StateFlow<MoodType> = _selectedMood.asStateFlow()

    private val _reflectionText = MutableStateFlow("")
    val reflectionText: StateFlow<String> = _reflectionText.asStateFlow()

    private val _currentPrompt = MutableStateFlow(determineDefaultPrompt())
    val currentPrompt: StateFlow<String> = _currentPrompt.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun selectMood(mood: MoodType) {
        _selectedMood.value = mood
    }

    fun onReflectionTextChanged(text: String) {
        _reflectionText.value = text
    }

    fun refreshPrompt() {
        val prompts = listOf(
            "Apa yang paling kamu syukuri saat fajar menyingsing hari ini?",
            "Hal apa yang membuat jiwamu merasa tenang dan damai hari ini?",
            "Pelajaran berharga apa yang ingin kamu bawa dari hening hari ini?",
            "Apa satu niat baik yang ingin kamu pancarkan kepada sesama hari ini?",
            "Lepaskan apa yang tidak bisa kamu kendalikan, apa yang ingin kamu ikhlaskan malam ini?"
        )
        _currentPrompt.value = prompts.random()
    }

    fun saveTodayEntry(quoteId: String? = null) {
        val content = _reflectionText.value.trim()
        if (content.isBlank()) return

        viewModelScope.launch {
            val existing = todayEntry.value
            val entry = JournalEntry(
                id = existing?.id ?: UUID.randomUUID().toString(),
                date = todayDateString,
                mood = _selectedMood.value,
                reflectionPrompt = _currentPrompt.value,
                content = content,
                quoteId = quoteId ?: existing?.quoteId,
                createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            journalRepository.saveJournalEntry(entry)
            _statusMessage.value = "Refleksi mindful berhasil tersimpan di perangkat lokal."
            _reflectionText.value = ""
        }
    }

    fun deleteEntry(id: String) {
        viewModelScope.launch {
            journalRepository.deleteJournalEntry(id)
            _statusMessage.value = "Catatan refleksi dihapus."
        }
    }

    fun dismissStatusMessage() {
        _statusMessage.value = null
    }

    companion object {
        fun determineDefaultPrompt(): String {
            val hour = LocalTime.now().hour
            return when (hour) {
                in 5..11 -> "Apa satu niat baik dan berkah yang ingin kamu bawa di awal hari ini?"
                in 12..16 -> "Apa yang membuat jiwamu merasa tenang di tengah dinamika siang ini?"
                in 17..21 -> "Hal damai apa yang paling kamu syukuri dari perjalanan harimu?"
                else -> "Ikhlaskan yang telah lalu, keheningan apa yang ingin kamu resapi malam ini?"
            }
        }

        fun provideFactory(
            journalRepository: JournalRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return JournalViewModel(journalRepository) as T
            }
        }
    }
}
