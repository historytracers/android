// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MiscStrings(
    val multiplicationTable: String,
    val multiplicationTableWithAbacus: String,
    val abacusInOrdersReading: String,
    val carrying: String,
)

val EnMiscStrings = MiscStrings(
    multiplicationTable = "Multiplication Table",
    multiplicationTableWithAbacus = "Multiplication Table with Abacus",
    abacusInOrdersReading = "1, 10,...",
    carrying = "Carrying",
)

val PtMiscStrings = MiscStrings(
    multiplicationTable = "Tabela de Multiplica\u00e7\u00e3o",
    multiplicationTableWithAbacus = "Tabela de Multiplica\u00e7\u00e3o com \u00c1baco",
    abacusInOrdersReading = "1, 10,...",
    carrying = "Levando 1",
)

val EsMiscStrings = MiscStrings(
    multiplicationTable = "Tabla de Multiplicar",
    multiplicationTableWithAbacus = "Tabla de Multiplicar con \u00c1baco",
    abacusInOrdersReading = "1, 10,...",
    carrying = "Llevando 1",
)

val LocalMiscStrings = staticCompositionLocalOf { EnMiscStrings }

fun miscStringsForLanguage(language: String): MiscStrings = when (language) {
    "pt-BR" -> PtMiscStrings
    "es-ES" -> EsMiscStrings
    else -> EnMiscStrings
}
