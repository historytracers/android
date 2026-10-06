// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class AroundTheWorldScreenStrings(
    val instruction: String,
    val missingInstruction: String,
    val correctMessage: String,
    val targetLabel: String,
    val home: String,
)

val EnAroundTheWorldScreenStrings = AroundTheWorldScreenStrings(
    instruction = "Represent the number %d with the tool below.",
    missingInstruction = "Find the missing number in the sequence.",
    correctMessage = "\uD83C\uDF89 Correct! You earned 2 points. \uD83C\uDF89",
    targetLabel = "Represent: %d",
    home = "Main Menu",
)

val PtAroundTheWorldScreenStrings = AroundTheWorldScreenStrings(
    instruction = "Represente o n\u00famero %d com a ferramenta abaixo.",
    missingInstruction = "Encontre o n\u00famero que falta na sequ\u00eancia.",
    correctMessage = "\uD83C\uDF89 Correto! Voc\u00ea ganhou 2 pontos. \uD83C\uDF89",
    targetLabel = "Represente: %d",
    home = "Menu Principal",
)

val EsAroundTheWorldScreenStrings = AroundTheWorldScreenStrings(
    instruction = "Representa el n\u00famero %d con la herramienta de abajo.",
    missingInstruction = "Encuentra el n\u00famero que falta en la secuencia.",
    correctMessage = "\uD83C\uDF89 \u00a1Correcto! Ganaste 2 puntos. \uD83C\uDF89",
    targetLabel = "Representa: %d",
    home = "Men\u00fa Principal",
)

val LocalAroundTheWorldScreenStrings = staticCompositionLocalOf { EnAroundTheWorldScreenStrings }

fun aroundTheWorldScreenStringsForLanguage(language: String): AroundTheWorldScreenStrings = when (language) {
    "pt-BR" -> PtAroundTheWorldScreenStrings
    "es-ES" -> EsAroundTheWorldScreenStrings
    else -> EnAroundTheWorldScreenStrings
}
