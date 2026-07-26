// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.random.Random

private const val ROWS = 4
private const val MIN_MULTIPLIER = 1
private const val MAX_MULTIPLIER = 9

private val yupanaSelectors = listOf(
    -1, 4, 3, 2, 4, 1, 1, 1, 1, 1,
    -1, -1, -1, -1, 2, -1, 4, 3, 2, 2,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, 4
)

private data class MypExercise(val a: Int, val b: Int) {
    val expected: Int get() = a * b
}

private val placeLabels = listOf("thousands", "hundreds", "tens", "units")

private fun computeMarkerValue(markersByRow: List<Set<Int>>): Int {
    var total = 0
    for (row in markersByRow.indices) {
        var rowValue = 0
        for (col in markersByRow[row]) {
            rowValue += when (col) { 1 -> 5; 2 -> 3; 3 -> 2; 4 -> 1; else -> 0 }
        }
        if (rowValue > 9) return -1
        val place = ROWS - 1 - row
        total += rowValue * Math.pow(10.0, place.toDouble()).toInt()
    }
    return total
}

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

private fun generateMypExercise(multiplier: Int): MypExercise {
    val a = Random.nextInt(1, 10)
    return MypExercise(a, multiplier)
}

private fun numberToDigits(n: Int): List<Int> {
    val clamped = n.coerceIn(0, 9999)
    val s = clamped.toString().padStart(ROWS, '0')
    return s.map { it - '0' }
}

private fun buildMypSteps(exercise: MypExercise): List<Int> {
    val steps = mutableListOf<Int>()
    for (i in 1..exercise.b) {
        steps.add(exercise.a * i)
    }
    return steps
}

@Composable
fun PracticingMultiplicationYupanaScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var currentMultiplier by remember { mutableIntStateOf(MIN_MULTIPLIER) }
    var exercise by remember { mutableStateOf(generateMypExercise(currentMultiplier)) }
    var steps by remember { mutableStateOf(buildMypSteps(exercise)) }
    var currentStepIdx by remember { mutableIntStateOf(0) }
    var stepCompleted by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var isFeedbackPositive by remember { mutableStateOf(false) }
    var exerciseStarted by remember { mutableStateOf(false) }
    var showLastLevelMessage by remember { mutableStateOf(false) }
    var finalCongratsShown by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    var showDhavitPremSubmenu by remember { mutableStateOf(false) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var userMarkers by remember { mutableStateOf(List(ROWS) { emptySet<Int>() }) }

    fun checkStep(): Boolean {
        val target = steps[currentStepIdx]
        val currentValue = computeMarkerValue(userMarkers)
        return currentValue == target
    }

    fun toggleColumn(row: Int, col: Int) {
        if (stepCompleted) return
        if (!exerciseStarted) exerciseStarted = true
        userMarkers = userMarkers.toMutableList().also { list ->
            val current = list[row]
            list[row] = if (col in current) current - col else current + col
        }
        if (checkStep()) {
            stepCompleted = true
            if (currentStepIdx == steps.lastIndex) {
                finalCongratsShown = true
                onScoreChanged(currentScore + 2)
                scope.launch { preferences.recordLessonCompletion() }
                feedbackMessage = s.yupana.ypMultiplyPerfectMessage.format(exercise.a, exercise.b, exercise.expected)
                isFeedbackPositive = true
            } else {
                feedbackMessage = s.yupana.ypCorrectMessage
                isFeedbackPositive = true
            }
        }
    }

    fun resetExercise() {
        exercise = generateMypExercise(currentMultiplier)
        steps = buildMypSteps(exercise)
        currentStepIdx = 0
        stepCompleted = false
        feedbackMessage = ""
        isFeedbackPositive = false
        exerciseStarted = false
        finalCongratsShown = false
        showLastLevelMessage = false
        userMarkers = List(ROWS) { emptySet() }
    }

    fun resetCurrentExercise() {
        currentStepIdx = 0
        stepCompleted = false
        feedbackMessage = ""
        isFeedbackPositive = false
        exerciseStarted = false
        finalCongratsShown = false
        showLastLevelMessage = false
        userMarkers = List(ROWS) { emptySet() }
    }

    fun toggleLevel() {
        if (currentMultiplier == MAX_MULTIPLIER && !showLastLevelMessage) {
            showLastLevelMessage = true
            feedbackMessage = s.yupana.ypLastLevelMessage
            return
        }
        showLastLevelMessage = false
        currentMultiplier = if (currentMultiplier >= MAX_MULTIPLIER) MIN_MULTIPLIER else currentMultiplier + 1
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.common.back)
                    }
                    Text(
                        text = s.yupana.multiplyingWithYupana,
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
                    text = s.yupana.ypMultiplyInstruction,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${s.common.levelPrefix}$currentMultiplier",
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
                        text = "${exercise.a} \u00D7 ${exercise.b} = ?",
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
                        .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
                        .pointerInput(stepCompleted) {
                            if (!stepCompleted) {
                                detectTapGestures { offset ->
                                    val margin = 3f / 860f * canvasSize.width
                                    val usableWidth = canvasSize.width - 2f * margin
                                    val colW = usableWidth / 4f
                                    val startX = margin
                                    val rowHeight = (canvasSize.height - 6f / 480f * canvasSize.height) / ROWS
                                    val startY = 3f / 480f * canvasSize.height
                                    if (offset.x in startX..(startX + 4f * colW) && offset.y in startY..(startY + ROWS * rowHeight)) {
                                        val col = ((offset.x - startX) / colW).toInt().coerceIn(0, 3)
                                        val row = ((offset.y - startY) / rowHeight).toInt().coerceIn(0, ROWS - 1)
                                        toggleColumn(row, col + 1)
                                    }
                                }
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawYpMultiplyBackground(size)
                        drawYpMultiplyFrame(size)
                        val margin = 3f / 860f * size.width
                        val usableWidth = size.width - 2f * margin
                        val rowHeight = (size.height - 6f / 480f * size.height) / ROWS
                        val colW = usableWidth / 4f
                        val startX = margin
                        val startY = 3f / 480f * size.height

                        for (row in 0 until ROWS) {
                            val ry = startY + row * rowHeight
                            drawYpMultiplyRow(
                                cellOriginX = startX,
                                cellOriginY = ry,
                                cellWidth = colW,
                                cellHeight = rowHeight,
                                canvasSize = size,
                                markers = userMarkers.getOrElse(row) { emptySet() }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (currentStepIdx in steps.indices) {
                    val target = steps[currentStepIdx]
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF2E241F),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        val stepText = if (currentStepIdx == 0)
                            s.yupana.ypMultiplyStepPlace.format(currentStepIdx + 1, steps.size, exercise.a)
                        else
                            s.yupana.ypMultiplyStepAdd.format(currentStepIdx + 1, steps.size, exercise.a, target)
                        Text(
                            text = stepText,
                            color = Color(0xFFF2ECD8),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (currentStepIdx in steps.indices) {
                    val target = steps[currentStepIdx]
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF2E241F),
                    ) {
                        Text(
                            text = "${s.common.value}: $target",
                            color = Color(0xFFF2ECD8),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(
                            onClick = { resetExercise() },
                            enabled = !exerciseStarted || finalCongratsShown,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ButtonYellow,
                                contentColor = OnButtonYellow
                            )
                        ) {
                            Text(
                                text = s.common.newExercise,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                        FilledTonalButton(
                            onClick = {
                                if (!finalCongratsShown && currentStepIdx < steps.lastIndex) {
                                    currentStepIdx++
                                    stepCompleted = false
                                    feedbackMessage = ""
                                    isFeedbackPositive = false
                                }
                            },
                            enabled = stepCompleted,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ButtonYellow,
                                contentColor = OnButtonYellow
                            )
                        ) {
                            Text(
                                text = s.common.nextStep,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (finalCongratsShown) {
                            FilledTonalButton(
                                onClick = { resetCurrentExercise() },
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = ButtonYellow,
                                    contentColor = OnButtonYellow
                                )
                            ) {
                                Text(
                                    text = s.common.reset,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                        FilledTonalButton(
                            onClick = { toggleLevel() },
                            enabled = !exerciseStarted || finalCongratsShown,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ButtonYellow,
                                contentColor = OnButtonYellow
                            )
                        ) {
                            Text(
                                text = s.common.nextLevel,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                if (feedbackMessage.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = feedbackMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isFeedbackPositive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
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
                        text = s.common.sources,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = showSourcesMenu && !showMainTextSubmenu && !showDhavitPremSubmenu,
                    onDismissRequest = { showSourcesMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Dhavit Prem") },
                        trailingIcon = {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                        },
                        onClick = { showDhavitPremSubmenu = true }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.originalText) },
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
                        text = { Text(s.common.copyUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showMainTextSubmenu = false
                            val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=9a7a978b-3fac-422c-8e41-3ef1a24e88f3"))
                            Toast.makeText(ctx, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showMainTextSubmenu = false
                            uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=9a7a978b-3fac-422c-8e41-3ef1a24e88f3")
                        }
                    )
                }

                DropdownMenu(
                    expanded = showSourcesMenu && showDhavitPremSubmenu,
                    onDismissRequest = { showDhavitPremSubmenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(s.common.copyUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showDhavitPremSubmenu = false
                            val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.researchgate.net/publication/334520917_TAWA_PUKLLAY_-_LA_ARITMETICA_INCA_DE_RECONOCIMIENTO_DE_FORMAS_Y_MOVIMIENTOS_OPERABLE_EN_PARALELO_Y_QUE_NO_REQUIERE_CALCULOS_NUMERICOS_MENTALES"))
                            Toast.makeText(ctx, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showDhavitPremSubmenu = false
                            uriHandler.openUri("https://www.researchgate.net/publication/334520917_TAWA_PUKLLAY_-_LA_ARITMETICA_INCA_DE_RECONOCIMIENTO_DE_FORMAS_Y_MOVIMIENTOS_OPERABLE_EN_PARALELO_Y_QUE_NO_REQUIERE_CALCULOS_NUMERICOS_MENTALES")
                        }
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawYpMultiplyBackground(size: Size) {
    drawRect(color = Color(0xFFFEF5E0), size = size)
}

private fun DrawScope.drawYpMultiplyFrame(size: Size) {
    drawRect(
        color = Color(0xFFB48B5A),
        topLeft = Offset(3f / 860f * size.width, 3f / 480f * size.height),
        size = Size(size.width - 6f / 860f * size.width, size.height - 6f / 480f * size.height),
        style = Stroke(width = 2f / 480f * size.height)
    )
}

private fun DrawScope.drawYpMultiplyRow(
    cellOriginX: Float,
    cellOriginY: Float,
    cellWidth: Float,
    cellHeight: Float,
    canvasSize: Size,
    markers: Set<Int>,
) {
    val cw = canvasSize.width
    val ch = canvasSize.height

    val cornerRadius = CornerRadius(6f / 860f * cw)
    val borderWidth = 1.2f / 480f * ch
    val shadowOffset = 2f / 480f * ch

    for (col in 0..3) {
        val cellLeft = cellOriginX + col * cellWidth
        val cellTop = cellOriginY

        drawRoundRect(
            color = Color(0xFF000000).copy(alpha = 0.1f),
            topLeft = Offset(cellLeft + shadowOffset, cellTop + shadowOffset),
            size = Size(cellWidth, cellHeight),
            cornerRadius = cornerRadius
        )

        drawRoundRect(
            color = Color(0xFFFEF8E8),
            topLeft = Offset(cellLeft, cellTop),
            size = Size(cellWidth, cellHeight),
            cornerRadius = cornerRadius
        )

        drawRoundRect(
            color = Color(0xFFB48B5A),
            topLeft = Offset(cellLeft, cellTop),
            size = Size(cellWidth, cellHeight),
            cornerRadius = cornerRadius,
            style = Stroke(width = borderWidth)
        )
    }

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

        val hasMarker = colNum in markers

        val topEdge = cellOriginY + cellHeight * 0.08f
        val bottomEdge = cellOriginY + cellHeight * 0.92f
        val topMarkerY = topEdge + markerGap

        if (hasMarker) {
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
