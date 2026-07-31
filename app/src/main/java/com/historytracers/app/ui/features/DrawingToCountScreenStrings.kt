// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class DrawingToCountScreenStrings(
    val drawingToCountDescription: String,
)

val EnDrawingToCountScreenStrings = DrawingToCountScreenStrings(
    drawingToCountDescription = "On this screen, we will learn how numbers are represented on the Yupana. Using the arrows below, observe how numbers grow from right to left. Also, observe that the dots on the screen can be compared with the tips of our fingers. Finally, observe that on each Yupana line, it is not possible to represent numbers greater than 9.",
)

val PtDrawingToCountScreenStrings = DrawingToCountScreenStrings(
    drawingToCountDescription = "Nesta tela, aprenderemos como os n\u00fameros s\u00e3o representados na Yupana. Usando as setas abaixo, observe como os n\u00fameros crescem da direita para a esquerda. Observe tamb\u00e9m que os pontos na tela podem ser comparados com a ponta dos nossos dedos. Finalmente, observe que em cada linha da Yupana, n\u00e3o \u00e9 poss\u00edvel representar n\u00fameros maiores que 9.",
)

val EsDrawingToCountScreenStrings = DrawingToCountScreenStrings(
    drawingToCountDescription = "En esta pantalla, aprenderemos c\u00f3mo se representan los n\u00fameros en la Yupana. Usando las flechas de abajo, observa c\u00f3mo los n\u00fameros crecen de derecha a izquierda. Observa tambi\u00e9n que los puntos en la pantalla pueden compararse con la punta de nuestros dedos. Finalmente, observa que en cada l\u00ednea de la Yupana, no es posible representar n\u00fameros mayores que 9.",
)

val LocalDrawingToCountScreenStrings = staticCompositionLocalOf { EnDrawingToCountScreenStrings }

fun drawingToCountScreenStringsForLanguage(language: String): DrawingToCountScreenStrings = when (language) {
    "pt-BR" -> PtDrawingToCountScreenStrings
    "es-ES" -> EsDrawingToCountScreenStrings
    else -> EnDrawingToCountScreenStrings
}
