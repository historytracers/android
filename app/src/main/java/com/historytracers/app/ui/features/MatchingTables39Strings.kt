// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MatchingTables39Strings(
    val rulesTitle: String,
)

val EnMatchingTables39Strings = MatchingTables39Strings(
    rulesTitle = "Matching the Tables of 3 and 9",
)

val PtMatchingTables39Strings = MatchingTables39Strings(
    rulesTitle = "Combinando as Tabuadas de 3 e 9",
)

val EsMatchingTables39Strings = MatchingTables39Strings(
    rulesTitle = "Combinando las Tablas de 3 y 9",
)

val LocalMatchingTables39Strings = staticCompositionLocalOf { EnMatchingTables39Strings }

fun matchingTables39StringsForLanguage(language: String): MatchingTables39Strings = when (language) {
    "pt-BR" -> PtMatchingTables39Strings
    "es-ES" -> EsMatchingTables39Strings
    else -> EnMatchingTables39Strings
}
