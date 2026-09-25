// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class RepresentYouScreenStrings(
    val title: String,
    val instruction: String,
    val roman: String,
    val hinduArabic: String,
    val levelComplete: String,
    val finalCongrats: String,
    val playAgain: String,
)

val EnRepresentYouScreenStrings = RepresentYouScreenStrings(
    title = "I Represent You",
    instruction = "Match each Roman numeral on the left with its Hindu-Arabic number on the right. Select one from each column to pair them.",
    roman = "Roman",
    hinduArabic = "Hindu-Arabic",
    levelComplete = "Congratulations, you finished level %d/%d!",
    finalCongrats = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou matched all the numbers!\nGreat job!",
    playAgain = "Play Again",
)

val PtRepresentYouScreenStrings = RepresentYouScreenStrings(
    title = "Eu Represento Voc\u00ea",
    instruction = "Ligue cada n\u00famero romano \u00e0 esquerda ao seu n\u00famero indo-\u00e1rabe \u00e0 direita. Selecione um de cada coluna para formar o par.",
    roman = "Romano",
    hinduArabic = "Indo-\u00e1rabe",
    levelComplete = "Parab\u00e9ns, voc\u00ea finalizou o n\u00edvel %d/%d!",
    finalCongrats = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFEITO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea ligou todos os n\u00fameros!\n\u00d3timo trabalho!",
    playAgain = "Jogar Novamente",
)

val EsRepresentYouScreenStrings = RepresentYouScreenStrings(
    title = "Yo Te Represento",
    instruction = "Empareja cada n\u00famero romano de la izquierda con su n\u00famero indo\u00e1rabe de la derecha. Selecciona uno de cada columna para formar la pareja.",
    roman = "Romano",
    hinduArabic = "Indo\u00e1rabe",
    levelComplete = "\u00a1Felicitaciones, terminaste el nivel %d/%d!",
    finalCongrats = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1PERFECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1Emparejaste todos los n\u00fameros!\n\u00a1Gran trabajo!",
    playAgain = "Jugar de Nuevo",
)

val LocalRepresentYouScreenStrings = staticCompositionLocalOf { EnRepresentYouScreenStrings }

fun representYouScreenStringsForLanguage(language: String): RepresentYouScreenStrings = when (language) {
    "pt-BR" -> PtRepresentYouScreenStrings
    "es-ES" -> EsRepresentYouScreenStrings
    else -> EnRepresentYouScreenStrings
}
