// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SequenceGameOrdersScreenStrings(
    val title: String,
    val instruction: String,
    val units: String,
    val tens: String,
    val hundreds: String,
    val thousands: String,
    val tenThousands: String,
    val hundredThousands: String,
    val millions: String,
    val tenMillions: String,
    val hundredMillions: String,
    val billions: String,
    val newGame: String,
    val completionMessage: String,
)

val EnSequenceGameOrdersScreenStrings = SequenceGameOrdersScreenStrings(
    title = "Sequence Game (Orders)",
    instruction = "Connect each number on the left with its order name on the right. Select a number, then select its name to pair them.",
    units = "Units",
    tens = "Tens",
    hundreds = "Hundreds",
    thousands = "Thousands",
    tenThousands = "Ten Thousands",
    hundredThousands = "Hundred Thousands",
    millions = "Millions",
    tenMillions = "Ten Millions",
    hundredMillions = "Hundred Millions",
    billions = "Billions",
    newGame = "New Game",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou matched all numbers with their order names!\nGreat job!",
)

val PtSequenceGameOrdersScreenStrings = SequenceGameOrdersScreenStrings(
    title = "Jogo da sequ\u00eancia (Ordens)",
    instruction = "Ligue cada n\u00famero \u00e0 esquerda ao nome de sua ordem \u00e0 direita. Selecione um n\u00famero e depois selecione seu nome para formar o par.",
    units = "Unidades",
    tens = "Dezenas",
    hundreds = "Centenas",
    thousands = "Milhares",
    tenThousands = "Dezenas de milhar",
    hundredThousands = "Centenas de milhar",
    millions = "Milh\u00f5es",
    tenMillions = "Dezenas de milh\u00f5es",
    hundredMillions = "Centenas de milh\u00f5es",
    billions = "Bilh\u00f5es",
    newGame = "Novo Jogo",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFEITO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea ligou todos os n\u00fameros \u00e0s suas ordens!\n\u00d3timo trabalho!",
)

val EsSequenceGameOrdersScreenStrings = SequenceGameOrdersScreenStrings(
    title = "Juego de secuencia (\u00d3rdenes)",
    instruction = "Conecta cada n\u00famero de la izquierda con el nombre de su orden de la derecha. Selecciona un n\u00famero y luego selecciona su nombre para formar la pareja.",
    units = "Unidades",
    tens = "Decenas",
    hundreds = "Centenas",
    thousands = "Miles",
    tenThousands = "Decenas de mil",
    hundredThousands = "Centenas de mil",
    millions = "Millones",
    tenMillions = "Decenas de millones",
    hundredMillions = "Centenas de millones",
    billions = "Billones",
    newGame = "Nuevo Juego",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1PERFECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1Conectaste todos los n\u00fameros con sus \u00f3rdenes!\n\u00a1Gran trabajo!",
)

val LocalSequenceGameOrdersScreenStrings = staticCompositionLocalOf { EnSequenceGameOrdersScreenStrings }

fun sequenceGameOrdersScreenStringsForLanguage(language: String): SequenceGameOrdersScreenStrings = when (language) {
    "pt-BR" -> PtSequenceGameOrdersScreenStrings
    "es-ES" -> EsSequenceGameOrdersScreenStrings
    else -> EnSequenceGameOrdersScreenStrings
}
