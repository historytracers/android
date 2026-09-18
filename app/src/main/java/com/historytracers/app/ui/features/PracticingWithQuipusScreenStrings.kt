// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PracticingWithQuipusScreenStrings(
    val title: String,
    val instruction: String,
    val orderUnits: String,
    val orderTens: String,
    val orderHundreds: String,
    val feedbackOrder: String,
    val feedbackString: String,
    val feedbackLevel: String,
    val feedbackAllLevels: String,
    val addKnot: String,
    val removeKnot: String,
)

val EnPracticingWithQuipusScreenStrings = PracticingWithQuipusScreenStrings(
    title = "Practicing with Quipus",
    instruction = "Add knots to build the number shown above each string. When an order is complete, the game jumps to the next order.",
    orderUnits = "Units",
    orderTens = "Tens",
    orderHundreds = "Hundreds",
    feedbackOrder = "Order complete! The game jumps to the next order.",
    feedbackString = "Number complete! Now continue with the next string.",
    feedbackLevel = "Level complete! Click Next Level to continue or New Exercise to redo this level.",
    feedbackAllLevels = "\uD83C\uDF89\uD83C\uDFC6 ALL LEVELS COMPLETE! \uD83C\uDFC6\uD83C\uDF89 You built every number! Click Next Level to start again at Level 1 or New Exercise to redo this level.",
    addKnot = "Add Knot",
    removeKnot = "Remove Knot",
)

val PtPracticingWithQuipusScreenStrings = PracticingWithQuipusScreenStrings(
    title = "Praticando com Quipus",
    instruction = "Adicione n\u00f3s para construir o n\u00famero mostrado acima de cada cord\u00e3o. Quando uma ordem se completa, o jogo salta para a ordem seguinte.",
    orderUnits = "Unidades",
    orderTens = "Dezenas",
    orderHundreds = "Centenas",
    feedbackOrder = "Ordem completa! O jogo salta para a ordem seguinte.",
    feedbackString = "N\u00famero completo! Agora continue com o pr\u00f3ximo cord\u00e3o.",
    feedbackLevel = "N\u00edvel completo! Clique em Pr\u00f3ximo N\u00edvel para continuar ou em Novo Exerc\u00edcio para refazer este n\u00edvel.",
    feedbackAllLevels = "\uD83C\uDF89\uD83C\uDFC6 TODOS OS N\u00cdVEIS CONCLU\u00cdDOS! \uD83C\uDFC6\uD83C\uDF89 Voc\u00ea construiu todos os n\u00fameros! Clique em Pr\u00f3ximo N\u00edvel para come\u00e7ar de novo no N\u00edvel 1 ou em Novo Exerc\u00edcio para refazer este n\u00edvel.",
    addKnot = "Adicionar N\u00f3",
    removeKnot = "Remover N\u00f3",
)

val EsPracticingWithQuipusScreenStrings = PracticingWithQuipusScreenStrings(
    title = "Practicando con Quipus",
    instruction = "A\u00f1ade nudos para construir el n\u00famero que aparece sobre cada cuerda. Cuando un orden se completa, el juego salta al orden siguiente.",
    orderUnits = "Unidades",
    orderTens = "Decenas",
    orderHundreds = "Centenas",
    feedbackOrder = "\u00a1Orden completo! El juego salta al orden siguiente.",
    feedbackString = "\u00a1N\u00famero completo! Ahora contin\u00faa con la siguiente cuerda.",
    feedbackLevel = "\u00a1Nivel completo! Haz clic en Siguiente Nivel para continuar o en Nuevo Ejercicio para repetir este nivel.",
    feedbackAllLevels = "\u00a1\uD83C\uDF89\uD83C\uDFC6 TODOS LOS NIVELES COMPLETADOS! \uD83C\uDFC6\uD83C\uDF89 \u00a1Construiste todos los n\u00fameros! Haz clic en Siguiente Nivel para empezar de nuevo en el Nivel 1 o en Nuevo Ejercicio para repetir este nivel.",
    addKnot = "A\u00f1adir Nudo",
    removeKnot = "Quitar Nudo",
)

val LocalPracticingWithQuipusScreenStrings = staticCompositionLocalOf { EnPracticingWithQuipusScreenStrings }

fun practicingWithQuipusScreenStringsForLanguage(language: String): PracticingWithQuipusScreenStrings = when (language) {
    "pt-BR" -> PtPracticingWithQuipusScreenStrings
    "es-ES" -> EsPracticingWithQuipusScreenStrings
    else -> EnPracticingWithQuipusScreenStrings
}
