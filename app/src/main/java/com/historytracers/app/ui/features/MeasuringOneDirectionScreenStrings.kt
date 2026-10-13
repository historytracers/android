// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MeasuringOneDirectionScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
    val scoreDoubledMessage: String,
    val labelA: String,
    val labelB: String,
    val labelC: String,
    val stringsEqual: String,
    val stringsDifferent: String,
)

val EnMeasuringOneDirectionScreenStrings = MeasuringOneDirectionScreenStrings(
    title = "Measuring in one direction",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new, so we are going to reward you with 1 point in your score.",
    scoreDoubledMessage = "Great! Because you answered correctly, your score for this screen will be doubled. Keep paying attention and learning!",
    labelA = "A",
    labelB = "B",
    labelC = "C",
    stringsEqual = "%1\$s and %2\$s are equal.",
    stringsDifferent = "%1\$s and %2\$s are different.",
)

val PtMeasuringOneDirectionScreenStrings = MeasuringOneDirectionScreenStrings(
    title = "Medindo em uma direção",
    wrongAnswerMessage = "Esta não é a resposta esperada. Mas você aprendeu algo novo, então vamos recompensá-lo com 1 ponto na sua pontuação.",
    scoreDoubledMessage = "Ótimo! Como você respondeu corretamente, sua pontuação nesta tela será dobrada. Continue prestando atenção e aprendendo!",
    labelA = "A",
    labelB = "B",
    labelC = "C",
    stringsEqual = "%1\$s e %2\$s são iguais.",
    stringsDifferent = "%1\$s e %2\$s são diferentes.",
)

val EsMeasuringOneDirectionScreenStrings = MeasuringOneDirectionScreenStrings(
    title = "Midiendo en una dirección",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo, así que vamos a recompensarte con 1 punto en tu puntuación.",
    scoreDoubledMessage = "¡Genial! Como respondiste correctamente, tu puntuación en esta pantalla se duplicará. ¡Sigue prestando atención y aprendiendo!",
    labelA = "A",
    labelB = "B",
    labelC = "C",
    stringsEqual = "%1\$s y %2\$s son iguales.",
    stringsDifferent = "%1\$s y %2\$s son diferentes.",
)

val LocalMeasuringOneDirectionScreenStrings = staticCompositionLocalOf { EnMeasuringOneDirectionScreenStrings }

fun measuringOneDirectionScreenStringsForLanguage(language: String): MeasuringOneDirectionScreenStrings = when (language) {
    "pt-BR" -> PtMeasuringOneDirectionScreenStrings
    "es-ES" -> EsMeasuringOneDirectionScreenStrings
    else -> EnMeasuringOneDirectionScreenStrings
}
