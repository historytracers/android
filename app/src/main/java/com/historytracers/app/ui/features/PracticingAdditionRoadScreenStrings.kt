// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PracticingAdditionRoadScreenStrings(
    // Intro: practice exercise
    val practiceTitle: String,
    val practiceIntro: String,
    val practiceStep1: String,
    val practiceStep2: String,
    val practiceStep3: String,
    val practiceComplement: String,
    val practiceFinish: String,
    val start: String,
    // Game screen
    val title: String,
    val gameInstruction: String,
    val orderUnits: String,
    val orderTens: String,
    val orderHundreds: String,
    val questionTemplate: String,
    val incomingNote: String,
    val msgCarry: String,
    val msgNoCarry: String,
    val squaresLabel: String,
    val squaresUsed: String,
    val complementPending: String,
    val complementUsed: String,
    val addOneSquare: String,
    val removeOneSquare: String,
    val nextColumn: String,
    val newProblem: String,
    val medal: String,
)

val EnPracticingAdditionRoadScreenStrings = PracticingAdditionRoadScreenStrings(
    practiceTitle = "Practice Exercise",
    practiceIntro = "Reading about addition helps, but practice is essential for real understanding. Below is an addition written vertically, column by column, from the units to the hundreds. In each column:",
    practiceStep1 = "The light blue squares show the digit already in the column.",
    practiceStep2 = "Use the up button to add one square and the down button to remove one square, until the light yellow squares match the bottom digit of the second number.",
    practiceStep3 = "When the count reaches ten squares, those ten are grouped and become the 1 carried to the next order.",
    practiceComplement = "Before clicking the buttons, look at the complement of the value already in the column: the complement tells you how many squares are still missing to reach ten. Then associate the squares with the result \u2014 the group of ten becomes the 1 carried to the next order, and the squares left outside the group are the digit that stays in the column.",
    practiceFinish = "When you finish the calculation, you will receive a medal (\uD83E\uDD47) and you can move on to the next problem by clicking the button below.",
    start = "Start",
    title = "Practicing Addition",
    gameInstruction = "Solve the addition column by column, starting from the units. In each column, use the buttons to add or remove squares until the light yellow squares match the bottom digit of the second number. When the count reaches ten, those ten become the 1 carried to the next order.",
    orderUnits = "Units",
    orderTens = "Tens",
    orderHundreds = "Hundreds",
    questionTemplate = "The %s column already holds %d. Now use the buttons to add the bottom digit of the second number.",
    incomingNote = "The 1 carried from the previous column is added: %d + 1 = %d.",
    msgCarry = "%1\$d + %2\$d = %3\$d. It reaches 10, so the %5\$s column keeps %4\$d and carries 1 to the next order.",
    msgNoCarry = "%1\$d + %2\$d = %3\$d. It stays below 10, so the %5\$s column keeps %4\$d.",
    squaresLabel = "Addition in this column",
    squaresUsed = "Squares used: %d.",
    complementPending = "Complement of %1\$d to ten: %2\$d. Squares still missing to complete the ten: %3\$d.",
    complementUsed = "Complement of %1\$d to ten: %2\$d. Squares that completed the ten: %2\$d; squares outside the ten: %3\$d.",
    addOneSquare = "Add one square",
    removeOneSquare = "Remove one square",
    nextColumn = "Next column",
    newProblem = "New problem",
    medal = "\uD83E\uDD47",
)

val PtPracticingAdditionRoadScreenStrings = PracticingAdditionRoadScreenStrings(
    practiceTitle = "Exerc\u00edcio de pr\u00e1tica",
    practiceIntro = "Ler sobre a soma ajuda, mas a pr\u00e1tica \u00e9 essencial para uma compreens\u00e3o de verdade. Abaixo h\u00e1 uma soma escrita na vertical, coluna por coluna, das unidades \u00e0s centenas. Em cada coluna:",
    practiceStep1 = "Os quadrados azul-claro mostram o d\u00edgito que j\u00e1 est\u00e1 na coluna.",
    practiceStep2 = "Use o bot\u00e3o para cima para somar um quadrado e o bot\u00e3o para baixo para tirar um quadrado, at\u00e9 que os quadrados amarelo-claro coincidam com o d\u00edgito de baixo do segundo n\u00famero.",
    practiceStep3 = "Quando a conta chegar a dez quadrados, esses dez se agrupam e se tornam o 1 transportado para a pr\u00f3xima ordem.",
    practiceComplement = "Antes de clicar nos bot\u00f5es, observe o complemento do valor que j\u00e1 est\u00e1 na coluna: o complemento diz quantos quadrados ainda faltam para chegar a dez. Depois, associe os quadrados ao resultado \u2014 o grupo de dez se torna o 1 transportado para a pr\u00f3xima ordem, e os quadrados que ficam fora do grupo s\u00e3o o d\u00edgito que permanece na coluna.",
    practiceFinish = "Ao terminar o c\u00e1lculo, voc\u00ea receber\u00e1 uma medalha (\uD83E\uDD47) e poder\u00e1 passar para o pr\u00f3ximo problema clicando no bot\u00e3o abaixo.",
    start = "Come\u00e7ar",
    title = "Praticando Adi\u00e7\u00e3o",
    gameInstruction = "Resolva a soma coluna por coluna, come\u00e7ando pelas unidades. Em cada coluna, use os bot\u00f5es para somar ou remover quadrados at\u00e9 que os quadrados amarelo-claro coincidam com o d\u00edgito de baixo do segundo n\u00famero. Quando a conta chegar a dez quadrados, esses dez se tornam o 1 transportado para a pr\u00f3xima ordem.",
    orderUnits = "Unidades",
    orderTens = "Dezenas",
    orderHundreds = "Centenas",
    questionTemplate = "A coluna das %s j\u00e1 tem %d. Agora use os bot\u00f5es para somar o d\u00edgito de baixo do segundo n\u00famero.",
    incomingNote = "O 1 transportado da coluna anterior \u00e9 somado: %d + 1 = %d.",
    msgCarry = "%1\$d + %2\$d = %3\$d. Chega a 10, ent\u00e3o a coluna das %5\$s mant\u00e9m %4\$d e transporta 1 para a pr\u00f3xima ordem.",
    msgNoCarry = "%1\$d + %2\$d = %3\$d. Fica abaixo de 10, ent\u00e3o a coluna das %5\$s mant\u00e9m %4\$d.",
    squaresLabel = "Soma nesta coluna",
    squaresUsed = "Quadrados usados: %d.",
    complementPending = "Complemento de %1\$d para dez: %2\$d. Ainda faltam %3\$d quadrados para completar o dez.",
    complementUsed = "Complemento de %1\$d para dez: %2\$d. Quadrados que completaram o dez: %2\$d; quadrados fora do dez: %3\$d.",
    addOneSquare = "Somar um quadrado",
    removeOneSquare = "Remover um quadrado",
    nextColumn = "Pr\u00f3xima coluna",
    newProblem = "Novo problema",
    medal = "\uD83E\uDD47",
)

val EsPracticingAdditionRoadScreenStrings = PracticingAdditionRoadScreenStrings(
    practiceTitle = "Ejercicio de pr\u00e1ctica",
    practiceIntro = "Leer sobre la suma ayuda, pero la pr\u00e1ctica es esencial para una comprensi\u00f3n real. A continuaci\u00f3n se muestra una suma escrita en vertical, columna por columna, desde las unidades hasta las centenas. En cada columna:",
    practiceStep1 = "Los cuadrados azul claro muestran el d\u00edgito que ya est\u00e1 en la columna.",
    practiceStep2 = "Usa el bot\u00f3n hacia arriba para a\u00f1adir un cuadrado y el bot\u00f3n hacia abajo para quitar uno, hasta que los cuadrados amarillo claro coincidan con el d\u00edgito inferior del segundo n\u00famero.",
    practiceStep3 = "Cuando la cuenta llegue a diez cuadrados, esos diez se agrupan y se convierten en el 1 que se acarrea al siguiente orden.",
    practiceComplement = "Antes de hacer clic en los botones, observa el complemento del valor que ya est\u00e1 en la columna: el complemento indica cu\u00e1ntos cuadrados faltan para llegar a diez. Despu\u00e9s, asocia los cuadrados con el resultado \u2014 el grupo de diez se convierte en el 1 que se acarrea al siguiente orden, y los cuadrados que quedan fuera del grupo son el d\u00edgito que permanece en la columna.",
    practiceFinish = "Al terminar el c\u00e1lculo, recibir\u00e1s una medalla (\uD83E\uDD47) y podr\u00e1s pasar al siguiente problema haciendo clic en el bot\u00f3n de abajo.",
    start = "Empezar",
    title = "Practicando Suma",
    gameInstruction = "Resuelve la suma columna por columna, empezando por las unidades. En cada columna, usa los botones para sumar o quitar cuadrados hasta que los cuadrados amarillo claro coincidan con el d\u00edgito inferior del segundo n\u00famero. Cuando la cuenta llegue a diez cuadrados, esos diez se convierten en el 1 que se acarrea al siguiente orden.",
    orderUnits = "Unidades",
    orderTens = "Decenas",
    orderHundreds = "Centenas",
    questionTemplate = "La columna de las %s ya tiene %d. Ahora usa los botones para sumar el d\u00edgito inferior del segundo n\u00famero.",
    incomingNote = "El 1 acarreado de la columna anterior se suma: %d + 1 = %d.",
    msgCarry = "%1\$d + %2\$d = %3\$d. Alcanza 10, as\u00ed que la columna de las %5\$s conserva %4\$d y acarrea 1 al siguiente orden.",
    msgNoCarry = "%1\$d + %2\$d = %3\$d. No llega a 10, as\u00ed que la columna de las %5\$s conserva %4\$d.",
    squaresLabel = "Suma en esta columna",
    squaresUsed = "Cuadrados usados: %d.",
    complementPending = "Complemento de %1\$d hasta diez: %2\$d. Todav\u00eda faltan %3\$d cuadrados para completar el diez.",
    complementUsed = "Complemento de %1\$d hasta diez: %2\$d. Cuadrados que completaron el diez: %2\$d; cuadrados fuera del diez: %3\$d.",
    addOneSquare = "Sumar un cuadrado",
    removeOneSquare = "Quitar un cuadrado",
    nextColumn = "Siguiente columna",
    newProblem = "Nuevo problema",
    medal = "\uD83E\uDD47",
)

val LocalPracticingAdditionRoadScreenStrings = staticCompositionLocalOf { EnPracticingAdditionRoadScreenStrings }

fun practicingAdditionRoadScreenStringsForLanguage(language: String): PracticingAdditionRoadScreenStrings = when (language) {
    "pt-BR" -> PtPracticingAdditionRoadScreenStrings
    "es-ES" -> EsPracticingAdditionRoadScreenStrings
    else -> EnPracticingAdditionRoadScreenStrings
}
