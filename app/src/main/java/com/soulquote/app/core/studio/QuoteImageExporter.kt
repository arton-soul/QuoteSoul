package com.soulquote.app.core.studio

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.FileProvider
import com.soulquote.app.presentation.studio.StudioBackground
import com.soulquote.app.presentation.studio.StudioFont
import com.soulquote.app.presentation.studio.StudioUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

object QuoteImageExporter {

    suspend fun renderBitmap(context: Context, state: StudioUiState): Bitmap = withContext(Dispatchers.IO) {
        val width = state.aspectRatio.exportWidth
        val height = state.aspectRatio.exportHeight

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Draw Background
        when (val bg = state.background) {
            is StudioBackground.Solid -> {
                canvas.drawColor(bg.colorHex.toInt())
            }
            is StudioBackground.Gradient -> {
                val shader = LinearGradient(
                    0f, 0f, 0f, height.toFloat(),
                    bg.startColorHex.toInt(), bg.endColorHex.toInt(),
                    Shader.TileMode.CLAMP
                )
                val paint = Paint().apply { this.shader = shader }
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
            }
            is StudioBackground.CustomImage -> {
                try {
                    context.contentResolver.openInputStream(bg.uri)?.use { stream ->
                        val decoded = BitmapFactory.decodeStream(stream)
                        if (decoded != null) {
                            drawCenterCropped(canvas, decoded, width, height)
                        } else {
                            canvas.drawColor(Color.DKGRAY)
                        }
                    }
                } catch (_: Exception) {
                    canvas.drawColor(Color.DKGRAY)
                }
            }
        }

        // 2. Dimmer overlay
        if (state.overlayOpacity > 0f) {
            val overlayAlpha = (state.overlayOpacity.coerceIn(0f, 1f) * 255).toInt()
            val overlayPaint = Paint().apply {
                color = Color.BLACK
                alpha = overlayAlpha
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)
        }

        // 3. Configure Typeface & Paints
        val textColorInt = state.textColor.colorHex.toInt()
        val typeface = when (state.font) {
            StudioFont.Serif -> Typeface.SERIF
            StudioFont.SansSerif -> Typeface.SANS_SERIF
            StudioFont.Cursive -> Typeface.create("cursive", Typeface.NORMAL)
            StudioFont.Monospace -> Typeface.MONOSPACE
        }

        // Scaling factor based on standard 360dp mobile width
        val scale = width / 360f
        val quoteTextSize = state.fontSizeSp * scale * 0.95f
        val horizontalMargin = (width * 0.10f).toInt()
        val maxTextWidth = width - (horizontalMargin * 2)

        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColorInt
            textSize = quoteTextSize
            this.typeface = typeface
            isAntiAlias = true
            setShadowLayer(8f * (scale / 3f), 2f, 2f, Color.argb(128, 0, 0, 0))
        }

        val alignment = when (state.textAlign) {
            TextAlign.Left, TextAlign.Start -> Layout.Alignment.ALIGN_NORMAL
            TextAlign.Right, TextAlign.End -> Layout.Alignment.ALIGN_OPPOSITE
            else -> Layout.Alignment.ALIGN_CENTER
        }

        val formattedQuote = "\u201C${state.quoteText}\u201D"
        val quoteLayout = StaticLayout.Builder.obtain(
            formattedQuote, 0, formattedQuote.length, textPaint, maxTextWidth
        ).setAlignment(alignment)
            .setLineSpacing(0f, 1.25f)
            .setIncludePad(true)
            .build()

        val authorPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColorInt
            textSize = quoteTextSize * 0.52f
            this.typeface = typeface
            alpha = 220
            setShadowLayer(6f * (scale / 3f), 2f, 2f, Color.argb(100, 0, 0, 0))
        }

        val authorText = if (state.showAuthor && state.author.isNotBlank()) "— ${state.author}" else ""
        val authorLayout = if (authorText.isNotBlank()) {
            StaticLayout.Builder.obtain(
                authorText, 0, authorText.length, authorPaint, maxTextWidth
            ).setAlignment(alignment)
                .build()
        } else null

        val totalContentHeight = quoteLayout.height + (authorLayout?.height?.plus((24 * scale).toInt()) ?: 0)
        val startY = max((height - totalContentHeight) / 2f, height * 0.15f)

        // 4. Draw Quote Text
        canvas.save()
        canvas.translate(horizontalMargin.toFloat(), startY)
        quoteLayout.draw(canvas)
        canvas.restore()

        // 5. Draw Author Text
        if (authorLayout != null) {
            canvas.save()
            val authorY = startY + quoteLayout.height + (24 * scale)
            canvas.translate(horizontalMargin.toFloat(), authorY)
            authorLayout.draw(canvas)
            canvas.restore()
        }

        // 6. Watermark at Bottom
        if (state.showWatermark) {
            val watermarkPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = textColorInt
                textSize = 14f * scale
                this.typeface = Typeface.SANS_SERIF
                alpha = 160
                setShadowLayer(4f, 1f, 1f, Color.argb(80, 0, 0, 0))
            }
            val watermarkText = "SoulQuote"
            val watermarkWidth = watermarkPaint.measureText(watermarkText)
            val watermarkX = (width - watermarkWidth) / 2f
            val watermarkY = height - (32 * scale)
            canvas.drawText(watermarkText, watermarkX, watermarkY, watermarkPaint)
        }

        bitmap
    }

    private fun drawCenterCropped(canvas: Canvas, bitmap: Bitmap, targetWidth: Int, targetHeight: Int) {
        val srcWidth = bitmap.width.toFloat()
        val srcHeight = bitmap.height.toFloat()

        val scale = max(targetWidth / srcWidth, targetHeight / srcHeight)
        val scaledWidth = srcWidth * scale
        val scaledHeight = srcHeight * scale

        val left = (targetWidth - scaledWidth) / 2f
        val top = (targetHeight - scaledHeight) / 2f

        val destRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
        val srcRect = Rect(0, 0, bitmap.width, bitmap.height)
        canvas.drawBitmap(bitmap, srcRect, destRect, Paint(Paint.FILTER_BITMAP_FLAG))
    }

    suspend fun saveToGallery(context: Context, state: StudioUiState): Uri? = withContext(Dispatchers.IO) {
        val bitmap = renderBitmap(context, state)
        val filename = "SoulQuote_${System.currentTimeMillis()}.png"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/SoulQuote")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { destinationUri ->
            resolver.openOutputStream(destinationUri)?.use { outStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(destinationUri, contentValues, null, null)
            }
        }
        uri
    }

    suspend fun createShareableImageFile(context: Context, state: StudioUiState): Uri = withContext(Dispatchers.IO) {
        val bitmap = renderBitmap(context, state)
        val sharedDir = File(context.cacheDir, "shared_quotes").apply { mkdirs() }
        val imageFile = File(sharedDir, "quote_${System.currentTimeMillis()}.png")

        FileOutputStream(imageFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }

    suspend fun shareQuoteImage(context: Context, state: StudioUiState) {
        val uri = createShareableImageFile(context, state)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "SoulQuote Inspiration")
            putExtra(Intent.EXTRA_TEXT, "“${state.quoteText}” — ${state.author}\n\nCreated with SoulQuote Studio")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "Share Quote Image").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}