// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SunTzuLearningScreenStrings(
    val title: String,
)

val EnSunTzuLearningScreenStrings = SunTzuLearningScreenStrings(
    title = "Sun Tzu and Learning",
)

val PtSunTzuLearningScreenStrings = SunTzuLearningScreenStrings(
    title = "Sun Tzu e a aprendizagem",
)

val EsSunTzuLearningScreenStrings = SunTzuLearningScreenStrings(
    title = "Sun Tzu y el aprendizaje",
)

val LocalSunTzuLearningScreenStrings = staticCompositionLocalOf { EnSunTzuLearningScreenStrings }

fun sunTzuLearningScreenStringsForLanguage(language: String): SunTzuLearningScreenStrings = when (language) {
    "pt-BR" -> PtSunTzuLearningScreenStrings
    "es-ES" -> EsSunTzuLearningScreenStrings
    else -> EnSunTzuLearningScreenStrings
}
