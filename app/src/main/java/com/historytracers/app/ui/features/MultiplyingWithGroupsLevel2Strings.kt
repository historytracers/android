// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplyingWithGroupsLevel2Strings(
    val rulesTitle: String,
    val rulesText: String,
    val gameTitle: String,
    val gameIntro: String,
    val levelFormat: String,
    val playAgain: String,
    val correctMessage: String,
    val perfectMessage: String,
    val wrongPairMessage: String,
    val groupsDescription: String,
    val noGroupsDescription: String,
    val randomObject: String,
    val drawLabel: String,
)

val EnMultiplyingWithGroupsLevel2Strings = MultiplyingWithGroupsLevel2Strings(
    rulesTitle = "Multiplying with Groups (4 to 7)",
    rulesText = "This game helps you see multiplication as equal groups. On the left, you will see a multiplication; on the right, its result appears together with a picture of groups of characters.\n\nEach rectangle on the right is one group. Every rectangle holds as many characters as the first factor, and the number of rectangles is the second factor. For example, 4 \u00D7 6 shows four characters in each of six rectangles — that is six groups of four, and the result is 24.\n\nThe game has four levels:\n\n- **Level 1**: two multiplications from the 4 times table.\n- **Level 2**: two multiplications from the 5 times table.\n- **Level 3**: two multiplications from the 6 times table.\n- **Level 4**: two multiplications from the 7 times table.\n\nAbove the game, a selector chooses the image used in the groups: **\uD83C\uDFB2 Random** draws a random object from the multiplication tables in every new exercise, or you can select one specific image — the same ones used in the multiplication tables — and every group uses it while you play.\n\nTo play, select a multiplication on the left and then its result on the right. If the pair is correct, both stay highlighted. When you match every pair, you complete the level and can move on to the next one.",
    gameTitle = "The Game",
    gameIntro = "Match each multiplication on the left with its result on the right. The result also appears as groups of characters, so you can count them to check your answer. If a pair is wrong, a message will ask you to try again.",
    levelFormat = "Level %d/%d",
    playAgain = "Play Again",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Correct! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou matched all the pairs in this level!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Perfect! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou matched all the multiplications!",
    wrongPairMessage = "%d is not the result of %d \u00D7 %d. Count the characters and try again.",
    groupsDescription = "Groups: %d. Characters per group: %d.",
    noGroupsDescription = "No groups.",
    randomObject = "\uD83C\uDFB2 Random",
    drawLabel = "\uD83C\uDFA8",
)

val PtMultiplyingWithGroupsLevel2Strings = MultiplyingWithGroupsLevel2Strings(
    rulesTitle = "Multiplicando com Grupos (4 a 7)",
    rulesText = "Este jogo ajuda você a ver a multiplicação como grupos iguais. À esquerda aparece uma multiplicação; à direita, o seu resultado é mostrado junto com um desenho de grupos de caracteres.\n\nCada retângulo à direita é um grupo. Todos os retângulos contêm tantos caracteres quanto o primeiro fator, e o número de retângulos é o segundo fator. Por exemplo, 4 \u00D7 6 mostra quatro caracteres em cada um de seis retângulos: isso é seis grupos de quatro, e o resultado é 24.\n\nO jogo tem quatro níveis:\n\n- **Nível 1**: duas multiplicações da tabuada do 4.\n- **Nível 2**: duas multiplicações da tabuada do 5.\n- **Nível 3**: duas multiplicações da tabuada do 6.\n- **Nível 4**: duas multiplicações da tabuada do 7.\n\nAcima do jogo, um seletor escolhe a imagem usada nos grupos: **\uD83C\uDFB2 Aleatório** desenha um objeto aleatório das tabuadas de multiplicação em cada novo exercício, ou você pode selecionar uma imagem específica — as mesmas usadas nas tabuadas de multiplicação — e todos os grupos a usam enquanto você joga.\n\nPara jogar, selecione uma multiplicação da esquerda e depois o resultado da direita. Se o par estiver correto, ambos ficam destacados. Quando acertar todos os pares, você terá completado o nível e poderá avançar para o próximo.",
    gameTitle = "O Jogo",
    gameIntro = "Una cada multiplicação da esquerda com o resultado da direita. O resultado também aparece como grupos de caracteres, assim você pode contá-los para conferir a sua resposta. Se um par estiver errado, uma mensagem pedirá que você tente novamente.",
    levelFormat = "Nível %d/%d",
    playAgain = "Jogar Novamente",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Correto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVocê acertou todos os pares deste nível!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Perfeito! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVocê acertou todas as multiplicações!",
    wrongPairMessage = "%d não é o resultado de %d \u00D7 %d. Conte os caracteres e tente novamente.",
    groupsDescription = "Grupos: %d. Caracteres por grupo: %d.",
    noGroupsDescription = "Sem grupos.",
    randomObject = "\uD83C\uDFB2 Aleatório",
    drawLabel = "\uD83C\uDFA8",
)

val EsMultiplyingWithGroupsLevel2Strings = MultiplyingWithGroupsLevel2Strings(
    rulesTitle = "Multiplicando con Grupos (4 a 7)",
    rulesText = "Este juego te ayuda a ver la multiplicación como grupos iguales. A la izquierda aparece una multiplicación; a la derecha, su resultado se muestra junto con un dibujo de grupos de caracteres.\n\nCada rectángulo de la derecha es un grupo. Todos los rectángulos contienen tantos caracteres como el primer factor, y el número de rectángulos es el segundo factor. Por ejemplo, 4 \u00D7 6 muestra cuatro caracteres en cada uno de seis rectángulos: eso es seis grupos de cuatro, y el resultado es 24.\n\nEl juego tiene cuatro niveles:\n\n- **Nivel 1**: dos multiplicaciones de la tabla del 4.\n- **Nivel 2**: dos multiplicaciones de la tabla del 5.\n- **Nivel 3**: dos multiplicaciones de la tabla del 6.\n- **Nivel 4**: dos multiplicaciones de la tabla del 7.\n\nEncima del juego, un selector elige la imagen usada en los grupos: **\uD83C\uDFB2 Aleatorio** dibuja un objeto aleatorio de las tablas de multiplicar en cada nuevo ejercicio, o puedes seleccionar una imagen concreta — las mismas usadas en las tablas de multiplicar — y todos los grupos la usan mientras juegas.\n\nPara jugar, selecciona una multiplicación de la izquierda y luego su resultado de la derecha. Si la pareja es correcta, ambos quedan resaltados. Cuando aciertes todas las parejas, completarás el nivel y podrás pasar al siguiente.",
    gameTitle = "El Juego",
    gameIntro = "Une cada multiplicación de la izquierda con su resultado de la derecha. El resultado también se muestra como grupos de caracteres, así puedes contarlos para comprobar tu respuesta. Si una pareja es incorrecta, un mensaje te pedirá que lo intentes de nuevo.",
    levelFormat = "Nivel %d/%d",
    playAgain = "Jugar de Nuevo",
    correctMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 ¡Correcto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n¡Acertaste todas las parejas de este nivel!",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 ¡Perfecto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n¡Acertaste todas las multiplicaciones!",
    wrongPairMessage = "%d no es el resultado de %d \u00D7 %d. Cuenta los caracteres e inténtalo de nuevo.",
    groupsDescription = "Grupos: %d. Caracteres por grupo: %d.",
    noGroupsDescription = "Sin grupos.",
    randomObject = "\uD83C\uDFB2 Aleatorio",
    drawLabel = "\uD83C\uDFA8",
)

val LocalMultiplyingWithGroupsLevel2Strings = staticCompositionLocalOf { EnMultiplyingWithGroupsLevel2Strings }

fun multiplyingWithGroupsLevel2StringsForLanguage(language: String): MultiplyingWithGroupsLevel2Strings = when (language) {
    "pt-BR" -> PtMultiplyingWithGroupsLevel2Strings
    "es-ES" -> EsMultiplyingWithGroupsLevel2Strings
    else -> EnMultiplyingWithGroupsLevel2Strings
}
