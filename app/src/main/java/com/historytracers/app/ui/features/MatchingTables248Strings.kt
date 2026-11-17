// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MatchingTables248Strings(
    val rulesTitle: String,
)

val EnMatchingTables248Strings = MatchingTables248Strings(
    rulesTitle = "Matching the Tables of 2, 4 and 8",
)

val PtMatchingTables248Strings = MatchingTables248Strings(
    rulesTitle = "Combinando as Tabuadas de 2, 4 e 8",
)

val EsMatchingTables248Strings = MatchingTables248Strings(
    rulesTitle = "Combinando las Tablas de 2, 4 y 8",
)

val LocalMatchingTables248Strings = staticCompositionLocalOf { EnMatchingTables248Strings }

fun matchingTables248StringsForLanguage(language: String): MatchingTables248Strings = when (language) {
    "pt-BR" -> PtMatchingTables248Strings
    "es-ES" -> EsMatchingTables248Strings
    else -> EnMatchingTables248Strings
}
