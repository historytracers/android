// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplicationTableMyBodyScreenStrings(
    val title: String,
    val fullTitle: String,
    val figureCaption: String,
    val wrongAnswerMessage: String,
    val scoreDoubledMessage: String,
)

val EnMultiplicationTableMyBodyScreenStrings = MultiplicationTableMyBodyScreenStrings(
    title = "My Body, as a Multiplication Table",
    fullTitle = "Multiplication Table (My Body)",
    figureCaption = "Figure 1: our hands and our feet, each one with five fingers or five toes.",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new in your life, so we are going to reward you with 1 point in your score.",
    scoreDoubledMessage = "Great! Because you answered correctly, your score for this screen will be doubled. Keep paying attention and learning!",
)

val PtMultiplicationTableMyBodyScreenStrings = MultiplicationTableMyBodyScreenStrings(
    title = "Meu Corpo, como uma Tabuada",
    fullTitle = "Tabuada (Meu Corpo)",
    figureCaption = "Figura 1: as nossas m\u00e3os e os nossos p\u00e9s, cada um com cinco dedos.",
    wrongAnswerMessage = "Esta n\u00e3o \u00e9 a resposta esperada. Mas voc\u00ea aprendeu algo novo na sua vida, ent\u00e3o vamos recompens\u00e1-lo com 1 ponto na sua pontua\u00e7\u00e3o.",
    scoreDoubledMessage = "\u00d3timo! Como voc\u00ea respondeu corretamente, sua pontua\u00e7\u00e3o nesta tela ser\u00e1 dobrada. Continue prestando aten\u00e7\u00e3o e aprendendo!",
)

val EsMultiplicationTableMyBodyScreenStrings = MultiplicationTableMyBodyScreenStrings(
    title = "Mi Cuerpo, como una Tabla de Multiplicar",
    fullTitle = "Tabla de Multiplicar (Mi Cuerpo)",
    figureCaption = "Figura 1: nuestras manos y nuestros pies, cada uno con cinco dedos.",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo en tu vida, as\u00ed que vamos a recompensarte con 1 punto en tu puntuaci\u00f3n.",
    scoreDoubledMessage = "\u00a1Genial! Como respondiste correctamente, tu puntuaci\u00f3n en esta pantalla se duplicar\u00e1. \u00a1Sigue prestando atenci\u00f3n y aprendiendo!",
)

val LocalMultiplicationTableMyBodyScreenStrings =
    staticCompositionLocalOf { EnMultiplicationTableMyBodyScreenStrings }

fun multiplicationTableMyBodyScreenStringsForLanguage(
    language: String
): MultiplicationTableMyBodyScreenStrings = when (language) {
    "pt-BR" -> PtMultiplicationTableMyBodyScreenStrings
    "es-ES" -> EsMultiplicationTableMyBodyScreenStrings
    else -> EnMultiplicationTableMyBodyScreenStrings
}
