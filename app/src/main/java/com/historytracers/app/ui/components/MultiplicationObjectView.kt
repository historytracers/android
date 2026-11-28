// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import coil.compose.AsyncImage
import com.historytracers.app.data.UserPreferences
import java.io.File

/**
 * Value stored in [UserPreferences.multiplicationObject] when the user wants the
 * multiplication tables to use their own cropped image instead of an emoji.
 */
const val MULTIPLICATION_CUSTOM_OBJECT_INDEX = 1000

sealed interface MultiplicationObject {
    data class Emoji(val value: String) : MultiplicationObject
    data class CustomImage(val path: String) : MultiplicationObject
}

/**
 * Resolves the object currently configured in Settings. The star (index 0) is
 * the default; when the custom image is selected but its file is missing, the
 * star is used instead.
 */
@Composable
fun rememberMultiplicationObject(): MultiplicationObject {
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val selectedIndex by preferences.multiplicationObject.collectAsState(initial = 0)
    val customPath by preferences.multiplicationCustomImage.collectAsState(initial = "")

    return remember(selectedIndex, customPath) {
        if (selectedIndex == MULTIPLICATION_CUSTOM_OBJECT_INDEX &&
            customPath.isNotBlank() &&
            File(customPath).exists()
        ) {
            MultiplicationObject.CustomImage(customPath)
        } else {
            val index = selectedIndex.coerceIn(0, MultiplicationTableObjects.lastIndex)
            MultiplicationObject.Emoji(MultiplicationTableObjects[index])
        }
    }
}

/**
 * Draws the configured multiplication object. Emojis keep the requested font
 * size; the custom image is drawn at the equivalent dp size so both fit the
 * multiplication group layouts.
 */
@Composable
fun MultiplicationObjectView(
    objectValue: MultiplicationObject,
    emojiSize: TextUnit,
    modifier: Modifier = Modifier
) {
    when (objectValue) {
        is MultiplicationObject.Emoji -> {
            Text(text = objectValue.value, fontSize = emojiSize, modifier = modifier)
        }
        is MultiplicationObject.CustomImage -> {
            val imageSize: Dp = with(LocalDensity.current) { emojiSize.toDp() }
            AsyncImage(
                model = File(objectValue.path),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = modifier.size(imageSize)
            )
        }
    }
}
