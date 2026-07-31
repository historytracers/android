// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class WorkoutScreenStrings(
    val thinking: String,
    val voice: String,
)

val EnWorkoutScreenStrings = WorkoutScreenStrings(
    thinking = "Thinking",
    voice = "Voice",
)

val PtWorkoutScreenStrings = WorkoutScreenStrings(
    thinking = "Pensando",
    voice = "Voz",
)

val EsWorkoutScreenStrings = WorkoutScreenStrings(
    thinking = "Pensando",
    voice = "Voz",
)

val LocalWorkoutScreenStrings = staticCompositionLocalOf { EnWorkoutScreenStrings }

fun workoutScreenStringsForLanguage(language: String): WorkoutScreenStrings = when (language) {
    "pt-BR" -> PtWorkoutScreenStrings
    "es-ES" -> EsWorkoutScreenStrings
    else -> EnWorkoutScreenStrings
}
