package com.soulquote.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.soulquote.app.data.local.entity.user.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    suspend fun getJournalEntriesList(): List<JournalEntryEntity>

    @Query("SELECT * FROM journal_entries WHERE date = :date ORDER BY createdAt DESC LIMIT 1")
    fun getJournalEntryByDate(date: String): Flow<JournalEntryEntity?>

    @Query("SELECT * FROM journal_entries WHERE date = :date ORDER BY createdAt DESC LIMIT 1")
    suspend fun getJournalEntryByDateSync(date: String): JournalEntryEntity?

    @Query("SELECT * FROM journal_entries WHERE id = :id LIMIT 1")
    suspend fun getJournalEntryById(id: String): JournalEntryEntity?

    @Query("SELECT COUNT(*) FROM journal_entries")
    suspend fun countJournalEntries(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntries(entries: List<JournalEntryEntity>)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteJournalEntry(id: String)

    @Query("DELETE FROM journal_entries")
    suspend fun clearJournalEntries()
}
