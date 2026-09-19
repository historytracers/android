// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.historytracers.app.ui.features.buildingLikeAMesoamericanScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val MAX_LEVEL = 2
private const val ROWS = 5
private const val COLUMNS = 4

private fun hiddenPositionsFor(level: Int): Set<Int> {
    val hidden = mutableSetOf<Int>()
    for (row in 0 until ROWS) {
        val columns = (0 until COLUMNS).shuffled(Random).take(level)
        columns.forEach { hidden.add(row * COLUMNS + it) }
    }
    return hidden
}

@Composable
private fun MayaGameNumber(value: Int, modifier: Modifier = Modifier) {
    val ink = Color(0xFF5A3F2C)
    if (value == 0) {
        Image(
            painter = painterResource(R.drawable.ic_maya_zero_shell),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(ink),
            modifier = modifier
        )
        return
    }
    Canvas(modifier = modifier) {
        val dotRadius = size.minDimension * 0.07f
        val gap = size.height * 0.06f
        val barWidth = 4 * dotRadius * 2f + 3 * gap
        val barHeight = size.height * 0.14f
        val bars = value / 5
        val dots = value % 5
        val barsHeight = if (bars > 0) bars * barHeight + (bars - 1) * gap else 0f
        val dotsHeight = if (dots > 0) dotRadius * 2f + gap else 0f
        val contentHeight = barsHeight + dotsHeight
        var y = size.height - size.height * 0.05f - contentHeight
        if (dots > 0) {
            val totalWidth = dots * dotRadius * 2f + (dots - 1) * gap
            var x = center.x - totalWidth / 2f + dotRadius
            repeat(dots) {
                drawCircle(color = ink, radius = dotRadius, center = Offset(x, y + dotRadius))
                x += dotRadius * 2f + gap
            }
            y += dotRadius * 2f + gap
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingLikeAMesoamericanScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = buildingLikeAMesoamericanScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(1) }
    var hiddenPositions by remember { mutableStateOf(hiddenPositionsFor(1)) }
    var revealed by remember { mutableStateOf(mutableSetOf<Int>()) }
    var completed by remember { mutableStateOf(false) }
    var showFinalCongrats by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var isFeedbackPositive by remember { mutableStateOf(false) }

    fun startLevel(newLevel: Int) {
        level = newLevel
        hiddenPositions = hiddenPositionsFor(newLevel)
        revealed = mutableSetOf()
        completed = false
        showFinalCongrats = false
        feedbackMessage = ""
        isFeedbackPositive = false
    }

    LaunchedEffect(Unit) {
        startLevel(1)
    }

    fun revealCell(position: Int) {
        if (completed) return
        if (position !in hiddenPositions || position in revealed) return
        revealed = (revealed + position).toMutableSet()
        if (revealed.size == hiddenPositions.size) {
            completed = true
            totalAwarded += 2
            onScoreChanged(initialScore + totalAwarded)
            feedbackMessage = xs.completionMessage
            isFeedbackPositive = true
            if (level == MAX_LEVEL) {
                scope.launch {
                    preferences.recordLessonCompletion()
                    preferences.markAnotherWayToCountSectionCompleted("building_like_a_mesoamerican")
                }
            }
        }
    }

    fun toggleLevel() {
        if (!completed) return
        if (level == MAX_LEVEL) {
            if (!showFinalCongrats) {
                showFinalCongrats = true
                feedbackMessage = xs.congratsMessage
                isFeedbackPositive = true
                return
            }
            startLevel(1)
        } else {
            startLevel(level + 1)
        }
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
                    text = xs.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = xs.instruction,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp)
            )

            Text(
                text = "${s.common.levelPrefix}$level",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                for (row in 0 until ROWS) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until COLUMNS) {
                            val position = row * COLUMNS + col
                            val value = position
                            val isHidden = position in hiddenPositions
                            val isRevealed = position in revealed
                            val showQuestion = isHidden && !isRevealed
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (showQuestion) ButtonYellow else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        width = if (showQuestion) 2.dp else 1.dp,
                                        color = if (showQuestion) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable(enabled = showQuestion && !completed) { revealCell(position) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (showQuestion) {
                                    Text(
                                        text = "?",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White,
                                        modifier = Modifier.size(52.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            MayaGameNumber(
                                                value = value,
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = { startLevel(level) },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = s.common.newExercise,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                FilledTonalButton(
                    onClick = { toggleLevel() },
                    enabled = completed,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = s.common.nextLevel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (feedbackMessage.isNotEmpty()) {
                Text(
                    text = feedbackMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isFeedbackPositive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
