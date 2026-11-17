// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.AutoSizeTitleText
import com.historytracers.app.ui.features.carryingInAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.countingWithBonesScreenStringsForLanguage
import com.historytracers.app.ui.features.expandingScreenStringsForLanguage
import com.historytracers.app.ui.features.latestAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.matchingTables24StringsForLanguage
import com.historytracers.app.ui.features.matchingTables248StringsForLanguage
import com.historytracers.app.ui.features.matchingTables39StringsForLanguage
import com.historytracers.app.ui.features.matterAndEnergyScreenStringsForLanguage
import com.historytracers.app.ui.features.myFirstCubeScreenStringsForLanguage
import com.historytracers.app.ui.features.runningAmongNumbersScreenStringsForLanguage
import com.historytracers.app.ui.features.sharedOriginScreenStringsForLanguage
import com.historytracers.app.ui.features.sharingWithWhomScreenStringsForLanguage
import com.historytracers.app.ui.features.theLastScreenStringsForLanguage
import com.historytracers.app.ui.features.universeExpansionScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.OnButtonYellow

private data class LatestAdditionEntry(
    val sectionId: String,
    val label: String,
    val icon: @Composable () -> Unit,
    val isCompleted: () -> Boolean,
    val onNavigate: () -> Unit
)

@Composable
fun LatestAdditionScreen(
    scrollState: ScrollState = rememberScrollState(),
    onNavigateBack: () -> Unit = {},
    onNavigateToWhatIsIt: () -> Unit = {},
    onNavigateToMultiplicationTableZero: () -> Unit = {},
    onNavigateToDivisionAndRemainder: () -> Unit = {},
    onNavigateToWritingTheQuotient: () -> Unit = {},
    onNavigateToPlayingWithAxioms: () -> Unit = {},
    onNavigateToCarryingInAdditionIntro: () -> Unit = {},
    onNavigateToLargeNumbersIntro: () -> Unit = {},
    onNavigateToQuipuOnTheYupana: () -> Unit = {},
    onNavigateToMesoamericanSymbols: () -> Unit = {},
    onNavigateToOvercomingLimits: () -> Unit = {},
    onNavigateToBuildingLikeAMesoamerican: () -> Unit = {},
    onNavigateToMesoamericanOrders: () -> Unit = {},
    onNavigateToRunningAmongNumbersIntro: () -> Unit = {},
    onNavigateToSharedOriginIntro: () -> Unit = {},
    onNavigateToMatterAndEnergyIntro: () -> Unit = {},
    onNavigateToCountingWithBonesIntro: () -> Unit = {},
    onNavigateToEverythingWasTogether: () -> Unit = {},
    onNavigateToSharingWithWhomIntro: () -> Unit = {},
    onNavigateToQuipusIntro: () -> Unit = {},
    onNavigateToPracticingWithQuipus: () -> Unit = {},
    onNavigateToEqualityIntro: () -> Unit = {},
    onNavigateToHistoricalEqualityIntro: () -> Unit = {},
    onNavigateToHistoricalEqualityPyramidsIntro: () -> Unit = {},
    onNavigateToTextOrNumber: () -> Unit = {},
    onNavigateToTheMissingNumbers: () -> Unit = {},
    onNavigateToIPreferThis: () -> Unit = {},
    onNavigateToBuildingLikeEtruscanRomans: () -> Unit = {},
    onNavigateToEtruscanRomanTens: () -> Unit = {},
    onNavigateToHundredsAndThousands: () -> Unit = {},
    onNavigateToCalculi: () -> Unit = {},
    onNavigateToAbacusHistory: () -> Unit = {},
    onNavigateToTheSameResult: () -> Unit = {},
    onNavigateToHalfPiece: () -> Unit = {},
    onNavigateToBiggerOrSmaller: () -> Unit = {},
    onNavigateToGreaterOrSmallerGame: () -> Unit = {},
    onNavigateToMeasuringOneDirection: () -> Unit = {},
    onNavigateToWalkingBackwards: () -> Unit = {},
    onNavigateToComplementAndSubtraction: () -> Unit = {},
    onNavigateToPracticingSubtraction: () -> Unit = {},
    onNavigateToPracticingSubtractionTyped: () -> Unit = {},
    onNavigateToPracticingSubtractionChosen: () -> Unit = {},
    onNavigateToSunTzuSubtraction: () -> Unit = {},
    onNavigateToAroundTheWorld: () -> Unit = {},
    onNavigateToAroundTheWorldAddition: () -> Unit = {},
    onNavigateToCarryOrNotCarry: () -> Unit = {},
    onNavigateToAdvancingAndComplementing: () -> Unit = {},
    onNavigateToOddOrEven: () -> Unit = {},
    onNavigateToExpanding: () -> Unit = {},
    onNavigateToGroupingThrees: () -> Unit = {},
    onNavigateToPracticingAdditionChosen: () -> Unit = {},
    onNavigateToSunTzuLearning: () -> Unit = {},
    onNavigateToWritingQuotientAndRemainder: () -> Unit = {},
    onNavigateToChoosingDividendAndDivisor: () -> Unit = {},
    onNavigateToMultiplicationDivisionRelationship: () -> Unit = {},
    onNavigateToDivisionRecipe: () -> Unit = {},
    onNavigateToExtendedDivisionRecipe: () -> Unit = {},
    onNavigateToMultiplicationTableEqualSides: () -> Unit = {},
    onNavigateToMultiplicationTableDoubleValues: () -> Unit = {},
    onNavigateToMultiplicationTableTripleValues: () -> Unit = {},
    onNavigateToMultiplicationTableFirstSquareNumber: () -> Unit = {},
    onNavigateToMultiplicationTableMyBody: () -> Unit = {},
    onNavigateToMultiplyingWithFlowers: () -> Unit = {},
    onNavigateToWeeksOfLife: () -> Unit = {},
    onNavigateToMultiplyingWithGroupsLevel2: () -> Unit = {},
    onNavigateToMultiplyingWithGroups: () -> Unit = {},
    onNavigateToMatchingTables: () -> Unit = {},
    onNavigateToMatchingTables67: () -> Unit = {},
    onNavigateToMatchingTables24: () -> Unit = {},
    onNavigateToMatchingTables248: () -> Unit = {},
    onNavigateToTheLast: () -> Unit = {},
    onNavigateToMatchingTables39: () -> Unit = {},
    onNavigateToMyFirstCube: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = latestAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val cas = carryingInAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val maes = matterAndEnergyScreenStringsForLanguage(LocalAppLanguage.current)
    val cwbs = countingWithBonesScreenStringsForLanguage(LocalAppLanguage.current)
    val rnas = runningAmongNumbersScreenStringsForLanguage(LocalAppLanguage.current)
    val sos = sharedOriginScreenStringsForLanguage(LocalAppLanguage.current)
    val swws = sharingWithWhomScreenStringsForLanguage(LocalAppLanguage.current)
    val ues = universeExpansionScreenStringsForLanguage(LocalAppLanguage.current)
    val exs = expandingScreenStringsForLanguage(LocalAppLanguage.current)
    val mfcs = myFirstCubeScreenStringsForLanguage(LocalAppLanguage.current)
    val mts24 = matchingTables24StringsForLanguage(LocalAppLanguage.current)
    val mts248 = matchingTables248StringsForLanguage(LocalAppLanguage.current)
    val tls = theLastScreenStringsForLanguage(LocalAppLanguage.current)
    val mts39 = matchingTables39StringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedReturning by preferences.completedReturningSections.collectAsState(initial = emptySet())
    val completedLongDivision by preferences.completedLongDivisionSections.collectAsState(initial = emptySet())
    val completedMultiplicationTable by preferences.completedMultiplicationTableSections.collectAsState(initial = emptySet())

    val entries = listOf(
        LatestAdditionEntry(
            sectionId = "matching_tables_3_9",
            label = mts39.rulesTitle,
            icon = {
                Text(
                    text = "3\u00D73\u00D7N\n=9\u00D7N",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "matching_tables_3_9" in completedMultiplicationTable },
            onNavigate = onNavigateToMatchingTables39
        ),
        LatestAdditionEntry(
            sectionId = "the_last",
            label = tls.fullTitle,
            icon = {
                Text(
                    text = "9",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "the_last" in completedMultiplicationTable },
            onNavigate = onNavigateToTheLast
        ),
        LatestAdditionEntry(
            sectionId = "matching_tables_2_4_8",
            label = mts248.rulesTitle,
            icon = {
                Text(
                    text = "2\u00D72\u00D72\u00D7N\n=4\u00D72\u00D7N\n=8\u00D7N",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "matching_tables_2_4_8" in completedMultiplicationTable },
            onNavigate = onNavigateToMatchingTables248
        ),
        LatestAdditionEntry(
            sectionId = "my_first_cube",
            label = mfcs.fullTitle,
            icon = {
                Text(
                    text = "8",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "my_first_cube" in completedMultiplicationTable },
            onNavigate = onNavigateToMyFirstCube
        ),
        LatestAdditionEntry(
            sectionId = "matching_tables_2_4",
            label = mts24.rulesTitle,
            icon = {
                Text(
                    text = "2\u00D72\u00D7N\n=4\u00D7N",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "matching_tables_2_4" in completedMultiplicationTable },
            onNavigate = onNavigateToMatchingTables24
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.common.back)
                }
                AutoSizeTitleText(
                    text = xs.title,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            entries.forEach { entry ->
                FilledIconButton(
                    onClick = { entry.onNavigate() },
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = entry.label },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (entry.isCompleted()) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    entry.icon()
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = entry.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}
