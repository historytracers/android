// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.historytracers.app.ui.features.countingWithBonesScreenStringsForLanguage
import com.historytracers.app.ui.features.latestAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.matterAndEnergyScreenStringsForLanguage
import com.historytracers.app.ui.features.practicingWithQuipusScreenStringsForLanguage
import com.historytracers.app.ui.features.quipusScreenStringsForLanguage
import com.historytracers.app.ui.features.runningAmongNumbersScreenStringsForLanguage
import com.historytracers.app.ui.features.sharedOriginScreenStringsForLanguage
import com.historytracers.app.ui.features.sharingWithWhomScreenStringsForLanguage
import com.historytracers.app.ui.features.universeExpansionScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

private data class LatestAdditionEntry(
    val sectionId: String,
    val label: String,
    val icon: @Composable () -> Unit,
    val isCompleted: () -> Boolean,
    val markCompleted: suspend () -> Unit,
    val onNavigate: () -> Unit
)

@Composable
fun LatestAdditionScreen(
    scrollState: ScrollState = rememberScrollState(),
    onNavigateBack: () -> Unit = {},
    onNavigateToPlayingWithAxioms: () -> Unit = {},
    onNavigateToCarryingInAdditionIntro: () -> Unit = {},
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
    onNavigateToHistoricalEqualityPyramidsIntro: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = latestAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val cas = carryingInAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val maes = matterAndEnergyScreenStringsForLanguage(LocalAppLanguage.current)
    val cwbs = countingWithBonesScreenStringsForLanguage(LocalAppLanguage.current)
    val rnas = runningAmongNumbersScreenStringsForLanguage(LocalAppLanguage.current)
    val sos = sharedOriginScreenStringsForLanguage(LocalAppLanguage.current)
    val swws = sharingWithWhomScreenStringsForLanguage(LocalAppLanguage.current)
    val qs = quipusScreenStringsForLanguage(LocalAppLanguage.current)
    val pwqs = practicingWithQuipusScreenStringsForLanguage(LocalAppLanguage.current)
    val ues = universeExpansionScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedRoadToSomewhere by preferences.completedRoadToSomewhereSections.collectAsState(initial = emptySet())
    val completedWhereAreWeFrom by preferences.completedWhereAreWeFromSections.collectAsState(initial = emptySet())
    val completedAnotherWayToCount by preferences.completedAnotherWayToCountSections.collectAsState(initial = emptySet())
    val completedFirstSteps by preferences.completedFirstStepsSections.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    val entries = listOf(
        LatestAdditionEntry(
            sectionId = "practicing_with_quipus",
            label = pwqs.title,
            icon = {
                Image(
                    painter = painterResource(R.drawable.ic_quipu_practice),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            },
            isCompleted = { "practicing_with_quipus" in completedAnotherWayToCount },
            markCompleted = { preferences.markAnotherWayToCountSectionCompleted("practicing_with_quipus") },
            onNavigate = onNavigateToPracticingWithQuipus
        ),
        LatestAdditionEntry(
            sectionId = "quipus",
            label = qs.title,
            icon = {
                Image(
                    painter = painterResource(R.drawable.ic_quipu_knot),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            },
            isCompleted = { "quipus" in completedAnotherWayToCount },
            markCompleted = { preferences.markAnotherWayToCountSectionCompleted("quipus") },
            onNavigate = onNavigateToQuipusIntro
        ),
        LatestAdditionEntry(
            sectionId = "sharing_with_whom",
            label = swws.title,
            icon = {
                Text(
                    text = "?",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "sharing_with_whom" in completedWhereAreWeFrom },
            markCompleted = { preferences.markWhereAreWeFromSectionCompleted("sharing_with_whom") },
            onNavigate = onNavigateToSharingWithWhomIntro
        ),
        LatestAdditionEntry(
            sectionId = "everything_together",
            label = ues.title,
            icon = {
                Image(
                    painter = painterResource(R.drawable.ic_universe_expansion),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            },
            isCompleted = { "everything_together" in completedWhereAreWeFrom },
            markCompleted = { preferences.markWhereAreWeFromSectionCompleted("everything_together") },
            onNavigate = onNavigateToEverythingWasTogether
        ),
        LatestAdditionEntry(
            sectionId = "counting_with_bones",
            label = cwbs.title,
            icon = {
                Image(
                    painter = painterResource(R.drawable.ic_hand_bones),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            },
            isCompleted = { "counting_with_bones" in completedFirstSteps },
            markCompleted = { preferences.markFirstStepsSectionCompleted("counting_with_bones") },
            onNavigate = onNavigateToCountingWithBonesIntro
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
                    onClick = {
                        scope.launch {
                            entry.markCompleted()
                            entry.onNavigate()
                        }
                    },
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
