// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class ReturningScreenStrings(
    val walkingBackwards: String,
    val practicingSubtraction: String,
    val subtractionExample: String,
    val practicingSubtractionTyped: String,
    val subtractionTypedExample: String,
)

val EnReturningScreenStrings = ReturningScreenStrings(
    walkingBackwards = "Walking Backwards",
    practicingSubtraction = "Practicing Subtraction",
    subtractionExample = "9 - 5",
    practicingSubtractionTyped = "Practicing Subtraction with Typed Values",
    subtractionTypedExample = "8 - 1 = ?",
)

val PtReturningScreenStrings = ReturningScreenStrings(
    walkingBackwards = "Andando para Tr\u00e1s",
    practicingSubtraction = "Praticando Subtra\u00e7\u00e3o",
    subtractionExample = "9 - 5",
    practicingSubtractionTyped = "Praticando Subtra\u00e7\u00e3o com Valores Digitados",
    subtractionTypedExample = "8 - 1 = ?",
)

val EsReturningScreenStrings = ReturningScreenStrings(
    walkingBackwards = "Caminando Hacia Atr\u00e1s",
    practicingSubtraction = "Practicando Resta",
    subtractionExample = "9 - 5",
    practicingSubtractionTyped = "Practicando Resta con Valores Escritos",
    subtractionTypedExample = "8 - 1 = ?",
)

val LocalReturningScreenStrings = staticCompositionLocalOf { EnReturningScreenStrings }

fun returningScreenStringsForLanguage(language: String): ReturningScreenStrings = when (language) {
    "pt-BR" -> PtReturningScreenStrings
    "es-ES" -> EsReturningScreenStrings
    else -> EnReturningScreenStrings
}
