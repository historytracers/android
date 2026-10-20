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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.practicingAdditionTypedScreenStringsForLanguage
import kotlin.random.Random

private const val TYPED_ORIGINAL_TEXT_URL =
    "https://www.historytracers.org/index.html?page=class_content&arg=18ca9e1f-b936-447a-9bab-798159cefbb9"
private const val TYPED_ADD_DIGITS = 3

private val TypedGreen = Color(0xFF4CAF50)
private val TypedGreenDark = Color(0xFF2E7D32)
private val TypedBlue = Color(0xFF2C5F8A)
private val TypedInputBackground = Color(0xFFEAF3FB)
private val TypedCellBorder = Color(0xFF333333)
private val TypedActiveCell = Color(0xFFADD8E6)
private val TypedBaseSquare = Color(0xFFADD8E6)
private val TypedAddedSquare = Color(0xFFFFFFE0)
private val TypedPendingSquare = Color.White
private val TypedWrittenSquare = Color(0xFFFFFFE0)
private val TypedWrittenBaseSquare = Color(0xFF00008B)
private val TypedComplement = Color(0xFFE67E22)
private val TypedComplementUsed = Color(0xFFD35400)
private val TypedTenGroup = Color(0xFFC0392B)

private data class TypedAdditionState(
    val first: Int,
    val addend: Int,
    val firstDigits: List<Int>,
    val addendDigits: List<Int>,
    val column: Int = 0,
    val incoming: Int = 0,
    val base: Int = 0,
    val addendDigit: Int = 0,
    val carryNeeded: Boolean = false,
    val answered: Boolean = false,
    val lastCarry: Int = 0,
    val carries: Map<Int, Int> = emptyMap(),
    val results: Map<Int, Int> = emptyMap(),
) {
    val sum: Int get() = base + addendDigit
}

private fun typedDigits(value: Int): List<Int> =
    listOf(value / 100, (value / 10) % 10, value % 10)

private fun TypedAdditionState.setupColumn(): TypedAdditionState {
    val columnBase = firstDigits[column] + incoming
    return copy(
        base = columnBase,
        addendDigit = addendDigits[column],
        carryNeeded = columnBase + addendDigits[column] >= 10,
        answered = false,
    )
}

private fun newTypedAdditionState(): TypedAdditionState {
    val first = Random.nextInt(100, 1000)
    val addend = Random.nextInt(100, 1000)
    return TypedAdditionState(
        first = first,
        addend = addend,
        firstDigits = typedDigits(first),
        addendDigits = typedDigits(addend),
    ).setupColumn()
}

private fun TypedAdditionState.resolve(): TypedAdditionState {
    if (answered) return this
    val columnSum = base + addendDigit
    val carry = if (columnSum >= 10) 1 else 0
    var next = copy(
        answered = true,
        lastCarry = carry,
        carries = carries + (column to carry),
        results = results + (column to columnSum % 10),
    )
    if (column >= TYPED_ADD_DIGITS - 1 && carry == 1) {
        next = next.copy(results = next.results + (TYPED_ADD_DIGITS to 1))
    }
    return next
}

private fun TypedAdditionState.next(): TypedAdditionState =
    if (column >= TYPED_ADD_DIGITS - 1) {
        newTypedAdditionState()
    } else {
        copy(column = column + 1, incoming = lastCarry).setupColumn()
    }

private fun TypedAdditionState.carryAt(order: Int): String {
    if (order < 1) return ""
    return when (carries[order - 1]) {
        1 -> "1"
        else -> ""
    }
}

private fun TypedAdditionState.resultAt(order: Int): String =
    results[order]?.toString() ?: ""

private fun writtenIndices(state: TypedAdditionState, digitText: String): Set<Int> {
    if (digitText.isEmpty()) return emptySet()
    val written = digitText.toIntOrNull() ?: return emptySet()
    val total = state.base + state.addendDigit
    val available = if (state.carryNeeded) {
        (10 until total).toList()
    } else {
        (0 until total).toList()
    }
    return available.take(written.coerceAtLeast(0)).toSet()
}

@Composable
private fun typedOrderName(column: Int): String {
    val xs = practicingAdditionTypedScreenStringsForLanguage(LocalAppLanguage.current)
    return when (column) {
        0 -> xs.orderUnits
        1 -> xs.orderTens
        else -> xs.orderHundreds
    }
}

// MARK: - Intro screen

@Composable
fun PracticingAdditionTypedIntroScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = practicingAdditionTypedScreenStringsForLanguage(LocalAppLanguage.current)

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
                    text = xs.introTitle,
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
            Spacer(Modifier.height(16.dp))

            Text(
                text = xs.introBody,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = xs.checkingBody,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = xs.complementBody,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = xs.finishBody,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(24.dp))

            FilledTonalButton(
                onClick = onNavigateNext,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = TypedGreen,
                    contentColor = Color.White
                )
            ) {
                Text(xs.start, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// MARK: - Game screen

@Composable
private fun RowScope.TypedTextCell(text: String, active: Boolean) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(42.dp)
            .then(
                if (active) Modifier.background(TypedActiveCell, RoundedCornerShape(4.dp))
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RowScope.TypedOpCell(text: String) {
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
            color = TypedGreenDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RowScope.TypedInputCell(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    active: Boolean
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(42.dp)
            .then(
                if (active) Modifier.background(TypedActiveCell, RoundedCornerShape(4.dp))
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = { raw ->
                onValueChange(raw.filter { it.isDigit() }.takeLast(1))
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = TypedBlue
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(32.dp)
                .height(32.dp)
                .background(TypedInputBackground, RoundedCornerShape(4.dp))
                .border(2.dp, TypedBlue, RoundedCornerShape(4.dp))
                .padding(2.dp)
                .semantics { contentDescription = label }
        )
    }
}

@Composable
private fun TypedAdditionBoard(
    state: TypedAdditionState,
    digitText: String,
    carryText: String,
    onDigitChange: (String) -> Unit,
    onCarryChange: (String) -> Unit
) {
    val xs = practicingAdditionTypedScreenStringsForLanguage(LocalAppLanguage.current)
    val pending = !state.answered

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            TypedOpCell("")
            for (order in TYPED_ADD_DIGITS downTo 0) {
                if (pending && state.carryNeeded && order == state.column + 1) {
                    TypedInputCell(
                        value = carryText,
                        onValueChange = onCarryChange,
                        label = xs.carryLabel,
                        active = false
                    )
                } else {
                    TypedTextCell(
                        text = state.carryAt(order),
                        active = order == state.column
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            TypedOpCell("")
            for (order in TYPED_ADD_DIGITS downTo 0) {
                TypedTextCell(
                    text = if (order < TYPED_ADD_DIGITS) state.firstDigits[order].toString() else "",
                    active = order == state.column
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            TypedOpCell("+")
            for (order in TYPED_ADD_DIGITS downTo 0) {
                TypedTextCell(
                    text = if (order < TYPED_ADD_DIGITS) state.addendDigits[order].toString() else "",
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
                    .background(TypedCellBorder)
            )
        }
        Row(Modifier.fillMaxWidth()) {
            TypedOpCell("")
            for (order in TYPED_ADD_DIGITS downTo 0) {
                if (pending && order == state.column) {
                    TypedInputCell(
                        value = digitText,
                        onValueChange = onDigitChange,
                        label = xs.digitLabel,
                        active = true
                    )
                } else {
                    TypedTextCell(
                        text = state.resultAt(order),
                        active = order == state.column
                    )
                }
            }
        }
    }
}

@Composable
private fun TypedSquare(index: Int, state: TypedAdditionState, written: Set<Int>) {
    val revealed = state.answered
    val isWritten = index in written
    val fill = when {
        isWritten && index < state.base -> TypedWrittenBaseSquare
        isWritten -> TypedWrittenSquare
        index < state.base -> TypedBaseSquare
        index - state.base < (if (revealed) state.addendDigit else 0) -> TypedAddedSquare
        else -> TypedPendingSquare
    }
    val isComplement = index >= state.base && index < 10
    val usedComplement = index - state.base < (if (revealed) state.addendDigit else 0)

    Box(
        modifier = Modifier
            .size(24.dp)
            .background(fill, RoundedCornerShape(4.dp))
            .border(2.dp, TypedCellBorder, RoundedCornerShape(4.dp))
    ) {
        if (isComplement) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .border(
                        2.dp,
                        if (usedComplement) TypedComplementUsed else TypedComplement,
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TypedColumnSquares(state: TypedAdditionState, digitText: String) {
    val xs = practicingAdditionTypedScreenStringsForLanguage(LocalAppLanguage.current)
    val total = state.base + state.addendDigit
    val complement = if (state.base < 10) 10 - state.base else 0
    val revealed = state.answered
    val used = if (revealed) state.addendDigit else 0
    val written = if (revealed) emptySet() else writtenIndices(state, digitText)

    val note = buildString {
        append(xs.squaresUsed.format(used))
        if (complement > 0) {
            append(" ")
            append(
                if (used < complement) {
                    xs.complementPending.format(state.base, complement, complement - used)
                } else {
                    xs.complementUsed.format(state.base, complement, used - complement)
                }
            )
        }
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
            if (total >= 10) {
                Row(
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                color = TypedTenGroup,
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
                        TypedSquare(index = index, state = state, written = written)
                    }
                }
                for (index in 10 until total) {
                    TypedSquare(index = index, state = state, written = written)
                }
            } else {
                for (index in 0 until total) {
                    TypedSquare(index = index, state = state, written = written)
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = if (revealed) {
                "${state.base} + ${state.addendDigit} = $total"
            } else {
                "${state.base} + ${state.addendDigit} = ?"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = note,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = Color(0xFF8A4B08),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun PracticingAdditionTypedGameScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToRoadToSomewhere: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = practicingAdditionTypedScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }

    var state by remember { mutableStateOf(newTypedAdditionState()) }
    var digitText by remember { mutableStateOf("") }
    var carryText by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var feedbackIsError by remember { mutableStateOf(false) }
    var arrivalAwarded by remember { mutableStateOf(false) }
    var completionHandled by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    val initialScore = remember { currentScore }

    LaunchedEffect(Unit) {
        preferences.recordLessonCompletion()
        if (!arrivalAwarded) {
            arrivalAwarded = true
            onScoreChanged(initialScore + 2)
        }
    }

    LaunchedEffect(state.answered, state.column) {
        if (state.answered && state.column >= TYPED_ADD_DIGITS - 1 && !completionHandled) {
            completionHandled = true
            preferences.markRoadToSomewhereSectionCompleted("practicing_addition_typed")
        }
    }

    val currentOrder = typedOrderName(state.column)
    val question = xs.questionTemplate.format(currentOrder, state.base, state.addendDigit)
    val incomingNote = if (state.incoming > 0) {
        xs.incomingNote.format(state.firstDigits[state.column], state.base)
    } else {
        null
    }
    val complement = if (state.base < 10) 10 - state.base else 0
    val lastColumnDone = state.answered && state.column >= TYPED_ADD_DIGITS - 1

    fun evaluate(newDigit: String, newCarry: String) {
        if (state.answered) return
        val sum = state.sum
        val correctDigit = sum % 10
        val correctCarry = if (sum >= 10) 1 else 0
        val digitOk = newDigit.isNotEmpty() && newDigit.toIntOrNull() == correctDigit
        val carryOk = !state.carryNeeded ||
            (newCarry.isNotEmpty() && newCarry.toIntOrNull() == correctCarry)

        if (newDigit.isNotEmpty() && !digitOk) {
            feedback = xs.wrongDigit.format(state.base, complement, currentOrder)
            feedbackIsError = true
            return
        }
        if (state.carryNeeded && newCarry.isNotEmpty() && !carryOk) {
            feedback = xs.wrongCarry.format(state.base, state.addendDigit)
            feedbackIsError = true
            return
        }
        if (digitOk && carryOk) {
            val isLast = state.column >= TYPED_ADD_DIGITS - 1
            state = state.resolve()
            val explanation = if (sum >= 10) {
                xs.msgCarry.format(state.base, state.addendDigit, sum, sum % 10, currentOrder)
            } else {
                xs.msgNoCarry.format(state.base, state.addendDigit, sum, sum % 10, currentOrder)
            }
            feedback = if (isLast) {
                xs.perfect.format(state.first, state.addend, state.first + state.addend) + "\n" + explanation
            } else {
                xs.congrats + "\n" + explanation
            }
            feedbackIsError = false
        } else {
            feedback = ""
            feedbackIsError = false
        }
    }

    fun nextColumn() {
        state = state.next()
        digitText = ""
        carryText = ""
        feedback = ""
        feedbackIsError = false
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
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (order in 0 until TYPED_ADD_DIGITS) {
                        val active = order == state.column
                        Box(
                            modifier = Modifier
                                .background(
                                    if (active) TypedBlue else Color(0xFFF4F4F4),
                                    RoundedCornerShape(6.dp)
                                )
                                .border(
                                    1.dp,
                                    if (active) TypedBlue else Color(0xFFBBBBBB),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = typedOrderName(order),
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
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                if (incomingNote != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = incomingNote,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    TypedAdditionBoard(
                        state = state,
                        digitText = digitText,
                        carryText = carryText,
                        onDigitChange = {
                            digitText = it
                            evaluate(it, carryText)
                        },
                        onCarryChange = {
                            carryText = it
                            evaluate(digitText, it)
                        }
                    )
                }

                Spacer(Modifier.height(12.dp))

                TypedColumnSquares(state = state, digitText = digitText)

                if (feedback.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (feedbackIsError) MaterialTheme.colorScheme.error else TypedGreenDark,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                if (state.answered) {
                    Spacer(Modifier.height(12.dp))

                    FilledTonalButton(
                        onClick = { nextColumn() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = TypedGreen,
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

                if (lastColumnDone) {
                    Spacer(Modifier.height(20.dp))

                    FilledTonalButton(
                        onClick = onNavigateToRoadToSomewhere,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = TypedGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(hts.aRoadToSomewhere, fontWeight = FontWeight.Bold)
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
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", TYPED_ORIGINAL_TEXT_URL))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        safeOpenUri(uriHandler, TYPED_ORIGINAL_TEXT_URL)
                    }
                )
            }
        }
    }
}
