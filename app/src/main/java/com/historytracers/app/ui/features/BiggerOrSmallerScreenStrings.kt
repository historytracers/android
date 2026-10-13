// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class BiggerOrSmallerScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
    val scoreDoubledMessage: String,
)

val EnBiggerOrSmallerScreenStrings = BiggerOrSmallerScreenStrings(
    title = "Bigger or Smaller",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new, so we are going to reward you with 1 point in your score.",
    scoreDoubledMessage = "Great! Because you answered correctly, your score for this screen will be doubled. Keep paying attention and learning!",
)

val PtBiggerOrSmallerScreenStrings = BiggerOrSmallerScreenStrings(
    title = "Maior ou Menor",
    wrongAnswerMessage = "Esta não é a resposta esperada. Mas você aprendeu algo novo, então vamos recompensá-lo com 1 ponto na sua pontuação.",
    scoreDoubledMessage = "Ótimo! Como você respondeu corretamente, sua pontuação nesta tela será dobrada. Continue prestando atenção e aprendendo!",
)

val EsBiggerOrSmallerScreenStrings = BiggerOrSmallerScreenStrings(
    title = "Mayor o Menor",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo, así que vamos a recompensarte con 1 punto en tu puntuación.",
    scoreDoubledMessage = "¡Genial! Como respondiste correctamente, tu puntuación en esta pantalla se duplicará. ¡Sigue prestando atención y aprendiendo!",
)

val LocalBiggerOrSmallerScreenStrings = staticCompositionLocalOf { EnBiggerOrSmallerScreenStrings }

fun biggerOrSmallerScreenStringsForLanguage(language: String): BiggerOrSmallerScreenStrings = when (language) {
    "pt-BR" -> PtBiggerOrSmallerScreenStrings
    "es-ES" -> EsBiggerOrSmallerScreenStrings
    else -> EnBiggerOrSmallerScreenStrings
}
