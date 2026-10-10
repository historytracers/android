// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.R
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.carryingInAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.carryOrNotCarryScreenStringsForLanguage
import com.historytracers.app.ui.features.countingWithBonesScreenStringsForLanguage
import com.historytracers.app.ui.features.latestAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.matterAndEnergyScreenStringsForLanguage
import com.historytracers.app.ui.features.oddOrEvenScreenStringsForLanguage
import com.historytracers.app.ui.features.halfPieceScreenStringsForLanguage
import com.historytracers.app.ui.features.walkingBackwardsScreenStringsForLanguage
import com.historytracers.app.ui.features.runningAmongNumbersScreenStringsForLanguage
import com.historytracers.app.ui.features.roadToSomewhereScreenStringsForLanguage
import com.historytracers.app.ui.features.sharedOriginScreenStringsForLanguage
import com.historytracers.app.ui.features.sharingWithWhomScreenStringsForLanguage
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
    onNavigateToWalkingBackwards: () -> Unit = {},
    onNavigateToAroundTheWorld: () -> Unit = {},
    onNavigateToCarryOrNotCarry: () -> Unit = {},
    onNavigateToAdvancingAndComplementing: () -> Unit = {},
    onNavigateToOddOrEven: () -> Unit = {}
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
    val ooes = oddOrEvenScreenStringsForLanguage(LocalAppLanguage.current)
    val hps = halfPieceScreenStringsForLanguage(LocalAppLanguage.current)
    val wbs = walkingBackwardsScreenStringsForLanguage(LocalAppLanguage.current)
    val cncs = carryOrNotCarryScreenStringsForLanguage(LocalAppLanguage.current)
    val ros = roadToSomewhereScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedRoadToSomewhere by preferences.completedRoadToSomewhereSections.collectAsState(initial = emptySet())
    val completedWhereAreWeFrom by preferences.completedWhereAreWeFromSections.collectAsState(initial = emptySet())
    val completedFirstSteps by preferences.completedFirstStepsSections.collectAsState(initial = emptySet())
    val completedReturning by preferences.completedReturningSections.collectAsState(initial = emptySet())

    val entries = listOf(
        LatestAdditionEntry(
            sectionId = "odd_or_even_game",
            label = ooes.title,
            icon = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("1", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(OnButtonYellow, CircleShape)
                    )
                }
            },
            isCompleted = { "odd_or_even_game" in completedWhereAreWeFrom },
            onNavigate = onNavigateToOddOrEven
        ),
        LatestAdditionEntry(
            sectionId = "advancing_and_complementing",
            label = ros.advancingAndComplementing,
            icon = {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(2) { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            repeat(5) { col ->
                                val idx = row * 5 + col
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            if (idx < 8) OnButtonYellow else Color(0xFF00B7EB),
                                            RoundedCornerShape(1.dp)
                                        )
                                )
                            }
                        }
                    }
                }
            },
            isCompleted = { "advancing_and_complementing" in completedRoadToSomewhere },
            onNavigate = onNavigateToAdvancingAndComplementing
        ),
        LatestAdditionEntry(
            sectionId = "carry_or_not_carry",
            label = cncs.title,
            icon = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("1", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("?", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            },
            isCompleted = { "carry_or_not_carry" in completedRoadToSomewhere },
            onNavigate = onNavigateToCarryOrNotCarry
        ),
        LatestAdditionEntry(
            sectionId = "walking_backwards",
            label = wbs.title,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_turn_left),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            },
            isCompleted = { "walking_backwards" in completedReturning },
            onNavigate = onNavigateToWalkingBackwards
        ),
        LatestAdditionEntry(
            sectionId = "half_piece",
            label = hps.title,
            icon = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val sheet = Modifier
                        .size(width = 20.dp, height = 26.dp)
                        .background(Color.White, RoundedCornerShape(2.dp))
                        .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(2.dp))
                    Box(sheet)
                    Box(sheet)
                }
            },
            isCompleted = { "half_piece" in completedWhereAreWeFrom },
            onNavigate = onNavigateToHalfPiece
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
