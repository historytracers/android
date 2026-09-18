// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class QuipuOnTheYupanaScreenStrings(
    val title: String,
    val heading: String,
    val intro: String,
    val instructions: String,
    val levelLabel: String,
    val newNumber: String,
    val nextLevel: String,
    val orderUnits: String,
    val orderTens: String,
    val orderHundreds: String,
    val orderThousands: String,
    val msgStart: String,
    val msgReading: String,
    val msgLevelComplete: String,
    val msgAllLevels: String,
    val msgOverflow: String,
)

val EnQuipuOnTheYupanaScreenStrings = QuipuOnTheYupanaScreenStrings(
    title = "Quipu on the Yupana",
    heading = "Read and Set the Number",
    intro = "In this game, you will do what the ancient Inca counters did: read the value recorded on the quipu and set that same value on the Yupana. This way, you practice reading a number in one system and writing it in another.",
    instructions = "Look at the highlighted cord and count its knots: that is the number to read. Use the bottom row of the Yupana for the units, the next row for the tens, and the row above it for the hundreds. Tap a square to add a mark; tap it again to remove it. A row with no marks means zero. When the number on the Yupana matches the number on the cord, the game marks the cord as done.",
    levelLabel = "Level",
    newNumber = "New number",
    nextLevel = "Next level",
    orderUnits = "Units",
    orderTens = "Tens",
    orderHundreds = "Hundreds",
    orderThousands = "Thousands",
    msgStart = "Read the highlighted cord and set its number on the Yupana.",
    msgReading = "Keep going: set the number shown on the highlighted cord.",
    msgLevelComplete = "\uD83C\uDF89 Level complete! Tap Next level to continue or New number to redo this level.",
    msgAllLevels = "\uD83C\uDF89\uD83C\uDFC6 All levels complete! \uD83C\uDFC6\uD83C\uDF89 You read every quipu! Tap Next level to start again at level 1 or New number to redo this level.",
    msgOverflow = "\u26A0\uFE0F Overflow! The %s row cannot hold more than 9. Remove some marks and try again.",
)

val PtQuipuOnTheYupanaScreenStrings = QuipuOnTheYupanaScreenStrings(
    title = "Quipu na Yupana",
    heading = "Leia e Represente o N\u00famero",
    intro = "Neste jogo, voc\u00ea far\u00e1 como os antigos contadores incas: ler\u00e1 o valor registrado no quipu e o representar\u00e1 na Yupana. Assim, voc\u00ea pratica a leitura de um n\u00famero em um sistema e a escrita em outro.",
    instructions = "Observe a corda destacada e conte seus n\u00f3s: esse \u00e9 o n\u00famero que voc\u00ea deve ler. Use a fileira de baixo da Yupana para as unidades, a seguinte para as dezenas, e a de cima para as centenas. Toque em um quadrado para adicionar uma marca; toque novamente para remov\u00ea-la. Uma fileira sem marcas significa zero. Quando o n\u00famero da Yupana coincidir com o da corda, o jogo marcar\u00e1 a corda como conclu\u00edda.",
    levelLabel = "N\u00edvel",
    newNumber = "Novo n\u00famero",
    nextLevel = "Pr\u00f3ximo n\u00edvel",
    orderUnits = "Unidades",
    orderTens = "Dezenas",
    orderHundreds = "Centenas",
    orderThousands = "Milhares",
    msgStart = "Leia a corda destacada e represente o n\u00famero dela na Yupana.",
    msgReading = "Continue: represente o n\u00famero que a corda destacada mostra.",
    msgLevelComplete = "\uD83C\uDF89 N\u00edvel conclu\u00eddo! Toque em Pr\u00f3ximo n\u00edvel para continuar ou em Novo n\u00famero para refazer este n\u00edvel.",
    msgAllLevels = "\uD83C\uDF89\uD83C\uDFC6 Todos os n\u00edveis conclu\u00eddos! \uD83C\uDFC6\uD83C\uDF89 Voc\u00ea leu todos os quipus! Toque em Pr\u00f3ximo n\u00edvel para come\u00e7ar de novo no n\u00edvel 1 ou em Novo n\u00famero para refazer este n\u00edvel.",
    msgOverflow = "\u26A0\uFE0F Transbordo! A fileira de %s n\u00e3o pode ter mais de 9. Remova algumas marcas e tente novamente.",
)

val EsQuipuOnTheYupanaScreenStrings = QuipuOnTheYupanaScreenStrings(
    title = "Quipu en la Yupana",
    heading = "Lee y Representa el N\u00famero",
    intro = "En este juego, har\u00e1s como los antiguos contadores incas: leer\u00e1s el valor registrado en el quipu y lo representar\u00e1s en la Yupana. As\u00ed, practicas la lectura de un n\u00famero en un sistema y su escritura en otro.",
    instructions = "Observa la cuerda resaltada y cuenta sus nudos: ese es el n\u00famero que debes leer. Usa la fila de abajo de la Yupana para las unidades, la siguiente para las decenas, y la de arriba para las centenas. Toca un cuadrado para a\u00f1adir una marca; t\u00f3calo de nuevo para quitarla. Una fila sin marcas significa cero. Cuando el n\u00famero de la Yupana coincida con el de la cuerda, el juego marcar\u00e1 la cuerda como completada.",
    levelLabel = "Nivel",
    newNumber = "Nuevo n\u00famero",
    nextLevel = "Siguiente nivel",
    orderUnits = "Unidades",
    orderTens = "Decenas",
    orderHundreds = "Centenas",
    orderThousands = "Millares",
    msgStart = "Lee la cuerda resaltada y representa su n\u00famero en la Yupana.",
    msgReading = "Contin\u00faa: representa el n\u00famero que muestra la cuerda resaltada.",
    msgLevelComplete = "\uD83C\uDF89 \u00a1Nivel completado! Toca Siguiente nivel para continuar o Nuevo n\u00famero para repetir este nivel.",
    msgAllLevels = "\uD83C\uDF89\uD83C\uDFC6 \u00a1Todos los niveles completados! \uD83C\uDFC6\uD83C\uDF89 \u00a1Le\u00edste todos los quipus! Toca Siguiente nivel para empezar de nuevo en el nivel 1 o Nuevo n\u00famero para repetir este nivel.",
    msgOverflow = "\u26A0\uFE0F \u00a1Desborde! La fila de %s no puede tener m\u00e1s de 9. Quita algunas marcas e int\u00e9ntalo de nuevo.",
)

val LocalQuipuOnTheYupanaScreenStrings = staticCompositionLocalOf { EnQuipuOnTheYupanaScreenStrings }

fun quipuOnTheYupanaScreenStringsForLanguage(language: String): QuipuOnTheYupanaScreenStrings = when (language) {
    "pt-BR" -> PtQuipuOnTheYupanaScreenStrings
    "es-ES" -> EsQuipuOnTheYupanaScreenStrings
    else -> EnQuipuOnTheYupanaScreenStrings
}
