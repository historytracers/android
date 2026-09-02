// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class RunningAndGrowingScreenStrings(
    val addingSameNumberExpression: String,
    val addingTheSameNumber: String,
)

val EnRunningAndGrowingScreenStrings = RunningAndGrowingScreenStrings(
    addingSameNumberExpression = "4+4+..",
    addingTheSameNumber = "Adding the same number",
)

val PtRunningAndGrowingScreenStrings = RunningAndGrowingScreenStrings(
    addingSameNumberExpression = "4+4+..",
    addingTheSameNumber = "Somando o mesmo n\u00famero",
)

val EsRunningAndGrowingScreenStrings = RunningAndGrowingScreenStrings(
    addingSameNumberExpression = "4+4+..",
    addingTheSameNumber = "Sumando el mismo n\u00famero",
)

val LocalRunningAndGrowingScreenStrings = staticCompositionLocalOf { EnRunningAndGrowingScreenStrings }

fun runningAndGrowingScreenStringsForLanguage(language: String): RunningAndGrowingScreenStrings = when (language) {
    "pt-BR" -> PtRunningAndGrowingScreenStrings
    "es-ES" -> EsRunningAndGrowingScreenStrings
    else -> EnRunningAndGrowingScreenStrings
}
