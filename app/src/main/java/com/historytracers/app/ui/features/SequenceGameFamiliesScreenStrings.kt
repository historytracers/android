// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SequenceGameFamiliesScreenStrings(
    val title: String,
    val instruction: String,
    val upArrow: String,
    val downArrow: String,
    val newGame: String,
    val completionMessage: String,
)

val EnSequenceGameFamiliesScreenStrings = SequenceGameFamiliesScreenStrings(
    title = "Sequence Game (Families)",
    instruction = "Complete the sequence by finding the missing number. Use the arrows to adjust the value until it matches.",
    upArrow = "Increase value",
    downArrow = "Decrease value",
    newGame = "New Game",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CORRECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou found the missing number!\nGreat job!",
)

val PtSequenceGameFamiliesScreenStrings = SequenceGameFamiliesScreenStrings(
    title = "Jogo da sequ\u00eancia (Fam\u00edlias)",
    instruction = "Complete a sequ\u00eancia descobrindo o n\u00famero que falta. Use as setas para ajustar o valor at\u00e9 que ele corresponda.",
    upArrow = "Aumentar valor",
    downArrow = "Diminuir valor",
    newGame = "Novo Jogo",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CORRETO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea encontrou o n\u00famero que faltava!\n\u00d3timo trabalho!",
)

val EsSequenceGameFamiliesScreenStrings = SequenceGameFamiliesScreenStrings(
    title = "Juego de secuencia (Familias)",
    instruction = "Completa la secuencia descubriendo el n\u00famero que falta. Usa las flechas para ajustar el valor hasta que coincida.",
    upArrow = "Aumentar valor",
    downArrow = "Disminuir valor",
    newGame = "Nuevo Juego",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1CORRECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1Encontraste el n\u00famero que faltaba!\n\u00a1Gran trabajo!",
)

val LocalSequenceGameFamiliesScreenStrings = staticCompositionLocalOf { EnSequenceGameFamiliesScreenStrings }

fun sequenceGameFamiliesScreenStringsForLanguage(language: String): SequenceGameFamiliesScreenStrings = when (language) {
    "pt-BR" -> PtSequenceGameFamiliesScreenStrings
    "es-ES" -> EsSequenceGameFamiliesScreenStrings
    else -> EnSequenceGameFamiliesScreenStrings
}
