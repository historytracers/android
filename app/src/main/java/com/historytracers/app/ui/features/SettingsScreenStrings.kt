// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SettingsScreenStrings(
    val startLearning: String,
)

val EnSettingsScreenStrings = SettingsScreenStrings(
    startLearning = "Start Learning",
)

val PtSettingsScreenStrings = SettingsScreenStrings(
    startLearning = "Come\u00e7ar a Aprender",
)

val EsSettingsScreenStrings = SettingsScreenStrings(
    startLearning = "Empezar a Aprender",
)

val LocalSettingsScreenStrings = staticCompositionLocalOf { EnSettingsScreenStrings }

fun settingsScreenStringsForLanguage(language: String): SettingsScreenStrings = when (language) {
    "pt-BR" -> PtSettingsScreenStrings
    "es-ES" -> EsSettingsScreenStrings
    else -> EnSettingsScreenStrings
}
