// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

private const val ANDROID_ASSET_PREFIX = "file:///android_asset/"

private val LOCAL_ASSET_IMAGES = mapOf(
    "https://www.historytracers.org/images/ESA/Planck_history_of_Universe.jpg"
        to "ESA/planck_history_of_universe.jpg",
    "https://www.historytracers.org/images/ESA/Planck_s_view_of_the_cosmic_microwave_background.jpg"
        to "ESA/Planck_s_view_of_the_cosmic_microwave_background.jpg",
    "https://www.historytracers.org/images/MexicoCityMuseo/HomoSapiens.jpg"
        to "MexicoCityMuseo/HomoSapiens.jpg",
    "https://www.historytracers.org/images/MexicoCityMuseo/Oaxaca.jpg"
        to "MexicoCityMuseo/Oaxaca.jpg",
    "https://www.historytracers.org/images/DonsMaps/img_6652habilissm.jpg"
        to "DonsMaps/img_6652habilissm.jpg",
    "https://www.historytracers.org/images/ResearchGate/Figura-9-Hueso-de-Lebombo.png"
        to "ResearchGate/Figura-9-Hueso-de-Lebombo.png",
    "https://www.historytracers.org/images/TaiChimpanzeeProject/c4b711_0ebd581742a8483e90a28c521cadd3cb~mv2.jpeg"
        to "TaiChimpanzeeProject/c4b711_0ebd581742a8483e90a28c521cadd3cb~mv2.jpeg",
    "https://www.historytracers.org/images/Mapswire/mapswire-continent_na-printable-map-north-america-robinson-269_mesoamerica2.jpg"
        to "Mapswire/mapswire-continent_na-printable-map-north-america-robinson-269_mesoamerica2.jpg",
    "https://www.historytracers.org/images/Copan/CopanAltarGenealogy0.jpg"
        to "Copan/CopanAltarGenealogy0.jpg",
    "https://www.historytracers.org/images/Copan/CopanAltarGenealogy2.jpg"
        to "Copan/CopanAltarGenealogy2.jpg",
    "https://www.historytracers.org/images/CahalPech/ChocolatPot.jpg"
        to "CahalPech/ChocolatPot.jpg"
)

fun resolveImageSource(url: String): Any =
    LOCAL_ASSET_IMAGES[url]?.let { ANDROID_ASSET_PREFIX + it } ?: url
