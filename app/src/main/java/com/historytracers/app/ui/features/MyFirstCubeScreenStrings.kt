// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MyFirstCubeScreenStrings(
    val title: String,
    val fullTitle: String,
)

val EnMyFirstCubeScreenStrings = MyFirstCubeScreenStrings(
    title = "My First Cube",
    fullTitle = "Multiplication Table (My First Cube)",
)

val PtMyFirstCubeScreenStrings = MyFirstCubeScreenStrings(
    title = "Meu Primeiro Cubo",
    fullTitle = "Tabuada (Meu Primeiro Cubo)",
)

val EsMyFirstCubeScreenStrings = MyFirstCubeScreenStrings(
    title = "Mi Primer Cubo",
    fullTitle = "Tabla de Multiplicar (Mi Primer Cubo)",
)

val LocalMyFirstCubeScreenStrings = staticCompositionLocalOf { EnMyFirstCubeScreenStrings }

fun myFirstCubeScreenStringsForLanguage(language: String): MyFirstCubeScreenStrings = when (language) {
    "pt-BR" -> PtMyFirstCubeScreenStrings
    "es-ES" -> EsMyFirstCubeScreenStrings
    else -> EnMyFirstCubeScreenStrings
}
