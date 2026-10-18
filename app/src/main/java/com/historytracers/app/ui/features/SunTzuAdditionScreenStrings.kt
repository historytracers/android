// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SunTzuAdditionScreenStrings(
    val title: String,
)

val EnSunTzuAdditionScreenStrings = SunTzuAdditionScreenStrings(
    title = "Sun Tzu and Addition",
)

val PtSunTzuAdditionScreenStrings = SunTzuAdditionScreenStrings(
    title = "Sun Tzu e a Adi\u00e7\u00e3o",
)

val EsSunTzuAdditionScreenStrings = SunTzuAdditionScreenStrings(
    title = "Sun Tzu y la Suma",
)

val LocalSunTzuAdditionScreenStrings = staticCompositionLocalOf { EnSunTzuAdditionScreenStrings }

fun sunTzuAdditionScreenStringsForLanguage(language: String): SunTzuAdditionScreenStrings = when (language) {
    "pt-BR" -> PtSunTzuAdditionScreenStrings
    "es-ES" -> EsSunTzuAdditionScreenStrings
    else -> EnSunTzuAdditionScreenStrings
}
