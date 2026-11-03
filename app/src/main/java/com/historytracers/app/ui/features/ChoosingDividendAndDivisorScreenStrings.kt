// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class ChoosingDividendAndDivisorScreenStrings(
    val title: String,
    val instruction: String,
    val dividendLabel: String,
    val divisorLabel: String,
    val start: String,
    val invalidDividendMessage: String,
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

val EnChoosingDividendAndDivisorScreenStrings = ChoosingDividendAndDivisorScreenStrings(
    title = "Divisions: Choosing the Dividend and the Divisor",
    instruction = "Choose the dividend and the divisor, press Start and write the quotient and the remainder.",
    dividendLabel = "Dividend",
    divisorLabel = "Divisor",
    start = "Start",
    invalidDividendMessage = "Please type a whole number between 0 and 9999.",
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

val PtChoosingDividendAndDivisorScreenStrings = ChoosingDividendAndDivisorScreenStrings(
    title = "Divis\u00F5es: escolhendo o dividendo e o divisor",
    instruction = "Escolha o dividendo e o divisor, toque em Iniciar e escreva o quociente e o resto.",
    dividendLabel = "Dividendo",
    divisorLabel = "Divisor",
    start = "Iniciar",
    invalidDividendMessage = "Digite um n\u00FAmero inteiro entre 0 e 9999.",
    formatLabel = "Formato da divis\u00E3o:",
    resultBelow = "Resultado abaixo",
    resultTop = "Resultado acima",
    promptPrefix = "No momento, estamos dividindo:",
    promptDivision = "%1\$d \u00F7 %2\$d",
    subtractPrompt = "Agora subtraia e escreva o resultado:",
    subtractFormat = "%1\$d \u2212 %2\$d",
    quotientLabel = "Quociente",
    remainderLabel = "Resto",
    resultFormat = "%1\$d \u00F7 %2\$d = %3\$d, resto %4\$d",
    newDivision = "Nova divis\u00E3o",
    timesTableTitle = "Tabuada de %1\$d",
    timesTableEntry = "%1\$d \u00D7 %2\$2d = %3\$2d",
    wrongDigitMessage = "Esse algarismo n\u00E3o serve. Compare com a tabuada abaixo e tente novamente.",
    wrongRemainderMessage = "O resultado da subtra\u00E7\u00E3o n\u00E3o est\u00E1 correto. Tente novamente.",
    activeDigitLabel = "Algarismo do quociente",
    remainderInputLabel = "Resto",
)

val EsChoosingDividendAndDivisorScreenStrings = ChoosingDividendAndDivisorScreenStrings(
    title = "Divisiones: eligiendo el dividendo y el divisor",
    instruction = "Elija el dividendo y el divisor, pulse Iniciar y escriba el cociente y el resto.",
    dividendLabel = "Dividendo",
    divisorLabel = "Divisor",
    start = "Iniciar",
    invalidDividendMessage = "Escriba un n\u00FAmero entero entre 0 y 9999.",
    formatLabel = "Formato de la divisi\u00F3n:",
    resultBelow = "Resultado debajo",
    resultTop = "Resultado encima",
    promptPrefix = "En este momento, estamos dividiendo:",
    promptDivision = "%1\$d \u00F7 %2\$d",
    subtractPrompt = "Ahora resta y escribe el resultado:",
    subtractFormat = "%1\$d \u2212 %2\$d",
    quotientLabel = "Cociente",
    remainderLabel = "Resto",
    resultFormat = "%1\$d \u00F7 %2\$d = %3\$d, resto %4\$d",
    newDivision = "Nueva divisi\u00F3n",
    timesTableTitle = "Tabla de %1\$d",
    timesTableEntry = "%1\$d \u00D7 %2\$2d = %3\$2d",
    wrongDigitMessage = "Esa cifra no sirve. Comp\u00E1rela con la tabla de abajo e int\u00E9ntelo de nuevo.",
    wrongRemainderMessage = "El resultado de la resta no es correcto. Int\u00E9ntalo de nuevo.",
    activeDigitLabel = "Cifra del cociente",
    remainderInputLabel = "Resto",
)

val LocalChoosingDividendAndDivisorScreenStrings =
    staticCompositionLocalOf { EnChoosingDividendAndDivisorScreenStrings }

fun choosingDividendAndDivisorScreenStringsForLanguage(language: String): ChoosingDividendAndDivisorScreenStrings =
    when (language) {
        "pt-BR" -> PtChoosingDividendAndDivisorScreenStrings
        "es-ES" -> EsChoosingDividendAndDivisorScreenStrings
        else -> EnChoosingDividendAndDivisorScreenStrings
    }
