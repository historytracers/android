// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class TheResultIsScreenStrings(
    val title: String,
    val instruction: String,
    val multiplyStep: String,
    val additionStep: String,
    val tableTitle: String,
    val tableLine: String,
    val congratsMessage: String,
    val repeat: String,
    val medal: String,
    val increase: String,
    val decrease: String,
)

val EnTheResultIsScreenStrings = TheResultIsScreenStrings(
    title = "The Result Is",
    instruction = "This game helps you practice the connection between addition and multiplication: multiplying is adding the same number many times.\n\nFor each calculation below, first try to find the result in your head. Then use the side arrows (\u25B2 \u25BC) to reach the answer \u2014 each press of the upper arrow adds the repeated number. When you finish the three calculations, the complete times table of that number is revealed.",
    multiplyStep = "We are multiplying: %d \u00D7 %d.\nMultiplying is adding the same number many times. Try to calculate the result in your head first, then use the \u25B2 arrow to add %d until you reach the answer.",
    additionStep = "We keep adding %d: %d + %d.\nTry to calculate the result in your head first, then use the \u25B2 arrow to add %d until you reach the answer.",
    tableTitle = "%d Times Table",
    tableLine = "%d \u00D7 %d = %d",
    congratsMessage = "You completed the three calculations!\nBelow is the complete %d times table. Notice that each result is the previous one plus %d.",
    repeat = "Repeat",
    medal = "\uD83E\uDD47",
    increase = "Increase",
    decrease = "Decrease",
)

val PtTheResultIsScreenStrings = TheResultIsScreenStrings(
    title = "O resultado \u00e9",
    instruction = "Este jogo ajuda voc\u00ea a praticar a conex\u00e3o entre adi\u00e7\u00e3o e multiplica\u00e7\u00e3o: multiplicar \u00e9 somar o mesmo n\u00famero muitas vezes.\n\nPara cada c\u00e1lculo abaixo, tente primeiro encontrar o resultado mentalmente. Depois use as setas laterais (\u25B2 \u25BC) para chegar \u00e0 resposta \u2014 cada toque na seta superior soma o n\u00famero repetido. Ao terminar os tr\u00eas c\u00e1lculos, a tabuada completa desse n\u00famero \u00e9 revelada.",
    multiplyStep = "Vamos multiplicar: %d \u00D7 %d.\nMultiplicar \u00e9 somar o mesmo n\u00famero muitas vezes. Tente calcular o resultado mentalmente primeiro e depois use a seta \u25B2 para somar %d at\u00e9 chegar \u00e0 resposta.",
    additionStep = "Continuamos somando %d: %d + %d.\nTente calcular o resultado mentalmente primeiro e depois use a seta \u25B2 para somar %d at\u00e9 chegar \u00e0 resposta.",
    tableTitle = "Tabuada do %d",
    tableLine = "%d \u00D7 %d = %d",
    congratsMessage = "Voc\u00ea completou os tr\u00eas c\u00e1lculos!\nAbaixo est\u00e1 a tabuada completa do %d. Observe que cada resultado \u00e9 o anterior mais %d.",
    repeat = "Repetir",
    medal = "\uD83E\uDD47",
    increase = "Aumentar",
    decrease = "Diminuir",
)

val EsTheResultIsScreenStrings = TheResultIsScreenStrings(
    title = "El resultado es",
    instruction = "Este juego te ayuda a practicar la conexi\u00f3n entre la suma y la multiplicaci\u00f3n: multiplicar es sumar el mismo n\u00famero muchas veces.\n\nPara cada c\u00e1lculo de abajo, intenta primero encontrar el resultado mentalmente. Luego usa las flechas laterales (\u25B2 \u25BC) para llegar a la respuesta \u2014 cada pulsaci\u00f3n de la flecha superior suma el n\u00famero repetido. Cuando termines los tres c\u00e1lculos, se revela la tabla de multiplicar completa de ese n\u00famero.",
    multiplyStep = "Vamos a multiplicar: %d \u00D7 %d.\nMultiplicar es sumar el mismo n\u00famero muchas veces. Intenta calcular el resultado mentalmente primero y luego usa la flecha \u25B2 para sumar %d hasta llegar a la respuesta.",
    additionStep = "Seguimos sumando %d: %d + %d.\nIntenta calcular el resultado mentalmente primero y luego usa la flecha \u25B2 para sumar %d hasta llegar a la respuesta.",
    tableTitle = "Tabla de multiplicar del %d",
    tableLine = "%d \u00D7 %d = %d",
    congratsMessage = "\u00a1Completaste los tres c\u00e1lculos!\nAbajo est\u00e1 la tabla de multiplicar completa del %d. Observa que cada resultado es el anterior m\u00e1s %d.",
    repeat = "Repetir",
    medal = "\uD83E\uDD47",
    increase = "Aumentar",
    decrease = "Disminuir",
)

val LocalTheResultIsScreenStrings = staticCompositionLocalOf { EnTheResultIsScreenStrings }

fun theResultIsScreenStringsForLanguage(language: String): TheResultIsScreenStrings = when (language) {
    "pt-BR" -> PtTheResultIsScreenStrings
    "es-ES" -> EsTheResultIsScreenStrings
    else -> EnTheResultIsScreenStrings
}
