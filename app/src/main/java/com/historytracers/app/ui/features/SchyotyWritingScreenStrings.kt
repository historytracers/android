// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class SchyotyWritingScreenStrings(
    val schyotyWritingInstruction: String,
    val schyotyAllLevelsComplete: String,
)

val EnSchyotyWritingScreenStrings = SchyotyWritingScreenStrings(
    schyotyWritingInstruction = "In Schyoty (\u0441\u0447\u0451\u0442\u044B), all beads have the same value. When all beads are on the right, the value is zero. Move beads to the left to represent numbers. Beads 5 and 6 (from the left) are colored gray. The Schyoty has 9 rows, each representing a decimal place, with units at the bottom and higher orders above. Progress through levels by representing numbers with more digits.",
    schyotyAllLevelsComplete = "\uD83C\uDF89\uD83C\uDF86 ALL LEVELS COMPLETE! \uD83C\uDF86\uD83C\uDF89\nYou mastered the Schyoty abacus!",
)

val PtSchyotyWritingScreenStrings = SchyotyWritingScreenStrings(
    schyotyWritingInstruction = "No Schyoty (\u0441\u0447\u0451\u0442\u044B), todas as contas t\u00eam o mesmo valor. Quando todas as contas est\u00e3o \u00e0 direita, o valor \u00e9 zero. Mova as contas para a esquerda para representar n\u00fameros. As contas 5 e 6 (da esquerda) s\u00e3o cinzas. O Schyoty tem 9 linhas, cada uma representando uma casa decimal, com as unidades na parte inferior e ordens superiores acima. Avance pelos n\u00edveis representando n\u00fameros com mais d\u00edgitos.",
    schyotyAllLevelsComplete = "\uD83C\uDF89\uD83C\uDF86 TODOS OS N\u00cdVEIS COMPLETOS! \uD83C\uDF86\uD83C\uDF89\nVoc\u00ea dominou o \u00e1baco Schyoty!",
)

val EsSchyotyWritingScreenStrings = SchyotyWritingScreenStrings(
    schyotyWritingInstruction = "En Schyoty (\u0441\u0447\u0451\u0442\u044B), todas las cuentas tienen el mismo valor. Cuando todas las cuentas est\u00e1n a la derecha, el valor es cero. Mueve las cuentas hacia la izquierda para representar n\u00fameros. Las cuentas 5 y 6 (desde la izquierda) son grises. El Schyoty tiene 9 filas, cada una representa un lugar decimal, con las unidades en la parte inferior y los \u00f3rdenes superiores arriba. Avanza por los niveles representando n\u00fameros con m\u00e1s d\u00edgitos.",
    schyotyAllLevelsComplete = "\uD83C\uDF89\uD83C\uDF86 \u00a1TODOS LOS NIVELES COMPLETADOS! \uD83C\uDF86\uD83C\uDF89\n\u00a1Dominaste el \u00e1baco Schyoty!",
)

val LocalSchyotyWritingScreenStrings = staticCompositionLocalOf { EnSchyotyWritingScreenStrings }

fun schyotyWritingScreenStringsForLanguage(language: String): SchyotyWritingScreenStrings = when (language) {
    "pt-BR" -> PtSchyotyWritingScreenStrings
    "es-ES" -> EsSchyotyWritingScreenStrings
    else -> EnSchyotyWritingScreenStrings
}
