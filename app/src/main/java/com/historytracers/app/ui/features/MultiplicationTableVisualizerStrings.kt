// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplicationTableVisualizerStrings(
    val chooseObject: String,
    val tapHint: String,
    val tableOf: String,
)

val EnMultiplicationTableVisualizerStrings = MultiplicationTableVisualizerStrings(
    chooseObject = "Choose an object:",
    tapHint = "Tap on a multiplication to visualize it.",
    tableOf = "Table of %d",
)

val PtMultiplicationTableVisualizerStrings = MultiplicationTableVisualizerStrings(
    chooseObject = "Escolha um objeto:",
    tapHint = "Toque em uma multiplicação para visualizá-la.",
    tableOf = "Tabuada do %d",
)

val EsMultiplicationTableVisualizerStrings = MultiplicationTableVisualizerStrings(
    chooseObject = "Elige un objeto:",
    tapHint = "Toca una multiplicación para visualizarla.",
    tableOf = "Tabla del %d",
)

val LocalMultiplicationTableVisualizerStrings = staticCompositionLocalOf { EnMultiplicationTableVisualizerStrings }

fun multiplicationTableVisualizerStringsForLanguage(language: String): MultiplicationTableVisualizerStrings = when (language) {
    "pt-BR" -> PtMultiplicationTableVisualizerStrings
    "es-ES" -> EsMultiplicationTableVisualizerStrings
    else -> EnMultiplicationTableVisualizerStrings
}
