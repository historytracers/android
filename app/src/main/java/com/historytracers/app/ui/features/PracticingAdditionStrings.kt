// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PracticingAdditionStrings(
    val practicingAddition: String,
    val complementToTen: String,
    val practicingAdditionInstruction: String,
    val stepWriteFirst: String,
    val stepAddTo: String,
    val stepCarrying: String,
    val stepFinal: String,
    val feedbackPerfect: String,
    val feedbackCorrect: String,
    val feedbackCongratulations: String,
)

val EnPracticingAdditionStrings = PracticingAdditionStrings(
    practicingAddition = "Practicing Addition",
    complementToTen = "Complement to 10",
    practicingAdditionInstruction = "Set the value shown in each step by tapping the beads on the abacus above. Complete all steps to finish the exercise.",
    stepWriteFirst = "Step 1: Write the first number %d in the abacus column(s) (%s).",
    stepAddTo = "Add to the %s: Add %d to the %s column. After this addition, the abacus should show %d.",
    stepCarrying = "Carrying 1: Adding %d to %s gives %d + %d = %d. Remove the complement %d from the %s column. Add 1 to the %s column. After these movements, the abacus should show %d.",
    stepFinal = "Final: %d + %d = %d. The abacus should show this sum!",
    feedbackPerfect = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\nGreat job using the abacus!",
    feedbackCorrect = "\u2705 Correct! Click 'Next step' to continue.",
    feedbackCongratulations = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CONGRATULATIONS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou calculated %d + %d = %d\nClick 'New exercise' to practice more!",
)

val PtPracticingAdditionStrings = PracticingAdditionStrings(
    practicingAddition = "Praticando Adi\u00e7\u00e3o",
    complementToTen = "Complemento de 10",
    practicingAdditionInstruction = "Defina o valor mostrado em cada passo tocando nas contas do \u00e1baco acima. Complete todos os passos para finalizar o exerc\u00edcio.",
    stepWriteFirst = "Passo 1: Escreva o primeiro n\u00famero %d na(s) coluna(s) do \u00e1baco (%s).",
    stepAddTo = "Adicione \u00e0 %s: Adicione %d \u00e0 coluna %s. Ap\u00f3s esta adi\u00e7\u00e3o, o \u00e1baco deve mostrar %d.",
    stepCarrying = "Carregando 1: Adicionando %d \u00e0 %s resulta em %d + %d = %d. Remova o complemento %d da coluna %s. Adicione 1 \u00e0 coluna %s. Ap\u00f3s esses movimentos, o \u00e1baco deve mostrar %d.",
    stepFinal = "Final: %d + %d = %d. O \u00e1baco deve mostrar esta soma!",
    feedbackPerfect = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFEITO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\n\u00d3timo trabalho usando o \u00e1baco!",
    feedbackCorrect = "\u2705 Correto! Clique em 'Pr\u00f3ximo passo' para continuar.",
    feedbackCongratulations = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PARAB\u00c9NS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea calculou %d + %d = %d\nClique em 'Novo exerc\u00edcio' para praticar mais!",
)

val EsPracticingAdditionStrings = PracticingAdditionStrings(
    practicingAddition = "Practicando Suma",
    complementToTen = "Complemento de 10",
    practicingAdditionInstruction = "Establece el valor mostrado en cada paso tocando las cuentas del \u00e1baco de arriba. Completa todos los pasos para finalizar el ejercicio.",
    stepWriteFirst = "Paso 1: Escribe el primer n\u00famero %d en la(s) columna(s) del \u00e1baco (%s).",
    stepAddTo = "Agrega a %s: Agrega %d a la columna %s. Despu\u00e9s de esta suma, el \u00e1baco debe mostrar %d.",
    stepCarrying = "Llevando 1: Sumando %d a %s da %d + %d = %d. Retira el complemento %d de la columna %s. Agrega 1 a la columna %s. Despu\u00e9s de estos movimientos, el \u00e1baco debe mostrar %d.",
    stepFinal = "Final: %d + %d = %d. \u00a1El \u00e1baco debe mostrar esta suma!",
    feedbackPerfect = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1PERFECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\n\u00a1Gran trabajo usando el \u00e1baco!",
    feedbackCorrect = "\u2705 \u00a1Correcto! Haz clic en 'Siguiente paso' para continuar.",
    feedbackCongratulations = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1FELICITACIONES! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nHas calculado %d + %d = %d\n\u00a1Haz clic en 'Nuevo ejercicio' para practicar m\u00e1s!",
)

val LocalPracticingAdditionStrings = staticCompositionLocalOf { EnPracticingAdditionStrings }

fun practicingAdditionStringsForLanguage(language: String): PracticingAdditionStrings = when (language) {
    "pt-BR" -> PtPracticingAdditionStrings
    "es-ES" -> EsPracticingAdditionStrings
    else -> EnPracticingAdditionStrings
}
