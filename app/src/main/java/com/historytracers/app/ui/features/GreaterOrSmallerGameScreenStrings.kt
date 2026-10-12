// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class GreaterOrSmallerGameScreenStrings(
    val title: String,
    val instruction: String,
    val inOrder: String,
    val notInOrder: String,
    val labelA: String,
    val labelB: String,
    val correctBigger: String,
    val correctMuchBigger: String,
    val wrongSmaller: String,
    val wrongMuchSmaller: String,
    val finalCongratulations: String,
    val playAgain: String,
)

val EnGreaterOrSmallerGameScreenStrings = GreaterOrSmallerGameScreenStrings(
    title = "Greater or Smaller (The Game)",
    instruction = "Are the numbers in order? In other words, is the right number bigger than the left one?",
    inOrder = "In order",
    notInOrder = "Not in order",
    labelA = "A",
    labelB = "B",
    correctBigger = "\uD83C\uDF89 Congratulations! Number %1\$s is bigger than %2\$s (%2\$s < %1\$s).",
    correctMuchBigger = "\uD83C\uDF89 Congratulations! Number %1\$s is much bigger than %2\$s (%1\$s \u226B %2\$s).",
    wrongSmaller = "Let us remember: The number %1\$s is smaller than number %2\$s. (%1\$s < %2\$s).",
    wrongMuchSmaller = "Let us remember: The number %1\$s is much smaller than number %2\$s. (%1\$s \u226A %2\$s).",
    finalCongratulations = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Congratulations! You completed all the levels! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89",
    playAgain = "Play Again",
)

val PtGreaterOrSmallerGameScreenStrings = GreaterOrSmallerGameScreenStrings(
    title = "Maior ou Menor (O Jogo)",
    instruction = "Os números estão em ordem? Ou seja, o número da direita é maior que o da esquerda?",
    inOrder = "Em ordem",
    notInOrder = "Fora de ordem",
    labelA = "A",
    labelB = "B",
    correctBigger = "\uD83C\uDF89 Parabéns! O número %1\$s é maior que %2\$s (%2\$s < %1\$s).",
    correctMuchBigger = "\uD83C\uDF89 Parabéns! O número %1\$s é muito maior que %2\$s (%1\$s \u226B %2\$s).",
    wrongSmaller = "Vamos relembrar: O número %1\$s é menor que o número %2\$s. (%1\$s < %2\$s).",
    wrongMuchSmaller = "Vamos relembrar: O número %1\$s é muito menor que o número %2\$s. (%1\$s \u226A %2\$s).",
    finalCongratulations = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Parabéns! Você completou todos os níveis! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89",
    playAgain = "Jogar Novamente",
)

val EsGreaterOrSmallerGameScreenStrings = GreaterOrSmallerGameScreenStrings(
    title = "Mayor o Menor (El Juego)",
    instruction = "¿Están los números en orden? Es decir, ¿el número de la derecha es mayor que el de la izquierda?",
    inOrder = "En orden",
    notInOrder = "Fuera de orden",
    labelA = "A",
    labelB = "B",
    correctBigger = "\uD83C\uDF89 ¡Felicitaciones! El número %1\$s es mayor que %2\$s (%2\$s < %1\$s).",
    correctMuchBigger = "\uD83C\uDF89 ¡Felicitaciones! El número %1\$s es mucho mayor que %2\$s (%1\$s \u226B %2\$s).",
    wrongSmaller = "Vamos a recordar: El número %1\$s es menor que el número %2\$s. (%1\$s < %2\$s).",
    wrongMuchSmaller = "Vamos a recordar: El número %1\$s es mucho menor que el número %2\$s. (%1\$s \u226A %2\$s).",
    finalCongratulations = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 ¡Felicitaciones! ¡Completaste todos los niveles! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89",
    playAgain = "Jugar de Nuevo",
)

val LocalGreaterOrSmallerGameScreenStrings = staticCompositionLocalOf { EnGreaterOrSmallerGameScreenStrings }

fun greaterOrSmallerGameScreenStringsForLanguage(language: String): GreaterOrSmallerGameScreenStrings = when (language) {
    "pt-BR" -> PtGreaterOrSmallerGameScreenStrings
    "es-ES" -> EsGreaterOrSmallerGameScreenStrings
    else -> EnGreaterOrSmallerGameScreenStrings
}
