// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class FirstStepsScreenStrings(
    val building: String,
)

val EnFirstStepsScreenStrings = FirstStepsScreenStrings(
    building = "Building",
)

val PtFirstStepsScreenStrings = FirstStepsScreenStrings(
    building = "Constru\u00e7\u00e3o",
)

val EsFirstStepsScreenStrings = FirstStepsScreenStrings(
    building = "Construcci\u00f3n",
)

val LocalFirstStepsScreenStrings = staticCompositionLocalOf { EnFirstStepsScreenStrings }

fun firstStepsScreenStringsForLanguage(language: String): FirstStepsScreenStrings = when (language) {
    "pt-BR" -> PtFirstStepsScreenStrings
    "es-ES" -> EsFirstStepsScreenStrings
    else -> EnFirstStepsScreenStrings
}
