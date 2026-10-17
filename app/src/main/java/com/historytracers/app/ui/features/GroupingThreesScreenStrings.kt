// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class GroupingThreesScreenStrings(
    val title: String,
    val levelLabel: String,
    val question: String,
    val totalLabel: String,
    val newExercise: String,
    val nextLevel: String,
    val playAgain: String,
    val tableTitle: String,
    val sourceAlwaysPresent: String,
    val sourceTable: String,
    val msgCorrect: String,
    val msgWrong: String,
    val msgFinal: String,
)

val EnGroupingThreesScreenStrings = GroupingThreesScreenStrings(
    title = "Grouping in Threes",
    levelLabel = "Level %1\$d of 3",
    question = "In how many groups of 3 can we organize these elements?",
    totalLabel = "Total of %1\$d elements",
    newExercise = "\uD83C\uDFB2 New exercise",
    nextLevel = "Next level",
    playAgain = "Play again",
    tableTitle = "Multiplication Table of 3",
    sourceAlwaysPresent = "Always present",
    sourceTable = "Understanding the Multiplication Table (2 and 3)",
    msgCorrect = "\uD83C\uDF89 Congratulations! The %1\$d elements, grouped in threes, form %2\$d groups. So %1\$d = 3 \u00d7 %2\$d.",
    msgWrong = "Not yet. Each group holds 3 elements. Count how many groups of three fit inside %1\$d and try again.",
    msgFinal = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Congratulations! You completed all the levels! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89",
)

val PtGroupingThreesScreenStrings = GroupingThreesScreenStrings(
    title = "Agrupando de Três em Três",
    levelLabel = "Nível %1\$d de 3",
    question = "Em quantos grupos de 3 podemos organizar estes elementos?",
    totalLabel = "Total de %1\$d elementos",
    newExercise = "\uD83C\uDFB2 Novo exercício",
    nextLevel = "Próximo nível",
    playAgain = "Jogar novamente",
    tableTitle = "Tabuada do 3",
    sourceAlwaysPresent = "Sempre presente",
    sourceTable = "Entendendo a tabuada (2 e 3)",
    msgCorrect = "\uD83C\uDF89 Parabéns! Os %1\$d elementos, agrupados de três em três, formam %2\$d grupos. Assim, %1\$d = 3 \u00d7 %2\$d.",
    msgWrong = "Ainda não. Cada grupo tem 3 elementos. Conte quantos grupos de três cabem em %1\$d e tente novamente.",
    msgFinal = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 Parabéns! Você completou todos os níveis! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89",
)

val EsGroupingThreesScreenStrings = GroupingThreesScreenStrings(
    title = "Agrupando de Tres en Tres",
    levelLabel = "Nivel %1\$d de 3",
    question = "¿En cuántos grupos de 3 podemos organizar estos elementos?",
    totalLabel = "Total de %1\$d elementos",
    newExercise = "\uD83C\uDFB2 Nuevo ejercicio",
    nextLevel = "Siguiente nivel",
    playAgain = "Jugar de nuevo",
    tableTitle = "Tabla de multiplicar del 3",
    sourceAlwaysPresent = "Siempre presente",
    sourceTable = "Comprendiendo la tabla de multiplicar (2 y 3)",
    msgCorrect = "\uD83C\uDF89 ¡Felicitaciones! Los %1\$d elementos, agrupados de tres en tres, forman %2\$d grupos. Así, %1\$d = 3 \u00d7 %2\$d.",
    msgWrong = "Todavía no. Cada grupo tiene 3 elementos. Cuenta cuántos grupos de tres caben en %1\$d e inténtalo de nuevo.",
    msgFinal = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 ¡Felicitaciones! ¡Completaste todos los niveles! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89",
)

val LocalGroupingThreesScreenStrings = staticCompositionLocalOf { EnGroupingThreesScreenStrings }

fun groupingThreesScreenStringsForLanguage(language: String): GroupingThreesScreenStrings = when (language) {
    "pt-BR" -> PtGroupingThreesScreenStrings
    "es-ES" -> EsGroupingThreesScreenStrings
    else -> EnGroupingThreesScreenStrings
}
