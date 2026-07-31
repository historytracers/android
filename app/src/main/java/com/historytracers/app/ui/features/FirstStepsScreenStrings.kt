// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class FirstStepsScreenStrings(
    val building: String,
    val learningInLayers: String,
    val howDoILearn: String,
)

val EnFirstStepsScreenStrings = FirstStepsScreenStrings(
    building = "Building",
    learningInLayers = "Learning in layers",
    howDoILearn = "How do I learn?",
)

val PtFirstStepsScreenStrings = FirstStepsScreenStrings(
    building = "Constru\u00e7\u00e3o",
    learningInLayers = "Aprendendo em camadas",
    howDoILearn = "Como eu aprendo?",
)

val EsFirstStepsScreenStrings = FirstStepsScreenStrings(
    building = "Construcci\u00f3n",
    learningInLayers = "Aprendiendo en capas",
    howDoILearn = "\u00bfC\u00f3mo aprendo?",
)

val LocalFirstStepsScreenStrings = staticCompositionLocalOf { EnFirstStepsScreenStrings }

fun firstStepsScreenStringsForLanguage(language: String): FirstStepsScreenStrings = when (language) {
    "pt-BR" -> PtFirstStepsScreenStrings
    "es-ES" -> EsFirstStepsScreenStrings
    else -> EnFirstStepsScreenStrings
}
