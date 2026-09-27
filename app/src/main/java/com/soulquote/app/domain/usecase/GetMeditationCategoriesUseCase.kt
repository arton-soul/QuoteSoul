package com.soulquote.app.domain.usecase

import com.soulquote.app.domain.model.MeditationCategory
import com.soulquote.app.domain.repository.MeditationRepository
import kotlinx.coroutines.flow.Flow

class GetMeditationCategoriesUseCase(
    private val meditationRepository: MeditationRepository
) {
    operator fun invoke(): Flow<List<MeditationCategory>> {
        return meditationRepository.getAllCategories()
    }
}
