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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.practicingAdditionRoadScreenStringsForLanguage
import kotlin.random.Random

private const val ORIGINAL_TEXT_URL = "https://www.historytracers.org/index.html?page=class_content&arg=a4caa9fe-de1e-49c5-92de-50ed9c22a42d"
private const val ADD_DIGITS = 3

private val AddGreen = Color(0xFF4CAF50)
private val AddGreenDark = Color(0xFF2E7D32)
private val AddBlue = Color(0xFF2C5F8A)
private val AddCellBorder = Color(0xFF333333)
private val AddActiveCell = Color(0xFFADD8E6)
private val AddBaseSquare = Color(0xFFADD8E6)
private val AddAddedSquare = Color(0xFFFFFFE0)
private val AddPendingSquare = Color.White
private val AddComplement = Color(0xFFE67E22)
private val AddComplementUsed = Color(0xFFD35400)
private val AddTenGroup = Color(0xFFC0392B)

private data class RoadAdditionState(
    val first: Int,
    val addend: Int,
    val firstDigits: List<Int>,
    val addendDigits: List<Int>,
    val column: Int = 0,
    val incoming: Int = 0,
    val base: Int = 0,
    val addendDigit: Int = 0,
    val added: Int = 0,
    val started: Boolean = false,
    val answered: Boolean = false,
    val lastCarry: Int = 0,
    val carries: Map<Int, Int> = emptyMap(),
    val results: Map<Int, Int> = emptyMap(),
) {
    val sum: Int get() = base + added
}

private fun addDigits(value: Int): List<Int> =
    listOf(value / 100, (value / 10) % 10, value % 10)

private fun RoadAdditionState.setupColumn(): RoadAdditionState {
    val columnBase = firstDigits[column] + incoming
    return copy(
        base = columnBase,
        addendDigit = addendDigits[column],
        added = 0,
        started = false,
        answered = false,
    )
}

private fun newRoadAdditionState(): RoadAdditionState {
    val first = Random.nextInt(100, 1000)
    val addend = Random.nextInt(100, 1000)
    return RoadAdditionState(
        first = first,
        addend = addend,
        firstDigits = addDigits(first),
        addendDigits = addDigits(addend),
    ).setupColumn()
}

/** Records the result digit of the column and the 1 it carries, if any. */
private fun RoadAdditionState.resolve(): RoadAdditionState {
    if (answered) return this
    val columnSum = base + added
    val carry = if (columnSum >= 10) 1 else 0
    var next = copy(
        answered = true,
        lastCarry = carry,
        carries = carries + (column to carry),
        results = results + (column to columnSum % 10),
    )
    if (column >= ADD_DIGITS - 1 && carry == 1) {
        next = next.copy(results = next.results + (ADD_DIGITS to 1))
    }
    return next
}

/**
 * The buttons add or remove one yellow square at a time. The first click only
 * reveals the value already in the column; the column is solved as soon as the
 * squares added match the bottom digit of the second number.
 */
private fun RoadAdditionState.change(delta: Int): RoadAdditionState {
    if (answered) return this
    if (!started) {
        val revealed = copy(started = true)
        return if (revealed.added == revealed.addendDigit) revealed.resolve() else revealed
    }
    val nextAdded = (added + delta).coerceIn(0, addendDigit)
    val changed = copy(added = nextAdded, started = true)
    return if (changed.added == changed.addendDigit) changed.resolve() else changed
}

private fun RoadAdditionState.next(): RoadAdditionState =
    if (column >= ADD_DIGITS - 1) {
        newRoadAdditionState()
    } else {
        copy(column = column + 1, incoming = lastCarry).setupColumn()
    }

private fun RoadAdditionState.carryAt(order: Int): String {
    if (order < 1) return ""
    val source = order - 1
    if (source == column && !answered && started) {
        return if (sum >= 10) "1" else ""
    }
    return when (carries[source]) {
        1 -> "1"
        0 -> ""
        else -> ""
    }
}

private fun RoadAdditionState.resultAt(order: Int): String {
    if (order == column && !answered && started) return (sum % 10).toString()
    return results[order]?.toString() ?: ""
}

@Composable
private fun orderName(column: Int): String {
    val xs = practicingAdditionRoadScreenStringsForLanguage(LocalAppLanguage.current)
    return when (column) {
        0 -> xs.orderUnits
        1 -> xs.orderTens
        else -> xs.orderHundreds
    }
}

// MARK: - Intro screens

@Composable
private fun RoadIntroFrame(
    title: String,
    onNavigateBack: () -> Unit,
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    nextLabel: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val s = LocalUiStrings.current

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
                    text = title,
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

            content()

            Spacer(Modifier.height(20.dp))

            if (onNavigatePrev != null || onNavigateNext != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onNavigatePrev != null) {
                        FilledTonalButton(
                            onClick = onNavigatePrev,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = AddGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(s.common.previous, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (onNavigateNext != null) {
                        FilledTonalButton(
                            onClick = onNavigateNext,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = AddGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Text(nextLabel ?: s.common.next, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun NumberedCard(index: Int, title: String?, body: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = AddGreen,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${index + 1}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                }
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PracticingAdditionPracticeScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: () -> Unit = {}
) {
    val xs = practicingAdditionRoadScreenStringsForLanguage(LocalAppLanguage.current)

    val steps = listOf(
        xs.practiceStep1,
        xs.practiceStep2,
        xs.practiceStep3,
    )

    RoadIntroFrame(
        title = xs.practiceTitle,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext,
        nextLabel = xs.start
    ) {
        Text(
            text = xs.practiceIntro,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(16.dp))

        steps.forEachIndexed { index, body ->
            NumberedCard(
                index = index,
                title = null,
                body = body
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = xs.practiceComplement,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = xs.practiceFinish,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

// MARK: - Game screen

@Composable
private fun RowScope.DigitCell(text: String, active: Boolean) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(42.dp)
            .then(
                if (active) Modifier.background(AddActiveCell, RoundedCornerShape(4.dp))
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
private fun RowScope.OpCell(text: String) {
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
            color = AddGreenDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AdditionBoard(state: RoadAdditionState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            OpCell("")
            for (order in ADD_DIGITS downTo 0) {
                DigitCell(
                    text = state.carryAt(order),
                    active = order == state.column
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            OpCell("")
            for (order in ADD_DIGITS downTo 0) {
                DigitCell(
                    text = if (order < ADD_DIGITS) state.firstDigits[order].toString() else "",
                    active = order == state.column
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            OpCell("+")
            for (order in ADD_DIGITS downTo 0) {
                DigitCell(
                    text = if (order < ADD_DIGITS) state.addendDigits[order].toString() else "",
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
                    .background(AddCellBorder)
            )
        }
        Row(Modifier.fillMaxWidth()) {
            OpCell("")
            for (order in ADD_DIGITS downTo 0) {
                DigitCell(
                    text = state.resultAt(order),
                    active = order == state.column
                )
            }
        }
    }
}

@Composable
private fun AddSquare(index: Int, state: RoadAdditionState) {
    val fill = when {
        index < state.base -> AddBaseSquare
        index - state.base < state.added -> AddAddedSquare
        else -> AddPendingSquare
    }
    val isComplement = index >= state.base && index < 10
    val usedComplement = index - state.base < state.added

    Box(
        modifier = Modifier
            .size(24.dp)
            .background(fill, RoundedCornerShape(4.dp))
            .border(2.dp, AddCellBorder, RoundedCornerShape(4.dp))
    ) {
        if (isComplement) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .border(
                        2.dp,
                        if (usedComplement) AddComplementUsed else AddComplement,
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnSquares(state: RoadAdditionState) {
    val xs = practicingAdditionRoadScreenStringsForLanguage(LocalAppLanguage.current)
    val total = state.base + state.added
    val fullTotal = state.base + state.addendDigit
    val complement = if (state.base < 10) 10 - state.base else 0

    val note = buildString {
        append(xs.squaresUsed.format(state.added))
        if (complement > 0) {
            append(" ")
            append(
                if (state.added < complement) {
                    xs.complementPending.format(state.base, complement, complement - state.added)
                } else {
                    xs.complementUsed.format(state.base, complement, state.added - complement)
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
            if (fullTotal >= 10) {
                Row(
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                color = AddTenGroup,
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
                        AddSquare(index = index, state = state)
                    }
                }
                for (index in 10 until fullTotal) {
                    AddSquare(index = index, state = state)
                }
            } else {
                for (index in 0 until fullTotal) {
                    AddSquare(index = index, state = state)
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = "${state.base} + ${state.added} = $total",
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
fun PracticingAdditionRoadScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToRoadToSomewhere: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = practicingAdditionRoadScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }

    var state by remember { mutableStateOf(newRoadAdditionState()) }
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

    LaunchedEffect(state.column, state.answered) {
        if (state.answered && state.column >= ADD_DIGITS - 1 && !completionHandled) {
            completionHandled = true
            preferences.markRoadToSomewhereSectionCompleted("practicing_addition")
        }
    }

    val currentOrder = orderName(state.column)
    val question = xs.questionTemplate.format(currentOrder, state.base)
    val incomingNote = if (state.incoming > 0) {
        xs.incomingNote.format(state.firstDigits[state.column], state.base)
    } else {
        null
    }
    val feedback = if (state.answered) {
        val sum = state.sum
        if (sum >= 10) {
            xs.msgCarry.format(state.base, state.added, sum, sum % 10, currentOrder)
        } else {
            xs.msgNoCarry.format(state.base, state.added, sum, sum % 10, currentOrder)
        }
    } else {
        ""
    }
    val lastColumnDone = state.answered && state.column >= ADD_DIGITS - 1

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

                Text(
                    text = xs.gameInstruction,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (order in 0 until ADD_DIGITS) {
                        val active = order == state.column
                        Box(
                            modifier = Modifier
                                .background(
                                    if (active) AddBlue else Color(0xFFF4F4F4),
                                    RoundedCornerShape(6.dp)
                                )
                                .border(
                                    1.dp,
                                    if (active) AddBlue else Color(0xFFBBBBBB),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = orderName(order),
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
                    AdditionBoard(state = state)
                }

                Spacer(Modifier.height(12.dp))

                ColumnSquares(state = state)

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = { state = state.change(1) },
                        enabled = !state.answered,
                        modifier = Modifier.size(52.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = AddBlue,
                            contentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = xs.addOneSquare,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    FilledIconButton(
                        onClick = { state = state.change(-1) },
                        enabled = !state.answered,
                        modifier = Modifier.size(52.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = AddBlue,
                            contentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = xs.removeOneSquare,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                if (state.answered) {
                    Spacer(Modifier.height(12.dp))

                    if (lastColumnDone) {
                        Text(text = xs.medal, fontSize = 44.sp)
                        Spacer(Modifier.height(4.dp))
                    }

                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (state.sum >= 10) AddGreenDark else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    FilledTonalButton(
                        onClick = { state = state.next() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = AddGreen,
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

                Spacer(Modifier.height(20.dp))

                FilledTonalButton(
                    onClick = onNavigateToRoadToSomewhere,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AddGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(hts.aRoadToSomewhere, fontWeight = FontWeight.Bold)
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
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", ORIGINAL_TEXT_URL))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        safeOpenUri(uriHandler, ORIGINAL_TEXT_URL)
                    }
                )
            }
        }
    }
}
