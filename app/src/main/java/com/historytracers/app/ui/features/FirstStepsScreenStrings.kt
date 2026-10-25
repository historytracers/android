// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class FirstStepsScreenStrings(
    val building: String,
    val sequenceGameOrders: String,
    val sequenceGameFamilies: String,
    val whereAreThey: String,
    val limitsMinMax: String,
    val ordersUnits: String,
    val ordersTens: String,
    val ordersMore: String,
    val firstHands: String,
    val firstVoice: String,
    val countingWithBones: String,
    val weHaveZero: String,
)

val EnFirstStepsScreenStrings = FirstStepsScreenStrings(
    building = "Building Game",
    sequenceGameOrders = "Sequence Game (Orders)",
    sequenceGameFamilies = "Sequence Game (Families)",
    whereAreThey = "Where Are The Numbers?",
    limitsMinMax = "Limits (min and max)",
    ordersUnits = "Units",
    ordersTens = "Tens",
    ordersMore = "..",
    firstHands = "The First Ones (Hands)",
    firstVoice = "The First Ones (Voice)",
    countingWithBones = "Counting with Bones",
    weHaveZero = "We Have Zero",
)

val PtFirstStepsScreenStrings = FirstStepsScreenStrings(
    building = "Jogo de Constru\u00e7\u00e3o",
    sequenceGameOrders = "Jogo da sequ\u00eancia (Ordens)",
    sequenceGameFamilies = "Jogo da sequ\u00eancia (Fam\u00edlias)",
    whereAreThey = "Onde est\u00e3o os n\u00fameros?",
    limitsMinMax = "Limites (m\u00edn e m\u00e1x)",
    ordersUnits = "Unidades",
    ordersTens = "Dezenas",
    ordersMore = "..",
    firstHands = "Os primeiros (m\u00e3os)",
    firstVoice = "Os primeiros (voz)",
    countingWithBones = "Contando com ossos",
    weHaveZero = "Temos zero",
)

val EsFirstStepsScreenStrings = FirstStepsScreenStrings(
    building = "Juego de Construcci\u00f3n",
    sequenceGameOrders = "Juego de secuencia (\u00d3rdenes)",
    sequenceGameFamilies = "Juego de secuencia (Familias)",
    whereAreThey = "\u00bfD\u00f3nde est\u00e1n los n\u00fameros?",
    limitsMinMax = "L\u00edmites (m\u00edn y m\u00e1x)",
    ordersUnits = "Unidades",
    ordersTens = "Decenas",
    ordersMore = "..",
    firstHands = "Los primeros (manos)",
    firstVoice = "Los primeros (voz)",
    countingWithBones = "Contando con huesos",
    weHaveZero = "Tenemos cero",
)

val LocalFirstStepsScreenStrings = staticCompositionLocalOf { EnFirstStepsScreenStrings }

fun firstStepsScreenStringsForLanguage(language: String): FirstStepsScreenStrings = when (language) {
    "pt-BR" -> PtFirstStepsScreenStrings
    "es-ES" -> EsFirstStepsScreenStrings
    else -> EnFirstStepsScreenStrings
}
