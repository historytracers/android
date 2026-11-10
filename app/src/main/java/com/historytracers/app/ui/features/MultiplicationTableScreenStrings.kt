// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplicationTableScreenStrings(
    val whatIsIt: String,
)

val EnMultiplicationTableScreenStrings = MultiplicationTableScreenStrings(
    whatIsIt = "What is it?",
)

val PtMultiplicationTableScreenStrings = MultiplicationTableScreenStrings(
    whatIsIt = "O que \u00e9 isto?",
)

val EsMultiplicationTableScreenStrings = MultiplicationTableScreenStrings(
    whatIsIt = "\u00bfQu\u00e9 es esto?",
)

val LocalMultiplicationTableScreenStrings = staticCompositionLocalOf { EnMultiplicationTableScreenStrings }

fun multiplicationTableScreenStringsForLanguage(language: String): MultiplicationTableScreenStrings = when (language) {
    "pt-BR" -> PtMultiplicationTableScreenStrings
    "es-ES" -> EsMultiplicationTableScreenStrings
    else -> EnMultiplicationTableScreenStrings
}
