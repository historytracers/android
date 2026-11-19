// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class CompleteTheMultiplicationTableStrings(
    val rulesTitle: String,
    val gameTitle: String,
    val tableLabel: String,
    val randomTable: String,
    val groupLabel: String,
    val randomObject: String,
    val tableHeader: String,
    val clickHint: String,
    val tipMessage: String,
    val completeMessage: String,
    val groupsDescription: String,
    val answerDescription: String,
)

val EnCompleteTheMultiplicationTableStrings = CompleteTheMultiplicationTableStrings(
    rulesTitle = "Complete the Multiplication Table",
    gameTitle = "The Game",
    tableLabel = "Multiplication Table:",
    randomTable = "\uD83C\uDFB2 Random",
    groupLabel = "Group image:",
    randomObject = "\uD83C\uDFB2 Random",
    tableHeader = "Table of %d",
    clickHint = "Tap a filled result on the left to see its groups here.",
    tipMessage = "We are expecting a number between %d and %d, and it is %d positions from each one of these values.",
    completeMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Correct! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou completed the whole table!",
    groupsDescription = "Groups: %d. Characters per group: %d.",
    answerDescription = "Table of %d: %d \u00D7 %d",
)

val PtCompleteTheMultiplicationTableStrings = CompleteTheMultiplicationTableStrings(
    rulesTitle = "Complete a Tabela de Multiplica\u00E7\u00E3o",
    gameTitle = "O Jogo",
    tableLabel = "Tabuada:",
    randomTable = "\uD83C\uDFB2 Aleat\u00F3rio",
    groupLabel = "Imagem do grupo:",
    randomObject = "\uD83C\uDFB2 Aleat\u00F3rio",
    tableHeader = "Tabuada do %d",
    clickHint = "Toque em um resultado preenchido \u00E0 esquerda para ver os grupos dele aqui.",
    tipMessage = "Estamos esperando um n\u00FAmero entre %d e %d, e ele est\u00E1 a %d posi\u00E7\u00F5es de cada um desses valores.",
    completeMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Correto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00EA completou a tabela toda!",
    groupsDescription = "Grupos: %d. Caracteres por grupo: %d.",
    answerDescription = "Tabuada do %d: %d \u00D7 %d",
)

val EsCompleteTheMultiplicationTableStrings = CompleteTheMultiplicationTableStrings(
    rulesTitle = "Completa la Tabla de Multiplicar",
    gameTitle = "El Juego",
    tableLabel = "Tabla de Multiplicar:",
    randomTable = "\uD83C\uDFB2 Aleatorio",
    groupLabel = "Imagen del grupo:",
    randomObject = "\uD83C\uDFB2 Aleatorio",
    tableHeader = "Tabla del %d",
    clickHint = "Toca un resultado completo a la izquierda para ver sus grupos aqu\u00ED.",
    tipMessage = "Esperamos un n\u00FAmero entre %d y %d, y est\u00E1 a %d posiciones de cada uno de estos valores.",
    completeMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00A1Correcto! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00A1Completaste toda la tabla!",
    groupsDescription = "Grupos: %d. Caracteres por grupo: %d.",
    answerDescription = "Tabla del %d: %d \u00D7 %d",
)

val LocalCompleteTheMultiplicationTableStrings =
    staticCompositionLocalOf { EnCompleteTheMultiplicationTableStrings }

fun completeTheMultiplicationTableStringsForLanguage(language: String): CompleteTheMultiplicationTableStrings = when (language) {
    "pt-BR" -> PtCompleteTheMultiplicationTableStrings
    "es-ES" -> EsCompleteTheMultiplicationTableStrings
    else -> EnCompleteTheMultiplicationTableStrings
}
