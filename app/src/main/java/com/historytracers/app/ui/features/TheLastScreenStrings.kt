// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class TheLastScreenStrings(
    val title: String,
    val fullTitle: String,
)

val EnTheLastScreenStrings = TheLastScreenStrings(
    title = "The Last",
    fullTitle = "Multiplication Table (The Last)",
)

val PtTheLastScreenStrings = TheLastScreenStrings(
    title = "O \u00DAltimo",
    fullTitle = "Tabuada (O \u00DAltimo)",
)

val EsTheLastScreenStrings = TheLastScreenStrings(
    title = "El \u00DAltimo",
    fullTitle = "Tabla de Multiplicar (El \u00DAltimo)",
)

val LocalTheLastScreenStrings = staticCompositionLocalOf { EnTheLastScreenStrings }

fun theLastScreenStringsForLanguage(language: String): TheLastScreenStrings = when (language) {
    "pt-BR" -> PtTheLastScreenStrings
    "es-ES" -> EsTheLastScreenStrings
    else -> EnTheLastScreenStrings
}
