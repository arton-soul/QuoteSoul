package com.soulquote.app.domain

import com.soulquote.app.domain.model.AmbientPreset
import com.soulquote.app.domain.model.AmbientSound
import com.soulquote.app.presentation.ambient.AmbientMixerUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AmbientSoundTest {

    @Test
    fun defaultSounds_containsExpectedLibraryAndUniqueIds() {
        val sounds = AmbientSound.DEFAULT_SOUNDS
        assertEquals(7, sounds.size)

        val ids = sounds.map { it.id }.toSet()
        assertEquals("All ambient sounds must have unique IDs", sounds.size, ids.size)

        sounds.forEach { sound ->
            assertTrue("Sound name must not be blank", sound.name.isNotBlank())
            assertTrue("Sound description must not be blank", sound.description.isNotBlank())
            assertTrue("Sound raw resource ID must be valid", sound.rawResId != 0)
        }
    }

    @Test
    fun defaultPresets_referenceValidSoundsAndVolumes() {
        val presets = AmbientPreset.DEFAULT_PRESETS
        assertTrue(presets.isNotEmpty())

        val soundIds = AmbientSound.DEFAULT_SOUNDS.map { it.id }.toSet()

        presets.forEach { preset ->
            assertTrue("Preset name must not be blank", preset.name.isNotBlank())
            assertTrue("Preset sound volumes must not be empty", preset.soundVolumes.isNotEmpty())

            preset.soundVolumes.forEach { (soundId, volume) ->
                assertTrue("Preset references known sound ID: $soundId", soundIds.contains(soundId))
                assertTrue("Volume must be between 0 and 1: $volume", volume in 0f..1f)
            }
        }
    }

    @Test
    fun ambientMixerUiState_formattedRemainingTime_formatsCorrectly() {
        val state1 = AmbientMixerUiState(remainingSeconds = 90)
        assertEquals("01:30", state1.formattedRemainingTime)

        val state2 = AmbientMixerUiState(remainingSeconds = 1205)
        assertEquals("20:05", state2.formattedRemainingTime)

        val state3 = AmbientMixerUiState(remainingSeconds = null)
        assertEquals("", state3.formattedRemainingTime)
    }
}
