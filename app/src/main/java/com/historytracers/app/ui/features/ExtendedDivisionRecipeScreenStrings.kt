// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class ExtendedDivisionRecipeScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
    val scoreDoubledMessage: String,
    val formatLabel: String,
    val resultBelow: String,
    val resultTop: String,
    val tableTitle: String,
    val tableRangeCaption: String,
    val divisionCaption: String,
)

val EnExtendedDivisionRecipeScreenStrings = ExtendedDivisionRecipeScreenStrings(
    title = "Extended Division Recipe",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new in your life.",
    scoreDoubledMessage = "Great! Because you answered correctly, your score for this screen will be doubled. Keep paying attention and learning!",
    formatLabel = "Division format:",
    resultBelow = "Result below",
    resultTop = "Result on top",
    tableTitle = "Multiplication Table of 5",
    tableRangeCaption = "The results range from 0 to 50.",
    divisionCaption = "Division of 320 by 5.",
)

val PtExtendedDivisionRecipeScreenStrings = ExtendedDivisionRecipeScreenStrings(
    title = "Receita Estendida da Divis\u00E3o",
    wrongAnswerMessage = "Esta n\u00E3o \u00E9 a resposta esperada. Mas voc\u00EA aprendeu algo novo na sua vida.",
    scoreDoubledMessage = "\u00D3timo! Como voc\u00EA respondeu corretamente, sua pontua\u00E7\u00E3o nesta tela ser\u00E1 dobrada. Continue prestando aten\u00E7\u00E3o e aprendendo!",
    formatLabel = "Formato da divis\u00E3o:",
    resultBelow = "Resultado abaixo",
    resultTop = "Resultado acima",
    tableTitle = "Tabuada do 5",
    tableRangeCaption = "Os resultados v\u00E3o de 0 a 50.",
    divisionCaption = "Divis\u00E3o de 320 por 5.",
)

val EsExtendedDivisionRecipeScreenStrings = ExtendedDivisionRecipeScreenStrings(
    title = "Receta Extendida de la Divisi\u00F3n",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo en tu vida.",
    scoreDoubledMessage = "\u00A1Genial! Como respondiste correctamente, tu puntuaci\u00F3n en esta pantalla se duplicar\u00E1. \u00A1Sigue prestando atenci\u00F3n y aprendiendo!",
    formatLabel = "Formato de la divisi\u00F3n:",
    resultBelow = "Resultado debajo",
    resultTop = "Resultado encima",
    tableTitle = "Tabla de Multiplicar del 5",
    tableRangeCaption = "Los resultados van de 0 a 50.",
    divisionCaption = "Divisi\u00F3n de 320 entre 5.",
)

val LocalExtendedDivisionRecipeScreenStrings =
    staticCompositionLocalOf { EnExtendedDivisionRecipeScreenStrings }

fun extendedDivisionRecipeScreenStringsForLanguage(language: String): ExtendedDivisionRecipeScreenStrings = when (language) {
    "pt-BR" -> PtExtendedDivisionRecipeScreenStrings
    "es-ES" -> EsExtendedDivisionRecipeScreenStrings
    else -> EnExtendedDivisionRecipeScreenStrings
}
