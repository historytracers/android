// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class LongDivisionScreenStrings(
    val divisionAndRemainder: String,
    val divisionExample: String,
    val writingTheQuotient: String,
    val writingTheQuotientExample: String,
    val writingQuotientAndRemainder: String,
    val writingQuotientAndRemainderExample: String,
    val choosingDividendAndDivisor: String,
    val choosingDividendAndDivisorExample: String,
)

val EnLongDivisionScreenStrings = LongDivisionScreenStrings(
    divisionAndRemainder = "Division and Remainder",
    divisionExample = "257 \u00F7 4",
    writingTheQuotient = "Divisions: Writing the Quotient",
    writingTheQuotientExample = "125 \u00F7 5",
    writingQuotientAndRemainder = "Divisions: Writing the Quotient and the Remainder",
    writingQuotientAndRemainderExample = "987 \u00F7 1",
    choosingDividendAndDivisor = "Divisions: Choosing the Dividend and the Divisor",
    choosingDividendAndDivisorExample = "??? \u00F7 ?",
)

val PtLongDivisionScreenStrings = LongDivisionScreenStrings(
    divisionAndRemainder = "Divis\u00e3o e Resto",
    divisionExample = "257 \u00F7 4",
    writingTheQuotient = "Divis\u00f5es: escrevendo o quociente",
    writingTheQuotientExample = "125 \u00F7 5",
    writingQuotientAndRemainder = "Divis\u00f5es: escrevendo o quociente e o resto",
    writingQuotientAndRemainderExample = "987 \u00F7 1",
    choosingDividendAndDivisor = "Divis\u00f5es: escolhendo o dividendo e o divisor",
    choosingDividendAndDivisorExample = "??? \u00F7 ?",
)

val EsLongDivisionScreenStrings = LongDivisionScreenStrings(
    divisionAndRemainder = "Divisi\u00F3n y Resto",
    divisionExample = "257 \u00F7 4",
    writingTheQuotient = "Divisiones: escribiendo el cociente",
    writingTheQuotientExample = "125 \u00F7 5",
    writingQuotientAndRemainder = "Divisiones: escribiendo el cociente y el resto",
    writingQuotientAndRemainderExample = "987 \u00F7 1",
    choosingDividendAndDivisor = "Divisiones: eligiendo el dividendo y el divisor",
    choosingDividendAndDivisorExample = "??? \u00F7 ?",
)

val LocalLongDivisionScreenStrings = staticCompositionLocalOf { EnLongDivisionScreenStrings }

fun longDivisionScreenStringsForLanguage(language: String): LongDivisionScreenStrings = when (language) {
    "pt-BR" -> PtLongDivisionScreenStrings
    "es-ES" -> EsLongDivisionScreenStrings
    else -> EnLongDivisionScreenStrings
}
