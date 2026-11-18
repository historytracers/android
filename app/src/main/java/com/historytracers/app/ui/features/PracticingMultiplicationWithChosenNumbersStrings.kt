// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PracticingMultiplicationWithChosenNumbersStrings(
    val rulesTitle: String,
    val gameTitle: String,
    val numberLabel: String,
    val numberField: String,
    val multiplierField: String,
    val restart: String,
)

val EnPracticingMultiplicationWithChosenNumbersStrings = PracticingMultiplicationWithChosenNumbersStrings(
    rulesTitle = "Practicing Multiplication with Chosen Numbers",
    gameTitle = "The Game",
    numberLabel = "Number:",
    numberField = "Number to be multiplied",
    multiplierField = "One-digit number that multiplies",
    restart = "Restart",
)

val PtPracticingMultiplicationWithChosenNumbersStrings = PracticingMultiplicationWithChosenNumbersStrings(
    rulesTitle = "Praticando Multiplica\u00E7\u00E3o com N\u00FAmeros Escolhidos",
    gameTitle = "O Jogo",
    numberLabel = "N\u00FAmero:",
    numberField = "N\u00FAmero a ser multiplicado",
    multiplierField = "N\u00FAmero de um d\u00EDgito que multiplica",
    restart = "Reiniciar",
)

val EsPracticingMultiplicationWithChosenNumbersStrings = PracticingMultiplicationWithChosenNumbersStrings(
    rulesTitle = "Practicando la Multiplicaci\u00F3n con N\u00FAmeros Elegidos",
    gameTitle = "El Juego",
    numberLabel = "N\u00FAmero:",
    numberField = "N\u00FAmero que se multiplica",
    multiplierField = "N\u00FAmero de una cifra que multiplica",
    restart = "Reiniciar",
)

val LocalPracticingMultiplicationWithChosenNumbersStrings =
    staticCompositionLocalOf { EnPracticingMultiplicationWithChosenNumbersStrings }

fun practicingMultiplicationWithChosenNumbersStringsForLanguage(
    language: String
): PracticingMultiplicationWithChosenNumbersStrings = when (language) {
    "pt-BR" -> PtPracticingMultiplicationWithChosenNumbersStrings
    "es-ES" -> EsPracticingMultiplicationWithChosenNumbersStrings
    else -> EnPracticingMultiplicationWithChosenNumbersStrings
}
