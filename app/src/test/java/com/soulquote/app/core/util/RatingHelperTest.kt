package com.soulquote.app.core.util

import com.soulquote.app.domain.model.MeditationHistoryItem
import com.soulquote.app.domain.model.UserSettings
import com.soulquote.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RatingHelperTest {

    private class FakeUserRepository : UserRepository {
        private val settings = mutableMapOf<String, String>()

        override fun getUserSettings(): Flow<UserSettings> = emptyFlow()
        override suspend fun updateUserSettings(settings: UserSettings) {}

        override suspend fun saveSetting(key: String, value: String) {
            settings[key] = value
        }

        override suspend fun getSetting(key: String, defaultValue: String): String {
            return settings[key] ?: defaultValue
        }

        override fun getMeditationHistory(): Flow<List<MeditationHistoryItem>> = emptyFlow()
        override suspend fun recordMeditationHistory(item: MeditationHistoryItem) {}
        override suspend fun getDownloadedAudioIds(): Set<String> = emptySet()
        override suspend fun registerDownloadedAudio(assetId: String, assetType: String, path: String, size: Long, checksum: String) {}
        override suspend fun removeDownloadedAudio(assetId: String) {}
    }

    @Test
    fun checkAndTrackAppLaunch_triggersOnlyOnThirdLaunch() = runBlocking {
        val repo = FakeUserRepository()

        // 1st launch: should not trigger
        val prompt1 = RatingHelper.checkAndTrackAppLaunch(repo)
        assertFalse("Prompt should not show on 1st launch", prompt1)
        assertEquals("1", repo.getSetting(RatingHelper.KEY_APP_LAUNCH_COUNT, "0"))
        assertFalse("rating_prompt_shown should be false", RatingHelper.isRatingPromptShown(repo))

        // 2nd launch: should not trigger
        val prompt2 = RatingHelper.checkAndTrackAppLaunch(repo)
        assertFalse("Prompt should not show on 2nd launch", prompt2)
        assertEquals("2", repo.getSetting(RatingHelper.KEY_APP_LAUNCH_COUNT, "0"))
        assertFalse("rating_prompt_shown should be false", RatingHelper.isRatingPromptShown(repo))

        // 3rd launch: MUST trigger!
        val prompt3 = RatingHelper.checkAndTrackAppLaunch(repo)
        assertTrue("Prompt MUST trigger on 3rd launch", prompt3)
        assertEquals("3", repo.getSetting(RatingHelper.KEY_APP_LAUNCH_COUNT, "0"))
        assertTrue("rating_prompt_shown must now be true", RatingHelper.isRatingPromptShown(repo))

        // 4th launch: must NOT trigger again (single-shot)
        val prompt4 = RatingHelper.checkAndTrackAppLaunch(repo)
        assertFalse("Prompt must NEVER trigger again after being shown", prompt4)
        assertEquals("4", repo.getSetting(RatingHelper.KEY_APP_LAUNCH_COUNT, "0"))
        assertTrue("rating_prompt_shown remains true", RatingHelper.isRatingPromptShown(repo))
    }

    @Test
    fun markRatingPromptShown_suppressesAutomatedPromptOnThirdLaunch() = runBlocking {
        val repo = FakeUserRepository()

        // User opens app once
        RatingHelper.checkAndTrackAppLaunch(repo)
        assertEquals("1", repo.getSetting(RatingHelper.KEY_APP_LAUNCH_COUNT, "0"))

        // User proactively rates the app from Settings menu
        RatingHelper.markRatingPromptShown(repo)
        assertTrue("rating_prompt_shown is now true", RatingHelper.isRatingPromptShown(repo))

        // 2nd launch
        val prompt2 = RatingHelper.checkAndTrackAppLaunch(repo)
        assertFalse("Prompt should not trigger", prompt2)

        // 3rd launch: should be suppressed because user already rated
        val prompt3 = RatingHelper.checkAndTrackAppLaunch(repo)
        assertFalse("Prompt should be suppressed on 3rd launch because user already rated from Settings", prompt3)
        assertEquals("3", repo.getSetting(RatingHelper.KEY_APP_LAUNCH_COUNT, "0"))
    }
}
