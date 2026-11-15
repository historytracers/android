// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MatchingTables24Strings(
    val rulesTitle: String,
    val gameTitle: String,
    val levelFormat: String,
    val playAgain: String,
    val correctMessage: String,
    val perfectMessage: String,
    val wrongPairMessage: String,
    val groupsDescription: String,
    val randomObject: String,
    val drawLabel: String,
)

val EnMatchingTables24Strings = MatchingTables24Strings(
    rulesTitle = "Matching the Tables of 2 and 4",
    gameTitle = "The Game",
    levelFormat = "Level %d/%d",
    playAgain = "Play Again",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Correct! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou matched all the pairs in this level!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Perfect! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou matched all the equalities!",
    wrongPairMessage = "%d is not the result of %s. Count the characters and try again.",
    groupsDescription = "Groups: %d. Characters per group: %d.",
    randomObject = "\uD83C\uDFB2 Random",
    drawLabel = "\uD83C\uDFA8",
)

val PtMatchingTables24Strings = MatchingTables24Strings(
    rulesTitle = "Combinando as Tabuadas de 2 e 4",
    gameTitle = "O Jogo",
    levelFormat = "N\u00edvel %d/%d",
    playAgain = "Jogar Novamente",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Correto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea acertou todos os pares deste n\u00edvel!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Perfeito! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea acertou todas as igualdades!",
    wrongPairMessage = "%d n\u00e3o \u00e9 o resultado de %s. Conte os caracteres e tente novamente.",
    groupsDescription = "Grupos: %d. Caracteres por grupo: %d.",
    randomObject = "\uD83C\uDFB2 Aleat\u00f3rio",
    drawLabel = "\uD83C\uDFA8",
)

val EsMatchingTables24Strings = MatchingTables24Strings(
    rulesTitle = "Combinando las Tablas de 2 y 4",
    gameTitle = "El Juego",
    levelFormat = "Nivel %d/%d",
    playAgain = "Jugar de Nuevo",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1Correcto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1Acertaste todas las parejas de este nivel!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1Perfecto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1Acertaste todas las igualdades!",
    wrongPairMessage = "%d no es el resultado de %s. Cuenta los caracteres e int\u00e9ntalo de nuevo.",
    groupsDescription = "Grupos: %d. Caracteres por grupo: %d.",
    randomObject = "\uD83C\uDFB2 Aleatorio",
    drawLabel = "\uD83C\uDFA8",
)

val LocalMatchingTables24Strings = staticCompositionLocalOf { EnMatchingTables24Strings }

fun matchingTables24StringsForLanguage(language: String): MatchingTables24Strings = when (language) {
    "pt-BR" -> PtMatchingTables24Strings
    "es-ES" -> EsMatchingTables24Strings
    else -> EnMatchingTables24Strings
}
