// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class CountingWithBonesScreenStrings(
    val title: String,
    val whyTitle: String,
    val boneFigureCaption: String,
    val boneLookText: String,
    val gameRulesTitle: String,
    val gameRules: String,
    val prompt: String,
    val handsLabel: String,
    val levelLeft: String,
    val levelLeftRight: String,
    val levelFriend: String,
    val levelTwoPeople: String,
    val addMark: String,
    val removeMark: String,
    val newRound: String,
    val congrats: String,
    val allLevelsCongrats: String,
    val practiceTitle: String,
    val practiceText: String,
    val raisedHandDesc: String,
    val boneDesc: String,
)

val EnCountingWithBonesScreenStrings = CountingWithBonesScreenStrings(
    title = "Counting with Bones",
    whyTitle = "Counting Like Ancient Humans",
    boneFigureCaption = "Figure 1: A bone with marks. The marks look like our fingers.",
    boneLookText = "Look at the marks on the bone. They look like our fingers. In this game, we do the opposite: we look at hands with raised fingers and we place marks on a bone until the marks match the raised fingers.",
    gameRulesTitle = "Objective and Rules",
    gameRules = "Look at the raised fingers. Then use the arrows beside the bone to place marks. The up arrow adds a mark, and the down arrow removes a mark. The game stops as soon as the bone shows the same quantity of marks as raised fingers. After you complete a level, a button appears to continue to the next set of hands. To practice the same level again, start a new round.",
    prompt = "Count the raised fingers, then place the same quantity of marks on the bone.",
    handsLabel = "Hands:",
    levelLeft = "Left hand",
    levelLeftRight = "Left and right hands",
    levelFriend = "Both hands and a friend's left hand",
    levelTwoPeople = "Both people's hands",
    addMark = "Add one mark",
    removeMark = "Remove one mark",
    newRound = "New Round",
    congrats = "The marks on the bone match the raised fingers!",
    allLevelsCongrats = "You completed all 4 levels!",
    practiceTitle = "What Are We Practicing?",
    practiceText = "With this game, we practice turning a quantity into a sequence of marks, like some ancient peoples did on bones. We also practice counting with the fingers of our hands and, when the quantity grows, with the hands of another person.",
    raisedHandDesc = "Raised fingers: %d",
    boneDesc = "Marks: %d",
)

val PtCountingWithBonesScreenStrings = CountingWithBonesScreenStrings(
    title = "Contando com Ossos",
    whyTitle = "Contando Como os Humanos Antigos",
    boneFigureCaption = "Figura 1: Um osso com marcas. As marcas parecem nossos dedos.",
    boneLookText = "Observe as marcas no osso. Elas parecem nossos dedos. Neste jogo, fazemos o contr\u00e1rio: olhamos m\u00e3os com dedos levantados e colocamos marcas em um osso at\u00e9 que elas coincidam com os dedos levantados.",
    gameRulesTitle = "Objetivo e Regras",
    gameRules = "Observe os dedos levantados. Depois, use as setas ao lado do osso para colocar marcas. A seta para cima adiciona uma marca, e a seta para baixo remove uma marca. O jogo para assim que o osso mostrar a mesma quantidade de marcas que a de dedos levantados. Ao completar um n\u00edvel, aparece um bot\u00e3o para continuar para o pr\u00f3ximo conjunto de m\u00e3os. Para praticar o mesmo n\u00edvel de novo, comece uma nova rodada.",
    prompt = "Conte os dedos levantados e coloque no osso a mesma quantidade de marcas.",
    handsLabel = "M\u00e3os:",
    levelLeft = "M\u00e3o esquerda",
    levelLeftRight = "M\u00e3os esquerda e direita",
    levelFriend = "As duas m\u00e3os e a m\u00e3o esquerda de um amigo",
    levelTwoPeople = "As m\u00e3os das duas pessoas",
    addMark = "Adicionar uma marca",
    removeMark = "Remover uma marca",
    newRound = "Nova Rodada",
    congrats = "As marcas no osso coincidem com os dedos levantados!",
    allLevelsCongrats = "Voc\u00ea completou todos os 4 n\u00edveis!",
    practiceTitle = "O Que Praticamos?",
    practiceText = "Com este jogo, praticamos transformar uma quantidade em uma sequ\u00eancia de marcas, como alguns povos antigos faziam nos ossos. Tamb\u00e9m praticamos contar com os dedos das nossas m\u00e3os e, quando a quantidade cresce, com as m\u00e3os de outra pessoa.",
    raisedHandDesc = "Dedos levantados: %d",
    boneDesc = "Marcas: %d",
)

val EsCountingWithBonesScreenStrings = CountingWithBonesScreenStrings(
    title = "Contando con Huesos",
    whyTitle = "Contando Como los Humanos Antiguos",
    boneFigureCaption = "Figura 1: Un hueso con marcas. Las marcas se parecen a nuestros dedos.",
    boneLookText = "Mira las marcas del hueso. Se parecen a nuestros dedos. En este juego hacemos lo contrario: miramos manos con dedos levantados y colocamos marcas en un hueso hasta que coinciden con los dedos levantados.",
    gameRulesTitle = "Objetivo y Reglas",
    gameRules = "Observa los dedos levantados. Despu\u00e9s usa las flechas al lado del hueso para colocar marcas. La flecha hacia arriba a\u00f1ade una marca, y la flecha hacia abajo quita una marca. El juego se detiene en cuanto el hueso muestra la misma cantidad de marcas que dedos levantados. Al completar un nivel, aparece un bot\u00f3n para continuar con el siguiente conjunto de manos. Para practicar el mismo nivel otra vez, comienza una nueva ronda.",
    prompt = "Cuenta los dedos levantados y coloca en el hueso la misma cantidad de marcas.",
    handsLabel = "Manos:",
    levelLeft = "Mano izquierda",
    levelLeftRight = "Manos izquierda y derecha",
    levelFriend = "Las dos manos y la mano izquierda de un amigo",
    levelTwoPeople = "Las manos de las dos personas",
    addMark = "A\u00f1adir una marca",
    removeMark = "Quitar una marca",
    newRound = "Nueva Ronda",
    congrats = "\u00a1Las marcas del hueso coinciden con los dedos levantados!",
    allLevelsCongrats = "\u00a1Completaste los 4 niveles!",
    practiceTitle = "\u00bfQu\u00e9 Practicamos?",
    practiceText = "Con este juego practicamos convertir una cantidad en una secuencia de marcas, como algunos pueblos antiguos hac\u00edan en los huesos. Tambi\u00e9n practicamos contar con los dedos de nuestras manos y, cuando la cantidad crece, con las manos de otra persona.",
    raisedHandDesc = "Dedos levantados: %d",
    boneDesc = "Marcas: %d",
)

val LocalCountingWithBonesScreenStrings = staticCompositionLocalOf { EnCountingWithBonesScreenStrings }

fun countingWithBonesScreenStringsForLanguage(language: String): CountingWithBonesScreenStrings = when (language) {
    "pt-BR" -> PtCountingWithBonesScreenStrings
    "es-ES" -> EsCountingWithBonesScreenStrings
    else -> EnCountingWithBonesScreenStrings
}
