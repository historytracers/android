// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class EqualSameGroupDifferentScreenStrings(
    val title: String,
    val instruction: String,
    val equal: String,
    val sameGroup: String,
    val neither: String,
    val wrongMessage: String,
    val levelCompleteMessage: String,
    val gameCompleteMessage: String,
    val questionWord: String,
    val ofWord: String,
    val restart: String,
    val playAgain: String,
    val levelNames: List<String>,
)

val EnEqualSameGroupDifferentScreenStrings = EqualSameGroupDifferentScreenStrings(
    title = "Equal, Same Group, or Different?",
    instruction = "The game shows two elements: numbers, figures (circles and squares), or images. You must decide how they relate by choosing one of the three buttons:\n\n- **Equal** (light green button): the two elements are exactly the same.\n- **Same group** (light yellow button): the two elements are not exactly the same, but they belong to the same group.\n- **Neither** (light red button): the two elements are neither equal nor in the same group.\n\nThe game has seven levels. In the first three levels, you compare numbers and only two answers are possible: **Equal** or **Neither**. From the fourth level onward, figures and images appear, and the three answers are used.",
    equal = "Equal",
    sameGroup = "Same Group",
    neither = "Neither",
    wrongMessage = "Not quite, try again.",
    levelCompleteMessage = "Level complete!",
    gameCompleteMessage = "Game complete! Congratulations!",
    questionWord = "Question",
    ofWord = "of",
    restart = "Restart",
    playAgain = "Play Again",
    levelNames = listOf(
        "Numbers from 0 to 9",
        "Numbers from 10 to 1,000,000",
        "Maya Numbers",
        "Numbers, Circles and Squares",
        "Numbers, Circles and Squares",
        "Numbers, Circles and Squares",
        "Egypt, Mesoamerica and Pyramids",
    ),
)

val PtEqualSameGroupDifferentScreenStrings = EqualSameGroupDifferentScreenStrings(
    title = "Iguais, mesmo grupo ou diferentes?",
    instruction = "O jogo mostra dois elementos: n\u00fameros, figuras (c\u00edrculos e quadrados) ou imagens. Voc\u00ea deve decidir como eles se relacionam escolhendo um dos tr\u00eas bot\u00f5es:\n\n- **Iguais** (bot\u00e3o verde-claro): ambos os elementos s\u00e3o exatamente iguais.\n- **Mesmo grupo** (bot\u00e3o amarelo-claro): ambos os elementos n\u00e3o s\u00e3o exatamente iguais, mas pertencem ao mesmo grupo.\n- **Diferentes** (bot\u00e3o vermelho-claro): ambos os elementos n\u00e3o s\u00e3o iguais nem do mesmo grupo.\n\nO jogo tem sete n\u00edveis. Nos tr\u00eas primeiros n\u00edveis, voc\u00ea compara n\u00fameros e apenas duas respostas s\u00e3o poss\u00edveis: **Iguais** ou **Diferentes**. A partir do quarto n\u00edvel, figuras e imagens aparecem, e as tr\u00eas respostas s\u00e3o usadas.",
    equal = "Iguais",
    sameGroup = "Mesmo grupo",
    neither = "Diferentes",
    wrongMessage = "Quase, tente novamente.",
    levelCompleteMessage = "N\u00edvel conclu\u00eddo!",
    gameCompleteMessage = "Jogo conclu\u00eddo! Parab\u00e9ns!",
    questionWord = "Pergunta",
    ofWord = "de",
    restart = "Reiniciar",
    playAgain = "Jogar novamente",
    levelNames = listOf(
        "N\u00fameros de 0 a 9",
        "N\u00fameros de 10 a 1.000.000",
        "N\u00fameros maias",
        "N\u00fameros, c\u00edrculos e quadrados",
        "N\u00fameros, c\u00edrculos e quadrados",
        "N\u00fameros, c\u00edrculos e quadrados",
        "Egito, Mesoam\u00e9rica e pir\u00e2mides",
    ),
)

val EsEqualSameGroupDifferentScreenStrings = EqualSameGroupDifferentScreenStrings(
    title = "\u00bfIguales, mismo grupo o diferentes?",
    instruction = "El juego muestra dos elementos: n\u00fameros, figuras (c\u00edrculos y cuadrados) o im\u00e1genes. Debes decidir c\u00f3mo se relacionan eligiendo uno de los tres botones:\n\n- **Iguales** (bot\u00f3n verde claro): ambos elementos son exactamente iguales.\n- **Mismo grupo** (bot\u00f3n amarillo claro): ambos elementos no son exactamente iguales, pero pertenecen al mismo grupo.\n- **Diferentes** (bot\u00f3n rojo claro): ambos elementos no son iguales ni del mismo grupo.\n\nEl juego tiene siete niveles. En los tres primeros niveles, comparas n\u00fameros y solo dos respuestas son posibles: **Iguales** o **Diferentes**. A partir del cuarto nivel, aparecen figuras e im\u00e1genes, y se usan las tres respuestas.",
    equal = "Iguales",
    sameGroup = "Mismo grupo",
    neither = "Diferentes",
    wrongMessage = "Casi, int\u00e9ntalo de nuevo.",
    levelCompleteMessage = "\u00a1Nivel completado!",
    gameCompleteMessage = "\u00a1Juego completado! \u00a1Felicidades!",
    questionWord = "Pregunta",
    ofWord = "de",
    restart = "Reiniciar",
    playAgain = "Jugar de nuevo",
    levelNames = listOf(
        "N\u00fameros del 0 al 9",
        "N\u00fameros del 10 al 1.000.000",
        "N\u00fameros mayas",
        "N\u00fameros, c\u00edrculos y cuadrados",
        "N\u00fameros, c\u00edrculos y cuadrados",
        "N\u00fameros, c\u00edrculos y cuadrados",
        "Egipto, Mesoam\u00e9rica y pir\u00e1mides",
    ),
)

val LocalEqualSameGroupDifferentScreenStrings = staticCompositionLocalOf { EnEqualSameGroupDifferentScreenStrings }

fun equalSameGroupDifferentScreenStringsForLanguage(language: String): EqualSameGroupDifferentScreenStrings = when (language) {
    "pt-BR" -> PtEqualSameGroupDifferentScreenStrings
    "es-ES" -> EsEqualSameGroupDifferentScreenStrings
    else -> EnEqualSameGroupDifferentScreenStrings
}
