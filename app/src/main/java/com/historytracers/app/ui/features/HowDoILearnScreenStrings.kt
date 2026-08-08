// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class HowDoILearnScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
    val chartLabels: List<String>,
)

val EnHowDoILearnScreenStrings = HowDoILearnScreenStrings(
    title = "How Do We Learn?",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new in your life, so we are going to reward you with 1 point in your score.",
    chartLabels = listOf(
        "Verbal-linguistic",
        "Logical-mathematical",
        "Visual-spatial",
        "Musical",
        "Bodily-kinesthetic",
        "Intrapersonal",
        "Interpersonal",
        "Naturalistic",
        "Existential",
    ),
)

val PtHowDoILearnScreenStrings = HowDoILearnScreenStrings(
    title = "Como aprendemos?",
    wrongAnswerMessage = "Esta n\u00e3o \u00e9 a resposta esperada. Mas voc\u00ea aprendeu algo novo na sua vida, ent\u00e3o vamos recompens\u00e1-lo com 1 ponto na sua pontua\u00e7\u00e3o.",
    chartLabels = listOf(
        "Verbal-Lingu\u00edstica",
        "L\u00f3gica-Matem\u00e1tica",
        "Espacial-Visual",
        "Musical",
        "Corporal-Cinest\u00e9sica",
        "Intrapessoal",
        "Interpessoal",
        "Naturalista",
        "Existencial",
    ),
)

val EsHowDoILearnScreenStrings = HowDoILearnScreenStrings(
    title = "\u00bfC\u00f3mo aprendemos?",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo en tu vida, as\u00ed que vamos a recompensarte con 1 punto en tu puntuaci\u00f3n.",
    chartLabels = listOf(
        "Verbal-ling\u00fc\u00edstica",
        "L\u00f3gico-matem\u00e1tica",
        "Espacial-visual",
        "Musical",
        "Corporal-cinest\u00e9sica",
        "Intrapersonal",
        "Interpersonal",
        "Naturalista",
        "Existencial",
    ),
)

val LocalHowDoILearnScreenStrings = staticCompositionLocalOf { EnHowDoILearnScreenStrings }

fun howDoILearnScreenStringsForLanguage(language: String): HowDoILearnScreenStrings = when (language) {
    "pt-BR" -> PtHowDoILearnScreenStrings
    "es-ES" -> EsHowDoILearnScreenStrings
    else -> EnHowDoILearnScreenStrings
}
