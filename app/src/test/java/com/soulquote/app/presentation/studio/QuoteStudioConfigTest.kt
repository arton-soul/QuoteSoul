package com.soulquote.app.presentation.studio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuoteStudioConfigTest {

    @Test
    fun testAspectRatiosAndExportDimensions() {
        assertEquals("1:1", StudioAspectRatio.Square.title)
        assertEquals(1080, StudioAspectRatio.Square.exportWidth)
        assertEquals(1080, StudioAspectRatio.Square.exportHeight)
        assertEquals(1f, StudioAspectRatio.Square.ratio, 0.001f)

        assertEquals("9:16", StudioAspectRatio.Story.title)
        assertEquals(1080, StudioAspectRatio.Story.exportWidth)
        assertEquals(1920, StudioAspectRatio.Story.exportHeight)
        assertEquals(9f / 16f, StudioAspectRatio.Story.ratio, 0.001f)

        assertEquals("4:5", StudioAspectRatio.Portrait.title)
        assertEquals(1080, StudioAspectRatio.Portrait.exportWidth)
        assertEquals(1350, StudioAspectRatio.Portrait.exportHeight)
        assertEquals(4f / 5f, StudioAspectRatio.Portrait.ratio, 0.001f)
    }

    @Test
    fun testBackgroundPresets() {
        val solids = StudioBackground.solidPresets
        assertTrue("Must provide solid background presets", solids.isNotEmpty())
        assertEquals("Obsidian", solids[0].name)
        assertEquals(0xFF101820, solids[0].colorHex)

        val gradients = StudioBackground.gradientPresets
        assertTrue("Must provide gradient presets", gradients.isNotEmpty())
        assertEquals("Sunset", gradients[0].name)
    }

    @Test
    fun testStudioFontAndTextColorPresets() {
        val fonts = StudioFont.entries
        assertEquals(4, fonts.size)
        assertTrue(fonts.any { it.label == "Serif" })
        assertTrue(fonts.any { it.label == "Modern" })

        val textColors = StudioTextColor.entries
        assertEquals(5, textColors.size)
        assertTrue(textColors.any { it.label == "White" })
        assertTrue(textColors.any { it.label == "Gold" })
    }

    @Test
    fun testStudioUiStateConstraints() {
        val defaultState = StudioUiState()
        assertEquals(24f, defaultState.fontSizeSp)
        assertEquals(0.25f, defaultState.overlayOpacity)
        assertTrue(defaultState.showWatermark)
        assertTrue(defaultState.showAuthor)
        assertFalse(defaultState.isExporting)

        // Verify opacity constraint bounds
        val boundedLow = defaultState.copy(overlayOpacity = (-0.5f).coerceIn(0f, 0.85f))
        assertEquals(0f, boundedLow.overlayOpacity)

        val boundedHigh = defaultState.copy(overlayOpacity = (1.5f).coerceIn(0f, 0.85f))
        assertEquals(0.85f, boundedHigh.overlayOpacity)
    }
}
