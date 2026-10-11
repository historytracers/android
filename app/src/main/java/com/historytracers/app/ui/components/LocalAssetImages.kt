// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

private const val ANDROID_ASSET_PREFIX = "file:///android_asset/"

private val LOCAL_ASSET_IMAGES = mapOf(
    "https://www.historytracers.org/images/Caral/QuipuPanel.png"
        to "Caral/QuipuPanel.png",
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
    "https://www.historytracers.org/images/Copan/CopanWholeTextStelaAltar.png"
        to "Copan/CopanWholeTextStelaAltar.png",
    "https://www.historytracers.org/images/CahalPech/ChocolatPot.jpg"
        to "CahalPech/ChocolatPot.jpg",
    "https://www.historytracers.org/images/HistoryTracers/HTHumanBody.jpg"
        to "HistoryTracers/HTHumanBody.jpg",
    "https://www.historytracers.org/images/Ashmolean/KingList.jpg"
        to "Ashmolean/KingList.jpg",
    "https://www.historytracers.org/images/DonsMaps/1594.jpg"
        to "DonsMaps/1594.jpg",
    "https://www.historytracers.org/images/SanJoseCRMuseo/SanJoseCRAntropologia.jpg"
        to "SanJoseCRMuseo/SanJoseCRAntropologia.jpg",
    "https://www.historytracers.org/images/HistoryTracers/pentagonal_pyramid.jpg"
        to "HistoryTracers/pentagonal_pyramid.jpg",
    "https://www.historytracers.org/images/Xunantunich/WitzXunantunich.jpg"
        to "Xunantunich/WitzXunantunich.jpg",
    "https://historytracers.org/images/Copan/Temple16Copan.png"
        to "Copan/Temple16Copan.png",
    "https://www.historytracers.org/images/Mapswire/mapswire-continent_sa-printable-map-south-america-lambert-az-hemi-271_Tawantsuyu.jpg"
        to "Mapswire/mapswire-continent_sa-printable-map-south-america-lambert-az-hemi-271_Tawantsuyu.jpg",
    "https://www.historytracers.org/images/Mapswire/mapswire-world-political-white-equal_earth_journey.png"
        to "Mapswire/mapswire-world-political-white-equal_earth_journey.png",
    "https://www.historytracers.org/images/Mapswire/continent_af-where-is-africa.png"
        to "Mapswire/continent_af-where-is-africa.png",
    "https://www.historytracers.org/images/Mapswire/continent_sa-where-is-south-america.png"
        to "Mapswire/continent_sa-where-is-south-america.png",
    "https://www.historytracers.org/images/Mapswire/continent_eu-where-is-europe.png"
        to "Mapswire/continent_eu-where-is-europe.png",
    "https://www.historytracers.org/images/Mapswire/continent_as-where-is-asia.png"
        to "Mapswire/continent_as-where-is-asia.png",
    // Same bone photo referenced from different paths/hosts in the content.
    "https://www.historytracers.org/images/GonzalesRedondo/Figura-9-Hueso-de-Lebombo.png"
        to "ResearchGate/Figura-9-Hueso-de-Lebombo.png",
    "https://historytracers.org/images/GonzalesRedondo/Figura-9-Hueso-de-Lebombo.png"
        to "ResearchGate/Figura-9-Hueso-de-Lebombo.png",
    "https://www.historytracers.org/images/ESA/Zoom_into_Gaia_s_star-formation_map_highlighted.jpg"
        to "ESA/Zoom_into_Gaia_s_star-formation_map_highlighted.jpg",
    "https://www.historytracers.org/images/CERN/2ALICEimages.jpg"
        to "CERN/2ALICEimages.jpg",
    "https://www.historytracers.org/images/HistoryTracers/Maya_0.png"
        to "HistoryTracers/Maya_0.png"
)

/** Returns the local asset URI for [url] when it maps to a bundled image, else null. */
fun localAssetUriFor(url: String): String? {
    val trimmed = url.trim()
    if (trimmed.isEmpty()) return null
    if (trimmed.startsWith(ANDROID_ASSET_PREFIX)) return trimmed
    return LOCAL_ASSET_IMAGES[trimmed]?.let { ANDROID_ASSET_PREFIX + it }
}

private val IMG_SRC_REGEX = Regex("""(src\s*=\s*["'])([^"']+)(["'])""")

/** Rewrites every `src="..."` that maps to a bundled image to its local asset URI. */
fun rewriteImageSourcesToLocal(html: String): String {
    if (!html.contains("src=")) return html
    return IMG_SRC_REGEX.replace(html) { match ->
        val local = localAssetUriFor(match.groupValues[2])
        if (local != null) match.groupValues[1] + local + match.groupValues[3] else match.value
    }
}

fun resolveImageSource(url: String): Any? {
    localAssetUriFor(url)?.let { return it }
    val trimmed = url.trim()
    // Strict: only https is ever fetched. Anything else (http, javascript:,
    // data:, file:, ...) is rejected; callers render the offline placeholder.
    if (!isSafeExternalUrl(trimmed)) return null
    return trimmed
}
