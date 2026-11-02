// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class WritingQuotientAndRemainderScreenStrings(
    val title: String,
    val instruction: String,
    val formatLabel: String,
    val resultBelow: String,
    val resultTop: String,
    val promptPrefix: String,
    val promptDivision: String,
    val subtractPrompt: String,
    val subtractFormat: String,
    val quotientLabel: String,
    val remainderLabel: String,
    val resultFormat: String,
    val newDivision: String,
    val timesTableTitle: String,
    val timesTableEntry: String,
    val wrongDigitMessage: String,
    val wrongRemainderMessage: String,
    val activeDigitLabel: String,
    val remainderInputLabel: String,
)

val EnWritingQuotientAndRemainderScreenStrings = WritingQuotientAndRemainderScreenStrings(
    title = "Divisions: Writing the Quotient and the Remainder",
    instruction = "Type the quotient digit and then the result of the subtraction.",
    formatLabel = "Division format:",
    resultBelow = "Result below",
    resultTop = "Result on top",
    promptPrefix = "Now we are dividing:",
    promptDivision = "%1\$d \u00F7 %2\$d",
    subtractPrompt = "Now subtract and type the result:",
    subtractFormat = "%1\$d \u2212 %2\$d",
    quotientLabel = "Quotient",
    remainderLabel = "Remainder",
    resultFormat = "%1\$d \u00F7 %2\$d = %3\$d, remainder %4\$d",
    newDivision = "New division",
    timesTableTitle = "Table of %1\$d",
    timesTableEntry = "%1\$d \u00D7 %2\$2d = %3\$2d",
    wrongDigitMessage = "That digit does not fit. Compare it with the times table below and try again.",
    wrongRemainderMessage = "The subtraction result is not correct. Try again.",
    activeDigitLabel = "Digit of the quotient",
    remainderInputLabel = "Remainder",
)

val PtWritingQuotientAndRemainderScreenStrings = WritingQuotientAndRemainderScreenStrings(
    title = "Divis\u00f5es: escrevendo o quociente e o resto",
    instruction = "Digite o algarismo do quociente e depois o resultado da subtra\u00e7\u00e3o.",
    formatLabel = "Formato da divis\u00e3o:",
    resultBelow = "Resultado abaixo",
    resultTop = "Resultado acima",
    promptPrefix = "No momento, estamos dividindo:",
    promptDivision = "%1\$d \u00F7 %2\$d",
    subtractPrompt = "Agora subtraia e escreva o resultado:",
    subtractFormat = "%1\$d \u2212 %2\$d",
    quotientLabel = "Quociente",
    remainderLabel = "Resto",
    resultFormat = "%1\$d \u00F7 %2\$d = %3\$d, resto %4\$d",
    newDivision = "Nova divis\u00e3o",
    timesTableTitle = "Tabuada de %1\$d",
    timesTableEntry = "%1\$d \u00D7 %2\$2d = %3\$2d",
    wrongDigitMessage = "Esse algarismo n\u00e3o serve. Compare com a tabuada abaixo e tente novamente.",
    wrongRemainderMessage = "O resultado da subtra\u00e7\u00e3o n\u00e3o est\u00e1 correto. Tente novamente.",
    activeDigitLabel = "Algarismo do quociente",
    remainderInputLabel = "Resto",
)

val EsWritingQuotientAndRemainderScreenStrings = WritingQuotientAndRemainderScreenStrings(
    title = "Divisiones: escribiendo el cociente y el resto",
    instruction = "Escriba la cifra del cociente y luego el resultado de la resta.",
    formatLabel = "Formato de la divisi\u00f3n:",
    resultBelow = "Resultado debajo",
    resultTop = "Resultado encima",
    promptPrefix = "En este momento, estamos dividiendo:",
    promptDivision = "%1\$d \u00F7 %2\$d",
    subtractPrompt = "Ahora resta y escribe el resultado:",
    subtractFormat = "%1\$d \u2212 %2\$d",
    quotientLabel = "Cociente",
    remainderLabel = "Resto",
    resultFormat = "%1\$d \u00F7 %2\$d = %3\$d, resto %4\$d",
    newDivision = "Nueva divisi\u00f3n",
    timesTableTitle = "Tabla de %1\$d",
    timesTableEntry = "%1\$d \u00D7 %2\$2d = %3\$2d",
    wrongDigitMessage = "Esa cifra no sirve. Comp\u00e1rela con la tabla de abajo e int\u00e9ntelo de nuevo.",
    wrongRemainderMessage = "El resultado de la resta no es correcto. Int\u00e9ntalo de nuevo.",
    activeDigitLabel = "Cifra del cociente",
    remainderInputLabel = "Resto",
)

val LocalWritingQuotientAndRemainderScreenStrings =
    staticCompositionLocalOf { EnWritingQuotientAndRemainderScreenStrings }

fun writingQuotientAndRemainderScreenStringsForLanguage(language: String): WritingQuotientAndRemainderScreenStrings =
    when (language) {
        "pt-BR" -> PtWritingQuotientAndRemainderScreenStrings
        "es-ES" -> EsWritingQuotientAndRemainderScreenStrings
        else -> EnWritingQuotientAndRemainderScreenStrings
    }
