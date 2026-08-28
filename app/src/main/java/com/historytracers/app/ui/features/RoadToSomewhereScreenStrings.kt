// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class RoadToSomewhereScreenStrings(
    val walkAmongNumbers: String,
    val carryingInAddition: String,
    val runningAmongNumbers: String,
    val practicingAddition: String,
    val numberOne: String,
    val practicingExpression: String,
)

val EnRoadToSomewhereScreenStrings = RoadToSomewhereScreenStrings(
    walkAmongNumbers = "Walking Among Numbers",
    carryingInAddition = "Carrying in Addition",
    runningAmongNumbers = "Running Among Numbers",
    practicingAddition = "Practicing Addition",
    numberOne = "1",
    practicingExpression = "12+34",
)

val PtRoadToSomewhereScreenStrings = RoadToSomewhereScreenStrings(
    walkAmongNumbers = "Andar entre os n\u00fameros",
    carryingInAddition = "Levando 1 na Adi\u00e7\u00e3o",
    runningAmongNumbers = "Correndo entre os n\u00fameros",
    practicingAddition = "Praticando Adi\u00e7\u00e3o",
    numberOne = "1",
    practicingExpression = "12+34",
)

val EsRoadToSomewhereScreenStrings = RoadToSomewhereScreenStrings(
    walkAmongNumbers = "Caminar entre n\u00fameros",
    carryingInAddition = "Llevando 1 en la Suma",
    runningAmongNumbers = "Corriendo entre n\u00fameros",
    practicingAddition = "Practicando Suma",
    numberOne = "1",
    practicingExpression = "12+34",
)

val LocalRoadToSomewhereScreenStrings = staticCompositionLocalOf { EnRoadToSomewhereScreenStrings }

fun roadToSomewhereScreenStringsForLanguage(language: String): RoadToSomewhereScreenStrings = when (language) {
    "pt-BR" -> PtRoadToSomewhereScreenStrings
    "es-ES" -> EsRoadToSomewhereScreenStrings
    else -> EnRoadToSomewhereScreenStrings
}
