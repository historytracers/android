// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class StreakScreenStrings(
    val months: List<String>,
    val monthFormat: String,
    val leapMarker: String,
)

val EnStreakScreenStrings = StreakScreenStrings(
    months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ),
    monthFormat = "Month %d",
    leapMarker = " (leap)",
)

val PtStreakScreenStrings = StreakScreenStrings(
    months = listOf(
        "Janeiro", "Fevereiro", "Mar\u00e7o", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    ),
    monthFormat = "M\u00eas %d",
    leapMarker = " (bissexto)",
)

val EsStreakScreenStrings = StreakScreenStrings(
    months = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    ),
    monthFormat = "Mes %d",
    leapMarker = " (bisiesto)",
)

val LocalStreakScreenStrings = staticCompositionLocalOf { EnStreakScreenStrings }

fun streakScreenStringsForLanguage(language: String): StreakScreenStrings = when (language) {
    "pt-BR" -> PtStreakScreenStrings
    "es-ES" -> EsStreakScreenStrings
    else -> EnStreakScreenStrings
}
