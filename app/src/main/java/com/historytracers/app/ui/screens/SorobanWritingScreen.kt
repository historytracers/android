// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlin.math.abs
import kotlin.math.sqrt

private const val COLUMNS = 9
private const val UPPER_MAX = 1
private const val LOWER_MAX = 4

private data class ColumnState(val upper: Boolean = false, val lower: Int = 0) {
    val value: Int get() = (if (upper) 5 else 0) + lower
}

private fun SorobanValue(state: List<ColumnState>): Long {
    var result = 0L
    for (col in state) {
        result = result * 10 + col.value
    }
    return result
}

@Composable
fun SorobanWritingScreen(
    onNavigateBack: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val state = remember { mutableStateOf(List(COLUMNS) { ColumnState() }) }

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
                    text = s.writingToSoroban,
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
            Spacer(Modifier.height(12.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .aspectRatio(860f / 400f)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val cw = size.width.toFloat()
                            val ch = size.height.toFloat()
                            val margin = 28f / 860f * cw
                            val usableWidth = cw - 2f * margin
                            val colW = usableWidth / COLUMNS
                            val startX = margin + colW / 2f
                            val beamY = ch / 2f
                            val bR = minOf(colW * 0.38f, 14f / 400f * ch, 14f / 860f * cw)
                            val dtt = beamY - 28f / 400f * ch
                            val dtb = beamY + 28f / 400f * ch
                            val x = offset.x
                            val y = offset.y

                            var colHit = -1
                            for (i in 0 until COLUMNS) {
                                if (abs(x - (startX + i * colW)) < colW * 0.45f) {
                                    colHit = i
                                    break
                                }
                            }

                            if (colHit < 0) return@detectTapGestures

                            val cx = startX + colHit * colW
                            var handled = false

                            for (bi in 0 until UPPER_MAX) {
                                val activeY = dtt - 6f / 400f * ch - bi * 14f / 400f * ch
                                val inactiveY = dtt - 38f / 400f * ch - bi * 11.2f / 400f * ch
                                val beadY = if (bi < (if (state.value[colHit].upper) 1 else 0)) activeY else inactiveY
                                if (sqrt((x - cx) * (x - cx) + (y - beadY) * (y - beadY)) < bR + 8f / 400f * ch && y < dtt - 2f / 400f * ch) {
                                    val cur = if (state.value[colHit].upper) 1 else 0
                                    val newUpper = if (bi < cur) bi else bi + 1
                                    state.value = state.value.toMutableList().also {
                                        it[colHit] = it[colHit].copy(upper = newUpper > 0)
                                    }
                                    handled = true
                                    break
                                }
                            }

                            if (!handled) {
                                for (bi in 0 until LOWER_MAX) {
                                    val activeY = dtb + 8f / 400f * ch + bi * 14f / 400f * ch
                                    val inactiveY = activeY + 28f / 400f * ch
                                    val beadY = if (bi < state.value[colHit].lower) activeY else inactiveY
                                    if (sqrt((x - cx) * (x - cx) + (y - beadY) * (y - beadY)) < bR + 8f / 400f * ch && y > dtb + 2f / 400f * ch) {
                                        val cur = state.value[colHit].lower
                                        val newLower = if (bi < cur) bi else bi + 1
                                        state.value = state.value.toMutableList().also {
                                            it[colHit] = it[colHit].copy(lower = newLower.coerceIn(0, LOWER_MAX))
                                        }
                                        break
                                    }
                                }
                            }
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val margin = 28f / 860f * canvasWidth
                val usableWidth = canvasWidth - 2f * margin
                val colWidth = usableWidth / COLUMNS
                val startX = margin + colWidth / 2f
                val beamY = canvasHeight / 2f
                val ballRadius = minOf(colWidth * 0.38f, 14f / 400f * canvasHeight, 14f / 860f * canvasWidth)
                val decimalTrackTop = beamY - 28f / 400f * canvasHeight
                val decimalTrackBottom = beamY + 28f / 400f * canvasHeight

                drawRect(color = Color(0xFFFEF5E0), size = size)

                drawRect(
                    color = Color(0xFFDAC894).copy(alpha = 0.4f),
                    topLeft = Offset(5f / 860f * canvasWidth, decimalTrackTop),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 10f / 860f * canvasWidth, decimalTrackBottom - decimalTrackTop)
                )
                drawRect(
                    color = Color(0xFFB59762),
                    topLeft = Offset(6f / 860f * canvasWidth, decimalTrackTop + 2f / 400f * canvasHeight),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 12f / 860f * canvasWidth, decimalTrackBottom - decimalTrackTop - 4f / 400f * canvasHeight),
                    style = Stroke(width = 2f / 400f * canvasHeight)
                )

                drawLine(
                    color = Color(0xFFC9A05A),
                    start = Offset(8f / 860f * canvasWidth, beamY),
                    end = Offset(canvasWidth - 8f / 860f * canvasWidth, beamY),
                    strokeWidth = 3f / 400f * canvasHeight
                )

                drawRect(
                    color = Color(0xFFC9A86B),
                    topLeft = Offset(5f / 860f * canvasWidth, beamY - 6f / 400f * canvasHeight),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 10f / 860f * canvasWidth, 12f / 400f * canvasHeight)
                )
                drawRect(
                    color = Color(0xFFE5C28E),
                    topLeft = Offset(5f / 860f * canvasWidth, beamY - 4f / 400f * canvasHeight),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 10f / 860f * canvasWidth, 8f / 400f * canvasHeight)
                )
                drawRect(
                    color = Color(0xFFF5E2B0),
                    topLeft = Offset(5f / 860f * canvasWidth, beamY - 2f / 400f * canvasHeight),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 10f / 860f * canvasWidth, 4f / 400f * canvasHeight)
                )

                for (col in 0 until COLUMNS) {
                    drawColumn(
                        cx = startX + col * colWidth,
                        canvasWidth = canvasWidth,
                        canvasHeight = canvasHeight,
                        ballRadius = ballRadius,
                        decimalTrackTop = decimalTrackTop,
                        decimalTrackBottom = decimalTrackBottom,
                        upperActive = state.value[col].upper,
                        lowerCount = state.value[col].lower
                    )
                }

                drawRect(
                    color = Color(0xFFF9EEC7),
                    topLeft = Offset(5f / 860f * canvasWidth, 5f / 400f * canvasHeight),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 10f / 860f * canvasWidth, canvasHeight - 10f / 400f * canvasHeight),
                    style = Stroke(width = 2.5f / 400f * canvasHeight)
                )
                drawRect(
                    color = Color(0xFFB48B5A),
                    topLeft = Offset(3f / 860f * canvasWidth, 3f / 400f * canvasHeight),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 6f / 860f * canvasWidth, canvasHeight - 6f / 400f * canvasHeight),
                    style = Stroke(width = 1.8f / 400f * canvasHeight)
                )
            }

            Spacer(Modifier.height(16.dp))

            val currentValue = SorobanValue(state.value)
            Surface(
                shape = RoundedCornerShape(40.dp),
                color = Color(0xFF2E241F),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "${s.value}: $currentValue",
                    color = Color(0xFFF2ECD8),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            FilledIconButton(
                onClick = {
                    state.value = List(COLUMNS) { ColumnState() }
                },
                modifier = Modifier.size(96.dp),
                shape = RoundedCornerShape(50),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = s.reset,
                    style = MaterialTheme.typography.titleMedium,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = s.resetDesc,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun DrawScope.drawColumn(
    cx: Float,
    canvasWidth: Float,
    canvasHeight: Float,
    ballRadius: Float,
    decimalTrackTop: Float,
    decimalTrackBottom: Float,
    upperActive: Boolean,
    lowerCount: Int
) {
    drawLine(
        color = Color(0xFFB08054),
        start = Offset(cx, 8f / 860f * canvasWidth),
        end = Offset(cx, canvasHeight - 10f / 400f * canvasHeight),
        strokeWidth = 3f / 400f * canvasHeight
    )

    for (i in 0 until UPPER_MAX) {
        val activeY = decimalTrackTop - 6f / 400f * canvasHeight - i * 14f / 400f * canvasHeight
        val inactiveY = decimalTrackTop - 38f / 400f * canvasHeight - i * 11.2f / 400f * canvasHeight
        val beadActive = i < (if (upperActive) 1 else 0)
        val by = if (beadActive) activeY else inactiveY

        drawCircle(color = Color(0xFFC03A28), radius = ballRadius, center = Offset(cx, by))
        drawCircle(color = Color(0xFFF06A50), radius = ballRadius * 0.85f, center = Offset(cx, by))
        drawCircle(color = Color(0xFF4A2018), radius = ballRadius, center = Offset(cx, by), style = Stroke(width = 1.5f / 400f * canvasHeight))
        drawCircle(color = Color(0xFFFFEAD4), radius = 3f / 860f * canvasWidth, center = Offset(cx - 3f / 860f * canvasWidth, by - 3f / 400f * canvasHeight))
    }

    for (i in 0 until LOWER_MAX) {
        val activeY = decimalTrackBottom + 8f / 400f * canvasHeight + i * 14f / 400f * canvasHeight
        val inactiveY = activeY + 28f / 400f * canvasHeight
        val beadActive = i < lowerCount
        val by = if (beadActive) activeY else inactiveY

        drawCircle(color = Color(0xFF3A6068), radius = ballRadius - 0.5f / 400f * canvasHeight, center = Offset(cx, by))
        drawCircle(color = Color(0xFF7DA0AE), radius = (ballRadius - 0.5f / 400f * canvasHeight) * 0.85f, center = Offset(cx, by))
        drawCircle(color = Color(0xFF1A3A3A), radius = ballRadius - 0.5f / 400f * canvasHeight, center = Offset(cx, by), style = Stroke(width = 1.2f / 400f * canvasHeight))
        drawCircle(color = Color(0xFFC8E2EC), radius = 2.5f / 860f * canvasWidth, center = Offset(cx - 2.5f / 860f * canvasWidth, by - 2.5f / 400f * canvasHeight))
    }
}
