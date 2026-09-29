// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PracticingSubtractionYupanaScreenStrings(
    val title: String,
    val instruction: String,
    val levelUnits: String,
    val levelTens: String,
    val levelHundreds: String,
    val stepPrefix: String,
    val stepFirstDigit: String,
    val stepSecondDigit: String,
    val correctMessage: String,
    val noMovementsMessage: String,
    val movementsMessage: String,
    val borrowInstruction: String,
    val borrowConfirmMessage: String,
    val evalMessage: String,
    val perfectMessage: String,
    val lastLevelMessage: String,
)

val EnPracticingSubtractionYupanaScreenStrings = PracticingSubtractionYupanaScreenStrings(
    title = "Practicing Subtraction with Yupana",
    instruction = "Tap the Yupana cells to place the markers for each digit. When the row is correct, click \"Next Step\". Use \"New Exercise\" to restart or \"Next Level\" to advance.",
    levelUnits = "Units",
    levelTens = "Tens",
    levelHundreds = "Hundreds",
    stepPrefix = "\uD83E\uDDEE",
    stepFirstDigit = "Place the first number's digit in the %s: %d",
    stepSecondDigit = "Place the second number's digit in the %s: %d",
    correctMessage = "\u2705 Correct! Click 'Next Step' to continue.",
    noMovementsMessage = "\u2705 Correct! No movement needed in the %s. Click 'Next Step' to continue.",
    movementsMessage = "\u2705 Correct! Movement(s) performed in the %s: %s. Click 'Next Step' to continue.",
    borrowInstruction = "The %s does not have enough markers to subtract. Borrow 1 from the next place: rewrite the markers, then click 'Next Step'.",
    borrowConfirmMessage = "\u2705 Correct! You borrowed 1 from the next place. Click 'Next Step' to continue.",
    evalMessage = "Resolve the %s: %d \u2212 %d = %d. Place the result markers.",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d \u2212 %d = %d\nGreat job using the Yupana!",
    lastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 ALL LEVELS COMPLETE! \uD83C\uDFC6\uD83C\uDF89\nYou mastered subtraction on the Yupana! Click \"New Exercise\" to start again at the first level.",
)

val PtPracticingSubtractionYupanaScreenStrings = PracticingSubtractionYupanaScreenStrings(
    title = "Praticando Subtra\u00e7\u00e3o com a Yupana",
    instruction = "Toque nas c\u00e9lulas da Yupana para colocar os marcadores de cada d\u00edgito. Quando a linha estiver correta, clique em \"Pr\u00f3ximo Passo\". Use \"Novo Exerc\u00edcio\" para reiniciar ou \"Pr\u00f3ximo N\u00edvel\" para avan\u00e7ar.",
    levelUnits = "Unidades",
    levelTens = "Dezenas",
    levelHundreds = "Centenas",
    stepPrefix = "\uD83E\uDDEE",
    stepFirstDigit = "Coloque o d\u00edgito do primeiro n\u00famero em %s: %d",
    stepSecondDigit = "Coloque o d\u00edgito do segundo n\u00famero em %s: %d",
    correctMessage = "\u2705 Correto! Clique em 'Pr\u00f3ximo Passo' para continuar.",
    noMovementsMessage = "\u2705 Correto! Nenhum movimento necess\u00e1rio em %s. Clique em 'Pr\u00f3ximo Passo' para continuar.",
    movementsMessage = "\u2705 Correto! Movimento(s) realizado(s) em %s: %s. Clique em 'Pr\u00f3ximo Passo' para continuar.",
    borrowInstruction = "A casa %s n\u00e3o tem marcadores suficientes para subtrair. Pe\u00e7a 1 emprestado \u00e0 pr\u00f3xima casa: reescreva os marcadores e clique em 'Pr\u00f3ximo Passo'.",
    borrowConfirmMessage = "\u2705 Correto! Voc\u00ea pediu 1 emprestado \u00e0 pr\u00f3xima casa. Clique em 'Pr\u00f3ximo Passo' para continuar.",
    evalMessage = "Resolva a casa %s: %d \u2212 %d = %d. Coloque os marcadores do resultado.",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFEITO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d \u2212 %d = %d\n\u00d3timo trabalho usando a Yupana!",
    lastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 TODOS OS N\u00cdVEIS CONCLU\u00cdDOS! \uD83C\uDFC6\uD83C\uDF89\nVoc\u00ea dominou a subtra\u00e7\u00e3o na Yupana! Clique em \"Novo Exerc\u00edcio\" para come\u00e7ar de novo no primeiro n\u00edvel.",
)

val EsPracticingSubtractionYupanaScreenStrings = PracticingSubtractionYupanaScreenStrings(
    title = "Practicando Resta con la Yupana",
    instruction = "Toca las celdas de la Yupana para colocar los marcadores de cada d\u00edgito. Cuando la fila sea correcta, haz clic en \"Siguiente Paso\". Usa \"Nuevo Ejercicio\" para reiniciar o \"Siguiente Nivel\" para avanzar.",
    levelUnits = "Unidades",
    levelTens = "Decenas",
    levelHundreds = "Centenas",
    stepPrefix = "\uD83E\uDDEE",
    stepFirstDigit = "Coloca el d\u00edgito del primer n\u00famero en %s: %d",
    stepSecondDigit = "Coloca el d\u00edgito del segundo n\u00famero en %s: %d",
    correctMessage = "\u2705 \u00a1Correcto! Haz clic en 'Siguiente Paso' para continuar.",
    noMovementsMessage = "\u2705 \u00a1Correcto! No se necesita movimiento en %s. Haz clic en 'Siguiente Paso' para continuar.",
    movementsMessage = "\u2705 \u00a1Correcto! Movimiento(s) realizado(s) en %s: %s. Haz clic en 'Siguiente Paso' para continuar.",
    borrowInstruction = "La casilla %s no tiene marcadores suficientes para restar. Pide 1 prestado a la siguiente posici\u00f3n: reescribe los marcadores y haz clic en 'Siguiente Paso'.",
    borrowConfirmMessage = "\u2705 \u00a1Correcto! Pediste 1 prestado a la siguiente posici\u00f3n. Haz clic en 'Siguiente Paso' para continuar.",
    evalMessage = "Resuelve la casilla %s: %d \u2212 %d = %d. Coloca los marcadores del resultado.",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1PERFECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d \u2212 %d = %d\n\u00a1Gran trabajo usando la Yupana!",
    lastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 \u00a1TODOS LOS NIVELES COMPLETADOS! \uD83C\uDFC6\uD83C\uDF89\n\u00a1Dominaste la resta en la Yupana! Haz clic en \"Nuevo Ejercicio\" para empezar de nuevo en el primer nivel.",
)

val LocalPracticingSubtractionYupanaScreenStrings = staticCompositionLocalOf { EnPracticingSubtractionYupanaScreenStrings }

fun practicingSubtractionYupanaScreenStringsForLanguage(language: String): PracticingSubtractionYupanaScreenStrings = when (language) {
    "pt-BR" -> PtPracticingSubtractionYupanaScreenStrings
    "es-ES" -> EsPracticingSubtractionYupanaScreenStrings
    else -> EnPracticingSubtractionYupanaScreenStrings
}
