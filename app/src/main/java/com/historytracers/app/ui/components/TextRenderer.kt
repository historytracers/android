// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.ContentRepository
import com.historytracers.common.HTText

@Composable
fun TextRenderer(text: HTText, repo: ContentRepository) {
    val displayText = text.text ?: return

    Text(
        text = displayText,
        style = MaterialTheme.typography.bodyLarge
    )

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
