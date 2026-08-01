// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class LearningInLayersScreenStrings(
    val title: String,
    val wrongAnswerMessage: String,
)

val EnLearningInLayersScreenStrings = LearningInLayersScreenStrings(
    title = "Learning in Layers",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new in your life, so we are going to reward you with 1 point in your score.",
)

val PtLearningInLayersScreenStrings = LearningInLayersScreenStrings(
    title = "Aprendendo em camadas",
    wrongAnswerMessage = "Esta n\u00e3o \u00e9 a resposta esperada. Mas voc\u00ea aprendeu algo novo na sua vida, ent\u00e3o vamos recompens\u00e1-lo com 1 ponto na sua pontua\u00e7\u00e3o.",
)

val EsLearningInLayersScreenStrings = LearningInLayersScreenStrings(
    title = "Aprendiendo en capas",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo en tu vida, as\u00ed que vamos a recompensarte con 1 punto en tu puntuaci\u00f3n.",
)

val LocalLearningInLayersScreenStrings = staticCompositionLocalOf { EnLearningInLayersScreenStrings }

fun learningInLayersScreenStringsForLanguage(language: String): LearningInLayersScreenStrings = when (language) {
    "pt-BR" -> PtLearningInLayersScreenStrings
    "es-ES" -> EsLearningInLayersScreenStrings
    else -> EnLearningInLayersScreenStrings
}
