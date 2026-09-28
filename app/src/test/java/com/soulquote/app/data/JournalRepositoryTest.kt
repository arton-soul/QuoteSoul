package com.soulquote.app.data

import com.soulquote.app.data.local.dao.JournalDao
import com.soulquote.app.data.local.dao.MeditationHistoryDao
import com.soulquote.app.data.local.entity.user.JournalEntryEntity
import com.soulquote.app.data.local.entity.user.MeditationHistoryEntity
import com.soulquote.app.data.repository.JournalRepositoryImpl
import com.soulquote.app.domain.model.JournalEntry
import com.soulquote.app.domain.model.MoodType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class JournalRepositoryTest {

    private lateinit var fakeJournalDao: FakeJournalDao
    private lateinit var fakeMeditationHistoryDao: FakeMeditationHistoryDao
    private lateinit var repository: JournalRepositoryImpl

    @Before
    fun setUp() {
        fakeJournalDao = FakeJournalDao()
        fakeMeditationHistoryDao = FakeMeditationHistoryDao()
        repository = JournalRepositoryImpl(fakeJournalDao, fakeMeditationHistoryDao)
    }

    @Test
    fun testSaveAndGetJournalEntry() = runBlocking {
        val today = LocalDate.now().toString()
        val entry = JournalEntry(
            id = "entry_1",
            date = today,
            mood = MoodType.PEACEFUL,
            reflectionPrompt = "Apa yang kamu syukuri?",
            content = "Hening pagi yang menyegarkan jiwa."
        )

        repository.saveJournalEntry(entry)

        val retrieved = repository.getJournalEntryByDate(today).first()
        assertNotNull(retrieved)
        assertEquals("entry_1", retrieved?.id)
        assertEquals(MoodType.PEACEFUL, retrieved?.mood)
        assertEquals("Hening pagi yang menyegarkan jiwa.", retrieved?.content)

        val all = repository.getAllJournalEntries().first()
        assertEquals(1, all.size)
    }

    @Test
    fun testDeleteJournalEntry() = runBlocking {
        val entry = JournalEntry(
            id = "entry_del",
            date = "2026-09-27",
            mood = MoodType.CALM,
            reflectionPrompt = "Prompt",
            content = "Content"
        )
        repository.saveJournalEntry(entry)
        assertEquals(1, repository.getAllJournalEntries().first().size)

        repository.deleteJournalEntry("entry_del")
        assertEquals(0, repository.getAllJournalEntries().first().size)
    }

    @Test
    fun testMultipleJournalEntriesOnSameDay() = runBlocking {
        val today = "2026-09-28"
        val entry1 = JournalEntry(
            id = "entry_1",
            date = today,
            mood = MoodType.CALM,
            reflectionPrompt = "Prompt 1",
            content = "First reflection of the day"
        )
        val entry2 = JournalEntry(
            id = "entry_2",
            date = today,
            mood = MoodType.GRATEFUL,
            reflectionPrompt = "Prompt 2",
            content = "Second reflection of the day"
        )
        repository.saveJournalEntry(entry1)
        repository.saveJournalEntry(entry2)

        val todayEntries = repository.getJournalEntriesByDate(today).first()
        assertEquals(2, todayEntries.size)

        val allEntries = repository.getAllJournalEntries().first()
        assertEquals(2, allEntries.size)
    }

    @Test
    fun testStreakAndBadgesCalculation() = runBlocking {
        val today = LocalDate.now()
        val todayMillis = System.currentTimeMillis()

        // Add 1 meditation session today (30 mins = 1800s)
        fakeMeditationHistoryDao.recordHistory(
            MeditationHistoryEntity(
                id = "med_1",
                meditationId = "meditation_calm",
                completedAt = todayMillis,
                durationListenedSeconds = 1800,
                completed = true
            )
        )

        // Add 1 journal entry today
        repository.saveJournalEntry(
            JournalEntry(
                id = "j_today",
                date = today.toString(),
                mood = MoodType.GRATEFUL,
                reflectionPrompt = "Syukur",
                content = "Refleksi damai hari ini",
                createdAt = todayMillis
            )
        )

        val streakInfo = repository.getMindfulStreakInfo().first()

        assertTrue(streakInfo.practicedToday)
        assertEquals(1, streakInfo.currentStreak)
        assertEquals(1, streakInfo.longestStreak)
        assertEquals(30, streakInfo.totalMeditationMinutes)
        assertEquals(1, streakInfo.totalJournalCount)

        // Check first step badge unlocked
        val firstStepBadge = streakInfo.badges.find { it.id == "first_step" }
        assertNotNull(firstStepBadge)
        assertTrue(firstStepBadge?.isUnlocked == true)
    }

    private class FakeJournalDao : JournalDao {
        private val entriesFlow = MutableStateFlow<List<JournalEntryEntity>>(emptyList())

        override fun getAllJournalEntries(): Flow<List<JournalEntryEntity>> = entriesFlow

        override fun getJournalEntriesByDate(date: String): Flow<List<JournalEntryEntity>> {
            return entriesFlow.map { list -> list.filter { it.date == date } }
        }

        override suspend fun getJournalEntriesList(): List<JournalEntryEntity> = entriesFlow.value

        override fun getJournalEntryByDate(date: String): Flow<JournalEntryEntity?> {
            return entriesFlow.map { list -> list.find { it.date == date } }
        }

        override suspend fun getJournalEntryByDateSync(date: String): JournalEntryEntity? {
            return entriesFlow.value.find { it.date == date }
        }

        override suspend fun getJournalEntryById(id: String): JournalEntryEntity? {
            return entriesFlow.value.find { it.id == id }
        }

        override suspend fun countJournalEntries(): Int = entriesFlow.value.size

        override suspend fun insertJournalEntry(entry: JournalEntryEntity) {
            val current = entriesFlow.value.toMutableList()
            current.removeAll { it.id == entry.id }
            current.add(0, entry)
            entriesFlow.value = current
        }

        override suspend fun insertJournalEntries(entries: List<JournalEntryEntity>) {
            val current = entriesFlow.value.toMutableList()
            for (e in entries) {
                current.removeAll { it.id == e.id }
                current.add(e)
            }
            entriesFlow.value = current
        }

        override suspend fun deleteJournalEntry(id: String) {
            val current = entriesFlow.value.toMutableList()
            current.removeAll { it.id == id }
            entriesFlow.value = current
        }

        override suspend fun clearJournalEntries() {
            entriesFlow.value = emptyList()
        }
    }

    private class FakeMeditationHistoryDao : MeditationHistoryDao {
        private val historyFlow = MutableStateFlow<List<MeditationHistoryEntity>>(emptyList())

        override fun getHistory(): Flow<List<MeditationHistoryEntity>> = historyFlow

        override suspend fun getAllHistoryList(): List<MeditationHistoryEntity> = historyFlow.value

        override suspend fun recordHistory(item: MeditationHistoryEntity) {
            val current = historyFlow.value.toMutableList()
            current.add(0, item)
            historyFlow.value = current
        }

        override suspend fun insertHistoryList(items: List<MeditationHistoryEntity>) {
            val current = historyFlow.value.toMutableList()
            current.addAll(items)
            historyFlow.value = current
        }

        override suspend fun getCompletedSessionCount(): Int {
            return historyFlow.value.count { it.completed }
        }

        override suspend fun clearHistory() {
            historyFlow.value = emptyList()
        }
    }
}
