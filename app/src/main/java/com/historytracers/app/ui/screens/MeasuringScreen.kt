// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.measuringScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

@Composable
fun MeasuringScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToBiggerOrSmaller: () -> Unit = {},
    onNavigateToGreaterOrSmallerGame: () -> Unit = {},
    onNavigateToMeasuringOneDirection: () -> Unit = {},
    onNavigateToMeasuringTwoDirections: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val xs = measuringScreenStringsForLanguage(LocalAppLanguage.current)

    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val completedMeasuring by preferences.completedMeasuringSections.collectAsState(initial = emptySet())
    val biggerOrSmallerDone = "bigger_or_smaller" in completedMeasuring
    val greaterOrSmallerGameDone = "greater_or_smaller_game" in completedMeasuring
    val measuringOneDirectionDone = "measuring_one_direction" in completedMeasuring
    val measuringTwoDirectionsDone = "measuring_two_directions" in completedMeasuring

    var showResetMenu by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

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
                    text = hts.measuring,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(Modifier.weight(1f))
                Box {
                    IconButton(onClick = { showResetMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = s.common.menu,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = showResetMenu,
                        onDismissRequest = { showResetMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(s.common.resetClasses) },
                            onClick = {
                                showResetMenu = false
                                showResetDialog = true
                            }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                FilledIconButton(
                    onClick = onNavigateToBiggerOrSmaller,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (biggerOrSmallerDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = xs.compareExpression,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.biggerOrSmaller,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToGreaterOrSmallerGame,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (greaterOrSmallerGameDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = xs.compareSymbols,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.greaterOrSmallerGame,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToMeasuringOneDirection,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = xs.measuringOneDirection },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (measuringOneDirectionDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    RulerWithLineIcon(
                        modifier = Modifier.size(52.dp),
                        color = LocalContentColor.current
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.measuringOneDirection,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToMeasuringTwoDirections,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = xs.measuringTwoDirections },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (measuringTwoDirectionsDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    RulersTwoDirectionsIcon(
                        modifier = Modifier.size(52.dp),
                        color = LocalContentColor.current
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.measuringTwoDirections,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(s.common.resetClasses) },
            text = { Text(s.common.resetClassesMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        scope.launch { preferences.resetAllClasses() }
                    }
                ) {
                    Text(s.common.ok)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(s.common.cancel)
                }
            }
        )
    }
}

@Composable
private fun RulersTwoDirectionsIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Horizontal ruler body (bottom).
        val hLeft = w * 0.30f
        val hTop = h * 0.60f
        val hWidth = w * 0.62f
        val hHeight = h * 0.28f
        drawRoundRect(
            color = color,
            topLeft = Offset(hLeft, hTop),
            size = Size(hWidth, hHeight),
            cornerRadius = CornerRadius(hHeight * 0.18f),
            style = Stroke(width = h * 0.05f)
        )

        // Tick marks along the horizontal ruler.
        val hTicks = 6
        for (i in 0..hTicks) {
            val x = hLeft + hWidth * i / hTicks
            val longTick = i % 2 == 0
            val tickHeight = if (longTick) hHeight * 0.55f else hHeight * 0.30f
            drawLine(
                color = color,
                start = Offset(x, hTop),
                end = Offset(x, hTop + tickHeight),
                strokeWidth = h * 0.035f,
                cap = StrokeCap.Round
            )
        }

        // Vertical ruler body (left side).
        val vLeft = w * 0.08f
        val vTop = h * 0.08f
        val vWidth = w * 0.28f
        val vHeight = h * 0.62f
        drawRoundRect(
            color = color,
            topLeft = Offset(vLeft, vTop),
            size = Size(vWidth, vHeight),
            cornerRadius = CornerRadius(vWidth * 0.18f),
            style = Stroke(width = h * 0.05f)
        )

        // Tick marks along the vertical ruler.
        val vTicks = 6
        for (i in 0..vTicks) {
            val y = vTop + vHeight * i / vTicks
            val longTick = i % 2 == 0
            val tickWidth = if (longTick) vWidth * 0.55f else vWidth * 0.30f
            drawLine(
                color = color,
                start = Offset(vLeft, y),
                end = Offset(vLeft + tickWidth, y),
                strokeWidth = h * 0.035f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun RulerWithLineIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // The line being measured, on top of the ruler.
        val lineY = h * 0.20f
        drawLine(
            color = color,
            start = Offset(w * 0.12f, lineY),
            end = Offset(w * 0.88f, lineY),
            strokeWidth = h * 0.055f,
            cap = StrokeCap.Round
        )

        // The ruler body.
        val rulerLeft = w * 0.08f
        val rulerTop = h * 0.52f
        val rulerWidth = w * 0.84f
        val rulerHeight = h * 0.30f
        drawRoundRect(
            color = color,
            topLeft = Offset(rulerLeft, rulerTop),
            size = Size(rulerWidth, rulerHeight),
            cornerRadius = CornerRadius(rulerHeight * 0.18f),
            style = Stroke(width = h * 0.05f)
        )

        // Tick marks along the ruler.
        val ticks = 6
        for (i in 0..ticks) {
            val x = rulerLeft + rulerWidth * i / ticks
            val longTick = i % 2 == 0
            val tickHeight = if (longTick) rulerHeight * 0.55f else rulerHeight * 0.30f
            drawLine(
                color = color,
                start = Offset(x, rulerTop),
                end = Offset(x, rulerTop + tickHeight),
                strokeWidth = h * 0.035f,
                cap = StrokeCap.Round
            )
        }
    }
}
