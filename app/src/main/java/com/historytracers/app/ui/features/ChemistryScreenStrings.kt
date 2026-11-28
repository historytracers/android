// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class ChemistryScreenStrings(
    val title: String,
    val theMatterDecoupling: String,
    val comingSoon: String,
)

val EnChemistryScreenStrings = ChemistryScreenStrings(
    title = "Matter and Us",
    theMatterDecoupling = "The Matter Decoupling",
    comingSoon = "New classes are coming soon.",
)

val PtChemistryScreenStrings = ChemistryScreenStrings(
    title = "A matéria e nós",
    theMatterDecoupling = "O desacoplamento da matéria",
    comingSoon = "Novas aulas estão chegando em breve.",
)

val EsChemistryScreenStrings = ChemistryScreenStrings(
    title = "La materia y nosotros",
    theMatterDecoupling = "El desacoplamiento de la materia",
    comingSoon = "Pronto habrá nuevas clases.",
)

val LocalChemistryScreenStrings = staticCompositionLocalOf { EnChemistryScreenStrings }

fun chemistryScreenStringsForLanguage(language: String): ChemistryScreenStrings = when (language) {
    "pt-BR" -> PtChemistryScreenStrings
    "es-ES" -> EsChemistryScreenStrings
    else -> EnChemistryScreenStrings
}
