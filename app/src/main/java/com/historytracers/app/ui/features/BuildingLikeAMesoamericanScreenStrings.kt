// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class BuildingLikeAMesoamericanScreenStrings(
    val title: String,
    val instruction: String,
    val completionMessage: String,
    val congratsMessage: String,
)

val EnBuildingLikeAMesoamericanScreenStrings = BuildingLikeAMesoamericanScreenStrings(
    title = "Building Like an Mesoamerican",
    instruction = "Tap the question marks to reveal the hidden Maya numbers. Reveal them all to complete the level.",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CORRECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou revealed all the Maya numbers!\nGreat job!",
    congratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CONGRATULATIONS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou completed all the levels!\nExcellent work!",
)

val PtBuildingLikeAMesoamericanScreenStrings = BuildingLikeAMesoamericanScreenStrings(
    title = "Construindo Como Um Mesoamericano",
    instruction = "Toque nos pontos de interroga\u00e7\u00e3o para revelar os n\u00fameros maias ocultos. Revele todos para completar o n\u00edvel.",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CORRETO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea revelou todos os n\u00fameros maias!\n\u00d3timo trabalho!",
    congratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PARAB\u00c9NS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea completou todos os n\u00edveis!\nExcelente trabalho!",
)

val EsBuildingLikeAMesoamericanScreenStrings = BuildingLikeAMesoamericanScreenStrings(
    title = "Construyendo Como Un Mesoamericano",
    instruction = "Toca los signos de interrogaci\u00f3n para revelar los n\u00fameros mayas ocultos. Rev\u00e9lalos todos para completar el nivel.",
    completionMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1CORRECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1Revelaste todos los n\u00fameros mayas!\n\u00a1Gran trabajo!",
    congratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1FELICITACIONES! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1Completaste todos los niveles!\n\u00a1Excelente trabajo!",
)

val LocalBuildingLikeAMesoamericanScreenStrings = staticCompositionLocalOf { EnBuildingLikeAMesoamericanScreenStrings }

fun buildingLikeAMesoamericanScreenStringsForLanguage(language: String): BuildingLikeAMesoamericanScreenStrings = when (language) {
    "pt-BR" -> PtBuildingLikeAMesoamericanScreenStrings
    "es-ES" -> EsBuildingLikeAMesoamericanScreenStrings
    else -> EnBuildingLikeAMesoamericanScreenStrings
}
