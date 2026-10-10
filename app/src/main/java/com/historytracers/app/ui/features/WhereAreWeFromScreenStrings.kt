// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class WhereAreWeFromScreenStrings(
    val title: String,
    val sharedOrigin: String,
    val matterAndEnergy: String,
    val everythingWasTogether: String,
    val sharedWithWho: String,
    val theSameResult: String,
    val halfPiece: String,
    val oddOrEven: String,
    val oddOrEvenGame: String,
)

val EnWhereAreWeFromScreenStrings = WhereAreWeFromScreenStrings(
    title = "Where Are We From?",
    sharedOrigin = "Shared Origin",
    matterAndEnergy = "Matter and Energy",
    everythingWasTogether = "Everything Was Together",
    sharedWithWho = "Shared with Who?",
    theSameResult = "The Same Result",
    halfPiece = "Half Piece",
    oddOrEven = "Odd or Even?",
    oddOrEvenGame = "Odd or Even? (Game)",
)

val PtWhereAreWeFromScreenStrings = WhereAreWeFromScreenStrings(
    title = "De onde somos?",
    sharedOrigin = "Origem Compartilhada",
    matterAndEnergy = "Mat\u00e9ria e Energia",
    everythingWasTogether = "Tudo Estava Junto",
    sharedWithWho = "Compartilhado com Quem?",
    theSameResult = "O Mesmo Resultado",
    halfPiece = "Meio Peda\u00e7o",
    oddOrEven = "Par ou \u00cdmpar?",
    oddOrEvenGame = "Par ou \u00cdmpar? (Jogo)",
)

val EsWhereAreWeFromScreenStrings = WhereAreWeFromScreenStrings(
    title = "\u00bfDe d\u00f3nde somos?",
    sharedOrigin = "Origen Compartido",
    matterAndEnergy = "Materia y Energ\u00eda",
    everythingWasTogether = "Todo Estaba Junto",
    sharedWithWho = "\u00bfCompartido con Qui\u00e9n?",
    theSameResult = "El Mismo Resultado",
    halfPiece = "Medio Pedazo",
    oddOrEven = "\u00bfPar o Impar?",
    oddOrEvenGame = "\u00bfPar o Impar? (Juego)",
)

val LocalWhereAreWeFromScreenStrings = staticCompositionLocalOf { EnWhereAreWeFromScreenStrings }

fun whereAreWeFromScreenStringsForLanguage(language: String): WhereAreWeFromScreenStrings = when (language) {
    "pt-BR" -> PtWhereAreWeFromScreenStrings
    "es-ES" -> EsWhereAreWeFromScreenStrings
    else -> EnWhereAreWeFromScreenStrings
}
