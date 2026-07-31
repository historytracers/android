// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SorobanWritingScreenStrings(
    val sorobanWritingInstruction: String,
)

val EnSorobanWritingScreenStrings = SorobanWritingScreenStrings(
    sorobanWritingInstruction = "This is the first in a series of texts that teach how to use the Soroban (\u7B97\u76E4). In this text, we will focus on the representation of numbers between 0 and 9; in future texts, we will learn how to use it more broadly. For now, we will only work with the first column from the right.\n\nThe marker on top (red) has value 5, while the marker below (blue) has value 1. Set the values as requested in the yellow box near the 'Value' box.",
)

val PtSorobanWritingScreenStrings = SorobanWritingScreenStrings(
    sorobanWritingInstruction = "Este \u00e9 o primeiro de uma s\u00e9rie de textos que ensinam como usar o Soroban (\u7B97\u76E4). Neste texto, vamos nos concentrar na representa\u00e7\u00e3o dos n\u00fameros entre 0 e 9; em textos futuros, aprenderemos a us\u00e1-lo de forma mais ampla. Por enquanto, vamos trabalhar apenas com a primeira coluna da direita.\n\nO marcador superior (vermelho) tem valor 5, enquanto o marcador inferior (azul) tem valor 1. Defina os valores conforme solicitado na caixa amarela perto da caixa \"Valor\".",
)

val EsSorobanWritingScreenStrings = SorobanWritingScreenStrings(
    sorobanWritingInstruction = "Este es el primero de una serie de textos que ense\u00f1an c\u00f3mo usar el Soroban (\u7B97\u76E4). En este texto, nos centraremos en la representaci\u00f3n de n\u00fameros entre 0 y 9; en textos futuros, aprenderemos a usarlo de forma m\u00e1s amplia. Por ahora, solo trabajaremos con la primera columna de la derecha.\n\nEl marcador superior (rojo) tiene valor 5, mientras que el marcador inferior (azul) tiene valor 1. Establezca los valores seg\u00fan lo solicitado en el cuadro amarillo cerca del cuadro \"Valor\".",
)

val LocalSorobanWritingScreenStrings = staticCompositionLocalOf { EnSorobanWritingScreenStrings }

fun sorobanWritingScreenStringsForLanguage(language: String): SorobanWritingScreenStrings = when (language) {
    "pt-BR" -> PtSorobanWritingScreenStrings
    "es-ES" -> EsSorobanWritingScreenStrings
    else -> EnSorobanWritingScreenStrings
}
