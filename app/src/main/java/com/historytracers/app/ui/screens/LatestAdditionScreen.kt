// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
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
import com.historytracers.app.ui.features.buildingLikeEtruscanRomansScreenStringsForLanguage
import com.historytracers.app.ui.features.countingWithBonesScreenStringsForLanguage
import com.historytracers.app.ui.features.etruscanRomanTensScreenStringsForLanguage
import com.historytracers.app.ui.features.hundredsAndThousandsScreenStringsForLanguage
import com.historytracers.app.ui.features.iPreferThisScreenStringsForLanguage
import com.historytracers.app.ui.features.latestAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.matterAndEnergyScreenStringsForLanguage
import com.historytracers.app.ui.features.representYouScreenStringsForLanguage
import com.historytracers.app.ui.features.runningAmongNumbersScreenStringsForLanguage
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
    onNavigateToRepresentYou: () -> Unit = {}
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
    val ipts = iPreferThisScreenStringsForLanguage(LocalAppLanguage.current)
    val bers = buildingLikeEtruscanRomansScreenStringsForLanguage(LocalAppLanguage.current)
    val erts = etruscanRomanTensScreenStringsForLanguage(LocalAppLanguage.current)
    val hats = hundredsAndThousandsScreenStringsForLanguage(LocalAppLanguage.current)
    val rys = representYouScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedRoadToSomewhere by preferences.completedRoadToSomewhereSections.collectAsState(initial = emptySet())
    val completedWhereAreWeFrom by preferences.completedWhereAreWeFromSections.collectAsState(initial = emptySet())
    val completedAnotherWayToCount by preferences.completedAnotherWayToCountSections.collectAsState(initial = emptySet())
    val completedFirstSteps by preferences.completedFirstStepsSections.collectAsState(initial = emptySet())

    val entries = listOf(
        LatestAdditionEntry(
            sectionId = "i_represent_you",
            label = rys.title,
            icon = {
                Text(
                    text = "10 = X",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF8B1A1A)
                )
            },
            isCompleted = { "i_represent_you" in completedAnotherWayToCount },
            onNavigate = onNavigateToRepresentYou
        ),
        LatestAdditionEntry(
            sectionId = "hundreds_and_thousands",
            label = hats.title,
            icon = {
                Text(
                    text = "C ... M",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF8B1A1A)
                )
            },
            isCompleted = { "hundreds_and_thousands" in completedAnotherWayToCount },
            onNavigate = onNavigateToHundredsAndThousands
        ),
        LatestAdditionEntry(
            sectionId = "etruscan_roman_tens",
            label = erts.title,
            icon = {
                Text(
                    text = "X .. XCIX",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF8B1A1A)
                )
            },
            isCompleted = { "etruscan_roman_tens" in completedAnotherWayToCount },
            onNavigate = onNavigateToEtruscanRomanTens
        ),
        LatestAdditionEntry(
            sectionId = "building_like_etruscan_romans",
            label = bers.title,
            icon = {
                Canvas(modifier = Modifier.size(48.dp)) {
                    val brickColor = Color(0xFF8B1A1A)
                    val gap = size.minDimension * 0.05f
                    val rows = 3
                    val rowHeight = (size.height - gap * (rows - 1)) / rows
                    val brickWidth = size.width / 3f
                    for (row in 0 until rows) {
                        val y = row * (rowHeight + gap)
                        val startX = if (row % 2 == 0) 0f else -brickWidth / 2f
                        var x = startX
                        while (x < size.width) {
                            val left = maxOf(x, 0f)
                            val right = minOf(x + brickWidth - gap, size.width)
                            if (right > left) {
                                drawRoundRect(
                                    color = brickColor,
                                    topLeft = Offset(left, y),
                                    size = Size(right - left, rowHeight),
                                    cornerRadius = CornerRadius(rowHeight * 0.2f)
                                )
                            }
                            x += brickWidth
                        }
                    }
                }
            },
            isCompleted = { "building_like_etruscan_romans" in completedAnotherWayToCount },
            onNavigate = onNavigateToBuildingLikeEtruscanRomans
        ),
        LatestAdditionEntry(
            sectionId = "i_prefer_this",
            label = ipts.title,
            icon = {
                Text(
                    text = "IV or IIII",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF8B1A1A)
                )
            },
            isCompleted = { "i_prefer_this" in completedAnotherWayToCount },
            onNavigate = onNavigateToIPreferThis
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
