package com.soulquote.app.domain.repository

import com.soulquote.app.domain.model.JournalEntry
import com.soulquote.app.domain.model.MindfulStreakInfo
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun getAllJournalEntries(): Flow<List<JournalEntry>>
    fun getJournalEntriesByDate(date: String): Flow<List<JournalEntry>>
    fun getJournalEntryByDate(date: String): Flow<JournalEntry?>
    suspend fun saveJournalEntry(entry: JournalEntry)
    suspend fun deleteJournalEntry(id: String)
    fun getMindfulStreakInfo(): Flow<MindfulStreakInfo>
}
