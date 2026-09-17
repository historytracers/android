// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class AnotherWayToCountScreenStrings(
    val quipus: String,
)

val EnAnotherWayToCountScreenStrings = AnotherWayToCountScreenStrings(
    quipus = "Quipus",
)

val PtAnotherWayToCountScreenStrings = AnotherWayToCountScreenStrings(
    quipus = "Quipus",
)

val EsAnotherWayToCountScreenStrings = AnotherWayToCountScreenStrings(
    quipus = "Quipus",
)

val LocalAnotherWayToCountScreenStrings = staticCompositionLocalOf { EnAnotherWayToCountScreenStrings }

fun anotherWayToCountScreenStringsForLanguage(language: String): AnotherWayToCountScreenStrings = when (language) {
    "pt-BR" -> PtAnotherWayToCountScreenStrings
    "es-ES" -> EsAnotherWayToCountScreenStrings
    else -> EnAnotherWayToCountScreenStrings
}
