// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PracticingAdditionRoadScreenStrings(
    val title: String,
    val instruction: String,
    val sumOfNumbers: String,
    val controls: String,
    val tableDescription: String,
    val addStepIntro: String,
    val addStepContinue: String,
    val addStepCarry: String,
    val repeat: String,
    val congratsMessage: String,
)

val EnPracticingAdditionRoadScreenStrings = PracticingAdditionRoadScreenStrings(
    title = "Practicing Addition",
    instruction = "Reading about addition is helpful, but practice is essential for full understanding. Below is a table with an addition problem. Before using the controls (\u25B2 \u25BC) to check the result, try solving it on your own.\n\nWhen you finish the calculation, you will receive a medal (\uD83E\uDD47) and can proceed to another problem by clicking the button (\u25B6) below:",
    sumOfNumbers = "Sum of Numbers",
    controls = "Controls",
    tableDescription = "Table 1: Exercise for Adding Two Numbers.",
    addStepIntro = "In this exercise, we will perform the addition operation: %d + %d.\nAddition is always performed in pairs. At this stage, we are adding: (%d + %d).",
    addStepContinue = "Addition is always performed in pairs. At this stage, we are adding: (%d + %d).",
    addStepCarry = "Addition is always performed in pairs. At this stage, we are adding: (%d + %d) + %d.\nEach numerical position can only contain digits between 0 and 9. As a result, the excess value (1) was carried over to the next higher position.",
    repeat = "Repeat",
    congratsMessage = "%d + %d = %d\nYou finished the addition! Click 'Repeat' to reinforce or 'New exercise' for another problem.",
)

val PtPracticingAdditionRoadScreenStrings = PracticingAdditionRoadScreenStrings(
    title = "Praticando Adi\u00e7\u00e3o",
    instruction = "A leitura de um texto pode parecer suficiente, mas a pr\u00e1tica \u00e9 essencial para o entendimento completo. Na tabela a seguir, propomos a soma de dois n\u00fameros. Antes de utilizar as setas laterais (\u25B2 \u25BC) para conferir o resultado, tente calcul\u00e1-la.\n\nAo concluir, voc\u00ea receber\u00e1 uma medalha (\uD83E\uDD47) e poder\u00e1 fazer um novo c\u00e1lculo clicando no bot\u00e3o correspondente (\u25B6):",
    sumOfNumbers = "Some os n\u00fameros",
    controls = "Controles",
    tableDescription = "Tabela 1: Exerc\u00edcio de soma de dois n\u00fameros.",
    addStepIntro = "Neste exerc\u00edcio, realizaremos a opera\u00e7\u00e3o de adi\u00e7\u00e3o: %d + %d.\nA soma \u00e9 sempre feita em pares. Neste momento, estamos somando: (%d + %d).",
    addStepContinue = "A soma \u00e9 sempre feita em pares. Neste momento, estamos somando: (%d + %d).",
    addStepCarry = "A soma \u00e9 sempre feita em pares. Neste momento, estamos somando: (%d + %d) + %d.\nCada posi\u00e7\u00e3o num\u00e9rica pode conter apenas d\u00edgitos de 0 a 9. Por isso, o valor excedente (1) foi transferido para a pr\u00f3xima posi\u00e7\u00e3o superior.",
    repeat = "Repetir",
    congratsMessage = "%d + %d = %d\nVoc\u00ea concluiu a adi\u00e7\u00e3o! Clique em 'Repetir' para refor\u00e7ar ou 'Novo exerc\u00edcio' para outra.",
)

val EsPracticingAdditionRoadScreenStrings = PracticingAdditionRoadScreenStrings(
    title = "Practicando Suma",
    instruction = "Leer un texto puede parecer suficiente, pero la pr\u00e1ctica es esencial para una comprensi\u00f3n completa. En la siguiente tabla, te proponemos la suma de dos n\u00fameros. Antes de usar las flechas laterales (\u25B2 \u25BC) para verificar el resultado, intenta calcularlo por tu cuenta.\n\nAl finalizar, recibir\u00e1s una medalla (\uD83E\uDD47) y podr\u00e1s realizar un nuevo c\u00e1lculo haciendo clic en el bot\u00f3n correspondiente (\u25B6):",
    sumOfNumbers = "Suma de n\u00fameros",
    controls = "Controles",
    tableDescription = "Tabla 1: Ejercicio de suma de dos n\u00fameros.",
    addStepIntro = "En este ejercicio, efectuaremos la operaci\u00f3n de adici\u00f3n: %d + %d.\nLa suma siempre se realiza en pares. En este momento, estamos sumando: (%d + %d).",
    addStepContinue = "La suma siempre se realiza en pares. En este momento, estamos sumando: (%d + %d).",
    addStepCarry = "La suma siempre se realiza en pares. En este momento, estamos sumando: (%d + %d) + %d.\nCada posici\u00f3n num\u00e9rica solo puede contener d\u00edgitos del 0 al 9. Por ello, el valor excedente (1) se traslad\u00f3 a la siguiente posici\u00f3n.",
    repeat = "Repetir",
    congratsMessage = "%d + %d = %d\n\u00a1Has terminado la suma! Haz clic en 'Repetir' para reforzar o 'Nuevo ejercicio' para otra.",
)

val LocalPracticingAdditionRoadScreenStrings = staticCompositionLocalOf { EnPracticingAdditionRoadScreenStrings }

fun practicingAdditionRoadScreenStringsForLanguage(language: String): PracticingAdditionRoadScreenStrings = when (language) {
    "pt-BR" -> PtPracticingAdditionRoadScreenStrings
    "es-ES" -> EsPracticingAdditionRoadScreenStrings
    else -> EnPracticingAdditionRoadScreenStrings
}
