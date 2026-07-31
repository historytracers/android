// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MiscStrings(
    val multiplicationTable: String,
    val multiplicationTableDescription: String,
    val abacusInOrdersReading: String,
    val abacusInRereading: String,
)

val EnMiscStrings = MiscStrings(
    multiplicationTable = "Multiplication Table",
    multiplicationTableDescription = "Select a number, then tap Auto to watch the abacus display each multiplication step, or tap Next Step to advance manually. Complete all 10 steps to finish.",
    abacusInOrdersReading = "1, 10,...",
    abacusInRereading = "Abacus in rereading",
)

val PtMiscStrings = MiscStrings(
    multiplicationTable = "Tabela de Multiplica\u00e7\u00e3o",
    multiplicationTableDescription = "Selecione um n\u00famero, depois toque em Autom\u00e1tico para ver o \u00e1baco mostrar cada passo da multiplica\u00e7\u00e3o, ou toque em Pr\u00f3ximo Passo para avan\u00e7ar manualmente. Complete todos os 10 passos para finalizar.",
    abacusInOrdersReading = "1, 10,...",
    abacusInRereading = "\u00c1baco na releitura",
)

val EsMiscStrings = MiscStrings(
    multiplicationTable = "Tabla de Multiplicar",
    multiplicationTableDescription = "Selecciona un n\u00famero, luego toca Autom\u00e1tico para ver el \u00e1baco mostrar cada paso de la multiplicaci\u00f3n, o toca Siguiente Paso para avanzar manualmente. Completa los 10 pasos para finalizar.",
    abacusInOrdersReading = "1, 10,...",
    abacusInRereading = "\u00c1baco en relectura",
)

val LocalMiscStrings = staticCompositionLocalOf { EnMiscStrings }

fun miscStringsForLanguage(language: String): MiscStrings = when (language) {
    "pt-BR" -> PtMiscStrings
    "es-ES" -> EsMiscStrings
    else -> EnMiscStrings
}
