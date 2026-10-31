// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PracticingSubtractionScreenStrings(
    val title: String,
    val fallbackInstruction: String,
    val orderUnits: String,
    val orderTens: String,
    val orderHundreds: String,
    val questionTemplate: String,
    val incomingNote: String,
    val borrowNote: String,
    val chainedNote: String,
    val msgBorrow: String,
    val msgNoBorrow: String,
    val nextColumn: String,
    val newProblem: String,
    val squaresLabel: String,
    val squaresUsedNote: String,
    val borrowSquaresNote: String,
    val completed: String,
    val removeSquare: String,
    val returnSquare: String,
)

val EnPracticingSubtractionScreenStrings = PracticingSubtractionScreenStrings(
    title = "Practicing Subtraction",
    fallbackInstruction = "Subtract two three-digit numbers column by column, from the units. Use the down and up buttons to remove and restore squares; if the top digit is smaller, the column borrows ten.",
    orderUnits = "Units",
    orderTens = "Tens",
    orderHundreds = "Hundreds",
    questionTemplate = "The %1\$s column has %2\$d squares available. Now use the buttons to remove the bottom digit of the second number.",
    incomingNote = "The 1 borrowed by the previous column is removed first: %1\$d - 1 = %2\$d.",
    borrowNote = "%1\$d is smaller than %2\$d, so 1 is borrowed from the next order: %1\$d + 10 = %3\$d.",
    chainedNote = "The previous column borrowed 1 from this column and it is 0, so it borrows from the next order: 0 - 1 + 10 = 9.",
    msgBorrow = "%1\$d - %2\$d = %3\$d. The 1 borrowed from the next order is used, and the %4\$s column keeps %3\$d.",
    msgNoBorrow = "%1\$d - %2\$d = %3\$d. The %4\$s column keeps %3\$d.",
    nextColumn = "Next column",
    newProblem = "New problem",
    squaresLabel = "Subtraction in this column",
    squaresUsedNote = "Squares removed: %1\$d. Squares remaining: %2\$d.",
    borrowSquaresNote = "Squares removed from the borrowed ten: %1\$d. Right now %2\$d squares stay in the ten; when all %3\$d have been removed, the complement of the bottom digit to ten (%4\$d) will stay. There are also %5\$d squares already in the column.",
    completed = "\uD83E\uDD47 Subtraction complete!",
    removeSquare = "Remove one square",
    returnSquare = "Put one square back",
)

val PtPracticingSubtractionScreenStrings = PracticingSubtractionScreenStrings(
    title = "Praticando Subtra\u00e7\u00e3o",
    fallbackInstruction = "Subtraia dois n\u00fameros de tr\u00eas algarismos coluna por coluna, a partir das unidades. Use os bot\u00f5es para baixo e para cima para remover e devolver quadrados; se o de cima for menor, a coluna pede dez.",
    orderUnits = "Unidades",
    orderTens = "Dezenas",
    orderHundreds = "Centenas",
    questionTemplate = "A coluna das %1\$s tem %2\$d quadrados dispon\u00edveis. Agora use os bot\u00f5es para remover o algarismo de baixo do segundo n\u00famero.",
    incomingNote = "O 1 emprestado pela coluna anterior \u00e9 removido primeiro: %1\$d - 1 = %2\$d.",
    borrowNote = "%1\$d \u00e9 menor que %2\$d, ent\u00e3o 1 \u00e9 emprestado da pr\u00f3xima ordem: %1\$d + 10 = %3\$d.",
    chainedNote = "A coluna anterior emprestou 1 desta coluna e ela \u00e9 0, ent\u00e3o 1 \u00e9 emprestado da pr\u00f3xima ordem: 0 - 1 + 10 = 9.",
    msgBorrow = "%1\$d - %2\$d = %3\$d. O 1 emprestado da pr\u00f3xima ordem \u00e9 usado, e a coluna das %4\$s mant\u00e9m %3\$d.",
    msgNoBorrow = "%1\$d - %2\$d = %3\$d. A coluna das %4\$s mant\u00e9m %3\$d.",
    nextColumn = "Pr\u00f3xima coluna",
    newProblem = "Novo problema",
    squaresLabel = "Subtra\u00e7\u00e3o nesta coluna",
    squaresUsedNote = "Quadrados removidos: %1\$d. Quadrados restantes: %2\$d.",
    borrowSquaresNote = "Quadrados removidos do dez emprestado: %1\$d. No momento, %2\$d quadrados ficam no dez; quando todos os %3\$d forem removidos, o complemento do algarismo de baixo para dez (%4\$d) ficar\u00e1. H\u00e1 tamb\u00e9m %5\$d quadrados j\u00e1 na coluna.",
    completed = "\uD83E\uDD47 Subtra\u00e7\u00e3o conclu\u00edda!",
    removeSquare = "Remover um quadrado",
    returnSquare = "Devolver um quadrado",
)

val EsPracticingSubtractionScreenStrings = PracticingSubtractionScreenStrings(
    title = "Practicando Resta",
    fallbackInstruction = "Resta dos n\u00fameros de tres cifras columna por columna, desde las unidades. Usa los botones hacia abajo y hacia arriba para quitar y devolver cuadrados; si la cifra superior es menor, la columna pide diez.",
    orderUnits = "Unidades",
    orderTens = "Decenas",
    orderHundreds = "Centenas",
    questionTemplate = "La columna de las %1\$s tiene %2\$d cuadrados disponibles. Ahora usa los botones para quitar la cifra inferior del segundo n\u00famero.",
    incomingNote = "El 1 que pidi\u00f3 prestado la columna anterior se quita primero: %1\$d - 1 = %2\$d.",
    borrowNote = "%1\$d es menor que %2\$d, as\u00ed que se pide prestado 1 al siguiente orden: %1\$d + 10 = %3\$d.",
    chainedNote = "La columna anterior pidi\u00f3 prestado 1 a esta columna y es 0, as\u00ed que pide prestado al siguiente orden: 0 - 1 + 10 = 9.",
    msgBorrow = "%1\$d - %2\$d = %3\$d. Se usa el 1 prestado del siguiente orden, y la columna de las %4\$s conserva %3\$d.",
    msgNoBorrow = "%1\$d - %2\$d = %3\$d. La columna de las %4\$s conserva %3\$d.",
    nextColumn = "Siguiente columna",
    newProblem = "Nuevo problema",
    squaresLabel = "Resta en esta columna",
    squaresUsedNote = "Cuadrados quitados: %1\$d. Cuadrados restantes: %2\$d.",
    borrowSquaresNote = "Cuadrados quitados del diez prestado: %1\$d. Ahora quedan %2\$d cuadrados en el diez; cuando se quiten los %3\$d, quedar\u00e1 el complemento de la cifra inferior a diez (%4\$d). Tambi\u00e9n hay %5\$d cuadrados que ya estaban en la columna.",
    completed = "\uD83E\uDD47 \u00a1Resta completada!",
    removeSquare = "Quitar un cuadrado",
    returnSquare = "Devolver un cuadrado",
)

val LocalPracticingSubtractionScreenStrings = staticCompositionLocalOf { EnPracticingSubtractionScreenStrings }

fun practicingSubtractionScreenStringsForLanguage(language: String): PracticingSubtractionScreenStrings = when (language) {
    "pt-BR" -> PtPracticingSubtractionScreenStrings
    "es-ES" -> EsPracticingSubtractionScreenStrings
    else -> EnPracticingSubtractionScreenStrings
}
