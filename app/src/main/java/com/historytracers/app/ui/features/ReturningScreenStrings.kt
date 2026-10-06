// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class ReturningScreenStrings(
    val walkingBackwards: String,
)

val EnReturningScreenStrings = ReturningScreenStrings(
    walkingBackwards = "Walking Backwards",
)

val PtReturningScreenStrings = ReturningScreenStrings(
    walkingBackwards = "Andando para Tr\u00e1s",
)

val EsReturningScreenStrings = ReturningScreenStrings(
    walkingBackwards = "Caminando Hacia Atr\u00e1s",
)

val LocalReturningScreenStrings = staticCompositionLocalOf { EnReturningScreenStrings }

fun returningScreenStringsForLanguage(language: String): ReturningScreenStrings = when (language) {
    "pt-BR" -> PtReturningScreenStrings
    "es-ES" -> EsReturningScreenStrings
    else -> EnReturningScreenStrings
}
