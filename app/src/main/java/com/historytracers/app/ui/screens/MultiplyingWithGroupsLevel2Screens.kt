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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.AutoSizeTitleText
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.MultiplicationTableObjects
import com.historytracers.app.ui.components.multiplicationTableFitsOnScreen
import com.historytracers.app.ui.components.ScreenAudioButton
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.miscStringsForLanguage
import com.historytracers.app.ui.features.multiplyingWithGroupsLevel2StringsForLanguage
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlin.random.Random

// Ports the web class "Multiplying with Groups (4 to 7)"
// (0f7102a6-c17d-494e-8e98-c4ab178116fd). Two screens: a rules screen and a
// two-column matching game. The left column shows a multiplication and the
// right column shows its result together with groups of characters. The first
// factor is the number of characters inside each rectangle and the second
// factor is the number of rectangles, so 4 x 6 is drawn as six rectangles with
// four characters. Four levels practice the tables of 4, 5, 6 and 7.
private const val MULTIPLYING_WITH_GROUPS_LEVEL2_SECTION_ID = "multiplying_with_groups_level2"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"
private const val ORIGINAL_CLASS_FILE = "0f7102a6-c17d-494e-8e98-c4ab178116fd"

// Each screen has its own content UUID so the audio follows the standard
// `{lessonUuid}_{contentUuid}_{language}.ogg` pattern.
private const val RULES_CONTENT_ID = "9a7df831-a592-42d8-bf1c-15a9899b05a2"
private const val GAME_CONTENT_ID = "a9577a5c-87df-42c4-8e85-a0f7724d0c97"

private const val MAX_LEVEL = 4
private const val MAX_MULTIPLIER = 10
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

private data class MultGroupLevel2Pair(val first: Int, val second: Int) {
    val result: Int get() = first * second
}

@Composable
fun MultiplyingWithGroupsLevel2RulesScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = multiplyingWithGroupsLevel2StringsForLanguage(LocalAppLanguage.current)

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
                    lessonId = ORIGINAL_CLASS_FILE,
                    contentId = RULES_CONTENT_ID
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MarkdownText(text = xs.rulesText)

                Spacer(Modifier.height(24.dp))

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

            MultiplyingWithGroupsLevel2SourcesMenu(
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
fun MultiplyingWithGroupsLevel2GameScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToMultiplicationTableHub: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val mcs = miscStringsForLanguage(LocalAppLanguage.current)
    val xs = multiplyingWithGroupsLevel2StringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var level by remember { mutableIntStateOf(1) }
    var pairs by remember { mutableStateOf<List<MultGroupLevel2Pair>>(emptyList()) }
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

    fun award(points: Int) {
        if (points <= 0) return
        // Award on top of the latest shared score instead of the snapshot taken
        // when the screen opened, so a score raised meanwhile is never overwritten.
        onScoreChanged(currentScore + points)
    }

    fun buildExercise() {
        // Each level practices one times table: 4, 5, 6 and 7.
        val factors = when (level) {
            1 -> listOf(4, 4)
            2 -> listOf(5, 5)
            3 -> listOf(6, 6)
            else -> listOf(7, 7)
        }
        val multipliers = (1..MAX_MULTIPLIER).toMutableList()
            .also { it.shuffle(Random) }
            .take(factors.size)
        pairs = factors.mapIndexed { i, f -> MultGroupLevel2Pair(f, multipliers[i]) }
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
                        // Finish the write even if the screen leaves composition
                        // right after the last pair (e.g. the user taps the hub
                        // button immediately), so the section is never lost.
                        withContext(NonCancellable) {
                            preferences.markMultiplicationTableSectionCompleted(
                                MULTIPLYING_WITH_GROUPS_LEVEL2_SECTION_ID
                            )
                        }
                    }
                }
            }
        } else {
            feedback = xs.wrongPairMessage.format(
                pairs[rightId].result,
                pairs[leftId].first,
                pairs[leftId].second
            )
            selectedLeft = null
            selectedRight = null
        }
    }

    fun nextLevel() {
        level = if (level >= MAX_LEVEL) 1 else level + 1
    }

    // Short viewports (landscape, split screen) keep a fixed board height and
    // let the page scroll, instead of squeezing the matching cells to nothing.
    val compactLayout = !multiplicationTableFitsOnScreen()

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
                    lessonId = ORIGINAL_CLASS_FILE,
                    contentId = GAME_CONTENT_ID
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (compactLayout) {
                            Modifier.verticalScroll(rememberScrollState())
                        } else {
                            Modifier
                        }
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MarkdownText(text = xs.gameIntro)

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
                    // exercise or one specific object from the multiplication tables.
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
                                        // Redraw the groups right away, keeping the
                                        // pairs and the matched buttons of the level.
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
                MultGroupLevel2Board(
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
                        .then(
                            if (compactLayout) {
                                Modifier.height(240.dp)
                            } else {
                                Modifier.weight(1f)
                            }
                        )
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

            MultiplyingWithGroupsLevel2SourcesMenu(
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
private fun MultGroupLevel2Board(
    pairs: List<MultGroupLevel2Pair>,
    icon: String,
    leftOrder: List<Int>,
    rightOrder: List<Int>,
    selectedLeft: Int?,
    selectedRight: Int?,
    matchedIds: Set<Int>,
    onSelect: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val xs = multiplyingWithGroupsLevel2StringsForLanguage(LocalAppLanguage.current)

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
                        text = "${pairs[id].first} \u00D7 ${pairs[id].second}",
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
                        .semantics {
                            contentDescription = if (pair.first > 0) {
                                "${pair.result}. ${xs.groupsDescription.format(pair.second, pair.first)}"
                            } else {
                                "${pair.result}. ${xs.noGroupsDescription}"
                            }
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
                        GroupGrid(
                            groups = pair.second,
                            perGroup = pair.first,
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
 * the available space: the grid and the character size are computed from the
 * available width and height, so large groups (for example 10 groups of 7) never
 * push the screen's buttons out of view.
 */
@Composable
private fun GroupGrid(
    groups: Int,
    perGroup: Int,
    icon: String,
    modifier: Modifier = Modifier
) {
    if (groups <= 0 || perGroup <= 0) return

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val gap = 3.dp
        val padding = 2.dp

        // Pick the number of group rows that allows the biggest characters.
        var bestEmoji = 0.dp
        var bestRows = 1
        var bestCols = groups
        for (rows in 1..groups) {
            val cols = (groups + rows - 1) / rows
            val cellWidth = (maxWidth - gap * (cols - 1)) / cols
            val cellHeight = (maxHeight - gap * (rows - 1)) / rows
            if (cellWidth <= padding * 2 || cellHeight <= padding * 2) continue
            // Characters are about as wide as 1.15 times their font size.
            val byWidth = (cellWidth - padding * 2) / (perGroup * 1.15f)
            val byHeight = (cellHeight - padding * 2) / 1.15f
            val emoji = minOf(byWidth, byHeight)
            if (emoji > bestEmoji) {
                bestEmoji = emoji
                bestRows = rows
                bestCols = cols
            }
        }

        val emojiDp = bestEmoji.coerceIn(4.dp, 14.dp)
        val emojiSize = with(LocalDensity.current) { emojiDp.toSp() }
        val boxPadding = if (emojiDp < 8.dp) 0.5.dp else padding
        val corner = if (emojiDp < 8.dp) 3.dp else 6.dp

        Column(
            verticalArrangement = Arrangement.spacedBy(gap),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var index = 0
            repeat(bestRows) {
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    repeat(bestCols) {
                        if (index < groups) {
                            Box(
                                modifier = Modifier
                                    .border(1.dp, GROUP_BOX_BORDER, RoundedCornerShape(corner))
                                    .background(GROUP_BOX_BG, RoundedCornerShape(corner))
                                    .padding(boxPadding)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(0.5.dp)) {
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
private fun MultiplyingWithGroupsLevel2SourcesMenu(modifier: Modifier = Modifier) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showOriginalTextSubmenu by remember { mutableStateOf(false) }

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
            expanded = showSourcesMenu && !showOriginalTextSubmenu,
            onDismissRequest = { showSourcesMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(s.common.originalText) },
                trailingIcon = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                },
                onClick = { showOriginalTextSubmenu = true }
            )
        }

        DropdownMenu(
            expanded = showSourcesMenu && showOriginalTextSubmenu,
            onDismissRequest = {
                showOriginalTextSubmenu = false
                showSourcesMenu = false
            }
        ) {
            val url = "$HISTORYTRACERS_ORIGIN/index.html?page=class_content&arg=$ORIGINAL_CLASS_FILE"
            DropdownMenuItem(
                text = { Text(s.common.copyUrl) },
                onClick = {
                    showSourcesMenu = false
                    showOriginalTextSubmenu = false
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("URL", url))
                    Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                }
            )
            DropdownMenuItem(
                text = { Text(s.common.goToUrl) },
                onClick = {
                    showSourcesMenu = false
                    showOriginalTextSubmenu = false
                    safeOpenUri(uriHandler, url)
                }
            )
        }
    }
}
