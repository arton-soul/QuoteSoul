package com.soulquote.app.domain.model

data class MindfulBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean,
    val currentProgress: Int,
    val targetProgress: Int
)

data class MindfulStreakInfo(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalMindfulDays: Int,
    val totalSessions: Int,
    val totalMeditationMinutes: Int,
    val totalJournalCount: Int,
    val practicedToday: Boolean,
    val badges: List<MindfulBadge>
)
