// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplicationTableVisualizerStrings(
    val chooseObject: String,
    val tapHint: String,
)

val EnMultiplicationTableVisualizerStrings = MultiplicationTableVisualizerStrings(
    chooseObject = "Choose an object:",
    tapHint = "Tap on a multiplication to visualize it.",
)

val PtMultiplicationTableVisualizerStrings = MultiplicationTableVisualizerStrings(
    chooseObject = "Escolha um objeto:",
    tapHint = "Toque em uma multiplicação para visualizá-la.",
)

val EsMultiplicationTableVisualizerStrings = MultiplicationTableVisualizerStrings(
    chooseObject = "Elige un objeto:",
    tapHint = "Toca una multiplicación para visualizarla.",
)

val LocalMultiplicationTableVisualizerStrings = staticCompositionLocalOf { EnMultiplicationTableVisualizerStrings }

fun multiplicationTableVisualizerStringsForLanguage(language: String): MultiplicationTableVisualizerStrings = when (language) {
    "pt-BR" -> PtMultiplicationTableVisualizerStrings
    "es-ES" -> EsMultiplicationTableVisualizerStrings
    else -> EnMultiplicationTableVisualizerStrings
}
