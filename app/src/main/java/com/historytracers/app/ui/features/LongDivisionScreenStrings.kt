// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class LongDivisionScreenStrings(
    val divisionAndRemainder: String,
    val divisionExample: String,
)

val EnLongDivisionScreenStrings = LongDivisionScreenStrings(
    divisionAndRemainder = "Division and Remainder",
    divisionExample = "257 \u00F7 4",
)

val PtLongDivisionScreenStrings = LongDivisionScreenStrings(
    divisionAndRemainder = "Divis\u00e3o e Resto",
    divisionExample = "257 \u00F7 4",
)

val EsLongDivisionScreenStrings = LongDivisionScreenStrings(
    divisionAndRemainder = "Divisi\u00f3n y Resto",
    divisionExample = "257 \u00F7 4",
)

val LocalLongDivisionScreenStrings = staticCompositionLocalOf { EnLongDivisionScreenStrings }

fun longDivisionScreenStringsForLanguage(language: String): LongDivisionScreenStrings = when (language) {
    "pt-BR" -> PtLongDivisionScreenStrings
    "es-ES" -> EsLongDivisionScreenStrings
    else -> EnLongDivisionScreenStrings
}
