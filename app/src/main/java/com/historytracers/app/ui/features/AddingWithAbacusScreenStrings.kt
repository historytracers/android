// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class AddingWithAbacusScreenStrings(
    val title: String,
    val instructionLevel1: String,
    val instructionLevel2: String,
    val level1Name: String,
    val level2Name: String,
    val stepWriteFirst: String,
    val stepAddLower: String,
    val stepAddExchange: String,
    val stepAddSchyoty: String,
    val stepFinal: String,
    val feedbackCorrect: String,
    val feedbackPerfect: String,
    val feedbackCongratulations: String,
    val lastLevelMessage: String,
)

val EnAddingWithAbacusScreenStrings = AddingWithAbacusScreenStrings(
    title = "Adding Two Numbers",
    instructionLevel1 = "We will add two numbers smaller than 5 using only the first column of the abacus. First, set the first number; then, add the second number by moving the markers. Complete all steps to finish the sum.",
    instructionLevel2 = "Now the results reach 5 or more. When 5 lower markers are together, exchange them for one upper marker (value 5). Complete all steps to finish the sum.",
    level1Name = "Adding from 0 to 4",
    level2Name = "The number 5 and beyond",
    stepWriteFirst = "\uD83D\uDCCC Step 1: Set the abacus to show %d.",
    stepAddLower = "\u2795 Step 2: Move %d lower markers toward the beam. The abacus should now show %d.",
    stepAddExchange = "\u2795 Step 2: Move %d markers toward the beam. When 5 lower markers are together, exchange them for one upper marker (value 5). The abacus should now show %d.",
    stepAddSchyoty = "\u2795 Step 2: Move %d beads to the left on the units wire. The abacus should now show %d.",
    stepFinal = "\uD83C\uDF89 Final: %d + %d = %d. The abacus should show this sum!",
    feedbackCorrect = "\u2705 Correct! Click 'Next step' to continue.",
    feedbackPerfect = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\nGreat job using the abacus!",
    feedbackCongratulations = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CONGRATULATIONS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou calculated %d + %d = %d\nClick 'New exercise' to practice more!",
    lastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 ALL LEVELS COMPLETE! \uD83C\uDFC6\uD83C\uDF89\nYou mastered adding with the abacus! Click \"New exercise\" to start again at level 1.",
)

val PtAddingWithAbacusScreenStrings = AddingWithAbacusScreenStrings(
    title = "Adicionando Dois N\u00fameros",
    instructionLevel1 = "Vamos somar dois n\u00fameros menores que 5 usando apenas a primeira coluna do \u00e1baco. Primeiro, represente o primeiro n\u00famero; depois, some o segundo n\u00famero movendo os marcadores. Complete todos os passos para finalizar a soma.",
    instructionLevel2 = "Agora os resultados chegam a 5 ou mais. Quando 5 marcadores inferiores estiverem juntos, troque-os por um marcador superior (valor 5). Complete todos os passos para finalizar a soma.",
    level1Name = "Somando de 0 a 4",
    level2Name = "O n\u00famero 5 e al\u00e9m",
    stepWriteFirst = "\uD83D\uDCCC Passo 1: Coloque o \u00e1baco para mostrar %d.",
    stepAddLower = "\u2795 Passo 2: Mova %d marcadores inferiores em dire\u00e7\u00e3o \u00e0 barra. O \u00e1baco deve mostrar %d agora.",
    stepAddExchange = "\u2795 Passo 2: Mova %d marcadores em dire\u00e7\u00e3o \u00e0 barra. Quando 5 marcadores inferiores estiverem juntos, troque-os por um marcador superior (valor 5). O \u00e1baco deve mostrar %d agora.",
    stepAddSchyoty = "\u2795 Passo 2: Mova %d contas para a esquerda no fio das unidades. O \u00e1baco deve mostrar %d agora.",
    stepFinal = "\uD83C\uDF89 Final: %d + %d = %d. O \u00e1baco deve mostrar esta soma!",
    feedbackCorrect = "\u2705 Correto! Clique em 'Pr\u00f3ximo passo' para continuar.",
    feedbackPerfect = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFEITO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\n\u00d3timo trabalho usando o \u00e1baco!",
    feedbackCongratulations = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PARAB\u00c9NS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea calculou %d + %d = %d\nClique em 'Novo exerc\u00edcio' para praticar mais!",
    lastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 TODOS OS N\u00cdVEIS CONCLU\u00cdDOS! \uD83C\uDFC6\uD83C\uDF89\nVoc\u00ea dominou a soma com o \u00e1baco! Clique em \"Novo exerc\u00edcio\" para come\u00e7ar novamente no n\u00edvel 1.",
)

val EsAddingWithAbacusScreenStrings = AddingWithAbacusScreenStrings(
    title = "Sumando Dos N\u00fameros",
    instructionLevel1 = "Vamos a sumar dos n\u00fameros menores que 5 usando solo la primera columna del \u00e1baco. Primero, representa el primer n\u00famero; luego, suma el segundo n\u00famero moviendo los marcadores. Completa todos los pasos para finalizar la suma.",
    instructionLevel2 = "Ahora los resultados llegan a 5 o m\u00e1s. Cuando 5 marcadores inferiores est\u00e9n juntos, c\u00e1mbialos por un marcador superior (valor 5). Completa todos los pasos para finalizar la suma.",
    level1Name = "Sumando de 0 a 4",
    level2Name = "El n\u00famero 5 y m\u00e1s all\u00e1",
    stepWriteFirst = "\uD83D\uDCCC Paso 1: Coloca el \u00e1baco para mostrar %d.",
    stepAddLower = "\u2795 Paso 2: Mueve %d marcadores inferiores hacia la barra. El \u00e1baco debe mostrar %d ahora.",
    stepAddExchange = "\u2795 Paso 2: Mueve %d marcadores hacia la barra. Cuando 5 marcadores inferiores est\u00e9n juntos, c\u00e1mbialos por un marcador superior (valor 5). El \u00e1baco debe mostrar %d ahora.",
    stepAddSchyoty = "\u2795 Paso 2: Mueve %d cuentas hacia la izquierda en el alambre de las unidades. El \u00e1baco debe mostrar %d ahora.",
    stepFinal = "\uD83C\uDF89 Final: %d + %d = %d. \u00a1El \u00e1baco debe mostrar esta suma!",
    feedbackCorrect = "\u2705 \u00a1Correcto! Haz clic en 'Siguiente paso' para continuar.",
    feedbackPerfect = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1PERFECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\n\u00a1Gran trabajo usando el \u00e1baco!",
    feedbackCongratulations = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1FELICITACIONES! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nHas calculado %d + %d = %d\n\u00a1Haz clic en 'Nuevo ejercicio' para practicar m\u00e1s!",
    lastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 \u00a1TODOS LOS NIVELES COMPLETADOS! \uD83C\uDFC6\uD83C\uDF89\n\u00a1Dominaste la suma con el \u00e1baco! Haz clic en \"Nuevo ejercicio\" para empezar de nuevo en el nivel 1.",
)

val LocalAddingWithAbacusScreenStrings = staticCompositionLocalOf { EnAddingWithAbacusScreenStrings }

fun addingWithAbacusScreenStringsForLanguage(language: String): AddingWithAbacusScreenStrings = when (language) {
    "pt-BR" -> PtAddingWithAbacusScreenStrings
    "es-ES" -> EsAddingWithAbacusScreenStrings
    else -> EnAddingWithAbacusScreenStrings
}
