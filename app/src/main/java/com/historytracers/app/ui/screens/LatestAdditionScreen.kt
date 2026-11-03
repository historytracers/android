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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.carryingInAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.countingWithBonesScreenStringsForLanguage
import com.historytracers.app.ui.features.divisionAndRemainderScreenStringsForLanguage
import com.historytracers.app.ui.features.choosingDividendAndDivisorScreenStringsForLanguage
import com.historytracers.app.ui.features.writingTheQuotientScreenStringsForLanguage
import com.historytracers.app.ui.features.writingQuotientAndRemainderScreenStringsForLanguage
import com.historytracers.app.ui.features.expandingScreenStringsForLanguage
import com.historytracers.app.ui.features.latestAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.matterAndEnergyScreenStringsForLanguage
import com.historytracers.app.ui.features.runningAmongNumbersScreenStringsForLanguage
import com.historytracers.app.ui.features.sharedOriginScreenStringsForLanguage
import com.historytracers.app.ui.features.sharingWithWhomScreenStringsForLanguage
import com.historytracers.app.ui.features.universeExpansionScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.R
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
    onNavigateToChoosingDividendAndDivisor: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = latestAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val cas = carryingInAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val maes = matterAndEnergyScreenStringsForLanguage(LocalAppLanguage.current)
    val cwbs = countingWithBonesScreenStringsForLanguage(LocalAppLanguage.current)
    val rnas = runningAmongNumbersScreenStringsForLanguage(LocalAppLanguage.current)
    val sos = sharedOriginScreenStringsForLanguage(LocalAppLanguage.current)
    val swws = sharingWithWhomScreenStringsForLanguage(LocalAppLanguage.current)
    val ues = universeExpansionScreenStringsForLanguage(LocalAppLanguage.current)
    val exs = expandingScreenStringsForLanguage(LocalAppLanguage.current)
    val ldrs = divisionAndRemainderScreenStringsForLanguage(LocalAppLanguage.current)
    val wtqs = writingTheQuotientScreenStringsForLanguage(LocalAppLanguage.current)
    val wqars = writingQuotientAndRemainderScreenStringsForLanguage(LocalAppLanguage.current)
    val cdds = choosingDividendAndDivisorScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedReturning by preferences.completedReturningSections.collectAsState(initial = emptySet())
    val completedAroundTheWorldAddition by preferences.completedAroundTheWorldAdditionSections.collectAsState(initial = emptySet())
    val completedLongDivision by preferences.completedLongDivisionSections.collectAsState(initial = emptySet())

    val entries = listOf(
        LatestAdditionEntry(
            sectionId = "choosing_dividend_and_divisor",
            label = cdds.title,
            icon = {
                Text(
                    text = "??? \u00F7 ?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "choosing_dividend_and_divisor" in completedLongDivision },
            onNavigate = onNavigateToChoosingDividendAndDivisor
        ),
        LatestAdditionEntry(
            sectionId = "writing_quotient_and_remainder",
            label = wqars.title,
            icon = {
                Text(
                    text = "987 \u00F7 1",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "writing_quotient_and_remainder" in completedLongDivision },
            onNavigate = onNavigateToWritingQuotientAndRemainder
        ),
        LatestAdditionEntry(
            sectionId = "writing_the_quotient",
            label = wtqs.title,
            icon = {
                Text(
                    text = "125 \u00F7 5",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "writing_the_quotient" in completedLongDivision },
            onNavigate = onNavigateToWritingTheQuotient
        ),
        LatestAdditionEntry(
            sectionId = "division_and_remainder",
            label = ldrs.title,
            icon = {
                Text(
                    text = "257 \u00F7 4",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "division_and_remainder" in completedLongDivision },
            onNavigate = onNavigateToDivisionAndRemainder
        ),
        LatestAdditionEntry(
            sectionId = "around_the_world_addition",
            label = hts.aroundTheWorldAddition,
            icon = {
                Icon(
                    painterResource(R.drawable.ic_world),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = Color.Unspecified
                )
            },
            isCompleted = { "around_the_world_addition" in completedAroundTheWorldAddition },
            onNavigate = onNavigateToAroundTheWorldAddition
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
                Text(
                    text = xs.title,
                    style = MaterialTheme.typography.titleLarge,
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
