// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PlayingWithAxiomsScreenStrings(
    val title: String,
    val explanation: String,
    val levelWord: String,
    val exerciseWord: String,
    val of: String,
    val scoreProgress: String,
    val equationLabel: String,
    val arrowsLabel: String,
    val restart: String,
    val playAgain: String,
    val level1Desc: String,
    val level2Desc: String,
    val level3Desc: String,
    val instr1: String,
    val instr2: String,
    val instr3: String,
    val increase: String,
    val decrease: String,
    val gameComplete: String,
)

val EnPlayingWithAxiomsScreenStrings = PlayingWithAxiomsScreenStrings(
    title = "Practicing the Axioms of Addition",
    explanation = "In this game, you will complete the missing values to understand an axiom of addition.",
    levelWord = "Level",
    exerciseWord = "Exercise",
    of = "of",
    scoreProgress = "Score: %d/%d",
    equationLabel = "Equation",
    arrowsLabel = "Arrows",
    restart = "Restart",
    playAgain = "Play again",
    level1Desc = "a + 0 = a",
    level2Desc = "a + b = b + a (Part 1)",
    level3Desc = "a + b = b + a (Part 2)",
    instr1 = "Use the arrows to set the value on the right to make both sides of the equation equal.",
    instr2 = "Use the arrows to fill in the missing value on the right to make both sides equal.",
    instr3 = "Use the arrows to fill in both values on the right in reversed order to make both sides equal.",
    increase = "Increase",
    decrease = "Decrease",
    gameComplete = "Game complete! Congratulations!",
)

val PtPlayingWithAxiomsScreenStrings = PlayingWithAxiomsScreenStrings(
    title = "Praticando os axiomas da adi\u00e7\u00e3o",
    explanation = "Neste jogo, voc\u00ea completar\u00e1 os valores que faltam para entender um axioma da adi\u00e7\u00e3o.",
    levelWord = "N\u00edvel",
    exerciseWord = "Exerc\u00edcio",
    of = "de",
    scoreProgress = "Pontua\u00e7\u00e3o: %d/%d",
    equationLabel = "Equa\u00e7\u00e3o",
    arrowsLabel = "Setas",
    restart = "Reiniciar",
    playAgain = "Jogar novamente",
    level1Desc = "a + 0 = a",
    level2Desc = "a + b = b + a (Parte 1)",
    level3Desc = "a + b = b + a (Parte 2)",
    instr1 = "Use as setas para definir o valor \u00e0 direita e fazer os dois lados da equa\u00e7\u00e3o ficarem iguais.",
    instr2 = "Use as setas para completar o valor que falta \u00e0 direita e fazer os dois lados ficarem iguais.",
    instr3 = "Use as setas para preencher os dois valores \u00e0 direita em ordem inversa e fazer os dois lados ficarem iguais.",
    increase = "Aumentar",
    decrease = "Diminuir",
    gameComplete = "Jogo conclu\u00eddo! Parab\u00e9ns!",
)

val EsPlayingWithAxiomsScreenStrings = PlayingWithAxiomsScreenStrings(
    title = "Practicando los axiomas de la suma",
    explanation = "En este juego, completar\u00e1s los valores que faltan para entender un axioma de la suma.",
    levelWord = "Nivel",
    exerciseWord = "Ejercicio",
    of = "de",
    scoreProgress = "Puntuaci\u00f3n: %d/%d",
    equationLabel = "Ecuaci\u00f3n",
    arrowsLabel = "Flechas",
    restart = "Reiniciar",
    playAgain = "Jugar de nuevo",
    level1Desc = "a + 0 = a",
    level2Desc = "a + b = b + a (Parte 1)",
    level3Desc = "a + b = b + a (Parte 2)",
    instr1 = "Usa las flechas para fijar el valor de la derecha y que ambos lados de la ecuaci\u00f3n sean iguales.",
    instr2 = "Usa las flechas para completar el valor que falta a la derecha y que ambos lados sean iguales.",
    instr3 = "Usa las flechas para rellenar los dos valores de la derecha en orden inverso y que ambos lados sean iguales.",
    increase = "Aumentar",
    decrease = "Disminuir",
    gameComplete = "\u00a1Juego completado! \u00a1Felicidades!",
)

val LocalPlayingWithAxiomsScreenStrings = staticCompositionLocalOf { EnPlayingWithAxiomsScreenStrings }

fun playingWithAxiomsScreenStringsForLanguage(language: String): PlayingWithAxiomsScreenStrings = when (language) {
    "pt-BR" -> PtPlayingWithAxiomsScreenStrings
    "es-ES" -> EsPlayingWithAxiomsScreenStrings
    else -> EnPlayingWithAxiomsScreenStrings
}
