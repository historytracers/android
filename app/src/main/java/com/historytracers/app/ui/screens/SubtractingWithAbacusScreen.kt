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
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.AbacusWriteStrings
import com.historytracers.app.ui.features.HubTitleStrings
import com.historytracers.app.ui.features.PlaceValueStrings
import com.historytracers.app.ui.features.SbwStrings
import com.historytracers.app.ui.features.abacusWriteStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.placeValueStringsForLanguage
import com.historytracers.app.ui.features.sbwStringsForLanguage
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
private const val MAX_DIGIT_LEVEL = 8
private const val MIN_DIGIT_LEVEL = 1

private data class SbwColumnState(val upper: Int = 0, val lower: Int = 0) {
    val value: Int get() = (upper * 5 + lower).coerceIn(0, 9)
    fun normalize(): SbwColumnState {
        val d = (upper * 5 + lower).coerceIn(0, 9)
        return SbwColumnState(upper = d / 5, lower = d % 5)
    }
}

private fun SbwValue(state: List<SbwColumnState>): Long {
    var result = 0L
    for (col in state) {
        result = result * 10 + col.value
    }
    return result
}

private data class SbwExercise(val a: Int, val b: Int) {
    val expected: Long get() = (a - b).toLong()
}

private data class SbwStepInfo(val instruction: String, val targetValue: Long)

private fun getLevelRange(level: Int): Pair<Int, Int> {
    if (level == 1) return Pair(1, 9)
    val power = Math.pow(10.0, (level - 1).toDouble()).toInt()
    return Pair(power, 2 * power - 1)
}

private fun generateSbwExercise(level: Int): SbwExercise {
    val (minA, maxA) = getLevelRange(level)
    val a = Random.nextInt(minA, maxA + 1)
    val b = if (level == 1) {
        Random.nextInt(1, a + 1)
    } else {
        val minB = Math.pow(10.0, (level - 1).toDouble()).toInt()
        Random.nextInt(minB, a + 1)
    }
    return SbwExercise(a, b)
}

private fun buildSbwSteps(exercise: SbwExercise, sbws: SbwStrings, ps: PlaceValueStrings): List<SbwStepInfo> {
    val steps = mutableListOf<SbwStepInfo>()
    val a = exercise.a
    val b = exercise.b
    val expected = exercise.expected

    val multipliers = listOf(1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L)
    val placeNames = listOf(
        ps.placeUnits, ps.placeTens, ps.placeHundreds, ps.placeThousands,
        ps.placeTenThousands, ps.placeHundredThousands, ps.placeMillions, ps.placeTenMillions
    )

    steps.add(SbwStepInfo(sbws.sbwSetupInstruction.format(a), a.toLong()))

    var currentValue = a.toLong()
    val bDigitCount = b.toString().length

    for (pos in 0 until bDigitCount) {
        val bDigit = (b / multipliers[pos] % 10).toInt()
        if (bDigit == 0) continue

        val currentDigit = (currentValue / multipliers[pos] % 10).toInt()
        val prefix = "$currentDigit − $bDigit: "

        if (currentDigit >= bDigit) {
            currentValue -= bDigit * multipliers[pos]
            val desc = sbws.sbwSubStepDesc.format(bDigit, placeNames[pos])
            steps.add(SbwStepInfo(
                prefix + sbws.sbwSubStepInstruction.format(desc, currentValue),
                currentValue
            ))
        } else {
            val maxPos = COLUMNS - 1
            var borrowFrom = pos + 1
            while (borrowFrom <= maxPos && (currentValue / multipliers[borrowFrom] % 10).toInt() == 0) {
                borrowFrom++
            }

            for (p in (pos + 1) until borrowFrom) {
                val pDigit = (currentValue / multipliers[p] % 10).toInt()
                if (pDigit == 0) {
                    currentValue += 9 * multipliers[p]
                    val desc = sbws.sbwBorrowSetNine.format(placeNames[p], placeNames[borrowFrom], placeNames[p])
                    steps.add(SbwStepInfo(
                        prefix + sbws.sbwSubStepInstruction.format(desc, currentValue),
                        currentValue
                    ))
                }
            }

            if (borrowFrom <= maxPos) {
                val borrowDigit = (currentValue / multipliers[borrowFrom] % 10).toInt()
                currentValue -= 1 * multipliers[borrowFrom]
                val desc = sbws.sbwBorrowReduce.format(placeNames[borrowFrom], borrowDigit, borrowDigit - 1)
                steps.add(SbwStepInfo(
                    prefix + sbws.sbwSubStepInstruction.format(desc, currentValue),
                    currentValue
                ))

                val complement = 10 - bDigit
                currentValue += complement * multipliers[pos]
                val newDigit = (currentValue / multipliers[pos] % 10).toInt()
                val descAdd = sbws.sbwBorrowSubUnits.format(complement, bDigit, complement, placeNames[pos], placeNames[pos], newDigit)
                steps.add(SbwStepInfo(
                    prefix + sbws.sbwSubStepInstruction.format(descAdd, currentValue),
                    currentValue
                ))
            }
        }
    }

    return steps
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubtractingWithAbacusScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val sbws = sbwStringsForLanguage(LocalAppLanguage.current)
    val ps = placeValueStringsForLanguage(LocalAppLanguage.current)
    val aws = abacusWriteStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    var abacusMode by remember { mutableStateOf("soroban") }
    val schyotyBeads = remember { mutableStateOf(List(9) { 0 }) }
    val upperMax = if (abacusMode == "soroban") SOROBAN_UPPER else SUANPAN_UPPER
    val lowerMax = if (abacusMode == "soroban") SOROBAN_LOWER else SUANPAN_LOWER

    val state = remember { mutableStateOf(List(COLUMNS) { SbwColumnState() }) }
    var currentDigitLevel by remember { mutableIntStateOf(MIN_DIGIT_LEVEL) }
    var exercise by remember { mutableStateOf(generateSbwExercise(currentDigitLevel)) }
    var steps by remember { mutableStateOf(buildSbwSteps(exercise, sbws, ps)) }
    var isFeedbackPositive by remember { mutableStateOf(false) }
    var currentStepIdx by remember { mutableIntStateOf(0) }
    var stepCompleted by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var exerciseStarted by remember { mutableStateOf(false) }
    var finalCongratsShown by remember { mutableStateOf(false) }
    var showLastLevelMessage by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    var showJessicaSubmenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        currentDigitLevel = MIN_DIGIT_LEVEL
        state.value = List(COLUMNS) { SbwColumnState() }
        exercise = generateSbwExercise(currentDigitLevel)
        steps = buildSbwSteps(exercise, sbws, ps)
        currentStepIdx = 0
        stepCompleted = false
        feedbackMessage = ""
        isFeedbackPositive = false
        exerciseStarted = false
        finalCongratsShown = false
        showLastLevelMessage = false
    }

    LaunchedEffect(finalCongratsShown) {
        if (finalCongratsShown) {
            preferences.recordLessonCompletion()
            preferences.markAbacusSectionCompleted("subtracting_with_abacus")
        }
    }

    fun currentSbwValue(): Long = if (abacusMode == "schyoty") {
        var v = 0L
        for (r in 0 until 9) {
            v += schyotyBeads.value[r] * Math.pow(10.0, r.toDouble()).toLong()
        }
        v
    } else {
        SbwValue(state.value)
    }

    fun resetExercise() {
        state.value = List(COLUMNS) { SbwColumnState() }
        schyotyBeads.value = List(9) { 0 }
        exercise = generateSbwExercise(currentDigitLevel)
        steps = buildSbwSteps(exercise, sbws, ps)
        currentStepIdx = 0
        stepCompleted = false
        feedbackMessage = ""
        isFeedbackPositive = false
        exerciseStarted = false
        finalCongratsShown = false
        showLastLevelMessage = false
    }

    fun checkStep() {
        if (currentStepIdx >= steps.size) return
        val currentVal = currentSbwValue()
        val step = steps[currentStepIdx]

        if (currentVal == step.targetValue) {
            if (!stepCompleted) {
                stepCompleted = true
                if (currentStepIdx == steps.size - 1) {
                    feedbackMessage = sbws.sbwPerfectMessage.format(exercise.a, exercise.b, exercise.expected)
                    isFeedbackPositive = true
                } else {
                    feedbackMessage = sbws.sbwCorrectMessage
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
        val currentVal = currentSbwValue()
        val currentStepTarget = steps.getOrNull(currentStepIdx)?.targetValue
        if (currentVal != currentStepTarget) return
        val isLastStep = currentStepIdx == steps.size - 1

        if (isLastStep) {
            if (!finalCongratsShown) {
                finalCongratsShown = true
                onScoreChanged(currentScore + 2)
            }
            feedbackMessage = sbws.sbwCongratsMessage.format(exercise.a, exercise.b, exercise.expected)
            isFeedbackPositive = true
        } else {
            currentStepIdx++
            stepCompleted = false
            feedbackMessage = ""
            isFeedbackPositive = false
        }
    }

    fun toggleLevel() {
        val wasLastLevel = currentDigitLevel == MAX_DIGIT_LEVEL
        val completed = wasLastLevel && finalCongratsShown
        if (completed && !showLastLevelMessage) {
            showLastLevelMessage = true
            feedbackMessage = sbws.sbwLastLevelMessage
            isFeedbackPositive = true
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.common.back)
                    }
                    Text(
                        text = sbws.sbwTitle,
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
                Spacer(Modifier.height(4.dp))

                Text(
                    text = sbws.sbwInstruction,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp)
                )

                Text(
                    text = "${exercise.a} \u2212 ${exercise.b} = ?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    val modeEnabled = !exerciseStarted || finalCongratsShown
                    FilledIconButton(
                        onClick = {
                            abacusMode = "soroban"
                            state.value = List(COLUMNS) { SbwColumnState() }
                        },
                        enabled = modeEnabled,
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(24),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (abacusMode == "soroban") ButtonYellow else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "S",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (abacusMode == "soroban") OnButtonYellow else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = aws.sorobanMode,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    FilledIconButton(
                        onClick = {
                            abacusMode = "suanpan"
                            state.value = List(COLUMNS) { SbwColumnState() }
                        },
                        enabled = modeEnabled,
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(24),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (abacusMode == "suanpan") ButtonYellow else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "S",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (abacusMode == "suanpan") OnButtonYellow else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = aws.suanpanMode,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    FilledIconButton(
                        onClick = {
                            abacusMode = "schyoty"
                            schyotyBeads.value = List(9) { 0 }
                        },
                        enabled = modeEnabled,
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(24),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (abacusMode == "schyoty") ButtonYellow else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "Sc",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (abacusMode == "schyoty") OnButtonYellow else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = aws.schyoty,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (abacusMode in listOf("soroban", "suanpan")) {

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
                                handleSbwAbacusTap(
                                    offset.x, offset.y, cw, ch, state,
                                    COLUMNS, upperMax, lowerMax
                                )
                                if (!exerciseStarted) exerciseStarted = true
                                checkStep()
                            }
                        }
                ) {
                    drawSbwAbacusBackground(size)
                    drawSbwAbacusFrame(size)
                    val margin = 28f / 860f * size.width
                    val usableWidth = size.width - 2f * margin
                    val colWidth = usableWidth / COLUMNS
                    val startX = margin + colWidth / 2f
                    val beamY = size.height / 2f - 30f / 400f * size.height
                    val ballRadius = minOf(
                        colWidth * 0.38f,
                        14f / 400f * size.height,
                        14f / 860f * size.width
                    )
                    val dtt = beamY - 28f / 400f * size.height
                    val dtb = beamY + 28f / 400f * size.height
                    for (col in 0 until COLUMNS) {
                        drawSbwAbacusRod(
                            cx = startX + col * colWidth,
                            canvasWidth = size.width,
                            canvasHeight = size.height
                        )
                        drawSbwColumnBeads(
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
                            .pointerInput(stepCompleted) {
                                detectTapGestures { offset ->
                                    if (stepCompleted) return@detectTapGestures
                                    val cw = size.width.toFloat()
                                    val ch = size.height.toFloat()
                                    val M = 14f
                                    val wireL = M / 480f * cw
                                    val wireR = (cw - M / 480f * cw)
                                    val areaH = ch - 2f * M / 480f * cw
                                    val rowSp = areaH / (9 + 1)
                                    val beadR = minOf((wireR - wireL) / (10 * 2.6f), rowSp * 0.38f, 14f / 480f * cw)
                                    val beadStep = beadR * 2f + beadR * 0.3f
                                    val activeX0 = wireL + beadR
                                    val inactiveX0 = wireR - beadR

                                    for (r in 0 until 9) {
                                        val y = M / 480f * cw + rowSp * (9 - r)
                                        if (abs(offset.y - y) > beadR + 10f / 480f * cw) continue

                                        val cnt = schyotyBeads.value[r]

                                        for (p in 0 until cnt) {
                                            val x = activeX0 + p * beadStep
                                            if (abs(offset.x - x) < beadR + 5f / 480f * cw) {
                                                schyotyBeads.value = schyotyBeads.value.toMutableList().also { it[r] = p }
                                                if (!exerciseStarted) exerciseStarted = true
                                                checkStep()
                                                return@detectTapGestures
                                            }
                                        }

                                        for (p in 0 until 10 - cnt) {
                                            val x = inactiveX0 - p * beadStep
                                            if (abs(offset.x - x) < beadR + 5f / 480f * cw) {
                                                schyotyBeads.value = schyotyBeads.value.toMutableList().also { it[r] = 10 - p }
                                                if (!exerciseStarted) exerciseStarted = true
                                                checkStep()
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
                                drawSbwSchyotyBead(x, y, beadR, active = true, idx = p)
                            }

                            for (p in 0 until 10 - cnt) {
                                val x = inactiveX0 - p * beadStep
                                drawSbwSchyotyBead(x, y, beadR, active = false, idx = 9 - p)
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
                        text = "${s.common.valuePrefix}${currentSbwValue()}",
                        color = Color(0xFFF2ECD8),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (steps.isNotEmpty() && currentStepIdx < steps.size && !showLastLevelMessage) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "${sbws.sbwStepPrefix}${steps[currentStepIdx].instruction}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                if (steps.isNotEmpty()) {
                    Text(
                        text = sbws.sbwStepStatus.format(currentStepIdx + 1, steps.size),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(4.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
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
                                    text = s.common.nextStep,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))

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

        if (!finalCongratsShown) {
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
                        text = { Text(hts.jessicaAmarteifio) },
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
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=e4103f19-1efb-469f-bd69-d44d43e0d3a3"))
                            Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showMainTextSubmenu = false
                            uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=e4103f19-1efb-469f-bd69-d44d43e0d3a3")
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
}

private fun handleSbwAbacusTap(
    x: Float, y: Float,
    cw: Float, ch: Float,
    state: MutableState<List<SbwColumnState>>,
    columns: Int,
    upperMax: Int,
    lowerMax: Int
) {
    val margin = 28f / 860f * cw
    val usableWidth = cw - 2f * margin
    val colW = usableWidth / columns
    val startX = margin + colW / 2f
    val beamY = ch / 2f - 30f / 400f * ch
    val bR = minOf(colW * 0.38f, 14f / 400f * ch, 14f / 860f * cw)
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
        val inactiveY = dtt - 100f / 400f * ch - bi * 22f / 400f * ch
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
            val inactiveY = activeY + 87f / 400f * ch
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

private fun DrawScope.drawSbwAbacusBackground(size: androidx.compose.ui.geometry.Size) {
    drawRect(color = Color(0xFFFEF5E0), size = size)
    val beamY = size.height / 2f - 30f / 400f * size.height
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

private fun DrawScope.drawSbwAbacusFrame(size: androidx.compose.ui.geometry.Size) {
    drawRect(
        color = Color(0xFFF9EEC7),
        topLeft = Offset(5f / 860f * size.width, 5f / 400f * size.height),
        size = androidx.compose.ui.geometry.Size(
            size.width - 10f / 860f * size.width,
            size.height - 30f / 400f * size.height
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

private fun DrawScope.drawSbwAbacusRod(cx: Float, canvasWidth: Float, canvasHeight: Float) {
    drawLine(
        color = Color(0xFFB08054),
        start = Offset(cx, 8f / 860f * canvasWidth),
        end = Offset(cx, canvasHeight - 10f / 400f * canvasHeight),
        strokeWidth = 3f / 400f * canvasHeight
    )
}

private fun DrawScope.drawSbwColumnBeads(
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
        val inactiveY = decimalTrackTop - 100f / 400f * canvasHeight - i * 22f / 400f * canvasHeight
        val beadActive = i < upperCount
        val by = if (beadActive) activeY else inactiveY

        drawCircle(color = Color(0xFFC03A28), radius = ballRadius, center = Offset(cx, by))
        drawCircle(color = Color(0xFFF06A50), radius = ballRadius * 0.85f, center = Offset(cx, by))
        drawCircle(color = Color(0xFF4A2018), radius = ballRadius, center = Offset(cx, by), style = Stroke(width = 1.5f / 400f * canvasHeight))
        drawCircle(color = Color(0xFFFFEAD4), radius = 3f / 860f * canvasWidth, center = Offset(cx - 3f / 860f * canvasWidth, by - 3f / 400f * canvasHeight))
    }

    for (i in 0 until lowerMax) {
        val activeY = decimalTrackBottom + 8f / 400f * canvasHeight + i * 22f / 400f * canvasHeight
        val inactiveY = activeY + 87f / 400f * canvasHeight
        val beadActive = i < lowerCount
        val by = if (beadActive) activeY else inactiveY

        drawCircle(color = Color(0xFF3A6068), radius = ballRadius - 0.5f / 400f * canvasHeight, center = Offset(cx, by))
        drawCircle(color = Color(0xFF7DA0AE), radius = (ballRadius - 0.5f / 400f * canvasHeight) * 0.85f, center = Offset(cx, by))
        drawCircle(color = Color(0xFF1A3A3A), radius = ballRadius - 0.5f / 400f * canvasHeight, center = Offset(cx, by), style = Stroke(width = 1.2f / 400f * canvasHeight))
        drawCircle(color = Color(0xFFC8E2EC), radius = 2.5f / 860f * canvasWidth, center = Offset(cx - 2.5f / 860f * canvasWidth, by - 2.5f / 400f * canvasHeight))
    }
}

private fun DrawScope.drawSbwSchyotyBead(x: Float, y: Float, r: Float, active: Boolean, idx: Int) {
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
