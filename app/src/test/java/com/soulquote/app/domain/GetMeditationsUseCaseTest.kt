package com.soulquote.app.domain

import com.soulquote.app.domain.model.Meditation
import com.soulquote.app.domain.model.MeditationCategory
import com.soulquote.app.domain.repository.MeditationRepository
import com.soulquote.app.domain.usecase.GetMeditationsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMeditationsUseCaseTest {

    private val sampleMeditations = listOf(
        Meditation(
            id = "med_1",
            title = "Mindful Breathing",
            description = "Gentle breathing session",
            categoryId = "breathing",
            durationSeconds = 300,
            instructor = "Bunda Arsaningsih",
            audioUrl = "http://example.com/audio1.mp3",
            localFileName = "med_1.mp3",
            checksum = "dummy",
            sizeBytes = 1000
        ),
        Meditation(
            id = "med_2",
            title = "Inner Peace",
            description = "Deep calm meditation",
            categoryId = "peace",
            durationSeconds = 600,
            instructor = "Bunda Arsaningsih",
            audioUrl = "http://example.com/audio2.mp3",
            localFileName = "med_2.mp3",
            checksum = "dummy",
            sizeBytes = 2000
        )
    )

    private val fakeRepo = object : MeditationRepository {
        override fun getAllMeditations(): Flow<List<Meditation>> = flowOf(sampleMeditations)
        override fun getMeditationsByCategory(categoryId: String): Flow<List<Meditation>> =
            flowOf(sampleMeditations.filter { it.categoryId == categoryId })
        override suspend fun getMeditationById(id: String): Meditation? =
            sampleMeditations.find { it.id == id }
        override fun getAllCategories(): Flow<List<MeditationCategory>> = flowOf(emptyList())
    }

    @Test
    fun getMeditations_whenCategoryNullOrAll_returnsAllMeditations() = runBlocking {
        val useCase = GetMeditationsUseCase(fakeRepo)
        val resultAll = useCase("all").first()
        val resultNull = useCase(null).first()

        assertEquals(2, resultAll.size)
        assertEquals(2, resultNull.size)
    }

    @Test
    fun getMeditations_whenCategorySpecified_filtersCorrectly() = runBlocking {
        val useCase = GetMeditationsUseCase(fakeRepo)
        val result = useCase("breathing").first()

        assertEquals(1, result.size)
        assertEquals("med_1", result.first().id)
        assertEquals("Mindful Breathing", result.first().title)
    }
}
