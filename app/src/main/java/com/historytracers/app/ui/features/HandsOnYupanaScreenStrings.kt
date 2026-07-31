// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class HandsOnYupanaScreenStrings(
    val handsOnYupanaDescription: String,
    val handsOnYupanaOfflineMessage: String,
    val mapswire: String,
    val drawToCount: String,
)

val EnHandsOnYupanaScreenStrings = HandsOnYupanaScreenStrings(
    handsOnYupanaDescription = "In the Tawantinsuyu region highlighted on the map, the Incas created a system that represents numerical growth in relation to our hands, and clicking on \"Draw to Count\" will teach you how the Incas represented numbers.",
    handsOnYupanaOfflineMessage = "The Tawantinsuyu map covers the modern-day regions of Ecuador, Peru, Bolivia, Argentina, and Chile.",
    mapswire = "Mapswire",
    drawToCount = "Draw to Count",
)

val PtHandsOnYupanaScreenStrings = HandsOnYupanaScreenStrings(
    handsOnYupanaDescription = "Na regi\u00e3o de Tawantinsuyu destacada no mapa, os Incas criaram um sistema que representa o crescimento num\u00e9rico em rela\u00e7\u00e3o \u00e0s nossas m\u00e3os, e clicar em \"Desenhar para Contar\" ensinar\u00e1 como os Incas representavam os n\u00fameros.",
    handsOnYupanaOfflineMessage = "O mapa de Tawantinsuyu cobre as regi\u00f5es atuais do Equador, Peru, Bol\u00edvia, Argentina e Chile.",
    mapswire = "Mapswire",
    drawToCount = "Desenhar para Contar",
)

val EsHandsOnYupanaScreenStrings = HandsOnYupanaScreenStrings(
    handsOnYupanaDescription = "En la regi\u00f3n de Tawantinsuyu resaltada en el mapa, los Incas crearon un sistema que representa el crecimiento num\u00e9rico en relaci\u00f3n con nuestras manos, y al hacer clic en \"Dibujar para Contar\" aprender\u00e1s c\u00f3mo los Incas representaban los n\u00fameros.",
    handsOnYupanaOfflineMessage = "El mapa de Tawantinsuyu cubre las regiones actuales de Ecuador, Per\u00fa, Bolivia, Argentina y Chile.",
    mapswire = "Mapswire",
    drawToCount = "Dibujar para Contar",
)

val LocalHandsOnYupanaScreenStrings = staticCompositionLocalOf { EnHandsOnYupanaScreenStrings }

fun handsOnYupanaScreenStringsForLanguage(language: String): HandsOnYupanaScreenStrings = when (language) {
    "pt-BR" -> PtHandsOnYupanaScreenStrings
    "es-ES" -> EsHandsOnYupanaScreenStrings
    else -> EnHandsOnYupanaScreenStrings
}
