// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PracticingMultiplicationByOneDigitNumbersStrings(
    val rulesTitle: String,
    val gameTitle: String,
    val levelFormat: String,
    val levelOneDigit: String,
    val levelTwoDigits: String,
    val levelThreeDigits: String,
    val units: String,
    val tens: String,
    val hundreds: String,
    val question: String,
    val incomingNote: String,
    val msgCarry: String,
    val msgNoCarry: String,
    val msgCarryIn: String,
    val msgNoCarryIn: String,
    val groupsNote: String,
    val tenNote: String,
    val nextColumn: String,
    val newProblem: String,
    val repeatGroup: String,
    val removeGroup: String,
    val squaresLabel: String,
    val medal: String,
)

val EnPracticingMultiplicationByOneDigitNumbersStrings = PracticingMultiplicationByOneDigitNumbersStrings(
    rulesTitle = "Practicing Multiplication by One-Digit Numbers",
    gameTitle = "The Game",
    levelFormat = "Level %1\$d: %2\$s",
    levelOneDigit = "one-digit number \u00D7 one digit",
    levelTwoDigits = "two-digit number \u00D7 one digit",
    levelThreeDigits = "three-digit number \u00D7 one digit",
    units = "Units",
    tens = "Tens",
    hundreds = "Hundreds",
    question = "The %1\$s column: %2\$d \u00D7 %3\$d. Use the buttons to repeat the group of %2\$d, one group per click, until there are %3\$d groups.",
    incomingNote = "The %1\$d carried from the previous column will be added after repeating the groups of this column.",
    msgCarry = "%1\$d \u00D7 %2\$d = %3\$d. It reaches ten or more, so the %8\$s column keeps %6\$d and carries %7\$d to the next order.",
    msgNoCarry = "%1\$d \u00D7 %2\$d = %3\$d. It stays below ten, so the %8\$s column keeps %6\$d.",
    msgCarryIn = "%1\$d \u00D7 %2\$d = %3\$d. Adding the %4\$d carried from the previous order gives %5\$d; it reaches ten or more, so the %8\$s column keeps %6\$d and carries %7\$d to the next order.",
    msgNoCarryIn = "%1\$d \u00D7 %2\$d = %3\$d. Adding the %4\$d carried from the previous order gives %5\$d; it stays below ten, so the %8\$s column keeps %6\$d.",
    groupsNote = "Groups repeated: %1\$d of %2\$d.",
    tenNote = "Complete groups of ten: %1\$d; squares that stay in the %2\$s column: %3\$d.",
    nextColumn = "Next Column",
    newProblem = "New Problem",
    repeatGroup = "Repeat one group of squares",
    removeGroup = "Remove one group of squares",
    squaresLabel = "Multiplication in This Column",
    medal = "\uD83C\uDFC5",
)

val PtPracticingMultiplicationByOneDigitNumbersStrings = PracticingMultiplicationByOneDigitNumbersStrings(
    rulesTitle = "Praticando a Multiplica\u00E7\u00E3o por N\u00FAmeros de Um D\u00EDgito",
    gameTitle = "O Jogo",
    levelFormat = "N\u00EDvel %1\$d: %2\$s",
    levelOneDigit = "n\u00FAmero de um d\u00EDgito \u00D7 um d\u00EDgito",
    levelTwoDigits = "n\u00FAmero de dois d\u00EDgitos \u00D7 um d\u00EDgito",
    levelThreeDigits = "n\u00FAmero de tr\u00EAs d\u00EDgitos \u00D7 um d\u00EDgito",
    units = "Unidades",
    tens = "Dezenas",
    hundreds = "Centenas",
    question = "A coluna das %1\$s: %2\$d \u00D7 %3\$d. Use os bot\u00F5es para repetir o grupo de %2\$d, um grupo por clique, at\u00E9 haver %3\$d grupos.",
    incomingNote = "O %1\$d transportado da coluna anterior ser\u00E1 somado depois de repetir os grupos desta coluna.",
    msgCarry = "%1\$d \u00D7 %2\$d = %3\$d. Chega a dez ou mais, ent\u00E3o a coluna das %8\$s mant\u00E9m %6\$d e transporta %7\$d para a pr\u00F3xima ordem.",
    msgNoCarry = "%1\$d \u00D7 %2\$d = %3\$d. Fica abaixo de dez, ent\u00E3o a coluna das %8\$s mant\u00E9m %6\$d.",
    msgCarryIn = "%1\$d \u00D7 %2\$d = %3\$d. Somando o %4\$d transportado da ordem anterior chegamos a %5\$d; chega a dez ou mais, ent\u00E3o a coluna das %8\$s mant\u00E9m %6\$d e transporta %7\$d para a pr\u00F3xima ordem.",
    msgNoCarryIn = "%1\$d \u00D7 %2\$d = %3\$d. Somando o %4\$d transportado da ordem anterior chegamos a %5\$d; fica abaixo de dez, ent\u00E3o a coluna das %8\$s mant\u00E9m %6\$d.",
    groupsNote = "Grupos repetidos: %1\$d de %2\$d.",
    tenNote = "Grupos completos de dez: %1\$d; quadrados que ficam na coluna das %2\$s: %3\$d.",
    nextColumn = "Pr\u00F3xima Coluna",
    newProblem = "Novo Problema",
    repeatGroup = "Repetir um grupo de quadrados",
    removeGroup = "Tirar um grupo de quadrados",
    squaresLabel = "Multiplica\u00E7\u00E3o nesta Coluna",
    medal = "\uD83C\uDFC5",
)

val EsPracticingMultiplicationByOneDigitNumbersStrings = PracticingMultiplicationByOneDigitNumbersStrings(
    rulesTitle = "Practicando la Multiplicaci\u00F3n por N\u00FAmeros de Una Cifra",
    gameTitle = "El Juego",
    levelFormat = "Nivel %1\$d: %2\$s",
    levelOneDigit = "n\u00FAmero de una cifra \u00D7 una cifra",
    levelTwoDigits = "n\u00FAmero de dos cifras \u00D7 una cifra",
    levelThreeDigits = "n\u00FAmero de tres cifras \u00D7 una cifra",
    units = "Unidades",
    tens = "Decenas",
    hundreds = "Centenas",
    question = "La columna de las %1\$s: %2\$d \u00D7 %3\$d. Usa los botones para repetir el grupo de %2\$d, un grupo por clic, hasta que haya %3\$d grupos.",
    incomingNote = "El %1\$d acarreado de la columna anterior se sumar\u00E1 despu\u00E9s de repetir los grupos de esta columna.",
    msgCarry = "%1\$d \u00D7 %2\$d = %3\$d. Llega a diez o m\u00E1s, as\u00ED que la columna de las %8\$s conserva %6\$d y acarrea %7\$d al siguiente orden.",
    msgNoCarry = "%1\$d \u00D7 %2\$d = %3\$d. No llega a diez, as\u00ED que la columna de las %8\$s conserva %6\$d.",
    msgCarryIn = "%1\$d \u00D7 %2\$d = %3\$d. Sumando el %4\$d acarreado del orden anterior llegamos a %5\$d; llega a diez o m\u00E1s, as\u00ED que la columna de las %8\$s conserva %6\$d y acarrea %7\$d al siguiente orden.",
    msgNoCarryIn = "%1\$d \u00D7 %2\$d = %3\$d. Sumando el %4\$d acarreado del orden anterior llegamos a %5\$d; no llega a diez, as\u00ED que la columna de las %8\$s conserva %6\$d.",
    groupsNote = "Grupos repetidos: %1\$d de %2\$d.",
    tenNote = "Grupos completos de diez: %1\$d; cuadrados que se quedan en la columna de las %2\$s: %3\$d.",
    nextColumn = "Siguiente Columna",
    newProblem = "Nuevo Problema",
    repeatGroup = "Repetir un grupo de cuadrados",
    removeGroup = "Quitar un grupo de cuadrados",
    squaresLabel = "Multiplicaci\u00F3n en Esta Columna",
    medal = "\uD83C\uDFC5",
)

val LocalPracticingMultiplicationByOneDigitNumbersStrings =
    staticCompositionLocalOf { EnPracticingMultiplicationByOneDigitNumbersStrings }

fun practicingMultiplicationByOneDigitNumbersStringsForLanguage(
    language: String
): PracticingMultiplicationByOneDigitNumbersStrings = when (language) {
    "pt-BR" -> PtPracticingMultiplicationByOneDigitNumbersStrings
    "es-ES" -> EsPracticingMultiplicationByOneDigitNumbersStrings
    else -> EnPracticingMultiplicationByOneDigitNumbersStrings
}
