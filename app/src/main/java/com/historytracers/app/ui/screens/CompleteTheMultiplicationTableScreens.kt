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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.AutoSizeTitleText
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.MultiplicationTableObjects
import com.historytracers.app.ui.components.ScreenAudioButton
import com.historytracers.app.ui.components.multiplicationTableFitsOnScreen
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.completeTheMultiplicationTableStringsForLanguage
import com.historytracers.app.ui.features.miscStringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile
import kotlin.random.Random

// Ports the web class "Complete the Multiplication Table"
// (19b00e11-1e70-4e76-808c-673a0cc71af4). Two screens: a rules screen and a
// game. The left side shows the multiplication table of a number chosen in the
// selector (random between 2 and 9 by default), almost fully filled: three
// products are left blank and the reader types them in. Tapping a filled
// product draws that multiplication on the right as groups of the same
// character — the first factor is the number of characters inside each
// rectangle and the second factor is the number of rectangles. An answer is
// checked as soon as it is complete (one digit for one-digit products, two for
// two-digit ones); a right answer reveals the product with its groups, a wrong
// one receives a tip with the range of the expected number.
private const val SMARTPHONE_GAME_FILE = "19b00e11-1e70-4e76-808c-673a0cc71af4"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"
private const val RULES_ID = "394b0a20-92d0-4dc2-93ed-89c0afdb446e"
private const val GAME_ID = "a64a8307-a051-4fa6-9429-da275f6580c1"

private const val COMPLETE_TABLE_SECTION_ID = "complete_the_multiplication_table"

private const val MAX_TABLE = 9
private const val MIN_RANDOM_TABLE = 2
private const val MAX_RANDOM_TABLE = 9
private const val MAX_ROW = 10
private const val BLANKS = 3
private const val RANDOM_TABLE = -1
private const val RANDOM_OBJECT = -1

private val GROUP_BTN_BG = Color(0xFFFFF8E7)
private val GROUP_BTN_BORDER = Color(0xFF5A3F2C)
private val GROUP_BTN_TEXT = Color(0xFF2E241F)
private val GROUP_BOX_BORDER = Color(0xFF2C5F8A)
private val GROUP_BOX_BG = Color(0xFFEAF3FB)
private val GROUP_FEEDBACK = Color(0xFFB45309)

private fun pickTable(choice: Int): Int =
    if (choice in 0..MAX_TABLE) choice else Random.nextInt(MIN_RANDOM_TABLE, MAX_RANDOM_TABLE + 1)

private fun pickBlanks(): Set<Int> =
    (1..MAX_ROW).shuffled(Random).take(BLANKS).toSet()

private fun pickIcon(objectIndex: Int): String =
    if (objectIndex in MultiplicationTableObjects.indices) {
        MultiplicationTableObjects[objectIndex]
    } else {
        MultiplicationTableObjects.random(Random)
    }

@Composable
fun CompleteTheMultiplicationTableRulesScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = completeTheMultiplicationTableStringsForLanguage(LocalAppLanguage.current)
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
                        CompleteTheMultiplicationTableSourcesMenu(
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
fun CompleteTheMultiplicationTableGameScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToMultiplicationTableHub: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val mcs = miscStringsForLanguage(LocalAppLanguage.current)
    val xs = completeTheMultiplicationTableStringsForLanguage(LocalAppLanguage.current)
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

    var tableChoice by remember { mutableIntStateOf(RANDOM_TABLE) }
    var table by remember { mutableIntStateOf(pickTable(RANDOM_TABLE)) }
    var blanks by remember { mutableStateOf(pickBlanks()) }
    var objectIndex by remember { mutableIntStateOf(RANDOM_OBJECT) }
    var icon by remember { mutableStateOf(pickIcon(RANDOM_OBJECT)) }
    var shownRow by remember { mutableStateOf<Int?>(null) }
    var typed by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var solved by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var wrongRow by remember { mutableStateOf<Int?>(null) }
    var feedback by remember { mutableStateOf("") }

    val tableComplete = solved.size >= BLANKS

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

    // Completing the whole table lets the class button outside change colour.
    LaunchedEffect(tableComplete) {
        if (tableComplete) {
            preferences.markMultiplicationTableSectionCompleted(COMPLETE_TABLE_SECTION_ID)
        }
    }

    fun newExercise() {
        table = pickTable(tableChoice)
        blanks = pickBlanks()
        icon = pickIcon(objectIndex)
        shownRow = null
        typed = emptyMap()
        solved = emptySet()
        wrongRow = null
        feedback = ""
    }

    fun submit(row: Int, text: String) {
        val value = text.toIntOrNull() ?: return
        val expected = table * row
        if (value == expected) {
            solved = solved + row
            typed = typed - row
            wrongRow = null
            feedback = ""
            shownRow = row
            focusManager.clearFocus()
            award(1)
        } else {
            wrongRow = row
            feedback = xs.tipMessage.format(
                table * (row - 1),
                table * (row + 1),
                table
            )
        }
    }

    fun onInputChange(row: Int, raw: String) {
        val digits = raw.filter { it.isDigit() }.take(4)
        typed = typed + (row to digits)
        if (wrongRow == row) {
            wrongRow = null
            feedback = ""
        }
        // The answer is checked as soon as it is complete: a one-digit product
        // with its only digit, a two-digit one once both digits are typed.
        if (digits.length >= (table * row).toString().length) {
            submit(row, digits)
        }
    }

    // Short viewports (landscape, split screen) keep a fixed board height and
    // let the page scroll, instead of squeezing the table to nothing.
    val compactLayout = !multiplicationTableFitsOnScreen()
    // The introduction text keeps a bounded height so the table and the buttons
    // never lose their space; the bound grows with the screen.
    val introMaxHeight = (LocalConfiguration.current.screenHeightDp * 0.22f)
        .dp
        .coerceIn(96.dp, 150.dp)

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
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // The introduction keeps a bounded height so the table and
                    // the buttons below always keep their space: on short
                    // screens only this text scrolls.
                    val gameText: @Composable () -> Unit = {
                        content.text?.forEach { text ->
                            if (text == null) return@forEach
                            MarkdownText(text = text.text ?: "")
                        }
                    }
                    if (compactLayout) {
                        gameText()
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = introMaxHeight)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Column { gameText() }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    CompleteTableSelectors(
                        tableChoice = tableChoice,
                        objectIndex = objectIndex,
                        onTableChange = {
                            tableChoice = it
                            newExercise()
                        },
                        onObjectChange = {
                            objectIndex = it
                            icon = pickIcon(it)
                        }
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (compactLayout) {
                                    Modifier.height(320.dp)
                                } else {
                                    Modifier.weight(1f)
                                }
                            ),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        CompleteTableColumn(
                            table = table,
                            blanks = blanks,
                            solved = solved,
                            typed = typed,
                            wrongRow = wrongRow,
                            onValueChange = ::onInputChange,
                            onShowGroups = { row ->
                                shownRow = row
                                // Like the web version, tapping a filled result
                                // clears the answer feedback.
                                feedback = ""
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxHeight()
                        )

                        CompleteGroupsPanel(
                            table = table,
                            row = shownRow,
                            icon = icon,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }

                    // Fixed message area: a tip or the completion message, so
                    // the buttons keep their place while messages appear.
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 52.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val message = if (tableComplete) xs.completeMessage else feedback
                        if (message.isNotEmpty()) {
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (tableComplete) Color(0xFF2E7D32) else GROUP_FEEDBACK,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = { newExercise() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(s.common.newExercise, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(8.dp))

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

                    Spacer(Modifier.height(8.dp))
                }
            }

            content?.sourceMenu?.takeIf { it.isNotEmpty() }?.let { sources ->
                CompleteTheMultiplicationTableSourcesMenu(
                    sources = sources,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompleteTableSelectors(
    tableChoice: Int,
    objectIndex: Int,
    onTableChange: (Int) -> Unit,
    onObjectChange: (Int) -> Unit
) {
    val xs = completeTheMultiplicationTableStringsForLanguage(LocalAppLanguage.current)
    var showTableMenu by remember { mutableStateOf(false) }
    var showObjectMenu by remember { mutableStateOf(false) }

    // The two selectors sit side by side when there is room and wrap into two
    // lines on narrow screens, so they never take more height than needed.
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box {
            SelectorPill(
                text = "${xs.tableLabel} " + if (tableChoice == RANDOM_TABLE) {
                    xs.randomTable
                } else {
                    "$tableChoice"
                },
                onClick = { showTableMenu = true }
            )
            DropdownMenu(
                expanded = showTableMenu,
                onDismissRequest = { showTableMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(xs.randomTable) },
                    onClick = {
                        showTableMenu = false
                        onTableChange(RANDOM_TABLE)
                    }
                )
                for (n in 0..MAX_TABLE) {
                    DropdownMenuItem(
                        text = { Text("$n") },
                        onClick = {
                            showTableMenu = false
                            onTableChange(n)
                        }
                    )
                }
            }
        }

        Box {
            SelectorPill(
                text = "${xs.groupLabel} " + if (objectIndex in MultiplicationTableObjects.indices) {
                    MultiplicationTableObjects[objectIndex]
                } else {
                    xs.randomObject
                },
                onClick = { showObjectMenu = true }
            )
            DropdownMenu(
                expanded = showObjectMenu,
                onDismissRequest = { showObjectMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(xs.randomObject) },
                    onClick = {
                        onObjectChange(RANDOM_OBJECT)
                        showObjectMenu = false
                    }
                )
                MultiplicationTableObjects.forEachIndexed { index, emoji ->
                    DropdownMenuItem(
                        text = { Text(emoji, fontSize = 20.sp) },
                        onClick = {
                            onObjectChange(index)
                            showObjectMenu = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectorPill(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = GROUP_BOX_BG,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = GROUP_BTN_TEXT,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/**
 * The left side of the game: the table of [table] from row 1 to 10. The rows
 * share the available height equally, so the whole table always fits the board
 * without pushing the buttons out of view, and the text size follows the row
 * height. A blank row shows a text input; every other row shows its product,
 * which can be tapped to draw its groups on the right.
 */
@Composable
private fun CompleteTableColumn(
    table: Int,
    blanks: Set<Int>,
    solved: Set<Int>,
    typed: Map<Int, String>,
    wrongRow: Int?,
    onValueChange: (Int, String) -> Unit,
    onShowGroups: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val xs = completeTheMultiplicationTableStringsForLanguage(LocalAppLanguage.current)

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val headerHeight = (maxHeight * 0.1f).coerceIn(16.dp, 34.dp)
        val rowHeight = ((maxHeight - headerHeight) / MAX_ROW).coerceAtLeast(8.dp)
        val textSize = TextUnit(
            (rowHeight.value * 0.5f).coerceIn(7f, 16f) / density.fontScale,
            TextUnitType.Sp
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = xs.tableHeader.format(table),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }

            for (row in 1..MAX_ROW) {
                val expected = table * row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$table \u00D7 $row",
                        fontSize = textSize,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1.2f)
                    )
                    Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(vertical = rowHeight * 0.1f)
                    ) {
                        if (row in solved || row !in blanks) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GROUP_BTN_BG)
                                    .border(1.dp, GROUP_BTN_BORDER, RoundedCornerShape(8.dp))
                                    .clickable { onShowGroups(row) }
                                    .semantics {
                                        contentDescription =
                                            "$table \u00D7 $row = $expected. " +
                                            xs.groupsDescription.format(row, table)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$expected",
                                    fontSize = textSize,
                                    fontWeight = FontWeight.Bold,
                                    color = GROUP_BTN_TEXT
                                )
                            }
                        } else {
                            val wrong = wrongRow == row
                            BasicTextField(
                                value = typed[row] ?: "",
                                onValueChange = { onValueChange(row, it) },
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = textSize,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(
                                        width = 1.dp,
                                        color = if (wrong) {
                                            MaterialTheme.colorScheme.error
                                        } else {
                                            MaterialTheme.colorScheme.outline
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .semantics {
                                        contentDescription = xs.answerDescription.format(
                                            table,
                                            table,
                                            row
                                        )
                                    },
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
                    }
                }
            }
        }
    }
}

/**
 * The right side of the game: the tapped (or just answered) multiplication with
 * its expression above the groups of characters. One rectangle per line keeps
 * the column narrow, and the character size is computed from the space the
 * groups receive, so nothing is clipped.
 */
@Composable
private fun CompleteGroupsPanel(
    table: Int,
    row: Int?,
    icon: String,
    modifier: Modifier = Modifier
) {
    val xs = completeTheMultiplicationTableStringsForLanguage(LocalAppLanguage.current)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (row == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = xs.clickHint,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        } else {
            Text(
                text = "$table \u00D7 $row = ${table * row}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            CompleteGroupStack(
                groups = row,
                perGroup = table,
                icon = icon,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

/**
 * Draws [groups] rectangles stacked vertically, each holding [perGroup]
 * characters. The rows share the exact available height through weights and the
 * character size is computed from the remaining space, so the drawings never
 * clip their last line nor push the screen's buttons out of view.
 */
@Composable
private fun CompleteGroupStack(
    groups: Int,
    perGroup: Int,
    icon: String,
    modifier: Modifier = Modifier
) {
    if (groups <= 0) return
    val xs = completeTheMultiplicationTableStringsForLanguage(LocalAppLanguage.current)

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val density = LocalDensity.current
        val gap = 2.dp
        val padding = 1.dp
        val border = 1.dp
        val cellHeight = (maxHeight - gap * (groups - 1)) / groups
        // Characters (emoji) render slightly wider than their font size, so the
        // width budget keeps a margin for that and for the box borders.
        val byWidth = (maxWidth - padding * 2 - border * 2) /
            (perGroup.coerceAtLeast(1) * 1.4f)
        val byHeight = (cellHeight - padding * 2 - border * 2) / 1.35f
        val charDp = minOf(byWidth, byHeight).coerceIn(2.dp, 15.dp)
        // Size the characters in dp even when the user's font scale is larger,
        // otherwise the fixed-height rows would overflow and clip.
        val charSize = TextUnit(charDp.value / density.fontScale, TextUnitType.Sp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = xs.groupsDescription.format(groups, perGroup) },
            verticalArrangement = Arrangement.spacedBy(gap),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(groups) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            // The table of zero draws one empty rectangle per
                            // group, like the web version, so it keeps a visible
                            // size even without characters.
                            .then(
                                if (perGroup == 0) {
                                    Modifier.size(16.dp)
                                } else {
                                    Modifier
                                }
                            )
                            .border(border, GROUP_BOX_BORDER, RoundedCornerShape(6.dp))
                            .background(GROUP_BOX_BG, RoundedCornerShape(6.dp))
                            .padding(padding)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(0.5.dp)) {
                            repeat(perGroup) {
                                Text(
                                    text = icon,
                                    fontSize = charSize,
                                    lineHeight = charSize
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompleteTheMultiplicationTableSourcesMenu(
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
