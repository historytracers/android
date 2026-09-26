// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class ReverseMovementScreenStrings(
    val title: String,
    val before: String,
    val after: String,
    val pichanaEquation32: String,
    val pichanaEquation21: String,
    val cancellationEquation5: String,
    val cancellationEquation3: String,
    val cancellationEquation2: String,
    val cancellationEquation1: String,
    val wrongAnswerMessage: String,
    val scoreDoubledMessage: String,
)

val EnReverseMovementScreenStrings = ReverseMovementScreenStrings(
    title = "Reverse Movements",
    before = "Before",
    after = "After",
    pichanaEquation32 = "3 \u2212 2 = 1",
    pichanaEquation21 = "2 \u2212 1 = 1",
    cancellationEquation5 = "5 \u2212 5 = 0",
    cancellationEquation3 = "3 \u2212 3 = 0",
    cancellationEquation2 = "2 \u2212 2 = 0",
    cancellationEquation1 = "1 \u2212 1 = 0",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new in your life, so we are going to reward you with 1 point in your score.",
    scoreDoubledMessage = "Great! Because you answered correctly, your score for this screen will be doubled. Keep paying attention and learning!",
)

val PtReverseMovementScreenStrings = ReverseMovementScreenStrings(
    title = "Movimentos Reversos",
    before = "Antes",
    after = "Depois",
    pichanaEquation32 = "3 \u2212 2 = 1",
    pichanaEquation21 = "2 \u2212 1 = 1",
    cancellationEquation5 = "5 \u2212 5 = 0",
    cancellationEquation3 = "3 \u2212 3 = 0",
    cancellationEquation2 = "2 \u2212 2 = 0",
    cancellationEquation1 = "1 \u2212 1 = 0",
    wrongAnswerMessage = "Esta n\u00e3o \u00e9 a resposta esperada. Mas voc\u00ea aprendeu algo novo na sua vida, ent\u00e3o vamos recompens\u00e1-lo com 1 ponto na sua pontua\u00e7\u00e3o.",
    scoreDoubledMessage = "\u00d3timo! Como voc\u00ea respondeu corretamente, sua pontua\u00e7\u00e3o nesta tela ser\u00e1 dobrada. Continue prestando aten\u00e7\u00e3o e aprendendo!",
)

val EsReverseMovementScreenStrings = ReverseMovementScreenStrings(
    title = "Movimientos Inversos",
    before = "Antes",
    after = "Despu\u00e9s",
    pichanaEquation32 = "3 \u2212 2 = 1",
    pichanaEquation21 = "2 \u2212 1 = 1",
    cancellationEquation5 = "5 \u2212 5 = 0",
    cancellationEquation3 = "3 \u2212 3 = 0",
    cancellationEquation2 = "2 \u2212 2 = 0",
    cancellationEquation1 = "1 \u2212 1 = 0",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo en tu vida, as\u00ed que vamos a recompensarte con 1 punto en tu puntuaci\u00f3n.",
    scoreDoubledMessage = "\u00a1Genial! Como respondiste correctamente, tu puntuaci\u00f3n en esta pantalla se duplicar\u00e1. \u00a1Sigue prestando atenci\u00f3n y aprendiendo!",
)

val LocalReverseMovementScreenStrings = staticCompositionLocalOf { EnReverseMovementScreenStrings }

fun reverseMovementScreenStringsForLanguage(language: String): ReverseMovementScreenStrings = when (language) {
    "pt-BR" -> PtReverseMovementScreenStrings
    "es-ES" -> EsReverseMovementScreenStrings
    else -> EnReverseMovementScreenStrings
}
