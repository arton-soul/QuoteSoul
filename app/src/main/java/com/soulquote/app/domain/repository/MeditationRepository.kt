package com.soulquote.app.domain.repository

import com.soulquote.app.domain.model.Meditation
import com.soulquote.app.domain.model.MeditationCategory
import kotlinx.coroutines.flow.Flow

interface MeditationRepository {
    fun getAllMeditations(): Flow<List<Meditation>>
    fun getMeditationsByCategory(categoryId: String): Flow<List<Meditation>>
    suspend fun getMeditationById(id: String): Meditation?
    fun getAllCategories(): Flow<List<MeditationCategory>>
}
