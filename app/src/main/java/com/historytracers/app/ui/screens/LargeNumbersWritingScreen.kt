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
import androidx.compose.foundation.shape.RoundedCornerShape
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

private const val COLUMNS = 9
private const val SOROBAN_UPPER = 1
private const val SOROBAN_LOWER = 4
private const val SUANPAN_UPPER = 2
private const val SUANPAN_LOWER = 5

private data class LnColumnState(val upper: Int = 0, val lower: Int = 0) {
    val value: Int get() = (upper * 5 + lower).coerceIn(0, 9)
    fun normalize(): LnColumnState {
        val d = (upper * 5 + lower).coerceIn(0, 9)
        return LnColumnState(upper = d / 5, lower = d % 5)
    }
}

private fun LnValue(state: List<LnColumnState>): Long {
    var result = 0L
    for (col in state) {
        result = result * 10 + col.value
    }
    return result
}

private fun getLevelRange(level: Int): LongRange {
    var start = 1L
    repeat(level) { start *= 10L }
    return start..(start * 10L - 1L)
}

@Composable
fun LargeNumbersWritingScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    var currentLevel by remember { mutableStateOf(1) }
    val state = remember { mutableStateOf(List(COLUMNS) { LnColumnState() }) }
    val targetValue = remember { mutableStateOf(getLevelRange(1).random()) }
    val showCongrats = remember { mutableStateOf(false) }
    var completedLevel by remember { mutableStateOf(0) }
    val currentValue = LnValue(state.value)
    var stepCompleted by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var isFeedbackPositive by remember { mutableStateOf(false) }
    var showLastLevelMessage by remember { mutableStateOf(false) }
    var hasInteracted by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    var showAPalSubmenu by remember { mutableStateOf(false) }
    var showTomokoSubmenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }

    fun advanceLevel() {
        if (currentLevel == 8 && !showLastLevelMessage) {
            showLastLevelMessage = true
            feedbackMessage = s.mw2LastLevelMessage
            isFeedbackPositive = true
            return
        }
        showLastLevelMessage = false
        currentLevel = if (currentLevel >= 8) 1 else currentLevel + 1
        state.value = List(COLUMNS) { LnColumnState() }
        targetValue.value = getLevelRange(currentLevel).random()
        showCongrats.value = false
        completedLevel = 0
        stepCompleted = false
        feedbackMessage = ""
        isFeedbackPositive = false
        hasInteracted = false
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
                        text = s.largeNumbers,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = s.largeNumbersWritingInstruction,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var isSoroban by remember { mutableStateOf(true) }

                Text(
                    text = "${s.level}: $currentLevel/8",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    FilledIconButton(
                        onClick = { isSoroban = true },
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(24),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isSoroban) ButtonYellow else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "S",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSoroban) OnButtonYellow else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = s.sorobanMode,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    FilledIconButton(
                        onClick = { isSoroban = false },
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(24),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (!isSoroban) ButtonYellow else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "S",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (!isSoroban) OnButtonYellow else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = s.suanpanMode,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                val upperMax = if (isSoroban) SOROBAN_UPPER else SUANPAN_UPPER
                val lowerMax = if (isSoroban) SOROBAN_LOWER else SUANPAN_LOWER

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .aspectRatio(860f / 400f)
                        .pointerInput(upperMax, lowerMax, stepCompleted) {
                            detectTapGestures { offset ->
                                if (stepCompleted) return@detectTapGestures
                                hasInteracted = true
                                val cw = size.width.toFloat()
                                val ch = size.height.toFloat()
                                handleAbacusTap(
                                    offset.x, offset.y, cw, ch, state,
                                    COLUMNS, upperMax, lowerMax
                                )
                            }
                        }
                ) {
                    drawAbacusBackground(size)
                    drawAbacusFrame(size)
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
                        drawAbacusRod(
                            cx = startX + col * colWidth,
                            canvasWidth = size.width,
                            canvasHeight = size.height
                        )
                        drawColumnBeads(
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

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(40.dp),
                        color = Color(0xFF2E241F),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = s.value,
                                color = Color(0xFFF2ECD8),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp)
                            )
                            Text(
                                text = "$currentValue",
                                color = Color(0xFFF2ECD8),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(40.dp),
                        color = Color(0xFFFFF9E6),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = s.write,
                                color = Color(0xFF2E241F),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp)
                            )
                            Text(
                                text = "${targetValue.value}",
                                color = Color(0xFF2E241F),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = {
                            state.value = List(COLUMNS) { LnColumnState() }
                            showCongrats.value = false
                            completedLevel = 0
                            stepCompleted = false
                            feedbackMessage = ""
                            isFeedbackPositive = false
                            showLastLevelMessage = false
                            hasInteracted = false
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Text(
                            text = s.reset,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            state.value = List(COLUMNS) { LnColumnState() }
                            targetValue.value = getLevelRange(currentLevel).random()
                            showCongrats.value = false
                            completedLevel = 0
                            stepCompleted = false
                            feedbackMessage = ""
                            isFeedbackPositive = false
                            showLastLevelMessage = false
                            hasInteracted = false
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Text(
                            text = s.newExercise,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                if (!hasInteracted || stepCompleted) {
                    Spacer(Modifier.height(8.dp))
                    FilledTonalButton(
                        onClick = { advanceLevel() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Text(
                            text = s.nextLevel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

        if (currentValue == targetValue.value && !showCongrats.value) {
            showCongrats.value = true
            completedLevel = currentLevel
            onScoreChanged(currentScore + 2)
            scope.launch { preferences.recordLessonCompletion() }
            scope.launch { preferences.markAbacusSectionCompleted("large_numbers_writing") }
            stepCompleted = true
            feedbackMessage = s.feedbackCorrect
            isFeedbackPositive = true
        }

        if (showCongrats.value && completedLevel > 0 && !showLastLevelMessage || feedbackMessage.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showCongrats.value && completedLevel > 0 && !showLastLevelMessage) {
                    val congratsText = if (completedLevel == 8) {
                        "${s.levelCompleteMax} \uD83C\uDF89\uD83C\uDF89"
                    } else {
                        "${s.levelComplete.format(completedLevel)} \uD83C\uDF89"
                    }
                    Text(
                        text = congratsText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }
                if (feedbackMessage.isNotEmpty()) {
                    Text(
                        text = feedbackMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isFeedbackPositive) Color(0xFF2E7D32) else Color(0xFFC62828),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp, start = 8.dp)
        ) {
            val uriHandler = LocalUriHandler.current
            val context = LocalContext.current

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
                expanded = showSourcesMenu && !showMainTextSubmenu && !showAPalSubmenu && !showTomokoSubmenu,
                onDismissRequest = { showSourcesMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(s.originalText) },
                    trailingIcon = {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    },
                    onClick = { showMainTextSubmenu = true }
                )
                DropdownMenuItem(
                    text = { Text(s.aPal) },
                    trailingIcon = {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    },
                    onClick = { showAPalSubmenu = true }
                )
                DropdownMenuItem(
                    text = { Text(s.tomokoHoult) },
                    trailingIcon = {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    },
                    onClick = { showTomokoSubmenu = true }
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
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=4dc32200-392c-4d20-ae2d-0089b3c288bb"))
                        Toast.makeText(context, s.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=4dc32200-392c-4d20-ae2d-0089b3c288bb")
                    }
                )
            }

            DropdownMenu(
                expanded = showSourcesMenu && showAPalSubmenu,
                onDismissRequest = { showAPalSubmenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(s.copyUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showAPalSubmenu = false
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.apalconnect.org/wp-content/uploads/2018/12/Chinese-Abacus-Introduction.pdf"))
                        Toast.makeText(context, s.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showAPalSubmenu = false
                        uriHandler.openUri("https://www.apalconnect.org/wp-content/uploads/2018/12/Chinese-Abacus-Introduction.pdf")
                    }
                )
            }

            DropdownMenu(
                expanded = showSourcesMenu && showTomokoSubmenu,
                onDismissRequest = { showTomokoSubmenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(s.copyUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showTomokoSubmenu = false
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.youtube.com/watch?v=-br2yp3tQ1M"))
                        Toast.makeText(context, s.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showTomokoSubmenu = false
                        uriHandler.openUri("https://www.youtube.com/watch?v=-br2yp3tQ1M")
                    }
                )
            }
        }
    }
}

private fun handleAbacusTap(
    x: Float, y: Float,
    cw: Float, ch: Float,
    state: MutableState<List<LnColumnState>>,
    columns: Int,
    upperMax: Int,
    lowerMax: Int
) {
    val margin = 28f / 860f * cw
    val usableWidth = cw - 2f * margin
    val colW = usableWidth / columns
    val startX = margin + colW / 2f
    val beamY = ch / 2f
    val bR = minOf(colW * 0.38f, 10f / 400f * ch, 10f / 860f * cw)
    val dtt = beamY - 28f / 400f * ch
    val dtb = beamY + 28f / 400f * ch

    var colHit = -1
    for (i in 0 until columns) {
        if (abs(x - (startX + i * colW)) < colW * 0.45f) {
            colHit = i
            break
        }
    }

    if (colHit < 0) return

    val cx = startX + colHit * colW
    var handled = false

    for (bi in 0 until upperMax) {
        val activeY = dtt - 6f / 400f * ch - bi * 22f / 400f * ch
        val inactiveY = dtt - 38f / 400f * ch - bi * 22f / 400f * ch
        val beadY = if (bi < state.value[colHit].upper) activeY else inactiveY
        if (sqrt((x - cx) * (x - cx) + (y - beadY) * (y - beadY)) < bR + 8f / 400f * ch && y < dtt - 2f / 400f * ch) {
            val cur = state.value[colHit].upper
            val newUpper = if (bi < cur) bi else bi + 1
            state.value = state.value.toMutableList().also {
                it[colHit] = it[colHit].copy(upper = newUpper.coerceIn(0, upperMax)).normalize()
            }
            handled = true
            break
        }
    }

    if (!handled) {
        for (bi in 0 until lowerMax) {
            val activeY = dtb + 8f / 400f * ch + bi * 22f / 400f * ch
            val inactiveY = activeY + 28f / 400f * ch
            val beadY = if (bi < state.value[colHit].lower) activeY else inactiveY
            if (sqrt((x - cx) * (x - cx) + (y - beadY) * (y - beadY)) < bR + 8f / 400f * ch && y > dtb + 2f / 400f * ch) {
                val cur = state.value[colHit].lower
                val newLower = if (bi < cur) bi else bi + 1
                state.value = state.value.toMutableList().also {
                    it[colHit] = it[colHit].copy(
                        lower = newLower.coerceIn(0, lowerMax)
                    ).normalize()
                }
                break
            }
        }
    }
}

private fun DrawScope.drawAbacusBackground(size: androidx.compose.ui.geometry.Size) {
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

private fun DrawScope.drawAbacusFrame(size: androidx.compose.ui.geometry.Size) {
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

private fun DrawScope.drawAbacusRod(cx: Float, canvasWidth: Float, canvasHeight: Float) {
    drawLine(
        color = Color(0xFFB08054),
        start = Offset(cx, 8f / 860f * canvasWidth),
        end = Offset(cx, canvasHeight - 10f / 400f * canvasHeight),
        strokeWidth = 3f / 400f * canvasHeight
    )
}

private fun DrawScope.drawColumnBeads(
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
