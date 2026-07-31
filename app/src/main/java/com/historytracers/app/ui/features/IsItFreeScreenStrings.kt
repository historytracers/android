// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class IsItFreeScreenStrings(
    val patreon: String,
    val paypal: String,
)

val EnIsItFreeScreenStrings = IsItFreeScreenStrings(
    patreon = "Patreon",
    paypal = "PayPal",
)

val PtIsItFreeScreenStrings = IsItFreeScreenStrings(
    patreon = "Patreon",
    paypal = "PayPal",
)

val EsIsItFreeScreenStrings = IsItFreeScreenStrings(
    patreon = "Patreon",
    paypal = "PayPal",
)

val LocalIsItFreeScreenStrings = staticCompositionLocalOf { EnIsItFreeScreenStrings }

fun isItFreeScreenStringsForLanguage(language: String): IsItFreeScreenStrings = when (language) {
    "pt-BR" -> PtIsItFreeScreenStrings
    "es-ES" -> EsIsItFreeScreenStrings
    else -> EnIsItFreeScreenStrings
}
