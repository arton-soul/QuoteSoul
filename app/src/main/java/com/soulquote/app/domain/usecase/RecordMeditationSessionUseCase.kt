package com.soulquote.app.domain.usecase

import com.soulquote.app.domain.model.MeditationHistoryItem
import com.soulquote.app.domain.repository.UserRepository

class RecordMeditationSessionUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        meditationId: String,
        durationSeconds: Int,
        completed: Boolean = true
    ) {
        val item = MeditationHistoryItem(
            id = java.util.UUID.randomUUID().toString(),
            meditationId = meditationId,
            completedAt = System.currentTimeMillis(),
            durationListenedSeconds = durationSeconds,
            completed = completed
        )
        userRepository.recordMeditationHistory(item)
    }
}
