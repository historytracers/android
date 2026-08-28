// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class RoadToSomewhereScreenStrings(
    val walkAmongNumbers: String,
)

val EnRoadToSomewhereScreenStrings = RoadToSomewhereScreenStrings(
    walkAmongNumbers = "Walk among Numbers",
)

val PtRoadToSomewhereScreenStrings = RoadToSomewhereScreenStrings(
    walkAmongNumbers = "Andar entre os n\u00fameros",
)

val EsRoadToSomewhereScreenStrings = RoadToSomewhereScreenStrings(
    walkAmongNumbers = "Caminar entre n\u00fameros",
)

val LocalRoadToSomewhereScreenStrings = staticCompositionLocalOf { EnRoadToSomewhereScreenStrings }

fun roadToSomewhereScreenStringsForLanguage(language: String): RoadToSomewhereScreenStrings = when (language) {
    "pt-BR" -> PtRoadToSomewhereScreenStrings
    "es-ES" -> EsRoadToSomewhereScreenStrings
    else -> EnRoadToSomewhereScreenStrings
}
