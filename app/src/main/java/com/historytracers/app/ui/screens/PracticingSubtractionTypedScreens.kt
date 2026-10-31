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
import com.historytracers.app.ui.features.practicingSubtractionTypedScreenStringsForLanguage
import kotlin.random.Random

private const val SUB_TYPED_SMARTPHONE_FILE = "d34954f9-220e-416f-bcfc-8f8ab3d06c72"
private const val SUB_TYPED_ORIGINAL_TEXT_URL =
    "https://www.historytracers.org/index.html?page=class_content&arg=cc21bf40-cb71-4f3c-a82a-8785f5d97611"
private const val SUB_TYPED_DIGITS = 3
private const val SUB_TYPED_SECTION_ID = "practicing_subtraction_typed"

private val TypedSubGreen = Color(0xFF4CAF50)
private val TypedSubGreenDark = Color(0xFF2E7D32)
private val TypedSubBlue = Color(0xFF2C5F8A)
private val TypedSubInputBackground = Color(0xFFEAF3FB)
private val TypedSubCellBorder = Color(0xFF333333)
private val TypedSubActiveCell = Color(0xFFADD8E6)
private val TypedSubBaseSquare = Color(0xFFADD8E6)
private val TypedSubRemovedSquare = Color(0xFFFFF3B0)
private val TypedSubPendingSquare = Color.White
private val TypedSubComplement = Color(0xFFE67E22)
private val TypedSubTenGroup = Color(0xFFC0392B)
private val TypedSubNoteColor = Color(0xFF8A4B08)

private data class TypedSubState(
    val top: Int,
    val bottom: Int,
    val topDigits: List<Int>,
    val bottomDigits: List<Int>,
    val column: Int = 0,
    val incoming: Int = 0,
    val borrows: Map<Int, Int> = emptyMap(),
    val results: Map<Int, Int> = emptyMap(),
    val regroup: Map<Int, Int> = emptyMap(),
    val reduceValues: Map<Int, Int> = emptyMap(),
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

private fun typedSubDigits(value: Int): List<Int> {
    var v = value
    return List(SUB_TYPED_DIGITS) {
        val digit = v % 10
        v /= 10
        digit
    }
}

// Prepares the column: the previous borrow is removed from the top digit, ten squares
// are borrowed when the digit is still too small, and the fields that the player must
// type are chosen (adjusted digit, value with ten, borrow and result digit).
private fun TypedSubState.setupColumn(): TypedSubState {
    val topDigit = topDigits[column]
    val bottom = bottomDigits[column]
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

private fun newTypedSubState(): TypedSubState {
    val first = Random.nextInt(100, 1000)
    val second = Random.nextInt(100, 1000)
    val top = maxOf(first, second)
    val bottom = minOf(first, second)
    return TypedSubState(
        top = top,
        bottom = bottom,
        topDigits = typedSubDigits(top),
        bottomDigits = typedSubDigits(bottom),
    ).setupColumn()
}

// The player accepted the adjusted digit written on top. When the column also
// borrows, the value with ten squares is typed next; otherwise the value is final.
private fun TypedSubState.acceptReduce(): TypedSubState {
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

private fun TypedSubState.resolve(): TypedSubState {
    if (answered) return this
    return copy(
        answered = true,
        borrows = borrows + (column to borrowOut),
        results = results + (column to (base - bottomDigit)),
        lastBorrow = borrowOut,
    )
}

private fun TypedSubState.next(): TypedSubState =
    if (column >= SUB_TYPED_DIGITS - 1) {
        newTypedSubState()
    } else {
        copy(column = column + 1, incoming = lastBorrow).setupColumn()
    }

private fun isTypedSubTopPartial(expected: Int?, raw: String): Boolean {
    if (expected == null) return false
    val text = expected.toString()
    return expected >= 10 && raw.length < text.length && text.startsWith(raw)
}

@Composable
private fun typedSubOrderName(column: Int): String {
    val xs = practicingSubtractionTypedScreenStringsForLanguage(LocalAppLanguage.current)
    return when (column) {
        0 -> xs.orderUnits
        1 -> xs.orderTens
        else -> xs.orderHundreds
    }
}

@Composable
private fun RowScope.TypedSubCell(text: String, active: Boolean, struck: Boolean = false) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(42.dp)
            .then(
                if (active) Modifier.background(TypedSubActiveCell, RoundedCornerShape(4.dp))
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
private fun RowScope.TypedSubOpCell(text: String) {
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
            color = TypedSubGreenDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TypedSubInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    maxLength: Int,
    width: Dp = 32.dp,
    enabled: Boolean = true,
) {
    BasicTextField(
        value = value,
        onValueChange = { raw -> onValueChange(raw.filter { it.isDigit() }.take(maxLength)) },
        enabled = enabled,
        singleLine = true,
        textStyle = MaterialTheme.typography.headlineSmall.copy(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = TypedSubBlue
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .width(width)
            .height(32.dp)
            .background(TypedSubInputBackground, RoundedCornerShape(4.dp))
            .border(2.dp, TypedSubBlue, RoundedCornerShape(4.dp))
            .padding(2.dp)
            .semantics { contentDescription = label }
    )
}

@Composable
private fun RowScope.TypedSubTopCell(
    state: TypedSubState,
    order: Int,
    topText: String,
    borrowText: String,
    onTopChange: (String) -> Unit,
    onBorrowChange: (String) -> Unit,
) {
    val xs = practicingSubtractionTypedScreenStringsForLanguage(LocalAppLanguage.current)
    val pending = !state.answered
    val active = order == state.column
    val showBorrow = pending && order == state.column + 1 && state.borrowNeeded && !state.borrowTyped
    val showTop = pending && order == state.column &&
        (state.topSlot == "reduce" || state.topSlot == "final")

    Box(
        modifier = Modifier
            .weight(1f)
            .height(42.dp)
            .then(
                if (active) Modifier.background(TypedSubActiveCell, RoundedCornerShape(4.dp))
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            showBorrow -> TypedSubInput(
                value = borrowText,
                onValueChange = onBorrowChange,
                label = xs.borrowLabel,
                maxLength = 1,
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
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                }
                TypedSubInput(
                    value = topText,
                    onValueChange = onTopChange,
                    label = xs.topValueLabel,
                    maxLength = 2,
                    width = 36.dp
                )
            }
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
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun TypedSubBoard(
    state: TypedSubState,
    topText: String,
    borrowText: String,
    digitText: String,
    onTopChange: (String) -> Unit,
    onBorrowChange: (String) -> Unit,
    onDigitChange: (String) -> Unit,
) {
    val xs = practicingSubtractionTypedScreenStringsForLanguage(LocalAppLanguage.current)
    val pending = !state.answered

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            TypedSubOpCell("")
            for (order in SUB_TYPED_DIGITS downTo 0) {
                TypedSubTopCell(
                    state = state,
                    order = order,
                    topText = topText,
                    borrowText = borrowText,
                    onTopChange = onTopChange,
                    onBorrowChange = onBorrowChange
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            TypedSubOpCell("")
            for (order in SUB_TYPED_DIGITS downTo 0) {
                val struck = state.regroup.containsKey(order) ||
                    (pending && order == state.column && state.topSlot != "none")
                TypedSubCell(
                    text = if (order < SUB_TYPED_DIGITS) state.topDigits[order].toString() else "",
                    active = order == state.column,
                    struck = struck
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            TypedSubOpCell("-")
            for (order in SUB_TYPED_DIGITS downTo 0) {
                TypedSubCell(
                    text = if (order < SUB_TYPED_DIGITS) state.bottomDigits[order].toString() else "",
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
                    .background(TypedSubCellBorder)
            )
        }
        Row(Modifier.fillMaxWidth()) {
            TypedSubOpCell("")
            for (order in SUB_TYPED_DIGITS downTo 0) {
                if (pending && order == state.column) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .background(TypedSubActiveCell, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        TypedSubInput(
                            value = digitText,
                            onValueChange = onDigitChange,
                            label = xs.digitLabel,
                            maxLength = 1,
                            enabled = state.stage == "solve"
                        )
                    }
                } else {
                    TypedSubCell(
                        text = state.results[order]?.toString() ?: "",
                        active = order == state.column
                    )
                }
            }
        }
    }
}

@Composable
private fun TypedSubSquare(
    index: Int,
    state: TypedSubState,
    inTen: Boolean,
    alwaysBase: Boolean = false,
) {
    val revealed = state.answered
    val isRemoved = !alwaysBase && index < state.bottomDigit
    val fill = when {
        isRemoved && revealed -> TypedSubRemovedSquare
        isRemoved -> TypedSubPendingSquare
        else -> TypedSubBaseSquare
    }
    val isComplement = inTen && index >= state.bottomDigit

    Box(
        modifier = Modifier
            .size(24.dp)
            .background(fill, RoundedCornerShape(4.dp))
            .border(2.dp, TypedSubCellBorder, RoundedCornerShape(4.dp))
    ) {
        if (isComplement) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .border(2.dp, TypedSubComplement, RoundedCornerShape(2.dp))
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TypedSubColumnSquares(state: TypedSubState) {
    val xs = practicingSubtractionTypedScreenStringsForLanguage(LocalAppLanguage.current)
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
                                color = TypedSubTenGroup,
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
                        TypedSubSquare(index = index, state = state, inTen = true)
                    }
                }
                for (index in 0 until state.base - 10) {
                    TypedSubSquare(index = index, state = state, inTen = false, alwaysBase = true)
                }
            } else {
                for (index in 0 until state.base) {
                    TypedSubSquare(index = index, state = state, inTen = false)
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
            color = TypedSubNoteColor,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun PracticingSubtractionTypedScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val s = LocalUiStrings.current
    val xs = practicingSubtractionTypedScreenStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }
    val orderNames = listOf(xs.orderUnits, xs.orderTens, xs.orderHundreds)

    var state by remember { mutableStateOf(newTypedSubState()) }
    var topText by remember { mutableStateOf("") }
    var borrowText by remember { mutableStateOf("") }
    var digitText by remember { mutableStateOf("") }
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
        when (val result = repo.loadAndParse("$language/$SUB_TYPED_SMARTPHONE_FILE")) {
            is ContentResult.SMGame ->
                instructionText = result.data?.content?.firstOrNull()?.text?.firstOrNull()?.text
            else -> instructionText = null
        }
    }

    LaunchedEffect(state.answered, state.column) {
        if (state.answered && state.column >= SUB_TYPED_DIGITS - 1 && !completionHandled) {
            completionHandled = true
            preferences.recordLessonCompletion()
            preferences.markReturningSectionCompleted(SUB_TYPED_SECTION_ID)
        }
    }

    fun evaluate(newTop: String, newBorrow: String, newDigit: String) {
        if (state.answered) return
        val columnName = orderNames[state.column]

        // Step 1: the adjusted digit written on the top row, when the previous
        // column borrowed 1.
        if (state.stage == "reduce") {
            if (newTop.isEmpty()) {
                errorFeedback = ""
                return
            }
            if (state.topExpected != null && newTop.toIntOrNull() == state.topExpected) {
                state = state.acceptReduce()
                topText = ""
                errorFeedback = ""
            } else if (!isTypedSubTopPartial(state.topExpected, newTop)) {
                errorFeedback = xs.wrongTop.format(state.topDigits[state.column], state.afterIncoming)
            } else {
                errorFeedback = ""
            }
            return
        }

        var next = state
        var topWritten = next.topSlot != "final"

        // Step 2: when the column borrows, the value with ten squares is typed on
        // the top row after the adjusted digit is crossed out.
        if (next.topSlot == "final") {
            val expected = next.topExpected
            if (newTop.isNotEmpty() && expected != null && newTop.toIntOrNull() == expected) {
                next = next.copy(regroup = next.regroup + (next.column to expected), topSlot = "none")
                topText = ""
                errorFeedback = ""
                topWritten = true
            } else if (newTop.isNotEmpty() && !isTypedSubTopPartial(expected, newTop)) {
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

        // The borrow typed above the next order. The player may also write there
        // the value that stays in the next order after lending the 1.
        val nextColumn = next.column + 1
        val nextReduced = if (nextColumn < SUB_TYPED_DIGITS) next.topDigits[nextColumn] - 1 else -1
        val nextTop = if (nextReduced < 0) 9 else nextReduced
        val borrowIsNextTop = next.borrowNeeded && nextColumn < SUB_TYPED_DIGITS &&
            newBorrow.isNotEmpty() && nextTop != 1 && newBorrow.toIntOrNull() == nextTop
        val borrowOk = !next.borrowNeeded ||
            (newBorrow.isNotEmpty() && (newBorrow.toIntOrNull() == 1 || borrowIsNextTop))
        if (borrowOk && next.borrowNeeded) {
            next = next.copy(borrows = next.borrows + (next.column to 1), borrowTyped = true)
            if (borrowIsNextTop) {
                next = next.copy(regroup = next.regroup + (nextColumn to nextTop))
            }
        }
        if (next.borrowNeeded && newBorrow.isNotEmpty() && !borrowOk) {
            val nextName = orderNames.getOrElse(nextColumn) { orderNames.last() }
            errorFeedback = xs.wrongBorrow.format(next.afterIncoming, next.bottomDigit, nextName)
            return
        }

        // Step 3: the digit that the column keeps, typed on the result row.
        val result = next.base - next.bottomDigit
        val digitOk = newDigit.isNotEmpty() && newDigit.toIntOrNull() == result
        if (newDigit.isNotEmpty() && !digitOk) {
            errorFeedback = xs.wrongDigit.format(next.bottomDigit, 10 - next.bottomDigit, columnName)
            return
        }

        state = if (topWritten && borrowOk && digitOk) next.resolve() else next
        errorFeedback = ""
    }

    fun nextColumn() {
        state = state.next()
        topText = ""
        borrowText = ""
        digitText = ""
        errorFeedback = ""
    }

    val columnName = orderNames[state.column]
    val question = xs.questionTemplate.format(columnName, state.base, state.bottomDigit)
    val note = when {
        state.stage == "reduce" ->
            xs.incomingNote.format(state.topDigits[state.column], state.afterIncoming)
        state.chained -> xs.chainedNote
        state.borrowed -> xs.borrowNote.format(state.afterIncoming, state.bottomDigit, state.base)
        state.incoming > 0 ->
            xs.incomingNote.format(state.topDigits[state.column], state.afterIncoming)
        else -> ""
    }
    val lastColumnDone = state.answered && state.column >= SUB_TYPED_DIGITS - 1
    val explanation = if (state.answered) {
        val template = if (state.borrowOut > 0) xs.msgBorrow else xs.msgNoBorrow
        template.format(state.base, state.bottomDigit, state.base - state.bottomDigit, columnName)
    } else {
        ""
    }
    val shownFeedback = when {
        errorFeedback.isNotEmpty() -> errorFeedback
        state.answered && !lastColumnDone -> xs.congrats + "\n" + explanation
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

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (order in 0 until SUB_TYPED_DIGITS) {
                        val active = order == state.column
                        Box(
                            modifier = Modifier
                                .background(
                                    if (active) TypedSubBlue else Color(0xFFF4F4F4),
                                    RoundedCornerShape(6.dp)
                                )
                                .border(
                                    1.dp,
                                    if (active) TypedSubBlue else Color(0xFFBBBBBB),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = typedSubOrderName(order),
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
                    TypedSubBoard(
                        state = state,
                        topText = topText,
                        borrowText = borrowText,
                        digitText = digitText,
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
                        }
                    )
                }

                Spacer(Modifier.height(12.dp))

                TypedSubColumnSquares(state = state)

                if (shownFeedback.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = shownFeedback,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (errorFeedback.isNotEmpty()) MaterialTheme.colorScheme.error else TypedSubGreenDark,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                if (state.answered) {
                    Spacer(Modifier.height(12.dp))
                    FilledTonalButton(
                        onClick = { nextColumn() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = TypedSubGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (lastColumnDone) xs.newProblem else xs.nextColumn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(72.dp))
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
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", SUB_TYPED_ORIGINAL_TEXT_URL))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        safeOpenUri(uriHandler, SUB_TYPED_ORIGINAL_TEXT_URL)
                    }
                )
            }
        }
    }
}
