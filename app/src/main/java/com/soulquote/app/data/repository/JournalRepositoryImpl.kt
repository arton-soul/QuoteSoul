package com.soulquote.app.data.repository

import com.soulquote.app.data.local.dao.JournalDao
import com.soulquote.app.data.local.dao.MeditationHistoryDao
import com.soulquote.app.data.local.entity.user.JournalEntryEntity
import com.soulquote.app.domain.model.JournalEntry
import com.soulquote.app.domain.model.MindfulBadge
import com.soulquote.app.domain.model.MindfulStreakInfo
import com.soulquote.app.domain.model.MoodType
import com.soulquote.app.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class JournalRepositoryImpl(
    private val journalDao: JournalDao,
    private val meditationHistoryDao: MeditationHistoryDao
) : JournalRepository {

    override fun getAllJournalEntries(): Flow<List<JournalEntry>> {
        return journalDao.getAllJournalEntries().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getJournalEntryByDate(date: String): Flow<JournalEntry?> {
        return journalDao.getJournalEntryByDate(date).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun saveJournalEntry(entry: JournalEntry) {
        journalDao.insertJournalEntry(entry.toEntity())
    }

    override suspend fun deleteJournalEntry(id: String) {
        journalDao.deleteJournalEntry(id)
    }

    override fun getMindfulStreakInfo(): Flow<MindfulStreakInfo> {
        return combine(
            journalDao.getAllJournalEntries(),
            meditationHistoryDao.getHistory()
        ) { journals, meditations ->
            val zoneId = ZoneId.systemDefault()
            val journalEpochDays = journals.map {
                Instant.ofEpochMilli(it.createdAt).atZone(zoneId).toLocalDate().toEpochDay()
            }
            val meditationEpochDays = meditations.map {
                Instant.ofEpochMilli(it.completedAt).atZone(zoneId).toLocalDate().toEpochDay()
            }

            val uniqueDays = (journalEpochDays + meditationEpochDays).toSet()
            val todayEpochDay = LocalDate.now().toEpochDay()
            val practicedToday = uniqueDays.contains(todayEpochDay)

            // Current streak calculation
            var currentStreak = 0
            var checkDay = if (practicedToday) todayEpochDay else todayEpochDay - 1
            while (uniqueDays.contains(checkDay)) {
                currentStreak++
                checkDay--
            }

            // Longest streak calculation
            var longestStreak = 0
            if (uniqueDays.isNotEmpty()) {
                val sortedDays = uniqueDays.sorted()
                var currentRun = 1
                longestStreak = 1
                for (i in 1 until sortedDays.size) {
                    if (sortedDays[i] == sortedDays[i - 1] + 1) {
                        currentRun++
                        if (currentRun > longestStreak) {
                            longestStreak = currentRun
                        }
                    } else {
                        currentRun = 1
                    }
                }
            }

            val totalMeditationSeconds = meditations.sumOf { it.durationListenedSeconds }
            val totalMeditationMinutes = totalMeditationSeconds / 60
            val totalSessions = meditations.size
            val totalJournalCount = journals.size
            val totalMindfulDays = uniqueDays.size

            val badges = listOf(
                MindfulBadge(
                    id = "first_step",
                    title = "Langkah Pertama",
                    description = "Menyelesaikan 1 refleksi atau meditasi pertama",
                    iconEmoji = "🌱",
                    isUnlocked = totalMindfulDays >= 1,
                    currentProgress = totalMindfulDays.coerceAtMost(1),
                    targetProgress = 1
                ),
                MindfulBadge(
                    id = "streak_3",
                    title = "Fokus 3 Hari",
                    description = "Melakukan rutinitas hening 3 hari berturut-turut",
                    iconEmoji = "🔥",
                    isUnlocked = longestStreak >= 3,
                    currentProgress = longestStreak.coerceAtMost(3),
                    targetProgress = 3
                ),
                MindfulBadge(
                    id = "streak_7",
                    title = "Seminggu Damai",
                    description = "Melakukan rutinitas hening 7 hari berturut-turut",
                    iconEmoji = "✨",
                    isUnlocked = longestStreak >= 7,
                    currentProgress = longestStreak.coerceAtMost(7),
                    targetProgress = 7
                ),
                MindfulBadge(
                    id = "streak_14",
                    title = "Kedamaian Batin",
                    description = "Konsistensi mindfulness selama 14 hari penuh",
                    iconEmoji = "🌸",
                    isUnlocked = longestStreak >= 14,
                    currentProgress = longestStreak.coerceAtMost(14),
                    targetProgress = 14
                ),
                MindfulBadge(
                    id = "streak_30",
                    title = "Zen Master",
                    description = "Mencapai mindfulness konsisten 30 hari berturut-turut",
                    iconEmoji = "🏆",
                    isUnlocked = longestStreak >= 30,
                    currentProgress = longestStreak.coerceAtMost(30),
                    targetProgress = 30
                ),
                MindfulBadge(
                    id = "meditator_hour",
                    title = "Penyelam Batin",
                    description = "Akumulasi meditasi hening lebih dari 60 menit",
                    iconEmoji = "🧘",
                    isUnlocked = totalMeditationMinutes >= 60,
                    currentProgress = totalMeditationMinutes.coerceAtMost(60),
                    targetProgress = 60
                )
            )

            MindfulStreakInfo(
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                totalMindfulDays = totalMindfulDays,
                totalSessions = totalSessions,
                totalMeditationMinutes = totalMeditationMinutes,
                totalJournalCount = totalJournalCount,
                practicedToday = practicedToday,
                badges = badges
            )
        }
    }

    private fun JournalEntryEntity.toDomain() = JournalEntry(
        id = id,
        date = date,
        mood = MoodType.fromString(mood),
        reflectionPrompt = reflectionPrompt,
        content = content,
        quoteId = quoteId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun JournalEntry.toEntity() = JournalEntryEntity(
        id = id,
        date = date,
        mood = mood.name,
        reflectionPrompt = reflectionPrompt,
        content = content,
        quoteId = quoteId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
