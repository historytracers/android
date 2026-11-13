// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplyingWithGroupsGameScreenStrings(
    val gameTitle: String,
    val gameIntro: String,
    val levelFormat: String,
    val playAgain: String,
    val correctMessage: String,
    val perfectMessage: String,
    val wrongPairMessage: String,
    val groupsDescription: String,
    val noGroupsDescription: String,
)

val EnMultiplyingWithGroupsGameScreenStrings = MultiplyingWithGroupsGameScreenStrings(
    gameTitle = "The Game",
    gameIntro = "Match each multiplication on the left with its result on the right. The result also appears as groups of characters, so you can count them to check your answer. If a pair is wrong, a message will ask you to try again.",
    levelFormat = "Level %d/%d",
    playAgain = "Play Again",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Correct! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou matched all the pairs in this level!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Perfect! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou matched all the multiplications!",
    wrongPairMessage = "%d is not the result of %d \u00D7 %d. Count the characters and try again.",
    groupsDescription = "Groups: %d. Characters per group: %d.",
    noGroupsDescription = "No groups.",
)

val PtMultiplyingWithGroupsGameScreenStrings = MultiplyingWithGroupsGameScreenStrings(
    gameTitle = "O Jogo",
    gameIntro = "Una cada multiplicação da esquerda com o resultado da direita. O resultado também aparece como grupos de caracteres, assim você pode contá-los para conferir a sua resposta. Se um par estiver errado, uma mensagem pedirá que você tente novamente.",
    levelFormat = "Nível %d/%d",
    playAgain = "Jogar Novamente",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Correto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVocê acertou todos os pares deste nível!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Perfeito! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVocê acertou todas as multiplicações!",
    wrongPairMessage = "%d não é o resultado de %d \u00D7 %d. Conte os caracteres e tente novamente.",
    groupsDescription = "Grupos: %d. Caracteres por grupo: %d.",
    noGroupsDescription = "Sem grupos.",
)

val EsMultiplyingWithGroupsGameScreenStrings = MultiplyingWithGroupsGameScreenStrings(
    gameTitle = "El Juego",
    gameIntro = "Une cada multiplicación de la izquierda con su resultado de la derecha. El resultado también se muestra como grupos de caracteres, así puedes contarlos para comprobar tu respuesta. Si una pareja es incorrecta, un mensaje te pedirá que lo intentes de nuevo.",
    levelFormat = "Nivel %d/%d",
    playAgain = "Jugar de Nuevo",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 ¡Correcto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n¡Acertaste todas las parejas de este nivel!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 ¡Perfecto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n¡Acertaste todas las multiplicaciones!",
    wrongPairMessage = "%d no es el resultado de %d \u00D7 %d. Cuenta los caracteres e inténtalo de nuevo.",
    groupsDescription = "Grupos: %d. Caracteres por grupo: %d.",
    noGroupsDescription = "Sin grupos.",
)

val LocalMultiplyingWithGroupsGameScreenStrings = staticCompositionLocalOf { EnMultiplyingWithGroupsGameScreenStrings }

fun multiplyingWithGroupsGameScreenStringsForLanguage(language: String): MultiplyingWithGroupsGameScreenStrings = when (language) {
    "pt-BR" -> PtMultiplyingWithGroupsGameScreenStrings
    "es-ES" -> EsMultiplyingWithGroupsGameScreenStrings
    else -> EnMultiplyingWithGroupsGameScreenStrings
}
