// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class AbacusWriteStrings(
    val writingToSoroban: String,
    val writingToSuanpan: String,
    val writingLargeNumbers: String,
    val largeNumbers: String,
    val sorobanWritingInstruction: String,
    val suanpanWritingInstruction: String,
    val largeNumbersWritingInstruction: String,
    val sorobanAbbr: String,
    val suanpanAbbr: String,
    val sorobanMode: String,
    val suanpanMode: String,
    val schyoty: String,
    val writingToSchyoty: String,
    val schyotyWritingInstruction: String,
    val schyotyAllLevelsComplete: String,
)

val EnAbacusWriteStrings = AbacusWriteStrings(
    writingToSoroban = "Writing to Soroban",
    writingToSuanpan = "Writing to Suanpan",
    writingLargeNumbers = "Writing Large Numbers",
    largeNumbers = "Large Numbers",
    sorobanWritingInstruction = "This is the first in a series of texts that teach how to use the Soroban (\u7B97\u76E4). In this text, we will focus on the representation of numbers between 0 and 9; in future texts, we will learn how to use it more broadly. For now, we will only work with the first column from the right.\n\nThe marker on top (red) has value 5, while the marker below (blue) has value 1. Set the values as requested in the yellow box near the 'Value' box.",
    suanpanWritingInstruction = "This is the first in a series of texts that teach how to use the Suanpan (\u7B97\u76E4). In this text, we will focus on the representation of numbers between 0 and 9; in future texts, we will learn how to use it more broadly. For now, we will only work with the first column from the right.\n\nThe markers on top (red) have value 5 each, while the markers below (blue) have value 1 each. Set the values as requested in the yellow box near the 'Value' box.",
    largeNumbersWritingInstruction = "Practice representing large numbers (1 to 99,999,999) on both the Soroban (\u7B97\u76E4) and Suanpan (\u7B97\u76E4) simultaneously. Both abacuses share the same values. Use the 9 columns to represent units, tens, hundreds, and so on. Set the requested value shown in the yellow box using either abacus.",
    sorobanAbbr = "S",
    suanpanAbbr = "S",
    sorobanMode = "Soroban (1\u00D74)",
    suanpanMode = "Suanpan (2\u00D75)",
    schyoty = "\u0441\u0447\u0451\u0442\u044B",
    writingToSchyoty = "Writing to \"Schyoty\"",
    schyotyWritingInstruction = "In Schyoty (\u0441\u0447\u0451\u0442\u044B), all beads have the same value. When all beads are on the right, the value is zero. Move beads to the left to represent numbers. Beads 5 and 6 (from the left) are colored gray. The Schyoty has 9 rows, each representing a decimal place, with units at the bottom and higher orders above. Progress through levels by representing numbers with more digits.",
    schyotyAllLevelsComplete = "\uD83C\uDF89\uD83C\uDF86 ALL LEVELS COMPLETE! \uD83C\uDF86\uD83C\uDF89\nYou mastered the Schyoty abacus!",
)

val PtAbacusWriteStrings = AbacusWriteStrings(
    writingToSoroban = "Escrevendo no Soroban",
    writingToSuanpan = "Escrevendo no Suanpan",
    writingLargeNumbers = "Escrevendo n\u00fameros grandes",
    largeNumbers = "N\u00fameros grandes",
    sorobanWritingInstruction = "Este \u00e9 o primeiro de uma s\u00e9rie de textos que ensinam como usar o Soroban (\u7B97\u76E4). Neste texto, vamos nos concentrar na representa\u00e7\u00e3o dos n\u00fameros entre 0 e 9; em textos futuros, aprenderemos a us\u00e1-lo de forma mais ampla. Por enquanto, vamos trabalhar apenas com a primeira coluna da direita.\n\nO marcador superior (vermelho) tem valor 5, enquanto o marcador inferior (azul) tem valor 1. Defina os valores conforme solicitado na caixa amarela perto da caixa \"Valor\".",
    suanpanWritingInstruction = "Este \u00e9 o primeiro de uma s\u00e9rie de textos que ensinam como usar o Suanpan (\u7B97\u76E4). Neste texto, vamos nos concentrar na representa\u00e7\u00e3o dos n\u00fameros entre 0 e 9; em textos futuros, aprenderemos a us\u00e1-lo de forma mais ampla. Por enquanto, vamos trabalhar apenas com a primeira coluna da direita.\n\nOs marcadores superiores (vermelho) t\u00eam valor 5 cada, enquanto os marcadores inferiores (azul) t\u00eam valor 1 cada. Defina os valores conforme solicitado na caixa amarela perto da caixa \"Valor\".",
    largeNumbersWritingInstruction = "Pratique a representa\u00e7\u00e3o de n\u00fameros grandes (1 a 99.999.999) no Soroban (\u7B97\u76E4) e no Suanpan (\u7B97\u76E4) simultaneamente. Ambos os \u00e1bacos compartilham os mesmos valores. Use as 9 colunas para representar unidades, dezenas, centenas e assim por diante. Defina o valor solicitado na caixa amarela usando qualquer um dos \u00e1bacos.",
    sorobanAbbr = "S",
    suanpanAbbr = "S",
    sorobanMode = "Soroban (1\u00D74)",
    suanpanMode = "Suanpan (2\u00D75)",
    schyoty = "\u0441\u0447\u0451\u0442\u044B",
    writingToSchyoty = "Writing to \"Schyoty\"",
    schyotyWritingInstruction = "No Schyoty (\u0441\u0447\u0451\u0442\u044B), todas as contas t\u00eam o mesmo valor. Quando todas as contas est\u00e3o \u00e0 direita, o valor \u00e9 zero. Mova as contas para a esquerda para representar n\u00fameros. As contas 5 e 6 (da esquerda) s\u00e3o cinzas. O Schyoty tem 9 linhas, cada uma representando uma casa decimal, com as unidades na parte inferior e ordens superiores acima. Avance pelos n\u00edveis representando n\u00fameros com mais d\u00edgitos.",
    schyotyAllLevelsComplete = "\uD83C\uDF89\uD83C\uDF86 TODOS OS N\u00cdVEIS COMPLETOS! \uD83C\uDF86\uD83C\uDF89\nVoc\u00ea dominou o \u00e1baco Schyoty!",
)

val EsAbacusWriteStrings = AbacusWriteStrings(
    writingToSoroban = "Escribiendo en el Soroban",
    writingToSuanpan = "Escribiendo en el Suanpan",
    writingLargeNumbers = "Escribiendo n\u00fameros grandes",
    largeNumbers = "N\u00fameros grandes",
    sorobanWritingInstruction = "Este es el primero de una serie de textos que ense\u00f1an c\u00f3mo usar el Soroban (\u7B97\u76E4). En este texto, nos centraremos en la representaci\u00f3n de n\u00fameros entre 0 y 9; en textos futuros, aprenderemos a usarlo de forma m\u00e1s amplia. Por ahora, solo trabajaremos con la primera columna de la derecha.\n\nEl marcador superior (rojo) tiene valor 5, mientras que el marcador inferior (azul) tiene valor 1. Establezca los valores seg\u00fan lo solicitado en el cuadro amarillo cerca del cuadro \"Valor\".",
    suanpanWritingInstruction = "Este es el primero de una serie de textos que ense\u00f1an c\u00f3mo usar el Suanpan (\u7B97\u76E4). En este texto, nos centraremos en la representaci\u00f3n de n\u00fameros entre 0 y 9; en textos futuros, aprenderemos a usarlo de forma m\u00e1s amplia. Por ahora, solo trabajaremos con la primera columna de la derecha.\n\nLos marcadores superiores (rojo) tienen valor 5 cada uno, mientras que los marcadores inferiores (azul) tienen valor 1 cada uno. Establezca los valores seg\u00fan lo solicitado en el cuadro amarillo cerca del cuadro \"Valor\".",
    largeNumbersWritingInstruction = "Practique la representaci\u00f3n de n\u00fameros grandes (1 a 99.999.999) en el Soroban (\u7B97\u76E4) y en el Suanpan (\u7B97\u76E4) simult\u00e1neamente. Ambos \u00e1bacos comparten los mismos valores. Use las 9 columnas para representar unidades, decenas, centenas y as\u00ed sucesivamente. Establezca el valor solicitado en el cuadro amarillo usando cualquiera de los \u00e1bacos.",
    sorobanAbbr = "S",
    suanpanAbbr = "S",
    sorobanMode = "Soroban (1\u00D74)",
    suanpanMode = "Suanpan (2\u00D75)",
    schyoty = "\u0441\u0447\u0451\u0442\u044B",
    writingToSchyoty = "Writing to \"Schyoty\"",
    schyotyWritingInstruction = "En Schyoty (\u0441\u0447\u0451\u0442\u044B), todas las cuentas tienen el mismo valor. Cuando todas las cuentas est\u00e1n a la derecha, el valor es cero. Mueve las cuentas hacia la izquierda para representar n\u00fameros. Las cuentas 5 y 6 (desde la izquierda) son grises. El Schyoty tiene 9 filas, cada una representa un lugar decimal, con las unidades en la parte inferior y los \u00f3rdenes superiores arriba. Avanza por los niveles representando n\u00fameros con m\u00e1s d\u00edgitos.",
    schyotyAllLevelsComplete = "\uD83C\uDF89\uD83C\uDF86 \u00a1TODOS LOS NIVELES COMPLETADOS! \uD83C\uDF86\uD83C\uDF89\n\u00a1Dominaste el \u00e1baco Schyoty!",
)

val LocalAbacusWriteStrings = staticCompositionLocalOf { EnAbacusWriteStrings }

fun abacusWriteStringsForLanguage(language: String): AbacusWriteStrings = when (language) {
    "pt-BR" -> PtAbacusWriteStrings
    "es-ES" -> EsAbacusWriteStrings
    else -> EnAbacusWriteStrings
}
