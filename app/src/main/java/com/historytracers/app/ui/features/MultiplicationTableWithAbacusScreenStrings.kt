// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplicationTableWithAbacusScreenStrings(
    val whatIsIt: String,
)

val EnMultiplicationTableWithAbacusScreenStrings = MultiplicationTableWithAbacusScreenStrings(
    whatIsIt = "What is it?",
)

val PtMultiplicationTableWithAbacusScreenStrings = MultiplicationTableWithAbacusScreenStrings(
    whatIsIt = "O que \u00e9 isto?",
)

val EsMultiplicationTableWithAbacusScreenStrings = MultiplicationTableWithAbacusScreenStrings(
    whatIsIt = "\u00bfQu\u00e9 es esto?",
)

val LocalMultiplicationTableWithAbacusScreenStrings = staticCompositionLocalOf { EnMultiplicationTableWithAbacusScreenStrings }

fun multiplicationTableWithAbacusScreenStringsForLanguage(language: String): MultiplicationTableWithAbacusScreenStrings = when (language) {
    "pt-BR" -> PtMultiplicationTableWithAbacusScreenStrings
    "es-ES" -> EsMultiplicationTableWithAbacusScreenStrings
    else -> EnMultiplicationTableWithAbacusScreenStrings
}
