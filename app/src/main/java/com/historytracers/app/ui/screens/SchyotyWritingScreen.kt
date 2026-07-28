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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
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
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

private const val ROWS = 9
private const val BEADS_PER_ROW = 10

@Composable
fun SchyotyWritingScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val beads = remember { mutableStateOf(List(ROWS) { 0 }) }
    val targetLevel = remember { mutableStateOf(0) }
    val targetValue = remember { mutableStateOf(targetNumber(0)) }
    val showCongrats = remember { mutableStateOf(false) }
    val showAllLevels = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    fun totalValue(): Long {
        var v = 0L
        for (r in 0 until ROWS) {
            v += beads.value[r] * Math.pow(10.0, r.toDouble()).toLong()
        }
        return v
    }

    fun newExercise() {
        beads.value = List(ROWS) { 0 }
        val lvl = targetLevel.value
        targetValue.value = targetNumber(lvl)
        showCongrats.value = false
        showAllLevels.value = false
    }

    fun nextLevel() {
        val nextLvl = (targetLevel.value + 1) % ROWS
        targetLevel.value = nextLvl
        beads.value = List(ROWS) { 0 }
        targetValue.value = targetNumber(nextLvl)
        showCongrats.value = false
        showAllLevels.value = false
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
                        text = s.abacusWrite.writingToSchyoty,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = s.abacusWrite.schyotyWritingInstruction,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                        .aspectRatio(480f / 360f)
                        .offset(y = (-60).dp)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val cw = size.width.toFloat()
                                val ch = size.height.toFloat()
                                val M = 14f
                                val wireL = M / 480f * cw
                                val wireR = (cw - M / 480f * cw)
                                val areaH = ch - 2f * M / 480f * cw
                                val rowSp = areaH / (ROWS + 1)
                                val beadR = minOf((wireR - wireL) / (BEADS_PER_ROW * 2.6f), rowSp * 0.38f, 15f / 480f * cw)
                                val beadStep = beadR * 2f + beadR * 0.3f
                                val activeX0 = wireL + beadR
                                val inactiveX0 = wireR - beadR

                                for (r in 0 until ROWS) {
                                    val y = M / 480f * cw + rowSp * (ROWS - r)
                                    if (abs(offset.y - y) > beadR + 10f / 480f * cw) continue

                                    val cnt = beads.value[r]

                                    for (p in 0 until cnt) {
                                        val x = activeX0 + p * beadStep
                                        if (abs(offset.x - x) < beadR + 5f / 480f * cw) {
                                            beads.value = beads.value.toMutableList().also { it[r] = p }
                                            return@detectTapGestures
                                        }
                                    }

                                    for (p in 0 until BEADS_PER_ROW - cnt) {
                                        val x = inactiveX0 - p * beadStep
                                        if (abs(offset.x - x) < beadR + 5f / 480f * cw) {
                                            beads.value = beads.value.toMutableList().also { it[r] = BEADS_PER_ROW - p }
                                            return@detectTapGestures
                                        }
                                    }
                                }
                            }
                        }
                ) {
                    val cw = size.width
                    val ch = size.height
                    val M = 14f / 480f * cw
                    val wireL = M
                    val wireR = cw - M
                    val areaH = ch - 2f * M
                    val rowSp = areaH / (ROWS + 1)
                    val beadR = minOf((wireR - wireL) / (BEADS_PER_ROW * 2.6f), rowSp * 0.38f, 15f / 480f * cw)
                    val beadGap = beadR * 0.3f
                    val beadStep = beadR * 2f + beadGap
                    val activeX0 = wireL + beadR
                    val inactiveX0 = wireR - beadR

                    drawRect(color = Color(0xFFFEF5E0), size = size)

                    drawRect(
                        color = Color(0xFFB48B5A),
                        topLeft = Offset(2f, 2f),
                        size = androidx.compose.ui.geometry.Size(cw - 4f, ch - 4f),
                        style = Stroke(width = 2f)
                    )
                    drawRect(
                        color = Color(0xFFF9EEC7),
                        topLeft = Offset(5f, 5f),
                        size = androidx.compose.ui.geometry.Size(cw - 10f, ch - 10f),
                        style = Stroke(width = 1.5f)
                    )

                    for (r in 0 until ROWS) {
                        val y = M + rowSp * (ROWS - r)
                        drawLine(color = Color(0xFFB08054), start = Offset(wireL, y), end = Offset(wireR, y), strokeWidth = 2f)
                        drawLine(color = Color(0xFFE9C48B), start = Offset(wireL, y), end = Offset(wireR, y), strokeWidth = 1f)

                        val cnt = beads.value[r]

                        for (p in 0 until cnt) {
                            val x = activeX0 + p * beadStep
                            drawSchyotyBead(x, y, beadR, active = true, idx = p)
                        }

                        for (p in 0 until BEADS_PER_ROW - cnt) {
                            val x = inactiveX0 - p * beadStep
                            drawSchyotyBead(x, y, beadR, active = false, idx = 9 - p)
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                Row(
                    modifier = Modifier.offset(y = (-120).dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(40.dp),
                        color = Color(0xFF2E241F),
                    ) {
                        Text(
                            text = "${s.common.value}: ${totalValue()}",
                            color = Color(0xFFF2ECD8),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(40.dp),
                        color = Color(0xFFFFF9E6),
                    ) {
                        Text(
                            text = "${s.common.write}: ${targetValue.value}",
                            color = Color(0xFF2E241F),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.offset(y = (-120).dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { newExercise() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Text(
                            text = s.common.newExercise,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = { nextLevel() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Text(
                            text = s.common.nextLevel,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }

        if (showAllLevels.value) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = s.abacusWrite.schyotyAllLevelsComplete,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        } else if (showCongrats.value) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\u2705 ${s.common.correct}!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = s.common.resetHint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
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
                text = s.common.sources,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = showSourcesMenu && !showMainTextSubmenu,
            onDismissRequest = { showSourcesMenu = false }
        ) {
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
                    clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=1aad8822-1ace-45fd-954e-833799836d1a"))
                    Toast.makeText(ctx, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                }
            )
            DropdownMenuItem(
                text = { Text(s.common.goToUrl) },
                onClick = {
                    showSourcesMenu = false
                    showMainTextSubmenu = false
                    uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=1aad8822-1ace-45fd-954e-833799836d1a")
                }
            )
        }
    }
}

if (totalValue() == targetValue.value.toLong() && !showCongrats.value && !showAllLevels.value) {
    if (targetLevel.value == ROWS - 1) {
        showAllLevels.value = true
        onScoreChanged(currentScore + 5)
        scope.launch { preferences.recordLessonCompletion() }
    } else {
        showCongrats.value = true
        onScoreChanged(currentScore + 2)
        scope.launch { preferences.recordLessonCompletion() }
    }
}
}

private fun targetNumber(level: Int): Int {
    val minV = Math.pow(10.0, level.toDouble()).toInt()
    val maxV = Math.pow(10.0, level + 1.0).toInt() - 1
    return Random.nextInt(minV, maxV + 1)
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
