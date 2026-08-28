// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class RoadToSomewhereScreenStrings(
    val walkAmongNumbers: String,
    val carryingInAddition: String,
)

val EnRoadToSomewhereScreenStrings = RoadToSomewhereScreenStrings(
    walkAmongNumbers = "Walk among Numbers",
    carryingInAddition = "Carrying in Addition",
)

val PtRoadToSomewhereScreenStrings = RoadToSomewhereScreenStrings(
    walkAmongNumbers = "Andar entre os n\u00fameros",
    carryingInAddition = "Levando 1 na Adi\u00e7\u00e3o",
)

val EsRoadToSomewhereScreenStrings = RoadToSomewhereScreenStrings(
    walkAmongNumbers = "Caminar entre n\u00fameros",
    carryingInAddition = "Llevando 1 en la Suma",
)

val LocalRoadToSomewhereScreenStrings = staticCompositionLocalOf { EnRoadToSomewhereScreenStrings }

fun roadToSomewhereScreenStringsForLanguage(language: String): RoadToSomewhereScreenStrings = when (language) {
    "pt-BR" -> PtRoadToSomewhereScreenStrings
    "es-ES" -> EsRoadToSomewhereScreenStrings
    else -> EnRoadToSomewhereScreenStrings
}
