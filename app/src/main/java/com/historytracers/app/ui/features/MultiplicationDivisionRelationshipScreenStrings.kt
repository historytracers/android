// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplicationDivisionRelationshipScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
    val scoreDoubledMessage: String,
)

val EnMultiplicationDivisionRelationshipScreenStrings = MultiplicationDivisionRelationshipScreenStrings(
    title = "The Relationship Between Multiplication and Division",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new in your life, so we are going to reward you with 1 point in your score.",
    scoreDoubledMessage = "Great! Because you answered correctly, your score for this screen will be doubled. Keep paying attention and learning!",
)

val PtMultiplicationDivisionRelationshipScreenStrings = MultiplicationDivisionRelationshipScreenStrings(
    title = "A Rela\u00E7\u00E3o Entre Multiplica\u00E7\u00E3o e Divis\u00E3o",
    wrongAnswerMessage = "Esta n\u00E3o \u00E9 a resposta esperada. Mas voc\u00EA aprendeu algo novo na sua vida, ent\u00E3o vamos recompens\u00E1-lo com 1 ponto na sua pontua\u00E7\u00E3o.",
    scoreDoubledMessage = "\u00D3timo! Como voc\u00EA respondeu corretamente, sua pontua\u00E7\u00E3o nesta tela ser\u00E1 dobrada. Continue prestando aten\u00E7\u00E3o e aprendendo!",
)

val EsMultiplicationDivisionRelationshipScreenStrings = MultiplicationDivisionRelationshipScreenStrings(
    title = "La Relaci\u00F3n Entre Multiplicaci\u00F3n y Divisi\u00F3n",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo en tu vida, as\u00ED que vamos a recompensarte con 1 punto en tu puntuaci\u00F3n.",
    scoreDoubledMessage = "\u00A1Genial! Como respondiste correctamente, tu puntuaci\u00F3n en esta pantalla se duplicar\u00E1. \u00A1Sigue prestando atenci\u00F3n y aprendiendo!",
)

val LocalMultiplicationDivisionRelationshipScreenStrings =
    staticCompositionLocalOf { EnMultiplicationDivisionRelationshipScreenStrings }

fun multiplicationDivisionRelationshipScreenStringsForLanguage(
    language: String
): MultiplicationDivisionRelationshipScreenStrings = when (language) {
    "pt-BR" -> PtMultiplicationDivisionRelationshipScreenStrings
    "es-ES" -> EsMultiplicationDivisionRelationshipScreenStrings
    else -> EnMultiplicationDivisionRelationshipScreenStrings
}
