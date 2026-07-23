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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlin.math.min

private const val ROWS = 4

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

private enum class YpMode { VALUES, RESULT, STEP_BY_STEP }

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

@Composable
fun PracticingAdditionYupanaScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current

    var leftValue by remember { mutableStateOf("512") }
    var rightValue by remember { mutableStateOf("513") }
    var mode by remember { mutableStateOf(YpMode.VALUES) }
    var rows by remember { mutableStateOf(List(ROWS) { YpRowState() }) }
    var stepRowIdx by remember { mutableStateOf(-1) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun updateDisplay() {
        val l = leftValue.toIntOrNull()?.coerceIn(0, 4999) ?: 0
        val r = rightValue.toIntOrNull()?.coerceIn(0, 4999) ?: 0
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

    LaunchedEffect(Unit) { updateDisplay() }

    fun handleValues() { mode = YpMode.VALUES; stepRowIdx = -1; updateDisplay() }
    fun handleResult() { mode = YpMode.RESULT; stepRowIdx = -1; updateDisplay() }
    fun handleStepByStep() {
        mode = YpMode.STEP_BY_STEP
        stepRowIdx = -1
        updateDisplay()
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

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = leftValue,
                        onValueChange = {
                            val filtered = it.filter { c -> c.isDigit() }
                            if (filtered.length <= 4) leftValue = filtered
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        label = { Text("512") }
                    )
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = rightValue,
                        onValueChange = {
                            val filtered = it.filter { c -> c.isDigit() }
                            if (filtered.length <= 4) rightValue = filtered
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        label = { Text("513") }
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    FilledTonalButton(
                        onClick = { handleValues() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (mode == YpMode.VALUES) ButtonYellow else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (mode == YpMode.VALUES) OnButtonYellow else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = s.yupanaValues,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    FilledTonalButton(
                        onClick = { handleResult() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (mode == YpMode.RESULT) ButtonYellow else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (mode == YpMode.RESULT) OnButtonYellow else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = s.yupanaResult,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    FilledTonalButton(
                        onClick = { handleStepByStep() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (mode == YpMode.STEP_BY_STEP) ButtonYellow else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (mode == YpMode.STEP_BY_STEP) OnButtonYellow else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = s.yupanaStepByStep,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .aspectRatio(860f / 440f)
                ) {
                    drawYupanaBackground(size)
                    drawYupanaFrame(size)
                    val margin = 28f / 860f * size.width
                    val usableWidth = size.width - 2f * margin
                    val rowHeight = (size.height - 44f / 440f * size.height) / ROWS
                    val colW = usableWidth / 4f
                    val startX = margin
                    val startY = 42f / 440f * size.height

                    for (row in 0 until ROWS) {
                        val ry = startY + row * rowHeight
                        val rowState = rows.getOrNull(row) ?: YpRowState()

                        val leftMarkers = getMarkersForDigit(
                            if (mode == YpMode.RESULT) 0 else rowState.leftDigit
                        )
                        val rightMarkers = getMarkersForDigit(
                            if (mode == YpMode.RESULT) 0 else rowState.rightDigit
                        )
                        val resultMarkers = getMarkersForDigit(
                            when (mode) {
                                YpMode.RESULT -> rowState.resultDigit
                                YpMode.STEP_BY_STEP -> if (row >= ROWS - 1 - stepRowIdx) rowState.resultDigit else 0
                                YpMode.VALUES -> 0
                            }
                        )

                        drawYupanaRow(
                            cellOriginX = startX,
                            cellOriginY = ry,
                            cellWidth = colW,
                            cellHeight = rowHeight,
                            canvasSize = size,
                            leftMarkers = leftMarkers,
                            rightMarkers = rightMarkers,
                            resultMarkers = resultMarkers,
                            mode = mode
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF2E241F),
                ) {
                    val l = leftValue.toIntOrNull() ?: 0
                    val r = rightValue.toIntOrNull() ?: 0
                    Text(
                        text = "${l} + ${r} = ${l + r}",
                        color = Color(0xFFF2ECD8),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (mode == YpMode.STEP_BY_STEP && stepRowIdx >= 0 && stepRowIdx < ROWS) {
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
                            text = if (mode == YpMode.RESULT) "→" else "",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (mode == YpMode.STEP_BY_STEP) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (stepRowIdx < ROWS - 1) {
                            FilledTonalButton(
                                onClick = {
                                    if (stepRowIdx < ROWS - 1) stepRowIdx++
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
                        }
                        FilledTonalButton(
                            onClick = {
                                stepRowIdx = -1
                                handleValues()
                            },
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
                }

                Spacer(Modifier.height(48.dp))
            }
        }

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

private fun DrawScope.drawYupanaBackground(size: Size) {
    drawRect(color = Color(0xFFFEF5E0), size = size)
}

private fun DrawScope.drawYupanaFrame(size: Size) {
    drawRect(
        color = Color(0xFFB48B5A),
        topLeft = Offset(3f / 860f * size.width, 3f / 440f * size.height),
        size = Size(size.width - 6f / 860f * size.width, size.height - 6f / 440f * size.height),
        style = Stroke(width = 2f / 440f * size.height)
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
    mode: YpMode
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

    val dotRadius = minOf(cellWidth * 0.09f, cellHeight * 0.09f, 4.5f / 860f * cw)
    val markerRadius = dotRadius * 0.9f
    val markerGap = cellHeight * 0.12f

    val dotPositionsByCol = listOf(
        listOf(
            Offset(-dotRadius * 1.5f, -dotRadius * 2f),
            Offset(-dotRadius * 1.5f, 0f),
            Offset(-dotRadius * 1.5f, dotRadius * 2f),
            Offset(dotRadius * 1.5f, -dotRadius * 0.8f),
            Offset(dotRadius * 1.5f, dotRadius * 0.8f),
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

        val leftActive = when (mode) {
            YpMode.VALUES -> hasLeftMarker
            else -> false
        }
        val rightActive = when (mode) {
            YpMode.VALUES -> hasRightMarker
            else -> false
        }
        val resultActive = when (mode) {
            YpMode.RESULT, YpMode.STEP_BY_STEP -> hasResultMarker
            YpMode.VALUES -> false
        }

        if (leftActive) {
            drawCircle(
                color = Color(0xFFC0392B),
                radius = markerRadius,
                center = Offset(cx, topMarkerY)
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.2f),
                radius = markerRadius,
                center = Offset(cx, topMarkerY),
                style = Stroke(width = 0.8f / 440f * ch)
            )
        }

        if (rightActive) {
            drawCircle(
                color = Color(0xFF2980B9),
                radius = markerRadius,
                center = Offset(cx, bottomMarkerY)
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.2f),
                radius = markerRadius,
                center = Offset(cx, bottomMarkerY),
                style = Stroke(width = 0.8f / 440f * ch)
            )
        }

        if (resultActive) {
            drawCircle(
                color = Color(0xFF2E241F),
                radius = markerRadius,
                center = Offset(cx, topMarkerY)
            )
            drawCircle(
                color = Color(0xFFF2ECD8).copy(alpha = 0.3f),
                radius = markerRadius * 0.7f,
                center = Offset(cx, topMarkerY)
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.2f),
                radius = markerRadius,
                center = Offset(cx, topMarkerY),
                style = Stroke(width = 0.8f / 440f * ch)
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
