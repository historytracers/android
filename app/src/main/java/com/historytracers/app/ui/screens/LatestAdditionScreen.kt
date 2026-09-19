// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
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
import com.historytracers.app.R
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.carryingInAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.countingWithBonesScreenStringsForLanguage
import com.historytracers.app.ui.features.largeNumbersScreenStringsForLanguage
import com.historytracers.app.ui.features.latestAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.matterAndEnergyScreenStringsForLanguage
import com.historytracers.app.ui.features.mesoamericanSymbolsScreenStringsForLanguage
import com.historytracers.app.ui.features.overcomingLimitsScreenStringsForLanguage
import com.historytracers.app.ui.features.practicingWithQuipusScreenStringsForLanguage
import com.historytracers.app.ui.features.quipuOnTheYupanaScreenStringsForLanguage
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
    onNavigateToLargeNumbersIntro: () -> Unit = {},
    onNavigateToQuipuOnTheYupana: () -> Unit = {},
    onNavigateToMesoamericanSymbols: () -> Unit = {},
    onNavigateToOvercomingLimits: () -> Unit = {},
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
    val pwqs = practicingWithQuipusScreenStringsForLanguage(LocalAppLanguage.current)
    val ues = universeExpansionScreenStringsForLanguage(LocalAppLanguage.current)
    val lns = largeNumbersScreenStringsForLanguage(LocalAppLanguage.current)
    val qys = quipuOnTheYupanaScreenStringsForLanguage(LocalAppLanguage.current)
    val mss = mesoamericanSymbolsScreenStringsForLanguage(LocalAppLanguage.current)
    val ols = overcomingLimitsScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedRoadToSomewhere by preferences.completedRoadToSomewhereSections.collectAsState(initial = emptySet())
    val completedWhereAreWeFrom by preferences.completedWhereAreWeFromSections.collectAsState(initial = emptySet())
    val completedAnotherWayToCount by preferences.completedAnotherWayToCountSections.collectAsState(initial = emptySet())
    val completedFirstSteps by preferences.completedFirstStepsSections.collectAsState(initial = emptySet())
    val completedYupana by preferences.completedYupanaSections.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    val entries = listOf(
        LatestAdditionEntry(
            sectionId = "overcoming_limits",
            label = ols.title,
            icon = {
                Canvas(modifier = Modifier.size(48.dp)) {
                    val ink = Color(0xFF8B1A1A)
                    val dotRadius = size.minDimension * 0.07f
                    val gap = size.height * 0.06f
                    val barWidth = 4 * dotRadius * 2f + 3 * gap
                    val barHeight = size.height * 0.14f
                    val bars = 3
                    val dots = 4
                    val barsHeight = bars * barHeight + (bars - 1) * gap
                    val dotsHeight = dotRadius * 2f + gap
                    var y = size.height - size.height * 0.05f - (barsHeight + dotsHeight)
                    val totalWidth = dots * dotRadius * 2f + (dots - 1) * gap
                    var x = center.x - totalWidth / 2f + dotRadius
                    repeat(dots) {
                        drawCircle(color = ink, radius = dotRadius, center = Offset(x, y + dotRadius))
                        x += dotRadius * 2f + gap
                    }
                    y += dotRadius * 2f + gap
                    repeat(bars) {
                        drawRoundRect(
                            color = ink,
                            topLeft = Offset(center.x - barWidth / 2f, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(barHeight / 2f)
                        )
                        y += barHeight + gap
                    }
                }
            },
            isCompleted = { "overcoming_limits" in completedAnotherWayToCount },
            markCompleted = { preferences.markAnotherWayToCountSectionCompleted("overcoming_limits") },
            onNavigate = onNavigateToOvercomingLimits
        ),
        LatestAdditionEntry(
            sectionId = "mesoamerican_symbols",
            label = mss.title,
            icon = {
                Canvas(modifier = Modifier.size(48.dp)) {
                    val radius = size.minDimension * 0.16f
                    val left = size.width * 0.3f
                    val right = size.width * 0.7f
                    val top = size.height * 0.3f
                    val bottom = size.height * 0.7f
                    val dotColor = Color(0xFF8B1A1A)
                    listOf(
                        Offset(left, top),
                        Offset(right, top),
                        Offset(left, bottom),
                        Offset(right, bottom)
                    ).forEach { center ->
                        drawCircle(color = dotColor, radius = radius, center = center)
                    }
                }
            },
            isCompleted = { "mesoamerican_symbols" in completedAnotherWayToCount },
            markCompleted = { preferences.markAnotherWayToCountSectionCompleted("mesoamerican_symbols") },
            onNavigate = onNavigateToMesoamericanSymbols
        ),
        LatestAdditionEntry(
            sectionId = "quipu_on_the_yupana",
            label = qys.title,
            icon = {
                Image(
                    painter = painterResource(R.drawable.ic_quipu_knot),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            },
            isCompleted = { "quipu_on_the_yupana" in completedYupana },
            markCompleted = { preferences.markYupanaSectionCompleted("quipu_on_the_yupana") },
            onNavigate = onNavigateToQuipuOnTheYupana
        ),
        LatestAdditionEntry(
            sectionId = "large_numbers",
            label = lns.title,
            icon = {
                Image(
                    painter = painterResource(R.drawable.ic_grid_2x4),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            },
            isCompleted = { "large_numbers" in completedYupana },
            markCompleted = { preferences.markYupanaSectionCompleted("large_numbers") },
            onNavigate = onNavigateToLargeNumbersIntro
        ),
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
