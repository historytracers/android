// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class BuildingGameScreenStrings(
    val title: String,
    val instruction: String,
    val completionMessage: String,
    val congratsMessage: String,
)

val EnBuildingGameScreenStrings = BuildingGameScreenStrings(
    title = "Building",
    instruction = "Tap the question marks to reveal the hidden numbers. Reveal them all to complete the level.",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CORRECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou revealed all the numbers!\nGreat job!",
    congratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CONGRATULATIONS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou completed all the levels!\nExcellent work!",
)

val PtBuildingGameScreenStrings = BuildingGameScreenStrings(
    title = "Constru\u00e7\u00e3o",
    instruction = "Toque nos pontos de interroga\u00e7\u00e3o para revelar os n\u00fameros ocultos. Revele todos para completar o n\u00edvel.",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CORRETO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea revelou todos os n\u00fameros!\n\u00d3timo trabalho!",
    congratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PARAB\u00c9NS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea completou todos os n\u00edveis!\nExcelente trabalho!",
)

val EsBuildingGameScreenStrings = BuildingGameScreenStrings(
    title = "Construcci\u00f3n",
    instruction = "Toca los signos de interrogaci\u00f3n para revelar los n\u00fameros ocultos. Rev\u00e9lalos todos para completar el nivel.",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1CORRECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1Revelaste todos los n\u00fameros!\n\u00a1Gran trabajo!",
    congratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1FELICITACIONES! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1Completaste todos los niveles!\n\u00a1Excelente trabajo!",
)

val LocalBuildingGameScreenStrings = staticCompositionLocalOf { EnBuildingGameScreenStrings }

fun buildingGameScreenStringsForLanguage(language: String): BuildingGameScreenStrings = when (language) {
    "pt-BR" -> PtBuildingGameScreenStrings
    "es-ES" -> EsBuildingGameScreenStrings
    else -> EnBuildingGameScreenStrings
}
