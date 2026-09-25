// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.buildingLikeEtruscanRomansScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

private const val MAX_LEVEL = 5
private const val ROWS = 5
private const val COLUMNS = 4
private const val VALUES_PER_LEVEL = ROWS * COLUMNS

private fun levelStartValue(level: Int): Int = (level - 1) * VALUES_PER_LEVEL + 1

private fun levelValueCount(level: Int): Int =
    if (level == MAX_LEVEL) 99 - levelStartValue(level) + 1 else VALUES_PER_LEVEL

private fun hiddenPositionsFor(level: Int): Set<Int> {
    val hidden = mutableSetOf<Int>()
    val count = levelValueCount(level)
    for (row in 0 until ROWS) {
        val rowPositions = (0 until COLUMNS)
            .map { row * COLUMNS + it }
            .filter { it < count }
        if (rowPositions.isEmpty()) continue
        hidden.add(rowPositions.random())
    }
    return hidden
}

private val etruscanRomanSymbols = listOf(
    1000 to "M", 900 to "CM", 500 to "D", 400 to "CD",
    100 to "C", 90 to "XC", 50 to "L", 40 to "XL",
    10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I"
)

private fun toEtruscanRoman(value: Int): String {
    var remaining = value
    val result = StringBuilder()
    for ((symbolValue, symbol) in etruscanRomanSymbols) {
        while (remaining >= symbolValue) {
            result.append(symbol)
            remaining -= symbolValue
        }
    }
    return result.toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingLikeEtruscanRomansScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = buildingLikeEtruscanRomansScreenStringsForLanguage(LocalAppLanguage.current)
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
                    preferences.markAnotherWayToCountSectionCompleted("building_like_etruscan_romans")
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

    val startValue = levelStartValue(level)
    val valueCount = levelValueCount(level)
    val rangeEnd = startValue + valueCount - 1

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
                text = "${s.common.levelPrefix}$level ($startValue \u2013 $rangeEnd)",
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
                            if (position >= valueCount) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )
                                continue
                            }
                            val value = startValue + position
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
                                Text(
                                    text = if (showQuestion) "?" else toEtruscanRoman(value),
                                    style = if (showQuestion) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    color = if (showQuestion) MaterialTheme.colorScheme.primary else Color(0xFF5A3F2C)
                                )
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
