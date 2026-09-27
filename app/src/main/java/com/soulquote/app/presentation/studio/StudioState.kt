package com.soulquote.app.presentation.studio

import android.net.Uri
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign

enum class StudioAspectRatio(
    val title: String,
    val ratio: Float,
    val exportWidth: Int,
    val exportHeight: Int
) {
    Square("1:1", 1f, 1080, 1080),
    Story("9:16", 9f / 16f, 1080, 1920),
    Portrait("4:5", 4f / 5f, 1080, 1350)
}

sealed class StudioBackground {
    data class Solid(val colorHex: Long, val name: String) : StudioBackground()
    data class Gradient(val startColorHex: Long, val endColorHex: Long, val name: String) : StudioBackground()
    data class CustomImage(val uri: Uri) : StudioBackground()

    companion object {
        val solidPresets = listOf(
            Solid(0xFF101820, "Obsidian"),
            Solid(0xFF0F172A, "Midnight"),
            Solid(0xFF1B3B2B, "Forest"),
            Solid(0xFF2D1B36, "Plum"),
            Solid(0xFF3B2A1A, "Warm Earth"),
            Solid(0xFF334155, "Slate"),
            Solid(0xFFF8F6F0, "Ivory")
        )

        val gradientPresets = listOf(
            Gradient(0xFF2C3E50, 0xFFFD746C, "Sunset"),
            Gradient(0xFF0F2027, 0xFF2C5364, "Deep Ocean"),
            Gradient(0xFF134E5E, 0xFF71B280, "Aurora"),
            Gradient(0xFF2E0854, 0xFF8A2387, "Mystic"),
            Gradient(0xFF3E5151, 0xFFDECBA4, "Desert Sand")
        )
    }
}

enum class StudioFont(val label: String, val composeFont: FontFamily, val androidTypefaceName: String) {
    Serif("Serif", FontFamily.Serif, "serif"),
    SansSerif("Modern", FontFamily.SansSerif, "sans-serif"),
    Cursive("Script", FontFamily.Cursive, "cursive"),
    Monospace("Monospace", FontFamily.Monospace, "monospace")
}

enum class StudioTextColor(val label: String, val colorHex: Long) {
    White("White", 0xFFFFFFFF),
    Black("Black", 0xFF121212),
    Gold("Gold", 0xFFFFD700),
    Cream("Cream", 0xFFFFF8E7),
    PastelMint("Mint", 0xFFA7F3D0)
}

data class StudioUiState(
    val quoteId: String? = null,
    val quoteText: String = "Kedamaian jiwa dimulai saat pikiran tenang dan hati penuh syukur.",
    val author: String = "Bunda Arsaningsih",
    val aspectRatio: StudioAspectRatio = StudioAspectRatio.Square,
    val background: StudioBackground = StudioBackground.solidPresets.first(),
    val font: StudioFont = StudioFont.Serif,
    val fontSizeSp: Float = 24f,
    val textAlign: TextAlign = TextAlign.Center,
    val textColor: StudioTextColor = StudioTextColor.White,
    val overlayOpacity: Float = 0.25f,
    val showWatermark: Boolean = true,
    val showAuthor: Boolean = true,
    val isExporting: Boolean = false,
    val exportStatusMessage: String? = null
)