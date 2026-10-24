// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.historytracers.app.ui.features.carryingInAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.countingWithBonesScreenStringsForLanguage
import com.historytracers.app.ui.features.expandingScreenStringsForLanguage
import com.historytracers.app.ui.features.latestAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.matterAndEnergyScreenStringsForLanguage
import com.historytracers.app.ui.features.practicingAdditionRoadScreenStringsForLanguage
import com.historytracers.app.ui.features.practicingAdditionChosenScreenStringsForLanguage
import com.historytracers.app.ui.features.practicingAdditionTypedScreenStringsForLanguage
import com.historytracers.app.ui.features.runningAmongNumbersScreenStringsForLanguage
import com.historytracers.app.ui.features.sharedOriginScreenStringsForLanguage
import com.historytracers.app.ui.features.sharingWithWhomScreenStringsForLanguage
import com.historytracers.app.ui.features.sunTzuAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.whoAmIScreenStringsForLanguage
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
    onNavigateToBiggerOrSmaller: () -> Unit = {},
    onNavigateToGreaterOrSmallerGame: () -> Unit = {},
    onNavigateToMeasuringOneDirection: () -> Unit = {},
    onNavigateToWalkingBackwards: () -> Unit = {},
    onNavigateToAroundTheWorld: () -> Unit = {},
    onNavigateToCarryOrNotCarry: () -> Unit = {},
    onNavigateToAdvancingAndComplementing: () -> Unit = {},
    onNavigateToOddOrEven: () -> Unit = {},
    onNavigateToExpanding: () -> Unit = {},
    onNavigateToGroupingThrees: () -> Unit = {},
    onNavigateToPracticingAddition: () -> Unit = {},
    onNavigateToSunTzuAddition: () -> Unit = {},
    onNavigateToPracticingAdditionTyped: () -> Unit = {},
    onNavigateToPracticingAdditionChosen: () -> Unit = {},
    onNavigateToSunTzuLearning: () -> Unit = {}
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
    val pas = practicingAdditionRoadScreenStringsForLanguage(LocalAppLanguage.current)
    val pts = practicingAdditionTypedScreenStringsForLanguage(LocalAppLanguage.current)
    val pcs = practicingAdditionChosenScreenStringsForLanguage(LocalAppLanguage.current)
    val sts = sunTzuAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedRoadToSomewhere by preferences.completedRoadToSomewhereSections.collectAsState(initial = emptySet())
    val completedWhoAmI by preferences.completedWhoAmISections.collectAsState(initial = emptySet())
    val wss = whoAmIScreenStringsForLanguage(LocalAppLanguage.current)

    val entries = listOf(
        LatestAdditionEntry(
            sectionId = "practicing_addition_chosen",
            label = pcs.title,
            icon = {
                Text(
                    text = "?? + ??",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "practicing_addition_chosen" in completedRoadToSomewhere },
            onNavigate = onNavigateToPracticingAdditionChosen
        ),
        LatestAdditionEntry(
            sectionId = "sun_tzu_and_learning",
            label = wss.sunTzuAndLearning,
            icon = {
                Text(
                    text = "孫子",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "sun_tzu_and_learning" in completedWhoAmI },
            onNavigate = onNavigateToSunTzuLearning
        ),
        LatestAdditionEntry(
            sectionId = "practicing_addition_typed",
            label = pts.title,
            icon = {
                Text(
                    text = "56 + 78",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "practicing_addition_typed" in completedRoadToSomewhere },
            onNavigate = onNavigateToPracticingAdditionTyped
        ),
        LatestAdditionEntry(
            sectionId = "sun_tzu_addition",
            label = sts.title,
            icon = {
                Text(
                    text = "\u5b6b\u5b50",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "sun_tzu_addition" in completedRoadToSomewhere },
            onNavigate = onNavigateToSunTzuAddition
        ),
        LatestAdditionEntry(
            sectionId = "practicing_addition",
            label = pas.title,
            icon = {
                Text(
                    text = "+",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "practicing_addition" in completedRoadToSomewhere },
            onNavigate = onNavigateToPracticingAddition
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
