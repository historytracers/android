// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MultiplicationTableHubScreenStrings(
    val whatIsIt: String,
    val theSameResult: String,
    val equalSides: String,
    val doubleValues: String,
    val tripleValues: String,
    val multiplyingWithGroups: String,
    val multiplyingWithGroupsLevel2: String,
    val firstSquareNumber: String,
    val matchingTables2And4: String,
    val myBodyAsMultiplicationTable: String,
    val multiplyingWithFlowers: String,
    val matchingTables: String,
    val matchingTables6And7: String,
    val weeksOfLife: String,
    val myFirstCube: String,
    val matchingTables2And4And8: String,
    val theLast: String,
    val matchingTables3And9: String,
)

val EnMultiplicationTableHubScreenStrings = MultiplicationTableHubScreenStrings(
    whatIsIt = "What is it?",
    theSameResult = "The Same Result",
    equalSides = "Equal Sides",
    doubleValues = "Double Values",
    tripleValues = "Triple Values",
    multiplyingWithGroups = "Multiplying with Groups (0 to 3)",
    multiplyingWithGroupsLevel2 = "Multiplying with Groups (4 to 7)",
    firstSquareNumber = "The First Square Number",
    matchingTables2And4 = "Matching the Tables of 2 and 4",
    myBodyAsMultiplicationTable = "My Body, as a Multiplication Table",
    multiplyingWithFlowers = "Multiplying with Flowers",
    matchingTables = "Matching the Tables of 2, 3, and 6",
    matchingTables6And7 = "Matching the Tables of 6 and 7",
    weeksOfLife = "Weeks of Life",
    myFirstCube = "My First Cube",
    matchingTables2And4And8 = "Matching the Tables of 2, 4 and 8",
    theLast = "The Last",
    matchingTables3And9 = "Matching the Tables of 3 and 9",
)

val PtMultiplicationTableHubScreenStrings = MultiplicationTableHubScreenStrings(
    whatIsIt = "O que \u00e9 isto?",
    theSameResult = "O Mesmo Resultado",
    equalSides = "Lados Iguais",
    doubleValues = "Valores Dobrados",
    tripleValues = "Valores Triplicados",
    multiplyingWithGroups = "Multiplicando com Grupos (0 a 3)",
    multiplyingWithGroupsLevel2 = "Multiplicando com Grupos (4 a 7)",
    firstSquareNumber = "O Primeiro N\u00famero Quadrado",
    matchingTables2And4 = "Combinando as Tabuadas de 2 e 4",
    myBodyAsMultiplicationTable = "Meu Corpo, como uma Tabuada",
    multiplyingWithFlowers = "Multiplicando com Flores",
    matchingTables = "Combinando as Tabuadas de 2, 3 e 6",
    matchingTables6And7 = "Combinando as Tabuadas de 6 e 7",
    weeksOfLife = "Semanas da Vida",
    myFirstCube = "Meu Primeiro Cubo",
    matchingTables2And4And8 = "Combinando as Tabuadas de 2, 4 e 8",
    theLast = "O Último",
    matchingTables3And9 = "Combinando as Tabuadas de 3 e 9",
)

val EsMultiplicationTableHubScreenStrings = MultiplicationTableHubScreenStrings(
    whatIsIt = "\u00bfQu\u00e9 es esto?",
    theSameResult = "El Mismo Resultado",
    equalSides = "Lados Iguales",
    doubleValues = "Valores Duplicados",
    tripleValues = "Valores Triplicados",
    multiplyingWithGroups = "Multiplicando con Grupos (0 a 3)",
    multiplyingWithGroupsLevel2 = "Multiplicando con Grupos (4 a 7)",
    firstSquareNumber = "El Primer N\u00famero Cuadrado",
    matchingTables2And4 = "Combinando las Tablas de 2 y 4",
    myBodyAsMultiplicationTable = "Mi Cuerpo, como una Tabla de Multiplicar",
    multiplyingWithFlowers = "Multiplicando con Flores",
    matchingTables = "Combinando las Tablas de 2, 3 y 6",
    matchingTables6And7 = "Combinando las Tablas de 6 y 7",
    weeksOfLife = "Semanas de la Vida",
    myFirstCube = "Mi Primer Cubo",
    matchingTables2And4And8 = "Combinando las Tablas de 2, 4 y 8",
    theLast = "El Último",
    matchingTables3And9 = "Combinando las Tablas de 3 y 9",
)

val LocalMultiplicationTableHubScreenStrings = staticCompositionLocalOf { EnMultiplicationTableHubScreenStrings }

fun multiplicationTableHubScreenStringsForLanguage(language: String): MultiplicationTableHubScreenStrings = when (language) {
    "pt-BR" -> PtMultiplicationTableHubScreenStrings
    "es-ES" -> EsMultiplicationTableHubScreenStrings
    else -> EnMultiplicationTableHubScreenStrings
}
