// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// The original History Tracers software always draws on a white background, so the
// app keeps the light colour scheme even when the device is in dark mode.
private val LightColorScheme = lightColorScheme(
    primary = Brown40,
    secondary = Brown60,
    background = LightBackground
)

@Composable
fun HistoryTracersTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
