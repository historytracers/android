// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.practicingSubtractionChosenScreenStringsForLanguage

private const val CHOSEN_SUB_SMARTPHONE_FILE = "59a489d3-707b-48dc-9f84-49a6224b112e"
private const val CHOSEN_SUB_ORIGINAL_TEXT_URL =
    "https://www.historytracers.org/index.html?page=class_content&arg=91cc765a-206a-4215-bc55-3b3adcc40727"
private const val CHOSEN_SUB_MIN = 0
private const val CHOSEN_SUB_MAX = 99999
private const val CHOSEN_SUB_SECTION_ID = "practicing_subtraction_chosen"

private val ChosenSubGreen = Color(0xFF4CAF50)
private val ChosenSubGreenDark = Color(0xFF2E7D32)
private val ChosenSubBlue = Color(0xFF2C5F8A)
private val ChosenSubInputBackground = Color(0xFFEAF3FB)
private val ChosenSubCellBorder = Color(0xFF333333)
private val ChosenSubActiveCell = Color(0xFFADD8E6)
private val ChosenSubBaseSquare = Color(0xFFADD8E6)
private val ChosenSubRemovedSquare = Color(0xFFFFF3B0)
private val ChosenSubPendingSquare = Color.White
private val ChosenSubComplement = Color(0xFFE67E22)
private val ChosenSubTenGroup = Color(0xFFC0392B)
private val ChosenSubNoteColor = Color(0xFF8A4B08)

private data class ChosenSubState(
    val top: Int,
    val bottom: Int,
    val columns: Int,
    val topDigits: List<Int>,
    val bottomDigits: List<Int>,
    val column: Int = 0,
    val incoming: Int = 0,
    val borrows: Map<Int, Int> = emptyMap(),
    val results: Map<Int, Int> = emptyMap(),
    val regroup: Map<Int, Int> = emptyMap(),
    val reduceValues: Map<Int, Int> = emptyMap(),
    val settled: Set<Int> = emptySet(),
    val pendingRewrites: Map<Int, Int> = emptyMap(),
    val base: Int = 0,
    val bottomDigit: Int = 0,
    val afterIncoming: Int = 0,
    val borrowOut: Int = 0,
    val borrowed: Boolean = false,
    val chained: Boolean = false,
    val borrowNeeded: Boolean = false,
    val borrowTyped: Boolean = false,
    val stage: String = "solve",
    val topSlot: String = "none",
    val topExpected: Int? = null,
    val answered: Boolean = false,
    val lastBorrow: Int = 0,
)

private fun chosenSubDigits(value: Int, length: Int): List<Int> {
    var v = value
    return List(length) {
        val digit = v % 10
        v /= 10
        digit
    }
}

private fun newChosenSubState(first: Int, second: Int): ChosenSubState {
    val columns = maxOf(first.toString().length, second.toString().length)
    return ChosenSubState(
        top = first,
        bottom = second,
        columns = columns,
        topDigits = chosenSubDigits(first, columns),
        bottomDigits = chosenSubDigits(second, columns),
    ).setupColumn()
}

private fun ChosenSubState.setupColumn(): ChosenSubState {
    val topDigit = topDigits[column]
    val bottom = bottomDigits[column]

    // A settled column already has its rewritten value on top: the borrow that
    // produced it has been applied, including the whole chain of zeros. Only a
    // new borrow from the next order (when the rewritten value is still smaller
    // than the bottom digit) has to be handled.
    if (column in settled) {
        val finalTop = regroup[column] ?: topDigit
        var available = finalTop
        var borrowOutValue = 0
        var borrowedValue = false
        if (available < bottom) {
            available += 10
            borrowOutValue = 1
            borrowedValue = true
        }
        val needTop = borrowOutValue == 1
        return copy(
            afterIncoming = finalTop,
            base = available,
            borrowOut = borrowOutValue,
            borrowed = borrowedValue,
            chained = false,
            bottomDigit = bottom,
            borrowNeeded = needTop,
            borrowTyped = false,
            answered = false,
            reduceValues = reduceValues + (column to finalTop),
            stage = "solve",
            topSlot = if (needTop) "final" else "none",
            topExpected = if (needTop) available else null,
        )
    }

    val afterIncomingValue = topDigit - incoming
    var available = afterIncomingValue
    var borrowOutValue = 0
    var borrowedValue = false
    var chainedValue = false
    val prewritten = regroup.containsKey(column)

    if (available < 0) {
        available += 10
        borrowOutValue = 1
        chainedValue = true
    }
    if (available < bottom) {
        available += 10
        borrowOutValue = 1
        borrowedValue = true
    }

    val borrowNeededValue = borrowOutValue == 1
    val reduceEntry = !prewritten && incoming > 0 && afterIncomingValue >= 0
    val needTop = borrowNeededValue && (!prewritten || regroup[column] != available)

    return copy(
        afterIncoming = afterIncomingValue,
        base = available,
        borrowOut = borrowOutValue,
        borrowed = borrowedValue,
        chained = chainedValue,
        bottomDigit = bottom,
        borrowNeeded = borrowNeededValue,
        borrowTyped = false,
        answered = false,
        reduceValues = if (prewritten) reduceValues + (column to (regroup[column] ?: 0)) else reduceValues,
        stage = if (reduceEntry) "reduce" else "solve",
        topSlot = when {
            reduceEntry -> "reduce"
            needTop -> "final"
            else -> "none"
        },
        topExpected = when {
            reduceEntry -> afterIncomingValue
            needTop -> available
            else -> null
        },
    )
}

// Starts rewriting the whole chain that follows a zero column: every following
// zero must be written as 9, and the first nonzero digit loses 1. The values are
// not filled in automatically: empty fields are shown on the top row for the
// player to complete before the subtraction continues.
private fun ChosenSubState.startChainRewrite(startColumn: Int): ChosenSubState {
    if (startColumn in settled) return this
    var result = copy(settled = settled + startColumn)
    var next = startColumn + 1
    while (next < columns) {
        if (next in result.settled) break
        val original = result.topDigits[next]
        if (result.pendingRewrites.containsKey(next)) break
        val expected = if (original == 0) 9 else original - 1
        result = result.copy(pendingRewrites = result.pendingRewrites + (next to expected))
        if (original != 0) break
        next++
    }
    return result
}

private fun ChosenSubState.acceptReduce(): ChosenSubState {
    val reduceValue = topExpected ?: return this
    val withReduce = copy(reduceValues = reduceValues + (column to reduceValue))
    return if (borrowNeeded) {
        withReduce.copy(stage = "solve", topSlot = "final", topExpected = base)
    } else {
        withReduce.copy(
            regroup = regroup + (column to reduceValue),
            stage = "solve",
            topSlot = "none",
            topExpected = null,
        )
    }
}

private fun ChosenSubState.resolve(): ChosenSubState {
    if (answered) return this
    return copy(
        answered = true,
        borrows = borrows + (column to borrowOut),
        results = results + (column to (base - bottomDigit)),
        lastBorrow = borrowOut,
    )
}

private fun ChosenSubState.nextColumn(): ChosenSubState =
    copy(column = column + 1, incoming = lastBorrow).setupColumn()

private fun isChosenSubTopPartial(expected: Int?, raw: String): Boolean {
    if (expected == null) return false
    val text = expected.toString()
    return expected >= 10 && raw.length < text.length && text.startsWith(raw)
}

@Composable
private fun RowScope.ChosenSubCell(text: String, active: Boolean, struck: Boolean = false) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(42.dp)
            .then(
                if (active) Modifier.background(ChosenSubActiveCell, RoundedCornerShape(4.dp))
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            textDecoration = if (struck) TextDecoration.LineThrough else TextDecoration.None,
            color = if (struck) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun RowScope.ChosenSubOpCell(text: String) {
    Box(
        modifier = Modifier
            .width(24.dp)
            .height(42.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = ChosenSubGreenDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ChosenSubInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    maxLength: Int,
    width: Dp,
    compact: Boolean,
    enabled: Boolean = true,
) {
    BasicTextField(
        value = value,
        onValueChange = { raw -> onValueChange(raw.filter { it.isDigit() }.take(maxLength)) },
        enabled = enabled,
        singleLine = true,
        textStyle = (if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall).copy(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = ChosenSubBlue
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .width(width)
            .height(32.dp)
            .background(ChosenSubInputBackground, RoundedCornerShape(4.dp))
            .border(2.dp, ChosenSubBlue, RoundedCornerShape(4.dp))
            .padding(2.dp)
            .semantics { contentDescription = label }
    )
}

@Composable
private fun RowScope.ChosenSubTopCell(
    state: ChosenSubState,
    order: Int,
    topText: String,
    borrowText: String,
    rewriteText: String,
    onTopChange: (String) -> Unit,
    onBorrowChange: (String) -> Unit,
    onRewriteChange: (String) -> Unit,
    compact: Boolean,
) {
    val xs = practicingSubtractionChosenScreenStringsForLanguage(LocalAppLanguage.current)
    val pending = !state.answered
    val active = order == state.column
    val showBorrow = pending && order == state.column + 1 && state.borrowNeeded && !state.borrowTyped
    val showTop = pending && order == state.column &&
        (state.topSlot == "reduce" || state.topSlot == "final")
    val showRewrite = pending && order in state.pendingRewrites && !showTop && !showBorrow

    Box(
        modifier = Modifier
            .weight(1f)
            .height(42.dp)
            .then(
                when {
                    active -> Modifier.background(ChosenSubActiveCell, RoundedCornerShape(4.dp))
                    showRewrite -> Modifier.background(Color(0xFFFFF8E1), RoundedCornerShape(4.dp))
                    else -> Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            showBorrow -> ChosenSubInput(
                value = borrowText,
                onValueChange = onBorrowChange,
                label = xs.borrowLabel,
                maxLength = 1,
                width = if (compact) 24.dp else 30.dp,
                compact = compact,
                enabled = state.stage == "solve"
            )
            showTop -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (state.topSlot == "final") {
                    val reduced = state.reduceValues[order] ?: state.regroup[order]
                    if (reduced != null) {
                        Text(
                            text = reduced.toString(),
                            style = if (compact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.width(if (compact) 2.dp else 4.dp))
                    }
                }
                ChosenSubInput(
                    value = topText,
                    onValueChange = onTopChange,
                    label = xs.topValueLabel,
                    maxLength = 2,
                    width = if (compact) 26.dp else 36.dp,
                    compact = compact
                )
            }
            showRewrite -> ChosenSubInput(
                value = rewriteText,
                onValueChange = onRewriteChange,
                label = xs.topValueLabel,
                maxLength = 1,
                width = if (compact) 26.dp else 30.dp,
                compact = compact
            )
            else -> {
                val regroup = state.regroup[order]
                val reduced = state.reduceValues[order]
                if (regroup != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (reduced != null && reduced != regroup) {
                            Text(
                                text = reduced.toString(),
                                style = if (compact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = regroup.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (state.borrows[order - 1] == 1) {
                    Text(
                        text = "1",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ChosenSubBoard(
    state: ChosenSubState,
    topText: String,
    borrowText: String,
    digitText: String,
    rewriteTexts: Map<Int, String>,
    onTopChange: (String) -> Unit,
    onBorrowChange: (String) -> Unit,
    onDigitChange: (String) -> Unit,
    onRewriteChange: (Int, String) -> Unit,
) {
    val xs = practicingSubtractionChosenScreenStringsForLanguage(LocalAppLanguage.current)
    val pending = !state.answered
    val compact = state.columns >= 5

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            ChosenSubOpCell("")
            for (order in state.columns downTo 0) {
                ChosenSubTopCell(
                    state = state,
                    order = order,
                    topText = topText,
                    borrowText = borrowText,
                    rewriteText = rewriteTexts[order] ?: "",
                    onTopChange = onTopChange,
                    onBorrowChange = onBorrowChange,
                    onRewriteChange = { raw -> onRewriteChange(order, raw) },
                    compact = compact
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            ChosenSubOpCell("")
            for (order in state.columns downTo 0) {
                val struck = state.regroup.containsKey(order) ||
                    (pending && order == state.column && state.topSlot != "none")
                ChosenSubCell(
                    text = if (order < state.columns) state.topDigits[order].toString() else "",
                    active = order == state.column,
                    struck = struck
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            ChosenSubOpCell("-")
            for (order in state.columns downTo 0) {
                ChosenSubCell(
                    text = if (order < state.columns) state.bottomDigits[order].toString() else "",
                    active = order == state.column
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.width(24.dp).height(2.dp))
            Box(
                Modifier
                    .weight(1f)
                    .height(2.dp)
                    .background(ChosenSubCellBorder)
            )
        }
        Row(Modifier.fillMaxWidth()) {
            ChosenSubOpCell("")
            for (order in state.columns downTo 0) {
                if (pending && order == state.column) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .background(ChosenSubActiveCell, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        ChosenSubInput(
                            value = digitText,
                            onValueChange = onDigitChange,
                            label = xs.digitLabel,
                            maxLength = 1,
                            width = if (compact) 24.dp else 30.dp,
                            compact = compact,
                            enabled = state.stage == "solve" && state.pendingRewrites.isEmpty()
                        )
                    }
                } else {
                    ChosenSubCell(
                        text = state.results[order]?.toString() ?: "",
                        active = order == state.column
                    )
                }
            }
        }
    }
}

@Composable
private fun ChosenSubSquare(
    index: Int,
    state: ChosenSubState,
    inTen: Boolean,
    alwaysBase: Boolean = false,
) {
    val revealed = state.answered
    val isRemoved = !alwaysBase && index < state.bottomDigit
    val fill = when {
        isRemoved && revealed -> ChosenSubRemovedSquare
        isRemoved -> ChosenSubPendingSquare
        else -> ChosenSubBaseSquare
    }
    val isComplement = inTen && index >= state.bottomDigit

    Box(
        modifier = Modifier
            .size(24.dp)
            .background(fill, RoundedCornerShape(4.dp))
            .border(2.dp, ChosenSubCellBorder, RoundedCornerShape(4.dp))
    ) {
        if (isComplement) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .border(2.dp, ChosenSubComplement, RoundedCornerShape(2.dp))
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChosenSubColumnSquares(state: ChosenSubState) {
    val xs = practicingSubtractionChosenScreenStringsForLanguage(LocalAppLanguage.current)
    val revealed = state.answered
    val note = when {
        revealed && state.borrowed -> xs.squaresBorrowUsedNote.format(
            state.bottomDigit,
            10 - state.bottomDigit,
            state.base - 10,
            state.base - state.bottomDigit
        )
        revealed -> xs.squaresUsedNote.format(state.bottomDigit, state.base - state.bottomDigit)
        state.borrowed -> xs.squaresBorrowPendingNote.format(
            state.bottomDigit,
            10 - state.bottomDigit,
            state.base - 10
        )
        else -> xs.squaresPendingNote.format(state.bottomDigit)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = xs.squaresLabel,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(6.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (state.borrowed) {
                Row(
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                color = ChosenSubTenGroup,
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
                                ),
                                cornerRadius = CornerRadius(6.dp.toPx())
                            )
                        }
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    for (index in 0 until 10) {
                        ChosenSubSquare(index = index, state = state, inTen = true)
                    }
                }
                for (index in 0 until state.base - 10) {
                    ChosenSubSquare(index = index, state = state, inTen = false, alwaysBase = true)
                }
            } else {
                for (index in 0 until state.base) {
                    ChosenSubSquare(index = index, state = state, inTen = false)
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = "${state.base} - ${state.bottomDigit} = " +
                if (revealed) (state.base - state.bottomDigit).toString() else "?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = note,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = ChosenSubNoteColor,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticingSubtractionChosenScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val s = LocalUiStrings.current
    val xs = practicingSubtractionChosenScreenStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }
    val orderNames = listOf(
        xs.orderUnits,
        xs.orderTens,
        xs.orderHundreds,
        xs.orderThousands,
        xs.orderTenThousands
    )

    var state by remember { mutableStateOf<ChosenSubState?>(null) }
    var firstText by remember { mutableStateOf("") }
    var secondText by remember { mutableStateOf("") }
    var setupError by remember { mutableStateOf("") }
    var topText by remember { mutableStateOf("") }
    var borrowText by remember { mutableStateOf("") }
    var digitText by remember { mutableStateOf("") }
    var rewriteTexts by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var errorFeedback by remember { mutableStateOf("") }
    var instructionText by remember { mutableStateOf<String?>(null) }
    var arrivalAwarded by remember { mutableStateOf(false) }
    var completionHandled by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    val initialScore = remember { currentScore }

    LaunchedEffect(Unit) {
        if (!arrivalAwarded) {
            arrivalAwarded = true
            onScoreChanged(initialScore + 2)
        }
    }

    LaunchedEffect(language) {
        when (val result = repo.loadAndParse("$language/$CHOSEN_SUB_SMARTPHONE_FILE")) {
            is ContentResult.SMGame ->
                instructionText = result.data?.content?.firstOrNull()?.text?.firstOrNull()?.text
            else -> instructionText = null
        }
    }

    LaunchedEffect(state?.answered, state?.column, state?.columns) {
        val current = state ?: return@LaunchedEffect
        if (current.answered && current.column >= current.columns - 1 && !completionHandled) {
            completionHandled = true
            preferences.recordLessonCompletion()
            preferences.markReturningSectionCompleted(CHOSEN_SUB_SECTION_ID)
        }
    }

    fun startGame() {
        val first = firstText.toIntOrNull()
        val second = secondText.toIntOrNull()
        if (first == null || second == null ||
            first < CHOSEN_SUB_MIN || first > CHOSEN_SUB_MAX ||
            second < CHOSEN_SUB_MIN || second > CHOSEN_SUB_MAX
        ) {
            setupError = xs.errorRange.format(CHOSEN_SUB_MIN, CHOSEN_SUB_MAX)
            return
        }
        if (second > first) {
            setupError = xs.errorOrder
            return
        }
        setupError = ""
        topText = ""
        borrowText = ""
        digitText = ""
        rewriteTexts = emptyMap()
        errorFeedback = ""
        state = newChosenSubState(first, second)
    }

    fun newNumbers() {
        state = null
        topText = ""
        borrowText = ""
        digitText = ""
        rewriteTexts = emptyMap()
        errorFeedback = ""
        setupError = ""
    }

    fun evaluate(newTop: String, newBorrow: String, newDigit: String) {
        val current = state ?: return
        if (current.answered) return
        val columnName = orderNames.getOrElse(current.column) { orderNames.last() }

        if (current.stage == "reduce") {
            if (newTop.isEmpty()) {
                errorFeedback = ""
                return
            }
            if (current.topExpected != null && newTop.toIntOrNull() == current.topExpected) {
                state = current.acceptReduce()
                topText = ""
                errorFeedback = ""
            } else if (!isChosenSubTopPartial(current.topExpected, newTop)) {
                errorFeedback = xs.wrongTop.format(current.topDigits[current.column], current.afterIncoming)
            } else {
                errorFeedback = ""
            }
            return
        }

        var next = current
        var topWritten = next.topSlot != "final"

        if (next.topSlot == "final") {
            val expected = next.topExpected
            if (newTop.isNotEmpty() && expected != null && newTop.toIntOrNull() == expected) {
                next = next.copy(regroup = next.regroup + (next.column to expected), topSlot = "none")
                topText = ""
                errorFeedback = ""
                topWritten = true
                if (next.chained && next.topDigits[next.column] == 0) {
                    next = next.copy(
                        borrows = next.borrows + (next.column to 1),
                        borrowTyped = true,
                    )
                    next = next.startChainRewrite(next.column)
                }
            } else if (newTop.isNotEmpty() && !isChosenSubTopPartial(expected, newTop)) {
                errorFeedback = if (next.chained) {
                    xs.wrongTopChained
                } else {
                    xs.wrongTopBorrow.format(next.afterIncoming, next.base)
                }
                return
            } else {
                errorFeedback = ""
                return
            }
        }

        val nextColumn = next.column + 1
        val nextReduced = if (nextColumn < next.columns) next.topDigits[nextColumn] - 1 else -1
        val nextTop = if (nextReduced < 0) 9 else nextReduced
        val borrowIsNextTop = next.borrowNeeded && nextColumn < next.columns &&
            newBorrow.isNotEmpty() && nextTop != 1 && newBorrow.toIntOrNull() == nextTop
        val borrowOk = !next.borrowNeeded || next.borrowTyped ||
            (newBorrow.isNotEmpty() && (newBorrow.toIntOrNull() == 1 || borrowIsNextTop))
        if (borrowOk && next.borrowNeeded) {
            next = next.copy(borrows = next.borrows + (next.column to 1), borrowTyped = true)
            if (borrowIsNextTop) {
                next = next.copy(regroup = next.regroup + (nextColumn to nextTop))
                if (next.topDigits[nextColumn] == 0) {
                    next = next.startChainRewrite(nextColumn)
                }
            }
        }
        if (next.borrowNeeded && newBorrow.isNotEmpty() && !borrowOk) {
            val nextName = orderNames.getOrElse(nextColumn) { orderNames.last() }
            errorFeedback = xs.wrongBorrow.format(next.afterIncoming, next.bottomDigit, nextName)
            return
        }

        val result = next.base - next.bottomDigit
        val digitOk = newDigit.isNotEmpty() && newDigit.toIntOrNull() == result
        if (newDigit.isNotEmpty() && !digitOk) {
            errorFeedback = xs.wrongDigit.format(next.bottomDigit, 10 - next.bottomDigit, columnName)
            return
        }

        state = if (topWritten && borrowOk && digitOk && next.pendingRewrites.isEmpty()) {
            next.resolve()
        } else {
            next
        }
        errorFeedback = ""
    }

    fun advanceColumn() {
        val current = state ?: return
        if (current.column >= current.columns - 1) {
            newNumbers()
            return
        }
        state = current.nextColumn()
        topText = ""
        borrowText = ""
        digitText = ""
        rewriteTexts = emptyMap()
        errorFeedback = ""
    }

    fun onRewriteChange(order: Int, raw: String) {
        val current = state ?: return
        val expected = current.pendingRewrites[order] ?: return
        val text = raw.filter { it.isDigit() }.take(1)
        rewriteTexts = rewriteTexts + (order to text)
        if (text.toIntOrNull() == expected) {
            val filled = current.copy(
                regroup = current.regroup + (order to expected),
                settled = current.settled + order,
                pendingRewrites = current.pendingRewrites - order,
            )
            state = filled
            rewriteTexts = rewriteTexts - order
            errorFeedback = ""
            if (filled.pendingRewrites.isEmpty()) {
                evaluate(topText, borrowText, digitText)
            }
        } else if (text.isNotEmpty()) {
            errorFeedback = xs.wrongRewrite
        } else {
            errorFeedback = ""
        }
    }

    val game = state
    val columnName = game?.let { orderNames.getOrElse(it.column) { orderNames.last() } } ?: ""
    val question = game?.let {
        if (it.column in it.settled && (it.borrowTyped || !it.borrowNeeded)) {
            xs.settledQuestionTemplate.format(columnName, it.base, it.bottomDigit)
        } else {
            xs.questionTemplate.format(columnName, it.base, it.bottomDigit)
        }
    } ?: ""
    val note = game?.let {
        when {
            it.pendingRewrites.isNotEmpty() -> xs.rewriteNote
            it.column in it.settled ->
                if (it.borrowed) {
                    xs.borrowNote.format(it.afterIncoming, it.bottomDigit, it.base)
                } else {
                    ""
                }
            it.stage == "reduce" -> xs.incomingNote.format(it.topDigits[it.column], it.afterIncoming)
            it.chained -> xs.chainedNote
            it.borrowed -> xs.borrowNote.format(it.afterIncoming, it.bottomDigit, it.base)
            it.incoming > 0 -> xs.incomingNote.format(it.topDigits[it.column], it.afterIncoming)
            else -> ""
        }
    } ?: ""
    val lastColumnDone = game != null && game.answered && game.column >= game.columns - 1
    val explanation = if (game != null && game.answered) {
        val template = if (game.borrowOut > 0) xs.msgBorrow else xs.msgNoBorrow
        template.format(game.base, game.bottomDigit, game.base - game.bottomDigit, columnName)
    } else {
        ""
    }
    val shownFeedback = when {
        errorFeedback.isNotEmpty() -> errorFeedback
        game != null && game.answered && !lastColumnDone -> xs.congrats + "\n" + explanation
        else -> ""
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
                        text = xs.title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            if (game == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(8.dp))

                    val instruction = instructionText
                    if (instruction != null) {
                        MarkdownText(text = instruction)
                    } else {
                        Text(
                            text = xs.fallbackInstruction,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    OutlinedTextField(
                        value = firstText,
                        onValueChange = { raw ->
                            firstText = raw.filter { it.isDigit() }.take(5)
                            setupError = ""
                        },
                        label = { Text(xs.firstLabel) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = secondText,
                        onValueChange = { raw ->
                            secondText = raw.filter { it.isDigit() }.take(5)
                            setupError = ""
                        },
                        label = { Text(xs.secondLabel) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (setupError.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = setupError,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    FilledTonalButton(
                        onClick = { startGame() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ChosenSubGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = xs.start,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(72.dp))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (order in 0 until game.columns) {
                            val active = order == game.column
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (active) ChosenSubBlue else Color(0xFFF4F4F4),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (active) ChosenSubBlue else Color(0xFFBBBBBB),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = orderNames.getOrElse(order) { orderNames.last() },
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                    color = if (active) Color.White else Color(0xFF555555)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = question,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    if (note.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ChosenSubBoard(
                            state = game,
                            topText = topText,
                            borrowText = borrowText,
                            digitText = digitText,
                            rewriteTexts = rewriteTexts,
                            onTopChange = {
                                topText = it
                                evaluate(it, borrowText, digitText)
                            },
                            onBorrowChange = {
                                borrowText = it
                                evaluate(topText, it, digitText)
                            },
                            onDigitChange = {
                                digitText = it
                                evaluate(topText, borrowText, it)
                            },
                            onRewriteChange = { order, raw -> onRewriteChange(order, raw) }
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    ChosenSubColumnSquares(state = game)

                    if (shownFeedback.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = shownFeedback,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (errorFeedback.isNotEmpty()) {
                                MaterialTheme.colorScheme.error
                            } else {
                                ChosenSubGreenDark
                            },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    if (game.answered) {
                        Spacer(Modifier.height(12.dp))
                        FilledTonalButton(
                            onClick = { advanceColumn() },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ChosenSubGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (lastColumnDone) xs.newNumbers else xs.nextColumn,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(72.dp))
                }
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
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", CHOSEN_SUB_ORIGINAL_TEXT_URL))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        safeOpenUri(uriHandler, CHOSEN_SUB_ORIGINAL_TEXT_URL)
                    }
                )
            }
        }
    }
}
