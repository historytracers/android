// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SuanpanWritingScreenStrings(
    val suanpanWritingInstruction: String,
)

val EnSuanpanWritingScreenStrings = SuanpanWritingScreenStrings(
    suanpanWritingInstruction = "This is the first in a series of texts that teach how to use the Suanpan (\u7B97\u76E4). In this text, we will focus on the representation of numbers between 0 and 9; in future texts, we will learn how to use it more broadly. For now, we will only work with the first column from the right.\n\nThe markers on top (red) have value 5 each, while the markers below (blue) have value 1 each. Set the values as requested in the yellow box near the 'Value' box.",
)

val PtSuanpanWritingScreenStrings = SuanpanWritingScreenStrings(
    suanpanWritingInstruction = "Este \u00e9 o primeiro de uma s\u00e9rie de textos que ensinam como usar o Suanpan (\u7B97\u76E4). Neste texto, vamos nos concentrar na representa\u00e7\u00e3o dos n\u00fameros entre 0 e 9; em textos futuros, aprenderemos a us\u00e1-lo de forma mais ampla. Por enquanto, vamos trabalhar apenas com a primeira coluna da direita.\n\nOs marcadores superiores (vermelho) t\u00eam valor 5 cada, enquanto os marcadores inferiores (azul) t\u00eam valor 1 cada. Defina os valores conforme solicitado na caixa amarela perto da caixa \"Valor\".",
)

val EsSuanpanWritingScreenStrings = SuanpanWritingScreenStrings(
    suanpanWritingInstruction = "Este es el primero de una serie de textos que ense\u00f1an c\u00f3mo usar el Suanpan (\u7B97\u76E4). En este texto, nos centraremos en la representaci\u00f3n de n\u00fameros entre 0 y 9; en textos futuros, aprenderemos a usarlo de forma m\u00e1s amplia. Por ahora, solo trabajaremos con la primera columna de la derecha.\n\nLos marcadores superiores (rojo) tienen valor 5 cada uno, mientras que los marcadores inferiores (azul) tienen valor 1 cada uno. Establezca los valores seg\u00fan lo solicitado en el cuadro amarillo cerca del cuadro \"Valor\".",
)

val LocalSuanpanWritingScreenStrings = staticCompositionLocalOf { EnSuanpanWritingScreenStrings }

fun suanpanWritingScreenStringsForLanguage(language: String): SuanpanWritingScreenStrings = when (language) {
    "pt-BR" -> PtSuanpanWritingScreenStrings
    "es-ES" -> EsSuanpanWritingScreenStrings
    else -> EnSuanpanWritingScreenStrings
}
