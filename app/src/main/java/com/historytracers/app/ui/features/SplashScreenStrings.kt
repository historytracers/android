// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SplashScreenStrings(
    val title: String,
    val subtitle: String,
)

val EnSplashScreenStrings = SplashScreenStrings(
    title = "History Tracers",
    subtitle = "Understand Yourself and Everything Around You",
)

val PtSplashScreenStrings = SplashScreenStrings(
    title = "History Tracers",
    subtitle = "Entenda a si mesmo e tudo ao seu redor",
)

val EsSplashScreenStrings = SplashScreenStrings(
    title = "History Tracers",
    subtitle = "Enti\u00e9ndete a ti mismo y todo lo que te rodea",
)

val LocalSplashScreenStrings = staticCompositionLocalOf { EnSplashScreenStrings }

fun splashScreenStringsForLanguage(language: String): SplashScreenStrings = when (language) {
    "pt-BR" -> PtSplashScreenStrings
    "es-ES" -> EsSplashScreenStrings
    else -> EnSplashScreenStrings
}
