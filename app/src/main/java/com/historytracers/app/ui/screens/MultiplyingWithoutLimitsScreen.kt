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

private data class MwlStepInfo(val instruction: String, val targetValue: Long, val isStoreStep: Boolean = false)

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

    val multipliers = listOf(1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L)
    val placeNames = listOf(
        s.placeUnits, s.placeTens, s.placeHundreds, s.placeThousands,
        s.placeTenThousands, s.placeHundredThousands, s.placeMillions, s.placeTenMillions
    )

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
        s.mw2StepWriteFirst.format(firstDigitValue, digit, firstProduct, firstProduct),
        firstProduct
    ))

    var currentValue = firstProduct
    val maxMultPlace = multipliers.size - 1

    for (ci in 1 until contribs.size) {
        val (d, place, addValue) = contribs[ci]
        val digitPlaceValue = d * Math.pow(10.0, place.toDouble()).toLong()
        val prefix = "$digitPlaceValue \u00D7 $digit = $addValue: "

        val highPlace = minOf(place + addValue.toString().length - 1, maxMultPlace)
        for (p in place..highPlace) {
            val digitB = (addValue / multipliers[p] % 10).toInt()
            if (digitB == 0 && p < highPlace) continue
            if (digitB == 0) break

            val digitA = (currentValue / multipliers[p] % 10).toInt()
            val totalDigit = digitA + digitB

            if (totalDigit < 10) {
                currentValue += digitB * multipliers[p]
                steps.add(MwlStepInfo(
                    prefix + s.mwStepAdd.format(digitB, placeNames[p], currentValue),
                    currentValue
                ))
            } else {
                val complement = 10 - digitB
                val newValue = currentValue + (multipliers[p] * 10) - (complement * multipliers[p])
                val nextPlace = if (p + 1 < placeNames.size) placeNames[p + 1] else s.placeNext
                steps.add(MwlStepInfo(
                    prefix + s.mwStepCarry.format(digitB, placeNames[p], digitA, digitB, totalDigit, complement, placeNames[p], nextPlace, newValue),
                    newValue
                ))
                currentValue = newValue
            }
        }
    }

    return Pair(steps, currentValue)
}

private fun buildMwlAddSteps(
    addValue: Long, startValue: Long, s: com.historytracers.app.ui.UiStrings, prefix: String
): List<MwlStepInfo> {
    val steps = mutableListOf<MwlStepInfo>()
    var currentValue = startValue

    val multipliers = listOf(1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L)
    val placeNames = listOf(
        s.placeUnits, s.placeTens, s.placeHundreds, s.placeThousands,
        s.placeTenThousands, s.placeHundredThousands, s.placeMillions, s.placeTenMillions
    )

    val addStr = addValue.toString()
    val highPlace = addStr.length - 1

    for (p in 0..highPlace) {
        val digitB = (addValue / multipliers[p] % 10).toInt()
        if (digitB == 0) continue

        val digitA = (currentValue / multipliers[p] % 10).toInt()
        val totalDigit = digitA + digitB

        if (totalDigit < 10) {
            currentValue += digitB * multipliers[p]
            steps.add(MwlStepInfo(
                prefix + s.mwStepAdd.format(digitB, placeNames[p], currentValue),
                currentValue
            ))
        } else {
            val complement = 10 - digitB
            val newValue = currentValue + (multipliers[p] * 10) - (complement * multipliers[p])
            val nextPlace = if (p + 1 < placeNames.size) placeNames[p + 1] else s.placeNext
            steps.add(MwlStepInfo(
                prefix + s.mwStepCarry.format(digitB, placeNames[p], digitA, digitB, totalDigit, complement, placeNames[p], nextPlace, newValue),
                newValue
            ))
            currentValue = newValue
        }
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
        s.mwlShiftInstruction.format(tensResult, storedValue),
        storedValue
    ))

    steps.add(MwlStepInfo(
        s.mwlStoreInstruction.format(storedValue),
        storedValue,
        isStoreStep = true
    ))

    steps.add(MwlStepInfo(
        s.mwlResetInstruction,
        0L
    ))

    val (onesSteps, onesResult) = buildMwlSingleDigitSteps(a, onesDigit, s)
    for (step in onesSteps) steps.add(step)

    val addSteps = buildMwlAddSteps(storedValue, onesResult, s, s.mwlAddStoredPrefix.format(storedValue))
    for (step in addSteps) steps.add(step)

    steps.add(MwlStepInfo(
        s.mwStepFinal.format(a, fullB, total),
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
    var isSoroban by remember { mutableStateOf(true) }
    val upperMax = if (isSoroban) SOROBAN_UPPER else SUANPAN_UPPER
    val lowerMax = if (isSoroban) SOROBAN_LOWER else SUANPAN_LOWER

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
    var storedDisplay by remember { mutableStateOf("") }
    var showResetButton by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

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
        showLastLevelMessage = false
        storedDisplay = ""
        showResetButton = false
    }

    LaunchedEffect(finalCongratsShown) {
        if (finalCongratsShown) {
            preferences.recordLessonCompletion()
            preferences.markAbacusSectionCompleted("multiplying_without_limits")
        }
    }

    fun resetExercise() {
        state.value = List(COLUMNS) { MwlColumnState() }
        exercise = generateMwlExercise(currentDigitLevel)
        steps = buildMwlSteps(exercise, s)
        currentStepIdx = 0
        stepCompleted = false
        feedbackMessage = ""
        isFeedbackPositive = false
        exerciseStarted = false
        finalCongratsShown = false
        showLastLevelMessage = false
        storedDisplay = ""
        showResetButton = steps.getOrNull(0)?.instruction == s.mwlResetInstruction
    }

    fun checkStep() {
        if (currentStepIdx >= steps.size) return
        val currentVal = MwlValue(state.value)
        val step = steps[currentStepIdx]

        if (step.isStoreStep) {
            storedDisplay = s.mwlStoreInstruction.format(steps[currentStepIdx - 1]?.targetValue ?: 0L)
        }

        if (currentVal == step.targetValue) {
            if (!stepCompleted) {
                stepCompleted = true
                if (currentStepIdx == steps.size - 1) {
                    feedbackMessage = s.mwPerfectMessage.format(exercise.a, exercise.fullB, exercise.expected)
                    isFeedbackPositive = true
                } else {
                    feedbackMessage = s.mwCorrectMessage
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

    fun resetAbacus() {
        state.value = List(COLUMNS) { MwlColumnState() }
        showResetButton = false
        checkStep()
    }

    fun advanceStep() {
        val currentVal = MwlValue(state.value)
        val currentStepTarget = steps.getOrNull(currentStepIdx)?.targetValue
        if (currentVal != currentStepTarget) return
        val isLastStep = currentStepIdx == steps.size - 1

        if (isLastStep) {
            if (!finalCongratsShown) {
                finalCongratsShown = true
                onScoreChanged(currentScore + 2)
            }
            feedbackMessage = s.mwCongratulations.format(exercise.a, exercise.fullB, exercise.expected)
            isFeedbackPositive = true
        } else {
            currentStepIdx++
            stepCompleted = false
            feedbackMessage = ""
            isFeedbackPositive = false
            val step = steps[currentStepIdx]
            showResetButton = step.instruction == s.mwlResetInstruction
            if (step.isStoreStep) {
                storedDisplay = s.mwlStoreInstruction.format(
                    steps.getOrNull(currentStepIdx - 1)?.targetValue ?: 0L
                )
            }
        }
    }

    fun toggleLevel() {
        val wasLastLevel = currentDigitLevel == MAX_DIGIT_LEVEL
        val completed = wasLastLevel && finalCongratsShown
        if (completed && !showLastLevelMessage) {
            showLastLevelMessage = true
            feedbackMessage = s.mwlLastLevelMessage
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back)
                }
                Text(
                    text = s.mw3Title,
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
                text = s.mwlInstruction,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            Text(
                text = "${s.levelPrefix}$currentDigitLevel",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "${exercise.a} \u00D7 ${exercise.fullB} = ?",
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
                val beamY = size.height / 2f
                val ballRadius = minOf(
                    colWidth * 0.38f,
                    10f / 400f * size.height,
                    10f / 860f * size.width
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

            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(40.dp),
                color = Color(0xFF2E241F),
            ) {
                Text(
                    text = "${s.valuePrefix}${MwlValue(state.value)}",
                    color = Color(0xFFF2ECD8),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            if (storedDisplay.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF3A6068),
                ) {
                    Text(
                        text = storedDisplay,
                        color = Color(0xFFFFECB3),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (steps.isNotEmpty() && currentStepIdx < steps.size && !showLastLevelMessage) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "${s.mwStepPrefix}${steps[currentStepIdx].instruction}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (showResetButton) {
                Spacer(Modifier.height(8.dp))
                FilledTonalButton(
                    onClick = { resetAbacus() },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text(
                        text = s.resetToZero,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            if (steps.isNotEmpty()) {
                Text(
                    text = s.mwStepStatus.format(currentStepIdx + 1, steps.size),
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
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=052bd667-eb38-4e87-8c05-439cfd9c4178"))
                            Toast.makeText(context, s.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.goToUrl) },
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

private fun DrawScope.drawMwlAbacusBackground(size: androidx.compose.ui.geometry.Size) {
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

private fun DrawScope.drawMwlAbacusFrame(size: androidx.compose.ui.geometry.Size) {
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
