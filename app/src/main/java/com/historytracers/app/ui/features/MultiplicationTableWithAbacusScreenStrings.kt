// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplicationTableWithAbacusScreenStrings(
    val whatIsIt: String,
    val multiplicationTableDescription: String,
)

val EnMultiplicationTableWithAbacusScreenStrings = MultiplicationTableWithAbacusScreenStrings(
    whatIsIt = "What is it?",
    multiplicationTableDescription = "Select a number, then tap Auto to watch the abacus display each multiplication step, or tap Next Step to advance manually. Complete all 10 steps to finish.",
)

val PtMultiplicationTableWithAbacusScreenStrings = MultiplicationTableWithAbacusScreenStrings(
    whatIsIt = "O que \u00e9 isto?",
    multiplicationTableDescription = "Selecione um n\u00famero, depois toque em Autom\u00e1tico para ver o \u00e1baco mostrar cada passo da multiplica\u00e7\u00e3o, ou toque em Pr\u00f3ximo Passo para avan\u00e7ar manualmente. Complete todos os 10 passos para finalizar.",
)

val EsMultiplicationTableWithAbacusScreenStrings = MultiplicationTableWithAbacusScreenStrings(
    whatIsIt = "\u00bfQu\u00e9 es esto?",
    multiplicationTableDescription = "Selecciona un n\u00famero, luego toca Autom\u00e1tico para ver el \u00e1baco mostrar cada paso de la multiplicaci\u00f3n, o toca Siguiente Paso para avanzar manualmente. Completa los 10 pasos para finalizar.",
)

val LocalMultiplicationTableWithAbacusScreenStrings = staticCompositionLocalOf { EnMultiplicationTableWithAbacusScreenStrings }

fun multiplicationTableWithAbacusScreenStringsForLanguage(language: String): MultiplicationTableWithAbacusScreenStrings = when (language) {
    "pt-BR" -> PtMultiplicationTableWithAbacusScreenStrings
    "es-ES" -> EsMultiplicationTableWithAbacusScreenStrings
    else -> EnMultiplicationTableWithAbacusScreenStrings
}
