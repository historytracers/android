// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SbwStrings(
    val returningWithAbacus: String,
    val subtractingWithAbacus: String,
    val sbwTitle: String,
    val sbwInstruction: String,
    val sbwStepPrefix: String,
    val sbwSetupInstruction: String,
    val sbwSubStepInstruction: String,
    val sbwSubStepDesc: String,
    val sbwBorrowSetNine: String,
    val sbwBorrowReduce: String,
    val sbwBorrowSubUnits: String,
    val sbwFinalInstruction: String,
    val sbwCorrectMessage: String,
    val sbwPerfectMessage: String,
    val sbwCongratsMessage: String,
    val sbwLastLevelMessage: String,
    val sbwWelcomeMessage: String,
    val sbwStepStatus: String,
)

val EnSbwStrings = SbwStrings(
    returningWithAbacus = "Returning with Abacus",
    subtractingWithAbacus = "Subtracting with Abacus",
    sbwTitle = "Practicing Subtraction",
    sbwInstruction = "Set the abacus to show the initial number, then subtract each digit of the subtrahend by tapping the beads. Complete all steps to finish the subtraction.",
    sbwStepPrefix = "\uD83E\uDDEE ",
    sbwSetupInstruction = "\uD83D\uDCCC Step 1: Set the abacus to show %d.",
    sbwSubStepInstruction = "%s After this change, the abacus should show %d.",
    sbwSubStepDesc = "\u2796 Subtract %d from the %s column.",
    sbwBorrowSetNine = "\u26A1 Since %s is 0, borrow from %s: set %s to 9 (we keep 9 here; the remaining value passes to the next column).",
    sbwBorrowReduce = "\u26A1 Borrow from %s (reduce from %d to %d). The value removed will be passed to the next column.",
    sbwBorrowSubUnits = "\u26A1 Add the complement %d (10 \u2212 %d = %d) to the %s column (%s becomes %d).",
    sbwFinalInstruction = "\uD83C\uDF89 Final: %d \u2212 %d = %d. The abacus should show this result!",
    sbwCorrectMessage = "\u2705 Correct! Click 'Next step' to continue.",
    sbwPerfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d \u2212 %d = %d\nGreat job using the abacus!",
    sbwCongratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CONGRATULATIONS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou calculated %d \u2212 %d = %d\nClick 'New exercise' to practice more!",
    sbwLastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 ALL LEVELS COMPLETE! \uD83C\uDFC6\uD83C\uDF89\nYou mastered subtraction! Click \"New exercise\" to start again at level 1.",
    sbwWelcomeMessage = "Click 'New exercise' to start. Follow the instructions to arrange the abacus.",
    sbwStepStatus = "\uD83D\uDCD6 Step %d/%d",
)

val PtSbwStrings = SbwStrings(
    returningWithAbacus = "Retornando com \u00c1baco",
    subtractingWithAbacus = "Subtraindo com \u00c1baco",
    sbwTitle = "Praticando Subtra\u00e7\u00e3o",
    sbwInstruction = "Coloque o \u00e1baco para mostrar o n\u00famero inicial e subtraia cada d\u00edgito do subtraendo tocando nas contas. Complete todos os passos para finalizar a subtra\u00e7\u00e3o.",
    sbwStepPrefix = "\uD83E\uDDEE ",
    sbwSetupInstruction = "\uD83D\uDCCC Passo 1: Coloque o \u00e1baco para mostrar %d.",
    sbwSubStepInstruction = "%s Ap\u00f3s esta mudan\u00e7a, o \u00e1baco dever\u00e1 mostrar %d.",
    sbwSubStepDesc = "\u2796 Subtraia %d da coluna das %s.",
    sbwBorrowSetNine = "\u26A1 Como %s \u00e9 0, empreste de %s: coloque %s em 9 (mantemos 9 aqui; o valor restante passa para a pr\u00f3xima coluna).",
    sbwBorrowReduce = "\u26A1 Pegue emprestado das %s (reduza de %d para %d). O valor retirado ser\u00e1 passado para a pr\u00f3xima coluna.",
    sbwBorrowSubUnits = "\u26A1 Adicione o complemento %d (10 \u2212 %d = %d) \u00e0 coluna de %s (%s se torna %d).",
    sbwFinalInstruction = "\uD83C\uDF89 Final: %d \u2212 %d = %d. O \u00e1baco dever\u00e1 mostrar este resultado!",
    sbwCorrectMessage = "\u2705 Correto! Clique em 'Pr\u00f3ximo passo' para continuar.",
    sbwPerfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFEITO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d \u2212 %d = %d\n\u00d3timo trabalho usando o \u00e1baco!",
    sbwCongratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PARAB\u00c9NS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea calculou %d \u2212 %d = %d\nClique em 'Novo exerc\u00edcio' para praticar mais!",
    sbwLastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 TODOS OS N\u00cdVEIS CONCLU\u00cdDOS! \uD83C\uDFC6\uD83C\uDF89\nVoc\u00ea dominou a subtra\u00e7\u00e3o! Clique em \"Novo exerc\u00edcio\" para come\u00e7ar novamente no n\u00edvel 1.",
    sbwWelcomeMessage = "Clique em 'Novo exerc\u00edcio' para iniciar. Siga as instru\u00e7\u00f5es para organizar o \u00e1baco.",
    sbwStepStatus = "\uD83D\uDCD6 Passo %d/%d",
)

val EsSbwStrings = SbwStrings(
    returningWithAbacus = "Regresando con \u00c1baco",
    subtractingWithAbacus = "Restando con \u00c1baco",
    sbwTitle = "Practicando la Resta",
    sbwInstruction = "Coloca el \u00e1baco para mostrar el n\u00famero inicial y resta cada d\u00edgito del sustraendo tocando las cuentas. Completa todos los pasos para finalizar la resta.",
    sbwStepPrefix = "\uD83E\uDDEE ",
    sbwSetupInstruction = "\uD83D\uDCCC Paso 1: Coloca el \u00e1baco para mostrar %d.",
    sbwSubStepInstruction = "%s Despu\u00e9s de este cambio, el \u00e1baco deber\u00eda mostrar %d.",
    sbwSubStepDesc = "\u2796 Resta %d de la columna de las %s.",
    sbwBorrowSetNine = "\u26A1 Como %s es 0, pide prestado de %s: pon %s en 9 (mantenemos 9 aqu\u00ed; el valor restante pasa a la siguiente columna).",
    sbwBorrowReduce = "\u26A1 Pide prestado de las %s (reduce de %d a %d). El valor quitado ser\u00e1 pasado a la siguiente columna.",
    sbwBorrowSubUnits = "\u26A1 A\u00f1ade el complemento %d (10 \u2212 %d = %d) a la columna de %s (%s se convierte en %d).",
    sbwFinalInstruction = "\uD83C\uDF89 Final: %d \u2212 %d = %d. \u00a1El \u00e1baco deber\u00eda mostrar este resultado!",
    sbwCorrectMessage = "\u2705 \u00a1Correcto! Haz clic en 'Siguiente paso' para continuar.",
    sbwPerfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1PERFECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d \u2212 %d = %d\n\u00a1Gran trabajo usando el \u00e1baco!",
    sbwCongratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1FELICIDADES! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nHas calculado %d \u2212 %d = %d\n\u00a1Haz clic en 'Nuevo ejercicio' para practicar m\u00e1s!",
    sbwLastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 \u00a1TODOS LOS NIVELES COMPLETADOS! \uD83C\uDFC6\uD83C\uDF89\n\u00a1Dominaste la resta! Haz clic en \"Nuevo ejercicio\" para empezar de nuevo en el nivel 1.",
    sbwWelcomeMessage = "Haz clic en 'Nuevo ejercicio' para comenzar. Sigue las instrucciones para organizar el \u00e1baco.",
    sbwStepStatus = "\uD83D\uDCD6 Paso %d/%d",
)

val LocalSbwStrings = staticCompositionLocalOf { EnSbwStrings }

fun sbwStringsForLanguage(language: String): SbwStrings = when (language) {
    "pt-BR" -> PtSbwStrings
    "es-ES" -> EsSbwStrings
    else -> EnSbwStrings
}
