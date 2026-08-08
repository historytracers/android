// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MyBodyScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
)

val EnMyBodyScreenStrings = MyBodyScreenStrings(
    title = "My Body",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new in your life, so we are going to reward you with 1 point in your score.",
)

val PtMyBodyScreenStrings = MyBodyScreenStrings(
    title = "Meu corpo",
    wrongAnswerMessage = "Esta n\u00e3o \u00e9 a resposta esperada. Mas voc\u00ea aprendeu algo novo na sua vida, ent\u00e3o vamos recompens\u00e1-lo com 1 ponto na sua pontua\u00e7\u00e3o.",
)

val EsMyBodyScreenStrings = MyBodyScreenStrings(
    title = "Mi cuerpo",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo en tu vida, as\u00ed que vamos a recompensarte con 1 punto en tu puntuaci\u00f3n.",
)

val LocalMyBodyScreenStrings = staticCompositionLocalOf { EnMyBodyScreenStrings }

fun myBodyScreenStringsForLanguage(language: String): MyBodyScreenStrings = when (language) {
    "pt-BR" -> PtMyBodyScreenStrings
    "es-ES" -> EsMyBodyScreenStrings
    else -> EnMyBodyScreenStrings
}
