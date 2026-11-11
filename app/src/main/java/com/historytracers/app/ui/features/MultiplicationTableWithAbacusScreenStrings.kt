// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplicationTableWithAbacusScreenStrings(
    val whatIsIt: String,
    val theSameResult: String,
    val equalSides: String,
)

val EnMultiplicationTableWithAbacusScreenStrings = MultiplicationTableWithAbacusScreenStrings(
    whatIsIt = "What is it?",
    theSameResult = "The Same Result",
    equalSides = "Equal Sides",
)

val PtMultiplicationTableWithAbacusScreenStrings = MultiplicationTableWithAbacusScreenStrings(
    whatIsIt = "O que \u00e9 isto?",
    theSameResult = "O Mesmo Resultado",
    equalSides = "Lados Iguais",
)

val EsMultiplicationTableWithAbacusScreenStrings = MultiplicationTableWithAbacusScreenStrings(
    whatIsIt = "\u00bfQu\u00e9 es esto?",
    theSameResult = "El Mismo Resultado",
    equalSides = "Lados Iguales",
)

val LocalMultiplicationTableWithAbacusScreenStrings = staticCompositionLocalOf { EnMultiplicationTableWithAbacusScreenStrings }

fun multiplicationTableWithAbacusScreenStringsForLanguage(language: String): MultiplicationTableWithAbacusScreenStrings = when (language) {
    "pt-BR" -> PtMultiplicationTableWithAbacusScreenStrings
    "es-ES" -> EsMultiplicationTableWithAbacusScreenStrings
    else -> EnMultiplicationTableWithAbacusScreenStrings
}
