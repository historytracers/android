// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PracticingMultiplicationYupanaScreenStrings(
    val ypMultiplyInstruction: String,
    val ypMultiplyPerfectMessage: String,
    val ypMultiplyCongratsMessage: String,
    val ypMultiplyStepPlace: String,
    val ypMultiplyStepAdd: String,
)

val EnPracticingMultiplicationYupanaScreenStrings = PracticingMultiplicationYupanaScreenStrings(
    ypMultiplyInstruction = "Tap the Yupana columns to place markers for each step of the multiplication. Complete all steps to finish.",
    ypMultiplyPerfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d \u00D7 %d = %d\nGreat job using the Yupana!",
    ypMultiplyCongratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CONGRATULATIONS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou calculated %d \u00D7 %d = %d\nClick 'New exercise' to practice more!",
    ypMultiplyStepPlace = "Step %d/%d: Place markers for %d on the Yupana",
    ypMultiplyStepAdd = "Step %d/%d: Add %d again \u2192 running total = %d",
)

val PtPracticingMultiplicationYupanaScreenStrings = PracticingMultiplicationYupanaScreenStrings(
    ypMultiplyInstruction = "Toque nas colunas da Yupana para colocar marcadores em cada passo da multiplica\u00e7\u00e3o. Complete todos os passos para finalizar.",
    ypMultiplyPerfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFEITO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d \u00D7 %d = %d\n\u00d3timo trabalho usando a Yupana!",
    ypMultiplyCongratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PARAB\u00c9NS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea calculou %d \u00D7 %d = %d\nClique em 'Novo exerc\u00edcio' para praticar mais!",
    ypMultiplyStepPlace = "Passo %d/%d: Coloque marcadores para %d na Yupana",
    ypMultiplyStepAdd = "Passo %d/%d: Adicione %d novamente \u2192 total parcial = %d",
)

val EsPracticingMultiplicationYupanaScreenStrings = PracticingMultiplicationYupanaScreenStrings(
    ypMultiplyInstruction = "Toca las columnas de la Yupana para colocar marcadores en cada paso de la multiplicaci\u00f3n. Completa todos los pasos para finalizar.",
    ypMultiplyPerfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1PERFECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d \u00D7 %d = %d\n\u00a1Gran trabajo usando la Yupana!",
    ypMultiplyCongratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1FELICITACIONES! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nHas calculado %d \u00D7 %d = %d\n\u00a1Haz clic en 'Nuevo ejercicio' para practicar m\u00e1s!",
    ypMultiplyStepPlace = "Paso %d/%d: Coloca marcadores para %d en la Yupana",
    ypMultiplyStepAdd = "Paso %d/%d: Agrega %d nuevamente \u2192 total parcial = %d",
)

val LocalPracticingMultiplicationYupanaScreenStrings = staticCompositionLocalOf { EnPracticingMultiplicationYupanaScreenStrings }

fun practicingMultiplicationYupanaScreenStringsForLanguage(language: String): PracticingMultiplicationYupanaScreenStrings = when (language) {
    "pt-BR" -> PtPracticingMultiplicationYupanaScreenStrings
    "es-ES" -> EsPracticingMultiplicationYupanaScreenStrings
    else -> EnPracticingMultiplicationYupanaScreenStrings
}
