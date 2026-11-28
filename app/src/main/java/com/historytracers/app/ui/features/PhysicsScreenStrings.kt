// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PhysicsScreenStrings(
    val title: String,
    val fromBeginningToNow: String,
    val comingSoon: String,
)

val EnPhysicsScreenStrings = PhysicsScreenStrings(
    title = "The Laws of the Universe",
    fromBeginningToNow = "From Beginning to Now",
    comingSoon = "New classes are coming soon.",
)

val PtPhysicsScreenStrings = PhysicsScreenStrings(
    title = "As leis do universo",
    fromBeginningToNow = "Do início até agora",
    comingSoon = "Novas aulas estão chegando em breve.",
)

val EsPhysicsScreenStrings = PhysicsScreenStrings(
    title = "Las leyes del universo",
    fromBeginningToNow = "Del principio hasta ahora",
    comingSoon = "Pronto habrá nuevas clases.",
)

val LocalPhysicsScreenStrings = staticCompositionLocalOf { EnPhysicsScreenStrings }

fun physicsScreenStringsForLanguage(language: String): PhysicsScreenStrings = when (language) {
    "pt-BR" -> PtPhysicsScreenStrings
    "es-ES" -> EsPhysicsScreenStrings
    else -> EnPhysicsScreenStrings
}
