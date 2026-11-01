// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SunTzuSubtractionScreenStrings(
    val title: String,
    val practiceExercise: String,
    val terrainSquaresCaption: String,
)

val EnSunTzuSubtractionScreenStrings = SunTzuSubtractionScreenStrings(
    title = "Sun Tzu and Subtraction",
    practiceExercise = "Practice Exercise",
    terrainSquaresCaption = "15 - 8 = 7",
)

val PtSunTzuSubtractionScreenStrings = SunTzuSubtractionScreenStrings(
    title = "Sun Tzu e a Subtra\u00e7\u00e3o",
    practiceExercise = "Exerc\u00edcio de Pr\u00e1tica",
    terrainSquaresCaption = "15 - 8 = 7",
)

val EsSunTzuSubtractionScreenStrings = SunTzuSubtractionScreenStrings(
    title = "Sun Tzu y la Resta",
    practiceExercise = "Ejercicio de Pr\u00e1ctica",
    terrainSquaresCaption = "15 - 8 = 7",
)

val LocalSunTzuSubtractionScreenStrings = staticCompositionLocalOf { EnSunTzuSubtractionScreenStrings }

fun sunTzuSubtractionScreenStringsForLanguage(language: String): SunTzuSubtractionScreenStrings = when (language) {
    "pt-BR" -> PtSunTzuSubtractionScreenStrings
    "es-ES" -> EsSunTzuSubtractionScreenStrings
    else -> EnSunTzuSubtractionScreenStrings
}
