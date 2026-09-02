// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class RunningAndGrowingScreenStrings(
    val addingSameNumberExpression: String,
    val addingTheSameNumber: String,
    val resultExpression: String,
    val theResultIs: String,
    val inversion: String,
)

val EnRunningAndGrowingScreenStrings = RunningAndGrowingScreenStrings(
    addingSameNumberExpression = "4+4+..",
    addingTheSameNumber = "Adding the same number",
    resultExpression = "7 x 7 + 7=?",
    theResultIs = "The result is",
    inversion = "Inversion",
)

val PtRunningAndGrowingScreenStrings = RunningAndGrowingScreenStrings(
    addingSameNumberExpression = "4+4+..",
    addingTheSameNumber = "Somando o mesmo n\u00famero",
    resultExpression = "7 x 7 + 7=?",
    theResultIs = "O resultado \u00e9",
    inversion = "Invers\u00e3o",
)

val EsRunningAndGrowingScreenStrings = RunningAndGrowingScreenStrings(
    addingSameNumberExpression = "4+4+..",
    addingTheSameNumber = "Sumando el mismo n\u00famero",
    resultExpression = "7 x 7 + 7=?",
    theResultIs = "El resultado es",
    inversion = "Inversi\u00f3n",
)

val LocalRunningAndGrowingScreenStrings = staticCompositionLocalOf { EnRunningAndGrowingScreenStrings }

fun runningAndGrowingScreenStringsForLanguage(language: String): RunningAndGrowingScreenStrings = when (language) {
    "pt-BR" -> PtRunningAndGrowingScreenStrings
    "es-ES" -> EsRunningAndGrowingScreenStrings
    else -> EnRunningAndGrowingScreenStrings
}
