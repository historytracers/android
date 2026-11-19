// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
import com.historytracers.app.ui.features.miscStringsForLanguage
import com.historytracers.app.ui.features.practicingMultiplicationWithChosenNumbersStringsForLanguage
import com.historytracers.app.ui.features.practicingMultiplicationWithTypedValuesStringsForLanguage
import com.historytracers.common.SMGameFile

// Ports the same class as "Practicing multiplication with typed values"
// (f5a96471-94f4-4cf2-80c4-47be458c28ea), with one difference: the reader
// chooses the number to be multiplied and the one-digit number instead of
// receiving a random multiplication. The multiplication is written vertically
// and solved column by column: the reader types the digit that the column keeps
// in the result row and, when the column reaches ten or more, the number of
// complete groups of ten in a box above the next order. The values are checked
// as they are typed, and the squares show the product and the carried squares.
private const val SMARTPHONE_GAME_FILE = "b9ab21c5-dcf4-4130-81e1-f33f3c9bbb04"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"
private const val RULES_ID = "e6fe2291-ddd3-453e-bb76-c8c377a02ebb"
private const val GAME_ID = "4029d523-a3a8-4c56-b7b0-343bb41e9913"

private const val CHOSEN_NUMBERS_SECTION_ID = "practicing_multiplication_with_chosen_numbers"

private val INPUT_BG = Color(0xFFEAF3FB)
private val INPUT_BORDER = Color(0xFF2C5F8A)

// The field background is always pale, so the digits need a fixed dark colour
// instead of the theme-dependent onSurface (which turns light in a dark theme).
private val INPUT_TEXT = Color(0xFF1A2733)

private fun chosenDigits(multiplicand: Int): Int = when {
    multiplicand >= 100 -> 3
    multiplicand >= 10 -> 2
    else -> 1
}

@Composable
fun PracticingMultiplicationWithChosenNumbersRulesScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val cs = practicingMultiplicationWithChosenNumbersStringsForLanguage(LocalAppLanguage.current)
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
                    text = cs.rulesTitle,
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
fun PracticingMultiplicationWithChosenNumbersGameScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToMultiplicationTableHub: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val mcs = miscStringsForLanguage(LocalAppLanguage.current)
    // The game itself shares the labels of the typed-values class.
    val xs = practicingMultiplicationWithTypedValuesStringsForLanguage(LocalAppLanguage.current)
    val cs = practicingMultiplicationWithChosenNumbersStringsForLanguage(LocalAppLanguage.current)
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

    // The numbers are chosen by the reader; these default values just give the
    // board something to show on the first visit.
    var multiplicandText by remember { mutableStateOf("12") }
    var multiplierText by remember { mutableStateOf("3") }
    var multiplicand by remember { mutableIntStateOf(12) }
    var multiplier by remember { mutableIntStateOf(3) }

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

    val digits = chosenDigits(multiplicand)
    val level = digits - 1
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

    fun resetProduct() {
        column = 0
        incoming = 0
        carries = emptyMap()
        results = emptyMap()
        digitText = ""
        carryText = ""
        answered = false
        lastCarry = 0
        feedback = ""
        medalShown = false
    }

    fun chooseNumber(raw: String) {
        val clean = raw.filter { it.isDigit() }.take(3)
        multiplicandText = clean
        val value = clean.toIntOrNull() ?: return
        multiplicand = value
        resetProduct()
    }

    fun chooseMultiplier(raw: String) {
        val clean = raw.filter { it.isDigit() }.take(1)
        multiplierText = clean
        val value = clean.toIntOrNull() ?: return
        multiplier = value
        resetProduct()
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
            preferences.markMultiplicationTableSectionCompleted(CHOSEN_NUMBERS_SECTION_ID)
        }
    }

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
                    text = cs.gameTitle,
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
                        // The introduction is part of the scrolling page, so the
                        // fields, the board and the messages keep their natural size.
                        val intro: @Composable () -> Unit = {
                            content.text?.forEach { text ->
                                if (text == null) return@forEach
                                MarkdownText(text = text.text ?: "")
                            }
                        }
                        intro()

                        Spacer(Modifier.height(4.dp))

                        ChosenNumbersRow(
                            multiplicandText = multiplicandText,
                            multiplierText = multiplierText,
                            onMultiplicandChange = ::chooseNumber,
                            onMultiplierChange = ::chooseMultiplier
                        )

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
                                    // The numbers are chosen by the reader, so
                                    // a new problem repeats them.
                                    resetProduct()
                                } else {
                                    column += 1
                                    incoming = lastCarry
                                    digitText = ""
                                    carryText = ""
                                    answered = false
                                    feedback = ""
                                    medalShown = false
                                    focusManager.clearFocus()
                                }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                if (column >= digits - 1) cs.restart else xs.nextColumn,
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
 * The two fields where the reader chooses the multiplication: the number to be
 * multiplied (one, two or three digits) and the one-digit number.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChosenNumbersRow(
    multiplicandText: String,
    multiplierText: String,
    onMultiplicandChange: (String) -> Unit,
    onMultiplierChange: (String) -> Unit
) {
    val cs = practicingMultiplicationWithChosenNumbersStringsForLanguage(LocalAppLanguage.current)

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = cs.numberLabel,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ChosenNumberField(
            value = multiplicandText,
            onValueChange = onMultiplicandChange,
            description = cs.numberField,
            width = 56.dp
        )
        Text(
            text = "\u00D7",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        ChosenNumberField(
            value = multiplierText,
            onValueChange = onMultiplierChange,
            description = cs.multiplierField,
            width = 32.dp
        )
    }
}

@Composable
private fun ChosenNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    description: String,
    width: Dp
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = INPUT_TEXT
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .width(width)
            .height(34.dp)
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
