// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class AnotherWayToCountScreenStrings(
    val quipus: String,
    val practicingWithQuipus: String,
)

val EnAnotherWayToCountScreenStrings = AnotherWayToCountScreenStrings(
    quipus = "Quipus",
    practicingWithQuipus = "Practicing with Quipus",
)

val PtAnotherWayToCountScreenStrings = AnotherWayToCountScreenStrings(
    quipus = "Quipus",
    practicingWithQuipus = "Praticando com Quipus",
)

val EsAnotherWayToCountScreenStrings = AnotherWayToCountScreenStrings(
    quipus = "Quipus",
    practicingWithQuipus = "Practicando con Quipus",
)

val LocalAnotherWayToCountScreenStrings = staticCompositionLocalOf { EnAnotherWayToCountScreenStrings }

fun anotherWayToCountScreenStringsForLanguage(language: String): AnotherWayToCountScreenStrings = when (language) {
    "pt-BR" -> PtAnotherWayToCountScreenStrings
    "es-ES" -> EsAnotherWayToCountScreenStrings
    else -> EnAnotherWayToCountScreenStrings
}
