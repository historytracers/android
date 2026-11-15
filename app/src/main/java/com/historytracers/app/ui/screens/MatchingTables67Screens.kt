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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
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
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.miscStringsForLanguage
import com.historytracers.app.ui.features.matchingTables67StringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile
import kotlinx.coroutines.launch
import kotlin.random.Random

// Ports the web class "Matching the Tables of 6 and 7"
// (eaa3c957-8eb2-4d09-83be-5c1291d140f2). Two screens: a rules screen and a
// two-column matching game. The left column shows an equality like
// 6 x 3 + 3 = 7 x 3 (multiplying by 7 is multiplying by 6 and adding one more
// copy of the multiplier, 7 x N = 6 x N + N), and the right column shows its
// result together with groups of characters drawn as rectangles with seven
// characters each, so the picture matches the 7 x N side of the equality.
// Level 1 covers the first half of the table of 7 (results 7 to 35), level 2
// the second half (results 42 to 70) and level 3 mixes both halves.
private const val SMARTPHONE_GAME_FILE = "eaa3c957-8eb2-4d09-83be-5c1291d140f2"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"
private const val RULES_ID = "489463f6-cd86-4118-b239-eafca2a1f081"
private const val GAME_ID = "8e215a93-4e04-43fe-8159-a28e3182f507"

private const val MATCHING_TABLES_67_SECTION_ID = "matching_tables_6_7"

private const val MAX_LEVEL = 3
// Every rectangle holds seven characters, exactly like the 7 x N side of the
// equality.
private const val CHARACTERS_PER_GROUP = 7
// Selector value for the default behavior: a random object from
// MultiplicationTableObjects in every new exercise.
private const val RANDOM_OBJECT = -1

private val GROUP_BTN_BG = Color(0xFFFFF8E7)
private val GROUP_BTN_BORDER = Color(0xFF5A3F2C)
private val GROUP_BTN_TEXT = Color(0xFF2E241F)
private val GROUP_BTN_SELECTED_BG = Color(0xFF4A90D9)
private val GROUP_BTN_SELECTED_BORDER = Color(0xFF2C5F8F)
private val GROUP_BTN_MATCHED_BG = Color(0xFFD4EDDA)
private val GROUP_BTN_MATCHED_BORDER = Color(0xFF155724)
private val GROUP_BOX_BORDER = Color(0xFF2C5F8A)
private val GROUP_BOX_BG = Color(0xFFEAF3FB)
private val GROUP_FEEDBACK = Color(0xFFB45309)
private val LEVEL_BADGE_BG = Color(0xFFFFB347)

// One equality of a level: 6 x n + n = 7 x n.
private data class MatchingTables67Pair(val n: Int) {
    val result: Int get() = 7 * n
    val expression: String get() = "6 \u00D7 $n + $n = 7 \u00D7 $n"
}

// Level 1 practices the first half of the table of 7 (results 7 to 35),
// level 2 the second half (results 42 to 70) and level 3 mixes one equality
// from each half, so the whole table up to 7 x 10 = 70 is covered.
private fun buildPairs(level: Int): List<MatchingTables67Pair> = when (level) {
    1 -> listOf(1, 2, 3, 4, 5).shuffled(Random).take(2)
    2 -> listOf(6, 7, 8, 9, 10).shuffled(Random).take(2)
    else -> listOf(
        listOf(1, 2, 3, 4, 5).random(Random),
        listOf(6, 7, 8, 9, 10).random(Random)
    )
}.map { n -> MatchingTables67Pair(n) }

@Composable
fun MatchingTables67RulesScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = matchingTables67StringsForLanguage(LocalAppLanguage.current)
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
                    // Per-screen narration is not published for this class
                    // yet, so the button plays the class-level audio.
                    lessonId = SMARTPHONE_GAME_FILE,
                    contentId = null
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
                        MatchingTables67SourcesMenu(
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
fun MatchingTables67GameScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToMultiplicationTableHub: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val mcs = miscStringsForLanguage(LocalAppLanguage.current)
    val xs = matchingTables67StringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

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

    var level by remember { mutableIntStateOf(1) }
    var pairs by remember { mutableStateOf<List<MatchingTables67Pair>>(emptyList()) }
    var icon by remember { mutableStateOf(MultiplicationTableObjects.first()) }
    var objectIndex by remember { mutableIntStateOf(RANDOM_OBJECT) }
    var showObjectMenu by remember { mutableStateOf(false) }
    var leftOrder by remember { mutableStateOf<List<Int>>(emptyList()) }
    var rightOrder by remember { mutableStateOf<List<Int>>(emptyList()) }
    var selectedLeft by remember { mutableStateOf<Int?>(null) }
    var selectedRight by remember { mutableStateOf<Int?>(null) }
    var matchedIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var levelDone by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf("") }
    var sectionMarked by remember { mutableStateOf(false) }

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    fun award(points: Int) {
        if (points <= 0) return
        totalAwarded += points
        onScoreChanged(initialScore + totalAwarded)
    }

    fun buildExercise() {
        pairs = buildPairs(level)
        icon = if (objectIndex in MultiplicationTableObjects.indices) {
            MultiplicationTableObjects[objectIndex]
        } else {
            MultiplicationTableObjects.random(Random)
        }
        leftOrder = pairs.indices.shuffled(Random)
        rightOrder = pairs.indices.shuffled(Random)
        selectedLeft = null
        selectedRight = null
        matchedIds = emptySet()
        levelDone = false
        feedback = ""
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

    LaunchedEffect(level) {
        buildExercise()
    }

    fun select(side: String, id: Int) {
        if (id in matchedIds) return
        feedback = ""
        if (side == "left") selectedLeft = id else selectedRight = id

        val leftId = selectedLeft
        val rightId = selectedRight
        if (leftId == null || rightId == null) return

        if (leftId == rightId) {
            matchedIds = matchedIds + leftId
            selectedLeft = null
            selectedRight = null
            award(1)
            if (matchedIds.size == pairs.size) {
                levelDone = true
                // Completing a level lets the class button outside change colour.
                if (!sectionMarked) {
                    sectionMarked = true
                    scope.launch {
                        preferences.markMultiplicationTableSectionCompleted(
                            MATCHING_TABLES_67_SECTION_ID
                        )
                    }
                }
            }
        } else {
            feedback = xs.wrongPairMessage.format(
                pairs[rightId].result,
                pairs[leftId].expression
            )
            selectedLeft = null
            selectedRight = null
        }
    }

    fun nextLevel() {
        level = if (level >= MAX_LEVEL) 1 else level + 1
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
                    // Per-screen narration is not published for this class
                    // yet, so the button plays the class-level audio.
                    lessonId = SMARTPHONE_GAME_FILE,
                    contentId = null
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
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    content.text?.forEach { text ->
                        if (text == null) return@forEach
                        MarkdownText(text = text.text ?: "")
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(40.dp),
                            color = LEVEL_BADGE_BG
                        ) {
                            Text(
                                text = xs.levelFormat.format(level, MAX_LEVEL),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = GROUP_BTN_TEXT,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        // Drawing-object selector: a random object in every new
                        // exercise or one specific object from the multiplication
                        // tables.
                        Text(
                            text = xs.drawLabel,
                            fontSize = 20.sp
                        )
                        Box {
                            FilledTonalButton(
                                onClick = { showObjectMenu = true },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFFEAF3FB),
                                    contentColor = GROUP_BTN_TEXT
                                )
                            ) {
                                Text(
                                    text = if (objectIndex in MultiplicationTableObjects.indices) {
                                        MultiplicationTableObjects[objectIndex]
                                    } else {
                                        xs.randomObject
                                    },
                                    fontSize = 16.sp
                                )
                            }
                            DropdownMenu(
                                expanded = showObjectMenu,
                                onDismissRequest = { showObjectMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(xs.randomObject) },
                                    onClick = {
                                        objectIndex = RANDOM_OBJECT
                                        showObjectMenu = false
                                    }
                                )
                                MultiplicationTableObjects.forEachIndexed { index, emoji ->
                                    DropdownMenuItem(
                                        text = { Text(emoji, fontSize = 20.sp) },
                                        onClick = {
                                            objectIndex = index
                                            // Redraw the groups right away, keeping
                                            // the pairs and the matched buttons of
                                            // the level.
                                            icon = emoji
                                            showObjectMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // The board fills whatever height is left: the drawings inside
                    // each result shrink or grow so every group fits on screen and
                    // the buttons below never move out of view.
                    MatchingTables67Board(
                        pairs = pairs,
                        icon = icon,
                        leftOrder = leftOrder,
                        rightOrder = rightOrder,
                        selectedLeft = selectedLeft,
                        selectedRight = selectedRight,
                        matchedIds = matchedIds,
                        onSelect = ::select,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    // Fixed message area: feedback or congratulations, so the
                    // buttons keep their place while messages appear.
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val message = when {
                            levelDone && level >= MAX_LEVEL -> xs.perfectMessage
                            levelDone -> xs.correctMessage
                            else -> feedback
                        }
                        if (message.isNotEmpty()) {
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (levelDone) Color(0xFF2E7D32) else GROUP_FEEDBACK,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilledTonalButton(
                            onClick = { buildExercise() },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(s.common.newExercise, fontWeight = FontWeight.Bold)
                        }
                        if (levelDone) {
                            FilledTonalButton(
                                onClick = { nextLevel() },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFF4CAF50),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    if (level >= MAX_LEVEL) xs.playAgain else s.common.nextLevel,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
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
                }
            }

            content?.sourceMenu?.takeIf { it.isNotEmpty() }?.let { sources ->
                MatchingTables67SourcesMenu(
                    sources = sources,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun MatchingTables67Board(
    pairs: List<MatchingTables67Pair>,
    icon: String,
    leftOrder: List<Int>,
    rightOrder: List<Int>,
    selectedLeft: Int?,
    selectedRight: Int?,
    matchedIds: Set<Int>,
    onSelect: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val xs = matchingTables67StringsForLanguage(LocalAppLanguage.current)

    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            leftOrder.forEach { id ->
                val matched = id in matchedIds
                val selected = selectedLeft == id
                val container = when {
                    matched -> GROUP_BTN_MATCHED_BG
                    selected -> GROUP_BTN_SELECTED_BG
                    else -> GROUP_BTN_BG
                }
                val borderColor = when {
                    matched -> GROUP_BTN_MATCHED_BORDER
                    selected -> GROUP_BTN_SELECTED_BORDER
                    else -> GROUP_BTN_BORDER
                }
                val contentColor = if (selected) Color.White else GROUP_BTN_TEXT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(container)
                        .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                        .clickable(enabled = !matched) { onSelect("left", id) }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pairs[id].expression,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rightOrder.forEach { id ->
                val pair = pairs[id]
                val matched = id in matchedIds
                val selected = selectedRight == id
                val container = when {
                    matched -> GROUP_BTN_MATCHED_BG
                    selected -> GROUP_BTN_SELECTED_BG
                    else -> GROUP_BTN_BG
                }
                val borderColor = when {
                    matched -> GROUP_BTN_MATCHED_BORDER
                    selected -> GROUP_BTN_SELECTED_BORDER
                    else -> GROUP_BTN_BORDER
                }
                val contentColor = if (selected) Color.White else GROUP_BTN_TEXT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(container)
                        .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                        .clickable(enabled = !matched) { onSelect("right", id) }
                        // The groups are decorative for sighted users, so the
                        // accessible name must also describe them: "21.
                        // Groups: 3. Characters per group: 7."
                        .semantics {
                            contentDescription =
                                "${pair.result}. ${xs.groupsDescription.format(pair.n, CHARACTERS_PER_GROUP)}"
                        }
                        .padding(6.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // The result keeps its size; only the drawings below it
                        // adapt to the space that is left.
                        Text(
                            text = pair.result.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                        Spacer(Modifier.height(4.dp))
                        MatchingTables67GroupGrid(
                            groups = pair.n,
                            perGroup = CHARACTERS_PER_GROUP,
                            icon = icon,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Draws [groups] rectangles, each holding [perGroup] characters, always fitting
 * the available space: the grid rows share the exact available height through
 * weights and the character size is computed from the remaining space (keeping
 * room for the box padding and borders), so the drawings never clip their last
 * line nor push the screen's buttons out of view.
 */
@Composable
private fun MatchingTables67GroupGrid(
    groups: Int,
    perGroup: Int,
    icon: String,
    modifier: Modifier = Modifier
) {
    if (groups <= 0 || perGroup <= 0) return

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val gap = 3.dp
        val padding = 2.dp
        val border = 1.dp
        val charSpacing = 0.5.dp
        val slack = 1.dp

        // Pick the number of group rows that allows the biggest characters.
        var bestEmoji = 0.dp
        var bestRows = 1
        var bestCols = groups
        for (rows in 1..groups) {
            val cols = (groups + rows - 1) / rows
            val cellWidth = (maxWidth - gap * (cols - 1)) / cols
            val cellHeight = (maxHeight - gap * (rows - 1)) / rows
            // Characters are about 1.15 times their font size tall and wide;
            // keep room for the box padding and its borders on every side.
            val byWidth = (cellWidth - padding * 2 - border * 2 - charSpacing * (perGroup - 1)) /
                (perGroup * 1.15f)
            val byHeight = (cellHeight - padding * 2 - border * 2 - slack) / 1.15f
            val emoji = minOf(byWidth, byHeight)
            if (emoji > bestEmoji) {
                bestEmoji = emoji
                bestRows = rows
                bestCols = cols
            }
        }

        val emojiDp = bestEmoji.coerceIn(4.dp, 14.dp)
        // Size the characters in dp even when the user's font scale is larger,
        // otherwise the fixed-height grid rows would overflow and clip their
        // last line.
        val density = LocalDensity.current
        val emojiSize = TextUnit(emojiDp.value / density.fontScale, TextUnitType.Sp)
        val boxPadding = if (emojiDp < 8.dp) 0.5.dp else padding
        val corner = if (emojiDp < 8.dp) 3.dp else 6.dp

        // The rows share the exact available height through weights, so the
        // grid as a whole always fits its space.
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(gap)
        ) {
            var index = 0
            repeat(bestRows) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(bestCols) {
                        if (index < groups) {
                            Box(
                                modifier = Modifier
                                    .border(border, GROUP_BOX_BORDER, RoundedCornerShape(corner))
                                    .background(GROUP_BOX_BG, RoundedCornerShape(corner))
                                    .padding(boxPadding)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(charSpacing)) {
                                    repeat(perGroup) {
                                        Text(
                                            text = icon,
                                            fontSize = emojiSize,
                                            lineHeight = emojiSize
                                        )
                                    }
                                }
                            }
                        }
                        index++
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchingTables67SourcesMenu(
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
            onDismissRequest = { activeSource = null; showSourcesMenu = false }
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
