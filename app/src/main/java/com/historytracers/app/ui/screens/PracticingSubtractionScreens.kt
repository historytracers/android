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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.practicingSubtractionScreenStringsForLanguage
import kotlin.random.Random

private const val SUB_SMARTPHONE_FILE = "8bd1066b-e02b-46bf-96a0-527b2a037889"
private const val SUB_ORIGINAL_TEXT_URL =
    "https://www.historytracers.org/index.html?page=class_content&arg=4736db57-6444-4631-b3c8-08e4d1e35688"
private const val SUB_DIGITS = 3
private const val SUB_SECTION_ID = "practicing_subtraction"

private val SubGreen = Color(0xFF4CAF50)
private val SubGreenDark = Color(0xFF2E7D32)
private val SubBlue = Color(0xFF2C5F8A)
private val SubCellBorder = Color(0xFF333333)
private val SubActiveCell = Color(0xFFADD8E6)
private val SubBaseSquare = Color(0xFFADD8E6)
private val SubRemovedSquare = Color(0xFFFFF3B0)
private val SubComplement = Color(0xFFE67E22)
private val SubTenGroup = Color(0xFFC0392B)
private val SubNoteColor = Color(0xFF8A4B08)

private data class SubState(
    val top: Int,
    val bottom: Int,
    val topDigits: List<Int>,
    val bottomDigits: List<Int>,
    val column: Int = 0,
    val incoming: Int = 0,
    val base: Int = 0,
    val removed: Int = 0,
    val bottomDigit: Int = 0,
    val afterIncoming: Int = 0,
    val borrowOut: Int = 0,
    val borrowed: Boolean = false,
    val chained: Boolean = false,
    val answered: Boolean = false,
    val started: Boolean = false,
    val lastBorrow: Int = 0,
    val borrows: Map<Int, Int> = emptyMap(),
    val results: Map<Int, Int> = emptyMap(),
    val regroup: Map<Int, Int> = emptyMap(),
) {
    val liveResult: Int get() = base - removed
}

private fun subDigits(value: Int): List<Int> {
    var v = value
    return List(SUB_DIGITS) {
        val digit = v % 10
        v /= 10
        digit
    }
}

private fun SubState.setupColumn(): SubState {
    val topDigit = topDigits[column]
    val bottom = bottomDigits[column]
    val afterIncomingValue = topDigit - incoming
    var available = afterIncomingValue
    var borrowOutValue = 0
    var borrowedValue = false
    var chainedValue = false

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

    return copy(
        afterIncoming = afterIncomingValue,
        base = available,
        borrowOut = borrowOutValue,
        borrowed = borrowedValue,
        chained = chainedValue,
        bottomDigit = bottom,
        removed = 0,
        started = false,
        answered = false,
    )
}

private fun newSubState(): SubState {
    val first = Random.nextInt(100, 1000)
    val second = Random.nextInt(100, 1000)
    val top = maxOf(first, second)
    val bottom = minOf(first, second)
    return SubState(
        top = top,
        bottom = bottom,
        topDigits = subDigits(top),
        bottomDigits = subDigits(bottom),
    ).setupColumn()
}

// Shows the regrouped minuend above the original digits: the current column exposes
// its borrowed ten and the next orders lose the 1 that was borrowed.
private fun SubState.applyRegroup(): SubState {
    if (incoming <= 0 && borrowOut <= 0) return this

    var regrouped = regroup + (column to base)
    if (borrowOut > 0) {
        var next = column + 1
        var carry = 1
        while (next < SUB_DIGITS && carry == 1) {
            val value = topDigits[next] - 1
            if (value < 0) {
                regrouped = regrouped + (next to 9)
            } else {
                regrouped = regrouped + (next to value)
                carry = 0
            }
            next++
        }
    }
    return copy(regroup = regrouped)
}

// One click turns the first borrowed state into the regrouped minuend; after that,
// each click removes or puts back one square of the current column.
private fun SubState.change(delta: Int): SubState {
    if (answered) return this

    var next = this
    if (!started) {
        next = next.copy(started = true).applyRegroup()
        if (next.borrowOut > 0) return next
    }

    val newRemoved = (next.removed + delta).coerceIn(0, next.bottomDigit)
    next = next.copy(removed = newRemoved)
    return if (newRemoved == next.bottomDigit) next.resolve() else next
}

private fun SubState.resolve(): SubState {
    if (answered) return this
    return copy(
        answered = true,
        lastBorrow = if (borrowOut > 0) 1 else 0,
        borrows = borrows + (column to if (borrowOut > 0) 1 else 0),
        results = results + (column to liveResult),
    )
}

private fun SubState.next(): SubState =
    if (column >= SUB_DIGITS - 1) {
        newSubState()
    } else {
        copy(column = column + 1, incoming = lastBorrow).setupColumn()
    }

@Composable
private fun subOrderName(column: Int): String {
    val xs = practicingSubtractionScreenStringsForLanguage(LocalAppLanguage.current)
    return when (column) {
        0 -> xs.orderUnits
        1 -> xs.orderTens
        else -> xs.orderHundreds
    }
}

@Composable
private fun RowScope.SubTextCell(text: String, active: Boolean, struck: Boolean = false) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(42.dp)
            .then(
                if (active) Modifier.background(SubActiveCell, RoundedCornerShape(4.dp))
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
private fun RowScope.SubOpCell(text: String) {
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
            color = SubGreenDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SubBoard(state: SubState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Regrouped minuend, written above the original digits.
        Row(Modifier.fillMaxWidth()) {
            SubOpCell("")
            for (order in SUB_DIGITS downTo 0) {
                SubTextCell(
                    text = state.regroup[order]?.toString() ?: "",
                    active = order == state.column
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            SubOpCell("")
            for (order in SUB_DIGITS downTo 0) {
                SubTextCell(
                    text = if (order < SUB_DIGITS) state.topDigits[order].toString() else "",
                    active = order == state.column,
                    struck = state.regroup.containsKey(order)
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            SubOpCell("-")
            for (order in SUB_DIGITS downTo 0) {
                SubTextCell(
                    text = if (order < SUB_DIGITS) state.bottomDigits[order].toString() else "",
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
                    .background(SubCellBorder)
            )
        }
        Row(Modifier.fillMaxWidth()) {
            SubOpCell("")
            for (order in SUB_DIGITS downTo 0) {
                val result = when {
                    state.results[order] != null -> state.results[order].toString()
                    order == state.column && state.started -> state.liveResult.toString()
                    else -> ""
                }
                SubTextCell(text = result, active = order == state.column)
            }
        }
    }
}

@Composable
private fun SubSquare(
    index: Int,
    removed: Int,
    bottomDigit: Int,
    inTen: Boolean,
    alwaysBase: Boolean
) {
    val isRemoved = !alwaysBase && index < removed
    val isComplement = inTen && index >= bottomDigit

    Box(
        modifier = Modifier
            .size(24.dp)
            .background(
                if (isRemoved) SubRemovedSquare else SubBaseSquare,
                RoundedCornerShape(4.dp)
            )
            .border(2.dp, SubCellBorder, RoundedCornerShape(4.dp))
    ) {
        if (isComplement) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .border(2.dp, SubComplement, RoundedCornerShape(2.dp))
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SubColumnSquares(state: SubState) {
    val xs = practicingSubtractionScreenStringsForLanguage(LocalAppLanguage.current)
    val note = if (state.borrowed) {
        xs.borrowSquaresNote.format(
            state.removed,
            state.bottomDigit,
            10 - state.bottomDigit,
            10 - state.removed,
            state.base - 10
        )
    } else {
        xs.squaresUsedNote.format(state.removed, state.base - state.removed)
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
                                color = SubTenGroup,
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
                        SubSquare(
                            index = index,
                            removed = state.removed,
                            bottomDigit = state.bottomDigit,
                            inTen = true,
                            alwaysBase = false
                        )
                    }
                }
                for (index in 0 until state.base - 10) {
                    SubSquare(
                        index = index,
                        removed = state.removed,
                        bottomDigit = state.bottomDigit,
                        inTen = false,
                        alwaysBase = true
                    )
                }
            } else {
                for (index in 0 until state.base) {
                    SubSquare(
                        index = index,
                        removed = state.removed,
                        bottomDigit = state.bottomDigit,
                        inTen = false,
                        alwaysBase = false
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = "${state.base} - ${state.removed} = ${state.base - state.removed}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = note,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = SubNoteColor,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun PracticingSubtractionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = practicingSubtractionScreenStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }

    var state by remember { mutableStateOf(newSubState()) }
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
        when (val result = repo.loadAndParse("$language/$SUB_SMARTPHONE_FILE")) {
            is ContentResult.SMGame ->
                instructionText = result.data?.content?.firstOrNull()?.text?.firstOrNull()?.text
            else -> instructionText = null
        }
    }

    LaunchedEffect(state.answered, state.column) {
        if (state.answered && state.column >= SUB_DIGITS - 1 && !completionHandled) {
            completionHandled = true
            preferences.recordLessonCompletion()
            preferences.markReturningSectionCompleted(SUB_SECTION_ID)
        }
    }

    val orderName = subOrderName(state.column)
    val question = xs.questionTemplate.format(orderName, state.base, state.bottomDigit)
    val note = when {
        state.chained -> xs.chainedNote
        else -> buildString {
            if (state.incoming > 0) {
                append(xs.incomingNote.format(state.topDigits[state.column], state.afterIncoming))
            }
            if (state.borrowed) {
                if (isNotEmpty()) append(" ")
                append(xs.borrowNote.format(state.afterIncoming, state.bottomDigit, state.base))
            }
        }
    }
    val lastColumnDone = state.answered && state.column >= SUB_DIGITS - 1
    val feedback = if (state.answered) {
        val template = if (state.borrowOut > 0) xs.msgBorrow else xs.msgNoBorrow
        val message = template.format(state.base, state.removed, state.liveResult, orderName)
        if (lastColumnDone) message + "\n" + xs.completed else message
    } else {
        ""
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
                    for (order in 0 until SUB_DIGITS) {
                        val active = order == state.column
                        Box(
                            modifier = Modifier
                                .background(
                                    if (active) SubBlue else Color(0xFFF4F4F4),
                                    RoundedCornerShape(6.dp)
                                )
                                .border(
                                    1.dp,
                                    if (active) SubBlue else Color(0xFFBBBBBB),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = subOrderName(order),
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
                    SubBoard(state = state)
                }

                Spacer(Modifier.height(12.dp))

                SubColumnSquares(state = state)

                Spacer(Modifier.height(12.dp))

                if (!state.answered) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FilledTonalButton(
                            onClick = { state = state.change(-1) },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = SubGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = xs.returnSquare,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        FilledTonalButton(
                            onClick = { state = state.change(1) },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = SubGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = xs.removeSquare,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                if (feedback.isNotEmpty() && !lastColumnDone) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = SubGreenDark,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                if (state.answered) {
                    Spacer(Modifier.height(12.dp))
                    FilledTonalButton(
                        onClick = { state = state.next() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = SubGreen,
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
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", SUB_ORIGINAL_TEXT_URL))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        safeOpenUri(uriHandler, SUB_ORIGINAL_TEXT_URL)
                    }
                )
            }
        }
    }
}
