// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.UriHandler

const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"

/**
 * Resolves a bundled `source_menu` page to a full URL.
 * Relative pages starting with `index.html` are anchored to the
 * HistoryTracers origin; anything else passes through unchanged and must
 * still pass [isSafeExternalUrl] before use.
 */
fun sourceUrl(page: String): String =
    if (page.startsWith("index.html")) HISTORYTRACERS_ORIGIN + page else page

/**
 * Only https URLs are considered safe to open or fetch. HTTP is not used
 * anywhere in this project: any `http:` URL (or `javascript:`, `data:`,
 * `intent:`, `file:`, `content:`, `market:`, `tel:`, plain-text label, ...)
 * is rejected outright — never upgraded, never opened, never fetched.
 */
fun isSafeExternalUrl(url: String): Boolean {
    val trimmed = url.trim()
    if (trimmed.isEmpty()) return false
    val schemeEnd = trimmed.indexOf(':')
    if (schemeEnd <= 0) return false
    val scheme = trimmed.substring(0, schemeEnd).lowercase()
    if (scheme != "https") return false
    val rest = trimmed.substring(schemeEnd + 1)
    return rest.startsWith("//") && rest.length > 2
}

/**
 * Resolves [page] via [sourceUrl] and returns null when the result is not
 * a safe https URL. Callers should hide/disable the Go-to/Copy-URL actions
 * when null is returned.
 */
fun sanitizeSourceUrl(page: String): String? {
    val resolved = sourceUrl(page).trim()
    return if (isSafeExternalUrl(resolved)) resolved else null
}

/**
 * Validated wrapper around [UriHandler.openUri]. Returns true when the
 * URL was handed to the system; returns false (no crash) when the URL is
 * unsafe or no activity can handle it.
 */
fun safeOpenUri(uriHandler: UriHandler, url: String): Boolean {
    val trimmed = url.trim()
    if (!isSafeExternalUrl(trimmed)) return false
    return try {
        uriHandler.openUri(trimmed)
        true
    } catch (_: ActivityNotFoundException) {
        false
    } catch (_: SecurityException) {
        false
    } catch (_: IllegalArgumentException) {
        false
    }
}

/** Copies [url] only when it is a safe https URL. Shows [toastMessage] on success. */
fun safeCopyUrl(context: Context, url: String, toastMessage: String): Boolean {
    val trimmed = url.trim()
    if (!isSafeExternalUrl(trimmed)) return false
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("URL", trimmed))
    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
    return true
}
