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
import kotlin.math.roundToInt
import kotlin.math.sqrt

private const val ROWS = 4

private data class YpRowState(
    val leftDigit: Int = 0,
    val rightDigit: Int = 0,
    val resultDigit: Int = 0
)

private data class YpExercise(val left: Int, val right: Int) {
    val sum: Int get() = left + right
}

private enum class YpMode { VALUES, RESULT, STEP_BY_STEP }

private val placeLabels = listOf("milhares", "centenas", "dezenas", "unidades")

private fun decomposeDigit(d: Int): List<Int> {
    var rem = d.coerceIn(0, 9)
    val caps = listOf(5, 3, 2, 1)
    val result = mutableListOf<Int>()
    for (cap in caps) {
        val fill = minOf(rem, cap)
        result.add(fill)
        rem -= fill
    }
    return result
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
                        .aspectRatio(860f / 380f)
                ) {
                    drawYupanaBackground(size)
                    drawYupanaFrame(size)
                    val margin = 24f / 860f * size.width
                    val usableWidth = size.width - 2f * margin
                    val rowHeight = (size.height - 40f / 380f * size.height) / ROWS
                    val colW = usableWidth / 4f
                    val startX = margin
                    val startY = 38f / 380f * size.height

                    for (row in 0 until ROWS) {
                        val ry = startY + row * rowHeight
                        val rowState = rows.getOrNull(row) ?: YpRowState()

                        val leftDecomp = decomposeDigit(if (mode == YpMode.RESULT) 0 else rowState.leftDigit)
                        val rightDecomp = decomposeDigit(if (mode == YpMode.RESULT) 0 else rowState.rightDigit)
                        val resultDecomp = decomposeDigit(
                            when (mode) {
                                YpMode.RESULT -> rowState.resultDigit
                                YpMode.STEP_BY_STEP -> if (row <= stepRowIdx) rowState.resultDigit else if (row == stepRowIdx + 1) 0 else 0
                                YpMode.VALUES -> 0
                            }
                        )

                        val isStepRow = mode == YpMode.STEP_BY_STEP && (row == stepRowIdx || (stepRowIdx < 0 && row == 0))

                        drawYupanaRow(
                            cellOriginX = startX,
                            cellOriginY = ry,
                            cellWidth = colW,
                            cellHeight = rowHeight,
                            canvasSize = size,
                            leftDecomp = leftDecomp,
                            rightDecomp = rightDecomp,
                            resultDecomp = resultDecomp,
                            mode = mode,
                            isStepRow = isStepRow
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
                    val displayRows = rows.map { it.leftDigit } + rows.map { it.rightDigit }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Row ${ROWS - stepRowIdx} (${placeLabels[stepRowIdx]}): ${rows[stepRowIdx].leftDigit} + ${rows[stepRowIdx].rightDigit} = ${rows[stepRowIdx].resultDigit}",
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
        topLeft = Offset(3f / 860f * size.width, 3f / 380f * size.height),
        size = Size(size.width - 6f / 860f * size.width, size.height - 6f / 380f * size.height),
        style = Stroke(width = 2f / 380f * size.height)
    )
}

private fun DrawScope.drawYupanaRow(
    cellOriginX: Float,
    cellOriginY: Float,
    cellWidth: Float,
    cellHeight: Float,
    canvasSize: Size,
    leftDecomp: List<Int>,
    rightDecomp: List<Int>,
    resultDecomp: List<Int>,
    mode: YpMode,
    isStepRow: Boolean
) {
    val cellDots = listOf(5, 3, 2, 1)
    val cw = canvasSize.width
    val ch = canvasSize.height

    val rowBg = if (isStepRow) Color(0xFFFFF3D6) else Color(0xFFFEF8E8)
    drawRect(
        color = rowBg,
        topLeft = Offset(cellOriginX, cellOriginY),
        size = Size(4f * cellWidth, cellHeight)
    )

    drawRect(
        color = Color(0xFFD4B87A),
        topLeft = Offset(cellOriginX, cellOriginY + cellHeight),
        size = Size(4f * cellWidth, 1f / 380f * ch),
    )

    for (col in 0..3) {
        val cx = cellOriginX + col * cellWidth + cellWidth / 2f
        val maxDots = cellDots[col]
        val filledLeft = if (mode == YpMode.VALUES) leftDecomp.getOrElse(col) { 0 } else 0
        val filledRight = if (mode == YpMode.VALUES) rightDecomp.getOrElse(col) { 0 } else 0
        val filledResult = if (mode != YpMode.VALUES) resultDecomp.getOrElse(col) { 0 } else 0

        val dotRadius = minOf(cellWidth * 0.12f, cellHeight * 0.12f, 6f / 860f * cw)

        val dotPositions = when (col) {
            0 -> listOf(
                Offset(-dotRadius * 1.5f, -dotRadius * 2.5f),
                Offset(-dotRadius * 1.5f, 0f),
                Offset(-dotRadius * 1.5f, dotRadius * 2.5f),
                Offset(dotRadius * 1.5f, -dotRadius * 1.2f),
                Offset(dotRadius * 1.5f, dotRadius * 1.2f),
            )
            1 -> listOf(
                Offset(0f, -dotRadius * 2.2f),
                Offset(0f, 0f),
                Offset(0f, dotRadius * 2.2f),
            )
            2 -> listOf(
                Offset(0f, -dotRadius * 1.5f),
                Offset(0f, dotRadius * 1.5f),
            )
            3 -> listOf(
                Offset(0f, 0f),
            )
            else -> emptyList()
        }

        val colColor = when (col) {
            0 -> Color(0xFF8B4513)
            1 -> Color(0xFF5B3A1A)
            2 -> Color(0xFF3A2510)
            3 -> Color(0xFF2A1A0A)
            else -> Color.Gray
        }

        for ((dotIdx, pos) in dotPositions.withIndex()) {
            val dotCenter = Offset(cx + pos.x, cellOriginY + cellHeight / 2f + pos.y)

            val leftFilled = dotIdx < filledLeft
            val rightFilled = dotIdx < filledRight
            val resultFilled = dotIdx < filledResult

            val isFilled = when (mode) {
                YpMode.VALUES -> leftFilled || rightFilled
                YpMode.RESULT -> resultFilled
                YpMode.STEP_BY_STEP -> resultFilled
            }

            val fillColor = when {
                mode == YpMode.VALUES && leftFilled && rightFilled -> Color(0xFF9B59B6)
                mode == YpMode.VALUES && leftFilled -> Color(0xFFC0392B)
                mode == YpMode.VALUES && rightFilled -> Color(0xFF2980B9)
                isFilled -> colColor
                else -> Color(0xFFE8DCC0)
            }

            if (isFilled || (mode == YpMode.VALUES && (leftFilled || rightFilled))) {
                drawCircle(
                    color = fillColor,
                    radius = dotRadius * 0.9f,
                    center = dotCenter
                )
                drawCircle(
                    color = Color(0xFF000000).copy(alpha = 0.15f),
                    radius = dotRadius * 0.9f,
                    center = dotCenter,
                    style = Stroke(width = 0.8f / 380f * ch)
                )
            } else {
                drawCircle(
                    color = Color(0xFFD4C4A0),
                    radius = dotRadius * 0.9f,
                    center = dotCenter,
                    style = Stroke(width = 1f / 380f * ch)
                )
            }
        }
    }
}
