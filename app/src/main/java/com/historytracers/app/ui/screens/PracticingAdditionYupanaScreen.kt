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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.practicingAdditionYupanaScreenStringsForLanguage
import com.historytracers.app.ui.features.yupanaSharedStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.random.Random

private const val ROWS = 4
private const val MAX_DIGIT_LEVEL = 9
private const val MIN_DIGIT_LEVEL = 1

private val yupanaSelectors = listOf(
    -1, 4, 3, 2, 4, 1, 1, 1, 1, 1,
    -1, -1, -1, -1, 2, -1, 4, 3, 2, 2,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, 4
)

private data class YpRowState(
    val leftDigit: Int = 0,
    val rightDigit: Int = 0,
    val resultDigit: Int = 0
)

private data class YpExercise(val left: Int, val right: Int) {
    val sum: Int get() = left + right
}

private val placeLabels = listOf("thousands", "hundreds", "tens", "units")

private fun getMarkersForDigit(digit: Int): Set<Int> {
    val cols = mutableSetOf<Int>()
    for (offset in 0..2) {
        val idx = digit + offset * 10
        if (idx >= yupanaSelectors.size) break
        val col = yupanaSelectors[idx]
        if (col > 0) cols.add(col)
    }
    return cols
}

private val ACT_KINKIN_PAIR = "KINKIN_PAIR"
private val ACT_KINKIN_TRIPLE = "KINKIN_TRIPLE"
private val ACT_ISKAY = "ISKAY"
private val ACT_KIMSA = "KIMSA"
private val ACT_PICHANA_ADD = "PICHANA_ADD"
private val ACT_PICHANA_COMBINE = "PICHANA_COMBINE"
private val ACT_PISQA = "PISQA"

private fun movementDesc(name: String): String = when (name) {
    ACT_KINKIN_PAIR -> "KINKIN (1 + 1 = 2)"
    ACT_KINKIN_TRIPLE -> "KINKIN (1 + 1 + 1 = 3)"
    ACT_ISKAY -> "ISKAY (2 + 2 = 1 + 3)"
    ACT_KIMSA -> "KIMSA (3 + 3 = 1 + 5)"
    ACT_PICHANA_ADD -> "PICHANA (1 + 2 = 3)"
    ACT_PICHANA_COMBINE -> "PICHANA (2 + 3 = 5)"
    ACT_PISQA -> "PISQA (5 + 5 = 10)"
    else -> name
}

private fun writeSumOnYupana(withoutMoves: String, lValue: Int, rBase: Int, carryIn: Int): List<String> {
    val m = mutableListOf<String>()
    if (lValue == 0 && rBase == 0 && carryIn == 0) {
        m.add(withoutMoves)
        return m.map { movementDesc(it) }
    }

    val leftMarkers = getMarkersForDigit(lValue)
    val rightMarkers = getMarkersForDigit(rBase)

    // Auxiliary vector: count of markers per column
    // index 0=col1(value5), 1=col2(value3), 2=col3(value2), 3=col4(value1)
    val count = intArrayOf(0, 0, 0, 0)
    for (c in leftMarkers) count[c - 1]++
    for (c in rightMarkers) count[c - 1]++
    // Carry from previous row adds one marker in col4 (value 1)
    count[3] += carryIn

    // Iterative consolidation from col4 to col1 until stable
    while (true) {
        var changed = false

        // KINKIN: 1+1+1=3 (three col4 markers → one col2 marker)
        while (count[3] >= 3) {
            count[3] -= 3
            count[1]++
            m.add(ACT_KINKIN_TRIPLE)
            changed = true
        }

        // PICHANA: 1+2=3 (col3 + col4 → col2)
        while (count[2] >= 1 && count[3] >= 1) {
            count[2]--
            count[3]--
            count[1]++
            m.add(ACT_PICHANA_ADD)
            changed = true
        }

        // KINKIN: 1+1=2 (col4 + col4 → col3)
        while (count[3] >= 2) {
            count[3] -= 2
            count[2]++
            m.add(ACT_KINKIN_PAIR)
            changed = true
        }

        // ISKAY: 2+2=1+3 (col3 + col3 → col4 + col2)
        while (count[2] >= 2) {
            count[2] -= 2
            count[1]++
            count[3]++
            m.add(ACT_ISKAY)
            changed = true
        }

        // KIMSA: 3+3=1+5 (col2 + col2 → col4 + col1)
        while (count[1] >= 2) {
            count[1] -= 2
            count[0]++
            count[3]++
            m.add(ACT_KIMSA)
            changed = true
        }

        // PICHANA: 2+3=5 (col2 + col3 → col1)
        while (count[1] >= 1 && count[2] >= 1) {
            count[1]--
            count[2]--
            count[0]++
            m.add(ACT_PICHANA_COMBINE)
            changed = true
        }

        // PISQA: 5+5=10 (col1 + col1 → carry to next row)
        while (count[0] >= 2) {
            count[0] -= 2
            m.add(ACT_PISQA)
            changed = true
        }

        if (!changed) break
    }

    if (m.isEmpty()) m.add(withoutMoves)
    return m.map { movementDesc(it) }
}

private fun numberToDigits(n: Int): List<Int> {
    val clamped = n.coerceIn(0, 9999)
    val s = clamped.toString().padStart(ROWS, '0')
    return s.map { it - '0' }
}

private fun getLevelRange(level: Int): Pair<Int, Int> {
    val base = (level - 1) * 100
    val min = if (level == 1) 1 else base
    return Pair(min, base + 99)
}

private fun generateYpExercise(level: Int): YpExercise {
    val (min, max) = getLevelRange(level)
    val left = Random.nextInt(min, max + 1)
    val right = Random.nextInt(min, max + 1)
    return YpExercise(left, right)
}

@Composable
fun PracticingAdditionYupanaScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = practicingAdditionYupanaScreenStringsForLanguage(LocalAppLanguage.current)
    val ys = yupanaSharedStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var currentDigitLevel by remember { mutableIntStateOf(MIN_DIGIT_LEVEL) }
    var exercise by remember { mutableStateOf(generateYpExercise(currentDigitLevel)) }
    var rows by remember { mutableStateOf(List(ROWS) { YpRowState() }) }
    var stepRowIdx by remember { mutableIntStateOf(-1) }
    var stepCompleted by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var isFeedbackPositive by remember { mutableStateOf(false) }
    var isNeutralFeedback by remember { mutableStateOf(false) }
    var exerciseStarted by remember { mutableStateOf(false) }
    var showLastLevelMessage by remember { mutableStateOf(false) }
    var finalCongratsShown by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    var showDhavitPremSubmenu by remember { mutableStateOf(false) }
    var greenColumns by remember { mutableStateOf(setOf<Int>()) }
    var consumedLeft by remember { mutableStateOf(setOf<Int>()) }
    var consumedRight by remember { mutableStateOf(setOf<Int>()) }
    var consumedCarry by remember { mutableStateOf(false) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var rowCompleted by remember { mutableStateOf(false) }
    var phase by remember { mutableIntStateOf(0) }
    var userRedColumns by remember { mutableStateOf(emptySet<Int>()) }
    var userBlueColumns by remember { mutableStateOf(emptySet<Int>()) }
    var completedRedMarkers by remember { mutableStateOf(List(ROWS) { emptySet<Int>() }) }
    var completedBlueMarkers by remember { mutableStateOf(List(ROWS) { emptySet<Int>() }) }
    val lastMeaningfulStepRowIdx = remember(rows) {
        val nonZeroActiveIdx = (0 until ROWS).firstOrNull { rows[it].resultDigit != 0 } ?: ROWS
        if (nonZeroActiveIdx == ROWS) -1 else ROWS - 1 - nonZeroActiveIdx
    }
    val carryIntoRow = remember(rows) {
        val carries = IntArray(ROWS) { 0 }
        var carry = 0
        for (i in ROWS - 1 downTo 0) {
            carries[i] = carry
            val sum = rows[i].leftDigit + rows[i].rightDigit + carry
            carry = sum / 10
        }
        carries
    }

    fun recomputeConsumed() {
        val activeIdx = ROWS - 1 - stepRowIdx
        consumedLeft = completedRedMarkers.getOrElse(activeIdx) { getMarkersForDigit(rows[activeIdx].leftDigit) }
        consumedRight = completedBlueMarkers.getOrElse(activeIdx) { getMarkersForDigit(rows[activeIdx].rightDigit) }
        consumedCarry = carryIntoRow[activeIdx] > 0
    }

    fun toggleColumn(col: Int) {
        if (rowCompleted) return
        if (!exerciseStarted) exerciseStarted = true
        when (phase) {
            0 -> {
                userRedColumns = if (col in userRedColumns) userRedColumns - col else userRedColumns + col
                val activeIdx = ROWS - 1 - stepRowIdx
                val expected = getMarkersForDigit(rows[activeIdx].leftDigit)
                if (userRedColumns == expected) {
                    completedRedMarkers = completedRedMarkers.toMutableList().also { it[activeIdx] = userRedColumns }
                    rowCompleted = true
                    feedbackMessage = ys.ypCorrectMessage
                    isFeedbackPositive = true
                }
            }
            1 -> {
                userBlueColumns = if (col in userBlueColumns) userBlueColumns - col else userBlueColumns + col
                val activeIdx = ROWS - 1 - stepRowIdx
                val expected = getMarkersForDigit(rows[activeIdx].rightDigit)
                if (userBlueColumns == expected) {
                    completedBlueMarkers = completedBlueMarkers.toMutableList().also { it[activeIdx] = userBlueColumns }
                    rowCompleted = true
                    feedbackMessage = ys.ypCorrectMessage
                    isFeedbackPositive = true
                }
            }
            2 -> {
                greenColumns = if (col in greenColumns) greenColumns - col else greenColumns + col
                recomputeConsumed()
                if (stepRowIdx in 0 until ROWS) {
                    val activeIdx = ROWS - 1 - stepRowIdx
                    val expected = getMarkersForDigit(rows[activeIdx].resultDigit)
                    if (greenColumns == expected) {
                        rowCompleted = true
                        if (stepRowIdx == lastMeaningfulStepRowIdx) {
                            stepCompleted = true
                            feedbackMessage = ys.ypPerfectMessage.format(exercise.left, exercise.right, exercise.left + exercise.right)
                            finalCongratsShown = true
                            onScoreChanged(currentScore + 2)
                            scope.launch { preferences.recordLessonCompletion() }
                        } else {
                            feedbackMessage = ys.ypCorrectMessage
                        }
                        isFeedbackPositive = true
                    }
                }
            }
        }
    }

    fun updateDisplay() {
        val l = exercise.left
        val r = exercise.right
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

    fun advanceToPhase(newPhase: Int) {
        phase = newPhase
        stepRowIdx = 0
        when (newPhase) {
            0 -> { userRedColumns = emptySet() }
            1 -> { userBlueColumns = emptySet() }
            else -> { greenColumns = emptySet(); consumedLeft = emptySet(); consumedRight = emptySet(); consumedCarry = false }
        }
        rowCompleted = false
        feedbackMessage = ""
        isNeutralFeedback = false

        while (stepRowIdx <= lastMeaningfulStepRowIdx) {
            val idx = ROWS - 1 - stepRowIdx
            val digit = when (newPhase) {
                0 -> rows[idx].leftDigit
                1 -> rows[idx].rightDigit
                else -> rows[idx].resultDigit
            }
            if (getMarkersForDigit(digit).isNotEmpty()) break
            when (newPhase) {
                0 -> completedRedMarkers = completedRedMarkers.toMutableList().also { it[idx] = emptySet() }
                1 -> completedBlueMarkers = completedBlueMarkers.toMutableList().also { it[idx] = emptySet() }
            }
            if (stepRowIdx >= lastMeaningfulStepRowIdx) {
                if (newPhase >= 2) {
                    rowCompleted = true
                } else {
                    advanceToPhase(newPhase + 1)
                }
                return
            }
            stepRowIdx++
        }
    }

    fun zeroDigitMessage(idx: Int): String {
        val placeLabel = placeLabels[idx]
        return when (phase) {
            0 -> ys.ypNothingLeft.format(placeLabel)
            1 -> ys.ypNothingRight.format(placeLabel)
            else -> {
                val carryIn = carryIntoRow[idx]
                val total = rows[idx].leftDigit + rows[idx].rightDigit + carryIn
                if (carryIn > 0 || total >= 10) {
                    val carryOut = total / 10
                    ys.ypSumTen.format(rows[idx].leftDigit, rows[idx].rightDigit, carryIn, total, placeLabel, carryOut)
                } else {
                    ys.ypNothingResult.format(placeLabel)
                }
            }
        }
    }

    fun advanceToNextRow() {
        if (stepRowIdx >= lastMeaningfulStepRowIdx) {
            when (phase) {
                0 -> advanceToPhase(1)
                1 -> advanceToPhase(2)
            }
            return
        }
        stepRowIdx++
        when (phase) {
            0 -> { userRedColumns = emptySet() }
            1 -> { userBlueColumns = emptySet() }
            else -> { greenColumns = emptySet(); consumedLeft = emptySet(); consumedRight = emptySet(); consumedCarry = false }
        }
        rowCompleted = false
        feedbackMessage = ""
        isNeutralFeedback = false
        val idx = ROWS - 1 - stepRowIdx
        val digit = when (phase) {
            0 -> rows[idx].leftDigit
            1 -> rows[idx].rightDigit
            else -> rows[idx].resultDigit
        }
        if (getMarkersForDigit(digit).isEmpty()) {
            feedbackMessage = zeroDigitMessage(idx)
            isNeutralFeedback = true
            rowCompleted = true
        }
    }

    LaunchedEffect(exercise) {
        updateDisplay()
        exerciseStarted = false
        advanceToPhase(0)
    }

    fun resetExercise() {
        exercise = generateYpExercise(currentDigitLevel)
        phase = 0
        stepRowIdx = -1
        stepCompleted = false
        feedbackMessage = ""

        isFeedbackPositive = false
        isNeutralFeedback = false
        greenColumns = emptySet()
        consumedLeft = emptySet()
        consumedRight = emptySet()
        consumedCarry = false
        userRedColumns = emptySet()
        userBlueColumns = emptySet()
        completedRedMarkers = List(ROWS) { emptySet() }
        completedBlueMarkers = List(ROWS) { emptySet() }
        rowCompleted = false
        finalCongratsShown = false
        showLastLevelMessage = false
    }

    fun resetCurrentExercise() {
        phase = 0
        stepRowIdx = -1
        stepCompleted = false
        feedbackMessage = ""

        isFeedbackPositive = false
        isNeutralFeedback = false
        exerciseStarted = false
        greenColumns = emptySet()
        consumedLeft = emptySet()
        consumedRight = emptySet()
        consumedCarry = false
        userRedColumns = emptySet()
        userBlueColumns = emptySet()
        completedRedMarkers = List(ROWS) { emptySet() }
        completedBlueMarkers = List(ROWS) { emptySet() }
        rowCompleted = false
        finalCongratsShown = false
        showLastLevelMessage = false
        advanceToPhase(0)
    }

    fun toggleLevel() {
        if (currentDigitLevel == MAX_DIGIT_LEVEL && !showLastLevelMessage) {
            showLastLevelMessage = true
            feedbackMessage = ys.ypLastLevelMessage
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
                        text = xs.practicingWithYupana,
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
                    text = ys.ypButtonInstruction,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${s.common.levelPrefix}$currentDigitLevel",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF2E241F),
                ) {
                    Text(
                        text = "${exercise.left} + ${exercise.right} = ?",
                        color = Color(0xFFF2ECD8),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .aspectRatio(860f / 480f)
                        .onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }
                        .pointerInput(phase, stepRowIdx, rowCompleted) {
                            if (stepRowIdx in 0 until ROWS && !rowCompleted) {
                                val activeIdx = ROWS - 1 - stepRowIdx
                                val digit = when (phase) {
                                    0 -> rows[activeIdx].leftDigit
                                    1 -> rows[activeIdx].rightDigit
                                    else -> rows[activeIdx].resultDigit
                                }
                                if (getMarkersForDigit(digit).isEmpty()) {
                                    feedbackMessage = zeroDigitMessage(activeIdx)
                                    isNeutralFeedback = true
                                    rowCompleted = true
                                    return@pointerInput
                                }
                                detectTapGestures { offset ->
                                    val margin = 3f / 860f * canvasSize.width
                                    val usableWidth = canvasSize.width - 2f * margin
                                    val colW = usableWidth / 4f
                                    val startX = margin
                                    val rowHeight = (canvasSize.height - 6f / 480f * canvasSize.height) / ROWS
                                    val startY = 3f / 480f * canvasSize.height
                                    val activeRow = ROWS - 1 - stepRowIdx
                                    val rowTop = startY + activeRow * rowHeight
                                    if (offset.x in startX..(startX + 4f * colW) && offset.y in rowTop..(rowTop + rowHeight)) {
                                        val col = ((offset.x - startX) / colW).toInt().coerceIn(0, 3)
                                        toggleColumn(col + 1)
                                    }
                                }
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawYupanaBackground(size)
                        drawYupanaFrame(size)
                        val margin = 3f / 860f * size.width
                        val usableWidth = size.width - 2f * margin
                        val rowHeight = (size.height - 6f / 480f * size.height) / ROWS
                        val colW = usableWidth / 4f
                        val startX = margin
                        val startY = 3f / 480f * size.height

                        for (row in 0 until ROWS) {
                            val ry = startY + row * rowHeight
                            val rowState = rows.getOrNull(row) ?: YpRowState()

                            val activeRow = ROWS - 1 - stepRowIdx
                            val leftMarkers: Set<Int>
                            val rightMarkers: Set<Int>
                            val resultMarkers: Set<Int>

                            when (phase) {
                                0 -> {
                                    val redActiveRow = ROWS - 1 - stepRowIdx
                                    when {
                                        row == redActiveRow -> { leftMarkers = userRedColumns; rightMarkers = emptySet(); resultMarkers = emptySet() }
                                        else -> { leftMarkers = completedRedMarkers[row]; rightMarkers = emptySet(); resultMarkers = emptySet() }
                                    }
                                }
                                1 -> {
                                    val blueActiveRow = ROWS - 1 - stepRowIdx
                                    when {
                                        row == blueActiveRow -> { leftMarkers = completedRedMarkers[row]; rightMarkers = userBlueColumns; resultMarkers = emptySet() }
                                        else -> { leftMarkers = completedRedMarkers[row]; rightMarkers = completedBlueMarkers[row]; resultMarkers = emptySet() }
                                    }
                                }
                                else -> {
                                    when {
                                        row > activeRow -> {
                                            leftMarkers = emptySet()
                                            rightMarkers = emptySet()
                                            resultMarkers = getMarkersForDigit(rowState.resultDigit)
                                        }
                                        row == activeRow -> {
                                            leftMarkers = if (rowCompleted) emptySet() else completedRedMarkers[row] - consumedLeft
                                            rightMarkers = if (rowCompleted) emptySet() else completedBlueMarkers[row] - consumedRight
                                            resultMarkers = greenColumns
                                        }
                                        else -> {
                                            leftMarkers = completedRedMarkers[row]
                                            rightMarkers = completedBlueMarkers[row]
                                            resultMarkers = emptySet()
                                        }
                                    }
                                }
                            }

                            drawYupanaRow(
                                cellOriginX = startX,
                                cellOriginY = ry,
                                cellWidth = colW,
                                cellHeight = rowHeight,
                                canvasSize = size,
                                leftMarkers = leftMarkers,
                                rightMarkers = rightMarkers,
                                resultMarkers = resultMarkers,
                                carryMarker = phase >= 2 && carryIntoRow[row] > 0 && !consumedCarry &&
                                    ROWS - 1 - row == stepRowIdx && !rowCompleted
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (phase >= 2) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF2E241F),
                    ) {
                        Text(
                            text = "${exercise.left} + ${exercise.right} = ${exercise.left + exercise.right}",
                            color = Color(0xFFF2ECD8),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (stepRowIdx in 0 until ROWS) {
                    val placeIdx = ROWS - 1 - stepRowIdx
                    val target = rows[placeIdx].resultDigit
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                    Text(
                        text = when (phase) {
                            0 -> if (rowCompleted) "${rows[placeIdx].leftDigit} (${placeLabels[placeIdx]})" else ys.ypRedPhase.format(rows[placeIdx].leftDigit, placeLabels[placeIdx])
                            1 -> if (rowCompleted) "${rows[placeIdx].rightDigit} (${placeLabels[placeIdx]})" else ys.ypBluePhase.format(rows[placeIdx].rightDigit, placeLabels[placeIdx])
                               else -> if (rowCompleted) {
                                          val lv = rows[placeIdx].leftDigit
                                          val rBase = rows[placeIdx].rightDigit
                                          val carryIn = carryIntoRow[placeIdx]
                                          val total = lv + rBase + carryIn
                                          val lang = context.resources.configuration.locales[0].toLanguageTag()
                                          val wm = if (lang == "pt-BR") "Sem movimentos" else if (lang == "es-ES") "Sin movimiento" else "Without moves"
                                          val moves = writeSumOnYupana(wm, lv, rBase, carryIn)
                                          if (carryIn > 0)
                                              "$lv + $rBase + $carryIn ${ys.ypCarry} = $total (${placeLabels[placeIdx]}): ${moves.joinToString(", ")}"
                                          else
                                              "$lv + $rBase = $total (${placeLabels[placeIdx]}): ${moves.joinToString(", ")}"
                                    } else {
                                       val rawSum = rows[placeIdx].leftDigit + rows[placeIdx].rightDigit
                                       val carryFromPrev = carryIntoRow[placeIdx]
                                       val totalWithCarry = rawSum + carryFromPrev
                                       val curCarry = totalWithCarry / 10
                                       if (curCarry > 0) {
                                           val nextPlace = if (placeIdx > 0) placeLabels[placeIdx - 1] else ""
                                           if (carryFromPrev > 0)
                                               ys.ypCarryingCarry.format(placeLabels[placeIdx], rows[placeIdx].leftDigit, rows[placeIdx].rightDigit, carryFromPrev, totalWithCarry, target, nextPlace)
                                           else
                                               ys.ypCarrying.format(placeLabels[placeIdx], rows[placeIdx].leftDigit, rows[placeIdx].rightDigit, totalWithCarry, target, nextPlace)
                                       } else {
                                           if (carryFromPrev > 0)
                                               ys.ypAddToCarry.format(placeLabels[placeIdx], rows[placeIdx].leftDigit, rows[placeIdx].rightDigit, carryFromPrev, totalWithCarry, target)
                                           else
                                               ys.ypAddTo.format(placeLabels[placeIdx], rows[placeIdx].leftDigit, rows[placeIdx].rightDigit, rawSum, target)
                                       }
                                   }
                        },
                             style = MaterialTheme.typography.bodyMedium,
                             modifier = Modifier.padding(12.dp)
                         )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
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
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                        FilledTonalButton(
                            onClick = {
                                when (phase) {
                                    0 -> {
                                        if (stepRowIdx == -1) {
                                            advanceToPhase(0)
                                        } else {
                                            advanceToNextRow()
                                        }
                                    }
                                    1 -> {
                                        if (stepRowIdx == -1) {
                                            advanceToPhase(1)
                                        } else {
                                            advanceToNextRow()
                                        }
                                    }
                                    else -> {
                                        advanceToNextRow()
                                    }
                                }
                            },
                            enabled = rowCompleted,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ButtonYellow,
                                contentColor = OnButtonYellow
                            )
                        ) {
                            Text(
                                text = s.common.nextStep,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (finalCongratsShown) {
                            FilledTonalButton(
                                onClick = { resetCurrentExercise() },
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = ButtonYellow,
                                    contentColor = OnButtonYellow
                                )
                            ) {
                                Text(
                                    text = s.common.reset,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
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
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                if (feedbackMessage.isNotEmpty()) {
                    if (isNeutralFeedback) {
                        Text(
                            text = feedbackMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2E7D32),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                        )
                    } else {
                        Text(
                            text = feedbackMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isFeedbackPositive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(48.dp))
            }
        }

        if (!finalCongratsShown) {
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
                    expanded = showSourcesMenu && !showMainTextSubmenu && !showDhavitPremSubmenu,
                    onDismissRequest = { showSourcesMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Dhavit Prem") },
                        trailingIcon = {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                        },
                        onClick = { showDhavitPremSubmenu = true }
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
                            val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=b0bb8da3-ca00-453e-9060-9dfa767c80e2"))
                            Toast.makeText(ctx, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showMainTextSubmenu = false
                            uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=b0bb8da3-ca00-453e-9060-9dfa767c80e2")
                        }
                    )
                }

                DropdownMenu(
                    expanded = showSourcesMenu && showDhavitPremSubmenu,
                    onDismissRequest = { showDhavitPremSubmenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(s.common.copyUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showDhavitPremSubmenu = false
                            val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.researchgate.net/publication/334520917_TAWA_PUKLLAY_-_LA_ARITMETICA_INCA_DE_RECONOCIMIENTO_DE_FORMAS_Y_MOVIMIENTOS_OPERABLE_EN_PARALELO_Y_QUE_NO_REQUIERE_CALCULOS_NUMERICOS_MENTALES"))
                            Toast.makeText(ctx, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showDhavitPremSubmenu = false
                            uriHandler.openUri("https://www.researchgate.net/publication/334520917_TAWA_PUKLLAY_-_LA_ARITMETICA_INCA_DE_RECONOCIMIENTO_DE_FORMAS_Y_MOVIMIENTOS_OPERABLE_EN_PARALELO_Y_QUE_NO_REQUIERE_CALCULOS_NUMERICOS_MENTALES")
                        }
                    )
                }
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
        topLeft = Offset(3f / 860f * size.width, 3f / 480f * size.height),
        size = Size(size.width - 6f / 860f * size.width, size.height - 6f / 480f * size.height),
        style = Stroke(width = 2f / 480f * size.height)
    )
}

private fun DrawScope.drawYupanaRow(
    cellOriginX: Float,
    cellOriginY: Float,
    cellWidth: Float,
    cellHeight: Float,
    canvasSize: Size,
    leftMarkers: Set<Int>,
    rightMarkers: Set<Int>,
    resultMarkers: Set<Int>,
    carryMarker: Boolean = false,
) {
    val cw = canvasSize.width
    val ch = canvasSize.height

    val cornerRadius = CornerRadius(6f / 860f * cw)
    val borderWidth = 1.2f / 480f * ch
    val shadowOffset = 2f / 480f * ch

    for (col in 0..3) {
        val cellLeft = cellOriginX + col * cellWidth
        val cellTop = cellOriginY

        drawRoundRect(
            color = Color(0xFF000000).copy(alpha = 0.1f),
            topLeft = Offset(cellLeft + shadowOffset, cellTop + shadowOffset),
            size = Size(cellWidth, cellHeight),
            cornerRadius = cornerRadius
        )

        drawRoundRect(
            color = Color(0xFFFEF8E8),
            topLeft = Offset(cellLeft, cellTop),
            size = Size(cellWidth, cellHeight),
            cornerRadius = cornerRadius
        )

        drawRoundRect(
            color = Color(0xFFB48B5A),
            topLeft = Offset(cellLeft, cellTop),
            size = Size(cellWidth, cellHeight),
            cornerRadius = cornerRadius,
            style = Stroke(width = borderWidth)
        )
    }

    val dotRadius = minOf(cellWidth * 0.18f, cellHeight * 0.18f, 9f / 860f * cw)
    val markerRadius = dotRadius * 0.9f
    val markerGap = cellHeight * 0.12f
    val extraPx = with(density) { 3.dp.toPx() }

    val dotPositionsByCol = listOf(
        listOf(
            Offset(-dotRadius * 1.5f, -dotRadius * 2f),
            Offset(-dotRadius * 1.5f, 0f),
            Offset(-dotRadius * 1.5f, dotRadius * 2f),
            Offset(dotRadius * 1.5f, -dotRadius * 0.8f - extraPx / 2f),
            Offset(dotRadius * 1.5f, dotRadius * 0.8f + extraPx / 2f),
        ),
        listOf(
            Offset(0f, -dotRadius * 1.8f),
            Offset(0f, 0f),
            Offset(0f, dotRadius * 1.8f),
        ),
        listOf(
            Offset(0f, -dotRadius * 1.2f),
            Offset(0f, dotRadius * 1.2f),
        ),
        listOf(
            Offset(0f, 0f),
        ),
    )

    for (col in 0..3) {
        val cx = cellOriginX + col * cellWidth + cellWidth / 2f
        val cy = cellOriginY + cellHeight / 2f
        val colNum = col + 1
        val dotPositions = dotPositionsByCol[col]

        val hasLeftMarker = colNum in leftMarkers
        val hasRightMarker = colNum in rightMarkers
        val hasResultMarker = colNum in resultMarkers

        val topEdge = cellOriginY + cellHeight * 0.08f
        val bottomEdge = cellOriginY + cellHeight * 0.92f

        val topMarkerY = topEdge + markerGap
        val bottomMarkerY = bottomEdge - markerGap

        val leftActive = hasLeftMarker
        val rightActive = hasRightMarker
        val resultActive = hasResultMarker

        if (leftActive) {
            val my = topMarkerY - extraPx
            drawCircle(
                color = Color(0xFFC0392B),
                radius = markerRadius,
                center = Offset(cx, my)
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.2f),
                radius = markerRadius,
                center = Offset(cx, my),
                style = Stroke(width = 0.8f / 480f * ch)
            )
        }

        if (rightActive) {
            val my = bottomMarkerY + extraPx
            drawCircle(
                color = Color(0xFF2980B9),
                radius = markerRadius,
                center = Offset(cx, my)
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.2f),
                radius = markerRadius,
                center = Offset(cx, my),
                style = Stroke(width = 0.8f / 480f * ch)
            )
        }

        if (resultActive) {
            val my = topMarkerY - extraPx
            drawCircle(
                color = Color(0xFF27AE60),
                radius = markerRadius,
                center = Offset(cx, my)
            )
            drawCircle(
                color = Color(0xFFA8E6C1).copy(alpha = 0.4f),
                radius = markerRadius * 0.7f,
                center = Offset(cx, my)
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.2f),
                radius = markerRadius,
                center = Offset(cx, my),
                style = Stroke(width = 0.8f / 480f * ch)
            )
        }

        if (carryMarker && colNum == 4) {
            val carryY = bottomMarkerY + extraPx
            val carryX = if (rightActive) cx + markerRadius + with(density) { 5.dp.toPx() } + markerRadius * 1.1f else cx
            drawCircle(
                color = Color(0xFF808080),
                radius = markerRadius * 1.1f,
                center = Offset(carryX, carryY)
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.2f),
                radius = markerRadius * 1.1f,
                center = Offset(carryX, carryY),
                style = Stroke(width = 0.8f / 480f * ch)
            )
        }

        val dotColor = when (col) {
            0 -> Color(0xFF6B3A1A)
            1 -> Color(0xFF5B2E12)
            2 -> Color(0xFF4A2210)
            3 -> Color(0xFF3A1808)
            else -> Color.Gray
        }

        for (pos in dotPositions) {
            val dotCenter = Offset(cx + pos.x, cy + pos.y)
            drawCircle(
                color = dotColor,
                radius = dotRadius * 0.8f,
                center = dotCenter
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.15f),
                radius = dotRadius * 0.8f,
                center = dotCenter,
                style = Stroke(width = 0.6f / 440f * ch)
            )
        }
    }
}
