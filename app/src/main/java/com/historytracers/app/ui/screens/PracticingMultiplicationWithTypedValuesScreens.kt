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
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.PracticingMultiplicationWithTypedValuesStrings
import com.historytracers.app.ui.features.miscStringsForLanguage
import com.historytracers.app.ui.features.practicingMultiplicationWithTypedValuesStringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameFile
import kotlin.random.Random

// Ports the web class "Practicing multiplication with typed values"
// (f5a96471-94f4-4cf2-80c4-47be458c28ea). Two screens: a rules screen and a
// game. It is the same vertical multiplication as "Practicing multiplication by
// one-digit numbers", but instead of repeating the groups of squares with the
// buttons the reader types the digit that the column keeps in the result row
// and, when the column reaches ten or more, the number of complete groups of ten
// in a box above the next order. The values are checked as they are typed: a
// wrong value colours the squares and shows a tip, the right values fill the
// squares, show a congratulation and the button to the next column.
private const val SMARTPHONE_GAME_FILE = "f5a96471-94f4-4cf2-80c4-47be458c28ea"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"
private const val RULES_ID = "e7ba1890-f25b-4bde-89c3-953bf69c6c13"
private const val GAME_ID = "2be1fa60-ca02-4fea-9c85-5975bd4154c9"

private const val TYPED_VALUES_SECTION_ID = "practicing_multiplication_with_typed_values"

// Levels: 0 = units (one digit), 1 = tens (two digits), 2 = hundreds (three).
private const val MAX_LEVEL = 2

private val SQUARE_ADDED = Color(0xFFFFFFE0)
private val SQUARE_PENDING = Color.White
private val SQUARE_BASE = Color(0xFFADD8E6)
private val SQUARE_BASE_WRITTEN = Color(0xFF00008B)
private val SQUARE_BORDER = Color(0xFF333333)
private val TEN_GROUP_BORDER = Color(0xFFC0392B)
private val ACTIVE_CELL_BG = Color(0xFFADD8E6)
private val INPUT_BG = Color(0xFFEAF3FB)
private val INPUT_BORDER = Color(0xFF2C5F8A)

// The input background is always pale, so the typed digits need a fixed dark
// colour instead of the theme-dependent onSurface (light in a dark theme).
private val INPUT_TEXT = Color(0xFF1A2733)

private val ORDER_CHIP_BG = Color(0xFFF4F4F4)
private val ORDER_CHIP_BORDER = Color(0xFFBBBBBB)
private val ORDER_CHIP_TEXT = Color(0xFF555555)
private val ORDER_CHIP_ACTIVE = Color(0xFF2C5F8A)
private val LEVEL_BADGE_BG = Color(0xFFEAF3FB)

private fun levelName(level: Int, xs: PracticingMultiplicationWithTypedValuesStrings): String =
    when (level) {
        0 -> xs.levelOneDigit
        1 -> xs.levelTwoDigits
        else -> xs.levelThreeDigits
    }

internal fun typedOrderName(order: Int, xs: PracticingMultiplicationWithTypedValuesStrings): String =
    when (order) {
        0 -> xs.units
        1 -> xs.tens
        else -> xs.hundreds
    }

internal fun typedDigitAt(value: Int, order: Int): Int {
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
fun PracticingMultiplicationWithTypedValuesRulesScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = practicingMultiplicationWithTypedValuesStringsForLanguage(LocalAppLanguage.current)
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
                        TypedValuesSourcesMenu(
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
fun PracticingMultiplicationWithTypedValuesGameScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToMultiplicationTableHub: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val mcs = miscStringsForLanguage(LocalAppLanguage.current)
    val xs = practicingMultiplicationWithTypedValuesStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }
    val focusManager = LocalFocusManager.current

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
    var digitText by remember { mutableStateOf("") }
    var carryText by remember { mutableStateOf("") }
    var answered by remember { mutableStateOf(false) }
    var lastCarry by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("") }
    var medalShown by remember { mutableStateOf(false) }

    val digits = level + 1
    val digit = typedDigitAt(multiplicand, column)
    val product = digit * multiplier
    val total = product + incoming
    val carryNeeded = total >= 10
    val written = digitText.toIntOrNull() ?: 0

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
        digitText = ""
        carryText = ""
        feedback = ""
        medalShown = false
        focusManager.clearFocus()
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
        val carryOut = total / 10
        val order = typedOrderName(column, xs)
        carries = carries + (column to carryOut)
        results = results + (column to (total % 10))
        lastCarry = carryOut
        focusManager.clearFocus()

        val explanation = when {
            incoming > 0 && carryOut > 0 -> xs.msgCarryIn
            incoming > 0 -> xs.msgNoCarryIn
            carryOut > 0 -> xs.msgCarry
            else -> xs.msgNoCarry
        }.format(digit, multiplier, product, incoming, total, total % 10, carryOut, order)

        if (column >= digits - 1) {
            // The whole multiplication is done: a carry out of the last column
            // creates a new order on the left.
            if (carryOut > 0) {
                results = results + (column + 1 to carryOut)
            }
            feedback = xs.perfect.format(multiplicand, multiplier, multiplicand * multiplier) +
                "\n" + explanation
            medalShown = true
        } else {
            feedback = xs.congrats + "\n" + explanation
        }
        award(1)
    }

    // The values are checked as they are typed.
    fun evaluate(newDigit: String, newCarry: String) {
        if (answered) return
        val digitValue = newDigit.toIntOrNull()
        val carryValue = newCarry.toIntOrNull()
        val correctDigit = total % 10
        val correctCarry = total / 10
        val digitOk = newDigit.isNotEmpty() && digitValue == correctDigit
        val carryOk = !carryNeeded || (newCarry.isNotEmpty() && carryValue == correctCarry)
        val order = typedOrderName(column, xs)

        if (newDigit.isNotEmpty() && !digitOk) {
            feedback = when {
                carryNeeded && incoming > 0 -> xs.wrongAboveIn
                carryNeeded -> xs.wrongAbove
                incoming > 0 -> xs.wrongBelowIn
                else -> xs.wrongBelow
            }.format(digit, multiplier, product, incoming, total, total % 10, total / 10, order)
            medalShown = false
            return
        }
        if (carryNeeded && newCarry.isNotEmpty() && !carryOk) {
            feedback = xs.wrongCarry.format(
                digit, multiplier, product, incoming, total
            )
            medalShown = false
            return
        }
        if (digitOk && carryOk) {
            resolve()
        } else {
            feedback = ""
        }
    }

    val multiplicationComplete = answered && column >= digits - 1
    LaunchedEffect(multiplicationComplete) {
        if (multiplicationComplete) {
            preferences.markMultiplicationTableSectionCompleted(TYPED_VALUES_SECTION_ID)
        }
    }

    // The game area below is the flexible part of the page: it scrolls when its
    // content (board, squares and messages) grows with the number of digits, the
    // carried value or the font scale, and the next step and the navigation row
    // stay pinned underneath, so nothing is ever lost to a short window.
    val top = run {
        val last = digits - 1
        if (column == last && ((!answered && carryNeeded) || (answered && (carries[last] ?: 0) > 0))) {
            last + 1
        } else {
            last
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
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // The game area is the flexible part of the page: it scrolls
                    // only when its content (which grows with the number of
                    // digits, the carried value and the font scale) is taller
                    // than the space the navigation row leaves. Nothing is ever
                    // clipped, and the row stays reachable at every height.
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // The introduction is part of the scrolling game area, so
                        // the board, the squares and the messages keep their
                        // natural size.
                        val intro: @Composable () -> Unit = {
                            content.text?.forEach { text ->
                                if (text == null) return@forEach
                                MarkdownText(text = text.text ?: "")
                            }
                        }
                        intro()

                        Spacer(Modifier.height(4.dp))

                        TypedLevelRow(level = level, digits = digits, activeColumn = column)

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = xs.question.format(typedOrderName(column, xs), digit, multiplier),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        if (incoming > 0) {
                            Text(
                                text = xs.incomingNote.format(incoming, digit, multiplier),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        TypedBoard(
                            top = top,
                            digits = digits,
                            multiplicand = multiplicand,
                            multiplier = multiplier,
                            column = column,
                            carries = carries,
                            results = results,
                            pending = !answered,
                            carryNeeded = carryNeeded,
                            digitText = digitText,
                            carryText = carryText,
                            onDigitChange = { raw ->
                                val clean = raw.filter { it.isDigit() }.take(1)
                                digitText = clean
                                evaluate(clean, carryText)
                            },
                            onCarryChange = { raw ->
                                val clean = raw.filter { it.isDigit() }.take(1)
                                carryText = clean
                                evaluate(digitText, clean)
                            }
                        )

                        Spacer(Modifier.height(6.dp))

                        TypedSquaresPanel(
                            digit = digit,
                            multiplier = multiplier,
                            incoming = incoming,
                            written = written,
                            carryNeeded = carryNeeded,
                            answered = answered,
                            order = typedOrderName(column, xs),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Message area: a tip for a wrong value, or the
                        // congratulation with the explanation of the column.
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 116.dp)
                                .verticalScroll(rememberScrollState()),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            if (feedback.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.Top) {
                                    if (medalShown) {
                                        Text(text = xs.medal, fontSize = 22.sp)
                                        Spacer(Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = feedback,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (medalShown || answered) {
                                            Color(0xFF2E7D32)
                                        } else {
                                            Color(0xFFB45309)
                                        },
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                }
                            }
                        }

                    }

                    Spacer(Modifier.height(6.dp))

                    // The next step is pinned below the scrolling game area,
                    // together with the navigation row, so it stays visible even
                    // when the column message is long.
                    if (answered) {
                        FilledTonalButton(
                            onClick = {
                                if (column >= digits - 1) {
                                    newProblem()
                                } else {
                                    column += 1
                                    incoming = lastCarry
                                    settleColumn()
                                }
                            },
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
                        Spacer(Modifier.height(6.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                    ) {
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
                TypedValuesSourcesMenu(
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
internal fun TypedLevelRow(
    level: Int,
    digits: Int,
    activeColumn: Int
) {
    val xs = practicingMultiplicationWithTypedValuesStringsForLanguage(LocalAppLanguage.current)

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
                    text = typedOrderName(order, xs),
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
 * The vertical multiplication with the value fields inside it: the digit that
 * the column keeps is typed in the result row of the column being solved, and
 * the carry is typed in the carry row above the next order — exactly where they
 * are written by hand.
 */
@Composable
internal fun TypedBoard(
    top: Int,
    digits: Int,
    multiplicand: Int,
    multiplier: Int,
    column: Int,
    carries: Map<Int, Int>,
    results: Map<Int, Int>,
    pending: Boolean,
    carryNeeded: Boolean,
    digitText: String,
    carryText: String,
    onDigitChange: (String) -> Unit,
    onCarryChange: (String) -> Unit
) {
    val xs = practicingMultiplicationWithTypedValuesStringsForLanguage(LocalAppLanguage.current)
    val cellWidth = 28.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.width(IntrinsicSize.Max)) {
            // Carry row: a solved carry, or the field for the carry of the
            // column being solved (above the next order) when it reaches ten.
            BoardRow(top = top, column = column, cellWidth = cellWidth, op = null) { order ->
                val typedCarry = pending && carryNeeded && order == column + 1
                when {
                    typedCarry -> CellInput(
                        value = carryText,
                        onValueChange = onCarryChange,
                        description = xs.carryBox
                    )
                    order >= 1 && (carries[order - 1] ?: 0) > 0 -> CellText("${carries[order - 1]}")
                }
            }
            // Number being multiplied.
            BoardRow(top = top, column = column, cellWidth = cellWidth, op = null) { order ->
                if (order <= digits - 1) CellText("${typedDigitAt(multiplicand, order)}")
            }
            // One-digit multiplier, with the multiplication sign in its column.
            BoardRow(top = top, column = column, cellWidth = cellWidth, op = "\u00D7") { order ->
                if (order == 0) CellText("$multiplier")
            }
            // Result line.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.onSurface)
            )
            // Result row: the digit kept by the column is typed here.
            BoardRow(top = top, column = column, cellWidth = cellWidth, op = null) { order ->
                when {
                    pending && order == column -> CellInput(
                        value = digitText,
                        onValueChange = onDigitChange,
                        description = xs.digitBox
                    )
                    results[order] != null -> CellText("${results[order]}")
                }
            }
        }
    }
}

@Composable
private fun CellText(value: String) {
    if (value.isEmpty()) return
    Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}

/**
 * One row of the vertical multiplication. The operation cell is drawn on the
 * left (the multiplication sign on the multiplier row) and [content] fills one
 * cell per order, right to left, with the column being solved highlighted.
 */
@Composable
private fun BoardRow(
    top: Int,
    column: Int,
    cellWidth: androidx.compose.ui.unit.Dp,
    op: String?,
    content: @Composable (Int) -> Unit
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
                content(order)
            }
        }
    }
}

/**
 * The value field used inside the board (one digit).
 */
@Composable
private fun CellInput(
    value: String,
    onValueChange: (String) -> Unit,
    description: String
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = INPUT_TEXT
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .size(width = 24.dp, height = 30.dp)
            .background(INPUT_BG, RoundedCornerShape(4.dp))
            .border(1.5.dp, INPUT_BORDER, RoundedCornerShape(4.dp))
            .semantics { contentDescription = description },
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                innerTextField()
            }
        }
    )
}

/**
 * The squares of the column: the incoming carry squares are light blue at the
 * end, the product stays neutral until the column is solved (when it turns
 * light yellow) and the squares matching the value being typed are marked as
 * well. Complete groups of ten are outlined because they are the carry.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TypedSquaresPanel(
    digit: Int,
    multiplier: Int,
    incoming: Int,
    written: Int,
    carryNeeded: Boolean,
    answered: Boolean,
    order: String,
    modifier: Modifier = Modifier
) {
    val xs = practicingMultiplicationWithTypedValuesStringsForLanguage(LocalAppLanguage.current)
    val product = digit * multiplier
    val total = product + incoming
    val markedStart = if (carryNeeded) (total / 10) * 10 else 0

    fun stateOf(index: Int): TypedSquareState {
        val base = index >= product
        val marked = !answered && written > 0 &&
            index >= markedStart && index < markedStart + written
        return when {
            base && marked -> TypedSquareState.BASE_WRITTEN
            base -> TypedSquareState.BASE
            !answered && marked -> TypedSquareState.WRITTEN
            answered -> TypedSquareState.ADDED
            else -> TypedSquareState.PENDING
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
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
                var index = 0
                if (total >= 10) {
                    val groups = total / 10
                    repeat(groups) {
                        TypedSquareGroup(
                            states = (0 until 10).map { stateOf(index + it) },
                            borderColor = TEN_GROUP_BORDER
                        )
                        index += 10
                    }
                }
                if (index < total) {
                    TypedSquareGroup(
                        states = (index until total).map { stateOf(it) },
                        borderColor = null
                    )
                }
            }
        }

        Text(
            text = if (answered) {
                "$digit \u00D7 $multiplier = $product"
            } else {
                "$digit \u00D7 $multiplier = ?"
            },
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = if (answered) {
                xs.tenNote.format(total / 10, order, total % 10)
            } else if (incoming > 0) {
                xs.noteIncoming.format(digit, multiplier, product, incoming)
            } else {
                xs.note.format(digit, multiplier, product)
            },
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

private enum class TypedSquareState { ADDED, PENDING, BASE, WRITTEN, BASE_WRITTEN }

@Composable
private fun TypedSquareGroup(
    states: List<TypedSquareState>,
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
            .padding(2.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            states.forEach { state ->
                val color = when (state) {
                    TypedSquareState.ADDED -> SQUARE_ADDED
                    TypedSquareState.PENDING -> SQUARE_PENDING
                    TypedSquareState.BASE -> SQUARE_BASE
                    TypedSquareState.WRITTEN -> SQUARE_ADDED
                    TypedSquareState.BASE_WRITTEN -> SQUARE_BASE_WRITTEN
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
internal fun TypedValuesSourcesMenu(
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
