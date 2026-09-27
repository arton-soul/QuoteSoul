package com.soulquote.app.core.content

import java.util.regex.Pattern

object GoogleDriveUrlResolver {

    // Common Google Drive URL patterns:
    // https://drive.google.com/file/d/{FILE_ID}/view?usp=sharing
    // https://drive.google.com/file/d/{FILE_ID}/view
    // https://drive.google.com/open?id={FILE_ID}
    // https://drive.google.com/uc?id={FILE_ID}
    // https://drive.google.com/uc?export=download&id={FILE_ID}
    // https://docs.google.com/uc?export=download&id={FILE_ID}
    private val FILE_D_PATTERN = Pattern.compile("https?://drive\\.google\\.com/file/d/([a-zA-Z0-9_-]+)")
    private val OPEN_ID_PATTERN = Pattern.compile("https?://(?:drive|docs)\\.google\\.com/(?:open|uc)\\?[^#]*id=([a-zA-Z0-9_-]+)")

    fun extractFileId(urlOrId: String): String? {
        val trimmed = urlOrId.trim()
        if (trimmed.isEmpty()) return null

        // If it's already a raw Drive File ID (alphanumeric, dashes, underscores, typically 25-45 chars)
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://") && !trimmed.startsWith("asset://")) {
            if (trimmed.matches(Regex("^[a-zA-Z0-9_-]{15,60}$"))) {
                return trimmed
            }
        }

        val fileDMatcher = FILE_D_PATTERN.matcher(trimmed)
        if (fileDMatcher.find()) {
            return fileDMatcher.group(1)
        }

        val openIdMatcher = OPEN_ID_PATTERN.matcher(trimmed)
        if (openIdMatcher.find()) {
            return openIdMatcher.group(1)
        }

        return null
    }

    fun isGoogleDriveUrl(url: String): Boolean {
        val trimmed = url.trim()
        return extractFileId(trimmed) != null
    }

    fun resolveDirectDownloadUrl(urlOrId: String): String {
        val trimmed = urlOrId.trim()
        val fileId = extractFileId(trimmed)
        return if (fileId != null) {
            "https://drive.google.com/uc?export=download&id=$fileId"
        } else {
            trimmed
        }
    }

    fun isQuotaExceededResponse(htmlOrErrorResponse: String): Boolean {
        val lower = htmlOrErrorResponse.lowercase()
        return lower.contains("quota exceeded") ||
                lower.contains("download quota") ||
                lower.contains("too many requests") ||
                lower.contains("kuota unduhan terlampaui") ||
                lower.contains("telah melampaui kuota")
    }

    fun isVirusScanWarning(htmlResponse: String): Boolean {
        val lower = htmlResponse.lowercase()
        return lower.contains("virus scan warning") ||
                lower.contains("google drive can't scan this file for viruses") ||
                lower.contains("confirm=") ||
                lower.contains("uc-download-link")
    }

    fun extractConfirmToken(htmlResponse: String): String? {
        // Find confirm=xxxx in HTML response
        val pattern = Pattern.compile("confirm=([0-9a-zA-Z_-]+)")
        val matcher = pattern.matcher(htmlResponse)
        return if (matcher.find()) {
            matcher.group(1)
        } else {
            null
        }
    }
}
