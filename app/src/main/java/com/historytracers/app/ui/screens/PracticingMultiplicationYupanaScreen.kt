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

private data class MypExercise(val a: Int, val b: Int)

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
    if (lValue == 0 && rBase == 0 && carryIn == 0) {
        m.add(withoutMoves)
        return m.map { movementDesc(it) }
    }
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

private fun computeAddMovements(prevTotal: Int, addValue: Int, withoutMoves: String): List<String> {
    val allMoves = mutableListOf<String>()
    var carry = 0
    for (row in ROWS - 1 downTo 0) {
        val place = ROWS - 1 - row
        val divisor = Math.pow(10.0, place.toDouble()).toInt()
        val prevDigit = (prevTotal / divisor) % 10
        val addDigit = (addValue / divisor) % 10
        val rowMoves = writeSumOnYupana(withoutMoves, prevDigit, addDigit, carry)
        val sum = prevDigit + addDigit + carry
        carry = sum / 10
        if (rowMoves.size == 1 && rowMoves[0] == withoutMoves && allMoves.isNotEmpty()) continue
        allMoves.addAll(rowMoves)
    }
    if (allMoves.isEmpty()) allMoves.add(withoutMoves)
    return allMoves.distinct()
}

private fun numberToDigits(n: Int): List<Int> {
    val clamped = n.coerceIn(0, 9999)
    val s = clamped.toString().padStart(ROWS, '0')
    return s.map { it - '0' }
}

private fun generateMypExercise(multiplier: Int): MypExercise {
    val a = Random.nextInt(1, 10)
    return MypExercise(a, multiplier)
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
    var exercise by remember { mutableStateOf(generateMypExercise(currentMultiplier)) }
    var runningTotal by remember { mutableIntStateOf(0) }
    var iteration by remember { mutableIntStateOf(0) }
    var phase by remember { mutableIntStateOf(0) }
    var rowCompleted by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var isFeedbackPositive by remember { mutableStateOf(false) }
    var exerciseStarted by remember { mutableStateOf(false) }
    var showLastLevelMessage by remember { mutableStateOf(false) }
    var finalCongratsShown by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    var showDhavitPremSubmenu by remember { mutableStateOf(false) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var redMarkers by remember { mutableStateOf(List(ROWS) { emptySet<Int>() }) }
    var blueColumns by remember { mutableStateOf(emptySet<Int>()) }
    var greenMarkers by remember { mutableStateOf(List(ROWS) { emptySet<Int>() }) }
    var currentMovesText by remember { mutableStateOf("") }

    fun expectedGreenForResult(): List<Set<Int>> {
        val result = exercise.a * (iteration + 1)
        val digits = numberToDigits(result)
        return digits.map { getMarkersForDigit(it) }
    }

    fun nextIteration() {
        iteration++
        if (iteration >= exercise.b) {
            finalCongratsShown = true
            onScoreChanged(currentScore + 2)
            scope.launch { preferences.recordLessonCompletion() }
            feedbackMessage = s.yupana.ypMultiplyPerfectMessage.format(exercise.a, exercise.b, runningTotal)
            isFeedbackPositive = true
        } else {
            val lang = context.resources.configuration.locales[0].toLanguageTag()
            val wm = if (lang == "pt-BR") "Sem movimentos" else if (lang == "es-ES") "Sin movimiento" else "Without moves"
            val movements = computeAddMovements(runningTotal, exercise.a, wm)
            currentMovesText = movements.joinToString("; ")
            redMarkers = greenMarkers.map { it.toSet() }
            phase = 0
            rowCompleted = true
            feedbackMessage = s.yupana.ypCorrectMessage
            isFeedbackPositive = true
            blueColumns = emptySet()
            greenMarkers = List(ROWS) { emptySet() }
        }
    }

    fun completeGreen(resultMarkers: List<Set<Int>>) {
        greenMarkers = resultMarkers
        runningTotal = exercise.a * (iteration + 1)
        val lang = context.resources.configuration.locales[0].toLanguageTag()
        val wm = if (lang == "pt-BR") "Sem movimentos" else if (lang == "es-ES") "Sin movimiento" else "Without moves"
        val movements = computeAddMovements(runningTotal - exercise.a, exercise.a, wm)
        currentMovesText = movements.joinToString("; ")
        rowCompleted = true
        feedbackMessage = s.yupana.ypCorrectMessage
        isFeedbackPositive = true
    }

    fun toggleColumn(col: Int) {
        if (rowCompleted) return
        if (!exerciseStarted) exerciseStarted = true
        if (exercise.b == 1) {
            val current = greenMarkers[ROWS - 1]
            greenMarkers = greenMarkers.toMutableList().also { it[ROWS - 1] = if (col in current) current - col else current + col }
            val expected = expectedGreenForResult()
            if (greenMarkers == expected) {
                runningTotal = exercise.a
                rowCompleted = true
                val lang = context.resources.configuration.locales[0].toLanguageTag()
                val wm = if (lang == "pt-BR") "Sem movimentos" else if (lang == "es-ES") "Sin movimiento" else "Without moves"
                currentMovesText = wm
                feedbackMessage = s.yupana.ypCorrectMessage
                isFeedbackPositive = true
            }
        } else {
            when (phase) {
                0 -> {
                    val current = redMarkers[ROWS - 1]
                    redMarkers = redMarkers.toMutableList().also { it[ROWS - 1] = if (col in current) current - col else current + col }
                    if (redMarkers[ROWS - 1] == getMarkersForDigit(exercise.a)) {
                        rowCompleted = true
                        feedbackMessage = s.yupana.ypCorrectMessage
                        isFeedbackPositive = true
                    }
                }
                1 -> {
                    blueColumns = if (col in blueColumns) blueColumns - col else blueColumns + col
                    if (blueColumns == getMarkersForDigit(exercise.a)) {
                        rowCompleted = true
                        feedbackMessage = s.yupana.ypCorrectMessage
                        isFeedbackPositive = true
                    }
                }
                2 -> {
                    val current = greenMarkers[ROWS - 1]
                    greenMarkers = greenMarkers.toMutableList().also { it[ROWS - 1] = if (col in current) current - col else current + col }
                    val expected = expectedGreenForResult()
                    if (greenMarkers == expected) {
                        completeGreen(greenMarkers)
                    }
                }
            }
        }
    }

    fun advancePhase() {
        if (exercise.b == 1) {
            if (rowCompleted && !finalCongratsShown) {
                finalCongratsShown = true
                onScoreChanged(currentScore + 2)
                scope.launch { preferences.recordLessonCompletion() }
                feedbackMessage = s.yupana.ypMultiplyPerfectMessage.format(exercise.a, exercise.b, exercise.a)
                isFeedbackPositive = true
            }
            return
        }
        phase++
        rowCompleted = false
        feedbackMessage = ""
        currentMovesText = ""
        blueColumns = emptySet()
        if (phase > 2) {
            val result = exercise.a * (iteration + 1)
            val markers = numberToDigits(result).map { getMarkersForDigit(it) }
            greenMarkers = markers
            runningTotal = result
            nextIteration()
        } else if (phase == 0 && runningTotal > 0) {
            redMarkers = greenMarkers.map { it.toSet() }
            rowCompleted = true
            feedbackMessage = s.yupana.ypCorrectMessage
            isFeedbackPositive = true
        } else if (phase == 0) {
            redMarkers = List(ROWS) { emptySet() }
            greenMarkers = List(ROWS) { emptySet() }
        }
    }

    fun resetExercise() {
        exercise = generateMypExercise(currentMultiplier)
        runningTotal = 0
        iteration = 0
        phase = 0
        rowCompleted = false
        feedbackMessage = ""
        isFeedbackPositive = false
        exerciseStarted = false
        finalCongratsShown = false
        showLastLevelMessage = false
        redMarkers = List(ROWS) { emptySet() }
        blueColumns = emptySet()
        greenMarkers = List(ROWS) { emptySet() }
        currentMovesText = ""
    }

    fun resetCurrentExercise() {
        val prevTotal = runningTotal - exercise.a
        runningTotal = if (prevTotal >= 0) prevTotal else 0
        iteration = (iteration - 1).coerceAtLeast(0)
        phase = 0
        rowCompleted = false
        feedbackMessage = ""
        isFeedbackPositive = false
        exerciseStarted = false
        finalCongratsShown = false
        showLastLevelMessage = false
        redMarkers = List(ROWS) { emptySet() }
        blueColumns = emptySet()
        greenMarkers = List(ROWS) { emptySet() }
        currentMovesText = ""
    }

    fun toggleLevel() {
        if (currentMultiplier == MAX_MULTIPLIER && !showLastLevelMessage) {
            showLastLevelMessage = true
            feedbackMessage = s.yupana.ypLastLevelMessage
            return
        }
        showLastLevelMessage = false
        currentMultiplier = if (currentMultiplier >= MAX_MULTIPLIER) MIN_MULTIPLIER else currentMultiplier + 1
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
                        text = s.yupana.multiplyingWithYupana,
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
                    text = s.yupana.ypMultiplyInstruction,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${s.common.levelPrefix}$currentMultiplier",
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
                        text = "${exercise.a} \u00D7 ${exercise.b} = ?",
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
                        .pointerInput(phase, rowCompleted) {
                            if (!rowCompleted) {
                                detectTapGestures { offset ->
                                    val margin = 3f / 860f * canvasSize.width
                                    val usableWidth = canvasSize.width - 2f * margin
                                    val colW = usableWidth / 4f
                                    val startX = margin
                                    val rowHeight = (canvasSize.height - 6f / 480f * canvasSize.height) / ROWS
                                    val startY = 3f / 480f * canvasSize.height
                                    if (offset.x in startX..(startX + 4f * colW) && offset.y in startY..(startY + ROWS * rowHeight)) {
                                        val col = ((offset.x - startX) / colW).toInt().coerceIn(0, 3)
                                        toggleColumn(col + 1)
                                    }
                                }
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawYpMultiplyBackground(size)
                        drawYpMultiplyFrame(size)
                        val margin = 3f / 860f * size.width
                        val usableWidth = size.width - 2f * margin
                        val rowHeight = (size.height - 6f / 480f * size.height) / ROWS
                        val colW = usableWidth / 4f
                        val startX = margin
                        val startY = 3f / 480f * size.height

                        for (row in 0 until ROWS) {
                            val ry = startY + row * rowHeight
                            val linkMarkers: Set<Int>
                            val rinkMarkers: Set<Int>
                            val resultMarkers: Set<Int>

                            if (exercise.b == 1) {
                                linkMarkers = emptySet()
                                rinkMarkers = emptySet()
                                resultMarkers = greenMarkers[row]
                            } else if (phase == 2) {
                                linkMarkers = emptySet()
                                rinkMarkers = emptySet()
                                resultMarkers = greenMarkers[row]
                            } else if (phase == 0 && runningTotal == 0 && iteration == 0) {
                                linkMarkers = redMarkers[row]
                                rinkMarkers = emptySet()
                                resultMarkers = emptySet()
                            } else if (phase == 0 && (runningTotal > 0 || iteration > 0)) {
                                linkMarkers = redMarkers[row]
                                rinkMarkers = emptySet()
                                resultMarkers = emptySet()
                            } else if (phase == 1) {
                                linkMarkers = redMarkers[row]
                                rinkMarkers = blueColumns
                                resultMarkers = emptySet()
                            } else {
                                linkMarkers = emptySet()
                                rinkMarkers = emptySet()
                                resultMarkers = emptySet()
                            }

                            drawYpMultiplyRow(
                                cellOriginX = startX,
                                cellOriginY = ry,
                                cellWidth = colW,
                                cellHeight = rowHeight,
                                canvasSize = size,
                                leftMarkers = linkMarkers,
                                rightMarkers = rinkMarkers,
                                resultMarkers = resultMarkers,
                                persistentMarkers = emptySet()
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (!finalCongratsShown) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF2E241F),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            val stepDesc = when (phase) {
                                0 -> s.yupana.ypMultiplyStepPlace.format(iteration + 1, exercise.b, exercise.a)
                                1 -> s.yupana.ypMultiplyStepAdd.format(iteration + 1, exercise.b, exercise.a, runningTotal + exercise.a)
                                else -> "${exercise.a} \u00D7 ${iteration + 1} = ${exercise.a * (iteration + 1)}"
                            }
                            Text(
                                text = stepDesc,
                                color = Color(0xFFF2ECD8),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (currentMovesText.isNotEmpty() && rowCompleted && (phase == 2 || exercise.b == 1)) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = currentMovesText,
                                    color = Color(0xFFA8E6C1),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (!finalCongratsShown) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF2E241F),
                    ) {
                        Text(
                            text = "${exercise.a} \u00D7 ${iteration + 1} = ${exercise.a * (iteration + 1)}",
                            color = Color(0xFFF2ECD8),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

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
                                if (!finalCongratsShown && rowCompleted) {
                                    advancePhase()
                                }
                            },
                            enabled = rowCompleted && !finalCongratsShown,
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
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = feedbackMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isFeedbackPositive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
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
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=9a7a978b-3fac-422c-8e41-3ef1a24e88f3"))
                            Toast.makeText(ctx, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showMainTextSubmenu = false
                            uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=9a7a978b-3fac-422c-8e41-3ef1a24e88f3")
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

private fun DrawScope.drawYpMultiplyBackground(size: Size) {
    drawRect(color = Color(0xFFFEF5E0), size = size)
}

private fun DrawScope.drawYpMultiplyFrame(size: Size) {
    drawRect(
        color = Color(0xFFB48B5A),
        topLeft = Offset(3f / 860f * size.width, 3f / 480f * size.height),
        size = Size(size.width - 6f / 860f * size.width, size.height - 6f / 480f * size.height),
        style = Stroke(width = 2f / 480f * size.height)
    )
}

private fun DrawScope.drawYpMultiplyRow(
    cellOriginX: Float,
    cellOriginY: Float,
    cellWidth: Float,
    cellHeight: Float,
    canvasSize: Size,
    leftMarkers: Set<Int> = emptySet(),
    rightMarkers: Set<Int> = emptySet(),
    resultMarkers: Set<Int> = emptySet(),
    persistentMarkers: Set<Int> = emptySet(),
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

        val hasLeft = colNum in leftMarkers
        val hasRight = colNum in rightMarkers
        val hasResult = colNum in resultMarkers
        val hasPersistent = colNum in persistentMarkers

        val topEdge = cellOriginY + cellHeight * 0.08f
        val bottomEdge = cellOriginY + cellHeight * 0.92f
        val topMarkerY = topEdge + markerGap
        val bottomMarkerY = bottomEdge - markerGap

        if (hasLeft) {
            val my = topMarkerY - extraPx
            drawCircle(color = Color(0xFFC0392B), radius = markerRadius, center = Offset(cx, my))
            drawCircle(color = Color(0xFF000000).copy(alpha = 0.2f), radius = markerRadius, center = Offset(cx, my), style = Stroke(width = 0.8f / 480f * ch))
        }

        if (hasRight) {
            val my = bottomMarkerY + extraPx
            drawCircle(color = Color(0xFF2980B9), radius = markerRadius, center = Offset(cx, my))
            drawCircle(color = Color(0xFF000000).copy(alpha = 0.2f), radius = markerRadius, center = Offset(cx, my), style = Stroke(width = 0.8f / 480f * ch))
        }

        if (hasResult) {
            val my = topMarkerY - extraPx
            drawCircle(color = Color(0xFF27AE60), radius = markerRadius, center = Offset(cx, my))
            drawCircle(color = Color(0xFFA8E6C1).copy(alpha = 0.4f), radius = markerRadius * 0.7f, center = Offset(cx, my))
            drawCircle(color = Color(0xFF000000).copy(alpha = 0.2f), radius = markerRadius, center = Offset(cx, my), style = Stroke(width = 0.8f / 480f * ch))
        }

        if (hasPersistent && !hasResult && !hasLeft && !hasRight) {
            val my = topMarkerY - extraPx
            drawCircle(color = Color(0xFF27AE60), radius = markerRadius * 0.8f, center = Offset(cx, my))
            drawCircle(color = Color(0xFFA8E6C1).copy(alpha = 0.3f), radius = markerRadius * 0.5f, center = Offset(cx, my))
            drawCircle(color = Color(0xFF000000).copy(alpha = 0.15f), radius = markerRadius * 0.8f, center = Offset(cx, my), style = Stroke(width = 0.6f / 480f * ch))
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
            drawCircle(color = dotColor, radius = dotRadius * 0.8f, center = dotCenter)
            drawCircle(color = Color(0xFF000000).copy(alpha = 0.15f), radius = dotRadius * 0.8f, center = dotCenter, style = Stroke(width = 0.6f / 440f * ch))
        }
    }
}
