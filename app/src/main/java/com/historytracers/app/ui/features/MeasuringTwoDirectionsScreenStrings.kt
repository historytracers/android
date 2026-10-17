// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MeasuringTwoDirectionsScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
    val scoreDoubledMessage: String,
    val labelOne: String,
    val labelTwo: String,
    val length: String,
    val height: String,
    val rectsEqual: String,
    val rectsSameLengthDiffHeight: String,
    val rectsDiffLengthSameHeight: String,
    val rectsDiffLengthDiffHeight: String,
)

val EnMeasuringTwoDirectionsScreenStrings = MeasuringTwoDirectionsScreenStrings(
    title = "Measuring in two directions",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new, so we are going to reward you with 1 point in your score.",
    scoreDoubledMessage = "Great! Because you answered correctly, your score for this screen will be doubled. Keep paying attention and learning!",
    labelOne = "1",
    labelTwo = "2",
    length = "Length",
    height = "Height",
    rectsEqual = "Rectangle 1 and rectangle 2 are fully equal: they have the same length (%d) and the same height (%d).",
    rectsSameLengthDiffHeight = "Rectangle 1 and rectangle 2 are not fully equal: they have the same length (%d), but different heights (%d and %d).",
    rectsDiffLengthSameHeight = "Rectangle 1 and rectangle 2 are not fully equal: they have the same height (%d), but different lengths (%d and %d).",
    rectsDiffLengthDiffHeight = "Rectangle 1 and rectangle 2 are not fully equal: they have different lengths (%d and %d) and different heights (%d and %d).",
)

val PtMeasuringTwoDirectionsScreenStrings = MeasuringTwoDirectionsScreenStrings(
    title = "Medindo em duas direções",
    wrongAnswerMessage = "Esta não é a resposta esperada. Mas você aprendeu algo novo, então vamos recompensá-lo com 1 ponto na sua pontuação.",
    scoreDoubledMessage = "Ótimo! Como você respondeu corretamente, sua pontuação nesta tela será dobrada. Continue prestando atenção e aprendendo!",
    labelOne = "1",
    labelTwo = "2",
    length = "Comprimento",
    height = "Altura",
    rectsEqual = "O retângulo 1 e o retângulo 2 são totalmente iguais: eles têm o mesmo comprimento (%d) e a mesma altura (%d).",
    rectsSameLengthDiffHeight = "O retângulo 1 e o retângulo 2 não são totalmente iguais: eles têm o mesmo comprimento (%d), mas com alturas diferentes (%d e %d).",
    rectsDiffLengthSameHeight = "O retângulo 1 e o retângulo 2 não são totalmente iguais: eles têm a mesma altura (%d), mas com comprimentos diferentes (%d e %d).",
    rectsDiffLengthDiffHeight = "O retângulo 1 e o retângulo 2 não são totalmente iguais: eles têm comprimentos diferentes (%d e %d) e alturas diferentes (%d e %d).",
)

val EsMeasuringTwoDirectionsScreenStrings = MeasuringTwoDirectionsScreenStrings(
    title = "Midiendo en dos direcciones",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo, así que vamos a recompensarte con 1 punto en tu puntuación.",
    scoreDoubledMessage = "¡Genial! Como respondiste correctamente, tu puntuación en esta pantalla se duplicará. ¡Sigue prestando atención y aprendiendo!",
    labelOne = "1",
    labelTwo = "2",
    length = "Longitud",
    height = "Altura",
    rectsEqual = "El rectángulo 1 y el rectángulo 2 son totalmente iguales: tienen la misma longitud (%d) y la misma altura (%d).",
    rectsSameLengthDiffHeight = "El rectángulo 1 y el rectángulo 2 no son totalmente iguales: tienen la misma longitud (%d), pero con alturas diferentes (%d y %d).",
    rectsDiffLengthSameHeight = "El rectángulo 1 y el rectángulo 2 no son totalmente iguales: tienen la misma altura (%d), pero con longitudes diferentes (%d y %d).",
    rectsDiffLengthDiffHeight = "El rectángulo 1 y el rectángulo 2 no son totalmente iguales: tienen longitudes diferentes (%d y %d) y alturas diferentes (%d y %d).",
)

val LocalMeasuringTwoDirectionsScreenStrings = staticCompositionLocalOf { EnMeasuringTwoDirectionsScreenStrings }

fun measuringTwoDirectionsScreenStringsForLanguage(language: String): MeasuringTwoDirectionsScreenStrings = when (language) {
    "pt-BR" -> PtMeasuringTwoDirectionsScreenStrings
    "es-ES" -> EsMeasuringTwoDirectionsScreenStrings
    else -> EnMeasuringTwoDirectionsScreenStrings
}
