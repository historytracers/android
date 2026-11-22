// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.AutoSizeTitleText
import com.historytracers.app.ui.components.ScreenAudioButton
import com.historytracers.app.ui.features.hubTitleStringsForLanguage

private const val NEXT_LEVEL_AUDIO_LESSON_ID = "4d9e6b2a-1f3c-4c7e-9a51-8b6f2d0e7a13"
private const val NEXT_LEVEL_AUDIO_CONTENT_ID = "b7c2e4f1-6a90-4d38-8f52-3e1a9c7d6b24"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CongratulationScreen(
    onNavigateBack: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.common.back)
                }
                AutoSizeTitleText(
                    text = hts.congratulationTitle,
                    modifier = Modifier.padding(start = 8.dp).weight(1f)
                )
                ScreenAudioButton(
                    lessonId = NEXT_LEVEL_AUDIO_LESSON_ID,
                    contentId = NEXT_LEVEL_AUDIO_CONTENT_ID
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = hts.congratulationMessage,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}
