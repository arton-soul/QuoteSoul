package com.soulquote.app.data.local.entity.content

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val previewAsset: String? = null,
    val backgroundType: String,
    val backgroundValue: String,
    val fontName: String,
    val fontSizeSp: Int,
    val textColorHex: String,
    val alignment: String,
    val overlayType: String? = null,
    val overlayOpacity: Float = 0f,
    val active: Boolean = true,
    val version: Int = 1
)

@Entity(tableName = "app_content_config")
data class AppConfigEntity(
    @PrimaryKey
    val key: String,
    val value: String,
    val updatedAt: Long = System.currentTimeMillis()
)
