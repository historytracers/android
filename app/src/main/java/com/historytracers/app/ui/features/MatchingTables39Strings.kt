// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MatchingTables39Strings(
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

val EnMatchingTables39Strings = MatchingTables39Strings(
    rulesTitle = "Matching the Tables of 3 and 9",
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

val PtMatchingTables39Strings = MatchingTables39Strings(
    rulesTitle = "Combinando as Tabuadas de 3 e 9",
    gameTitle = "O Jogo",
    levelFormat = "N\u00EDvel %d/%d",
    playAgain = "Jogar Novamente",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Correto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00EA acertou todos os pares deste n\u00EDvel!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Perfeito! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00EA acertou todas as igualdades!",
    wrongPairMessage = "%d n\u00E3o \u00E9 o resultado de %s. Conte os caracteres e tente novamente.",
    groupsDescription = "Grupos: %d. Caracteres por grupo: %d.",
    randomObject = "\uD83C\uDFB2 Aleat\u00F3rio",
    drawLabel = "\uD83C\uDFA8",
)

val EsMatchingTables39Strings = MatchingTables39Strings(
    rulesTitle = "Combinando las Tablas de 3 y 9",
    gameTitle = "El Juego",
    levelFormat = "Nivel %d/%d",
    playAgain = "Jugar de Nuevo",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00A1Correcto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00A1Acertaste todas las parejas de este nivel!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00A1Perfecto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00A1Acertaste todas las igualdades!",
    wrongPairMessage = "%d no es el resultado de %s. Cuenta los caracteres e int\u00E9ntalo de nuevo.",
    groupsDescription = "Grupos: %d. Caracteres por grupo: %d.",
    randomObject = "\uD83C\uDFB2 Aleatorio",
    drawLabel = "\uD83C\uDFA8",
)

val LocalMatchingTables39Strings = staticCompositionLocalOf { EnMatchingTables39Strings }

fun matchingTables39StringsForLanguage(language: String): MatchingTables39Strings = when (language) {
    "pt-BR" -> PtMatchingTables39Strings
    "es-ES" -> EsMatchingTables39Strings
    else -> EnMatchingTables39Strings
}
