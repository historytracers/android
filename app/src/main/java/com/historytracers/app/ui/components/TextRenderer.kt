// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.historytracers.app.data.ContentRepository
import com.historytracers.common.HTText

private val IMG_TAG_REGEX = Regex("""<img[^>]*src\s*=\s*"([^"]*)"[^>]*/?>""")

@Composable
fun TextRenderer(text: HTText, repo: ContentRepository) {
    val displayText = text.text ?: return

    HtmlWithImages(displayText, text.imgdesc)

    text.source?.forEach { source ->
        repo.getSource(source.uuid)?.let { elem ->
            Spacer(Modifier.height(4.dp))
            Text(
                text = "[${source.uuid}] ${elem.citation ?: ""}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun HtmlWithImages(text: String, imgDesc: String?) {
    var index = 0
    for (match in IMG_TAG_REGEX.findAll(text)) {
        if (match.range.first > index) {
            Text(
                text = text.substring(index, match.range.first),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        AsyncImage(
            model = match.groupValues[1],
            contentDescription = imgDesc,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 480.dp)
                .padding(vertical = 8.dp),
            contentScale = ContentScale.Fit
        )
        index = match.range.last + 1
    }
    if (index < text.length) {
        Text(
            text = text.substring(index),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
