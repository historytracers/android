// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class AnotherWayToCountScreenStrings(
    val quipus: String,
    val practicingWithQuipus: String,
    val theMesoamericanSymbols: String,
    val overcomingLimits: String,
    val buildingLikeAMesoamerican: String,
    val mesoamericanOrder: String,
)

val EnAnotherWayToCountScreenStrings = AnotherWayToCountScreenStrings(
    quipus = "Quipus",
    practicingWithQuipus = "Practicing with Quipus",
    theMesoamericanSymbols = "The Mesoamerican Symbols",
    overcomingLimits = "Overcoming Limits",
    buildingLikeAMesoamerican = "Building Like an Mesoamerican",
    mesoamericanOrder = "Mesoamerican Orders",
)

val PtAnotherWayToCountScreenStrings = AnotherWayToCountScreenStrings(
    quipus = "Quipus",
    practicingWithQuipus = "Praticando com Quipus",
    theMesoamericanSymbols = "Os S\u00edmbolos Mesoamericanos",
    overcomingLimits = "Superando os Limites",
    buildingLikeAMesoamerican = "Construindo Como Um Mesoamericano",
    mesoamericanOrder = "Ordens Mesoamericanas",
)

val EsAnotherWayToCountScreenStrings = AnotherWayToCountScreenStrings(
    quipus = "Quipus",
    practicingWithQuipus = "Practicando con Quipus",
    theMesoamericanSymbols = "Los S\u00edmbolos Mesoamericanos",
    overcomingLimits = "Superando los L\u00edmites",
    buildingLikeAMesoamerican = "Construyendo Como Un Mesoamericano",
    mesoamericanOrder = "Órdenes Mesoamericanos",
)

val LocalAnotherWayToCountScreenStrings = staticCompositionLocalOf { EnAnotherWayToCountScreenStrings }

fun anotherWayToCountScreenStringsForLanguage(language: String): AnotherWayToCountScreenStrings = when (language) {
    "pt-BR" -> PtAnotherWayToCountScreenStrings
    "es-ES" -> EsAnotherWayToCountScreenStrings
    else -> EnAnotherWayToCountScreenStrings
}
