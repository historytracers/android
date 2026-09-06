// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HtmlRenderer(html: String, modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val maxHeight = with(LocalDensity.current) { (configuration.screenHeightDp * 0.5f).dp }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                loadDataWithBaseURL(
                    null,
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
                    <body>$html</body>
                    </html>
                    """.trimIndent(),
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                null,
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
                <body>$html</body>
                </html>
                """.trimIndent(),
                "text/html",
                "UTF-8",
                null
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight)
    )
}
