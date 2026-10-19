// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class PracticingAdditionTypedScreenStrings(
    // Intro screen
    val introTitle: String,
    val introBody: String,
    val checkingBody: String,
    val complementBody: String,
    val finishBody: String,
    val start: String,
    // Game screen
    val title: String,
    val orderUnits: String,
    val orderTens: String,
    val orderHundreds: String,
    val questionTemplate: String,
    val incomingNote: String,
    val msgCarry: String,
    val msgNoCarry: String,
    val wrongDigit: String,
    val wrongCarry: String,
    val congrats: String,
    val perfect: String,
    val nextColumn: String,
    val newProblem: String,
    val squaresLabel: String,
    val squaresUsed: String,
    val complementPending: String,
    val complementUsed: String,
    val digitLabel: String,
    val carryLabel: String,
)

val EnPracticingAdditionTypedScreenStrings = PracticingAdditionTypedScreenStrings(
    introTitle = "Practice Exercise",
    introBody = "Reading about addition helps, but practice is essential for real understanding. Below is an addition written vertically, column by column, from the units to the hundreds.",
    checkingBody = "The values are checked as you type. If a digit is wrong, the squares are colored to match the number you wrote, and a tip reminds you of the complement. If the sum carries, the squares beyond the group of ten are colored, showing the digit that stays in the column. When both values are right, the squares fill in, a congratulation appears, and the button that moves to the next column is shown.",
    complementBody = "Before typing, look at the complement of the value already in the column: the complement tells you how many squares are still missing to reach ten. If the bottom digit is at least the complement, the sum reaches ten and 1 is carried to the next order; the squares left outside the group of ten are the digit that stays in the column.",
    finishBody = "When you finish the calculation, you can move on to the next problem by clicking the button below.",
    start = "Start",
    title = "Practicing Addition with Typed Values",
    orderUnits = "Units",
    orderTens = "Tens",
    orderHundreds = "Hundreds",
    questionTemplate = "The %s column already holds %d. Add the bottom digit %d: type the digit that stays in the column in the result row, and, if the sum reaches ten, type 1 in the carry box above the next order.",
    incomingNote = "The 1 carried from the previous column is added: %d + 1 = %d.",
    msgCarry = "%1\$d + %2\$d = %3\$d. It reaches 10, so the %5\$s column keeps %4\$d and carries 1 to the next order.",
    msgNoCarry = "%1\$d + %2\$d = %3\$d. It stays below 10, so the %5\$s column keeps %4\$d.",
    wrongDigit = "That digit is not correct. Remember the complement of %1\$d to ten: %2\$d. Use it to find the digit that stays in the %3\$s column.",
    wrongCarry = "The carry is not correct. Check %1\$d + %2\$d: when the sum reaches ten, the carry must be 1.",
    congrats = "\u2705 Correct! Click 'Next column' to continue.",
    perfect = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\nGreat job!",
    nextColumn = "Next column",
    newProblem = "New problem",
    squaresLabel = "Addition in this column",
    squaresUsed = "Squares used: %d.",
    complementPending = "Complement of %1\$d to ten: %2\$d. Squares still missing to complete the ten: %3\$d.",
    complementUsed = "Complement of %1\$d to ten: %2\$d. Squares that completed the ten: %2\$d; squares outside the ten: %3\$d.",
    digitLabel = "Digit that the column keeps",
    carryLabel = "Carry to the next order",
)

val PtPracticingAdditionTypedScreenStrings = PracticingAdditionTypedScreenStrings(
    introTitle = "Exerc\u00edcio de pr\u00e1tica",
    introBody = "Ler sobre a soma ajuda, mas praticar \u00e9 essencial para compreend\u00ea-la de verdade. Abaixo h\u00e1 uma soma escrita na vertical, coluna por coluna, das unidades \u00e0s centenas.",
    checkingBody = "Os valores s\u00e3o verificados \u00e0 medida que voc\u00ea digita. Se um d\u00edgito estiver incorreto, os quadrados s\u00e3o coloridos de acordo com o n\u00famero digitado, e uma dica lembra o complemento. Quando a soma transporta, os quadrados al\u00e9m do grupo de dez s\u00e3o coloridos, mostrando o d\u00edgito que fica na coluna. Quando os dois valores est\u00e3o corretos, os quadrados s\u00e3o preenchidos, aparece um parab\u00e9ns e \u00e9 mostrado o bot\u00e3o que passa para a pr\u00f3xima coluna.",
    complementBody = "Antes de digitar, observe o complemento do valor que j\u00e1 est\u00e1 na coluna: o complemento diz quantos quadrados ainda faltam para chegar a dez. Se o d\u00edgito de baixo for pelo menos o complemento, a soma chega a dez e o 1 \u00e9 transportado para a pr\u00f3xima ordem; os quadrados que ficam fora do grupo de dez s\u00e3o o d\u00edgito que fica na coluna.",
    finishBody = "Ao terminar o c\u00e1lculo, voc\u00ea poder\u00e1 passar para o pr\u00f3ximo problema clicando no bot\u00e3o abaixo.",
    start = "Come\u00e7ar",
    title = "Praticando Adi\u00e7\u00e3o com Valores Digitados",
    orderUnits = "Unidades",
    orderTens = "Dezenas",
    orderHundreds = "Centenas",
    questionTemplate = "A coluna das %s j\u00e1 tem %d. Some o d\u00edgito de baixo %d: digite na linha do resultado o d\u00edgito que fica na coluna e, quando a soma chegar a dez, digite 1 na caixa de transporte acima da pr\u00f3xima ordem.",
    incomingNote = "O 1 transportado da coluna anterior \u00e9 somado: %d + 1 = %d.",
    msgCarry = "%1\$d + %2\$d = %3\$d. Chega a 10, ent\u00e3o a coluna das %5\$s mant\u00e9m %4\$d e transporta 1 para a pr\u00f3xima ordem.",
    msgNoCarry = "%1\$d + %2\$d = %3\$d. Fica abaixo de 10, ent\u00e3o a coluna das %5\$s mant\u00e9m %4\$d.",
    wrongDigit = "Esse d\u00edgito n\u00e3o est\u00e1 correto. Lembre-se do complemento de %1\$d para dez: %2\$d. Use-o para encontrar o d\u00edgito que fica na coluna das %3\$s.",
    wrongCarry = "O transporte n\u00e3o est\u00e1 correto. Verifique %1\$d + %2\$d: quando a soma chega a dez, o transporte deve ser 1.",
    congrats = "\u2705 Correto! Clique em 'Pr\u00f3xima coluna' para continuar.",
    perfect = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFEITO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\nBom trabalho!",
    nextColumn = "Pr\u00f3xima coluna",
    newProblem = "Novo problema",
    squaresLabel = "Soma nesta coluna",
    squaresUsed = "Quadrados usados: %d.",
    complementPending = "Complemento de %1\$d para dez: %2\$d. Ainda faltam %3\$d quadrados para completar o dez.",
    complementUsed = "Complemento de %1\$d para dez: %2\$d. Quadrados que completaram o dez: %2\$d; quadrados fora do dez: %3\$d.",
    digitLabel = "D\u00edgito que fica na coluna",
    carryLabel = "Transporte para a pr\u00f3xima ordem",
)

val EsPracticingAdditionTypedScreenStrings = PracticingAdditionTypedScreenStrings(
    introTitle = "Ejercicio de pr\u00e1ctica",
    introBody = "Leer sobre la suma ayuda, pero practicar es esencial para comprenderla de verdad. A continuaci\u00f3n hay una suma escrita en vertical, columna por columna, desde las unidades hasta las centenas.",
    checkingBody = "Los valores se comprueban a medida que escribes. Si un d\u00edgito es incorrecto, los cuadrados se colorean seg\u00fan el n\u00famero escrito, y una pista te recuerda el complemento. Si la suma acarrea, se colorean los cuadrados m\u00e1s all\u00e1 del grupo de diez, mostrando el d\u00edgito que queda en la columna. Cuando los dos valores son correctos, los cuadrados se llenan, aparece una felicitaci\u00f3n y se muestra el bot\u00f3n que pasa a la siguiente columna.",
    complementBody = "Antes de escribir, observa el complemento del valor que ya est\u00e1 en la columna: el complemento indica cu\u00e1ntos cuadrados faltan para llegar a diez. Si el d\u00edgito inferior es al menos el complemento, la suma llega a diez y se acarrea 1 al siguiente orden; los cuadrados que quedan fuera del grupo de diez son el d\u00edgito que queda en la columna.",
    finishBody = "Al terminar el c\u00e1lculo, podr\u00e1s pasar al siguiente problema haciendo clic en el bot\u00f3n de abajo.",
    start = "Empezar",
    title = "Practicando Suma con Valores Escritos",
    orderUnits = "Unidades",
    orderTens = "Decenas",
    orderHundreds = "Centenas",
    questionTemplate = "La columna de las %s ya tiene %d. Suma el d\u00edgito inferior %d: escribe en la fila del resultado el d\u00edgito que queda en la columna y, si la suma llega a diez, escribe 1 en la casilla del acarreo sobre la siguiente orden.",
    incomingNote = "El 1 acarreado de la columna anterior se suma: %d + 1 = %d.",
    msgCarry = "%1\$d + %2\$d = %3\$d. Alcanza 10, as\u00ed que la columna de las %5\$s conserva %4\$d y acarrea 1 al siguiente orden.",
    msgNoCarry = "%1\$d + %2\$d = %3\$d. No llega a 10, as\u00ed que la columna de las %5\$s conserva %4\$d.",
    wrongDigit = "Ese d\u00edgito no es correcto. Recuerda el complemento de %1\$d para diez: %2\$d. \u00dasalo para encontrar el d\u00edgito que queda en la columna de las %3\$s.",
    wrongCarry = "El acarreo no es correcto. Comprueba %1\$d + %2\$d: cuando la suma llega a diez, el acarreo debe ser 1.",
    congrats = "\u2705 \u00a1Correcto! Haz clic en 'Siguiente columna' para continuar.",
    perfect = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1PERFECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\n\u00a1Buen trabajo!",
    nextColumn = "Siguiente columna",
    newProblem = "Nuevo problema",
    squaresLabel = "Suma en esta columna",
    squaresUsed = "Cuadrados usados: %d.",
    complementPending = "Complemento de %1\$d para diez: %2\$d. A\u00fan faltan %3\$d cuadrados para completar el diez.",
    complementUsed = "Complemento de %1\$d para diez: %2\$d. Cuadrados que completaron el diez: %2\$d; cuadrados fuera del diez: %3\$d.",
    digitLabel = "D\u00edgito que queda en la columna",
    carryLabel = "Acarreo al siguiente orden",
)

val LocalPracticingAdditionTypedScreenStrings = staticCompositionLocalOf { EnPracticingAdditionTypedScreenStrings }

fun practicingAdditionTypedScreenStringsForLanguage(language: String): PracticingAdditionTypedScreenStrings = when (language) {
    "pt-BR" -> PtPracticingAdditionTypedScreenStrings
    "es-ES" -> EsPracticingAdditionTypedScreenStrings
    else -> EnPracticingAdditionTypedScreenStrings
}
