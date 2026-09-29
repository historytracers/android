// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class CalculiScreenStrings(
    val title: String,
    val instruction: String,
    val beadValues: String,
    val represent: String,
    val correctMessage: String,
    val finalCongrats: String,
    val playAgain: String,
)

val EnCalculiScreenStrings = CalculiScreenStrings(
    title = "Calculi",
    instruction = "Represent the proposed number on the Calculi abacus.",
    beadValues = "Markers below the bar have value 1 and markers above the bar have value 5. Move the markers toward the bar to represent the numbers.",
    represent = "Represent: %d",
    correctMessage = "\uD83C\uDF89 Correct! \uD83C\uDF89",
    finalCongrats = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Congratulations! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou completed all the levels!",
    playAgain = "Play Again",
)

val PtCalculiScreenStrings = CalculiScreenStrings(
    title = "Calculi",
    instruction = "Represente o n\u00famero proposto no \u00e1baco Calculi.",
    beadValues = "Os marcadores abaixo da barra valem 1 e os marcadores acima da barra valem 5. Mova os marcadores em dire\u00e7\u00e3o \u00e0 barra para representar os n\u00fameros.",
    represent = "Represente: %d",
    correctMessage = "\uD83C\uDF89 Correto! \uD83C\uDF89",
    finalCongrats = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Parab\u00e9ns! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea completou todos os n\u00edveis!",
    playAgain = "Jogar Novamente",
)

val EsCalculiScreenStrings = CalculiScreenStrings(
    title = "Calculi",
    instruction = "Representa el n\u00famero propuesto en el \u00e1baco Calculi.",
    beadValues = "Los marcadores debajo de la barra valen 1 y los marcadores encima de la barra valen 5. Mueve los marcadores hacia la barra para representar los n\u00fameros.",
    represent = "Representa: %d",
    correctMessage = "\uD83C\uDF89 \u00a1Correcto! \uD83C\uDF89",
    finalCongrats = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1Felicitaciones! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1Completaste todos los niveles!",
    playAgain = "Jugar de Nuevo",
)

val LocalCalculiScreenStrings = staticCompositionLocalOf { EnCalculiScreenStrings }

fun calculiScreenStringsForLanguage(language: String): CalculiScreenStrings = when (language) {
    "pt-BR" -> PtCalculiScreenStrings
    "es-ES" -> EsCalculiScreenStrings
    else -> EnCalculiScreenStrings
}
