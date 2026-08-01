// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class AbacusInRereadingScreenStrings(
    val title: String,
    val instruction: String,
    val yLabel: String,
    val xLabel: String,
    val zLabel: String,
    val equation: String,
    val upArrow: String,
    val downArrow: String,
    val perfectMessage: String,
    val carryHint: String,
)

val EnAbacusInRereadingScreenStrings = AbacusInRereadingScreenStrings(
    title = "Abacus in rereading",
    instruction = "In this exercise, the value Y is fixed and X is controlled by the arrows. The result Z = Y + X is always shown. When X reaches 9, the largest value of the units order, the exercise is complete.",
    yLabel = "Y",
    xLabel = "X",
    zLabel = "Z",
    equation = "%d + %d = %d",
    upArrow = "Increase X",
    downArrow = "Decrease X",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nX reached 9! You completed the exercise.\nClick 'New exercise' to try another number!",
    carryHint = "%d + %d = %d. When the result is 10 or more, on the abacus we need the next column (carrying over).",
)

val PtAbacusInRereadingScreenStrings = AbacusInRereadingScreenStrings(
    title = "\u00c1baco na releitura",
    instruction = "Neste exerc\u00edcio, o valor Y \u00e9 fixo e X \u00e9 controlado pelas setas. O resultado Z = Y + X \u00e9 sempre mostrado. Quando X chegar a 9, o maior valor da ordem das unidades, o exerc\u00edcio est\u00e1 completo.",
    yLabel = "Y",
    xLabel = "X",
    zLabel = "Z",
    equation = "%d + %d = %d",
    upArrow = "Aumentar X",
    downArrow = "Diminuir X",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFEITO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nX chegou a 9! Voc\u00ea completou o exerc\u00edcio.\nClique em 'Novo exerc\u00edcio' para tentar outro n\u00famero!",
    carryHint = "%d + %d = %d. Quando o resultado \u00e9 10 ou mais, no \u00e1baco precisamos da pr\u00f3xima coluna (indo para a pr\u00f3xima casa).",
)

val EsAbacusInRereadingScreenStrings = AbacusInRereadingScreenStrings(
    title = "\u00c1baco en relectura",
    instruction = "En este ejercicio, el valor Y es fijo y X se controla con las flechas. El resultado Z = Y + X siempre se muestra. Cuando X llegue a 9, el valor m\u00e1s grande del orden de las unidades, el ejercicio est\u00e1 completo.",
    yLabel = "Y",
    xLabel = "X",
    zLabel = "Z",
    equation = "%d + %d = %d",
    upArrow = "Aumentar X",
    downArrow = "Disminuir X",
    perfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1PERFECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1X lleg\u00f3 a 9! Completaste el ejercicio.\n\u00a1Haz clic en 'Nuevo ejercicio' para probar otro n\u00famero!",
    carryHint = "%d + %d = %d. Cuando el resultado es 10 o m\u00e1s, en el \u00e1baco necesitamos la siguiente columna (llevando a la siguiente posici\u00f3n).",
)

val LocalAbacusInRereadingScreenStrings = staticCompositionLocalOf { EnAbacusInRereadingScreenStrings }

fun abacusInRereadingScreenStringsForLanguage(language: String): AbacusInRereadingScreenStrings = when (language) {
    "pt-BR" -> PtAbacusInRereadingScreenStrings
    "es-ES" -> EsAbacusInRereadingScreenStrings
    else -> EnAbacusInRereadingScreenStrings
}
