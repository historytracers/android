// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class OddOrEvenClassScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
    val scoreDoubledMessage: String,
)

val EnOddOrEvenClassScreenStrings = OddOrEvenClassScreenStrings(
    title = "Odd or Even?",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new, so we are going to reward you with 1 point in your score.",
    scoreDoubledMessage = "Great! Because you answered correctly, your score for this screen will be doubled. Keep paying attention and learning!",
)

val PtOddOrEvenClassScreenStrings = OddOrEvenClassScreenStrings(
    title = "Par ou Ímpar?",
    wrongAnswerMessage = "Esta não é a resposta esperada. Mas você aprendeu algo novo, então vamos recompensá-lo com 1 ponto na sua pontuação.",
    scoreDoubledMessage = "Ótimo! Como você respondeu corretamente, sua pontuação nesta tela será dobrada. Continue prestando atenção e aprendendo!",
)

val EsOddOrEvenClassScreenStrings = OddOrEvenClassScreenStrings(
    title = "¿Par o Impar?",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo, así que vamos a recompensarte con 1 punto en tu puntuación.",
    scoreDoubledMessage = "¡Genial! Como respondiste correctamente, tu puntuación en esta pantalla se duplicará. ¡Sigue prestando atención y aprendiendo!",
)

val LocalOddOrEvenClassScreenStrings = staticCompositionLocalOf { EnOddOrEvenClassScreenStrings }

fun oddOrEvenClassScreenStringsForLanguage(language: String): OddOrEvenClassScreenStrings = when (language) {
    "pt-BR" -> PtOddOrEvenClassScreenStrings
    "es-ES" -> EsOddOrEvenClassScreenStrings
    else -> EnOddOrEvenClassScreenStrings
}
