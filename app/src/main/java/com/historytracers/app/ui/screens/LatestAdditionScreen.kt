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
import com.historytracers.app.ui.features.measuringOneDirectionScreenStringsForLanguage
import com.historytracers.app.ui.features.carryingInAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.countingWithBonesScreenStringsForLanguage
import com.historytracers.app.ui.features.expandingScreenStringsForLanguage
import com.historytracers.app.ui.features.groupingThreesScreenStringsForLanguage
import com.historytracers.app.ui.features.latestAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.matterAndEnergyScreenStringsForLanguage
import com.historytracers.app.ui.features.practicingAdditionRoadScreenStringsForLanguage
import com.historytracers.app.ui.features.runningAmongNumbersScreenStringsForLanguage
import com.historytracers.app.ui.features.sharedOriginScreenStringsForLanguage
import com.historytracers.app.ui.features.sharingWithWhomScreenStringsForLanguage
import com.historytracers.app.ui.features.sunTzuAdditionScreenStringsForLanguage
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
    onNavigateToSunTzuAddition: () -> Unit = {}
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
    val mods = measuringOneDirectionScreenStringsForLanguage(LocalAppLanguage.current)
    val exs = expandingScreenStringsForLanguage(LocalAppLanguage.current)
    val gts = groupingThreesScreenStringsForLanguage(LocalAppLanguage.current)
    val pas = practicingAdditionRoadScreenStringsForLanguage(LocalAppLanguage.current)
    val sts = sunTzuAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedWhereAreWeFrom by preferences.completedWhereAreWeFromSections.collectAsState(initial = emptySet())
    val completedFirstSteps by preferences.completedFirstStepsSections.collectAsState(initial = emptySet())
    val completedMeasuring by preferences.completedMeasuringSections.collectAsState(initial = emptySet())
    val completedRoadToSomewhere by preferences.completedRoadToSomewhereSections.collectAsState(initial = emptySet())

    val entries = listOf(
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
        ),
        LatestAdditionEntry(
            sectionId = "grouping_threes",
            label = gts.title,
            icon = {
                Text(
                    text = "3, 6, 9...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "grouping_threes" in completedWhereAreWeFrom },
            onNavigate = onNavigateToGroupingThrees
        ),
        LatestAdditionEntry(
            sectionId = "expanding",
            label = exs.title,
            icon = {
                Canvas(modifier = Modifier.size(48.dp)) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val center = Offset(cx, cy)
                    val s = size.minDimension * 0.22f
                    val dx = s * 0.7f
                    val dy = -s * 0.7f
                    val axisLen = s * 1.8f
                    val axisStroke = size.minDimension * 0.045f
                    val cubeStroke = size.minDimension * 0.05f
                    drawLine(
                        color = Color(0xFFE53935),
                        start = Offset(cx - axisLen, cy),
                        end = Offset(cx + axisLen, cy),
                        strokeWidth = axisStroke
                    )
                    drawLine(
                        color = Color(0xFF43A047),
                        start = Offset(cx, cy + axisLen),
                        end = Offset(cx, cy - axisLen),
                        strokeWidth = axisStroke
                    )
                    val zLen = kotlin.math.sqrt(dx * dx + dy * dy)
                    val zUnit = if (zLen > 0f) Offset(dx / zLen, dy / zLen) else Offset(1f, -1f)
                    drawLine(
                        color = Color(0xFF1E88E5),
                        start = center - zUnit * axisLen,
                        end = center + zUnit * axisLen,
                        strokeWidth = axisStroke
                    )
                    val a = Offset(cx - s, cy - s)
                    val b = Offset(cx + s, cy - s)
                    val c = Offset(cx + s, cy + s)
                    val d = Offset(cx - s, cy + s)
                    val a2 = a + Offset(dx, dy)
                    val b2 = b + Offset(dx, dy)
                    val c2 = c + Offset(dx, dy)
                    val d2 = d + Offset(dx, dy)
                    val cubeColor = Color(0xFF212121)
                    drawLine(cubeColor, a, b, strokeWidth = cubeStroke)
                    drawLine(cubeColor, b, c, strokeWidth = cubeStroke)
                    drawLine(cubeColor, c, d, strokeWidth = cubeStroke)
                    drawLine(cubeColor, d, a, strokeWidth = cubeStroke)
                    drawLine(cubeColor, a2, b2, strokeWidth = cubeStroke)
                    drawLine(cubeColor, b2, c2, strokeWidth = cubeStroke)
                    drawLine(cubeColor, c2, d2, strokeWidth = cubeStroke)
                    drawLine(cubeColor, d2, a2, strokeWidth = cubeStroke)
                    drawLine(cubeColor, a, a2, strokeWidth = cubeStroke)
                    drawLine(cubeColor, b, b2, strokeWidth = cubeStroke)
                    drawLine(cubeColor, c, c2, strokeWidth = cubeStroke)
                    drawLine(cubeColor, d, d2, strokeWidth = cubeStroke)
                }
            },
            isCompleted = { "expanding" in completedWhereAreWeFrom },
            onNavigate = onNavigateToExpanding
        ),
        LatestAdditionEntry(
            sectionId = "measuring_one_direction",
            label = mods.title,
            icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_ruler),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            },
            isCompleted = { "measuring_one_direction" in completedMeasuring },
            onNavigate = onNavigateToMeasuringOneDirection
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
