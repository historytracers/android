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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.ScreenAudioButton
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.miscStringsForLanguage
import com.historytracers.app.ui.features.multiplicationTableWithAbacusScreenStringsForLanguage
import com.historytracers.app.ui.features.multiplyingWithGroupsScreenStringsForLanguage
import kotlinx.coroutines.launch
import kotlin.random.Random

// Ports the web class "Multiplying with Groups (0 to 3)"
// (f641941e-55f0-40dd-a05f-81392b23cacd). Two screens: a rules screen and a
// two-column matching game. The left column shows a multiplication and the
// right column shows its result together with groups of characters. The first
// factor is the number of characters inside each rectangle and the second
// factor is the number of rectangles, so 2 x 4 is drawn as four rectangles
// with two characters.
private const val MULTIPLYING_WITH_GROUPS_SECTION_ID = "multiplying_with_groups"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"
private const val ORIGINAL_CLASS_FILE = "f641941e-55f0-40dd-a05f-81392b23cacd"

private const val MAX_LEVEL = 3
private const val MAX_MULTIPLIER = 10

// The exact same icon list used by the web version (htMultGroupIcons).
private val GROUP_ICONS = listOf("🐶", "🐱", "🍎", "⭐", "🔺")

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

private data class MultGroupPair(val first: Int, val second: Int) {
    val result: Int get() = first * second
}

@Composable
fun MultiplyingWithGroupsRulesScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val mts = multiplicationTableWithAbacusScreenStringsForLanguage(LocalAppLanguage.current)
    val xs = multiplyingWithGroupsScreenStringsForLanguage(LocalAppLanguage.current)

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
                    text = mts.multiplyingWithGroups,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 8.dp).weight(1f)
                )
                ScreenAudioButton(
                    lessonId = ORIGINAL_CLASS_FILE,
                    contentId = null
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

            MultiplyingWithGroupsSourcesMenu(
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
fun MultiplyingWithGroupsGameScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToMultiplicationTableHub: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val mcs = miscStringsForLanguage(LocalAppLanguage.current)
    val xs = multiplyingWithGroupsScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var level by remember { mutableIntStateOf(1) }
    var pairs by remember { mutableStateOf<List<MultGroupPair>>(emptyList()) }
    var icon by remember { mutableStateOf(GROUP_ICONS.first()) }
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
        val factors = when (level) {
            1 -> listOf(0, 1, 1)
            2 -> listOf(2, 2)
            else -> listOf(3, 3)
        }
        val multipliers = (1..MAX_MULTIPLIER).toMutableList()
            .also { it.shuffle(Random) }
            .take(factors.size)
        pairs = factors.mapIndexed { i, f -> MultGroupPair(f, multipliers[i]) }
        icon = GROUP_ICONS.random(Random)
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
                        preferences.markMultiplicationTableSectionCompleted(MULTIPLYING_WITH_GROUPS_SECTION_ID)
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
                    text = xs.gameTitle,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 8.dp).weight(1f)
                )
                ScreenAudioButton(
                    lessonId = ORIGINAL_CLASS_FILE,
                    contentId = null
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
                MarkdownText(text = xs.gameIntro)

                Spacer(Modifier.height(12.dp))

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

                Spacer(Modifier.height(12.dp))

                MultGroupBoard(
                    pairs = pairs,
                    icon = icon,
                    leftOrder = leftOrder,
                    rightOrder = rightOrder,
                    selectedLeft = selectedLeft,
                    selectedRight = selectedRight,
                    matchedIds = matchedIds,
                    onSelect = ::select
                )

                if (feedback.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = GROUP_FEEDBACK,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

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

                if (levelDone) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if (level >= MAX_LEVEL) xs.perfectMessage else xs.correctMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = onNavigateToMultiplicationTableHub,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text(mcs.multiplicationTable, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(48.dp))
            }

            MultiplyingWithGroupsSourcesMenu(
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MultGroupBoard(
    pairs: List<MultGroupPair>,
    icon: String,
    leftOrder: List<Int>,
    rightOrder: List<Int>,
    selectedLeft: Int?,
    selectedRight: Int?,
    matchedIds: Set<Int>,
    onSelect: (String, Int) -> Unit
) {
    val xs = multiplyingWithGroupsScreenStringsForLanguage(LocalAppLanguage.current)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
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
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(container)
                        .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                        .clickable(enabled = !matched) { onSelect("left", id) }
                        .padding(horizontal = 8.dp, vertical = 14.dp),
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
            modifier = Modifier.weight(1f),
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
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(container)
                        .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                        .clickable(enabled = !matched) { onSelect("right", id) }
                        // The groups are decorative for sighted users, so the
                        // accessible name must also describe them: "10.
                        // Groups: 2. Characters per group: 5."
                        .semantics {
                            contentDescription = if (pair.first > 0) {
                                "${pair.result}. ${xs.groupsDescription.format(pair.second, pair.first)}"
                            } else {
                                "${pair.result}. ${xs.noGroupsDescription}"
                            }
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = pair.result.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                        if (pair.first > 0) {
                            Spacer(Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                repeat(pair.second) {
                                    Box(
                                        modifier = Modifier
                                            .border(2.dp, GROUP_BOX_BORDER, RoundedCornerShape(8.dp))
                                            .background(GROUP_BOX_BG, RoundedCornerShape(8.dp))
                                            .padding(4.dp)
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            repeat(pair.first) {
                                                Text(text = icon, fontSize = 14.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MultiplyingWithGroupsSourcesMenu(modifier: Modifier = Modifier) {
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
