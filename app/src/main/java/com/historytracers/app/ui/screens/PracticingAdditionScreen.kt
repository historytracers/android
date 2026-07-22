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
import com.historytracers.app.ui.UiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random


private const val COLUMNS = 9
private const val SOROBAN_UPPER = 1
private const val SOROBAN_LOWER = 4
private const val SUANPAN_UPPER = 2
private const val SUANPAN_LOWER = 5

private data class PaColumnState(val upper: Int = 0, val lower: Int = 0) {
    val value: Int get() = (upper * 5 + lower).coerceIn(0, 9)
    fun normalize(): PaColumnState {
        val d = (upper * 5 + lower).coerceIn(0, 9)
        return PaColumnState(upper = d / 5, lower = d % 5)
    }
}

private fun PaValue(state: List<PaColumnState>): Long {
    var result = 0L
    for (col in state) {
        result = result * 10 + col.value
    }
    return result
}

private data class Exercise(val a: Long, val b: Long) {
    val expected: Long get() = a + b
}

private data class StepInfo(val instruction: String, val targetValue: Long)

private sealed class Level(val maxDigits: Int) {
    data object Units : Level(1)
    data object Tens : Level(2)
    data object Hundreds : Level(3)
    data object Thousands : Level(4)
    data object TenThousands : Level(5)
    data object HundredThousands : Level(6)
    data object Millions : Level(7)
    data object TenMillions : Level(8)
}

private fun levelName(level: Level, s: UiStrings): String = when (level) {
    Level.Units -> s.levelUnits
    Level.Tens -> s.levelTens
    Level.Hundreds -> s.levelHundreds
    Level.Thousands -> s.levelThousands
    Level.TenThousands -> s.levelTenThousands
    Level.HundredThousands -> s.levelHundredThousands
    Level.Millions -> s.levelMillions
    Level.TenMillions -> s.levelTenMillions
}

private fun placeNames(level: Level, s: UiStrings): List<String> = when (level) {
    Level.Units -> listOf(s.placeUnits)
    Level.Tens -> listOf(s.placeUnits, s.placeTens)
    Level.Hundreds -> listOf(s.placeUnits, s.placeTens, s.placeHundreds)
    Level.Thousands -> listOf(s.placeUnits, s.placeTens, s.placeHundreds, s.placeThousands)
    Level.TenThousands -> listOf(s.placeUnits, s.placeTens, s.placeHundreds, s.placeThousands, s.placeTenThousands)
    Level.HundredThousands -> listOf(s.placeUnits, s.placeTens, s.placeHundreds, s.placeThousands, s.placeTenThousands, s.placeHundredThousands)
    Level.Millions -> listOf(s.placeUnits, s.placeTens, s.placeHundreds, s.placeThousands, s.placeTenThousands, s.placeHundredThousands, s.placeMillions)
    Level.TenMillions -> listOf(s.placeUnits, s.placeTens, s.placeHundreds, s.placeThousands, s.placeTenThousands, s.placeHundredThousands, s.placeMillions, s.placeTenMillions)
}

private val levels = listOf(
    Level.Units, Level.Tens, Level.Hundreds, Level.Thousands,
    Level.TenThousands, Level.HundredThousands, Level.Millions,
    Level.TenMillions
)

private fun generateNumbers(level: Level): Exercise {
    val (min, max) = when (level) {
        Level.Units -> 0L to 9L
        Level.Tens -> 1L to 99L
        Level.Hundreds -> 100L to 999L
        Level.Thousands -> 1000L to 9999L
        Level.TenThousands -> 10000L to 99999L
        Level.HundredThousands -> 100000L to 999999L
        Level.Millions -> 1000000L to 9999999L
        Level.TenMillions -> 10000000L to 99999999L
    }
    return Exercise(Random.nextLong(min, max + 1), Random.nextLong(min, max + 1))
}

private fun buildSteps(exercise: Exercise, level: Level, s: UiStrings): List<StepInfo> {
    val steps = mutableListOf<StepInfo>()
    val multiplier = when (level) {
        Level.Units -> listOf(1L)
        Level.Tens -> listOf(1L, 10L)
        Level.Hundreds -> listOf(1L, 10L, 100L)
        Level.Thousands -> listOf(1L, 10L, 100L, 1000L)
        Level.TenThousands -> listOf(1L, 10L, 100L, 1000L, 10000L)
        Level.HundredThousands -> listOf(1L, 10L, 100L, 1000L, 10000L, 100000L)
        Level.Millions -> listOf(1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L)
        Level.TenMillions -> listOf(1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L)
    }
    val pn = placeNames(level, s)
    val placeDescription = pn.reversed().joinToString(", ")

    steps.add(StepInfo(s.stepWriteFirst.format(exercise.a, placeDescription), exercise.a))

    var currentValue = exercise.a

    for (p in 0 until level.maxDigits) {
        val digitB = ((exercise.b / multiplier[p]) % 10).toInt()
        if (digitB == 0) continue

        val digitA = ((currentValue / multiplier[p]) % 10).toInt()
        val total = digitA + digitB

        if (total < 10) {
            currentValue += digitB * multiplier[p]
            steps.add(StepInfo(s.stepAddTo.format(pn[p], digitB, pn[p], currentValue), currentValue))
        } else {
            val complement = 10 - digitB
            val newValue = currentValue + (multiplier[p] * 10) - (complement * multiplier[p])
            val nextPlace = if (p + 1 < pn.size) pn[p + 1] else s.placeNext
            steps.add(StepInfo(s.stepCarrying.format(digitB, pn[p], digitA, digitB, total, complement, pn[p], nextPlace, newValue), newValue))
            currentValue = newValue
        }
    }

    steps.add(StepInfo(s.stepFinal.format(exercise.a, exercise.b, exercise.expected), exercise.expected))
    return steps
}

@Composable
fun PracticingAdditionScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    var isSoroban by remember { mutableStateOf(true) }
    val upperMax = if (isSoroban) SOROBAN_UPPER else SUANPAN_UPPER
    val lowerMax = if (isSoroban) SOROBAN_LOWER else SUANPAN_LOWER

    val state = remember { mutableStateOf(List(COLUMNS) { PaColumnState() }) }
    var currentLevelIdx by remember { mutableIntStateOf(0) }
    var exercise by remember { mutableStateOf(generateNumbers(levels[0])) }
    var steps by remember { mutableStateOf(buildSteps(exercise, levels[0], s)) }
    var isFeedbackPositive by remember { mutableStateOf(false) }
    var currentStepIdx by remember { mutableIntStateOf(0) }
    var stepCompleted by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var exerciseStarted by remember { mutableStateOf(false) }
    var finalCongratsShown by remember { mutableStateOf(false) }
    var showFinalCongratsMessage by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }

    LaunchedEffect(finalCongratsShown) {
        if (finalCongratsShown) {
            preferences.recordLessonCompletion()
            preferences.markAbacusSectionCompleted("practicing_addition")
            onScoreChanged(currentScore + 2)
        }
    }

    fun resetExercise() {
        state.value = List(COLUMNS) { PaColumnState() }
        exercise = generateNumbers(levels[currentLevelIdx])
        steps = buildSteps(exercise, levels[currentLevelIdx], s)
        currentStepIdx = 0
        stepCompleted = false
        feedbackMessage = ""
        isFeedbackPositive = false
        exerciseStarted = false
        finalCongratsShown = false
        showFinalCongratsMessage = false
    }

    fun checkStep() {
        if (currentStepIdx >= steps.size) return
        val currentVal = PaValue(state.value)
        val step = steps[currentStepIdx]

        if (currentVal == step.targetValue) {
            if (!stepCompleted) {
                stepCompleted = true
                if (currentStepIdx == steps.size - 1) {
                    if (!finalCongratsShown) {
                        finalCongratsShown = true
                        feedbackMessage = s.feedbackPerfect.format(exercise.a, exercise.b, exercise.expected)
                        isFeedbackPositive = true
                    }
                } else {
                    feedbackMessage = s.feedbackCorrect
                    isFeedbackPositive = true
                }
            }
        } else {
            stepCompleted = false
            if (feedbackMessage.isNotEmpty() && !isFeedbackPositive) {
                feedbackMessage = ""
            }
        }
    }

    fun advanceStep() {
        val currentVal = PaValue(state.value)
        val currentStepTarget = steps.getOrNull(currentStepIdx)?.targetValue
        if (currentVal != currentStepTarget) return

        if (currentStepIdx + 1 < steps.size) {
            currentStepIdx++
            stepCompleted = false
            feedbackMessage = ""
            isFeedbackPositive = false
        } else {
            if (currentVal == exercise.expected && !finalCongratsShown) {
                finalCongratsShown = true
                feedbackMessage = s.feedbackCongratulations.format(exercise.a, exercise.b, exercise.expected)
                isFeedbackPositive = true
            }
        }
    }

    fun toggleLevel() {
        val wasLastLevel = currentLevelIdx == levels.size - 1
        val completed = wasLastLevel && finalCongratsShown
        if (completed && !showFinalCongratsMessage) {
            showFinalCongratsMessage = true
            feedbackMessage = "${s.levelCompleteMax} \uD83C\uDF89"
            isFeedbackPositive = true
            return
        }
        showFinalCongratsMessage = false
        currentLevelIdx = (currentLevelIdx + 1) % levels.size
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
                        text = s.practicingAddition,
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
            Spacer(Modifier.height(12.dp))

            Text(
                text = s.practicingAdditionInstruction,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            Text(
                text = "${s.levelPrefix}${levelName(levels[currentLevelIdx], s)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "${exercise.a} + ${exercise.b} = ?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
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

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .aspectRatio(860f / 400f)
                    .pointerInput(upperMax, lowerMax, stepCompleted) {
                        detectTapGestures { offset ->
                            if (stepCompleted) return@detectTapGestures
                            val cw = size.width.toFloat()
                            val ch = size.height.toFloat()
                            handlePaAbacusTap(
                                offset.x, offset.y, cw, ch, state,
                                COLUMNS, upperMax, lowerMax
                            )
                            if (!exerciseStarted) exerciseStarted = true
                            checkStep()
                        }
                    }
            ) {
                drawPaAbacusBackground(size)
                drawPaAbacusFrame(size)
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
                    drawPaAbacusRod(
                        cx = startX + col * colWidth,
                        canvasWidth = size.width,
                        canvasHeight = size.height
                    )
                    drawPaColumnBeads(
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

            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(40.dp),
                color = Color(0xFF2E241F),
            ) {
                Text(
                    text = "${s.valuePrefix}${PaValue(state.value)}",
                    color = Color(0xFFF2ECD8),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            if (steps.isNotEmpty() && currentStepIdx < steps.size && !showFinalCongratsMessage) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "🧮 ${steps[currentStepIdx].instruction}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            if (steps.isNotEmpty()) {
                Text(
                    text = "${s.stepPrefix}${currentStepIdx + 1}/${steps.size}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val hideButtons = exerciseStarted && !finalCongratsShown

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!hideButtons) {
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
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    if (!finalCongratsShown) {
                        FilledTonalButton(
                            onClick = { advanceStep() },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ButtonYellow,
                                contentColor = OnButtonYellow
                            )
                        ) {
                            Text(
                                text = s.nextStep,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                if (!hideButtons) {
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

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
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=f91d9720-3085-4e71-ae5d-7e7cf6b7b7b2"))
                        Toast.makeText(context, s.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=f91d9720-3085-4e71-ae5d-7e7cf6b7b7b2")
                    }
                )
            }
        }
    }
}

private fun handlePaAbacusTap(
    x: Float, y: Float,
    cw: Float, ch: Float,
    state: MutableState<List<PaColumnState>>,
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

private fun DrawScope.drawPaAbacusBackground(size: androidx.compose.ui.geometry.Size) {
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

private fun DrawScope.drawPaAbacusFrame(size: androidx.compose.ui.geometry.Size) {
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

private fun DrawScope.drawPaAbacusRod(cx: Float, canvasWidth: Float, canvasHeight: Float) {
    drawLine(
        color = Color(0xFFB08054),
        start = Offset(cx, 8f / 860f * canvasWidth),
        end = Offset(cx, canvasHeight - 10f / 400f * canvasHeight),
        strokeWidth = 3f / 400f * canvasHeight
    )
}

private fun DrawScope.drawPaColumnBeads(
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
