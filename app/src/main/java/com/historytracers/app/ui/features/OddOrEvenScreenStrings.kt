// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class OddOrEvenScreenStrings(
    val title: String,
    val howToPlay: String,
    val numberHeader: String,
    val shareHeader: String,
    val side1: String,
    val side2: String,
    val odd: String,
    val even: String,
    val newExercise: String,
    val msgOddCorrect: String,
    val msgEvenCorrect: String,
    val msgOddWrong: String,
    val msgEvenWrong: String,
)

val EnOddOrEvenScreenStrings = OddOrEvenScreenStrings(
    title = "Odd or Even?",
    howToPlay = "Look at the number and how its circles are shared between the two sides. Use the buttons to answer: tap Odd if one circle is left over, or tap Even if the circles form complete pairs with nothing left over.",
    numberHeader = "Number",
    shareHeader = "Sharing in pairs",
    side1 = "Side 1",
    side2 = "Side 2",
    odd = "Odd",
    even = "Even",
    newExercise = "\uD83C\uDFB2 New exercise",
    msgOddCorrect = "Correct! %1\$d is odd. When we divide %1\$d by 2, we get %2\$d with remainder 1. One circle is left over, so one side has one more circle than the other.",
    msgEvenCorrect = "Correct! %1\$d is even. When we divide %1\$d by 2, we get %2\$d with remainder 0. The circles form %2\$d complete pairs, so both sides are equal.",
    msgOddWrong = "The number is odd, because when divided by 2 there is one remaining: %1\$d = 2 \u00d7 %2\$d + 1. That extra circle is left over on one side.",
    msgEvenWrong = "The number is even, because when divided by 2 there is no remainder: %1\$d = 2 \u00d7 %2\$d. The circles can be shared between two equal sides, so nothing is left over.",
)

val PtOddOrEvenScreenStrings = OddOrEvenScreenStrings(
    title = "\u00cdmpar ou par?",
    howToPlay = "Observe o n\u00famero e como os seus c\u00edrculos s\u00e3o repartidos entre os dois lados. Use os bot\u00f5es para responder: toque em \u00cdmpar se sobrar um c\u00edrculo, ou em Par se os c\u00edrculos formarem pares completos sem sobrar nada.",
    numberHeader = "N\u00famero",
    shareHeader = "Compartilhar em pares",
    side1 = "Lado 1",
    side2 = "Lado 2",
    odd = "\u00cdmpar",
    even = "Par",
    newExercise = "\uD83C\uDFB2 Novo exerc\u00edcio",
    msgOddCorrect = "Correto! %1\$d \u00e9 \u00edmpar. Quando dividimos %1\$d por 2, obtemos %2\$d com resto 1. Sobra um c\u00edrculo, ent\u00e3o um lado tem um c\u00edrculo a mais que o outro.",
    msgEvenCorrect = "Correto! %1\$d \u00e9 par. Quando dividimos %1\$d por 2, obtemos %2\$d com resto 0. Os c\u00edrculos formam %2\$d pares completos, ent\u00e3o os dois lados s\u00e3o iguais.",
    msgOddWrong = "O n\u00famero \u00e9 \u00edmpar, porque ao dividir por 2 sobra um: %1\$d = 2 \u00d7 %2\$d + 1. Esse c\u00edrculo extra sobra de um lado.",
    msgEvenWrong = "O n\u00famero \u00e9 par, porque ao dividir por 2 n\u00e3o h\u00e1 resto: %1\$d = 2 \u00d7 %2\$d. Os c\u00edrculos podem ser repartidos em dois lados iguais, ent\u00e3o n\u00e3o sobra nada.",
)

val EsOddOrEvenScreenStrings = OddOrEvenScreenStrings(
    title = "\u00bfImpar o par?",
    howToPlay = "Observa el n\u00famero y c\u00f3mo se reparten sus c\u00edrculos entre los dos lados. Usa los botones para responder: toca Impar si sobra un c\u00edrculo, o Par si los c\u00edrculos forman pares completos sin que sobre nada.",
    numberHeader = "N\u00famero",
    shareHeader = "Compartir en pares",
    side1 = "Lado 1",
    side2 = "Lado 2",
    odd = "Impar",
    even = "Par",
    newExercise = "\uD83C\uDFB2 Nuevo ejercicio",
    msgOddCorrect = "\u00a1Correcto! %1\$d es impar. Cuando dividimos %1\$d entre 2, obtenemos %2\$d con resto 1. Sobra un c\u00edrculo, por lo que un lado tiene un c\u00edrculo m\u00e1s que el otro.",
    msgEvenCorrect = "\u00a1Correcto! %1\$d es par. Cuando dividimos %1\$d entre 2, obtenemos %2\$d con resto 0. Los c\u00edrculos forman %2\$d pares completos, por lo que ambos lados son iguales.",
    msgOddWrong = "El n\u00famero es impar, porque al dividirlo entre 2 sobra uno: %1\$d = 2 \u00d7 %2\$d + 1. Ese c\u00edrculo extra sobra en un lado.",
    msgEvenWrong = "El n\u00famero es par, porque al dividirlo entre 2 no hay resto: %1\$d = 2 \u00d7 %2\$d. Los c\u00edrculos se pueden repartir en dos lados iguales, por lo que no sobra nada.",
)

val LocalOddOrEvenScreenStrings = staticCompositionLocalOf { EnOddOrEvenScreenStrings }

fun oddOrEvenScreenStringsForLanguage(language: String): OddOrEvenScreenStrings = when (language) {
    "pt-BR" -> PtOddOrEvenScreenStrings
    "es-ES" -> EsOddOrEvenScreenStrings
    else -> EnOddOrEvenScreenStrings
}
