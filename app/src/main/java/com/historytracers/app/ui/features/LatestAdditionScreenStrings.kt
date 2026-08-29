// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class LatestAdditionScreenStrings(
    val title: String,
    val equalsSign: String,
)

val EnLatestAdditionScreenStrings = LatestAdditionScreenStrings(
    title = "Latest Addition",
    equalsSign = "=",
)

val PtLatestAdditionScreenStrings = LatestAdditionScreenStrings(
    title = "Adi\u00e7\u00e3o Mais Recente",
    equalsSign = "=",
)

val EsLatestAdditionScreenStrings = LatestAdditionScreenStrings(
    title = "Adici\u00f3n M\u00e1s Reciente",
    equalsSign = "=",
)

val LocalLatestAdditionScreenStrings = staticCompositionLocalOf { EnLatestAdditionScreenStrings }

fun latestAdditionScreenStringsForLanguage(language: String): LatestAdditionScreenStrings = when (language) {
    "pt-BR" -> PtLatestAdditionScreenStrings
    "es-ES" -> EsLatestAdditionScreenStrings
    else -> EnLatestAdditionScreenStrings
}
