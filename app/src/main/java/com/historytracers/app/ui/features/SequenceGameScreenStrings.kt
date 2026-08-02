// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SequenceGameScreenStrings(
    val instruction: String,
    val upArrow: String,
    val downArrow: String,
    val newGame: String,
    val completionMessage: String,
)

val EnSequenceGameScreenStrings = SequenceGameScreenStrings(
    instruction = "Complete the sequence by finding the missing number. Use the arrows to increase or decrease the value until it matches.",
    upArrow = "Increase value",
    downArrow = "Decrease value",
    newGame = "New Game",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CORRECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou found the missing number!\nGreat job!",
)

val PtSequenceGameScreenStrings = SequenceGameScreenStrings(
    instruction = "Complete a sequ\u00eancia descobrindo o n\u00famero que falta. Use as setas para aumentar ou diminuir o valor at\u00e9 que ele corresponda.",
    upArrow = "Aumentar valor",
    downArrow = "Diminuir valor",
    newGame = "Novo Jogo",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CORRETO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea encontrou o n\u00famero que faltava!\n\u00d3timo trabalho!",
)

val EsSequenceGameScreenStrings = SequenceGameScreenStrings(
    instruction = "Completa la secuencia descubriendo el n\u00famero que falta. Usa las flechas para aumentar o disminuir el valor hasta que coincida.",
    upArrow = "Aumentar valor",
    downArrow = "Disminuir valor",
    newGame = "Nuevo Juego",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1CORRECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1Encontraste el n\u00famero que faltaba!\n\u00a1Gran trabajo!",
)

val LocalSequenceGameScreenStrings = staticCompositionLocalOf { EnSequenceGameScreenStrings }

fun sequenceGameScreenStringsForLanguage(language: String): SequenceGameScreenStrings = when (language) {
    "pt-BR" -> PtSequenceGameScreenStrings
    "es-ES" -> EsSequenceGameScreenStrings
    else -> EnSequenceGameScreenStrings
}
