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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
import com.historytracers.app.ui.components.drawYupanaBackground
import com.historytracers.app.ui.components.drawYupanaFrame
import com.historytracers.app.ui.components.drawYupanaRow
import com.historytracers.app.ui.components.getMarkersForDigit
import com.historytracers.app.ui.features.practicingSubtractionYupanaScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val ROWS = 4
private const val MIN_LEVEL = 0
private const val MAX_LEVEL = 2

private val COL_VALUES = intArrayOf(5, 3, 2, 1)

private data class SubExercise(val a: Int, val b: Int) {
    val expected: Int get() = a - b
}

private data class Move(val name: String, val op: String)

private fun markersValue(markers: Set<Int>): Int = markers.sumOf { COL_VALUES[it - 1] }

private fun digitAt(value: Int, place: Int): Int {
    var v = value
    repeat(place) { v /= 10 }
    return v % 10
}

private fun digitPositions(n: Int): List<Int> {
    val positions = mutableListOf<Int>()
    var p = 0
    var pow = 1
    while (pow <= n) {
        if (digitAt(n, p) != 0) positions.add(p)
        p++
        pow *= 10
    }
    return positions
}

private fun generateExercise(level: Int): SubExercise = when (level) {
    0 -> {
        val a = Random.nextInt(1, 10)
        SubExercise(a, Random.nextInt(1, a + 1))
    }
    1 -> {
        val a = Random.nextInt(10, 100)
        SubExercise(a, Random.nextInt(1, a + 1))
    }
    else -> {
        val a = Random.nextInt(100, 1000)
        SubExercise(a, Random.nextInt(1, a + 1))
    }
}

private fun digitRep(v: Int): IntArray {
    val r = IntArray(4) // index 0=1, 1=2, 2=3, 3=5
    when (v) {
        1 -> r[0] = 1
        2 -> r[1] = 1
        3 -> r[2] = 1
        4 -> { r[2] = 1; r[0] = 1 }
        5 -> r[3] = 1
        6 -> { r[3] = 1; r[0] = 1 }
        7 -> { r[3] = 1; r[1] = 1 }
        8 -> { r[3] = 1; r[2] = 1 }
        9 -> { r[3] = 1; r[2] = 1; r[0] = 1 }
        10 -> r[3] = 2
    }
    return r
}

private fun getColumnMovements(dA: Int, dB: Int, didBorrow: Boolean): List<Move> {
    val moves = mutableListOf<Move>()
    if (dB == 0 && !didBorrow) return moves
    val r = digitRep(dA)
    val b = digitRep(dB)
    if (didBorrow) {
        r[3] += 2
        if (b[3] >= 1) {
            r[3] -= 1
            b[3] -= 1
            moves.add(Move("PISQA", "10 \u2212 5 = 5"))
        }
    }
    fun empty() = r[0] + r[1] + r[2] + r[3] == 0
    var guard = 0
    while (guard++ < 60) {
        when {
            r[0] >= 1 && b[0] >= 1 -> { r[0]--; b[0]--; moves.add(Move("CANCEL", "1 \u2212 1 = 0")) }
            r[1] >= 1 && b[1] >= 1 -> { r[1]--; b[1]--; moves.add(Move("CANCEL", "2 \u2212 2 = 0")) }
            r[2] >= 1 && b[2] >= 1 -> { r[2]--; b[2]--; moves.add(Move("CANCEL", "3 \u2212 3 = 0")) }
            r[3] >= 1 && b[3] >= 1 -> { r[3]--; b[3]--; moves.add(Move("CANCEL", "5 \u2212 5 = 0")) }
            r[2] >= 1 && b[1] >= 1 -> { r[2]--; b[1]--; r[0]++; moves.add(Move("PICHANA", "3 \u2212 2 = 1")) }
            r[3] >= 1 && b[1] >= 1 -> { r[3]--; b[1]--; r[2]++; moves.add(Move("PICHANA", "5 \u2212 2 = 3")) }
            r[3] >= 1 && b[2] >= 1 -> { r[3]--; b[2]--; r[1]++; moves.add(Move("PICHANA", "5 \u2212 3 = 2")) }
            r[2] >= 1 && b[0] >= 1 -> { r[2]--; b[0]--; r[1]++; moves.add(Move("ISKAY", "3 \u2212 1 = 2")) }
            r[3] >= 1 && b[0] >= 1 -> { r[3]--; b[0]--; r[2]++; r[0]++; moves.add(Move("KIMSA", "5 \u2212 1 = 4")) }
            r[1] >= 1 && b[0] >= 1 -> { r[1]--; b[0]--; r[0]++; moves.add(Move("PICHANA", "2 \u2212 1 = 1")) }
            else -> break
        }
        if (empty()) break
    }
    return moves
}

private const val ORIGINAL_TEXT_URL =
    "https://www.historytracers.org/index.html?page=class_content&arg=f17ec53f-af65-4708-bcc1-a140287d4dd9"
private const val DHAVIT_PREM_URL =
    "https://www.researchgate.net/publication/334520917_TAWA_PUKLLAY_-_LA_ARITMETICA_INCA_DE_RECONOCIMIENTO_DE_FORMAS_Y_MOVIMIENTOS_OPERABLE_EN_PARALELO_Y_QUE_NO_REQUIERE_CALCULOS_NUMERICOS_MENTALES"

@Composable
fun PracticingSubtractionYupanaScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = practicingSubtractionYupanaScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    val placeLabels = listOf(xs.levelUnits, xs.levelTens, xs.levelHundreds, xs.levelHundreds)

    var level by remember { mutableIntStateOf(MIN_LEVEL) }
    var exercise by remember { mutableStateOf(generateExercise(MIN_LEVEL)) }
    var red by remember { mutableStateOf(List(ROWS) { emptySet<Int>() }) }
    var blue by remember { mutableStateOf(List(ROWS) { emptySet<Int>() }) }
    var green by remember { mutableStateOf(List(ROWS) { emptySet<Int>() }) }
    var phase by remember { mutableIntStateOf(0) }
    var digitPositionsList by remember { mutableStateOf(emptyList<Int>()) }
    var digitIdx by remember { mutableIntStateOf(0) }
    var awaitingDigitStep by remember { mutableStateOf(false) }
    var evalCol by remember { mutableIntStateOf(0) }
    var expectBorrowRewrite by remember { mutableStateOf(false) }
    var borrowRewriteTargets by remember { mutableStateOf(emptyMap<Int, Int>()) }
    var borrowJustTaken by remember { mutableStateOf(false) }
    var awaitingMovementStep by remember { mutableStateOf(false) }
    var movementsDone by remember { mutableStateOf(emptyList<Move>()) }
    var stepMessage by remember { mutableStateOf("") }
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

    fun placeLabel(idx: Int): String = placeLabels.getOrElse(idx) { placeLabels.last() }

    fun rowRedValue(idx: Int): Int = markersValue(red.getOrElse(idx) { emptySet() })

    fun formatMoves(moves: List<Move>): String = moves.joinToString(", ") { "${it.name} (${it.op})" }

    fun showDigitInstruction() {
        feedbackMessage = ""
        isFeedbackPositive = false
        isNeutralFeedback = false
        val pos = digitPositionsList.getOrNull(digitIdx) ?: return
        stepMessage = if (phase == 0) {
            "${xs.stepPrefix} " + xs.stepFirstDigit.format(placeLabel(pos), digitAt(exercise.a, pos))
        } else {
            "${xs.stepPrefix} " + xs.stepSecondDigit.format(placeLabel(pos), digitAt(exercise.b, pos))
        }
    }

    fun showMovements(col: Int) {
        feedbackMessage = if (movementsDone.isEmpty()) {
            xs.noMovementsMessage.format(placeLabel(col))
        } else {
            xs.movementsMessage.format(placeLabel(col), formatMoves(movementsDone))
        }
        isFeedbackPositive = true
        isNeutralFeedback = false
    }

    fun finishGame() {
        phase = 3
        finalCongratsShown = true
        stepMessage = ""
        onScoreChanged(currentScore + 2)
        scope.launch { preferences.markYupanaSectionCompleted("practicing_subtraction_yupana") }
        scope.launch { preferences.recordLessonCompletion() }
        movementsDone = getColumnMovements(0, 0, false)
        feedbackMessage = xs.perfectMessage.format(exercise.a, exercise.b, exercise.expected)
        isFeedbackPositive = true
        isNeutralFeedback = false
    }

    fun showColumnSolve(col: Int, dA: Int, dB: Int, didBorrow: Boolean) {
        val borrowed = didBorrow || dB > dA
        val result = if (borrowed) dA + 10 - dB else dA - dB
        if (result == 0) {
            red = red.toMutableList().also { it[col] = emptySet() }
            blue = blue.toMutableList().also { it[col] = emptySet() }
            green = green.toMutableList().also { it[col] = emptySet() }
            movementsDone = getColumnMovements(dA, dB, borrowed)
            awaitingMovementStep = true
            showMovements(col)
            return
        }
        if (borrowed && result == 9) {
            green = green.toMutableList().also { it[col] = getMarkersForDigit(9) }
            movementsDone = getColumnMovements(dA, dB, borrowed)
            awaitingMovementStep = true
            showMovements(col)
            return
        }
        green = green.toMutableList().also { it[col] = emptySet() }
        awaitingMovementStep = false
        val displayA = if (borrowed) dA + 10 else dA
        stepMessage = "${xs.stepPrefix} " + xs.evalMessage.format(placeLabel(col), displayA, dB, result)
        feedbackMessage = ""
    }

    fun processNextColumn() {
        var col = evalCol
        while (col < ROWS) {
            val dA = rowRedValue(col)
            val dB = digitAt(exercise.b, col)
            if (dA != 0 || dB != 0) break
            col++
        }
        evalCol = col
        if (col >= ROWS) {
            finishGame()
            return
        }
        val dA = rowRedValue(col)
        val dB = digitAt(exercise.b, col)
        if (dB > dA) {
            val targets = mutableMapOf<Int, Int>()
            var t = col + 1
            while (t < ROWS && rowRedValue(t) == 0) {
                targets[t] = 9
                t++
            }
            if (t >= ROWS) t = ROWS - 1
            targets[t] = rowRedValue(t) - 1
            borrowRewriteTargets = targets.toMap()
            expectBorrowRewrite = true
            borrowJustTaken = false
            stepMessage = "${xs.stepPrefix} " + xs.borrowInstruction.format(placeLabel(col))
            feedbackMessage = ""
            return
        }
        showColumnSolve(col, dA, dB, false)
    }

    fun startEvaluation() {
        phase = 2
        evalCol = 0
        expectBorrowRewrite = false
        borrowRewriteTargets = emptyMap()
        borrowJustTaken = false
        awaitingMovementStep = false
        movementsDone = emptyList()
        stepMessage = ""
        feedbackMessage = ""
        processNextColumn()
    }

    fun startExercise() {
        exercise = generateExercise(level)
        red = List(ROWS) { emptySet() }
        blue = List(ROWS) { emptySet() }
        green = List(ROWS) { emptySet() }
        stepMessage = ""
        feedbackMessage = ""
        isFeedbackPositive = false
        isNeutralFeedback = false
        exerciseStarted = false
        awaitingDigitStep = false
        awaitingMovementStep = false
        expectBorrowRewrite = false
        borrowRewriteTargets = emptyMap()
        borrowJustTaken = false
        movementsDone = emptyList()
        finalCongratsShown = false
        showLastLevelMessage = false
        phase = 0
        digitPositionsList = digitPositions(exercise.a)
        digitIdx = 0
        if (digitPositionsList.isEmpty()) {
            phase = 1
            digitPositionsList = digitPositions(exercise.b)
            digitIdx = 0
            if (digitPositionsList.isEmpty()) startEvaluation()
        } else {
            showDigitInstruction()
        }
    }

    fun advanceDigitPhase() {
        digitIdx++
        if (digitIdx < digitPositionsList.size) {
            showDigitInstruction()
        } else if (phase == 0) {
            phase = 1
            digitPositionsList = digitPositions(exercise.b)
            digitIdx = 0
            if (digitPositionsList.isEmpty()) startEvaluation() else showDigitInstruction()
        } else {
            startEvaluation()
        }
    }

    fun onCellClick(stateRow: Int, col: Int) {
        if (finalCongratsShown || stateRow !in 0 until ROWS) return
        if (!exerciseStarted) exerciseStarted = true
        when (phase) {
            0 -> {
                val pos = digitPositionsList.getOrNull(digitIdx) ?: return
                if (stateRow != pos || awaitingDigitStep) return
                val cur = red[stateRow]
                val next = if (col in cur) cur - col else cur + col
                red = red.toMutableList().also { it[stateRow] = next }
                if (next == getMarkersForDigit(digitAt(exercise.a, stateRow))) {
                    awaitingDigitStep = true
                    feedbackMessage = xs.correctMessage
                    isFeedbackPositive = true
                }
            }
            1 -> {
                val pos = digitPositionsList.getOrNull(digitIdx) ?: return
                if (stateRow != pos || awaitingDigitStep) return
                val cur = blue[stateRow]
                val next = if (col in cur) cur - col else cur + col
                blue = blue.toMutableList().also { it[stateRow] = next }
                if (next == getMarkersForDigit(digitAt(exercise.b, stateRow))) {
                    awaitingDigitStep = true
                    feedbackMessage = xs.correctMessage
                    isFeedbackPositive = true
                }
            }
            else -> {
                if (expectBorrowRewrite) {
                    if (!borrowRewriteTargets.containsKey(stateRow)) return
                    val cur = red[stateRow]
                    val next = if (col in cur) cur - col else cur + col
                    red = red.toMutableList().also { it[stateRow] = next }
                    if (borrowRewriteTargets.all { (r, target) -> red[r] == getMarkersForDigit(target) }) {
                        expectBorrowRewrite = false
                        borrowJustTaken = true
                        awaitingMovementStep = true
                        feedbackMessage = xs.borrowConfirmMessage
                        isFeedbackPositive = true
                    }
                    return
                }
                if (awaitingMovementStep) return
                if (stateRow != evalCol) return
                val cur = green[stateRow]
                val next = if (col in cur) cur - col else cur + col
                green = green.toMutableList().also { it[stateRow] = next }
                val dA = rowRedValue(evalCol)
                val dB = digitAt(exercise.b, evalCol)
                val need = if (dB > dA) dA + 10 - dB else dA - dB
                if (next == getMarkersForDigit(need)) {
                    red = red.toMutableList().also { it[evalCol] = emptySet() }
                    blue = blue.toMutableList().also { it[evalCol] = emptySet() }
                    movementsDone = getColumnMovements(dA, dB, dB > dA)
                    awaitingMovementStep = true
                    showMovements(evalCol)
                }
            }
        }
    }

    fun onNextStep() {
        if (finalCongratsShown) return
        when (phase) {
            0, 1 -> {
                if (!awaitingDigitStep) return
                awaitingDigitStep = false
                advanceDigitPhase()
            }
            else -> {
                if (!awaitingMovementStep) return
                awaitingMovementStep = false
                feedbackMessage = ""
                isFeedbackPositive = false
                isNeutralFeedback = false
                if (borrowJustTaken) {
                    borrowJustTaken = false
                    val col = evalCol
                    showColumnSolve(col, rowRedValue(col), digitAt(exercise.b, col), true)
                } else {
                    evalCol++
                    processNextColumn()
                }
            }
        }
    }

    fun nextLevel() {
        if (level == MAX_LEVEL && !showLastLevelMessage) {
            showLastLevelMessage = true
            feedbackMessage = xs.lastLevelMessage
            return
        }
        showLastLevelMessage = false
        level = if (level >= MAX_LEVEL) MIN_LEVEL else level + 1
        startExercise()
    }

    LaunchedEffect(Unit) { startExercise() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
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
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = xs.instruction,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${s.common.levelPrefix}${placeLabels[level]}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF2E241F)) {
                    Text(
                        text = "${exercise.a} \u2212 ${exercise.b} = ?",
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
                        .pointerInput(phase, digitIdx, awaitingDigitStep, awaitingMovementStep, evalCol, expectBorrowRewrite, red, blue, green, borrowRewriteTargets) {
                            if (finalCongratsShown) return@pointerInput
                            detectTapGestures { offset ->
                                val margin = 3f / 860f * canvasSize.width
                                val usableWidth = canvasSize.width - 2f * margin
                                val colW = usableWidth / 4f
                                val startX = margin
                                val rowHeight = (canvasSize.height - 6f / 480f * canvasSize.height) / ROWS
                                val startY = 3f / 480f * canvasSize.height
                                if (offset.x in startX..(startX + 4f * colW) && offset.y in startY..(startY + ROWS * rowHeight)) {
                                    val col = ((offset.x - startX) / colW).toInt().coerceIn(0, 3)
                                    val displayRow = ((offset.y - startY) / rowHeight).toInt().coerceIn(0, ROWS - 1)
                                    onCellClick(ROWS - 1 - displayRow, col + 1)
                                }
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawYupanaBackground(size)
                        drawYupanaFrame(size)
                        val margin = 3f / 860f * size.width
                        val usableWidth = size.width - 2f * margin
                        val colW = usableWidth / 4f
                        val rowHeight = (size.height - 6f / 480f * size.height) / ROWS
                        val startX = margin
                        val startY = 3f / 480f * size.height
                        for (displayRow in 0 until ROWS) {
                            val idx = ROWS - 1 - displayRow
                            drawYupanaRow(
                                cellOriginX = startX,
                                cellOriginY = startY + displayRow * rowHeight,
                                cellWidth = colW,
                                cellHeight = rowHeight,
                                canvasSize = size,
                                leftMarkers = red.getOrElse(idx) { emptySet() },
                                rightMarkers = blue.getOrElse(idx) { emptySet() },
                                resultMarkers = green.getOrElse(idx) { emptySet() }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (stepMessage.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = stepMessage,
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
                            onClick = { startExercise() },
                            enabled = !exerciseStarted || finalCongratsShown,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ButtonYellow,
                                contentColor = OnButtonYellow
                            )
                        ) {
                            Text(s.common.newExercise, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                        FilledTonalButton(
                            onClick = { onNextStep() },
                            enabled = (phase <= 1 && awaitingDigitStep) || (phase >= 2 && awaitingMovementStep),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ButtonYellow,
                                contentColor = OnButtonYellow
                            )
                        ) {
                            Text(s.common.nextStep, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                    FilledTonalButton(
                        onClick = { nextLevel() },
                        enabled = !exerciseStarted || finalCongratsShown,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Text(s.common.nextLevel, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                    }
                }

                if (feedbackMessage.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = feedbackMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isFeedbackPositive || isNeutralFeedback) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.height(48.dp))
            }
        }

        if (!finalCongratsShown) {
            Box(modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 8.dp, start = 8.dp)) {
                val uriHandler = LocalUriHandler.current
                val ctx = LocalContext.current
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { showSourcesMenu = true }.padding(8.dp)
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
                        trailingIcon = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
                        onClick = { showDhavitPremSubmenu = true }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.originalText) },
                        trailingIcon = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
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
                            (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                                .setPrimaryClip(ClipData.newPlainText("URL", ORIGINAL_TEXT_URL))
                            Toast.makeText(ctx, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showMainTextSubmenu = false
                            uriHandler.openUri(ORIGINAL_TEXT_URL)
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
                            (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                                .setPrimaryClip(ClipData.newPlainText("URL", DHAVIT_PREM_URL))
                            Toast.makeText(ctx, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showDhavitPremSubmenu = false
                            uriHandler.openUri(DHAVIT_PREM_URL)
                        }
                    )
                }
            }
        }
    }
}
