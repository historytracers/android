// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class TotallyEqualScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
    val scoreDoubledMessage: String,
)

val EnTotallyEqualScreenStrings = TotallyEqualScreenStrings(
    title = "Totally Equal",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new in your life, so we are going to reward you with 1 point in your score.",
    scoreDoubledMessage = "Great! Because you answered correctly, your score for this screen will be doubled. Keep paying attention and learning!",
)

val PtTotallyEqualScreenStrings = TotallyEqualScreenStrings(
    title = "Totalmente igual",
    wrongAnswerMessage = "Esta n\u00e3o \u00e9 a resposta esperada. Mas voc\u00ea aprendeu algo novo na sua vida, ent\u00e3o vamos recompens\u00e1-lo com 1 ponto na sua pontua\u00e7\u00e3o.",
    scoreDoubledMessage = "\u00d3timo! Como voc\u00ea respondeu corretamente, sua pontua\u00e7\u00e3o nesta tela ser\u00e1 dobrada. Continue prestando aten\u00e7\u00e3o e aprendendo!",
)

val EsTotallyEqualScreenStrings = TotallyEqualScreenStrings(
    title = "Totalmente iguales",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo en tu vida, as\u00ed que vamos a recompensarte con 1 punto en tu puntuaci\u00f3n.",
    scoreDoubledMessage = "\u00a1Genial! Como respondiste correctamente, tu puntuaci\u00f3n en esta pantalla se duplicar\u00e1. \u00a1Sigue prestando atenci\u00f3n y aprendiendo!",
)

val LocalTotallyEqualScreenStrings = staticCompositionLocalOf { EnTotallyEqualScreenStrings }

fun totallyEqualScreenStringsForLanguage(language: String): TotallyEqualScreenStrings = when (language) {
    "pt-BR" -> PtTotallyEqualScreenStrings
    "es-ES" -> EsTotallyEqualScreenStrings
    else -> EnTotallyEqualScreenStrings
}
