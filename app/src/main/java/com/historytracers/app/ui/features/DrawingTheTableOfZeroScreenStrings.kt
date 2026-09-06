// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class DrawingTheTableOfZeroScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
    val scoreDoubledMessage: String,
)

val EnDrawingTheTableOfZeroScreenStrings = DrawingTheTableOfZeroScreenStrings(
    title = "Drawing the Table of Zero",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new in your life, so we are going to reward you with 1 point in your score.",
    scoreDoubledMessage = "Great! Because you answered correctly, your score for this screen will be doubled. Keep paying attention and learning!",
)

val PtDrawingTheTableOfZeroScreenStrings = DrawingTheTableOfZeroScreenStrings(
    title = "Desenhando a Tabuada do Zero",
    wrongAnswerMessage = "Esta não é a resposta esperada. Mas você aprendeu algo novo na sua vida, então vamos recompensá-lo com 1 ponto na sua pontuação.",
    scoreDoubledMessage = "Ótimo! Como você respondeu corretamente, sua pontuação nesta tela será dobrada. Continue prestando atenção e aprendendo!",
)

val EsDrawingTheTableOfZeroScreenStrings = DrawingTheTableOfZeroScreenStrings(
    title = "Dibujando la Tabla del Cero",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo en tu vida, así que vamos a recompensarte con 1 punto en tu puntuación.",
    scoreDoubledMessage = "¡Genial! Como respondiste correctamente, tu puntuación en esta pantalla se duplicará. ¡Sigue prestando atención y aprendiendo!",
)

val LocalDrawingTheTableOfZeroScreenStrings = staticCompositionLocalOf { EnDrawingTheTableOfZeroScreenStrings }

fun drawingTheTableOfZeroScreenStringsForLanguage(language: String): DrawingTheTableOfZeroScreenStrings = when (language) {
    "pt-BR" -> PtDrawingTheTableOfZeroScreenStrings
    "es-ES" -> EsDrawingTheTableOfZeroScreenStrings
    else -> EnDrawingTheTableOfZeroScreenStrings
}
