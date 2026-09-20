// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class TextOrNumberScreenStrings(
    val title: String,
    val tableNumeralSystem: String,
    val tableNumbers: String,
    val etruscanRoman: String,
    val hinduArabic: String,
    val etruscan: String,
    val memoryVisualizing: String,
    val memoryReinforce: String,
    val memoryStoring: String,
    val wrongAnswerMessage: String,
    val scoreDoubledMessage: String,
)

val EnTextOrNumberScreenStrings = TextOrNumberScreenStrings(
    title = "Text or Number",
    tableNumeralSystem = "Numeral system",
    tableNumbers = "Numbers",
    etruscanRoman = "Etruscan-Roman",
    hinduArabic = "Hindu-Arabic",
    etruscan = "Etruscan",
    memoryVisualizing = "Visualizing",
    memoryReinforce = "Reinforce",
    memoryStoring = "Storing",
    wrongAnswerMessage = "This is not the expected answer. But you have learned something new in your life, so we are going to reward you with 1 point in your score.",
    scoreDoubledMessage = "Great! Because you answered correctly, your score for this screen will be doubled. Keep paying attention and learning!",
)

val PtTextOrNumberScreenStrings = TextOrNumberScreenStrings(
    title = "Texto ou N\u00famero",
    tableNumeralSystem = "Sistema num\u00e9rico",
    tableNumbers = "N\u00fameros",
    etruscanRoman = "Etrusco-Romano",
    hinduArabic = "Indo-\u00e1rabe",
    etruscan = "Etrusco",
    memoryVisualizing = "Visualizar",
    memoryReinforce = "Refor\u00e7ar",
    memoryStoring = "Armazenar",
    wrongAnswerMessage = "Esta n\u00e3o \u00e9 a resposta esperada. Mas voc\u00ea aprendeu algo novo na sua vida, ent\u00e3o vamos recompens\u00e1-lo com 1 ponto na sua pontua\u00e7\u00e3o.",
    scoreDoubledMessage = "\u00d3timo! Como voc\u00ea respondeu corretamente, sua pontua\u00e7\u00e3o nesta tela ser\u00e1 dobrada. Continue prestando aten\u00e7\u00e3o e aprendendo!",
)

val EsTextOrNumberScreenStrings = TextOrNumberScreenStrings(
    title = "Texto o N\u00famero",
    tableNumeralSystem = "Sistema num\u00e9rico",
    tableNumbers = "N\u00fameros",
    etruscanRoman = "Etrusco-Romano",
    hinduArabic = "Indo\u00e1rabigo",
    etruscan = "Etrusco",
    memoryVisualizing = "Visualizar",
    memoryReinforce = "Reforzar",
    memoryStoring = "Almacenar",
    wrongAnswerMessage = "Esta no es la respuesta esperada. Pero has aprendido algo nuevo en tu vida, as\u00ed que vamos a recompensarte con 1 punto en tu puntuaci\u00f3n.",
    scoreDoubledMessage = "\u00a1Genial! Como respondiste correctamente, tu puntuaci\u00f3n en esta pantalla se duplicar\u00e1. \u00a1Sigue prestando atenci\u00f3n y aprendiendo!",
)

val LocalTextOrNumberScreenStrings = staticCompositionLocalOf { EnTextOrNumberScreenStrings }

fun textOrNumberScreenStringsForLanguage(language: String): TextOrNumberScreenStrings = when (language) {
    "pt-BR" -> PtTextOrNumberScreenStrings
    "es-ES" -> EsTextOrNumberScreenStrings
    else -> EnTextOrNumberScreenStrings
}
