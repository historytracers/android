// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplicationTableHubScreenStrings(
    val whatIsIt: String,
    val theSameResult: String,
    val equalSides: String,
    val doubleValues: String,
    val tripleValues: String,
    val multiplyingWithGroups: String,
)

val EnMultiplicationTableHubScreenStrings = MultiplicationTableHubScreenStrings(
    whatIsIt = "What is it?",
    theSameResult = "The Same Result",
    equalSides = "Equal Sides",
    doubleValues = "Double Values",
    tripleValues = "Triple Values",
    multiplyingWithGroups = "Multiplying with Groups (0 to 3)",
)

val PtMultiplicationTableHubScreenStrings = MultiplicationTableHubScreenStrings(
    whatIsIt = "O que \u00e9 isto?",
    theSameResult = "O Mesmo Resultado",
    equalSides = "Lados Iguais",
    doubleValues = "Valores Dobrados",
    tripleValues = "Valores Triplicados",
    multiplyingWithGroups = "Multiplicando com Grupos (0 a 3)",
)

val EsMultiplicationTableHubScreenStrings = MultiplicationTableHubScreenStrings(
    whatIsIt = "\u00bfQu\u00e9 es esto?",
    theSameResult = "El Mismo Resultado",
    equalSides = "Lados Iguales",
    doubleValues = "Valores Duplicados",
    tripleValues = "Valores Triplicados",
    multiplyingWithGroups = "Multiplicando con Grupos (0 a 3)",
)

val LocalMultiplicationTableHubScreenStrings = staticCompositionLocalOf { EnMultiplicationTableHubScreenStrings }

fun multiplicationTableHubScreenStringsForLanguage(language: String): MultiplicationTableHubScreenStrings = when (language) {
    "pt-BR" -> PtMultiplicationTableHubScreenStrings
    "es-ES" -> EsMultiplicationTableHubScreenStrings
    else -> EnMultiplicationTableHubScreenStrings
}
