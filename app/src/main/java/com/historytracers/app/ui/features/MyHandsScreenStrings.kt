// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MyHandsScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
)

val EnMyHandsScreenStrings = MyHandsScreenStrings(
    title = "My hands",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new in your life, so we are going to reward you with 1 point in your score.",
)

val PtMyHandsScreenStrings = MyHandsScreenStrings(
    title = "Minhas m\u00e3os",
    wrongAnswerMessage = "Esta n\u00e3o \u00e9 a resposta esperada. Mas voc\u00ea aprendeu algo novo na sua vida, ent\u00e3o vamos recompens\u00e1-lo com 1 ponto na sua pontua\u00e7\u00e3o.",
)

val EsMyHandsScreenStrings = MyHandsScreenStrings(
    title = "Mis manos",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo en tu vida, as\u00ed que vamos a recompensarte con 1 punto en tu puntuaci\u00f3n.",
)

val LocalMyHandsScreenStrings = staticCompositionLocalOf { EnMyHandsScreenStrings }

fun myHandsScreenStringsForLanguage(language: String): MyHandsScreenStrings = when (language) {
    "pt-BR" -> PtMyHandsScreenStrings
    "es-ES" -> EsMyHandsScreenStrings
    else -> EnMyHandsScreenStrings
}
