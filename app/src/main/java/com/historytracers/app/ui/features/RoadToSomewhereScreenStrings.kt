// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class RoadToSomewhereScreenStrings(
    val title: String,
    val walkAmongNumbers: String,
    val carryingInAddition: String,
    val theOrderOfAddition: String,
    val runningAmongNumbers: String,
    val advancingAndComplementing: String,
    val carryOrNotCarry: String,
    val playingWithAxioms: String,
    val practicingAddition: String,
    val numberOne: String,
    val commutativeExpression: String,
    val axiomsExpression: String,
    val practicingExpression: String,
)

val EnRoadToSomewhereScreenStrings = RoadToSomewhereScreenStrings(
    title = "A Road to Somewhere",
    walkAmongNumbers = "Walking Among Numbers",
    carryingInAddition = "Carrying in Addition",
    theOrderOfAddition = "The Order of Addition",
    runningAmongNumbers = "Running Among Numbers",
    advancingAndComplementing = "Advancing and Complementing",
    carryOrNotCarry = "Carry or Not Carry?",
    playingWithAxioms = "Playing with Axioms",
    practicingAddition = "Practicing Addition",
    numberOne = "1",
    commutativeExpression = "1 + 2 = 2 + 1",
    axiomsExpression = "a + 0 = a,\na + b = b + a",
    practicingExpression = "12+34",
)

val PtRoadToSomewhereScreenStrings = RoadToSomewhereScreenStrings(
    title = "Uma estrada para algum lugar",
    walkAmongNumbers = "Andar entre os n\u00fameros",
    carryingInAddition = "Levando 1 na Adi\u00e7\u00e3o",
    theOrderOfAddition = "A Ordem da Adi\u00e7\u00e3o",
    runningAmongNumbers = "Correndo entre os n\u00fameros",
    advancingAndComplementing = "Avan\u00e7ando e Complementando",
    carryOrNotCarry = "Levar ou n\u00e3o levar?",
    playingWithAxioms = "Brincando com axiomas",
    practicingAddition = "Praticando Adi\u00e7\u00e3o",
    numberOne = "1",
    commutativeExpression = "1 + 2 = 2 + 1",
    axiomsExpression = "a + 0 = a,\na + b = b + a",
    practicingExpression = "12+34",
)

val EsRoadToSomewhereScreenStrings = RoadToSomewhereScreenStrings(
    title = "Un camino a alg\u00fan lugar",
    walkAmongNumbers = "Caminar entre n\u00fameros",
    carryingInAddition = "Llevando 1 en la Suma",
    theOrderOfAddition = "El Orden de la Suma",
    runningAmongNumbers = "Corriendo entre n\u00fameros",
    advancingAndComplementing = "Avanzando y Complementando",
    carryOrNotCarry = "\u00bfLlevar o no llevar?",
    playingWithAxioms = "Jugando con axiomas",
    practicingAddition = "Practicando Suma",
    numberOne = "1",
    commutativeExpression = "1 + 2 = 2 + 1",
    axiomsExpression = "a + 0 = a,\na + b = b + a",
    practicingExpression = "12+34",
)

val LocalRoadToSomewhereScreenStrings = staticCompositionLocalOf { EnRoadToSomewhereScreenStrings }

fun roadToSomewhereScreenStringsForLanguage(language: String): RoadToSomewhereScreenStrings = when (language) {
    "pt-BR" -> PtRoadToSomewhereScreenStrings
    "es-ES" -> EsRoadToSomewhereScreenStrings
    else -> EnRoadToSomewhereScreenStrings
}
