// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class IndexScreenStrings(
    val runningAndGrowing: String,
)

val EnIndexScreenStrings = IndexScreenStrings(
    runningAndGrowing = "Running and growing",
)

val PtIndexScreenStrings = IndexScreenStrings(
    runningAndGrowing = "Correndo e crescendo",
)

val EsIndexScreenStrings = IndexScreenStrings(
    runningAndGrowing = "Corriendo y creciendo",
)

val LocalIndexScreenStrings = staticCompositionLocalOf { EnIndexScreenStrings }

fun indexScreenStringsForLanguage(language: String): IndexScreenStrings = when (language) {
    "pt-BR" -> PtIndexScreenStrings
    "es-ES" -> EsIndexScreenStrings
    else -> EnIndexScreenStrings
}
