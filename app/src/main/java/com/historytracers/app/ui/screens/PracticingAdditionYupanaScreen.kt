// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlin.math.min
import kotlin.random.Random

private const val ROWS = 4
private const val MAX_DIGIT_LEVEL = 9
private const val MIN_DIGIT_LEVEL = 1

private val yupanaSelectors = listOf(
    -1, 4, 3, 2, 4, 1, 1, 1, 1, 1,
    -1, -1, -1, -1, 2, -1, 4, 3, 2, 2,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, 4
)

private data class YpRowState(
    val leftDigit: Int = 0,
    val rightDigit: Int = 0,
    val resultDigit: Int = 0
)

private data class YpExercise(val left: Int, val right: Int) {
    val sum: Int get() = left + right
}

private val placeLabels = listOf("thousands", "hundreds", "tens", "units")

private fun getMarkersForDigit(digit: Int): Set<Int> {
    val cols = mutableSetOf<Int>()
    for (offset in 0..2) {
        val idx = digit + offset * 10
        if (idx >= yupanaSelectors.size) break
        val col = yupanaSelectors[idx]
        if (col > 0) cols.add(col)
    }
    return cols
}

private fun numberToDigits(n: Int): List<Int> {
    val clamped = n.coerceIn(0, 9999)
    val s = clamped.toString().padStart(ROWS, '0')
    return s.map { it - '0' }
}

private fun getLevelRange(level: Int): Pair<Int, Int> {
    val base = (level - 1) * 100
    val min = if (level == 1) 1 else base
    return Pair(min, base + 99)
}

private fun generateYpExercise(level: Int): YpExercise {
    val (min, max) = getLevelRange(level)
    val left = Random.nextInt(min, max + 1)
    val right = Random.nextInt(min, max + 1)
    return YpExercise(left, right)
}

@Composable
fun PracticingAdditionYupanaScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current

    var currentDigitLevel by remember { mutableIntStateOf(MIN_DIGIT_LEVEL) }
    var exercise by remember { mutableStateOf(generateYpExercise(currentDigitLevel)) }
    var rows by remember { mutableStateOf(List(ROWS) { YpRowState() }) }
    var stepRowIdx by remember { mutableIntStateOf(-1) }
    var stepCompleted by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var showLastLevelMessage by remember { mutableStateOf(false) }
    var finalCongratsShown by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    fun updateDisplay() {
        val l = exercise.left
        val r = exercise.right
        val lDigits = numberToDigits(l)
        val rDigits = numberToDigits(r)
        val sum = l + r
        val sumDigits = numberToDigits(sum)

        rows = List(ROWS) { i ->
            YpRowState(
                leftDigit = lDigits[i],
                rightDigit = rDigits[i],
                resultDigit = sumDigits[i]
            )
        }
    }

    LaunchedEffect(exercise) { updateDisplay() }

    fun resetExercise() {
        exercise = generateYpExercise(currentDigitLevel)
        stepRowIdx = -1
        stepCompleted = false
        feedbackMessage = ""
    }

    fun toggleLevel() {
        if (currentDigitLevel == MAX_DIGIT_LEVEL && !showLastLevelMessage) {
            showLastLevelMessage = true
            feedbackMessage = s.ypLastLevelMessage
            return
        }
        showLastLevelMessage = false
        currentDigitLevel = if (currentDigitLevel >= MAX_DIGIT_LEVEL) MIN_DIGIT_LEVEL else currentDigitLevel + 1
        resetExercise()
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back)
                    }
                    Text(
                        text = s.practicingWithYupana,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "${s.levelPrefix}$currentDigitLevel",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF2E241F),
                ) {
                    Text(
                        text = "${exercise.left} + ${exercise.right} = ?",
                        color = Color(0xFFF2ECD8),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .aspectRatio(860f / 480f)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawYupanaBackground(size)
                        drawYupanaFrame(size)
                        val margin = 28f / 860f * size.width
                        val usableWidth = size.width - 2f * margin
                        val rowHeight = (size.height - 48f / 480f * size.height) / ROWS
                        val colW = usableWidth / 4f
                        val startX = margin
                        val startY = 46f / 480f * size.height

                        for (row in 0 until ROWS) {
                            val ry = startY + row * rowHeight
                            val rowState = rows.getOrNull(row) ?: YpRowState()

                            val leftMarkers = getMarkersForDigit(rowState.leftDigit)
                            val rightMarkers = getMarkersForDigit(rowState.rightDigit)
                            val resultMarkers = getMarkersForDigit(
                                if (row >= ROWS - 1 - stepRowIdx) rowState.resultDigit else 0
                            )

                            drawYupanaRow(
                                cellOriginX = startX,
                                cellOriginY = ry,
                                cellWidth = colW,
                                cellHeight = rowHeight,
                                canvasSize = size,
                                leftMarkers = leftMarkers,
                                rightMarkers = rightMarkers,
                                resultMarkers = resultMarkers
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF2E241F),
                ) {
                    Text(
                        text = "${exercise.left} + ${exercise.right} = ${exercise.left + exercise.right}",
                        color = Color(0xFFF2ECD8),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (stepRowIdx >= 0 && stepRowIdx < ROWS) {
                    val placeIdx = ROWS - 1 - stepRowIdx
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "${rows[placeIdx].leftDigit} + ${rows[placeIdx].rightDigit} = ${rows[placeIdx].resultDigit} (${placeLabels[placeIdx]})",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = s.yupana,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = s.yupanaValues,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = s.yupanaResult,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = s.moves,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }

                for (row in 0 until ROWS) {
                    val rs = rows[row]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "Row ${ROWS - row}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${rs.leftDigit} + ${rs.rightDigit}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${rs.resultDigit}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                stepRowIdx++
                                if (stepRowIdx == ROWS - 1) {
                                    stepCompleted = true
                                    feedbackMessage = s.ypPerfectMessage.format(exercise.left, exercise.right, exercise.left + exercise.right)
                                    if (currentDigitLevel == MAX_DIGIT_LEVEL) finalCongratsShown = true
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ButtonYellow,
                                contentColor = OnButtonYellow
                            )
                        ) {
                            Text(
                                text = s.nextStep,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                        FilledTonalButton(
                            onClick = { resetExercise() },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ButtonYellow,
                                contentColor = OnButtonYellow
                            )
                        ) {
                            Text(
                                text = s.newExercise,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = { toggleLevel() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Text(
                            text = s.nextLevel,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                if (feedbackMessage.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = feedbackMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(48.dp))
            }
        }

        if (!finalCongratsShown) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 8.dp, start = 8.dp)
            ) {
                val uriHandler = LocalUriHandler.current
                val ctx = LocalContext.current

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { showSourcesMenu = true }
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Book,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = s.sources,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = showSourcesMenu && !showMainTextSubmenu,
                    onDismissRequest = { showSourcesMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(s.originalText) },
                        trailingIcon = {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                        },
                        onClick = { showMainTextSubmenu = true }
                    )
                }

                DropdownMenu(
                    expanded = showSourcesMenu && showMainTextSubmenu,
                    onDismissRequest = { showMainTextSubmenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(s.copyUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showMainTextSubmenu = false
                            val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=b0bb8da3-ca00-453e-9060-9dfa767c80e2"))
                            Toast.makeText(ctx, s.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showMainTextSubmenu = false
                            uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=b0bb8da3-ca00-453e-9060-9dfa767c80e2")
                        }
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawYupanaBackground(size: Size) {
    drawRect(color = Color(0xFFFEF5E0), size = size)
}

private fun DrawScope.drawYupanaFrame(size: Size) {
    drawRect(
        color = Color(0xFFB48B5A),
        topLeft = Offset(3f / 860f * size.width, 3f / 480f * size.height),
        size = Size(size.width - 6f / 860f * size.width, size.height - 6f / 480f * size.height),
        style = Stroke(width = 2f / 480f * size.height)
    )
}

private fun DrawScope.drawYupanaRow(
    cellOriginX: Float,
    cellOriginY: Float,
    cellWidth: Float,
    cellHeight: Float,
    canvasSize: Size,
    leftMarkers: Set<Int>,
    rightMarkers: Set<Int>,
    resultMarkers: Set<Int>,
) {
    val cw = canvasSize.width
    val ch = canvasSize.height

    drawRect(
        color = Color(0xFFFEF8E8),
        topLeft = Offset(cellOriginX, cellOriginY),
        size = Size(4f * cellWidth, cellHeight)
    )

    drawRect(
        color = Color(0xFFD4B87A),
        topLeft = Offset(cellOriginX, cellOriginY + cellHeight),
        size = Size(4f * cellWidth, 0.8f / 440f * ch),
    )

    val dotRadius = minOf(cellWidth * 0.18f, cellHeight * 0.18f, 9f / 860f * cw)
    val markerRadius = dotRadius * 0.9f
    val markerGap = cellHeight * 0.12f
    val extraPx = with(density) { 3.dp.toPx() }

    val dotPositionsByCol = listOf(
        listOf(
            Offset(-dotRadius * 1.5f, -dotRadius * 2f),
            Offset(-dotRadius * 1.5f, 0f),
            Offset(-dotRadius * 1.5f, dotRadius * 2f),
            Offset(dotRadius * 1.5f, -dotRadius * 0.8f - extraPx / 2f),
            Offset(dotRadius * 1.5f, dotRadius * 0.8f + extraPx / 2f),
        ),
        listOf(
            Offset(0f, -dotRadius * 1.8f),
            Offset(0f, 0f),
            Offset(0f, dotRadius * 1.8f),
        ),
        listOf(
            Offset(0f, -dotRadius * 1.2f),
            Offset(0f, dotRadius * 1.2f),
        ),
        listOf(
            Offset(0f, 0f),
        ),
    )

    for (col in 0..3) {
        val cx = cellOriginX + col * cellWidth + cellWidth / 2f
        val cy = cellOriginY + cellHeight / 2f
        val colNum = col + 1
        val dotPositions = dotPositionsByCol[col]

        val hasLeftMarker = colNum in leftMarkers
        val hasRightMarker = colNum in rightMarkers
        val hasResultMarker = colNum in resultMarkers

        val topEdge = cellOriginY + cellHeight * 0.08f
        val bottomEdge = cellOriginY + cellHeight * 0.92f

        val topMarkerY = topEdge + markerGap
        val bottomMarkerY = bottomEdge - markerGap

        val leftActive = hasLeftMarker
        val rightActive = hasRightMarker
        val resultActive = hasResultMarker

        if (leftActive) {
            val my = topMarkerY - extraPx
            drawCircle(
                color = Color(0xFFC0392B),
                radius = markerRadius,
                center = Offset(cx, my)
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.2f),
                radius = markerRadius,
                center = Offset(cx, my),
                style = Stroke(width = 0.8f / 480f * ch)
            )
        }

        if (rightActive) {
            val my = bottomMarkerY + extraPx
            drawCircle(
                color = Color(0xFF2980B9),
                radius = markerRadius,
                center = Offset(cx, my)
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.2f),
                radius = markerRadius,
                center = Offset(cx, my),
                style = Stroke(width = 0.8f / 480f * ch)
            )
        }

        if (resultActive) {
            val my = topMarkerY - extraPx
            drawCircle(
                color = Color(0xFF27AE60),
                radius = markerRadius,
                center = Offset(cx, my)
            )
            drawCircle(
                color = Color(0xFFA8E6C1).copy(alpha = 0.4f),
                radius = markerRadius * 0.7f,
                center = Offset(cx, my)
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.2f),
                radius = markerRadius,
                center = Offset(cx, my),
                style = Stroke(width = 0.8f / 480f * ch)
            )
        }

        val dotColor = when (col) {
            0 -> Color(0xFF6B3A1A)
            1 -> Color(0xFF5B2E12)
            2 -> Color(0xFF4A2210)
            3 -> Color(0xFF3A1808)
            else -> Color.Gray
        }

        for (pos in dotPositions) {
            val dotCenter = Offset(cx + pos.x, cy + pos.y)
            drawCircle(
                color = dotColor,
                radius = dotRadius * 0.8f,
                center = dotCenter
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.15f),
                radius = dotRadius * 0.8f,
                center = dotCenter,
                style = Stroke(width = 0.6f / 440f * ch)
            )
        }
    }
}
