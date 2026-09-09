// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SettingsScreenStrings(
    val startLearning: String,
    val calGregorian: String,
    val calJulian: String,
    val calHebrew: String,
    val calIslamic: String,
    val calPersian: String,
    val calShaka: String,
    val calMesoamerican: String,
    val calMesoamericanExt: String,
    val calHispanic: String,
    val calIndianCivil: String,
    val calFrenchRepublican: String,
    val calChinese: String,
    val calAymara: String,
    val calMapuche: String,
    val calInca: String,
    val calJavanese: String,
    val calJapanese: String,
)

val EnSettingsScreenStrings = SettingsScreenStrings(
    startLearning = "Start Learning",
    calGregorian = "Gregorian",
    calJulian = "Julian",
    calHebrew = "Hebrew",
    calIslamic = "Islamic",
    calPersian = "Persian",
    calShaka = "Shaka",
    calMesoamerican = "Mesoamerican",
    calMesoamericanExt = "Mesoamerican (Ext.)",
    calHispanic = "Hispanic",
    calIndianCivil = "Indian Civil",
    calFrenchRepublican = "French Republican",
    calChinese = "Chinese",
    calAymara = "Aymara",
    calMapuche = "Mapuche",
    calInca = "Inca",
    calJavanese = "Javanese",
    calJapanese = "Japanese",
)

val PtSettingsScreenStrings = SettingsScreenStrings(
    startLearning = "Come\u00e7ar a Aprender",
    calGregorian = "Gregoriano",
    calJulian = "Juliano",
    calHebrew = "Hebreu",
    calIslamic = "Isl\u00e2mico",
    calPersian = "Persa",
    calShaka = "Shaka",
    calMesoamerican = "Mesoamericano",
    calMesoamericanExt = "Mesoamericano (Est.)",
    calHispanic = "Hisp\u00e2nico",
    calIndianCivil = "Civil Indiano",
    calFrenchRepublican = "Republicano Franc\u00eas",
    calChinese = "Chin\u00eas",
    calAymara = "Aimara",
    calMapuche = "Mapuche",
    calInca = "Inca",
    calJavanese = "Javan\u00eas",
    calJapanese = "Japon\u00eas",
)

val EsSettingsScreenStrings = SettingsScreenStrings(
    startLearning = "Empezar a Aprender",
    calGregorian = "Gregoriano",
    calJulian = "Juliano",
    calHebrew = "Hebreo",
    calIslamic = "Isl\u00e1mico",
    calPersian = "Persa",
    calShaka = "Shaka",
    calMesoamerican = "Mesoamericano",
    calMesoamericanExt = "Mesoamericano (Ext.)",
    calHispanic = "Hisp\u00e1nico",
    calIndianCivil = "Civil Indio",
    calFrenchRepublican = "Republicano Franc\u00e9s",
    calChinese = "Chino",
    calAymara = "Aimara",
    calMapuche = "Mapuche",
    calInca = "Inca",
    calJavanese = "Javan\u00e9s",
    calJapanese = "Japon\u00e9s",
)

val LocalSettingsScreenStrings = staticCompositionLocalOf { EnSettingsScreenStrings }

fun settingsScreenStringsForLanguage(language: String): SettingsScreenStrings = when (language) {
    "pt-BR" -> PtSettingsScreenStrings
    "es-ES" -> EsSettingsScreenStrings
    else -> EnSettingsScreenStrings
}
