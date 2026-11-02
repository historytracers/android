// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class DivisionAndRemainderScreenStrings(
    val title: String,
    val instruction: String,
    val formatLabel: String,
    val resultBelow: String,
    val resultTop: String,
    val promptPrefix: String,
    val promptDivision: String,
    val quotientLabel: String,
    val remainderLabel: String,
    val resultFormat: String,
    val newDivision: String,
    val increase: String,
    val decrease: String,
)

val EnDivisionAndRemainderScreenStrings = DivisionAndRemainderScreenStrings(
    title = "Division and Remainder",
    instruction = "Choose how the division is written and use the arrows to build the quotient digit by digit.",
    formatLabel = "Division format:",
    resultBelow = "Result below",
    resultTop = "Result on top",
    promptPrefix = "Now we are dividing:",
    promptDivision = "%1\$d \u00F7 %2\$d",
    quotientLabel = "Quotient",
    remainderLabel = "Remainder",
    resultFormat = "%1\$d \u00F7 %2\$d = %3\$d, remainder %4\$d",
    newDivision = "New division",
    increase = "Increase the digit of the quotient",
    decrease = "Decrease the digit of the quotient",
)

val PtDivisionAndRemainderScreenStrings = DivisionAndRemainderScreenStrings(
    title = "Divis\u00e3o e Resto",
    instruction = "Escolha como a divis\u00e3o \u00e9 escrita e use as setas para montar o quociente algarismo por algarismo.",
    formatLabel = "Formato da divis\u00e3o:",
    resultBelow = "Resultado abaixo",
    resultTop = "Resultado acima",
    promptPrefix = "No momento, estamos dividindo:",
    promptDivision = "%1\$d \u00F7 %2\$d",
    quotientLabel = "Quociente",
    remainderLabel = "Resto",
    resultFormat = "%1\$d \u00F7 %2\$d = %3\$d, resto %4\$d",
    newDivision = "Nova divis\u00e3o",
    increase = "Aumentar o algarismo do quociente",
    decrease = "Diminuir o algarismo do quociente",
)

val EsDivisionAndRemainderScreenStrings = DivisionAndRemainderScreenStrings(
    title = "Divisi\u00f3n y Resto",
    instruction = "Elija c\u00f3mo se escribe la divisi\u00f3n y use las flechas para formar el cociente cifra por cifra.",
    formatLabel = "Formato de la divisi\u00f3n:",
    resultBelow = "Resultado debajo",
    resultTop = "Resultado encima",
    promptPrefix = "En este momento, estamos dividiendo:",
    promptDivision = "%1\$d \u00F7 %2\$d",
    quotientLabel = "Cociente",
    remainderLabel = "Resto",
    resultFormat = "%1\$d \u00F7 %2\$d = %3\$d, resto %4\$d",
    newDivision = "Nueva divisi\u00f3n",
    increase = "Aumentar la cifra del cociente",
    decrease = "Disminuir la cifra del cociente",
)

val LocalDivisionAndRemainderScreenStrings = staticCompositionLocalOf { EnDivisionAndRemainderScreenStrings }

fun divisionAndRemainderScreenStringsForLanguage(language: String): DivisionAndRemainderScreenStrings = when (language) {
    "pt-BR" -> PtDivisionAndRemainderScreenStrings
    "es-ES" -> EsDivisionAndRemainderScreenStrings
    else -> EnDivisionAndRemainderScreenStrings
}
