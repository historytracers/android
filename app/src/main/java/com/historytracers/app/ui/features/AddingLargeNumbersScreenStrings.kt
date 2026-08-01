// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class AddingLargeNumbersScreenStrings(
    val title: String,
    val instruction: String,
    val level1Name: String,
    val level2Name: String,
    val level3Name: String,
    val stepWriteTens: String,
    val stepWriteUnits: String,
    val stepWriteSchyotyTens: String,
    val stepWriteSchyotyUnits: String,
    val stepAddSimple: String,
    val stepAddUnits: String,
    val stepAddTens: String,
    val stepAddCarry: String,
    val stepAddSchyoty: String,
    val stepFinal: String,
    val feedbackCorrect: String,
    val feedbackPerfect: String,
    val feedbackCongratulations: String,
    val lastLevelMessage: String,
)

val EnAddingLargeNumbersScreenStrings = AddingLargeNumbersScreenStrings(
    title = "Adding Large Numbers",
    instruction = "We will add two numbers from 1 to 49 using only the first two columns of the abacus: the tens column and the units column. First, set the first number; then, add the second number. When the sum of a column exceeds 9, we use the complement to carry 1 to the next column.",
    level1Name = "Adding up to 20",
    level2Name = "Adding up to 35",
    level3Name = "Adding up to 49",
    stepWriteTens = "\uD83D\uDCCC Step 1: Set the tens column to show %d and the units column to show %d, representing the number %d.",
    stepWriteUnits = "\uD83D\uDCCC Step 1: Set the units column to show %d, representing the number %d.",
    stepWriteSchyotyTens = "\uD83D\uDCCC Step 1: Move %d beads to the left on the tens wire and %d beads on the units wire, representing the number %d.",
    stepWriteSchyotyUnits = "\uD83D\uDCCC Step 1: Move %d beads to the left on the units wire, representing the number %d.",
    stepAddSimple = "\u2795 Step 2: Add %d to the units column and %d to the tens column. The abacus should now show %d.",
    stepAddUnits = "\u2795 Step 2: Add %d to the units column. The abacus should now show %d.",
    stepAddTens = "\u2795 Step 2: Add %d to the tens column. The abacus should now show %d.",
    stepAddCarry = "\u2795 Step 2: Add %d. The units sum is %d + %d = %d (\u2265 10), so use the complement: the complement of %d to 10 is %d. Remove %d from the units column, then add %d to the tens column (carry included). The abacus should now show %d.",
    stepAddSchyoty = "\u2795 Step 2: Add %d. Move the beads to the left so the abacus shows %d.",
    stepFinal = "\uD83C\uDF89 Final: %d + %d = %d. The abacus should show this sum!",
    feedbackCorrect = "\u2705 Correct! Click 'Next step' to continue.",
    feedbackPerfect = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\nGreat job using the abacus!",
    feedbackCongratulations = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CONGRATULATIONS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou calculated %d + %d = %d\nClick 'New exercise' to practice more!",
    lastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 ALL LEVELS COMPLETE! \uD83C\uDFC6\uD83C\uDF89\nYou mastered adding large numbers with the abacus! Click \"New exercise\" to start again at level 1.",
)

val PtAddingLargeNumbersScreenStrings = AddingLargeNumbersScreenStrings(
    title = "Adicionando N\u00fameros Grandes",
    instruction = "Vamos somar dois n\u00fameros de 1 a 49 usando apenas as duas primeiras colunas do \u00e1baco: a coluna das dezenas e a coluna das unidades. Primeiro, represente o primeiro n\u00famero; depois, some o segundo n\u00famero. Quando a soma de uma coluna exceder 9, usamos o complemento para levar 1 para a pr\u00f3xima coluna.",
    level1Name = "Somando at\u00e9 20",
    level2Name = "Somando at\u00e9 35",
    level3Name = "Somando at\u00e9 49",
    stepWriteTens = "\uD83D\uDCCC Passo 1: Coloque a coluna das dezenas para mostrar %d e a coluna das unidades para mostrar %d, representando o n\u00famero %d.",
    stepWriteUnits = "\uD83D\uDCCC Passo 1: Coloque a coluna das unidades para mostrar %d, representando o n\u00famero %d.",
    stepWriteSchyotyTens = "\uD83D\uDCCC Passo 1: Mova %d contas para a esquerda no fio das dezenas e %d contas no fio das unidades, representando o n\u00famero %d.",
    stepWriteSchyotyUnits = "\uD83D\uDCCC Passo 1: Mova %d contas para a esquerda no fio das unidades, representando o n\u00famero %d.",
    stepAddSimple = "\u2795 Passo 2: Adicione %d \u00e0 coluna das unidades e %d \u00e0 coluna das dezenas. O \u00e1baco deve mostrar %d agora.",
    stepAddUnits = "\u2795 Passo 2: Adicione %d \u00e0 coluna das unidades. O \u00e1baco deve mostrar %d agora.",
    stepAddTens = "\u2795 Passo 2: Adicione %d \u00e0 coluna das dezenas. O \u00e1baco deve mostrar %d agora.",
    stepAddCarry = "\u2795 Passo 2: Adicione %d. A soma das unidades \u00e9 %d + %d = %d (\u2265 10), ent\u00e3o use o complemento: o complemento de %d para 10 \u00e9 %d. Remova %d da coluna das unidades e adicione %d \u00e0 coluna das dezenas (incluindo o que vai). O \u00e1baco deve mostrar %d agora.",
    stepAddSchyoty = "\u2795 Passo 2: Adicione %d. Mova as contas para a esquerda para o \u00e1baco mostrar %d.",
    stepFinal = "\uD83C\uDF89 Final: %d + %d = %d. O \u00e1baco deve mostrar esta soma!",
    feedbackCorrect = "\u2705 Correto! Clique em 'Pr\u00f3ximo passo' para continuar.",
    feedbackPerfect = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFEITO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\n\u00d3timo trabalho usando o \u00e1baco!",
    feedbackCongratulations = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PARAB\u00c9NS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea calculou %d + %d = %d\nClique em 'Novo exerc\u00edcio' para praticar mais!",
    lastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 TODOS OS N\u00cdVEIS CONCLU\u00cdDOS! \uD83C\uDFC6\uD83C\uDF89\nVoc\u00ea dominou a soma de n\u00fameros grandes com o \u00e1baco! Clique em \"Novo exerc\u00edcio\" para come\u00e7ar novamente no n\u00edvel 1.",
)

val EsAddingLargeNumbersScreenStrings = AddingLargeNumbersScreenStrings(
    title = "Sumando N\u00fameros Grandes",
    instruction = "Vamos a sumar dos n\u00fameros del 1 al 49 usando solo las dos primeras columnas del \u00e1baco: la columna de las decenas y la columna de las unidades. Primero, representa el primer n\u00famero; luego, suma el segundo n\u00famero. Cuando la suma de una columna supere 9, usamos el complemento para llevar 1 a la siguiente columna.",
    level1Name = "Sumando hasta 20",
    level2Name = "Sumando hasta 35",
    level3Name = "Sumando hasta 49",
    stepWriteTens = "\uD83D\uDCCC Paso 1: Coloca la columna de las decenas para mostrar %d y la columna de las unidades para mostrar %d, representando el n\u00famero %d.",
    stepWriteUnits = "\uD83D\uDCCC Paso 1: Coloca la columna de las unidades para mostrar %d, representando el n\u00famero %d.",
    stepWriteSchyotyTens = "\uD83D\uDCCC Paso 1: Mueve %d cuentas a la izquierda en el alambre de las decenas y %d cuentas en el alambre de las unidades, representando el n\u00famero %d.",
    stepWriteSchyotyUnits = "\uD83D\uDCCC Paso 1: Mueve %d cuentas a la izquierda en el alambre de las unidades, representando el n\u00famero %d.",
    stepAddSimple = "\u2795 Paso 2: Agrega %d a la columna de las unidades y %d a la columna de las decenas. El \u00e1baco debe mostrar %d ahora.",
    stepAddUnits = "\u2795 Paso 2: Agrega %d a la columna de las unidades. El \u00e1baco debe mostrar %d ahora.",
    stepAddTens = "\u2795 Paso 2: Agrega %d a la columna de las decenas. El \u00e1baco debe mostrar %d ahora.",
    stepAddCarry = "\u2795 Paso 2: Agrega %d. La suma de las unidades es %d + %d = %d (\u2265 10), as\u00ed que usa el complemento: el complemento de %d para 10 es %d. Retira %d de la columna de las unidades y agrega %d a la columna de las decenas (incluyendo lo que se lleva). El \u00e1baco debe mostrar %d ahora.",
    stepAddSchyoty = "\u2795 Paso 2: Agrega %d. Mueve las cuentas a la izquierda para que el \u00e1baco muestre %d.",
    stepFinal = "\uD83C\uDF89 Final: %d + %d = %d. \u00a1El \u00e1baco debe mostrar esta suma!",
    feedbackCorrect = "\u2705 \u00a1Correcto! Haz clic en 'Siguiente paso' para continuar.",
    feedbackPerfect = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1PERFECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\n\u00a1Gran trabajo usando el \u00e1baco!",
    feedbackCongratulations = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1FELICITACIONES! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nHas calculado %d + %d = %d\n\u00a1Haz clic en 'Nuevo ejercicio' para practicar m\u00e1s!",
    lastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 \u00a1TODOS LOS NIVELES COMPLETADOS! \uD83C\uDFC6\uD83C\uDF89\n\u00a1Dominaste la suma de n\u00fameros grandes con el \u00e1baco! Haz clic en \"Nuevo ejercicio\" para empezar de nuevo en el nivel 1.",
)

val LocalAddingLargeNumbersScreenStrings = staticCompositionLocalOf { EnAddingLargeNumbersScreenStrings }

fun addingLargeNumbersScreenStringsForLanguage(language: String): AddingLargeNumbersScreenStrings = when (language) {
    "pt-BR" -> PtAddingLargeNumbersScreenStrings
    "es-ES" -> EsAddingLargeNumbersScreenStrings
    else -> EnAddingLargeNumbersScreenStrings
}
