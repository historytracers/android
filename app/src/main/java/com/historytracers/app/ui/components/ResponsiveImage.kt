// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.historytracers.app.ui.LocalUiStrings

private val IMG_TAG_REGEX = Regex("""<img[^>]*src\s*=\s*"([^"]*)"[^>]*/?>""")

@Composable
fun ResponsiveImage(html: String, imgDesc: String?, modifier: Modifier = Modifier) {
    val s = LocalUiStrings.current
    val url = IMG_TAG_REGEX.find(html)?.groupValues?.get(1) ?: return
    val configuration = LocalConfiguration.current
    val maxHeight = with(LocalDensity.current) { (configuration.screenHeightDp * 0.4f).dp }
    SubcomposeAsyncImage(
        model = url,
        contentDescription = imgDesc,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight)
            .padding(vertical = 8.dp),
        contentScale = ContentScale.Fit,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 96.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 96.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = s.common.imageOfflineMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}
