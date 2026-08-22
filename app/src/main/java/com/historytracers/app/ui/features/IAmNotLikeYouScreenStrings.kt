// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class IAmNotLikeYouScreenStrings(
    val toBeOrNotToBe: String,
)

val EnIAmNotLikeYouScreenStrings = IAmNotLikeYouScreenStrings(
    toBeOrNotToBe = "To Be or Not to Be",
)

val PtIAmNotLikeYouScreenStrings = IAmNotLikeYouScreenStrings(
    toBeOrNotToBe = "Ser ou n\u00e3o ser",
)

val EsIAmNotLikeYouScreenStrings = IAmNotLikeYouScreenStrings(
    toBeOrNotToBe = "Ser o no ser",
)

val LocalIAmNotLikeYouScreenStrings = staticCompositionLocalOf { EnIAmNotLikeYouScreenStrings }

fun iAmNotLikeYouScreenStringsForLanguage(language: String): IAmNotLikeYouScreenStrings = when (language) {
    "pt-BR" -> PtIAmNotLikeYouScreenStrings
    "es-ES" -> EsIAmNotLikeYouScreenStrings
    else -> EnIAmNotLikeYouScreenStrings
}
