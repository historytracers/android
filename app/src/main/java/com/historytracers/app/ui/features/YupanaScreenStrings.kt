// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class YupanaScreenStrings(
    val movingInYupana: String,
    val practicingMultiplicationYupana: String,
)

val EnYupanaScreenStrings = YupanaScreenStrings(
    movingInYupana = "Moving in Yupana",
    practicingMultiplicationYupana = "Practicing Multiplication with Yupana",
)

val PtYupanaScreenStrings = YupanaScreenStrings(
    movingInYupana = "Movendo no Yupana",
    practicingMultiplicationYupana = "Praticando Multiplica\u00e7\u00e3o com a Yupana",
)

val EsYupanaScreenStrings = YupanaScreenStrings(
    movingInYupana = "Movi\u00e9ndose en Yupana",
    practicingMultiplicationYupana = "Practicando Multiplicaci\u00f3n con la Yupana",
)

val LocalYupanaScreenStrings = staticCompositionLocalOf { EnYupanaScreenStrings }

fun yupanaScreenStringsForLanguage(language: String): YupanaScreenStrings = when (language) {
    "pt-BR" -> PtYupanaScreenStrings
    "es-ES" -> EsYupanaScreenStrings
    else -> EnYupanaScreenStrings
}
