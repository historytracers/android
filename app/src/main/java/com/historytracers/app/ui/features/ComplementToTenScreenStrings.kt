// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class ComplementToTenScreenStrings(
    val title: String,
    val instruction: String,
    val hint: String,
    val yLabel: String,
    val xLabel: String,
    val equation: String,
    val complementText: String,
    val upArrow: String,
    val downArrow: String,
    val completionMessage: String,
)

val EnComplementToTenScreenStrings = ComplementToTenScreenStrings(
    title = "Complement to 10",
    instruction = "When we add two numbers and the result is 10, each number is the complement of the other. In this table, the values Y and X always add up to 10.",
    hint = "This is the first step to understand how numbers are carried when adding with the abacus.",
    yLabel = "Y",
    xLabel = "X",
    equation = "%d + %d = 10",
    complementText = "The complement of %d to 10 is %d.",
    upArrow = "Increase X and decrease Y",
    downArrow = "Decrease X and increase Y",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou explored every combination of Y + X = 10!\nGreat job!",
)

val PtComplementToTenScreenStrings = ComplementToTenScreenStrings(
    title = "Complemento de 10",
    instruction = "Quando somamos dois n\u00fameros e o resultado \u00e9 10, cada n\u00famero \u00e9 o complemento do outro. Nesta tabela, os valores Y e X sempre somam 10.",
    hint = "Este \u00e9 o primeiro passo para entender como os n\u00fameros s\u00e3o levados ao somar com o \u00e1baco.",
    yLabel = "Y",
    xLabel = "X",
    equation = "%d + %d = 10",
    complementText = "O complemento de %d para 10 \u00e9 %d.",
    upArrow = "Aumentar X e diminuir Y",
    downArrow = "Diminuir X e aumentar Y",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFEITO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea explorou todas as combina\u00e7\u00f5es de Y + X = 10!\n\u00d3timo trabalho!",
)

val EsComplementToTenScreenStrings = ComplementToTenScreenStrings(
    title = "Complemento de 10",
    instruction = "Cuando sumamos dos n\u00fameros y el resultado es 10, cada n\u00famero es el complemento del otro. En esta tabla, los valores Y y X siempre suman 10.",
    hint = "Este es el primer paso para entender c\u00f3mo se llevan los n\u00fameros al sumar con el \u00e1baco.",
    yLabel = "Y",
    xLabel = "X",
    equation = "%d + %d = 10",
    complementText = "El complemento de %d para 10 es %d.",
    upArrow = "Aumentar X y disminuir Y",
    downArrow = "Disminuir X y aumentar Y",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1PERFECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1Exploraste todas las combinaciones de Y + X = 10!\n\u00a1Gran trabajo!",
)

val LocalComplementToTenScreenStrings = staticCompositionLocalOf { EnComplementToTenScreenStrings }

fun complementToTenScreenStringsForLanguage(language: String): ComplementToTenScreenStrings = when (language) {
    "pt-BR" -> PtComplementToTenScreenStrings
    "es-ES" -> EsComplementToTenScreenStrings
    else -> EnComplementToTenScreenStrings
}
