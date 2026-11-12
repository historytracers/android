// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplyingWithGroupsScreenStrings(
    val fullTitle: String,
    val gameTitle: String,
    val rulesText: String,
    val gameIntro: String,
    val levelFormat: String,
    val playAgain: String,
    val correctMessage: String,
    val perfectMessage: String,
    val wrongPairMessage: String,
    val groupsDescription: String,
    val noGroupsDescription: String,
)

val EnMultiplyingWithGroupsScreenStrings = MultiplyingWithGroupsScreenStrings(
    fullTitle = "Multiplying with Groups (0 to 3)",
    gameTitle = "The Game",
    rulesText = "This game helps you see multiplication as equal groups. On the left, you will see a multiplication; on the right, its result appears together with a picture of groups of characters.\n\nEach rectangle on the right is one group. Every rectangle holds as many characters as the first factor, and the number of rectangles is the second factor. For example, 2 \u00D7 4 shows two characters in each of four rectangles — that is four groups of two, and the result is 8. When the first factor is 0, no characters appear, because zero times any number is zero.\n\nThe game has three levels:\n\n- **Level 1**: three multiplications — one from the 0 times table and two from the 1 times table.\n- **Level 2**: two multiplications, both from the 2 times table.\n- **Level 3**: two multiplications, both from the 3 times table.\n\nTo play, select a multiplication on the left and then its result on the right. If the pair is correct, both stay highlighted. When you match every pair, you complete the level and can move on to the next one.",
    gameIntro = "Match each multiplication on the left with its result on the right. The result also appears as groups of characters, so you can count them to check your answer. If a pair is wrong, a message will ask you to try again.",
    levelFormat = "Level %d/%d",
    playAgain = "Play Again",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Correct! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou matched all the pairs in this level!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Perfect! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou matched all the multiplications!",
    wrongPairMessage = "%d is not the result of %d \u00D7 %d. Count the characters and try again.",
    groupsDescription = "Groups: %d. Characters per group: %d.",
    noGroupsDescription = "No groups.",
)

val PtMultiplyingWithGroupsScreenStrings = MultiplyingWithGroupsScreenStrings(
    fullTitle = "Multiplicando com Grupos (0 a 3)",
    gameTitle = "O Jogo",
    rulesText = "Este jogo ajuda você a ver a multiplicação como grupos iguais. À esquerda aparece uma multiplicação; à direita, o seu resultado é mostrado junto com um desenho de grupos de caracteres.\n\nCada retângulo à direita é um grupo. Todos os retângulos contêm tantos caracteres quanto o primeiro fator, e o número de retângulos é o segundo fator. Por exemplo, 2 \u00D7 4 mostra dois caracteres em cada um de quatro retângulos: isso é quatro grupos de dois, e o resultado é 8. Quando o primeiro fator é 0, não aparecem caracteres, porque multiplicar qualquer número por 0 dá 0.\n\nO jogo tem três níveis:\n\n- **Nível 1**: três multiplicações — uma da tabuada do 0 e duas da tabuada do 1.\n- **Nível 2**: duas multiplicações, ambas da tabuada do 2.\n- **Nível 3**: duas multiplicações, ambas da tabuada do 3.\n\nPara jogar, selecione uma multiplicação à esquerda e depois o resultado à direita. Se o par estiver correto, ambos ficam destacados. Quando acertar todos os pares, você terá completado o nível e poderá avançar para o próximo.",
    gameIntro = "Una cada multiplicação da esquerda com o resultado da direita. O resultado também aparece como grupos de caracteres, assim você pode contá-los para conferir a sua resposta. Se um par estiver errado, uma mensagem pedirá que você tente novamente.",
    levelFormat = "Nível %d/%d",
    playAgain = "Jogar Novamente",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Correto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVocê acertou todos os pares deste nível!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Perfeito! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVocê acertou todas as multiplicações!",
    wrongPairMessage = "%d não é o resultado de %d \u00D7 %d. Conte os caracteres e tente novamente.",
    groupsDescription = "Grupos: %d. Caracteres por grupo: %d.",
    noGroupsDescription = "Sem grupos.",
)

val EsMultiplyingWithGroupsScreenStrings = MultiplyingWithGroupsScreenStrings(
    fullTitle = "Multiplicando con Grupos (0 a 3)",
    gameTitle = "El Juego",
    rulesText = "Este juego te ayuda a ver la multiplicación como grupos iguales. A la izquierda aparece una multiplicación; a la derecha, su resultado se muestra junto con un dibujo de grupos de caracteres.\n\nCada rectángulo de la derecha es un grupo. Todos los rectángulos contienen tantos caracteres como el primer factor, y el número de rectángulos es el segundo factor. Por ejemplo, 2 \u00D7 4 muestra dos caracteres en cada uno de cuatro rectángulos: eso es cuatro grupos de dos, y el resultado es 8. Cuando el primer factor es 0, no aparecen caracteres, porque multiplicar cualquier número por 0 da 0.\n\nEl juego tiene tres niveles:\n\n- **Nivel 1**: tres multiplicaciones — una de la tabla del 0 y dos de la tabla del 1.\n- **Nivel 2**: dos multiplicaciones, ambas de la tabla del 2.\n- **Nivel 3**: dos multiplicaciones, ambas de la tabla del 3.\n\nPara jugar, selecciona una multiplicación de la izquierda y luego su resultado de la derecha. Si la pareja es correcta, ambos quedan resaltados. Cuando aciertes todas las parejas, completarás el nivel y podrás pasar al siguiente.",
    gameIntro = "Une cada multiplicación de la izquierda con su resultado de la derecha. El resultado también se muestra como grupos de caracteres, así puedes contarlos para comprobar tu respuesta. Si una pareja es incorrecta, un mensaje te pedirá que lo intentes de nuevo.",
    levelFormat = "Nivel %d/%d",
    playAgain = "Jugar de Nuevo",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 ¡Correcto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n¡Acertaste todas las parejas de este nivel!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 ¡Perfecto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n¡Acertaste todas las multiplicaciones!",
    wrongPairMessage = "%d no es el resultado de %d \u00D7 %d. Cuenta los caracteres e inténtalo de nuevo.",
    groupsDescription = "Grupos: %d. Caracteres por grupo: %d.",
    noGroupsDescription = "Sin grupos.",
)

val LocalMultiplyingWithGroupsScreenStrings = staticCompositionLocalOf { EnMultiplyingWithGroupsScreenStrings }

fun multiplyingWithGroupsScreenStringsForLanguage(language: String): MultiplyingWithGroupsScreenStrings = when (language) {
    "pt-BR" -> PtMultiplyingWithGroupsScreenStrings
    "es-ES" -> EsMultiplyingWithGroupsScreenStrings
    else -> EnMultiplyingWithGroupsScreenStrings
}
