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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
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
import kotlin.math.min
import kotlin.random.Random

private const val ROWS = 4
private const val MIN_MULTIPLIER = 1
private const val MAX_MULTIPLIER = 9

private val yupanaSelectors = listOf(
    -1, 4, 3, 2, 4, 1, 1, 1, 1, 1,
    -1, -1, -1, -1, 2, -1, 4, 3, 2, 2,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, 4
)

private data class MypRowState(val leftDigit: Int = 0, val rightDigit: Int = 0, val resultDigit: Int = 0)

private val placeLabels = listOf("thousands", "hundreds", "tens", "units")

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

private fun writeSumOnYupana(withoutMoves: String, lValue: Int, rBase: Int, carryIn: Int): List<String> {
    val m = mutableListOf<String>()
    if (lValue == 0 && rBase == 0 && carryIn == 0) { m.add(withoutMoves); return m.map { movementDesc(it) } }
    val leftMarkers = getMarkersForDigit(lValue)
    val rightMarkers = getMarkersForDigit(rBase)
    val count = intArrayOf(0, 0, 0, 0)
    for (c in leftMarkers) count[c - 1]++
    for (c in rightMarkers) count[c - 1]++
    count[3] += carryIn
    while (true) {
        var changed = false
        while (count[3] >= 3) { count[3] -= 3; count[1]++; m.add(ACT_KINKIN_TRIPLE); changed = true }
        while (count[2] >= 1 && count[3] >= 1) { count[2]--; count[3]--; count[1]++; m.add(ACT_PICHANA_ADD); changed = true }
        while (count[3] >= 2) { count[3] -= 2; count[2]++; m.add(ACT_KINKIN_PAIR); changed = true }
        while (count[2] >= 2) { count[2] -= 2; count[1]++; count[3]++; m.add(ACT_ISKAY); changed = true }
        while (count[1] >= 2) { count[1] -= 2; count[0]++; count[3]++; m.add(ACT_KIMSA); changed = true }
        while (count[1] >= 1 && count[2] >= 1) { count[1]--; count[2]--; count[0]++; m.add(ACT_PICHANA_COMBINE); changed = true }
        while (count[0] >= 2) { count[0] -= 2; m.add(ACT_PISQA); changed = true }
        if (!changed) break
    }
    if (m.isEmpty()) m.add(withoutMoves)
    return m.map { movementDesc(it) }
}

private fun numberToDigits(n: Int): List<Int> {
    val s = n.coerceIn(0, 9999).toString().padStart(ROWS, '0')
    return s.map { it - '0' }
}

private fun generateExercise(multiplier: Int): Pair<Int, Int> {
    val a = Random.nextInt(1, 10)
    return Pair(a, multiplier)
}

@Composable
fun PracticingMultiplicationYupanaScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var currentMultiplier by remember { mutableIntStateOf(MIN_MULTIPLIER) }
    var exercise by remember { mutableStateOf(generateExercise(currentMultiplier)) }
    var runningTotal by remember { mutableIntStateOf(0) }
    var iteration by remember { mutableIntStateOf(0) }
    var rows by remember { mutableStateOf(List(ROWS) { MypRowState() }) }
    var stepRowIdx by remember { mutableIntStateOf(-1) }
    var rowCompleted by remember { mutableStateOf(false) }
    var phase by remember { mutableIntStateOf(0) }
    var feedbackMessage by remember { mutableStateOf("") }
    var isFeedbackPositive by remember { mutableStateOf(false) }
    var isNeutralFeedback by remember { mutableStateOf(false) }
    var exerciseStarted by remember { mutableStateOf(false) }
    var showLastLevelMessage by remember { mutableStateOf(false) }
    var finalCongratsShown by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    var showDhavitPremSubmenu by remember { mutableStateOf(false) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var userRedColumns by remember { mutableStateOf(emptySet<Int>()) }
    var userBlueColumns by remember { mutableStateOf(emptySet<Int>()) }
    var greenColumns by remember { mutableStateOf(emptySet<Int>()) }
    var completedRedMarkers by remember { mutableStateOf(List(ROWS) { emptySet<Int>() }) }
    var completedBlueMarkers by remember { mutableStateOf(List(ROWS) { emptySet<Int>() }) }

    val lastMeaningfulStepRowIdx = remember(rows) {
        val idx = (0 until ROWS).firstOrNull { rows[it].resultDigit != 0 } ?: ROWS
        if (idx == ROWS) -1 else ROWS - 1 - idx
    }

    val carryIntoRow = remember(rows) {
        val c = IntArray(ROWS) { 0 }
        var carry = 0
        for (i in ROWS - 1 downTo 0) { c[i] = carry; val sum = rows[i].leftDigit + rows[i].rightDigit + carry; carry = sum / 10 }
        c
    }

    fun computeRows() {
        val runningDigits = numberToDigits(runningTotal); val aDigits = numberToDigits(exercise.first); val resDigits = numberToDigits(runningTotal + exercise.first)
        rows = List(ROWS) { i -> MypRowState(leftDigit = runningDigits[i], rightDigit = aDigits[i], resultDigit = resDigits[i]) }
    }

    fun advanceToPhase(newPhase: Int) {
        phase = newPhase; stepRowIdx = 0; rowCompleted = false; feedbackMessage = ""; isNeutralFeedback = false
        userRedColumns = emptySet(); userBlueColumns = emptySet(); greenColumns = emptySet()
        while (stepRowIdx <= lastMeaningfulStepRowIdx) {
            val idx = ROWS - 1 - stepRowIdx
            val digit = when (newPhase) { 0 -> rows[idx].leftDigit; 1 -> rows[idx].rightDigit; else -> rows[idx].resultDigit }
            if (getMarkersForDigit(digit).isNotEmpty()) break
            when (newPhase) { 0 -> completedRedMarkers = completedRedMarkers.toMutableList().also { it[idx] = emptySet() }; 1 -> completedBlueMarkers = completedBlueMarkers.toMutableList().also { it[idx] = emptySet() } }
            if (stepRowIdx >= lastMeaningfulStepRowIdx) {
                if (newPhase >= 2) { return }
                else { advanceToPhase(newPhase + 1); return }
            }
            stepRowIdx++
        }
    }

    fun startNextIteration() {
        computeRows(); advanceToPhase(0)
        if (exercise.second >= 2) { rowCompleted = true; feedbackMessage = s.yupana.ypCorrectMessage; isFeedbackPositive = true }
    }

    fun finishIteration() {
        runningTotal += exercise.first; iteration++
        val totalIterations = if (exercise.second == 1) 1 else exercise.second - 1
        if (iteration >= totalIterations) {
            rowCompleted = false; finalCongratsShown = true
            onScoreChanged(currentScore + 2)
            scope.launch { preferences.recordLessonCompletion() }
            feedbackMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 ${exercise.first} \u00D7 ${exercise.second} = $runningTotal\n${s.yupana.ypMultiplyPerfectMessage.format(exercise.first, exercise.second, runningTotal)}"
            isFeedbackPositive = true
        } else { startNextIteration() }
    }

    fun advanceToNextRow() {
        if (stepRowIdx >= lastMeaningfulStepRowIdx) {
            when (phase) { 0 -> advanceToPhase(1); 1 -> advanceToPhase(2) }
            return
        }
        stepRowIdx++; rowCompleted = false; feedbackMessage = ""; isNeutralFeedback = false
        when (phase) { 0 -> userRedColumns = emptySet(); 1 -> userBlueColumns = emptySet(); else -> greenColumns = emptySet() }
        val idx = ROWS - 1 - stepRowIdx
        val digit = when (phase) { 0 -> rows[idx].leftDigit; 1 -> rows[idx].rightDigit; else -> rows[idx].resultDigit }
        if (getMarkersForDigit(digit).isEmpty()) {
            val wm = if (context.resources.configuration.locales[0].toLanguageTag() == "pt-BR") "Sem movimentos" else if (context.resources.configuration.locales[0].toLanguageTag() == "es-ES") "Sin movimiento" else "Without moves"
            feedbackMessage = when (phase) { 0 -> s.yupana.ypNothingLeft.format(placeLabels[idx]); 1 -> s.yupana.ypNothingRight.format(placeLabels[idx]); else -> s.yupana.ypNothingResult.format(placeLabels[idx]) }
            isNeutralFeedback = true; rowCompleted = true
        }
    }

    fun toggleColumn(col: Int) {
        if (rowCompleted) return
        if (!exerciseStarted) exerciseStarted = true
        val activeIdx = ROWS - 1 - stepRowIdx
        when (phase) {
            0 -> {
                userRedColumns = if (col in userRedColumns) userRedColumns - col else userRedColumns + col
                if (userRedColumns == getMarkersForDigit(rows[activeIdx].leftDigit)) {
                    completedRedMarkers = completedRedMarkers.toMutableList().also { it[activeIdx] = userRedColumns }
                    rowCompleted = true; feedbackMessage = s.yupana.ypCorrectMessage; isFeedbackPositive = true
                }
            }
            1 -> {
                userBlueColumns = if (col in userBlueColumns) userBlueColumns - col else userBlueColumns + col
                if (userBlueColumns == getMarkersForDigit(rows[activeIdx].rightDigit)) {
                    completedBlueMarkers = completedBlueMarkers.toMutableList().also { it[activeIdx] = userBlueColumns }
                    rowCompleted = true; feedbackMessage = s.yupana.ypCorrectMessage; isFeedbackPositive = true
                }
            }
            2 -> {
                greenColumns = if (col in greenColumns) greenColumns - col else greenColumns + col
                if (greenColumns == getMarkersForDigit(rows[activeIdx].resultDigit)) {
                    rowCompleted = true
                    if (stepRowIdx == lastMeaningfulStepRowIdx) {
                        val lang = context.resources.configuration.locales[0].toLanguageTag()
                        val wm = if (lang == "pt-BR") "Sem movimentos" else if (lang == "es-ES") "Sin movimiento" else "Without moves"
                        val lv = rows[activeIdx].leftDigit; val rBase = rows[activeIdx].rightDigit; val carryIn = carryIntoRow[activeIdx]
                        val total = lv + rBase + carryIn
                        val moves = writeSumOnYupana(wm, lv, rBase, carryIn)
                        feedbackMessage = s.yupana.ypCorrectMessage + "\n${moves.joinToString(", ")}"
                        isFeedbackPositive = true
                        finishIteration()
                    } else {
                        feedbackMessage = s.yupana.ypCorrectMessage; isFeedbackPositive = true
                    }
                }
            }
        }
    }

    fun setupExercise() {
        exercise = generateExercise(currentMultiplier)
        runningTotal = if (exercise.second >= 2) exercise.first else 0; iteration = 0; computeRows()
        rowCompleted = false; feedbackMessage = ""; isFeedbackPositive = false; isNeutralFeedback = false
        exerciseStarted = false; finalCongratsShown = false; showLastLevelMessage = false
        userRedColumns = emptySet(); userBlueColumns = emptySet(); greenColumns = emptySet()
        completedRedMarkers = List(ROWS) { emptySet() }; completedBlueMarkers = List(ROWS) { emptySet() }
        if (exercise.second == 1) advanceToPhase(2) else advanceToPhase(0)
    }

    fun toggleLevel() {
        if (currentMultiplier == MAX_MULTIPLIER && !showLastLevelMessage) {
            showLastLevelMessage = true; feedbackMessage = s.yupana.ypLastLevelMessage; return
        }
        showLastLevelMessage = false
        currentMultiplier = if (currentMultiplier >= MAX_MULTIPLIER) MIN_MULTIPLIER else currentMultiplier + 1
        setupExercise()
    }

    LaunchedEffect(Unit) { setupExercise() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.common.back) }
                    Text(text = s.yupana.multiplyingWithYupana, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(start = 8.dp))
                }
            }
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(8.dp))
                Text(text = s.yupana.ypMultiplyInstruction, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                Spacer(Modifier.height(4.dp))
                Text(text = "${s.common.levelPrefix}$currentMultiplier", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 4.dp))
                Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF2E241F)) {
                    Text(text = "${exercise.first} \u00D7 ${exercise.second} = ?", color = Color(0xFFF2ECD8), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
                }
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).aspectRatio(860f / 480f).onSizeChanged { canvasSize = Size(it.width.toFloat(), it.height.toFloat()) }.pointerInput(phase, stepRowIdx, rowCompleted) {
                    if (stepRowIdx in 0 until ROWS && !rowCompleted) {
                        val activeIdx = ROWS - 1 - stepRowIdx; val digit = when (phase) { 0 -> rows[activeIdx].leftDigit; 1 -> rows[activeIdx].rightDigit; else -> rows[activeIdx].resultDigit }
                        if (getMarkersForDigit(digit).isEmpty()) {
                            val wm = if (context.resources.configuration.locales[0].toLanguageTag() == "pt-BR") "Sem movimentos" else if (context.resources.configuration.locales[0].toLanguageTag() == "es-ES") "Sin movimiento" else "Without moves"
                            feedbackMessage = when (phase) { 0 -> s.yupana.ypNothingLeft.format(placeLabels[activeIdx]); 1 -> s.yupana.ypNothingRight.format(placeLabels[activeIdx]); else -> s.yupana.ypNothingResult.format(placeLabels[activeIdx]) }
                            isNeutralFeedback = true; rowCompleted = true; return@pointerInput
                        }
                        detectTapGestures { offset ->
                            val margin = 3f / 860f * canvasSize.width; val usableWidth = canvasSize.width - 2f * margin; val colW = usableWidth / 4f; val startX = margin
                            val rowHeight = (canvasSize.height - 6f / 480f * canvasSize.height) / ROWS; val startY = 3f / 480f * canvasSize.height
                            val activeRow = ROWS - 1 - stepRowIdx
                            val rowTop = startY + activeRow * rowHeight
                            if (offset.x in startX..(startX + 4f * colW) && offset.y in rowTop..(rowTop + rowHeight)) {
                                val col = ((offset.x - startX) / colW).toInt().coerceIn(0, 3); toggleColumn(col + 1)
                            }
                        }
                    }
                }) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawYpMultiplyBackground(size); drawYpMultiplyFrame(size)
                        val margin = 3f / 860f * size.width; val usableWidth = size.width - 2f * margin; val rowHeight = (size.height - 6f / 480f * size.height) / ROWS; val colW = usableWidth / 4f; val startX = margin; val startY = 3f / 480f * size.height
                        for (row in 0 until ROWS) {
                            val ry = startY + row * rowHeight; val rowState = rows.getOrNull(row) ?: MypRowState()
                            val activeRow = ROWS - 1 - stepRowIdx
                            val leftMarkers: Set<Int>; val rightMarkers: Set<Int>; val resultMarkers: Set<Int>
                            when (phase) {
                                0 -> { val ra = ROWS - 1 - stepRowIdx; when { row == ra -> { leftMarkers = userRedColumns; rightMarkers = emptySet(); resultMarkers = emptySet() } else -> { leftMarkers = completedRedMarkers[row]; rightMarkers = emptySet(); resultMarkers = emptySet() } } }
                                1 -> { val ba = ROWS - 1 - stepRowIdx; when { row == ba -> { leftMarkers = completedRedMarkers[row]; rightMarkers = userBlueColumns; resultMarkers = emptySet() } else -> { leftMarkers = completedRedMarkers[row]; rightMarkers = completedBlueMarkers[row]; resultMarkers = emptySet() } } }
                                else -> { when { row > activeRow -> { leftMarkers = emptySet(); rightMarkers = emptySet(); resultMarkers = getMarkersForDigit(rowState.resultDigit) }; row == activeRow -> { leftMarkers = if (rowCompleted) emptySet() else completedRedMarkers[row]; rightMarkers = if (rowCompleted) emptySet() else completedBlueMarkers[row]; resultMarkers = greenColumns }; else -> { leftMarkers = completedRedMarkers[row]; rightMarkers = completedBlueMarkers[row]; resultMarkers = emptySet() } } }
                            }
                            drawYpMultiplyRow(cellOriginX = startX, cellOriginY = ry, cellWidth = colW, cellHeight = rowHeight, canvasSize = size, leftMarkers = leftMarkers, rightMarkers = rightMarkers, resultMarkers = resultMarkers, carryMarker = phase >= 2 && carryIntoRow[row] > 0 && ROWS - 1 - row == stepRowIdx && !rowCompleted)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                if (stepRowIdx in 0 until ROWS) {
                    val placeIdx = ROWS - 1 - stepRowIdx
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(horizontal = 16.dp)) {
                        val target = rows[placeIdx].resultDigit
                        val instruction = when (phase) {
                            0 -> s.yupana.ypRedPhase.format(rows[placeIdx].leftDigit, placeLabels[placeIdx])
                            1 -> s.yupana.ypBluePhase.format(rows[placeIdx].rightDigit, placeLabels[placeIdx])
                            else -> if (rowCompleted) {
                                val lv = rows[placeIdx].leftDigit; val rBase = rows[placeIdx].rightDigit; val carryIn = carryIntoRow[placeIdx]; val total = lv + rBase + carryIn
                                val lang = context.resources.configuration.locales[0].toLanguageTag()
                                val wm = if (lang == "pt-BR") "Sem movimentos" else if (lang == "es-ES") "Sin movimiento" else "Without moves"
                                val moves = writeSumOnYupana(wm, lv, rBase, carryIn)
                                "$lv + $rBase ${if (carryIn > 0) "+ $carryIn (${s.yupana.ypCarry}) " else ""}= $total (${placeLabels[placeIdx]}): ${moves.joinToString(", ")}"
                            } else {
                                val rawSum = rows[placeIdx].leftDigit + rows[placeIdx].rightDigit; val carryFromPrev = carryIntoRow[placeIdx]; val totalWithCarry = rawSum + carryFromPrev; val curCarry = totalWithCarry / 10; val nextPlace = if (placeIdx > 0) placeLabels[placeIdx - 1] else ""
                                when { curCarry > 0 -> if (carryFromPrev > 0) s.yupana.ypCarryingCarry.format(placeLabels[placeIdx], rows[placeIdx].leftDigit, rows[placeIdx].rightDigit, carryFromPrev, totalWithCarry, target, nextPlace) else s.yupana.ypCarrying.format(placeLabels[placeIdx], rows[placeIdx].leftDigit, rows[placeIdx].rightDigit, totalWithCarry, target, nextPlace)
                                    else -> if (carryFromPrev > 0) s.yupana.ypAddToCarry.format(placeLabels[placeIdx], rows[placeIdx].leftDigit, rows[placeIdx].rightDigit, carryFromPrev, totalWithCarry, target) else s.yupana.ypAddTo.format(placeLabels[placeIdx], rows[placeIdx].leftDigit, rows[placeIdx].rightDigit, rawSum, target) }
                            }
                        }
                        Text(text = instruction, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalButton(onClick = { setupExercise() }, enabled = !exerciseStarted || finalCongratsShown, shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.filledTonalButtonColors(containerColor = ButtonYellow, contentColor = OnButtonYellow)) {
                            Text(text = s.common.newExercise, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                        FilledTonalButton(onClick = { when (phase) { 0 -> if (stepRowIdx == -1) advanceToPhase(0) else advanceToNextRow(); 1 -> if (stepRowIdx == -1) advanceToPhase(1) else advanceToNextRow(); else -> advanceToNextRow() } }, enabled = rowCompleted, shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.filledTonalButtonColors(containerColor = ButtonYellow, contentColor = OnButtonYellow)) {
                            Text(text = s.common.nextStep, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (finalCongratsShown) {
                            FilledTonalButton(onClick = { setupExercise() }, shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.filledTonalButtonColors(containerColor = ButtonYellow, contentColor = OnButtonYellow)) {
                                Text(text = s.common.reset, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                            }
                        }
                        FilledTonalButton(onClick = { toggleLevel() }, enabled = !exerciseStarted || finalCongratsShown, shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.filledTonalButtonColors(containerColor = ButtonYellow, contentColor = OnButtonYellow)) {
                            Text(text = s.common.nextLevel, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                }
                if (feedbackMessage.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(text = feedbackMessage, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (isFeedbackPositive || isNeutralFeedback) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
                }
                Spacer(Modifier.height(48.dp))
            }
        }
        if (!finalCongratsShown) {
            Box(modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 8.dp, start = 8.dp)) {
                val uriHandler = LocalUriHandler.current; val ctx = LocalContext.current
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { showSourcesMenu = true }.padding(8.dp)) {
                    Icon(Icons.Filled.Book, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp)); Text(text = s.common.sources, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                DropdownMenu(expanded = showSourcesMenu && !showMainTextSubmenu && !showDhavitPremSubmenu, onDismissRequest = { showSourcesMenu = false }) {
                    DropdownMenuItem(text = { Text("Dhavit Prem") }, trailingIcon = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) }, onClick = { showDhavitPremSubmenu = true })
                    DropdownMenuItem(text = { Text(s.common.originalText) }, trailingIcon = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) }, onClick = { showMainTextSubmenu = true })
                }
                DropdownMenu(expanded = showSourcesMenu && showMainTextSubmenu, onDismissRequest = { showMainTextSubmenu = false }) {
                    DropdownMenuItem(text = { Text(s.common.copyUrl) }, onClick = { showSourcesMenu = false; showMainTextSubmenu = false; (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=9a7a978b-3fac-422c-8e41-3ef1a24e88f3")); Toast.makeText(ctx, s.common.copyUrl, Toast.LENGTH_SHORT).show() })
                    DropdownMenuItem(text = { Text(s.common.goToUrl) }, onClick = { showSourcesMenu = false; showMainTextSubmenu = false; uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=9a7a978b-3fac-422c-8e41-3ef1a24e88f3") })
                }
                DropdownMenu(expanded = showSourcesMenu && showDhavitPremSubmenu, onDismissRequest = { showDhavitPremSubmenu = false }) {
                    DropdownMenuItem(text = { Text(s.common.copyUrl) }, onClick = { showSourcesMenu = false; showDhavitPremSubmenu = false; (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("URL", "https://www.researchgate.net/publication/334520917_TAWA_PUKLLAY_-_LA_ARITMETICA_INCA_DE_RECONOCIMIENTO_DE_FORMAS_Y_MOVIMIENTOS_OPERABLE_EN_PARALELO_Y_QUE_NO_REQUIERE_CALCULOS_NUMERICOS_MENTALES")); Toast.makeText(ctx, s.common.copyUrl, Toast.LENGTH_SHORT).show() })
                    DropdownMenuItem(text = { Text(s.common.goToUrl) }, onClick = { showSourcesMenu = false; showDhavitPremSubmenu = false; uriHandler.openUri("https://www.researchgate.net/publication/334520917_TAWA_PUKLLAY_-_LA_ARITMETICA_INCA_DE_RECONOCIMIENTO_DE_FORMAS_Y_MOVIMIENTOS_OPERABLE_EN_PARALELO_Y_QUE_NO_REQUIERE_CALCULOS_NUMERICOS_MENTALES") })
                }
            }
        }
    }
}

private fun DrawScope.drawYpMultiplyBackground(size: Size) { drawRect(color = Color(0xFFFEF5E0), size = size) }

private fun DrawScope.drawYpMultiplyFrame(size: Size) { drawRect(color = Color(0xFFB48B5A), topLeft = Offset(3f / 860f * size.width, 3f / 480f * size.height), size = Size(size.width - 6f / 860f * size.width, size.height - 6f / 480f * size.height), style = Stroke(width = 2f / 480f * size.height)) }

private fun DrawScope.drawYpMultiplyRow(cellOriginX: Float, cellOriginY: Float, cellWidth: Float, cellHeight: Float, canvasSize: Size, leftMarkers: Set<Int> = emptySet(), rightMarkers: Set<Int> = emptySet(), resultMarkers: Set<Int> = emptySet(), carryMarker: Boolean = false) {
    val cw = canvasSize.width; val ch = canvasSize.height
    val cornerRadius = CornerRadius(6f / 860f * cw); val borderWidth = 1.2f / 480f * ch; val shadowOffset = 2f / 480f * ch
    for (col in 0..3) {
        val cellLeft = cellOriginX + col * cellWidth; val cellTop = cellOriginY
        drawRoundRect(color = Color(0xFF000000).copy(alpha = 0.1f), topLeft = Offset(cellLeft + shadowOffset, cellTop + shadowOffset), size = Size(cellWidth, cellHeight), cornerRadius = cornerRadius)
        drawRoundRect(color = Color(0xFFFEF8E8), topLeft = Offset(cellLeft, cellTop), size = Size(cellWidth, cellHeight), cornerRadius = cornerRadius)
        drawRoundRect(color = Color(0xFFB48B5A), topLeft = Offset(cellLeft, cellTop), size = Size(cellWidth, cellHeight), cornerRadius = cornerRadius, style = Stroke(width = borderWidth))
    }
    val dotRadius = minOf(cellWidth * 0.18f, cellHeight * 0.18f, 9f / 860f * cw); val markerRadius = dotRadius * 0.9f; val markerGap = cellHeight * 0.12f; val extraPx = with(density) { 3.dp.toPx() }
    val dotPositionsByCol = listOf(
        listOf(Offset(-dotRadius * 1.5f, -dotRadius * 2f), Offset(-dotRadius * 1.5f, 0f), Offset(-dotRadius * 1.5f, dotRadius * 2f), Offset(dotRadius * 1.5f, -dotRadius * 0.8f - extraPx / 2f), Offset(dotRadius * 1.5f, dotRadius * 0.8f + extraPx / 2f)),
        listOf(Offset(0f, -dotRadius * 1.8f), Offset(0f, 0f), Offset(0f, dotRadius * 1.8f)),
        listOf(Offset(0f, -dotRadius * 1.2f), Offset(0f, dotRadius * 1.2f)),
        listOf(Offset(0f, 0f)),
    )
    for (col in 0..3) {
        val cx = cellOriginX + col * cellWidth + cellWidth / 2f; val cy = cellOriginY + cellHeight / 2f; val colNum = col + 1; val dotPositions = dotPositionsByCol[col]
        val topEdge = cellOriginY + cellHeight * 0.08f; val bottomEdge = cellOriginY + cellHeight * 0.92f; val topMarkerY = topEdge + markerGap; val bottomMarkerY = bottomEdge - markerGap
        val leftActive = colNum in leftMarkers; val rightActive = colNum in rightMarkers; val resultActive = colNum in resultMarkers
        if (leftActive) { val my = topMarkerY - extraPx; drawCircle(color = Color(0xFFC0392B), radius = markerRadius, center = Offset(cx, my)); drawCircle(color = Color(0xFF000000).copy(alpha = 0.2f), radius = markerRadius, center = Offset(cx, my), style = Stroke(width = 0.8f / 480f * ch)) }
        if (rightActive) { val my = bottomMarkerY + extraPx; drawCircle(color = Color(0xFF2980B9), radius = markerRadius, center = Offset(cx, my)); drawCircle(color = Color(0xFF000000).copy(alpha = 0.2f), radius = markerRadius, center = Offset(cx, my), style = Stroke(width = 0.8f / 480f * ch)) }
        if (resultActive) { val my = topMarkerY - extraPx; drawCircle(color = Color(0xFF27AE60), radius = markerRadius, center = Offset(cx, my)); drawCircle(color = Color(0xFFA8E6C1).copy(alpha = 0.4f), radius = markerRadius * 0.7f, center = Offset(cx, my)); drawCircle(color = Color(0xFF000000).copy(alpha = 0.2f), radius = markerRadius, center = Offset(cx, my), style = Stroke(width = 0.8f / 480f * ch)) }
        if (carryMarker && colNum == 4) { val carryY = bottomMarkerY + extraPx; val carryX = if (rightActive) cx + markerRadius + with(density) { 5.dp.toPx() } + markerRadius * 1.1f else cx; drawCircle(color = Color(0xFF808080), radius = markerRadius * 1.1f, center = Offset(carryX, carryY)); drawCircle(color = Color(0xFF000000).copy(alpha = 0.2f), radius = markerRadius * 1.1f, center = Offset(carryX, carryY), style = Stroke(width = 0.8f / 480f * ch)) }
        val dotColor = when (col) { 0 -> Color(0xFF6B3A1A); 1 -> Color(0xFF5B2E12); 2 -> Color(0xFF4A2210); 3 -> Color(0xFF3A1808); else -> Color.Gray }
        for (pos in dotPositions) { val dotCenter = Offset(cx + pos.x, cy + pos.y); drawCircle(color = dotColor, radius = dotRadius * 0.8f, center = dotCenter); drawCircle(color = Color(0xFF000000).copy(alpha = 0.15f), radius = dotRadius * 0.8f, center = dotCenter, style = Stroke(width = 0.6f / 440f * ch)) }
    }
}
