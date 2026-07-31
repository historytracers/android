// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SplashScreenStrings(
    val tracers: String,
)

val EnSplashScreenStrings = SplashScreenStrings(
    tracers = "Tracers",
)

val PtSplashScreenStrings = SplashScreenStrings(
    tracers = "Tracers",
)

val EsSplashScreenStrings = SplashScreenStrings(
    tracers = "Tracers",
)

val LocalSplashScreenStrings = staticCompositionLocalOf { EnSplashScreenStrings }

fun splashScreenStringsForLanguage(language: String): SplashScreenStrings = when (language) {
    "pt-BR" -> PtSplashScreenStrings
    "es-ES" -> EsSplashScreenStrings
    else -> EnSplashScreenStrings
}
