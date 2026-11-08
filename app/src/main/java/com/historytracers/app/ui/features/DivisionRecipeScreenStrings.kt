// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class DivisionRecipeScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
    val scoreDoubledMessage: String,
    val formatLabel: String,
    val resultBelow: String,
    val resultTop: String,
    val groupsCaption: String,
)

val EnDivisionRecipeScreenStrings = DivisionRecipeScreenStrings(
    title = "Division Recipe",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new in your life.",
    scoreDoubledMessage = "Great! Because you answered correctly, your score for this screen will be doubled. Keep paying attention and learning!",
    formatLabel = "Division format:",
    resultBelow = "Result below",
    resultTop = "Result on top",
    groupsCaption = "Four groups of three squares: 4 \u00D7 3 = 12.",
)

val PtDivisionRecipeScreenStrings = DivisionRecipeScreenStrings(
    title = "Receita da Divis\u00E3o",
    wrongAnswerMessage = "Esta n\u00E3o \u00E9 a resposta esperada. Mas voc\u00EA aprendeu algo novo na sua vida.",
    scoreDoubledMessage = "\u00D3timo! Como voc\u00EA respondeu corretamente, sua pontua\u00E7\u00E3o nesta tela ser\u00E1 dobrada. Continue prestando aten\u00E7\u00E3o e aprendendo!",
    formatLabel = "Formato da divis\u00E3o:",
    resultBelow = "Resultado abaixo",
    resultTop = "Resultado acima",
    groupsCaption = "Quatro grupos de tr\u00EAs quadrados: 4 \u00D7 3 = 12.",
)

val EsDivisionRecipeScreenStrings = DivisionRecipeScreenStrings(
    title = "Receta de la Divisi\u00F3n",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo en tu vida.",
    scoreDoubledMessage = "\u00A1Genial! Como respondiste correctamente, tu puntuaci\u00F3n en esta pantalla se duplicar\u00E1. \u00A1Sigue prestando atenci\u00F3n y aprendiendo!",
    formatLabel = "Formato de la divisi\u00F3n:",
    resultBelow = "Resultado debajo",
    resultTop = "Resultado encima",
    groupsCaption = "Cuatro grupos de tres cuadrados: 4 \u00D7 3 = 12.",
)

val LocalDivisionRecipeScreenStrings = staticCompositionLocalOf { EnDivisionRecipeScreenStrings }

fun divisionRecipeScreenStringsForLanguage(language: String): DivisionRecipeScreenStrings = when (language) {
    "pt-BR" -> PtDivisionRecipeScreenStrings
    "es-ES" -> EsDivisionRecipeScreenStrings
    else -> EnDivisionRecipeScreenStrings
}
