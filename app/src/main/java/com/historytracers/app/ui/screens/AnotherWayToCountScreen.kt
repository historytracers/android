// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.R
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.anotherWayToCountScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.FlagBlueDark
import com.historytracers.app.ui.theme.FlagBlueLight
import kotlinx.coroutines.launch

private val anotherWayToCountFirstGroupIds = listOf("quipus", "practicing_with_quipus")
private val anotherWayToCountSecondGroupIds = listOf("mesoamerican_symbols", "overcoming_limits", "building_like_a_mesoamerican", "mesoamerican_orders")
private val anotherWayToCountThirdGroupIds = listOf("text_or_number")

@Composable
fun AnotherWayToCountScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToCongratulation: () -> Unit = {},
    onNavigateToQuipus: () -> Unit = {},
    onNavigateToPracticingWithQuipus: () -> Unit = {},
    onNavigateToMesoamericanSymbols: () -> Unit = {},
    onNavigateToOvercomingLimits: () -> Unit = {},
    onNavigateToBuildingLikeAMesoamerican: () -> Unit = {},
    onNavigateToMesoamericanOrder: () -> Unit = {},
    onNavigateToTextOrNumber: () -> Unit = {},
    onNavigateToTheMissingNumbers: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val xs = anotherWayToCountScreenStringsForLanguage(LocalAppLanguage.current)

    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedSections by preferences.completedAnotherWayToCountSections.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    val firstGroupCompleted = anotherWayToCountFirstGroupIds.all { it in completedSections }
    val secondGroupCompleted = anotherWayToCountSecondGroupIds.all { it in completedSections }
    val thirdGroupCompleted = anotherWayToCountThirdGroupIds.all { it in completedSections }
    val claimedLevels by preferences.claimedLevels.collectAsState(initial = emptySet())

    var showResetMenu by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    fun claimLevel(levelId: String) {
        if (levelId in claimedLevels) return
        onScoreChanged(currentScore + 10)
        scope.launch { preferences.markLevelClaimed(levelId) }
    }

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
                    text = hts.anotherWayToCount,
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
                    onClick = onNavigateToQuipus,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("quipus" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_quipu_knot),
                        contentDescription = xs.quipus,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.quipus,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))

                FilledIconButton(
                    onClick = onNavigateToPracticingWithQuipus,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("practicing_with_quipus" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_quipu_practice),
                        contentDescription = xs.practicingWithQuipus,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.practicingWithQuipus,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = {
                        claimLevel("another_way_to_count")
                        onNavigateToCongratulation()
                    },
                    enabled = firstGroupCompleted,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("another_way_to_count" in claimedLevels) FlagBlueDark else FlagBlueLight,
                        disabledContainerColor = FlagBlueLight
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_flag),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = Color.Unspecified
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = s.common.nextLevel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))

                FilledIconButton(
                    onClick = onNavigateToMesoamericanSymbols,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("mesoamerican_symbols" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Canvas(modifier = Modifier.size(52.dp)) {
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
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.theMesoamericanSymbols,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToOvercomingLimits,
                    modifier = Modifier.size(96.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("overcoming_limits" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Canvas(modifier = Modifier.size(52.dp)) {
                        val ink = Color(0xFF8B1A1A)
                        val dotRadius = size.minDimension * 0.07f
                        val gap = size.height * 0.06f
                        val barWidth = 4 * dotRadius * 2f + 3 * gap
                        val barHeight = size.height * 0.14f
                        val bars = 3
                        val dots = 4
                        val barsHeight = bars * barHeight + (bars - 1) * gap
                        val dotsHeight = dotRadius * 2f + gap
                        val contentHeight = barsHeight + dotsHeight
                        var y = size.height - size.height * 0.05f - contentHeight
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
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.overcomingLimits,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToBuildingLikeAMesoamerican,
                    modifier = Modifier.size(96.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("building_like_a_mesoamerican" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Canvas(modifier = Modifier.size(52.dp)) {
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
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.buildingLikeAMesoamerican,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToMesoamericanOrder,
                    modifier = Modifier.size(96.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("mesoamerican_orders" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Canvas(modifier = Modifier.size(52.dp)) {
                        val ink = Color(0xFF8B1A1A)
                        val stroke = size.minDimension * 0.055f
                        val left = size.width * 0.15f
                        val right = size.width * 0.85f
                        val top = size.height * 0.26f
                        val bottom = size.height * 0.88f
                        val corner = size.minDimension * 0.12f
                        val bodyWidth = right - left
                        val bodyHeight = bottom - top
                        drawRoundRect(
                            color = ink,
                            topLeft = Offset(left, top),
                            size = Size(bodyWidth, bodyHeight),
                            cornerRadius = CornerRadius(corner),
                            style = Stroke(width = stroke)
                        )
                        val headerHeight = bodyHeight * 0.3f
                        drawRoundRect(
                            color = ink,
                            topLeft = Offset(left, top),
                            size = Size(bodyWidth, headerHeight),
                            cornerRadius = CornerRadius(corner)
                        )
                        val ringX1 = left + bodyWidth * 0.3f
                        val ringX2 = left + bodyWidth * 0.7f
                        val ringTop = top - size.height * 0.12f
                        val ringBottom = top + headerHeight * 0.3f
                        drawLine(
                            color = ink,
                            start = Offset(ringX1, ringTop),
                            end = Offset(ringX1, ringBottom),
                            strokeWidth = stroke * 1.4f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = ink,
                            start = Offset(ringX2, ringTop),
                            end = Offset(ringX2, ringBottom),
                            strokeWidth = stroke * 1.4f,
                            cap = StrokeCap.Round
                        )
                        val dotRadius = size.minDimension * 0.035f
                        val cols = 3
                        val rows = 2
                        val gridAreaTop = top + headerHeight
                        val gridTop = gridAreaTop + (bottom - gridAreaTop) * 0.2f
                        val gridBottom = bottom - (bottom - gridAreaTop) * 0.2f
                        val x0 = left + bodyWidth * 0.18f
                        val x1 = right - bodyWidth * 0.18f
                        for (r in 0 until rows) {
                            for (c in 0 until cols) {
                                val x = x0 + (x1 - x0) * c / (cols - 1)
                                val y = gridTop + (gridBottom - gridTop) * r / (rows - 1)
                                drawCircle(color = ink, radius = dotRadius, center = Offset(x, y))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.mesoamericanOrder,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = {
                        claimLevel("another_way_to_count_2")
                        onNavigateToCongratulation()
                    },
                    enabled = secondGroupCompleted,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("another_way_to_count_2" in claimedLevels) FlagBlueDark else FlagBlueLight,
                        disabledContainerColor = FlagBlueLight
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_flag),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = Color.Unspecified
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = s.common.nextLevel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToTextOrNumber,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("text_or_number" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Text(
                        text = "VI",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B1A1A)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.textOrNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToTheMissingNumbers,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = ButtonYellow
                    )
                ) {
                    Text(
                        text = "I...X",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B1A1A)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.theMissingNumbers,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = {
                        claimLevel("another_way_to_count_3")
                        onNavigateToCongratulation()
                    },
                    enabled = thirdGroupCompleted,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("another_way_to_count_3" in claimedLevels) FlagBlueDark else FlagBlueLight,
                        disabledContainerColor = FlagBlueLight
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_flag),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = Color.Unspecified
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = s.common.nextLevel,
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
