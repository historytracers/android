// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MeasuringScreenStrings(
    val compareExpression: String,
    val biggerOrSmaller: String,
)

val EnMeasuringScreenStrings = MeasuringScreenStrings(
    compareExpression = "15 < 16",
    biggerOrSmaller = "Bigger or Smaller",
)

val PtMeasuringScreenStrings = MeasuringScreenStrings(
    compareExpression = "15 < 16",
    biggerOrSmaller = "Maior ou Menor",
)

val EsMeasuringScreenStrings = MeasuringScreenStrings(
    compareExpression = "15 < 16",
    biggerOrSmaller = "Mayor o Menor",
)

val LocalMeasuringScreenStrings = staticCompositionLocalOf { EnMeasuringScreenStrings }

fun measuringScreenStringsForLanguage(language: String): MeasuringScreenStrings = when (language) {
    "pt-BR" -> PtMeasuringScreenStrings
    "es-ES" -> EsMeasuringScreenStrings
    else -> EnMeasuringScreenStrings
}
