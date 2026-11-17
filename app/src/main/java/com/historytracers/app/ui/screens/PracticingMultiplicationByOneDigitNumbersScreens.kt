// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.AutoSizeTitleText
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.ScreenAudioButton
import com.historytracers.app.ui.components.multiplicationTableFitsOnScreen
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.PracticingMultiplicationByOneDigitNumbersStrings
import com.historytracers.app.ui.features.miscStringsForLanguage
import com.historytracers.app.ui.features.practicingMultiplicationByOneDigitNumbersStringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameFile
import kotlin.random.Random

// Ports the web class "Practicing multiplication by one-digit numbers"
// (b9e71da5-355b-4c0c-8d24-a746a27fcc3e). Two screens: a rules screen and a
// game. A number of one, two or three digits (levels 1 to 3) is multiplied by a
// one-digit number, column by column, from the units. The multiplication is
// written vertically, and below the board the squares of the column are shown
// from the start: the repeated groups of the column digit turn light yellow as
// the reader repeats them with the buttons, and the squares carried from the
// previous order are light blue at the end. When every group has been repeated,
// the carry is added: each complete group of ten squares becomes the carry to
// the next order and the squares left outside the last group of ten are the
// digit that stays in the column.
private const val SMARTPHONE_GAME_FILE = "b9e71da5-355b-4c0c-8d24-a746a27fcc3e"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"
private const val RULES_ID = "d11b9242-4a3a-494e-8545-3fbc4f057160"
private const val GAME_ID = "827cd90b-74c6-47fc-927e-0f058e5358fc"

private const val PRACTICING_MULTIPLICATION_SECTION_ID =
    "practicing_multiplication_by_one_digit_numbers"

// Levels: 0 = units (one digit), 1 = tens (two digits), 2 = hundreds (three).
private const val MAX_LEVEL = 2

private val SQUARE_ADDED = Color(0xFFFFFFE0)
private val SQUARE_PENDING = Color.White
private val SQUARE_BASE = Color(0xFFADD8E6)
private val SQUARE_BORDER = Color(0xFF333333)
private val GROUP_BORDER = Color(0xFF2C5F8A)
private val TEN_GROUP_BORDER = Color(0xFFC0392B)
private val ACTIVE_CELL_BG = Color(0xFFADD8E6)
private val ORDER_CHIP_BG = Color(0xFFF4F4F4)
private val ORDER_CHIP_BORDER = Color(0xFFBBBBBB)
private val ORDER_CHIP_TEXT = Color(0xFF555555)
private val ORDER_CHIP_ACTIVE = Color(0xFF2C5F8A)
private val LEVEL_BADGE_BG = Color(0xFFEAF3FB)

private fun levelName(level: Int, xs: PracticingMultiplicationByOneDigitNumbersStrings): String =
    when (level) {
        0 -> xs.levelOneDigit
        1 -> xs.levelTwoDigits
        else -> xs.levelThreeDigits
    }

private fun orderName(order: Int, xs: PracticingMultiplicationByOneDigitNumbersStrings): String =
    when (order) {
        0 -> xs.units
        1 -> xs.tens
        else -> xs.hundreds
    }

private fun digitAt(value: Int, order: Int): Int {
    var v = value
    repeat(order) { v /= 10 }
    return v % 10
}

private fun randomMultiplicand(level: Int): Int = when (level) {
    0 -> Random.nextInt(0, 10)
    1 -> Random.nextInt(10, 100)
    else -> Random.nextInt(100, 1000)
}

@Composable
fun PracticingMultiplicationByOneDigitNumbersRulesScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = practicingMultiplicationByOneDigitNumbersStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }

    var game by remember { mutableStateOf<SMGameFile?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(language) {
        game = null
        error = null
        when (val result = repo.loadAndParse("$language/$SMARTPHONE_GAME_FILE")) {
            is ContentResult.SMGame -> game = result.data
            is ContentResult.Error -> error = result.message
            else -> error = s.common.unsupportedContentType
        }
    }

    val content = game?.content?.firstOrNull { it.id == RULES_ID }

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    fun award(points: Int) {
        if (points <= 0) return
        totalAwarded += points
        onScoreChanged(initialScore + totalAwarded)
    }

    // Visit-local arrival guard: switching the app language reloads the content
    // object, which would re-run this effect and award the same visit again.
    val arrivalAwarded = remember { mutableSetOf<String>() }

    LaunchedEffect(content) {
        val node = content
        if (node != null && node.id !in arrivalAwarded) {
            arrivalAwarded.add(node.id)
            award(node.score)
            preferences.markArrivalAwarded(node.id)
        }
    }

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
                AutoSizeTitleText(
                    text = xs.rulesTitle,
                    modifier = Modifier.padding(start = 8.dp).weight(1f)
                )
                ScreenAudioButton(
                    lessonId = SMARTPHONE_GAME_FILE,
                    contentId = RULES_ID
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                game == null && error == null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                error != null -> Text(
                    text = "${s.common.error}: $error",
                    modifier = Modifier.padding(16.dp)
                )
                content == null -> Text(
                    text = "${s.common.error}: ${s.common.unsupportedContentType}",
                    modifier = Modifier.padding(16.dp)
                )
                else -> Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        content.text?.forEach { text ->
                            if (text == null) return@forEach
                            MarkdownText(text = text.text ?: "")
                            Spacer(Modifier.height(8.dp))
                        }

                        Spacer(Modifier.height(16.dp))

                        FilledTonalButton(
                            onClick = onNavigateNext,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(s.common.next, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(Modifier.height(48.dp))
                    }

                    content.sourceMenu?.takeIf { it.isNotEmpty() }?.let { sources ->
                        PracticingMultiplicationSourcesMenu(
                            sources = sources,
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PracticingMultiplicationByOneDigitNumbersGameScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToMultiplicationTableHub: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val mcs = miscStringsForLanguage(LocalAppLanguage.current)
    val xs = practicingMultiplicationByOneDigitNumbersStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }

    var game by remember { mutableStateOf<SMGameFile?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(language) {
        game = null
        error = null
        when (val result = repo.loadAndParse("$language/$SMARTPHONE_GAME_FILE")) {
            is ContentResult.SMGame -> game = result.data
            is ContentResult.Error -> error = result.message
            else -> error = s.common.unsupportedContentType
        }
    }

    val content = game?.content?.firstOrNull { it.id == GAME_ID }

    // Game state. The level starts at the first one (one-digit numbers).
    var level by remember { mutableIntStateOf(0) }
    var multiplicand by remember { mutableIntStateOf(randomMultiplicand(0)) }
    var multiplier by remember { mutableIntStateOf(Random.nextInt(0, 10)) }
    var column by remember { mutableIntStateOf(0) }
    var incoming by remember { mutableIntStateOf(0) }
    var carries by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var results by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var groups by remember { mutableIntStateOf(0) }
    var started by remember { mutableStateOf(false) }
    var answered by remember { mutableStateOf(false) }
    var lastCarry by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("") }

    val digits = level + 1
    val digit = digitAt(multiplicand, column)

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    fun award(points: Int) {
        if (points <= 0) return
        totalAwarded += points
        onScoreChanged(initialScore + totalAwarded)
    }

    // Visit-local arrival guard: switching the app language reloads the content
    // object, which would re-run this effect and award the same visit again.
    val arrivalAwarded = remember { mutableSetOf<String>() }

    LaunchedEffect(content) {
        val node = content
        if (node != null && node.id !in arrivalAwarded) {
            arrivalAwarded.add(node.id)
            award(node.score)
            preferences.markArrivalAwarded(node.id)
        }
    }

    // This is the group's last screen, so it records the day's streak on arrival.
    LaunchedEffect(Unit) {
        preferences.recordLessonCompletion()
    }

    fun settleColumn() {
        answered = false
        groups = 0
        started = false
        feedback = ""
    }

    fun newProblem() {
        level = (level + 1) % (MAX_LEVEL + 1)
        multiplicand = randomMultiplicand(level)
        multiplier = Random.nextInt(0, 10)
        column = 0
        incoming = 0
        carries = emptyMap()
        results = emptyMap()
        lastCarry = 0
        settleColumn()
    }

    fun resolve() {
        if (answered) return
        answered = true
        val product = digit * multiplier
        val total = product + incoming
        val carryOut = total / 10
        val order = orderName(column, xs)
        carries = carries + (column to carryOut)
        results = results + (column to (total % 10))
        lastCarry = carryOut
        feedback = when {
            incoming > 0 && carryOut > 0 -> xs.msgCarryIn
            incoming > 0 -> xs.msgNoCarryIn
            carryOut > 0 -> xs.msgCarry
            else -> xs.msgNoCarry
        }.format(digit, multiplier, product, incoming, total, total % 10, carryOut, order)
        if (column >= digits - 1 && carryOut > 0) {
            // The carry out of the last column creates a new order on the left.
            results = results + (column + 1 to carryOut)
        }
        award(1)
    }

    // Repeat (delta = 1) or remove (delta = -1) one group of squares. The column
    // is solved as soon as every group has been repeated.
    fun change(delta: Int) {
        if (answered) return
        if (!started) {
            started = true
            if (groups == multiplier) resolve()
            return
        }
        groups = (groups + delta).coerceIn(0, multiplier)
        if (groups == multiplier) resolve()
    }

    fun next() {
        if (!answered) return
        if (column >= digits - 1) {
            newProblem()
        } else {
            column += 1
            incoming = lastCarry
            settleColumn()
        }
    }

    // The whole multiplication is complete: the class button outside can change
    // colour.
    val multiplicationComplete = answered && column >= digits - 1
    LaunchedEffect(multiplicationComplete) {
        if (multiplicationComplete) {
            preferences.markMultiplicationTableSectionCompleted(PRACTICING_MULTIPLICATION_SECTION_ID)
        }
    }

    val compactLayout = !multiplicationTableFitsOnScreen()
    val introMaxHeight = (LocalConfiguration.current.screenHeightDp * 0.16f)
        .dp
        .coerceIn(60.dp, 120.dp)

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
                AutoSizeTitleText(
                    text = xs.gameTitle,
                    modifier = Modifier.padding(start = 8.dp).weight(1f)
                )
                ScreenAudioButton(
                    lessonId = SMARTPHONE_GAME_FILE,
                    contentId = GAME_ID
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                game == null && error == null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                error != null -> Text(
                    text = "${s.common.error}: $error",
                    modifier = Modifier.padding(16.dp)
                )
                content == null -> Text(
                    text = "${s.common.error}: ${s.common.unsupportedContentType}",
                    modifier = Modifier.padding(16.dp)
                )
                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (compactLayout) {
                                Modifier.verticalScroll(rememberScrollState())
                            } else {
                                Modifier
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // The introduction keeps a bounded height so the board, the
                    // squares and the buttons always keep their space.
                    val intro: @Composable () -> Unit = {
                        content.text?.forEach { text ->
                            if (text == null) return@forEach
                            MarkdownText(text = text.text ?: "")
                        }
                    }
                    if (compactLayout) {
                        intro()
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = introMaxHeight)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Column { intro() }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    MultLevelRow(
                        level = level,
                        digits = digits,
                        activeColumn = column
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = xs.question.format(
                            orderName(column, xs),
                            digit,
                            multiplier
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    if (incoming > 0) {
                        Text(
                            text = xs.incomingNote.format(incoming),
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    MultBoard(
                        top = run {
                            val last = digits - 1
                            var top = last
                            if (column == last) {
                                val carryOut = when {
                                    answered -> carries[last] ?: 0
                                    started -> (digit * groups) / 10
                                    else -> 0
                                }
                                if (carryOut > 0) top = last + 1
                            }
                            top
                        },
                        digits = digits,
                        multiplicand = multiplicand,
                        multiplier = multiplier,
                        column = column,
                        carries = carries,
                        results = results,
                        painted = digit * groups,
                        started = started,
                        answered = answered
                    )

                    Spacer(Modifier.height(6.dp))

                    MultSquaresPanel(
                        digit = digit,
                        multiplier = multiplier,
                        groups = groups,
                        incoming = incoming,
                        answered = answered,
                        order = orderName(column, xs),
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (compactLayout) {
                                    Modifier.height(190.dp)
                                } else {
                                    Modifier.weight(1f)
                                }
                            )
                    )

                    // Fixed message area: the column message (and the medal at
                    // the end), so the buttons keep their place.
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 62.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (multiplicationComplete) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = xs.medal, fontSize = 24.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = feedback,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                            }
                        } else if (feedback.isNotEmpty()) {
                            Text(
                                text = feedback,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RepeatButton(
                            up = true,
                            enabled = !answered,
                            description = xs.repeatGroup,
                            onClick = { change(1) }
                        )
                        RepeatButton(
                            up = false,
                            enabled = !answered,
                            description = xs.removeGroup,
                            onClick = { change(-1) }
                        )
                        if (answered) {
                            FilledTonalButton(
                                onClick = { next() },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFF4CAF50),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    if (column >= digits - 1) xs.newProblem else xs.nextColumn,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilledTonalButton(
                            onClick = onNavigatePrev,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(s.common.previous, fontWeight = FontWeight.Bold)
                        }
                        FilledTonalButton(
                            onClick = onNavigateToMultiplicationTableHub,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(mcs.multiplicationTable, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                }
            }

            content?.sourceMenu?.takeIf { it.isNotEmpty() }?.let { sources ->
                PracticingMultiplicationSourcesMenu(
                    sources = sources,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

/**
 * The level badge and the order chips (units, tens, hundreds) with the column
 * being solved highlighted.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MultLevelRow(
    level: Int,
    digits: Int,
    activeColumn: Int
) {
    val xs = practicingMultiplicationByOneDigitNumbersStringsForLanguage(LocalAppLanguage.current)

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = LEVEL_BADGE_BG
        ) {
            Text(
                text = xs.levelFormat.format(level + 1, levelName(level, xs)),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        for (order in 0 until digits) {
            val active = order == activeColumn
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (active) ORDER_CHIP_ACTIVE else ORDER_CHIP_BG,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = if (active) ORDER_CHIP_ACTIVE else ORDER_CHIP_BORDER,
                    shape = RoundedCornerShape(12.dp)
                )
            ) {
                Text(
                    text = orderName(order, xs),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                    color = if (active) Color.White else ORDER_CHIP_TEXT,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * The vertical multiplication: the carry row, the number being multiplied, the
 * one-digit multiplier below the multiplication sign, the result line and the
 * result row. The column being solved is highlighted and follows the squares
 * repeated so far, so its digit and its carry grow one group at a time.
 */
@Composable
private fun MultBoard(
    top: Int,
    digits: Int,
    multiplicand: Int,
    multiplier: Int,
    column: Int,
    carries: Map<Int, Int>,
    results: Map<Int, Int>,
    painted: Int,
    started: Boolean,
    answered: Boolean
) {
    val cellWidth = 26.dp

    fun carryFor(order: Int): String {
        if (order < 1) return ""
        if (order - 1 == column && !answered && started) {
            return if (painted >= 10) "${painted / 10}" else ""
        }
        // A zero carry is not written on the board.
        return carries[order - 1]?.takeIf { it > 0 }?.toString() ?: ""
    }

    fun multiplicandFor(order: Int): String =
        if (order <= digits - 1) digitAt(multiplicand, order).toString() else ""

    fun resultFor(order: Int): String {
        if (order == column && !answered && started) return "${painted % 10}"
        return results[order]?.toString() ?: ""
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            // Carry out of a column is written above the next order.
            BoardRow(top = top, column = column, cellWidth = cellWidth, op = null) { carryFor(it) }
            BoardRow(top = top, column = column, cellWidth = cellWidth, op = null) { multiplicandFor(it) }
            // Multiplier row: the multiplication sign in its own column.
            BoardRow(top = top, column = column, cellWidth = cellWidth, op = "\u00D7") {
                if (it == 0) "$multiplier" else ""
            }
            // Result line under the whole multiplication.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.onSurface)
            )
            BoardRow(top = top, column = column, cellWidth = cellWidth, op = null) { resultFor(it) }
        }
    }
}

/**
 * One row of the vertical multiplication: the operation cell on the left
 * (the multiplication sign on the multiplier row) followed by one cell per
 * order, right to left, with the column being solved highlighted.
 */
@Composable
private fun BoardRow(
    top: Int,
    column: Int,
    cellWidth: androidx.compose.ui.unit.Dp,
    op: String?,
    content: (Int) -> String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.width(20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (op != null) {
                Text(
                    text = op,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        for (order in top downTo 0) {
            val active = order == column
            Box(
                modifier = Modifier
                    .width(cellWidth)
                    .padding(1.dp)
                    .then(
                        if (active) {
                            Modifier.background(ACTIVE_CELL_BG, RoundedCornerShape(4.dp))
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = content(order),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * The squares of the column. Before the column is solved, every repetition of
 * the group is a dashed box that turns light yellow as the groups are repeated,
 * and the squares carried from the previous order are light blue at the end.
 * Once the column is solved, the squares are regrouped in complete groups of ten
 * (the carry to the next order) and the squares left outside show the digit
 * that stays in the column.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MultSquaresPanel(
    digit: Int,
    multiplier: Int,
    groups: Int,
    incoming: Int,
    answered: Boolean,
    order: String,
    modifier: Modifier = Modifier
) {
    val xs = practicingMultiplicationByOneDigitNumbersStringsForLanguage(LocalAppLanguage.current)
    val product = digit * multiplier
    val total = product + incoming

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = xs.squaresLabel,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (answered) {
                    var index = 0
                    while (index < total) {
                        val size = minOf(10, total - index)
                        val states = (0 until size).map { offset ->
                            if (index + offset < product) SquareState.ADDED else SquareState.BASE
                        }
                        SquareGroup(
                            states = states,
                            borderColor = if (size == 10) TEN_GROUP_BORDER else null
                        )
                        index += size
                    }
                } else {
                    repeat(multiplier) { group ->
                        SquareGroup(
                            states = List(digit) {
                                if (group < groups) SquareState.ADDED else SquareState.PENDING
                            },
                            borderColor = GROUP_BORDER
                        )
                    }
                    if (incoming > 0) {
                        SquareGroup(
                            states = List(incoming) { SquareState.BASE },
                            borderColor = GROUP_BORDER
                        )
                    }
                }
            }
        }

        Text(
            text = if (answered) {
                "$digit \u00D7 $multiplier = $product"
            } else {
                "$digit \u00D7 $groups = ${digit * groups}"
            },
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = if (answered) {
                xs.tenNote.format(total / 10, order, total % 10)
            } else {
                xs.groupsNote.format(groups, multiplier)
            },
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

private enum class SquareState { ADDED, PENDING, BASE }

@Composable
private fun SquareGroup(
    states: List<SquareState>,
    borderColor: Color?
) {
    Box(
        modifier = Modifier
            .then(
                if (borderColor != null) {
                    Modifier.border(2.dp, borderColor, RoundedCornerShape(6.dp))
                } else {
                    Modifier
                }
            )
            // A group of a zero digit has no squares, but it still shows as an
            // empty rectangle, like the web version.
            .then(
                if (states.isEmpty()) {
                    Modifier.size(width = 18.dp, height = 16.dp)
                } else {
                    Modifier
                }
            )
            .padding(2.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            states.forEach { state ->
                val color = when (state) {
                    SquareState.ADDED -> SQUARE_ADDED
                    SquareState.PENDING -> SQUARE_PENDING
                    SquareState.BASE -> SQUARE_BASE
                }
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color, RoundedCornerShape(3.dp))
                        .border(1.5.dp, SQUARE_BORDER, RoundedCornerShape(3.dp))
                )
            }
        }
    }
}

@Composable
private fun RepeatButton(
    up: Boolean,
    enabled: Boolean,
    description: String,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(48.dp)
            .semantics { contentDescription = description },
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(0xFFEAF3FB),
            contentColor = GROUP_BORDER,
            disabledContainerColor = Color(0xFFEAF3FB).copy(alpha = 0.5f),
            disabledContentColor = GROUP_BORDER.copy(alpha = 0.5f)
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = if (up) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun PracticingMultiplicationSourcesMenu(
    sources: List<HTSource>,
    modifier: Modifier = Modifier
) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var showSourcesMenu by remember { mutableStateOf(false) }
    var activeSource by remember { mutableStateOf<HTSource?>(null) }

    Box(
        modifier = modifier.padding(bottom = 8.dp, start = 8.dp)
    ) {
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
            expanded = showSourcesMenu && activeSource == null,
            onDismissRequest = { showSourcesMenu = false }
        ) {
            sources.forEach { source ->
                DropdownMenuItem(
                    text = { Text(source.text) },
                    trailingIcon = {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    },
                    onClick = { activeSource = source }
                )
            }
        }

        DropdownMenu(
            expanded = showSourcesMenu && activeSource != null,
            onDismissRequest = {
                activeSource = null
                showSourcesMenu = false
            }
        ) {
            activeSource?.let { source ->
                val url = if (source.page.startsWith("index.html")) {
                    HISTORYTRACERS_ORIGIN + source.page
                } else {
                    source.page
                }
                DropdownMenuItem(
                    text = { Text(s.common.copyUrl) },
                    onClick = {
                        showSourcesMenu = false
                        activeSource = null
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", url))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        activeSource = null
                        safeOpenUri(uriHandler, url)
                    }
                )
            }
        }
    }
}
