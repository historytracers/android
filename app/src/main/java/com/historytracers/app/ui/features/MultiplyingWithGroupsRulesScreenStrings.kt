// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplyingWithGroupsRulesScreenStrings(
    val title: String,
    val rulesText: String,
)

val EnMultiplyingWithGroupsRulesScreenStrings = MultiplyingWithGroupsRulesScreenStrings(
    title = "Multiplying with Groups (0 to 3)",
    rulesText = "This game helps you see multiplication as equal groups. On the left, you will see a multiplication; on the right, its result appears together with a picture of groups of characters.\n\nEach rectangle on the right is one group. Every rectangle holds as many characters as the first factor, and the number of rectangles is the second factor. For example, 2 \u00D7 4 shows two characters in each of four rectangles — that is four groups of two, and the result is 8. When the first factor is 0, no characters appear, because zero times any number is zero.\n\nThe game has three levels:\n\n- **Level 1**: three multiplications — one from the 0 times table and two from the 1 times table.\n- **Level 2**: two multiplications, both from the 2 times table.\n- **Level 3**: two multiplications, both from the 3 times table.\n\nTo play, select a multiplication on the left and then its result on the right. If the pair is correct, both stay highlighted. When you match every pair, you complete the level and can move on to the next one.",
)

val PtMultiplyingWithGroupsRulesScreenStrings = MultiplyingWithGroupsRulesScreenStrings(
    title = "Multiplicando com Grupos (0 a 3)",
    rulesText = "Este jogo ajuda você a ver a multiplicação como grupos iguais. À esquerda aparece uma multiplicação; à direita, o seu resultado é mostrado junto com um desenho de grupos de caracteres.\n\nCada retângulo à direita é um grupo. Todos os retângulos contêm tantos caracteres quanto o primeiro fator, e o número de retângulos é o segundo fator. Por exemplo, 2 \u00D7 4 mostra dois caracteres em cada um de quatro retângulos: isso é quatro grupos de dois, e o resultado é 8. Quando o primeiro fator é 0, não aparecem caracteres, porque multiplicar qualquer número por 0 dá 0.\n\nO jogo tem três níveis:\n\n- **Nível 1**: três multiplicações — uma da tabuada do 0 e duas da tabuada do 1.\n- **Nível 2**: duas multiplicações, ambas da tabuada do 2.\n- **Nível 3**: duas multiplicações, ambas da tabuada do 3.\n\nPara jogar, selecione uma multiplicação à esquerda e depois o resultado à direita. Se o par estiver correto, ambos ficam destacados. Quando acertar todos os pares, você terá completado o nível e poderá avançar para o próximo.",
)

val EsMultiplyingWithGroupsRulesScreenStrings = MultiplyingWithGroupsRulesScreenStrings(
    title = "Multiplicando con Grupos (0 a 3)",
    rulesText = "Este juego te ayuda a ver la multiplicación como grupos iguales. A la izquierda aparece una multiplicación; a la derecha, su resultado se muestra junto con un dibujo de grupos de caracteres.\n\nCada rectángulo de la derecha es un grupo. Todos los rectángulos contienen tantos caracteres como el primer factor, y el número de rectángulos es el segundo factor. Por ejemplo, 2 \u00D7 4 muestra dos caracteres en cada uno de cuatro rectángulos: eso es cuatro grupos de dos, y el resultado es 8. Cuando el primer factor es 0, no aparecen caracteres, porque multiplicar cualquier número por 0 da 0.\n\nEl juego tiene tres niveles:\n\n- **Nivel 1**: tres multiplicaciones — una de la tabla del 0 y dos de la tabla del 1.\n- **Nivel 2**: dos multiplicaciones, ambas de la tabla del 2.\n- **Nivel 3**: dos multiplicaciones, ambas de la tabla del 3.\n\nPara jugar, selecciona una multiplicación de la izquierda y luego su resultado de la derecha. Si la pareja es correcta, ambos quedan resaltados. Cuando aciertes todas las parejas, completarás el nivel y podrás pasar al siguiente.",
)

val LocalMultiplyingWithGroupsRulesScreenStrings = staticCompositionLocalOf { EnMultiplyingWithGroupsRulesScreenStrings }

fun multiplyingWithGroupsRulesScreenStringsForLanguage(language: String): MultiplyingWithGroupsRulesScreenStrings = when (language) {
    "pt-BR" -> PtMultiplyingWithGroupsRulesScreenStrings
    "es-ES" -> EsMultiplyingWithGroupsRulesScreenStrings
    else -> EnMultiplyingWithGroupsRulesScreenStrings
}
