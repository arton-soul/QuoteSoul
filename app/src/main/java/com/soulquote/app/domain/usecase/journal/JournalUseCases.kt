package com.soulquote.app.domain.usecase.journal

import com.soulquote.app.domain.model.JournalEntry
import com.soulquote.app.domain.model.MindfulStreakInfo
import com.soulquote.app.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetJournalEntriesUseCase(
    private val journalRepository: JournalRepository
) {
    operator fun invoke(): Flow<List<JournalEntry>> = journalRepository.getAllJournalEntries()
}

class GetTodayJournalEntryUseCase(
    private val journalRepository: JournalRepository
) {
    operator fun invoke(date: String = LocalDate.now().toString()): Flow<JournalEntry?> {
        return journalRepository.getJournalEntryByDate(date)
    }
}

class SaveJournalEntryUseCase(
    private val journalRepository: JournalRepository
) {
    suspend operator fun invoke(entry: JournalEntry) {
        journalRepository.saveJournalEntry(entry)
    }
}

class DeleteJournalEntryUseCase(
    private val journalRepository: JournalRepository
) {
    suspend operator fun invoke(id: String) {
        journalRepository.deleteJournalEntry(id)
    }
}

class GetMindfulStreakUseCase(
    private val journalRepository: JournalRepository
) {
    operator fun invoke(): Flow<MindfulStreakInfo> = journalRepository.getMindfulStreakInfo()
}
