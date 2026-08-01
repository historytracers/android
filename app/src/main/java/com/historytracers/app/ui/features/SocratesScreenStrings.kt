// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SocratesScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
)

val EnSocratesScreenStrings = SocratesScreenStrings(
    title = "I only know that I know nothing",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new in your life, so we are going to reward you with 1 point in your score.",
)

val PtSocratesScreenStrings = SocratesScreenStrings(
    title = "S\u00f3 sei que nada sei",
    wrongAnswerMessage = "Esta n\u00e3o \u00e9 a resposta esperada. Mas voc\u00ea aprendeu algo novo na sua vida, ent\u00e3o vamos recompens\u00e1-lo com 1 ponto na sua pontua\u00e7\u00e3o.",
)

val EsSocratesScreenStrings = SocratesScreenStrings(
    title = "Solo s\u00e9 que nada s\u00e9",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo en tu vida, as\u00ed que vamos a recompensarte con 1 punto en tu puntuaci\u00f3n.",
)

val LocalSocratesScreenStrings = staticCompositionLocalOf { EnSocratesScreenStrings }

fun socratesScreenStringsForLanguage(language: String): SocratesScreenStrings = when (language) {
    "pt-BR" -> PtSocratesScreenStrings
    "es-ES" -> EsSocratesScreenStrings
    else -> EnSocratesScreenStrings
}
