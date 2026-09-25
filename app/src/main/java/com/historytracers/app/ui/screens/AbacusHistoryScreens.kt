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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.ResponsiveImage
import com.historytracers.app.ui.components.TextRenderer
import com.historytracers.app.ui.features.abacusHistoryScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile
import kotlin.math.abs
import kotlin.math.sqrt
import kotlinx.coroutines.launch

private const val SMARTPHONE_GAME_FILE = "973b0f27-bd58-443b-a780-bd9e22aed8bd"
private const val SECTION_ID = "abacus_history"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"

private const val INTRO_ID = "d3a0ba4e-9989-45f3-abea-a02bd8192064"
private const val SUANPAN_ID = "7c9787e3-717a-4b6c-8bec-226b8714f316"
private const val SOROBAN_ID = "cb350f74-f3e4-4a32-91dc-fa1f35aeedb7"
private const val THINKING_ID = "ad646132-7831-49ba-b512-2518d2b3654d"
private const val SIMILARITIES_ID = "0132b702-2976-47c3-8f08-d219c4a77c48"
private const val SCHYOTY_ID = "98eb71ca-3dff-4495-a5a1-50dd40cc1b11"
private const val CALCULI_ID = "d1d2ec7a-36d2-4590-b3d8-0e6467e04cd3"
private const val CONCLUSION_ID = "46a3664d-3f9e-4026-a8aa-0d5f7ebb4b81"

private const val MARKER_SUANPAN = "abacus-suanpan"
private const val MARKER_SOROBAN = "abacus-soroban"
private const val MARKER_SCHYOTY = "abacus-schyoty"
private const val MARKER_CALCULI = "abacus-calculi"

private val calculiHeadings = listOf("(((I)))", "((I))", "(I)", "C", "X", "I")
private val calculiPlaces = listOf(100000L, 10000L, 1000L, 100L, 10L, 1L)

@Composable
fun AbacusHistoryIntroScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AbacusHistoryGameContent(
        contentId = INTRO_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AbacusHistorySuanpanScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AbacusHistoryGameContent(
        contentId = SUANPAN_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AbacusHistorySorobanScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AbacusHistoryGameContent(
        contentId = SOROBAN_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AbacusHistoryThinkingScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AbacusHistoryGameContent(
        contentId = THINKING_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AbacusHistorySimilaritiesScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AbacusHistoryGameContent(
        contentId = SIMILARITIES_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AbacusHistorySchyotyScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AbacusHistoryGameContent(
        contentId = SCHYOTY_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AbacusHistoryCalculiScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AbacusHistoryGameContent(
        contentId = CALCULI_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AbacusHistoryConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToAbacus: () -> Unit = {}
) {
    AbacusHistoryGameContent(
        contentId = CONCLUSION_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateToAbacus = onNavigateToAbacus
    )
}

private data class AbacusColumnState(val upper: Int = 0, val lower: Int = 0)

@Composable
private fun AbacusApp(
    columns: Int,
    upperMax: Int,
    lowerMax: Int,
    columnHeadings: List<String> = emptyList(),
    columnPlaces: List<Long>? = null,
    modifier: Modifier = Modifier
) {
    val s = LocalUiStrings.current
    var state by remember(columns) { mutableStateOf(List(columns) { AbacusColumnState() }) }

    val currentValue: Long = if (columnPlaces != null) {
        var sum = 0L
        for (i in 0 until columns) {
            sum += (state[i].upper * 5 + state[i].lower).toLong() * columnPlaces[i]
        }
        sum
    } else {
        var result = 0L
        for (col in state) {
            result = result * 10 + (col.upper * 5 + col.lower).coerceIn(0, 9)
        }
        result
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (columnHeadings.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                columnHeadings.forEachIndexed { index, heading ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = heading,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF40280F)
                        )
                        if (columnPlaces != null) {
                            Text(
                                text = columnPlaces[index].toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF7A4A24)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .aspectRatio(860f / 400f)
                .pointerInput(columns, upperMax, lowerMax) {
                    detectTapGestures { offset ->
                        val cw = size.width.toFloat()
                        val ch = size.height.toFloat()
                        val margin = 28f / 860f * cw
                        val usableWidth = cw - 2f * margin
                        val colWidth = usableWidth / columns
                        val startX = margin + colWidth / 2f
                        val beamY = ch / 2f - 30f / 400f * ch
                        val ballRadius = minOf(colWidth * 0.38f, 14f / 400f * ch, 14f / 860f * cw)
                        val trackTop = beamY - 28f / 400f * ch
                        val trackBottom = beamY + 28f / 400f * ch
                        val x = offset.x
                        val y = offset.y

                        var colHit = -1
                        for (i in 0 until columns) {
                            if (abs(x - (startX + i * colWidth)) < colWidth * 0.45f) {
                                colHit = i
                                break
                            }
                        }
                        if (colHit < 0) return@detectTapGestures

                        val cx = startX + colHit * colWidth
                        var handled = false

                        for (bi in 0 until upperMax) {
                            val activeY = trackTop - 6f / 400f * ch - bi * 22f / 400f * ch
                            val inactiveY = trackTop - 100f / 400f * ch - bi * 22f / 400f * ch
                            val beadY = if (bi < state[colHit].upper) activeY else inactiveY
                            if (sqrt((x - cx) * (x - cx) + (y - beadY) * (y - beadY)) < ballRadius + 8f / 400f * ch &&
                                y < trackTop - 2f / 400f * ch
                            ) {
                                val cur = state[colHit].upper
                                val newUpper = if (bi < cur) bi else bi + 1
                                state = state.toMutableList().also {
                                    it[colHit] = it[colHit].copy(upper = newUpper.coerceIn(0, upperMax))
                                }
                                handled = true
                                break
                            }
                        }

                        if (!handled) {
                            for (bi in 0 until lowerMax) {
                                val activeY = trackBottom + 8f / 400f * ch + bi * 22f / 400f * ch
                                val inactiveY = activeY + 87f / 400f * ch
                                val beadY = if (bi < state[colHit].lower) activeY else inactiveY
                                if (sqrt((x - cx) * (x - cx) + (y - beadY) * (y - beadY)) < ballRadius + 8f / 400f * ch &&
                                    y > trackBottom + 2f / 400f * ch
                                ) {
                                    val cur = state[colHit].lower
                                    val newLower = if (bi < cur) bi else bi + 1
                                    state = state.toMutableList().also {
                                        it[colHit] = it[colHit].copy(lower = newLower.coerceIn(0, lowerMax))
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
            val colWidth = usableWidth / columns
            val startX = margin + colWidth / 2f
            val beamY = canvasHeight / 2f - 30f / 400f * canvasHeight
            val ballRadius = minOf(colWidth * 0.38f, 14f / 400f * canvasHeight, 14f / 860f * canvasWidth)
            val trackTop = beamY - 28f / 400f * canvasHeight
            val trackBottom = beamY + 28f / 400f * canvasHeight

            drawRect(color = Color(0xFFFEF5E0), size = size)

            drawRect(
                color = Color(0xFFDAC894).copy(alpha = 0.4f),
                topLeft = Offset(5f / 860f * canvasWidth, trackTop),
                size = Size(canvasWidth - 14f / 860f * canvasWidth, trackBottom - trackTop)
            )
            drawRect(
                color = Color(0xFFB59762),
                topLeft = Offset(6f / 860f * canvasWidth, trackTop + 2f / 400f * canvasHeight),
                size = Size(canvasWidth - 12f / 860f * canvasWidth, trackBottom - trackTop - 4f / 400f * canvasHeight),
                style = Stroke(width = 2f / 400f * canvasHeight)
            )

            drawRect(
                color = Color(0xFFC9A86B),
                topLeft = Offset(5f / 860f * canvasWidth, beamY - 6f / 400f * canvasHeight),
                size = Size(canvasWidth - 14f / 860f * canvasWidth, 12f / 400f * canvasHeight)
            )
            drawRect(
                color = Color(0xFFE5C28E),
                topLeft = Offset(5f / 860f * canvasWidth, beamY - 4f / 400f * canvasHeight),
                size = Size(canvasWidth - 14f / 860f * canvasWidth, 8f / 400f * canvasHeight)
            )
            drawRect(
                color = Color(0xFFF5E2B0),
                topLeft = Offset(5f / 860f * canvasWidth, beamY - 2f / 400f * canvasHeight),
                size = Size(canvasWidth - 14f / 860f * canvasWidth, 4f / 400f * canvasHeight)
            )

            for (col in 0 until columns) {
                drawAbacusColumn(
                    cx = startX + col * colWidth,
                    canvasWidth = canvasWidth,
                    canvasHeight = canvasHeight,
                    ballRadius = ballRadius,
                    trackTop = trackTop,
                    trackBottom = trackBottom,
                    upperMax = upperMax,
                    lowerMax = lowerMax,
                    upperCount = state[col].upper,
                    lowerCount = state[col].lower
                )
            }

            drawRect(
                color = Color(0xFFF9EEC7),
                topLeft = Offset(5f / 860f * canvasWidth, 5f / 400f * canvasHeight),
                size = Size(canvasWidth - 14f / 860f * canvasWidth, canvasHeight - 14f / 400f * canvasHeight),
                style = Stroke(width = 2.5f / 400f * canvasHeight)
            )
            drawRect(
                color = Color(0xFFB48B5A),
                topLeft = Offset(3f / 860f * canvasWidth, 3f / 400f * canvasHeight),
                size = Size(canvasWidth - 6f / 860f * canvasWidth, canvasHeight - 6f / 400f * canvasHeight),
                style = Stroke(width = 1.8f / 400f * canvasHeight)
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(40.dp), color = Color(0xFF2E241F)) {
                Text(
                    text = "${s.common.value}: $currentValue",
                    color = Color(0xFFF2ECD8),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
            FilledTonalButton(
                onClick = { state = List(columns) { AbacusColumnState() } },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Text(
                    text = s.common.reset,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

private fun DrawScope.drawAbacusColumn(
    cx: Float,
    canvasWidth: Float,
    canvasHeight: Float,
    ballRadius: Float,
    trackTop: Float,
    trackBottom: Float,
    upperMax: Int,
    lowerMax: Int,
    upperCount: Int,
    lowerCount: Int
) {
    drawLine(
        color = Color(0xFFB08054),
        start = Offset(cx, 8f / 860f * canvasWidth),
        end = Offset(cx, canvasHeight - 14f / 400f * canvasHeight),
        strokeWidth = 3f / 400f * canvasHeight
    )

    for (i in 0 until upperMax) {
        val activeY = trackTop - 6f / 400f * canvasHeight - i * 22f / 400f * canvasHeight
        val inactiveY = trackTop - 100f / 400f * canvasHeight - i * 22f / 400f * canvasHeight
        val beadActive = i < upperCount
        val by = if (beadActive) activeY else inactiveY

        drawCircle(color = Color(0xFFC03A28), radius = ballRadius, center = Offset(cx, by))
        drawCircle(color = Color(0xFFF06A50), radius = ballRadius * 0.85f, center = Offset(cx, by))
        drawCircle(
            color = Color(0xFF4A2018),
            radius = ballRadius,
            center = Offset(cx, by),
            style = Stroke(width = 1.5f / 400f * canvasHeight)
        )
        drawCircle(
            color = Color(0xFFFFEAD4),
            radius = 3f / 860f * canvasWidth,
            center = Offset(cx - 3f / 860f * canvasWidth, by - 3f / 400f * canvasHeight)
        )
    }

    for (i in 0 until lowerMax) {
        val activeY = trackBottom + 8f / 400f * canvasHeight + i * 22f / 400f * canvasHeight
        val inactiveY = activeY + 87f / 400f * canvasHeight
        val beadActive = i < lowerCount
        val by = if (beadActive) activeY else inactiveY

        drawCircle(color = Color(0xFF3A6068), radius = ballRadius - 0.5f / 400f * canvasHeight, center = Offset(cx, by))
        drawCircle(color = Color(0xFF7DA0AE), radius = (ballRadius - 0.5f / 400f * canvasHeight) * 0.85f, center = Offset(cx, by))
        drawCircle(
            color = Color(0xFF1A3A3A),
            radius = ballRadius - 0.5f / 400f * canvasHeight,
            center = Offset(cx, by),
            style = Stroke(width = 1.2f / 400f * canvasHeight)
        )
        drawCircle(
            color = Color(0xFFC8E2EC),
            radius = 2.5f / 860f * canvasWidth,
            center = Offset(cx - 2.5f / 860f * canvasWidth, by - 2.5f / 400f * canvasHeight)
        )
    }
}

private const val SCHYOTY_ROWS = 9
private const val SCHYOTY_BEADS = 10

@Composable
private fun SchyotyApp(modifier: Modifier = Modifier) {
    val s = LocalUiStrings.current
    var beads by remember { mutableStateOf(List(SCHYOTY_ROWS) { 0 }) }

    fun totalValue(): Long {
        var value = 0L
        for (r in 0 until SCHYOTY_ROWS) {
            value += beads[r] * Math.pow(10.0, r.toDouble()).toLong()
        }
        return value
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .aspectRatio(640f / 360f)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val cw = size.width.toFloat()
                        val ch = size.height.toFloat()
                        val m = 14f
                        val wireL = m / 480f * cw
                        val wireR = cw - m / 480f * cw
                        val areaH = ch - 2f * m / 480f * cw
                        val rowSp = areaH / (SCHYOTY_ROWS + 1)
                        val beadR = minOf((wireR - wireL) / (SCHYOTY_BEADS * 2.6f), rowSp * 0.38f, 14f / 480f * cw)
                        val beadStep = beadR * 2f + beadR * 0.3f
                        val activeX0 = wireL + beadR
                        val inactiveX0 = wireR - beadR

                        for (r in 0 until SCHYOTY_ROWS) {
                            val y = m / 480f * cw + rowSp * (SCHYOTY_ROWS - r)
                            if (abs(offset.y - y) > beadR + 10f / 480f * cw) continue

                            val count = beads[r]

                            for (p in 0 until count) {
                                val x = activeX0 + p * beadStep
                                if (abs(offset.x - x) < beadR + 5f / 480f * cw) {
                                    beads = beads.toMutableList().also { it[r] = p }
                                    return@detectTapGestures
                                }
                            }

                            for (p in 0 until SCHYOTY_BEADS - count) {
                                val x = inactiveX0 - p * beadStep
                                if (abs(offset.x - x) < beadR + 5f / 480f * cw) {
                                    beads = beads.toMutableList().also { it[r] = SCHYOTY_BEADS - p }
                                    return@detectTapGestures
                                }
                            }
                        }
                    }
                }
        ) {
            val cw = size.width
            val ch = size.height
            val m = 14f / 480f * cw
            val wireL = m
            val wireR = cw - m
            val areaH = ch - 2f * m
            val rowSp = areaH / (SCHYOTY_ROWS + 1)
            val beadR = minOf((wireR - wireL) / (SCHYOTY_BEADS * 2.6f), rowSp * 0.38f, 14f / 480f * cw)
            val beadGap = beadR * 0.3f
            val beadStep = beadR * 2f + beadGap
            val activeX0 = wireL + beadR
            val inactiveX0 = wireR - beadR

            drawRect(color = Color(0xFFFEF5E0), size = size)

            drawRect(
                color = Color(0xFFB48B5A),
                topLeft = Offset(2f, 2f),
                size = Size(cw - 4f, ch - 4f),
                style = Stroke(width = 2f)
            )
            drawRect(
                color = Color(0xFFF9EEC7),
                topLeft = Offset(5f, 5f),
                size = Size(cw - 10f, ch - 10f),
                style = Stroke(width = 1.5f)
            )

            for (r in 0 until SCHYOTY_ROWS) {
                val y = m + rowSp * (SCHYOTY_ROWS - r)
                drawLine(color = Color(0xFFB08054), start = Offset(wireL, y), end = Offset(wireR, y), strokeWidth = 2f)
                drawLine(color = Color(0xFFE9C48B), start = Offset(wireL, y), end = Offset(wireR, y), strokeWidth = 1f)

                val count = beads[r]

                for (p in 0 until count) {
                    drawSchyotyBead(activeX0 + p * beadStep, y, beadR, active = true, idx = p)
                }

                for (p in 0 until SCHYOTY_BEADS - count) {
                    drawSchyotyBead(inactiveX0 - p * beadStep, y, beadR, active = false, idx = 9 - p)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(40.dp), color = Color(0xFF2E241F)) {
                Text(
                    text = "${s.common.value}: ${totalValue()}",
                    color = Color(0xFFF2ECD8),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
            FilledTonalButton(
                onClick = { beads = List(SCHYOTY_ROWS) { 0 } },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Text(
                    text = s.common.reset,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

private fun DrawScope.drawSchyotyBead(x: Float, y: Float, r: Float, active: Boolean, idx: Int) {
    val isSpecial = idx == 4 || idx == 5
    val baseColor = if (isSpecial) {
        if (active) Color(0xFF808080) else Color(0xFF606060)
    } else {
        if (active) Color(0xFFB08030) else Color(0xFF8A7050)
    }
    val highlightColor = if (isSpecial) {
        if (active) Color(0xFFD0D0D0) else Color(0xFFA0A0A0)
    } else {
        if (active) Color(0xFFF5C860) else Color(0xFFD4BC98)
    }
    val strokeColor = if (isSpecial) {
        if (active) Color(0xFF3A3A3A) else Color(0xFF2A2A2A)
    } else {
        if (active) Color(0xFF6A4A1A) else Color(0xFF5A4030)
    }

    drawCircle(color = baseColor, radius = r, center = Offset(x, y))
    drawCircle(color = highlightColor, radius = r * 0.85f, center = Offset(x, y))
    drawCircle(color = strokeColor, radius = r, center = Offset(x, y), style = Stroke(width = if (active) 1.5f else 1f))
    drawCircle(
        color = if (isSpecial) Color(0x99E6E6E6) else Color(0x99FFEBBE),
        radius = r * 0.25f,
        center = Offset(x - r * 0.25f, y - r * 0.25f)
    )
}

private fun smileEmoji(smile: String): String = when (smile) {
    "thinking", "think" -> "\uD83E\uDD14"
    "happy", "smile" -> "\uD83D\uDE0A"
    "nerd" -> "\uD83E\uDD13"
    "shocking", "surprise", "surprising" -> "\uD83D\uDE32"
    "party" -> "\uD83E\uDD73"
    "inlove", "loving" -> "\uD83D\uDE0D"
    else -> "\uD83D\uDE0A"
}

private fun sourceUrl(page: String): String =
    if (page.startsWith("index.html")) HISTORYTRACERS_ORIGIN + page else page

private fun containsMarker(text: String?, marker: String): Boolean =
    text?.contains("data-custom=\"$marker\"") == true

private fun hasImgSrc(text: String?): Boolean = text?.contains("<img") == true

@Composable
private fun AbacusHistoryGameContent(
    contentId: String,
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToAbacus: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = abacusHistoryScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }
    var game by remember { mutableStateOf<SMGameFile?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(language) {
        game = null
        error = null
        when (val result = repo.loadAndParse("$language/$SMARTPHONE_GAME_FILE")) {
            is ContentResult.SMGame -> game = result.data
            is ContentResult.Error -> error = result.message
            else -> error = s.common.unsupportedContentType
        }
    }

    val contentList = game?.content ?: emptyList()
    val content = contentList.firstOrNull { it.id == contentId }

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    fun award(points: Int) {
        if (points <= 0) return
        totalAwarded += points
        onScoreChanged(initialScore + totalAwarded)
    }

    var arrivalHandled by remember(contentId) { mutableStateOf(false) }

    LaunchedEffect(content) {
        val node = content
        if (node != null && !arrivalHandled) {
            arrivalHandled = true
            if (onNavigateToAbacus != null) {
                preferences.markAbacusSectionCompleted(SECTION_ID)
                preferences.recordLessonCompletion()
            }
            award(node.score)
            preferences.markArrivalAwarded(node.id)
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

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                game == null && error == null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                error != null -> Text(
                    text = "${s.common.error}: $error",
                    modifier = Modifier.padding(16.dp)
                )
                content == null -> Text(
                    text = "${s.common.error}: ${s.common.unsupportedContentType}",
                    modifier = Modifier.padding(16.dp)
                )
                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    content.text?.forEach { text ->
                        if (text == null) return@forEach
                        val html = text.text ?: ""
                        when {
                            containsMarker(html, MARKER_SUANPAN) -> AbacusApp(
                                columns = 9, upperMax = 2, lowerMax = 5
                            )
                            containsMarker(html, MARKER_SOROBAN) -> AbacusApp(
                                columns = 9, upperMax = 1, lowerMax = 4
                            )
                            containsMarker(html, MARKER_SCHYOTY) -> SchyotyApp()
                            containsMarker(html, MARKER_CALCULI) -> AbacusApp(
                                columns = 6,
                                upperMax = 1,
                                lowerMax = 4,
                                columnHeadings = calculiHeadings,
                                columnPlaces = calculiPlaces
                            )
                            text.format?.contains("markdown") == true -> MarkdownText(text = html)
                            hasImgSrc(html) -> ResponsiveImage(html = html, imgDesc = text.imgdesc)
                            else -> TextRenderer(text = text, repo = repo)
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    if (content.answer != null) {
                        AbacusHistoryAnswerSection(
                            content = content,
                            onAnswered = { points -> award(points) }
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onNavigatePrev != null) {
                            FilledTonalButton(
                                onClick = onNavigatePrev,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFF4CAF50),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(s.common.previous, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (onNavigateNext != null) {
                            FilledTonalButton(
                                onClick = onNavigateNext,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFF4CAF50),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(s.common.next, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    if (onNavigateToAbacus != null) {
                        Spacer(Modifier.height(16.dp))
                        FilledTonalButton(
                            onClick = onNavigateToAbacus,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = hts.abacus, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(48.dp))
                }
            }

            content?.takeIf { it.smile.isNotEmpty() }?.let { node ->
                Text(
                    text = smileEmoji(node.smile),
                    fontSize = 40.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 8.dp, end = 8.dp)
                )
            }

            content?.sourceMenu?.takeIf { it.isNotEmpty() }?.let { sources ->
                AbacusHistorySourcesMenu(
                    sources = sources,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun AbacusHistoryAnswerSection(
    content: SMGameContent,
    onAnswered: (Int) -> Unit
) {
    val s = LocalUiStrings.current
    val xs = abacusHistoryScreenStringsForLanguage(LocalAppLanguage.current)
    var selected by remember { mutableStateOf<String?>(null) }
    var hasSubmitted by remember { mutableStateOf(false) }
    var awarded by remember { mutableStateOf(false) }

    val correctAnswer = when (val answer = content.answer) {
        is Boolean -> answer
        is String -> answer.equals("yes", ignoreCase = true)
        else -> null
    }

    fun submit(answer: String) {
        selected = answer
        hasSubmitted = true
        if (!awarded) {
            awarded = true
            val answeredCorrectly = (answer == "yes") == correctAnswer
            val points = if (answeredCorrectly) content.score else content.score / 2
            onAnswered(points)
        }
    }

    Spacer(Modifier.height(16.dp))

    if (!hasSubmitted) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { submit("yes") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(s.common.yes)
            }
            Button(
                onClick = { submit("no") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(s.common.no)
            }
        }
    } else {
        val isCorrect = (selected == "yes") == correctAnswer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = if (isCorrect) "\uD83C\uDF89 ${s.common.correct} \uD83C\uDF89" else xs.wrongAnswerMessage,
                color = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            if (isCorrect) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = xs.scoreDoubledMessage,
                    color = Color(0xFF2E7D32),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AbacusHistorySourcesMenu(sources: List<HTSource>, modifier: Modifier = Modifier) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var showSourcesMenu by remember { mutableStateOf(false) }
    var activeSource by remember { mutableStateOf<HTSource?>(null) }

    Box(modifier = modifier.padding(bottom = 8.dp, start = 8.dp)) {
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
            expanded = showSourcesMenu && activeSource == null,
            onDismissRequest = { showSourcesMenu = false }
        ) {
            sources.forEach { source ->
                DropdownMenuItem(
                    text = { Text(source.text) },
                    trailingIcon = {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    },
                    onClick = { activeSource = source }
                )
            }
        }

        DropdownMenu(
            expanded = showSourcesMenu && activeSource != null,
            onDismissRequest = {
                showSourcesMenu = false
                activeSource = null
            }
        ) {
            activeSource?.let { source ->
                val url = sourceUrl(source.page)
                DropdownMenuItem(
                    text = { Text(s.common.copyUrl) },
                    onClick = {
                        showSourcesMenu = false
                        activeSource = null
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", url))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        activeSource = null
                        uriHandler.openUri(url)
                    }
                )
            }
        }
    }
}
