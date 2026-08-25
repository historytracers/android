// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class IAmNotLikeYouScreenStrings(
    val toBeOrNotToBe: String,
    val totallyEqual: String,
    val equalityInHistoryPyramids: String,
    val equalityInHistoryMetate: String,
    val equalSameGroupOrDifferent: String,
    val thinkingEmoji: String,
)

val EnIAmNotLikeYouScreenStrings = IAmNotLikeYouScreenStrings(
    toBeOrNotToBe = "To Be or Not to Be",
    totallyEqual = "Totally Equal",
    equalityInHistoryPyramids = "Equality in History (Pyramids)",
    equalityInHistoryMetate = "Equality in History (Metate)",
    equalSameGroupOrDifferent = "Equal, Same Group, or Different?",
    thinkingEmoji = "\uD83E\uDD14",
)

val PtIAmNotLikeYouScreenStrings = IAmNotLikeYouScreenStrings(
    toBeOrNotToBe = "Ser ou n\u00e3o ser",
    totallyEqual = "Totalmente iguais",
    equalityInHistoryPyramids = "Igualdade na Hist\u00f3ria (Pir\u00e2mides)",
    equalityInHistoryMetate = "Igualdade na Hist\u00f3ria (Metate)",
    equalSameGroupOrDifferent = "Iguais, mesmo grupo ou diferentes?",
    thinkingEmoji = "\uD83E\uDD14",
)

val EsIAmNotLikeYouScreenStrings = IAmNotLikeYouScreenStrings(
    toBeOrNotToBe = "Ser o no ser",
    totallyEqual = "Totalmente iguales",
    equalityInHistoryPyramids = "Igualdad en la Historia (Pir\u00e1mides)",
    equalityInHistoryMetate = "Igualdad en la Historia (Metate)",
    equalSameGroupOrDifferent = "\u00bfIguales, mismo grupo o diferentes?",
    thinkingEmoji = "\uD83E\uDD14",
)

val LocalIAmNotLikeYouScreenStrings = staticCompositionLocalOf { EnIAmNotLikeYouScreenStrings }

fun iAmNotLikeYouScreenStringsForLanguage(language: String): IAmNotLikeYouScreenStrings = when (language) {
    "pt-BR" -> PtIAmNotLikeYouScreenStrings
    "es-ES" -> EsIAmNotLikeYouScreenStrings
    else -> EnIAmNotLikeYouScreenStrings
}
