// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class AboutScreenStrings(
    val openApp: String,
    val appNotInstalled: String,
)

val EnAboutScreenStrings = AboutScreenStrings(
    openApp = "Open App",
    appNotInstalled = "App not installed",
)

val PtAboutScreenStrings = AboutScreenStrings(
    openApp = "Abrir App",
    appNotInstalled = "Aplicativo n\u00e3o instalado",
)

val EsAboutScreenStrings = AboutScreenStrings(
    openApp = "Abrir App",
    appNotInstalled = "Aplicaci\u00f3n no instalada",
)

val LocalAboutScreenStrings = staticCompositionLocalOf { EnAboutScreenStrings }

fun aboutScreenStringsForLanguage(language: String): AboutScreenStrings = when (language) {
    "pt-BR" -> PtAboutScreenStrings
    "es-ES" -> EsAboutScreenStrings
    else -> EnAboutScreenStrings
}
