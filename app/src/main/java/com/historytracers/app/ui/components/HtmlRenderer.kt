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

private fun wrapHtml(body: String): String =
    """
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
    <body>$body</body>
    </html>
    """.trimIndent()

@Composable
fun HtmlRenderer(html: String, modifier: Modifier = Modifier) {
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
                    wrapHtml(html),
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
