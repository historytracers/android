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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.delay

private const val COLUMNS = 9
private const val SOROBAN_UPPER = 1
private const val SOROBAN_LOWER = 4
private const val SUANPAN_UPPER = 2
private const val SUANPAN_LOWER = 5
private const val MAX_STEPS = 10

private data class MtColumnState(val upper: Int = 0, val lower: Int = 0) {
    val value: Int get() = (upper * 5 + lower).coerceIn(0, 9)
    fun normalize(): MtColumnState {
        val d = (upper * 5 + lower).coerceIn(0, 9)
        return MtColumnState(upper = d / 5, lower = d % 5)
    }
}

private fun MtValue(state: List<MtColumnState>): Long {
    var result = 0L
    for (col in state) {
        result = result * 10 + col.value
    }
    return result
}

private fun setMtAbacusValue(
    state: MutableState<List<MtColumnState>>,
    schyotyBeads: MutableState<List<Int>>,
    abacusMode: String,
    value: Long
) {
    if (abacusMode == "schyoty") {
        val numStr = value.toString()
        val newBeads = MutableList(9) { 0 }
        for (i in numStr.length - 1 downTo 0) {
            val idx = numStr.length - 1 - i
            if (idx >= 9) break
            newBeads[idx] = numStr[i] - '0'
        }
        schyotyBeads.value = newBeads
    } else {
        val numStr = value.toString()
        val newState = MutableList(COLUMNS) { MtColumnState() }
        var colIdx = COLUMNS - 1
        for (i in numStr.length - 1 downTo 0) {
            if (colIdx < 0) break
            val digit = numStr[i] - '0'
            newState[colIdx] = MtColumnState(upper = digit / 5, lower = digit % 5)
            colIdx--
        }
        state.value = newState
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiplicationTableScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    var abacusMode by remember { mutableStateOf("soroban") }
    val schyotyBeads = remember { mutableStateOf(List(9) { 0 }) }
    val upperMax = if (abacusMode == "soroban") SOROBAN_UPPER else SUANPAN_UPPER
    val lowerMax = if (abacusMode == "soroban") SOROBAN_LOWER else SUANPAN_LOWER

    val state = remember { mutableStateOf(List(COLUMNS) { MtColumnState() }) }
    var selectedNumber by remember { mutableIntStateOf(1) }
    var currentStep by remember { mutableIntStateOf(0) }
    var isAutoPlaying by remember { mutableStateOf(false) }
    var finalCongratsShown by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    var showJessicaSubmenu by remember { mutableStateOf(false) }

    val currentResult = selectedNumber.toLong() * currentStep.toLong()
    val equationText = if (currentStep > 0) "$currentStep \u00D7 $selectedNumber = $currentResult" else ""

    fun reset() {
        currentStep = 0
        isAutoPlaying = false
        finalCongratsShown = false
        setMtAbacusValue(state, schyotyBeads, abacusMode, 0L)
    }

    fun stepForward() {
        if (currentStep < MAX_STEPS) {
            currentStep++
            val result = selectedNumber.toLong() * currentStep.toLong()
            setMtAbacusValue(state, schyotyBeads, abacusMode, result)
            if (currentStep >= MAX_STEPS) {
                finalCongratsShown = true
            }
        }
    }

    fun currentMtValue(): Long = if (abacusMode == "schyoty") {
        var v = 0L
        for (r in 0 until 9) {
            v += schyotyBeads.value[r] * Math.pow(10.0, r.toDouble()).toLong()
        }
        v
    } else {
        MtValue(state.value)
    }

    LaunchedEffect(finalCongratsShown) {
        if (finalCongratsShown) {
            preferences.recordLessonCompletion()
            preferences.markAbacusSectionCompleted("multiplication_table")
            onScoreChanged(currentScore + 2)
        }
    }

    LaunchedEffect(isAutoPlaying) {
        if (!isAutoPlaying) return@LaunchedEffect
        while (currentStep < MAX_STEPS && isAutoPlaying) {
            delay(1500)
            if (!isAutoPlaying) break
            stepForward()
            if (currentStep >= MAX_STEPS) {
                isAutoPlaying = false
            }
        }
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
                        text = s.misc.multiplicationTable,
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
                    text = s.misc.multiplicationTableDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    FilledIconButton(
                        onClick = {
                            abacusMode = "soroban"
                            state.value = List(COLUMNS) { MtColumnState() }
                        },
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(24),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (abacusMode == "soroban") ButtonYellow else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("S", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                            color = if (abacusMode == "soroban") OnButtonYellow else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(s.abacusWrite.sorobanMode, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                    FilledIconButton(
                        onClick = {
                            abacusMode = "suanpan"
                            state.value = List(COLUMNS) { MtColumnState() }
                        },
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(24),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (abacusMode == "suanpan") ButtonYellow else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("S", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                            color = if (abacusMode == "suanpan") OnButtonYellow else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(s.abacusWrite.suanpanMode, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                    FilledIconButton(
                        onClick = {
                            abacusMode = "schyoty"
                            schyotyBeads.value = List(9) { 0 }
                        },
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(24),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (abacusMode == "schyoty") ButtonYellow else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("Sc", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                            color = if (abacusMode == "schyoty") OnButtonYellow else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(s.abacusWrite.schyoty, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }

                if (abacusMode in listOf("soroban", "suanpan")) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .aspectRatio(860f / 400f)
                ) {
                    drawMtAbacusBackground(size)
                    drawMtAbacusFrame(size)
                    val margin = 28f / 860f * size.width
                    val usableWidth = size.width - 2f * margin
                    val colWidth = usableWidth / COLUMNS
                    val startX = margin + colWidth / 2f
                    val beamY = size.height / 2f
                    val ballRadius = minOf(
                        colWidth * 0.38f,
                        10f / 400f * size.height,
                        10f / 860f * size.width
                    )
                    val dtt = beamY - 28f / 400f * size.height
                    val dtb = beamY + 28f / 400f * size.height
                    for (col in 0 until COLUMNS) {
                        drawMtAbacusRod(
                            cx = startX + col * colWidth,
                            canvasWidth = size.width,
                            canvasHeight = size.height
                        )
                        drawMtColumnBeads(
                            cx = startX + col * colWidth,
                            canvasWidth = size.width,
                            canvasHeight = size.height,
                            ballRadius = ballRadius,
                            decimalTrackTop = dtt,
                            decimalTrackBottom = dtb,
                            upperCount = state.value[col].upper,
                            lowerCount = state.value[col].lower,
                            upperMax = upperMax,
                            lowerMax = lowerMax
                        )
                    }
                }
                } else {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .aspectRatio(640f / 360f)
                    ) {
                        val cw = size.width
                        val ch = size.height
                        val M = 14f / 480f * cw
                        val wireL = M
                        val wireR = cw - M
                        val areaH = ch - 2f * M
                        val rowSp = areaH / (9 + 1)
                        val beadR = minOf((wireR - wireL) / (10 * 2.6f), rowSp * 0.38f, 14f / 480f * cw)
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

                        for (r in 0 until 9) {
                            val y = M + rowSp * (9 - r)
                            drawLine(color = Color(0xFFB08054), start = Offset(wireL, y), end = Offset(wireR, y), strokeWidth = 2f)
                            drawLine(color = Color(0xFFE9C48B), start = Offset(wireL, y), end = Offset(wireR, y), strokeWidth = 1f)

                            val cnt = schyotyBeads.value[r]

                            for (p in 0 until cnt) {
                                val x = activeX0 + p * beadStep
                                drawMtSchyotyBead(x, y, beadR, active = true, idx = p)
                            }

                            for (p in 0 until 10 - cnt) {
                                val x = inactiveX0 - p * beadStep
                                drawMtSchyotyBead(x, y, beadR, active = false, idx = 9 - p)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(40.dp),
                    color = Color(0xFF2E241F),
                ) {
                    Text(
                        text = "${s.common.valuePrefix}${currentMtValue()}",
                        color = Color(0xFFF2ECD8),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (equationText.isNotEmpty()) {
                    Text(
                        text = equationText,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(s.common.number, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold)
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        FilledTonalButton(
                            onClick = { expanded = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("$selectedNumber", style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            for (n in 1..9) {
                                DropdownMenuItem(
                                    text = { Text("$n") },
                                    onClick = {
                                        selectedNumber = n
                                        expanded = false
                                        reset()
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                if (currentStep > 0 && currentStep <= MAX_STEPS) {
                    Text(
                        text = "${s.common.stepPrefix}${currentStep}/$MAX_STEPS",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(12.dp))

                val isRunning = currentStep > 0 && currentStep < MAX_STEPS
                val isComplete = currentStep >= MAX_STEPS

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isRunning || isComplete) {
                            FilledTonalButton(
                                onClick = { reset() },
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = ButtonYellow,
                                    contentColor = OnButtonYellow
                                )
                            ) {
                                Text(s.common.reset, style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                            }
                        }

                        if (!isComplete) {
                            FilledTonalButton(
                                onClick = {
                                    if (isAutoPlaying) {
                                        isAutoPlaying = false
                                    } else {
                                        isAutoPlaying = true
                                    }
                                },
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = ButtonYellow,
                                    contentColor = OnButtonYellow
                                )
                            ) {
                                Text(
                                    if (isAutoPlaying) s.common.stop else s.common.auto,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }

                    if (!isComplete) {
                        FilledTonalButton(
                            onClick = {
                                isAutoPlaying = false
                                stepForward()
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ButtonYellow,
                                contentColor = OnButtonYellow
                            )
                        ) {
                            Text(s.common.nextStep, style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }

                    if (isComplete) {
                        Text(
                            text = s.common.complete,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp, start = 8.dp)
        ) {
            val uriHandler = LocalUriHandler.current

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
                expanded = showSourcesMenu && !showMainTextSubmenu && !showJessicaSubmenu,
                onDismissRequest = { showSourcesMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(s.titles.jessicaAmarteifio) },
                    trailingIcon = {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    },
                    onClick = { showJessicaSubmenu = true }
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
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=8bf96824-262d-4a55-bd39-2dbb887c1dc0"))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=8bf96824-262d-4a55-bd39-2dbb887c1dc0")
                    }
                )
            }

            DropdownMenu(
                expanded = showSourcesMenu && showJessicaSubmenu,
                onDismissRequest = { showJessicaSubmenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(s.common.copyUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showJessicaSubmenu = false
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.researchgate.net/publication/373989506_FACILITATORS'_GUIDE_TO_THE_MS_II_A_MODIFIED_S'CHYOTY_ABACUS"))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showJessicaSubmenu = false
                        uriHandler.openUri("https://www.researchgate.net/publication/373989506_FACILITATORS'_GUIDE_TO_THE_MS_II_A_MODIFIED_S'CHYOTY_ABACUS")
                    }
                )
            }
        }
    }
}

private fun DrawScope.drawMtAbacusBackground(size: androidx.compose.ui.geometry.Size) {
    drawRect(color = Color(0xFFFEF5E0), size = size)
    val beamY = size.height / 2f
    val decimalTrackTop = beamY - 28f / 400f * size.height
    val decimalTrackBottom = beamY + 28f / 400f * size.height

    drawRect(
        color = Color(0xFFDAC894).copy(alpha = 0.4f),
        topLeft = Offset(5f / 860f * size.width, decimalTrackTop),
        size = androidx.compose.ui.geometry.Size(
            size.width - 10f / 860f * size.width,
            decimalTrackBottom - decimalTrackTop
        )
    )
    drawRect(
        color = Color(0xFFB59762),
        topLeft = Offset(6f / 860f * size.width, decimalTrackTop + 2f / 400f * size.height),
        size = androidx.compose.ui.geometry.Size(
            size.width - 12f / 860f * size.width,
            decimalTrackBottom - decimalTrackTop - 4f / 400f * size.height
        ),
        style = Stroke(width = 2f / 400f * size.height)
    )

    drawLine(
        color = Color(0xFFC9A05A),
        start = Offset(8f / 860f * size.width, beamY),
        end = Offset(size.width - 8f / 860f * size.width, beamY),
        strokeWidth = 3f / 400f * size.height
    )

    drawRect(
        color = Color(0xFFC9A86B),
        topLeft = Offset(5f / 860f * size.width, beamY - 6f / 400f * size.height),
        size = androidx.compose.ui.geometry.Size(
            size.width - 10f / 860f * size.width,
            12f / 400f * size.height
        )
    )
    drawRect(
        color = Color(0xFFE5C28E),
        topLeft = Offset(5f / 860f * size.width, beamY - 4f / 400f * size.height),
        size = androidx.compose.ui.geometry.Size(
            size.width - 10f / 860f * size.width,
            8f / 400f * size.height
        )
    )
    drawRect(
        color = Color(0xFFF5E2B0),
        topLeft = Offset(5f / 860f * size.width, beamY - 2f / 400f * size.height),
        size = androidx.compose.ui.geometry.Size(
            size.width - 10f / 860f * size.width,
            4f / 400f * size.height
        )
    )
}

private fun DrawScope.drawMtAbacusFrame(size: androidx.compose.ui.geometry.Size) {
    drawRect(
        color = Color(0xFFF9EEC7),
        topLeft = Offset(5f / 860f * size.width, 5f / 400f * size.height),
        size = androidx.compose.ui.geometry.Size(
            size.width - 10f / 860f * size.width,
            size.height - 10f / 400f * size.height
        ),
        style = Stroke(width = 2.5f / 400f * size.height)
    )
    drawRect(
        color = Color(0xFFB48B5A),
        topLeft = Offset(3f / 860f * size.width, 3f / 400f * size.height),
        size = androidx.compose.ui.geometry.Size(
            size.width - 6f / 860f * size.width,
            size.height - 6f / 400f * size.height
        ),
        style = Stroke(width = 1.8f / 400f * size.height)
    )
}

private fun DrawScope.drawMtAbacusRod(cx: Float, canvasWidth: Float, canvasHeight: Float) {
    drawLine(
        color = Color(0xFFB08054),
        start = Offset(cx, 8f / 860f * canvasWidth),
        end = Offset(cx, canvasHeight - 10f / 400f * canvasHeight),
        strokeWidth = 3f / 400f * canvasHeight
    )
}

private fun DrawScope.drawMtColumnBeads(
    cx: Float,
    canvasWidth: Float,
    canvasHeight: Float,
    ballRadius: Float,
    decimalTrackTop: Float,
    decimalTrackBottom: Float,
    upperCount: Int,
    lowerCount: Int,
    upperMax: Int,
    lowerMax: Int
) {
    for (i in 0 until upperMax) {
        val activeY = decimalTrackTop - 6f / 400f * canvasHeight - i * 22f / 400f * canvasHeight
        val inactiveY = decimalTrackTop - 38f / 400f * canvasHeight - i * 22f / 400f * canvasHeight
        val beadActive = i < upperCount
        val by = if (beadActive) activeY else inactiveY

        drawCircle(color = Color(0xFFC03A28), radius = ballRadius, center = Offset(cx, by))
        drawCircle(color = Color(0xFFF06A50), radius = ballRadius * 0.85f, center = Offset(cx, by))
        drawCircle(color = Color(0xFF4A2018), radius = ballRadius, center = Offset(cx, by), style = Stroke(width = 1.5f / 400f * canvasHeight))
        drawCircle(color = Color(0xFFFFEAD4), radius = 3f / 860f * canvasWidth, center = Offset(cx - 3f / 860f * canvasWidth, by - 3f / 400f * canvasHeight))
    }

    for (i in 0 until lowerMax) {
        val activeY = decimalTrackBottom + 8f / 400f * canvasHeight + i * 22f / 400f * canvasHeight
        val inactiveY = activeY + 28f / 400f * canvasHeight
        val beadActive = i < lowerCount
        val by = if (beadActive) activeY else inactiveY

        drawCircle(color = Color(0xFF3A6068), radius = ballRadius - 0.5f / 400f * canvasHeight, center = Offset(cx, by))
        drawCircle(color = Color(0xFF7DA0AE), radius = (ballRadius - 0.5f / 400f * canvasHeight) * 0.85f, center = Offset(cx, by))
        drawCircle(color = Color(0xFF1A3A3A), radius = ballRadius - 0.5f / 400f * canvasHeight, center = Offset(cx, by), style = Stroke(width = 1.2f / 400f * canvasHeight))
        drawCircle(color = Color(0xFFC8E2EC), radius = 2.5f / 860f * canvasWidth, center = Offset(cx - 2.5f / 860f * canvasWidth, by - 2.5f / 400f * canvasHeight))
    }
}

private fun DrawScope.drawMtSchyotyBead(x: Float, y: Float, r: Float, active: Boolean, idx: Int) {
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
