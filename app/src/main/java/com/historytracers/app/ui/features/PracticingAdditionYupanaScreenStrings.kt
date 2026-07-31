// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PracticingAdditionYupanaScreenStrings(
    val practicingWithYupana: String,
    val practicingAdditionYupanaInstruction: String,
    val yupanaValues: String,
    val yupanaResult: String,
    val yupanaStepByStep: String,
    val moves: String,
)

val EnPracticingAdditionYupanaScreenStrings = PracticingAdditionYupanaScreenStrings(
    practicingWithYupana = "Practicing with the Yupana (Addition)",
    practicingAdditionYupanaInstruction = "To better understand the Yupana Inka Tawa Pukllay method, we will use a Yupana with 4 rows to add two numbers. With this configuration, we can represent values up to 4,999. However, it is possible to add more rows to add larger numbers, without limitations.\n\nIn the following example, we initially demonstrate the addition of 512 + 513. We invite you to switch between the three options to better understand the Yupana Inka Tawa Pukllay. Each option presents the following:\n- Values: shows how the numbers are written on the Yupana;\n- Result: shows the final result of the addition operation;\n- Step by Step: performs the addition row by row.\n\nYou can also modify the values in the example to reinforce your learning. After changing them, first click the Values button to display the numbers, and then see how they are represented on the Yupana.",
    yupanaValues = "Values",
    yupanaResult = "Result",
    yupanaStepByStep = "Step by Step",
    moves = "Moves",
)

val PtPracticingAdditionYupanaScreenStrings = PracticingAdditionYupanaScreenStrings(
    practicingWithYupana = "Exercitando com a Yupana (Adi\u00e7\u00e3o)",
    practicingAdditionYupanaInstruction = "Para compreender melhor o m\u00e9todo Yupana Inka Tawa Pukllay, utilizaremos uma Yupana com 4 linhas para somar dois n\u00fameros. Com essa configura\u00e7\u00e3o, podemos representar valores de at\u00e9 4.999. No entanto, \u00e9 poss\u00edvel adicionar mais linhas para somar n\u00fameros maiores, sem limita\u00e7\u00f5es.\n\nNo exemplo a seguir, demonstramos inicialmente a soma de 512 + 513. Convidamos voc\u00ea a alternar entre as tr\u00eas op\u00e7\u00f5es para compreender melhor o m\u00e9todo Yupana Inka Tawa Pukllay. Cada op\u00e7\u00e3o apresenta o seguinte:\n- Valores: mostra como os n\u00fameros s\u00e3o escritos na Yupana;\n- Calcular: mostra o resultado final da opera\u00e7\u00e3o de soma;\n- Passo a Passo: executa a soma linha por linha.\n\nVoc\u00ea tamb\u00e9m pode modificar os valores do exemplo para refor\u00e7ar seu aprendizado. Ap\u00f3s alter\u00e1-los, clique primeiro no bot\u00e3o Valores para exibir os n\u00fameros e, em seguida, veja como eles s\u00e3o representados na Yupana.",
    yupanaValues = "Valores",
    yupanaResult = "Calcular",
    yupanaStepByStep = "Passo a Passo",
    moves = "Movimentos",
)

val EsPracticingAdditionYupanaScreenStrings = PracticingAdditionYupanaScreenStrings(
    practicingWithYupana = "Ejercitando con la Yupana (Adici\u00f3n)",
    practicingAdditionYupanaInstruction = "Para comprender mejor el m\u00e9todo Yupana Inka Tawa Pukllay, utilizaremos una Yupana con 4 filas para sumar dos n\u00fameros. Con esta configuraci\u00f3n, podemos representar valores de hasta 4.999. Sin embargo, es posible agregar m\u00e1s filas para sumar n\u00fameros mayores, sin limitaciones.\n\nEn el siguiente ejemplo, demostramos inicialmente la suma de 512 + 513. Te invitamos a alternar entre las tres opciones para comprender mejor el m\u00e9todo Yupana Inka Tawa Pukllay. Cada opci\u00f3n presenta lo siguiente:\n- Valores: muestra c\u00f3mo se escriben los n\u00fameros en la Yupana;\n- Calcular: muestra el resultado final de la operaci\u00f3n de suma;\n- Paso a Paso: ejecuta la suma fila por fila.\n\nTambi\u00e9n puedes modificar los valores del ejemplo para reforzar tu aprendizaje. Despu\u00e9s de cambiarlos, haz clic primero en el bot\u00f3n Valores para mostrar los n\u00fameros y, a continuaci\u00f3n, observa c\u00f3mo se representan en la Yupana.",
    yupanaValues = "Valores",
    yupanaResult = "Calcular",
    yupanaStepByStep = "Paso a Paso",
    moves = "Movimientos",
)

val LocalPracticingAdditionYupanaScreenStrings = staticCompositionLocalOf { EnPracticingAdditionYupanaScreenStrings }

fun practicingAdditionYupanaScreenStringsForLanguage(language: String): PracticingAdditionYupanaScreenStrings = when (language) {
    "pt-BR" -> PtPracticingAdditionYupanaScreenStrings
    "es-ES" -> EsPracticingAdditionYupanaScreenStrings
    else -> EnPracticingAdditionYupanaScreenStrings
}
