// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class WhereAreWeFromScreenStrings(
    val sharedOrigin: String,
    val matterAndEnergy: String,
    val everythingWasTogether: String,
)

val EnWhereAreWeFromScreenStrings = WhereAreWeFromScreenStrings(
    sharedOrigin = "Shared Origin",
    matterAndEnergy = "Matter and Energy",
    everythingWasTogether = "Everything Was Together",
)

val PtWhereAreWeFromScreenStrings = WhereAreWeFromScreenStrings(
    sharedOrigin = "Origem Compartilhada",
    matterAndEnergy = "Mat\u00e9ria e Energia",
    everythingWasTogether = "Tudo Estava Junto",
)

val EsWhereAreWeFromScreenStrings = WhereAreWeFromScreenStrings(
    sharedOrigin = "Origen Compartido",
    matterAndEnergy = "Materia y Energ\u00eda",
    everythingWasTogether = "Todo Estaba Junto",
)

val LocalWhereAreWeFromScreenStrings = staticCompositionLocalOf { EnWhereAreWeFromScreenStrings }

fun whereAreWeFromScreenStringsForLanguage(language: String): WhereAreWeFromScreenStrings = when (language) {
    "pt-BR" -> PtWhereAreWeFromScreenStrings
    "es-ES" -> EsWhereAreWeFromScreenStrings
    else -> EnWhereAreWeFromScreenStrings
}
