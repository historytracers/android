// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class WritingTheQuotientScreenStrings(
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
    val timesTableTitle: String,
    val timesTableEntry: String,
    val wrongDigitMessage: String,
    val activeDigitLabel: String,
)

val EnWritingTheQuotientScreenStrings = WritingTheQuotientScreenStrings(
    title = "Divisions: Writing the Quotient",
    instruction = "Type each digit of the quotient in the highlighted box.",
    formatLabel = "Division format:",
    resultBelow = "Result below",
    resultTop = "Result on top",
    promptPrefix = "Now we are dividing:",
    promptDivision = "%1\$d \u00F7 %2\$d",
    quotientLabel = "Quotient",
    remainderLabel = "Remainder",
    resultFormat = "%1\$d \u00F7 %2\$d = %3\$d, remainder %4\$d",
    newDivision = "New division",
    timesTableTitle = "Table of %1\$d",
    timesTableEntry = "%1\$d \u00D7 %2\$2d = %3\$2d",
    wrongDigitMessage = "That digit does not fit. Compare it with the times table below and try again.",
    activeDigitLabel = "Digit of the quotient",
)

val PtWritingTheQuotientScreenStrings = WritingTheQuotientScreenStrings(
    title = "Divis\u00f5es: escrever o quociente",
    instruction = "Digite cada algarismo do quociente na caixa destacada.",
    formatLabel = "Formato da divis\u00e3o:",
    resultBelow = "Resultado abaixo",
    resultTop = "Resultado acima",
    promptPrefix = "No momento, estamos dividindo:",
    promptDivision = "%1\$d \u00F7 %2\$d",
    quotientLabel = "Quociente",
    remainderLabel = "Resto",
    resultFormat = "%1\$d \u00F7 %2\$d = %3\$d, resto %4\$d",
    newDivision = "Nova divis\u00e3o",
    timesTableTitle = "Tabuada de %1\$d",
    timesTableEntry = "%1\$d \u00D7 %2\$2d = %3\$2d",
    wrongDigitMessage = "Esse algarismo n\u00e3o serve. Compare com a tabuada abaixo e tente novamente.",
    activeDigitLabel = "Algarismo do quociente",
)

val EsWritingTheQuotientScreenStrings = WritingTheQuotientScreenStrings(
    title = "Divisiones: escribir el cociente",
    instruction = "Escriba cada cifra del cociente en la casilla resaltada.",
    formatLabel = "Formato de la divisi\u00f3n:",
    resultBelow = "Resultado debajo",
    resultTop = "Resultado encima",
    promptPrefix = "En este momento, estamos dividiendo:",
    promptDivision = "%1\$d \u00F7 %2\$d",
    quotientLabel = "Cociente",
    remainderLabel = "Resto",
    resultFormat = "%1\$d \u00F7 %2\$d = %3\$d, resto %4\$d",
    newDivision = "Nueva divisi\u00f3n",
    timesTableTitle = "Tabla de %1\$d",
    timesTableEntry = "%1\$d \u00D7 %2\$2d = %3\$2d",
    wrongDigitMessage = "Esa cifra no sirve. Comp\u00e1rela con la tabla de abajo e int\u00e9ntelo de nuevo.",
    activeDigitLabel = "Cifra del cociente",
)

val LocalWritingTheQuotientScreenStrings = staticCompositionLocalOf { EnWritingTheQuotientScreenStrings }

fun writingTheQuotientScreenStringsForLanguage(language: String): WritingTheQuotientScreenStrings = when (language) {
    "pt-BR" -> PtWritingTheQuotientScreenStrings
    "es-ES" -> EsWritingTheQuotientScreenStrings
    else -> EnWritingTheQuotientScreenStrings
}
