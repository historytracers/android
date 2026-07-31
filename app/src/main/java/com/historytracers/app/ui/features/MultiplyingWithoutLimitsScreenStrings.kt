// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplyingWithoutLimitsScreenStrings(
    val resetToZero: String,
)

val EnMultiplyingWithoutLimitsScreenStrings = MultiplyingWithoutLimitsScreenStrings(
    resetToZero = "Reset to 0",
)

val PtMultiplyingWithoutLimitsScreenStrings = MultiplyingWithoutLimitsScreenStrings(
    resetToZero = "Reiniciar para 0",
)

val EsMultiplyingWithoutLimitsScreenStrings = MultiplyingWithoutLimitsScreenStrings(
    resetToZero = "Reiniciar a 0",
)

val LocalMultiplyingWithoutLimitsScreenStrings = staticCompositionLocalOf { EnMultiplyingWithoutLimitsScreenStrings }

fun multiplyingWithoutLimitsScreenStringsForLanguage(language: String): MultiplyingWithoutLimitsScreenStrings = when (language) {
    "pt-BR" -> PtMultiplyingWithoutLimitsScreenStrings
    "es-ES" -> EsMultiplyingWithoutLimitsScreenStrings
    else -> EnMultiplyingWithoutLimitsScreenStrings
}
