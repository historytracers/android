// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SubjectsScreenStrings(
    val fundamentals: String,
    val fundamentalsTagline: String,
    val physics: String,
    val physicsTagline: String,
    val chemistry: String,
    val chemistryTagline: String,
    val life: String,
    val lifeTagline: String,
    val comingSoon: String,
)

val EnSubjectsScreenStrings = SubjectsScreenStrings(
    fundamentals = "Fundamentals",
    fundamentalsTagline = "Everything Has a Unique Language",
    physics = "Physics",
    physicsTagline = "The Laws of the Universe",
    chemistry = "Chemistry",
    chemistryTagline = "Matter and Us",
    life = "Life",
    lifeTagline = "What Is Life?",
    comingSoon = "New classes are coming soon.",
)

val PtSubjectsScreenStrings = SubjectsScreenStrings(
    fundamentals = "Fundamentos",
    fundamentalsTagline = "Tudo tem uma linguagem única",
    physics = "Física",
    physicsTagline = "As leis do universo",
    chemistry = "Química",
    chemistryTagline = "A matéria e nós",
    life = "Vida",
    lifeTagline = "O que é a vida?",
    comingSoon = "Novas aulas estão chegando em breve.",
)

val EsSubjectsScreenStrings = SubjectsScreenStrings(
    fundamentals = "Fundamentos",
    fundamentalsTagline = "Todo tiene un lenguaje único",
    physics = "Física",
    physicsTagline = "Las leyes del universo",
    chemistry = "Química",
    chemistryTagline = "La materia y nosotros",
    life = "Vida",
    lifeTagline = "¿Qué es la vida?",
    comingSoon = "Pronto habrá nuevas clases.",
)

val LocalSubjectsScreenStrings = staticCompositionLocalOf { EnSubjectsScreenStrings }

fun subjectsScreenStringsForLanguage(language: String): SubjectsScreenStrings = when (language) {
    "pt-BR" -> PtSubjectsScreenStrings
    "es-ES" -> EsSubjectsScreenStrings
    else -> EnSubjectsScreenStrings
}
