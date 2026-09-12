// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlin.math.roundToInt

private val SVG_FONT_SIZE_REGEX = Regex("""font-size="(\d+(?:\.\d+)?)"""")

private val SVG_BOTTOM_LABEL_REGEX = Regex("""(<text\b[^>]*\by="275"[^>]*font-size=")\d+(")""")

private const val SVG_FONT_SCALE = 1.5f

private const val SVG_BOTTOM_LABEL_FONT_SIZE = 24

private fun enlargeSvgFontSizes(html: String): String {
    val scaled = SVG_FONT_SIZE_REGEX.replace(html) { match ->
        val size = match.groupValues[1].toFloatOrNull() ?: return@replace match.value
        "font-size=\"${(size * SVG_FONT_SCALE).roundToInt()}\""
    }
    return SVG_BOTTOM_LABEL_REGEX.replace(scaled) { match ->
        match.groupValues[1] + SVG_BOTTOM_LABEL_FONT_SIZE + match.groupValues[2]
    }
}

private fun wrapHtml(body: String, scaleSvgText: Boolean): String {
    val content = if (scaleSvgText) enlargeSvgFontSizes(body) else body
    return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
            body {
                margin: 0;
                padding: 8px;
                display: flex;
                justify-content: center;
                align-items: center;
            }
            svg {
                max-width: 100%;
                height: auto;
            }
            .desc {
                text-align: center;
                margin: 4px 0;
            }
            b {
                font-size: 14px;
            }
        </style>
    </head>
    <body>$content</body>
    </html>
    """.trimIndent()
}

@Composable
fun HtmlRenderer(html: String, modifier: Modifier = Modifier, scaleSvgText: Boolean = false) {
    val configuration = LocalConfiguration.current
    val maxHeight = with(LocalDensity.current) { (configuration.screenHeightDp * 0.5f).dp }
    var lastLoadedHtml by remember { mutableStateOf("") }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = false
                settings.domStorageEnabled = false
                settings.allowFileAccess = false
            }
        },
        update = { webView ->
            if (html != lastLoadedHtml) {
                lastLoadedHtml = html
                webView.loadDataWithBaseURL(
                    null,
                    wrapHtml(html, scaleSvgText),
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight)
    )
}
