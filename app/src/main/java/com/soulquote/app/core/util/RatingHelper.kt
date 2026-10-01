package com.soulquote.app.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.soulquote.app.domain.repository.UserRepository

object RatingHelper {

    const val KEY_APP_LAUNCH_COUNT = "app_launch_count"
    const val KEY_RATING_PROMPT_SHOWN = "rating_prompt_shown"
    const val RATING_PROMPT_TRIGGER_COUNT = 3

    /**
     * Increments the launch count and returns true if the rating prompt
     * should be shown (i.e. launch count reaches [RATING_PROMPT_TRIGGER_COUNT]
     * and has never been shown before).
     */
    suspend fun checkAndTrackAppLaunch(userRepository: UserRepository): Boolean {
        val countStr = userRepository.getSetting(KEY_APP_LAUNCH_COUNT, "0")
        val currentCount = countStr.toIntOrNull() ?: 0
        val newCount = currentCount + 1
        userRepository.saveSetting(KEY_APP_LAUNCH_COUNT, newCount.toString())

        val alreadyShown = userRepository.getSetting(KEY_RATING_PROMPT_SHOWN, "false") == "true"
        if (newCount >= RATING_PROMPT_TRIGGER_COUNT && !alreadyShown) {
            userRepository.saveSetting(KEY_RATING_PROMPT_SHOWN, "true")
            return true
        }
        return false
    }

    /**
     * Marks the rating prompt as shown so that it will never be displayed automatically again.
     */
    suspend fun markRatingPromptShown(userRepository: UserRepository) {
        userRepository.saveSetting(KEY_RATING_PROMPT_SHOWN, "true")
    }

    /**
     * Checks if the rating prompt has already been shown to the user.
     */
    suspend fun isRatingPromptShown(userRepository: UserRepository): Boolean {
        return userRepository.getSetting(KEY_RATING_PROMPT_SHOWN, "false") == "true"
    }

    /**
     * Launches the Google Play Store page for SoulQuote to allow the user to rate and review the app.
     * Gracefully falls back to opening the web browser if the Play Store app is not installed.
     */
    fun launchPlayStoreRating(context: Context) {
        val packageName = context.packageName
        try {
            val marketIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
            context.startActivity(marketIntent)
        } catch (_: Exception) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }
}
