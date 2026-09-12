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
    "https://www.historytracers.org/images/DonsMaps/img_6652habilissm.jpg"
        to "DonsMaps/img_6652habilissm.jpg",
    "https://www.historytracers.org/images/ResearchGate/Figura-9-Hueso-de-Lebombo.png"
        to "ResearchGate/Figura-9-Hueso-de-Lebombo.png"
)

fun resolveImageSource(url: String): Any =
    LOCAL_ASSET_IMAGES[url]?.let { ANDROID_ASSET_PREFIX + it } ?: url
