// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class ClapScreenStrings(
    val numberOfClaps: String,
)

val EnClapScreenStrings = ClapScreenStrings(
    numberOfClaps = "Number of claps:",
)

val PtClapScreenStrings = ClapScreenStrings(
    numberOfClaps = "N\u00famero de palmas:",
)

val EsClapScreenStrings = ClapScreenStrings(
    numberOfClaps = "N\u00famero de aplausos:",
)

val LocalClapScreenStrings = staticCompositionLocalOf { EnClapScreenStrings }

fun clapScreenStringsForLanguage(language: String): ClapScreenStrings = when (language) {
    "pt-BR" -> PtClapScreenStrings
    "es-ES" -> EsClapScreenStrings
    else -> EnClapScreenStrings
}
