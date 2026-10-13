// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MeasuringScreenStrings(
    val compareExpression: String,
    val biggerOrSmaller: String,
    val compareSymbols: String,
    val greaterOrSmallerGame: String,
    val measuringOneDirection: String,
)

val EnMeasuringScreenStrings = MeasuringScreenStrings(
    compareExpression = "15 < 16",
    biggerOrSmaller = "Bigger or Smaller",
    compareSymbols = "> <",
    greaterOrSmallerGame = "Greater or smaller (The Game)",
    measuringOneDirection = "Measuring in one direction",
)

val PtMeasuringScreenStrings = MeasuringScreenStrings(
    compareExpression = "15 < 16",
    biggerOrSmaller = "Maior ou Menor",
    compareSymbols = "> <",
    greaterOrSmallerGame = "Maior ou menor (O Jogo)",
    measuringOneDirection = "Medindo em uma dire\u00e7\u00e3o",
)

val EsMeasuringScreenStrings = MeasuringScreenStrings(
    compareExpression = "15 < 16",
    biggerOrSmaller = "Mayor o Menor",
    compareSymbols = "> <",
    greaterOrSmallerGame = "Mayor o menor (El Juego)",
    measuringOneDirection = "Midiendo en una direcci\u00f3n",
)

val LocalMeasuringScreenStrings = staticCompositionLocalOf { EnMeasuringScreenStrings }

fun measuringScreenStringsForLanguage(language: String): MeasuringScreenStrings = when (language) {
    "pt-BR" -> PtMeasuringScreenStrings
    "es-ES" -> EsMeasuringScreenStrings
    else -> EnMeasuringScreenStrings
}
