// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class IAmNotLikeYouScreenStrings(
    val weAreEquals: String,
)

val EnIAmNotLikeYouScreenStrings = IAmNotLikeYouScreenStrings(
    weAreEquals = "We Are Equals",
)

val PtIAmNotLikeYouScreenStrings = IAmNotLikeYouScreenStrings(
    weAreEquals = "N\u00f3s somos iguais",
)

val EsIAmNotLikeYouScreenStrings = IAmNotLikeYouScreenStrings(
    weAreEquals = "Somos iguales",
)

val LocalIAmNotLikeYouScreenStrings = staticCompositionLocalOf { EnIAmNotLikeYouScreenStrings }

fun iAmNotLikeYouScreenStringsForLanguage(language: String): IAmNotLikeYouScreenStrings = when (language) {
    "pt-BR" -> PtIAmNotLikeYouScreenStrings
    "es-ES" -> EsIAmNotLikeYouScreenStrings
    else -> EnIAmNotLikeYouScreenStrings
}
