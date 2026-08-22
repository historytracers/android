// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class IAmNotLikeYouScreenStrings(
    val toBeOrNotToBe: String,
    val thinkingEmoji: String,
)

val EnIAmNotLikeYouScreenStrings = IAmNotLikeYouScreenStrings(
    toBeOrNotToBe = "To Be or Not to Be",
    thinkingEmoji = "\uD83E\uDD14",
)

val PtIAmNotLikeYouScreenStrings = IAmNotLikeYouScreenStrings(
    toBeOrNotToBe = "Ser ou n\u00e3o ser",
    thinkingEmoji = "\uD83E\uDD14",
)

val EsIAmNotLikeYouScreenStrings = IAmNotLikeYouScreenStrings(
    toBeOrNotToBe = "Ser o no ser",
    thinkingEmoji = "\uD83E\uDD14",
)

val LocalIAmNotLikeYouScreenStrings = staticCompositionLocalOf { EnIAmNotLikeYouScreenStrings }

fun iAmNotLikeYouScreenStringsForLanguage(language: String): IAmNotLikeYouScreenStrings = when (language) {
    "pt-BR" -> PtIAmNotLikeYouScreenStrings
    "es-ES" -> EsIAmNotLikeYouScreenStrings
    else -> EnIAmNotLikeYouScreenStrings
}
