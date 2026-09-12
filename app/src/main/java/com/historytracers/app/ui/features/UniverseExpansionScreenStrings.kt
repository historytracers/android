// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class UniverseExpansionScreenStrings(
    val title: String,
    val caption: String,
    val imageSource: String,
    val stepCounter: String,
    val eraTitles: List<String>,
    val eraTexts: List<String>,
)

val EnUniverseExpansionScreenStrings = UniverseExpansionScreenStrings(
    title = "Everything Was Together",
    caption = "Figure 2: Planck history of the Universe. ESA \u2014 C. Carreau.",
    imageSource = "ESA \u2014 Planck history of the Universe",
    stepCounter = "Step %d of %d",
    eraTitles = listOf(
        "13.82 Billion Years \u2014 The Universe Today",
        "1 to 13.82 Billion Years \u2014 Galaxy Evolution",
        "200 Million to 1 Billion Years \u2014 The First Stars and Galaxies",
        "380,000 Years to 200 Million Years \u2014 Recombination, the CMB and the Dark Ages",
        "100 Seconds to 380,000 Years \u2014 Matter and Light Are Coupled",
        "1 to 100 Seconds \u2014 Particles Form",
        "0 to 1 Second \u2014 Big Bang and Cosmic Inflation",
    ),
    eraTexts = listOf(
        "This is the universe as we see it today: clusters of galaxies, our own galaxy the Milky Way, and our solar system around an ordinary star.",
        "Galaxies grow and merge into larger systems. Along the filaments of dark matter, clusters of galaxies and superclusters form the cosmic web.",
        "The first stars ignite, ending the dark ages. Their light ionizes the surrounding gas, and the first galaxies begin to take shape.",
        "The universe cools enough for electrons and nuclei to join into atoms: matter decouples from light and the Cosmic Microwave Background is released \u2014 the oldest light we can still observe. With no free electrons the universe becomes transparent and dark, and matter falls into the structures formed by dark matter, slowly building the scaffolding of the galaxies to come.",
        "Ordinary matter particles are coupled to light, forming a hot, opaque plasma. Dark matter particles begin to build structures, gently pulling matter together through gravity.",
        "As the universe cools, energy turns into the first particles: protons, neutrons and electrons, together with their antiparticles. Matter and antimatter mostly annihilate, leaving a small surplus of matter that will build everything we see.",
        "Everything that exists begins in an extremely hot and dense point. In a fraction of a second, cosmic inflation stretches space faster than light, smoothing it out and creating the tiny seeds of all the structures to come.",
    ),
)

val PtUniverseExpansionScreenStrings = UniverseExpansionScreenStrings(
    title = "Tudo Estava Junto",
    caption = "Figura 2: Hist\u00f3ria do Universo segundo Planck. ESA \u2014 C. Carreau.",
    imageSource = "ESA \u2014 Hist\u00f3ria do Universo segundo Planck",
    stepCounter = "Passo %d de %d",
    eraTitles = listOf(
        "13,82 Bilh\u00f5es de Anos \u2014 O Universo Hoje",
        "1 a 13,82 Bilh\u00f5es de Anos \u2014 Evolu\u00e7\u00e3o das Gal\u00e1xias",
        "200 Milh\u00f5es a 1 Bilh\u00e3o de Anos \u2014 As Primeiras Estrelas e Gal\u00e1xias",
        "380 000 Anos a 200 Milh\u00f5es de Anos \u2014 Recombina\u00e7\u00e3o, a CMB e as Idades das Trevas",
        "100 Segundos a 380 000 Anos \u2014 Mat\u00e9ria e Luz Acopladas",
        "1 a 100 Segundos \u2014 Forma\u00e7\u00e3o das Part\u00edculas",
        "0 a 1 Segundo \u2014 Big Bang e Infla\u00e7\u00e3o C\u00f3smica",
    ),
    eraTexts = listOf(
        "Este \u00e9 o universo como o vemos hoje: aglomerados de gal\u00e1xias, a nossa pr\u00f3pria gal\u00e1xia, a Via L\u00e1ctea, e o nosso sistema solar em torno de uma estrela comum.",
        "As gal\u00e1xias crescem e fundem-se em sistemas maiores. Ao longo dos filamentos de mat\u00e9ria escura, formam-se aglomerados e superaglomerados de gal\u00e1xias: a teia c\u00f3smica.",
        "As primeiras estrelas acendem, pondo fim \u00e0s idades das trevas. A sua luz ioniza o g\u00e1s ao redor, e as primeiras gal\u00e1xias come\u00e7am a formar-se.",
        "O universo esfria o suficiente para que el\u00e9trons e n\u00facleos se unam em \u00e1tomos: a mat\u00e9ria desacopla-se da luz e a Radia\u00e7\u00e3o C\u00f3smica de Fundo em Micro-ondas (CMB) \u00e9 liberada \u2014 a luz mais antiga que ainda podemos observar. Sem el\u00e9trons livres, o universo torna-se transparente e escuro, e a mat\u00e9ria cai nas estruturas formadas pela mat\u00e9ria escura, construindo lentamente o esqueleto das futuras gal\u00e1xias.",
        "As part\u00edculas de mat\u00e9ria comum est\u00e3o acopladas \u00e0 luz, formando um plasma quente e opaco. As part\u00edculas de mat\u00e9ria escura come\u00e7am a construir estruturas, aproximando a mat\u00e9ria pela gravidade.",
        "\u00c0 medida que o universo esfria, a energia transforma-se nas primeiras part\u00edculas: pr\u00f3tons, n\u00eautrons e el\u00e9trons, al\u00e9m das suas antipart\u00edculas. Mat\u00e9ria e antimat\u00e9ria aniquilam-se em grande parte, deixando um pequeno excedente de mat\u00e9ria que construir\u00e1 tudo o que vemos.",
        "Tudo o que existe come\u00e7a num ponto extremamente quente e denso. Numa fra\u00e7\u00e3o de segundo, a infla\u00e7\u00e3o c\u00f3smica estica o espa\u00e7o mais r\u00e1pido que a luz, alisando-o e criando as pequenas sementes de todas as estruturas futuras.",
    ),
)

val EsUniverseExpansionScreenStrings = UniverseExpansionScreenStrings(
    title = "Todo Estaba Junto",
    caption = "Figura 2: Historia del Universo seg\u00fan Planck. ESA \u2014 C. Carreau.",
    imageSource = "ESA \u2014 Historia del Universo seg\u00fan Planck",
    stepCounter = "Paso %d de %d",
    eraTitles = listOf(
        "13,82 Mil Millones de A\u00f1os \u2014 El Universo Hoy",
        "1 a 13,82 Mil Millones de A\u00f1os \u2014 Evoluci\u00f3n de las Galaxias",
        "200 Millones a 1 000 Millones de A\u00f1os \u2014 Las Primeras Estrellas y Galaxias",
        "380 000 A\u00f1os a 200 Millones de A\u00f1os \u2014 Recombinaci\u00f3n, la CMB y las Edades Oscuras",
        "100 Segundos a 380 000 A\u00f1os \u2014 Materia y Luz Acopladas",
        "1 a 100 Segundos \u2014 Se Forman las Part\u00edculas",
        "0 a 1 Segundo \u2014 Big Bang e Inflaci\u00f3n C\u00f3smica",
    ),
    eraTexts = listOf(
        "Este es el universo tal como lo vemos hoy: c\u00famulos de galaxias, nuestra propia galaxia, la V\u00eda L\u00e1ctea, y nuestro sistema solar alrededor de una estrella com\u00fan.",
        "Las galaxias crecen y se fusionan en sistemas mayores. A lo largo de los filamentos de materia oscura, se forman c\u00famulos y superc\u00famulos de galaxias: la red c\u00f3smica.",
        "Las primeras estrellas se encienden, poniendo fin a las edades oscuras. Su luz ioniza el gas circundante y las primeras galaxias empiezan a formarse.",
        "El universo se enfr\u00eda lo suficiente para que electrones y n\u00facleos se unan en \u00e1tomos: la materia se desacopla de la luz y se libera la Radiaci\u00f3n C\u00f3smica de Fondo de Microondas (CMB) \u2014 la luz m\u00e1s antigua que a\u00fan podemos observar. Sin electrones libres, el universo se vuelve transparente y oscuro, y la materia cae en las estructuras formadas por la materia oscura, construyendo lentamente el andamiaje de las futuras galaxias.",
        "Las part\u00edculas de materia com\u00fan est\u00e1n acopladas a la luz, formando un plasma caliente y opaco. Las part\u00edculas de materia oscura empiezan a construir estructuras, atrayendo la materia por la gravedad.",
        "A medida que el universo se enfr\u00eda, la energ\u00eda se transforma en las primeras part\u00edculas: protones, neutrones y electrones, junto con sus antipart\u00edculas. Materia y antimateria se aniquilan en su mayor\u00eda, dejando un peque\u00f1o excedente de materia que construir\u00e1 todo lo que vemos.",
        "Todo lo que existe comienza en un punto extremadamente caliente y denso. En una fracci\u00f3n de segundo, la inflaci\u00f3n c\u00f3smica estira el espacio m\u00e1s r\u00e1pido que la luz, alis\u00e1ndolo y creando las peque\u00f1as semillas de todas las estructuras futuras.",
    ),
)

val LocalUniverseExpansionScreenStrings = staticCompositionLocalOf { EnUniverseExpansionScreenStrings }

fun universeExpansionScreenStringsForLanguage(language: String): UniverseExpansionScreenStrings = when (language) {
    "pt-BR" -> PtUniverseExpansionScreenStrings
    "es-ES" -> EsUniverseExpansionScreenStrings
    else -> EnUniverseExpansionScreenStrings
}
