package com.soulquote.app.domain.usecase

import com.soulquote.app.domain.model.Meditation
import com.soulquote.app.domain.repository.MeditationRepository
import kotlinx.coroutines.flow.Flow

class GetMeditationsUseCase(
    private val meditationRepository: MeditationRepository
) {
    operator fun invoke(categoryId: String? = null): Flow<List<Meditation>> {
        return if (categoryId.isNullOrBlank() || categoryId == "all") {
            meditationRepository.getAllMeditations()
        } else {
            meditationRepository.getMeditationsByCategory(categoryId)
        }
    }
}
