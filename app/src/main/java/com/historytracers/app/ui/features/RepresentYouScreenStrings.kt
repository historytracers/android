// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class RepresentYouScreenStrings(
    val title: String,
    val instruction: String,
    val roman: String,
    val hinduArabic: String,
    val levelComplete: String,
    val finalCongrats: String,
    val playAgain: String,
    val wrongPair: String,
    val wrongPairLeft: String,
    val and: String,
    val before: String,
    val withBar: String,
    val countTwo: String,
    val countThree: String,
    val placeUnits: String,
    val placeTens: String,
    val placeHundreds: String,
    val group: String,
    val groupMore: String,
)

val EnRepresentYouScreenStrings = RepresentYouScreenStrings(
    title = "I Represent You",
    instruction = "Match each Roman numeral on the left with its Hindu-Arabic number on the right. Select one from each column to pair them.",
    roman = "Roman",
    hinduArabic = "Hindu-Arabic",
    levelComplete = "Congratulations, you finished level %d/%d!",
    finalCongrats = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou matched all the numbers!\nGreat job!",
    playAgain = "Play Again",
    wrongPair = "%VALUE% is represented by %EXPLANATION%: %ROMAN%.",
    wrongPairLeft = "%ROMAN% is represented by %VALUE%.",
    and = "and",
    before = "before",
    withBar = "with a bar above",
    countTwo = "two",
    countThree = "three",
    placeUnits = "units",
    placeTens = "tens",
    placeHundreds = "hundreds",
    group = "%COUNT% %PLACE%",
    groupMore = "%COUNT% more %PLACE%",
)

val PtRepresentYouScreenStrings = RepresentYouScreenStrings(
    title = "Eu Represento Voc\u00ea",
    instruction = "Ligue cada n\u00famero romano \u00e0 esquerda ao seu n\u00famero indo-\u00e1rabe \u00e0 direita. Selecione um de cada coluna para formar o par.",
    roman = "Romano",
    hinduArabic = "Indo-\u00e1rabe",
    levelComplete = "Parab\u00e9ns, voc\u00ea finalizou o n\u00edvel %d/%d!",
    finalCongrats = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFEITO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea ligou todos os n\u00fameros!\n\u00d3timo trabalho!",
    playAgain = "Jogar Novamente",
    wrongPair = "%VALUE% \u00e9 representado por %EXPLANATION%: %ROMAN%.",
    wrongPairLeft = "%ROMAN% \u00e9 representado por %VALUE%.",
    and = "e",
    before = "antes de",
    withBar = "com uma barra em cima",
    countTwo = "duas",
    countThree = "tr\u00eas",
    placeUnits = "unidades",
    placeTens = "dezenas",
    placeHundreds = "centenas",
    group = "%COUNT% %PLACE%",
    groupMore = "mais %COUNT% %PLACE%",
)

val EsRepresentYouScreenStrings = RepresentYouScreenStrings(
    title = "Yo Te Represento",
    instruction = "Empareja cada n\u00famero romano de la izquierda con su n\u00famero indo\u00e1rabe de la derecha. Selecciona uno de cada columna para formar la pareja.",
    roman = "Romano",
    hinduArabic = "Indo\u00e1rabe",
    levelComplete = "\u00a1Felicitaciones, terminaste el nivel %d/%d!",
    finalCongrats = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1PERFECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n\u00a1Emparejaste todos los n\u00fameros!\n\u00a1Gran trabajo!",
    playAgain = "Jugar de Nuevo",
    wrongPair = "%VALUE% se representa con %EXPLANATION%: %ROMAN%.",
    wrongPairLeft = "%ROMAN% se representa con %VALUE%.",
    and = "y",
    before = "antes de",
    withBar = "con una barra encima",
    countTwo = "dos",
    countThree = "tres",
    placeUnits = "unidades",
    placeTens = "decenas",
    placeHundreds = "centenas",
    group = "%COUNT% %PLACE%",
    groupMore = "%COUNT% %PLACE% m\u00e1s",
)

val LocalRepresentYouScreenStrings = staticCompositionLocalOf { EnRepresentYouScreenStrings }

fun representYouScreenStringsForLanguage(language: String): RepresentYouScreenStrings = when (language) {
    "pt-BR" -> PtRepresentYouScreenStrings
    "es-ES" -> EsRepresentYouScreenStrings
    else -> EnRepresentYouScreenStrings
}
