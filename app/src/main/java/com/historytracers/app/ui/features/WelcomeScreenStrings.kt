// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class WelcomeScreenStrings(
    val title: String,
    val message: String,
    val nerdSmile: String,
    val thinkingSmile: String,
    val partySmile: String,
    val inloveSmile: String,
)

val EnWelcomeScreenStrings = WelcomeScreenStrings(
    title = "Welcome",
    message = "You will improve your knowledge and better understand yourself, your family, and everything around you. Enjoy your learning journey!",
    nerdSmile = "\uD83E\uDD13",
    thinkingSmile = "\uD83E\uDD14",
    partySmile = "\uD83E\uDD73",
    inloveSmile = "\uD83D\uDE0D",
)

val PtWelcomeScreenStrings = WelcomeScreenStrings(
    title = "Bem-vindo(a)!",
    message = "Voc\u00ea vai melhorar seus conhecimentos e entender melhor a si mesmo, sua fam\u00edlia e tudo ao seu redor. Aproveite sua jornada de aprendizado!",
    nerdSmile = "\uD83E\uDD13",
    thinkingSmile = "\uD83E\uDD14",
    partySmile = "\uD83E\uDD73",
    inloveSmile = "\uD83D\uDE0D",
)

val EsWelcomeScreenStrings = WelcomeScreenStrings(
    title = "\u00a1Bienvenido(a)!",
    message = "Mejorar\u00e1s tus conocimientos y entender\u00e1s mejor a ti mismo, a tu familia y todo lo que te rodea. \u00a1Disfruta de tu viaje de aprendizaje!",
    nerdSmile = "\uD83E\uDD13",
    thinkingSmile = "\uD83E\uDD14",
    partySmile = "\uD83E\uDD73",
    inloveSmile = "\uD83D\uDE0D",
)

val LocalWelcomeScreenStrings = staticCompositionLocalOf { EnWelcomeScreenStrings }

fun welcomeScreenStringsForLanguage(language: String): WelcomeScreenStrings = when (language) {
    "pt-BR" -> PtWelcomeScreenStrings
    "es-ES" -> EsWelcomeScreenStrings
    else -> EnWelcomeScreenStrings
}
