// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class WhoAmIScreenStrings(
    val learningInLayers: String,
    val howDoILearn: String,
    val sunTzuAndLearning: String,
)

val EnWhoAmIScreenStrings = WhoAmIScreenStrings(
    learningInLayers = "Learning in Layers",
    howDoILearn = "How Do I Learn?",
    sunTzuAndLearning = "Sun Tzu and Learning",
)

val PtWhoAmIScreenStrings = WhoAmIScreenStrings(
    learningInLayers = "Aprendendo em camadas",
    howDoILearn = "Como eu aprendo?",
    sunTzuAndLearning = "Sun Tzu e a aprendizagem",
)

val EsWhoAmIScreenStrings = WhoAmIScreenStrings(
    learningInLayers = "Aprendiendo en capas",
    howDoILearn = "\u00bfC\u00f3mo aprendo?",
    sunTzuAndLearning = "Sun Tzu y el aprendizaje",
)

val LocalWhoAmIScreenStrings = staticCompositionLocalOf { EnWhoAmIScreenStrings }

fun whoAmIScreenStringsForLanguage(language: String): WhoAmIScreenStrings = when (language) {
    "pt-BR" -> PtWhoAmIScreenStrings
    "es-ES" -> EsWhoAmIScreenStrings
    else -> EnWhoAmIScreenStrings
}
