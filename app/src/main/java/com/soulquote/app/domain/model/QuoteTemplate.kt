package com.soulquote.app.domain.model

data class QuoteTemplate(
    val id: String,
    val name: String,
    val previewAsset: String?,
    val backgroundType: String,
    val backgroundValue: String,
    val fontName: String,
    val fontSizeSp: Int,
    val textColorHex: String,
    val alignment: String,
    val overlayType: String?,
    val overlayOpacity: Float = 0f,
    val isActive: Boolean = true,
    val version: Int = 1
)
