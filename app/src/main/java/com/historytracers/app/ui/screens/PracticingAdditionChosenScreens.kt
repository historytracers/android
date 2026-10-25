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
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.practicingAdditionChosenScreenStringsForLanguage

private const val CHOSEN_ORIGINAL_TEXT_URL =
    "https://www.historytracers.org/index.html?page=class_content&arg=4d141853-389b-4ee9-87f2-03d8b2e4d5e4"
private const val CHOSEN_MAX = 99999
private const val CHOSEN_MAX_DIGITS = 5

private val ChosenGreen = Color(0xFF4CAF50)
private val ChosenGreenDark = Color(0xFF2E7D32)
private val ChosenBlue = Color(0xFF2C5F8A)
private val ChosenInputBackground = Color(0xFFEAF3FB)
private val ChosenCellBorder = Color(0xFF333333)
private val ChosenActiveCell = Color(0xFFADD8E6)
private val ChosenBaseSquare = Color(0xFFADD8E6)
private val ChosenAddedSquare = Color(0xFFFFFFE0)
private val ChosenPendingSquare = Color.White
private val ChosenWrittenSquare = Color(0xFFFFFFE0)
private val ChosenWrittenBaseSquare = Color(0xFF00008B)
private val ChosenComplement = Color(0xFFE67E22)
private val ChosenComplementUsed = Color(0xFFD35400)
private val ChosenTenGroup = Color(0xFFC0392B)

private data class ChosenAdditionState(
    val first: Int,
    val addend: Int,
    val columns: Int,
    val firstDigits: List<Int>,
    val addendDigits: List<Int>,
    val column: Int = 0,
    val incoming: Int = 0,
    val base: Int = 0,
    val addendDigit: Int = 0,
    val carryNeeded: Boolean = false,
    val carries: Map<Int, Int> = emptyMap(),
    val results: Map<Int, Int> = emptyMap(),
    val answered: Boolean = false,
    val lastCarry: Int = 0,
) {
    val sum: Int get() = base + addendDigit
}

// Returns the digits of value from the units order upwards (index 0 = units).
private fun chosenDigits(value: Int, length: Int): List<Int> {
    val digits = MutableList(length) { 0 }
    var remaining = value
    for (index in 0 until length) {
        digits[index] = remaining % 10
        remaining /= 10
    }
    return digits
}

private fun ChosenAdditionState.setupColumn(): ChosenAdditionState {
    val columnBase = firstDigits[column] + incoming
    return copy(
        base = columnBase,
        addendDigit = addendDigits[column],
        carryNeeded = columnBase + addendDigits[column] >= 10,
        answered = false,
    )
}

private fun newChosenAdditionState(first: Int, addend: Int): ChosenAdditionState {
    val columns = maxOf(first.toString().length, addend.toString().length)
    return ChosenAdditionState(
        first = first,
        addend = addend,
        columns = columns,
        firstDigits = chosenDigits(first, columns),
        addendDigits = chosenDigits(addend, columns),
    ).setupColumn()
}

private fun ChosenAdditionState.resolve(): ChosenAdditionState {
    if (answered) return this
    val columnSum = base + addendDigit
    val carry = if (columnSum >= 10) 1 else 0
    var next = copy(
        answered = true,
        lastCarry = carry,
        carries = carries + (column to carry),
        results = results + (column to columnSum % 10),
    )
    if (column >= columns - 1 && carry == 1) {
        next = next.copy(results = next.results + (columns to 1))
    }
    return next
}

private fun ChosenAdditionState.next(): ChosenAdditionState? =
    if (column >= columns - 1) {
        null
    } else {
        copy(column = column + 1, incoming = lastCarry).setupColumn()
    }

private fun ChosenAdditionState.carryAt(order: Int): String {
    if (order < 1) return ""
    return when (carries[order - 1]) {
        1 -> "1"
        else -> ""
    }
}

private fun ChosenAdditionState.resultAt(order: Int): String =
    results[order]?.toString() ?: ""

private fun writtenIndices(state: ChosenAdditionState, digitText: String): Set<Int> {
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
private fun chosenOrderName(column: Int): String {
    val xs = practicingAdditionChosenScreenStringsForLanguage(LocalAppLanguage.current)
    return when (column) {
        0 -> xs.orderUnits
        1 -> xs.orderTens
        2 -> xs.orderHundreds
        3 -> xs.orderThousands
        else -> xs.orderTenThousands
    }
}

// MARK: - Game screen

@Composable
private fun RowScope.ChosenTextCell(text: String, active: Boolean) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(42.dp)
            .then(
                if (active) Modifier.background(ChosenActiveCell, RoundedCornerShape(4.dp))
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
private fun RowScope.ChosenOpCell(text: String) {
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
            color = ChosenGreenDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RowScope.ChosenInputCell(
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
                if (active) Modifier.background(ChosenActiveCell, RoundedCornerShape(4.dp))
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
                color = ChosenBlue
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(32.dp)
                .height(32.dp)
                .background(ChosenInputBackground, RoundedCornerShape(4.dp))
                .border(2.dp, ChosenBlue, RoundedCornerShape(4.dp))
                .padding(2.dp)
                .semantics { contentDescription = label }
        )
    }
}

@Composable
private fun ChosenAdditionBoard(
    state: ChosenAdditionState,
    digitText: String,
    carryText: String,
    onDigitChange: (String) -> Unit,
    onCarryChange: (String) -> Unit
) {
    val xs = practicingAdditionChosenScreenStringsForLanguage(LocalAppLanguage.current)
    val pending = !state.answered

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            ChosenOpCell("")
            for (order in state.columns downTo 0) {
                if (pending && state.carryNeeded && order == state.column + 1) {
                    ChosenInputCell(
                        value = carryText,
                        onValueChange = onCarryChange,
                        label = xs.carryLabel,
                        active = false
                    )
                } else {
                    ChosenTextCell(
                        text = state.carryAt(order),
                        active = order == state.column
                    )
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            ChosenOpCell("")
            for (order in state.columns downTo 0) {
                ChosenTextCell(
                    text = if (order < state.columns) state.firstDigits[order].toString() else "",
                    active = order == state.column
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            ChosenOpCell("+")
            for (order in state.columns downTo 0) {
                ChosenTextCell(
                    text = if (order < state.columns) state.addendDigits[order].toString() else "",
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
                    .background(ChosenCellBorder)
            )
        }
        Row(Modifier.fillMaxWidth()) {
            ChosenOpCell("")
            for (order in state.columns downTo 0) {
                if (pending && order == state.column) {
                    ChosenInputCell(
                        value = digitText,
                        onValueChange = onDigitChange,
                        label = xs.digitLabel,
                        active = true
                    )
                } else {
                    ChosenTextCell(
                        text = state.resultAt(order),
                        active = order == state.column
                    )
                }
            }
        }
    }
}

@Composable
private fun ChosenSquare(index: Int, state: ChosenAdditionState, written: Set<Int>) {
    val revealed = state.answered
    val isWritten = index in written
    val fill = when {
        isWritten && index < state.base -> ChosenWrittenBaseSquare
        isWritten -> ChosenWrittenSquare
        index < state.base -> ChosenBaseSquare
        index - state.base < (if (revealed) state.addendDigit else 0) -> ChosenAddedSquare
        else -> ChosenPendingSquare
    }
    val isComplement = index >= state.base && index < 10
    val usedComplement = index - state.base < (if (revealed) state.addendDigit else 0)

    Box(
        modifier = Modifier
            .size(24.dp)
            .background(fill, RoundedCornerShape(4.dp))
            .border(2.dp, ChosenCellBorder, RoundedCornerShape(4.dp))
    ) {
        if (isComplement) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .border(
                        2.dp,
                        if (usedComplement) ChosenComplementUsed else ChosenComplement,
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChosenColumnSquares(state: ChosenAdditionState, digitText: String) {
    val xs = practicingAdditionChosenScreenStringsForLanguage(LocalAppLanguage.current)
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
                                color = ChosenTenGroup,
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
                        ChosenSquare(index = index, state = state, written = written)
                    }
                }
                for (index in 10 until total) {
                    ChosenSquare(index = index, state = state, written = written)
                }
            } else {
                for (index in 0 until total) {
                    ChosenSquare(index = index, state = state, written = written)
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
fun PracticingAdditionChosenGameScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToRoadToSomewhere: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = practicingAdditionChosenScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }

    var firstText by remember { mutableStateOf("") }
    var secondText by remember { mutableStateOf("") }
    var state by remember { mutableStateOf<ChosenAdditionState?>(null) }
    var digitText by remember { mutableStateOf("") }
    var carryText by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var feedbackIsError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
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

    LaunchedEffect(state?.answered, state?.column, state?.columns) {
        val current = state ?: return@LaunchedEffect
        if (current.answered && current.column >= current.columns - 1 && !completionHandled) {
            completionHandled = true
            preferences.markRoadToSomewhereSectionCompleted("practicing_addition_chosen")
        }
    }

    fun startAddition() {
        val first = firstText.toIntOrNull()
        val second = secondText.toIntOrNull()
        if (first == null || second == null || first !in 0..CHOSEN_MAX || second !in 0..CHOSEN_MAX) {
            errorText = xs.errorRange.format(0, CHOSEN_MAX)
            return
        }
        errorText = ""
        state = newChosenAdditionState(first, second)
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
                val current = state

                Spacer(Modifier.height(12.dp))

                Text(
                    text = xs.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                if (current == null) {
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = xs.setupTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = firstText,
                            onValueChange = { raw ->
                                firstText = raw.filter { it.isDigit() }.take(CHOSEN_MAX_DIGITS)
                                errorText = ""
                            },
                            label = { Text(xs.firstNumber) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = secondText,
                            onValueChange = { raw ->
                                secondText = raw.filter { it.isDigit() }.take(CHOSEN_MAX_DIGITS)
                                errorText = ""
                            },
                            label = { Text(xs.secondNumber) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (errorText.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = errorText,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    FilledTonalButton(
                        onClick = { startAddition() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ChosenGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(xs.start, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (order in 0 until current.columns) {
                            val active = order == current.column
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (active) ChosenBlue else Color(0xFFF4F4F4),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (active) ChosenBlue else Color(0xFFBBBBBB),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = chosenOrderName(order),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                    color = if (active) Color.White else Color(0xFF555555)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    val currentOrder = chosenOrderName(current.column)
                    val question = xs.questionTemplate.format(
                        currentOrder,
                        current.base,
                        current.addendDigit
                    )

                    Text(
                        text = question,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    if (current.incoming > 0) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = xs.incomingNote.format(
                                current.firstDigits[current.column],
                                current.base
                            ),
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
                        ChosenAdditionBoard(
                            state = current,
                            digitText = digitText,
                            carryText = carryText,
                            onDigitChange = { newDigit ->
                                digitText = newDigit
                                if (!current.answered) {
                                    val sum = current.sum
                                    val correctDigit = sum % 10
                                    val correctCarry = if (sum >= 10) 1 else 0
                                    val complement = if (current.base < 10) 10 - current.base else 0
                                    val digitOk = newDigit.isNotEmpty() &&
                                        newDigit.toIntOrNull() == correctDigit
                                    val carryOk = !current.carryNeeded ||
                                        (carryText.isNotEmpty() &&
                                            carryText.toIntOrNull() == correctCarry)
                                    if (newDigit.isNotEmpty() && !digitOk) {
                                        feedback = when {
                                            current.base >= 10 -> xs.wrongDigitOver.format(
                                                current.base,
                                                current.addendDigit,
                                                currentOrder
                                            )
                                            current.addendDigit >= complement -> xs.wrongDigitTen.format(
                                                current.base,
                                                current.addendDigit,
                                                complement,
                                                currentOrder
                                            )
                                            else -> xs.wrongDigitBelow.format(
                                                current.base,
                                                current.addendDigit,
                                                complement,
                                                currentOrder
                                            )
                                        }
                                        feedbackIsError = true
                                    } else if (current.carryNeeded && carryText.isNotEmpty() && !carryOk) {
                                        feedback = xs.wrongCarry.format(
                                            current.base,
                                            current.addendDigit
                                        )
                                        feedbackIsError = true
                                    } else if (digitOk && carryOk) {
                                        val isLast = current.column >= current.columns - 1
                                        state = current.resolve()
                                        val explanation = if (sum >= 10) {
                                            xs.msgCarry.format(
                                                current.base,
                                                current.addendDigit,
                                                sum,
                                                sum % 10,
                                                currentOrder
                                            )
                                        } else {
                                            xs.msgNoCarry.format(
                                                current.base,
                                                current.addendDigit,
                                                sum,
                                                sum % 10,
                                                currentOrder
                                            )
                                        }
                                        feedback = if (isLast) {
                                            xs.perfect.format(
                                                current.first,
                                                current.addend,
                                                current.first + current.addend
                                            ) + "\n" + explanation
                                        } else {
                                            xs.congrats + "\n" + explanation
                                        }
                                        feedbackIsError = false
                                    } else {
                                        feedback = ""
                                        feedbackIsError = false
                                    }
                                }
                            },
                            onCarryChange = { newCarry ->
                                carryText = newCarry
                                if (!current.answered) {
                                    val sum = current.sum
                                    val correctDigit = sum % 10
                                    val correctCarry = if (sum >= 10) 1 else 0
                                    val digitOk = digitText.isNotEmpty() &&
                                        digitText.toIntOrNull() == correctDigit
                                    val carryOk = !current.carryNeeded ||
                                        (newCarry.isNotEmpty() &&
                                            newCarry.toIntOrNull() == correctCarry)
                                    if (current.carryNeeded && newCarry.isNotEmpty() && !carryOk) {
                                        feedback = xs.wrongCarry.format(
                                            current.base,
                                            current.addendDigit
                                        )
                                        feedbackIsError = true
                                    } else if (digitOk && carryOk) {
                                        val isLast = current.column >= current.columns - 1
                                        state = current.resolve()
                                        val explanation = if (sum >= 10) {
                                            xs.msgCarry.format(
                                                current.base,
                                                current.addendDigit,
                                                sum,
                                                sum % 10,
                                                currentOrder
                                            )
                                        } else {
                                            xs.msgNoCarry.format(
                                                current.base,
                                                current.addendDigit,
                                                sum,
                                                sum % 10,
                                                currentOrder
                                            )
                                        }
                                        feedback = if (isLast) {
                                            xs.perfect.format(
                                                current.first,
                                                current.addend,
                                                current.first + current.addend
                                            ) + "\n" + explanation
                                        } else {
                                            xs.congrats + "\n" + explanation
                                        }
                                        feedbackIsError = false
                                    } else {
                                        feedback = ""
                                        feedbackIsError = false
                                    }
                                }
                            }
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    ChosenColumnSquares(state = current, digitText = digitText)

                    if (feedback.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = feedback,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (feedbackIsError) MaterialTheme.colorScheme.error else ChosenGreenDark,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }

                    if (current.answered) {
                        Spacer(Modifier.height(12.dp))

                        FilledTonalButton(
                            onClick = {
                                val next = current.next()
                                if (next == null) {
                                    state = null
                                    digitText = ""
                                    carryText = ""
                                    feedback = ""
                                    feedbackIsError = false
                                    errorText = ""
                                } else {
                                    state = next
                                    digitText = ""
                                    carryText = ""
                                    feedback = ""
                                    feedbackIsError = false
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ChosenGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (current.column >= current.columns - 1) {
                                    xs.newNumbers
                                } else {
                                    xs.nextColumn
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    if (current.answered && current.column >= current.columns - 1) {
                        Spacer(Modifier.height(20.dp))

                        FilledTonalButton(
                            onClick = onNavigateToRoadToSomewhere,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ChosenGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Text(hts.aRoadToSomewhere, fontWeight = FontWeight.Bold)
                        }
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
                        clipboard.setPrimaryClip(
                            ClipData.newPlainText("URL", CHOSEN_ORIGINAL_TEXT_URL)
                        )
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        safeOpenUri(uriHandler, CHOSEN_ORIGINAL_TEXT_URL)
                    }
                )
            }
        }
    }
}
