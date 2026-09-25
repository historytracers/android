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
    val textOrNumber: String,
    val theMissingNumbers: String,
    val iPreferThis: String,
    val buildingLikeEtruscanRomans: String,
    val etruscanRomanTens: String,
    val hundredsAndThousands: String,
    val iRepresentYou: String,
)

val EnAnotherWayToCountScreenStrings = AnotherWayToCountScreenStrings(
    quipus = "Quipus",
    practicingWithQuipus = "Practicing with Quipus",
    theMesoamericanSymbols = "The Mesoamerican Symbols",
    overcomingLimits = "Overcoming Limits",
    buildingLikeAMesoamerican = "Building Like an Mesoamerican",
    mesoamericanOrder = "Mesoamerican Orders",
    textOrNumber = "Text or Number",
    theMissingNumbers = "The Missing Numbers",
    iPreferThis = "I Prefer This",
    buildingLikeEtruscanRomans = "Building Like Etruscan-Romans",
    etruscanRomanTens = "Etruscan-Roman Tens",
    hundredsAndThousands = "Hundreds and Thousands",
    iRepresentYou = "I represent you",
)

val PtAnotherWayToCountScreenStrings = AnotherWayToCountScreenStrings(
    quipus = "Quipus",
    practicingWithQuipus = "Praticando com Quipus",
    theMesoamericanSymbols = "Os S\u00edmbolos Mesoamericanos",
    overcomingLimits = "Superando os Limites",
    buildingLikeAMesoamerican = "Construindo Como Um Mesoamericano",
    mesoamericanOrder = "Ordens Mesoamericanas",
    textOrNumber = "Texto ou N\u00famero",
    theMissingNumbers = "Os N\u00fameros Que Faltam",
    iPreferThis = "Eu Prefiro Este",
    buildingLikeEtruscanRomans = "Construindo Como Etrusco-Romanos",
    etruscanRomanTens = "Dezenas Etrusco-Romanas",
    hundredsAndThousands = "Centenas e Milhares",
    iRepresentYou = "Eu represento voc\u00ea",
)

val EsAnotherWayToCountScreenStrings = AnotherWayToCountScreenStrings(
    quipus = "Quipus",
    practicingWithQuipus = "Practicando con Quipus",
    theMesoamericanSymbols = "Los S\u00edmbolos Mesoamericanos",
    overcomingLimits = "Superando los L\u00edmites",
    buildingLikeAMesoamerican = "Construyendo Como Un Mesoamericano",
    mesoamericanOrder = "Órdenes Mesoamericanos",
    textOrNumber = "Texto o N\u00famero",
    theMissingNumbers = "Los N\u00fameros Que Faltan",
    iPreferThis = "Yo Prefiero Este",
    buildingLikeEtruscanRomans = "Construyendo Como Etrusco-Romanos",
    etruscanRomanTens = "Decenas Etrusco-Romanas",
    hundredsAndThousands = "Centenas y Millares",
    iRepresentYou = "Yo te represento",
)

val LocalAnotherWayToCountScreenStrings = staticCompositionLocalOf { EnAnotherWayToCountScreenStrings }

fun anotherWayToCountScreenStringsForLanguage(language: String): AnotherWayToCountScreenStrings = when (language) {
    "pt-BR" -> PtAnotherWayToCountScreenStrings
    "es-ES" -> EsAnotherWayToCountScreenStrings
    else -> EnAnotherWayToCountScreenStrings
}
