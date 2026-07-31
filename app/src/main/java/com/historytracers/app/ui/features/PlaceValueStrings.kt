// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PlaceValueStrings(
    val levelUnits: String,
    val levelTens: String,
    val levelHundreds: String,
    val levelThousands: String,
    val levelTenThousands: String,
    val levelHundredThousands: String,
    val levelMillions: String,
    val levelTenMillions: String,
    val placeUnits: String,
    val placeTens: String,
    val placeHundreds: String,
    val placeThousands: String,
    val placeTenThousands: String,
    val placeHundredThousands: String,
    val placeMillions: String,
    val placeTenMillions: String,
    val placeNext: String,
)

val EnPlaceValueStrings = PlaceValueStrings(
    levelUnits = "Units",
    levelTens = "Tens",
    levelHundreds = "Hundreds",
    levelThousands = "Thousands",
    levelTenThousands = "Ten Thousands",
    levelHundredThousands = "Hundred Thousands",
    levelMillions = "Millions",
    levelTenMillions = "Ten Millions",
    placeUnits = "units",
    placeTens = "tens",
    placeHundreds = "hundreds",
    placeThousands = "thousands",
    placeTenThousands = "ten thousands",
    placeHundredThousands = "hundred thousands",
    placeMillions = "millions",
    placeTenMillions = "ten millions",
    placeNext = "next",
)

val PtPlaceValueStrings = PlaceValueStrings(
    levelUnits = "Unidades",
    levelTens = "Dezenas",
    levelHundreds = "Centenas",
    levelThousands = "Milhares",
    levelTenThousands = "Dezenas de Milhar",
    levelHundredThousands = "Centenas de Milhar",
    levelMillions = "Milh\u00f5es",
    levelTenMillions = "Dezenas de Milh\u00f5es",
    placeUnits = "unidades",
    placeTens = "dezenas",
    placeHundreds = "centenas",
    placeThousands = "milhares",
    placeTenThousands = "dezenas de milhar",
    placeHundredThousands = "centenas de milhar",
    placeMillions = "milh\u00f5es",
    placeTenMillions = "dezenas de milh\u00f5es",
    placeNext = "pr\u00f3xima",
)

val EsPlaceValueStrings = PlaceValueStrings(
    levelUnits = "Unidades",
    levelTens = "Decenas",
    levelHundreds = "Centenas",
    levelThousands = "Miles",
    levelTenThousands = "Decenas de Millar",
    levelHundredThousands = "Centenas de Millar",
    levelMillions = "Millones",
    levelTenMillions = "Decenas de Millones",
    placeUnits = "unidades",
    placeTens = "decenas",
    placeHundreds = "centenas",
    placeThousands = "miles",
    placeTenThousands = "decenas de millar",
    placeHundredThousands = "centenas de millar",
    placeMillions = "millones",
    placeTenMillions = "decenas de millones",
    placeNext = "siguiente",
)

val LocalPlaceValueStrings = staticCompositionLocalOf { EnPlaceValueStrings }

fun placeValueStringsForLanguage(language: String): PlaceValueStrings = when (language) {
    "pt-BR" -> PtPlaceValueStrings
    "es-ES" -> EsPlaceValueStrings
    else -> EnPlaceValueStrings
}
