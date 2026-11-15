// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplicationTableWeeksOfLifeScreenStrings(
    val title: String,
    val fullTitle: String,
    val weekLabel: String,
    val figureCaption: String,
)

val EnMultiplicationTableWeeksOfLifeScreenStrings = MultiplicationTableWeeksOfLifeScreenStrings(
    title = "Weeks of Life",
    fullTitle = "Multiplication Table (Weeks of Life)",
    weekLabel = "Week %d",
    figureCaption = "Figure 1: Four weeks, with the day names used by the selected calendar and app language. Each row is one week of seven days.",
)

val PtMultiplicationTableWeeksOfLifeScreenStrings = MultiplicationTableWeeksOfLifeScreenStrings(
    title = "Semanas da Vida",
    fullTitle = "Tabuada (Semanas da Vida)",
    weekLabel = "Semana %d",
    figureCaption = "Figura 1: Quatro semanas, com os nomes dos dias usados pelo calendário selecionado e pelo idioma do aplicativo. Cada linha é uma semana de sete dias.",
)

val EsMultiplicationTableWeeksOfLifeScreenStrings = MultiplicationTableWeeksOfLifeScreenStrings(
    title = "Semanas de la Vida",
    fullTitle = "Tabla de Multiplicar (Semanas de la Vida)",
    weekLabel = "Semana %d",
    figureCaption = "Figura 1: Cuatro semanas, con los nombres de los días usados por el calendario seleccionado y por el idioma de la aplicación. Cada fila es una semana de siete días.",
)

val LocalMultiplicationTableWeeksOfLifeScreenStrings =
    staticCompositionLocalOf { EnMultiplicationTableWeeksOfLifeScreenStrings }

fun multiplicationTableWeeksOfLifeScreenStringsForLanguage(
    language: String
): MultiplicationTableWeeksOfLifeScreenStrings = when (language) {
    "pt-BR" -> PtMultiplicationTableWeeksOfLifeScreenStrings
    "es-ES" -> EsMultiplicationTableWeeksOfLifeScreenStrings
    else -> EnMultiplicationTableWeeksOfLifeScreenStrings
}
