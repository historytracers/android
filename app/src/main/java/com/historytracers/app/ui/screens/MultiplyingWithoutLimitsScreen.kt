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
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

private const val COLUMNS = 9
private const val SOROBAN_UPPER = 1
private const val SOROBAN_LOWER = 4
private const val SUANPAN_UPPER = 2
private const val SUANPAN_LOWER = 5
private const val MAX_DIGIT_LEVEL = 5
private const val MIN_DIGIT_LEVEL = 1

private data class MwlColumnState(val upper: Int = 0, val lower: Int = 0) {
    val value: Int get() = (upper * 5 + lower).coerceIn(0, 9)
    fun normalize(): MwlColumnState {
        val d = (upper * 5 + lower).coerceIn(0, 9)
        return MwlColumnState(upper = d / 5, lower = d % 5)
    }
}

private fun MwlValue(state: List<MwlColumnState>): Long {
    var result = 0L
    for (col in state) {
        result = result * 10 + col.value
    }
    return result
}

private data class MwlExercise(val a: Int, val tensDigit: Int, val onesDigit: Int) {
    val fullB: Int get() = tensDigit * 10 + onesDigit
    val expected: Long get() = (a * fullB).toLong()
}

private data class MwlStepInfo(
    val instruction: String,
    val targetValue: Long,
    val isStorePhase: Boolean = false,
    val isResetPhase: Boolean = false,
    val isSumPhase: Boolean = false
)

private fun getLevelRange(level: Int): Pair<Int, Int> {
    if (level == 1) return Pair(1, 9)
    val power = Math.pow(10.0, (level - 1).toDouble()).toInt()
    return Pair(power, 2 * power - 1)
}

private fun generateMwlExercise(level: Int): MwlExercise {
    val (minA, maxA) = getLevelRange(level)
    val a = Random.nextInt(minA, maxA + 1)
    val tensDigit = Random.nextInt(1, 10)
    val onesDigit = Random.nextInt(1, 10)
    return MwlExercise(a, tensDigit, onesDigit)
}

private fun buildMwlSingleDigitSteps(
    a: Int, digit: Int, s: com.historytracers.app.ui.UiStrings
): Pair<List<MwlStepInfo>, Long> {
    val steps = mutableListOf<MwlStepInfo>()
    val strA = a.toString()
    val numPlaces = strA.length

    val contribs = mutableListOf<Triple<Int, Int, Long>>()
    for (i in strA.indices) {
        val d = strA[i].digitToInt()
        if (d == 0) continue
        val place = numPlaces - 1 - i
        val placeValue = Math.pow(10.0, place.toDouble()).toLong()
        contribs.add(Triple(d, place, d * placeValue * digit))
    }

    if (contribs.isEmpty()) return Pair(listOf(MwlStepInfo("", 0L)), 0L)

    val first = contribs[0]
    val firstDigitValue = first.first * Math.pow(10.0, first.second.toDouble()).toLong()
    val firstProduct = first.third
    steps.add(MwlStepInfo(
        s.mw.mw2StepWriteFirst.format(firstDigitValue, digit, firstProduct, firstProduct),
        firstProduct
    ))

    var currentValue = firstProduct

    for (ci in 1 until contribs.size) {
        val (d, place, addValue) = contribs[ci]
        val digitPlaceValue = d * Math.pow(10.0, place.toDouble()).toLong()
        currentValue += addValue
        steps.add(MwlStepInfo(
            s.mw.mwStepAddContribution.format(digitPlaceValue, digit, addValue),
            currentValue
        ))
    }

    return Pair(steps, currentValue)
}

private fun buildMwlSumSteps(
    storedValue: Long, onesResult: Long, s: com.historytracers.app.ui.UiStrings
): List<MwlStepInfo> {
    if (storedValue == 0L) return emptyList()
    val steps = mutableListOf<MwlStepInfo>()
    var currentValue = onesResult
    val strVal = storedValue.toString()
    val numPlaces = strVal.length

    val multipliers = listOf(1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L)

    for (i in strVal.indices) {
        val d = strVal[i].digitToInt()
        if (d == 0) continue
        val place = numPlaces - 1 - i
        val addValue = d * multipliers[place]
        currentValue += addValue
        steps.add(MwlStepInfo(
            s.mw.mwlAddStoredPrefix.format(storedValue) + "+$addValue → $currentValue",
            currentValue,
            isSumPhase = true
        ))
    }

    return steps
}

private fun buildMwlSteps(exercise: MwlExercise, s: com.historytracers.app.ui.UiStrings): List<MwlStepInfo> {
    val steps = mutableListOf<MwlStepInfo>()
    val a = exercise.a
    val tensDigit = exercise.tensDigit
    val onesDigit = exercise.onesDigit
    val fullB = exercise.fullB
    val total = a * fullB

    val tensResult = a * tensDigit.toLong()
    val storedValue = tensResult * 10

    val (tensSteps, _) = buildMwlSingleDigitSteps(a, tensDigit, s)
    for (step in tensSteps) steps.add(step)

    steps.add(MwlStepInfo(
        s.mw.mwlShiftInstruction.format(tensResult, storedValue),
        storedValue
    ))

    steps.add(MwlStepInfo(
        s.mw.mwlStoreInstruction.format(storedValue),
        0L,
        isStorePhase = true
    ))

    steps.add(MwlStepInfo(
        s.mw.mwlResetInstruction,
        0L,
        isResetPhase = true
    ))

    val onesResult = a * onesDigit.toLong()
    val (onesSteps, _) = buildMwlSingleDigitSteps(a, onesDigit, s)
    for (step in onesSteps) steps.add(step)

    val sumSteps = buildMwlSumSteps(storedValue, onesResult, s)
    for (step in sumSteps) steps.add(step)

    steps.add(MwlStepInfo(
        s.mw.mwStepFinal.format(a, fullB, total),
        total.toLong()
    ))

    return steps
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiplyingWithoutLimitsScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    var abacusMode by remember { mutableStateOf("soroban") }
    val schyotyBeads = remember { mutableStateOf(List(9) { 0 }) }
    val upperMax = if (abacusMode == "soroban") SOROBAN_UPPER else SUANPAN_UPPER
    val lowerMax = if (abacusMode == "soroban") SOROBAN_LOWER else SUANPAN_LOWER

    val state = remember { mutableStateOf(List(COLUMNS) { MwlColumnState() }) }
    var currentDigitLevel by remember { mutableIntStateOf(MIN_DIGIT_LEVEL) }
    var exercise by remember { mutableStateOf(generateMwlExercise(currentDigitLevel)) }
    var steps by remember { mutableStateOf(buildMwlSteps(exercise, s)) }
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
    var showStoredValue by remember { mutableStateOf(false) }

    val tensResult = exercise.a * exercise.tensDigit.toLong()
    val storedValue = tensResult * 10

    LaunchedEffect(Unit) {
        currentDigitLevel = MIN_DIGIT_LEVEL
        state.value = List(COLUMNS) { MwlColumnState() }
        exercise = generateMwlExercise(currentDigitLevel)
        steps = buildMwlSteps(exercise, s)
        currentStepIdx = 0
        stepCompleted = false
        feedbackMessage = ""
        isFeedbackPositive = false
        exerciseStarted = false
        finalCongratsShown = false
        showStoredValue = false
        showLastLevelMessage = false
    }

    LaunchedEffect(finalCongratsShown) {
        if (finalCongratsShown) {
            preferences.recordLessonCompletion()
            preferences.markAbacusSectionCompleted("multiplying_without_limits")
        }
    }

    fun currentMwlValue(): Long = if (abacusMode == "schyoty") {
        var v = 0L
        for (r in 0 until 9) {
            v += schyotyBeads.value[r] * Math.pow(10.0, r.toDouble()).toLong()
        }
        v
    } else {
        MwlValue(state.value)
    }

    fun clearAbacus() {
        state.value = List(COLUMNS) { MwlColumnState() }
        schyotyBeads.value = List(9) { 0 }
    }

    fun resetExercise() {
        clearAbacus()
        showStoredValue = false
        exercise = generateMwlExercise(currentDigitLevel)
        steps = buildMwlSteps(exercise, s)
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
        val step = steps[currentStepIdx]
        if (step.isStorePhase) return
        val currentVal = currentMwlValue()
        if (currentVal == step.targetValue) {
            if (!stepCompleted) {
                stepCompleted = true
                if (currentStepIdx == steps.size - 1) {
                    feedbackMessage = s.mw.mwPerfectMessage.format(exercise.a, exercise.fullB, exercise.expected)
                    isFeedbackPositive = true
                } else {
                    feedbackMessage = s.mw.mwCorrectMessage
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
        if (currentStepIdx >= steps.size) return
        val step = steps[currentStepIdx]
        if (!stepCompleted && !step.isStorePhase) {
            val currentVal = currentMwlValue()
            if (currentVal != step.targetValue) return
        }
        val isLastStep = currentStepIdx == steps.size - 1

        if (isLastStep) {
            if (!finalCongratsShown) {
                finalCongratsShown = true
                onScoreChanged(currentScore + 2)
            }
            feedbackMessage = s.mw.mwCongratulations.format(exercise.a, exercise.fullB, exercise.expected)
            isFeedbackPositive = true
        } else {
            currentStepIdx++
            stepCompleted = false
            feedbackMessage = ""
            isFeedbackPositive = false
            if (currentStepIdx - 1 >= 0 && currentStepIdx - 1 < steps.size && steps[currentStepIdx - 1].isStorePhase) showStoredValue = true
        }
    }

    fun toggleLevel() {
        val wasLastLevel = currentDigitLevel == MAX_DIGIT_LEVEL
        val completed = wasLastLevel && finalCongratsShown
        if (completed && !showLastLevelMessage) {
            showLastLevelMessage = true
            feedbackMessage = s.mw.mwlLastLevelMessage
            isFeedbackPositive = true
            return
        }
        showLastLevelMessage = false
        currentDigitLevel = if (currentDigitLevel >= MAX_DIGIT_LEVEL) MIN_DIGIT_LEVEL else currentDigitLevel + 1
        resetExercise()
    }

    val currentStep = steps.getOrNull(currentStepIdx)

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
                    text = s.mw.mw3Title,
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
            Spacer(Modifier.height(2.dp))

            Text(
                text = s.mw.mwlInstruction,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp)
            )

            Text(
                text = "${exercise.a} \u00D7 ${exercise.fullB} = ?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                val modeEnabled = !exerciseStarted || finalCongratsShown
                FilledIconButton(
                    onClick = {
                        abacusMode = "soroban"
                        state.value = List(COLUMNS) { MwlColumnState() }
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
                    text = s.abacusWrite.sorobanMode,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                FilledIconButton(
                    onClick = {
                        abacusMode = "suanpan"
                        state.value = List(COLUMNS) { MwlColumnState() }
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
                    text = s.abacusWrite.suanpanMode,
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
                    text = s.abacusWrite.schyoty,
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
                    .pointerInput(upperMax, lowerMax, stepCompleted, currentStepIdx) {
                        detectTapGestures { offset ->
                            if (stepCompleted) return@detectTapGestures
                            if (currentStepIdx < steps.size && (steps[currentStepIdx].isStorePhase || steps[currentStepIdx].isResetPhase)) return@detectTapGestures
                            val cw = size.width.toFloat()
                            val ch = size.height.toFloat()
                            handleMwlAbacusTap(
                                offset.x, offset.y, cw, ch, state,
                                COLUMNS, upperMax, lowerMax
                            )
                            if (!exerciseStarted) exerciseStarted = true
                            checkStep()
                        }
                    }
            ) {
                drawMwlAbacusBackground(size)
                drawMwlAbacusFrame(size)
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
                    drawMwlAbacusRod(
                        cx = startX + col * colWidth,
                        canvasWidth = size.width,
                        canvasHeight = size.height
                    )
                    drawMwlColumnBeads(
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
                        .pointerInput(stepCompleted, currentStepIdx) {
                            detectTapGestures { offset ->
                                if (stepCompleted) return@detectTapGestures
                                if (currentStepIdx < steps.size && (steps[currentStepIdx].isStorePhase || steps[currentStepIdx].isResetPhase)) return@detectTapGestures
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
                            drawMwlSchyotyBead(x, y, beadR, active = true, idx = p)
                        }

                        for (p in 0 until 10 - cnt) {
                            val x = inactiveX0 - p * beadStep
                            drawMwlSchyotyBead(x, y, beadR, active = false, idx = 9 - p)
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(40.dp),
                    color = Color(0xFF2E241F),
                ) {
                    Text(
                        text = "${s.common.valuePrefix}${currentMwlValue()}",
                        color = Color(0xFFF2ECD8),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                if (steps.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(40.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        Text(
                            text = s.mw.mwStepStatus.format(currentStepIdx + 1, steps.size),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }

                if (showStoredValue) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Text(
                            text = s.mw.mwlStoreInstruction.format(storedValue),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            if (currentStep?.isStorePhase == true) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = s.mw.mwlStoreInstruction.format(storedValue),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            } else if (currentStep?.isResetPhase == true) {
                Text(
                    text = s.mw.mwlResetInstruction,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            } else if (currentStep != null && currentStep.instruction.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = (if (currentStep.isSumPhase) "\u2795 " else "\uD83E\uDDEE ") + currentStep.instruction,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
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

                    if (currentStep?.isResetPhase == true) {
                        FilledTonalButton(
                            onClick = {
                                clearAbacus()
                                if (!exerciseStarted) exerciseStarted = true
                                checkStep()
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ButtonYellow,
                                contentColor = OnButtonYellow
                            )
                        ) {
                            Text(
                                text = s.common.resetToZero,
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

            if (feedbackMessage.isNotEmpty()) {
                Text(
                    text = feedbackMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isFeedbackPositive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
        }
        }

        if (!finalCongratsShown) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 4.dp, start = 4.dp)
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
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=052bd667-eb38-4e87-8c05-439cfd9c4178"))
                            Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showMainTextSubmenu = false
                            uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=052bd667-eb38-4e87-8c05-439cfd9c4178")
                        }
                    )
                }
            }
        }
    }
}

private fun handleMwlAbacusTap(
    x: Float, y: Float,
    cw: Float, ch: Float,
    state: MutableState<List<MwlColumnState>>,
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
                    it[colHit] = it[colHit].copy(lower = newLower.coerceIn(0, lowerMax)).normalize()
                }
                break
            }
        }
    }
}

private fun DrawScope.drawMwlAbacusBackground(size: androidx.compose.ui.geometry.Size) {
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

private fun DrawScope.drawMwlAbacusFrame(size: androidx.compose.ui.geometry.Size) {
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

private fun DrawScope.drawMwlAbacusRod(cx: Float, canvasWidth: Float, canvasHeight: Float) {
    drawLine(
        color = Color(0xFFB08054),
        start = Offset(cx, 8f / 860f * canvasWidth),
        end = Offset(cx, canvasHeight - 10f / 400f * canvasHeight),
        strokeWidth = 3f / 400f * canvasHeight
    )
}

private fun DrawScope.drawMwlColumnBeads(
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

private fun DrawScope.drawMwlSchyotyBead(x: Float, y: Float, r: Float, active: Boolean, idx: Int) {
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
