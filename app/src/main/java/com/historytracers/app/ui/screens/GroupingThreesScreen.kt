// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.historytracers.app.ui.features.groupingThreesScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val GROUPING_THREES_SECTION_ID = "grouping_threes"
private const val GROUPING_THREES_LAST_LEVEL = 2

// Element sizes. The grouped board uses a smaller glyph and roomy padding so the
// surrounding rectangle can never overlap the drawings.
private val GROUPING_THREES_PLAIN_ICON = 26.sp
private val GROUPING_THREES_GROUP_ICON = 20.sp
private val GROUPING_THREES_GROUP_SPACING = 8.dp
private val GROUPING_THREES_GROUP_PADDING_H = 6.dp
private val GROUPING_THREES_GROUP_PADDING_V = 5.dp

// Apple, star, cat, triangle, dog - same elements used by the web game.
private val GROUPING_THREES_ICONS = listOf(
    "\uD83C\uDF4E",
    "\u2B50",
    "\uD83D\uDC31",
    "\uD83D\uDD3A",
    "\uD83D\uDC36"
)

// Level 1 asks 1-3 groups, level 2 asks 4-6, level 3 asks 7-10.
private val GROUPING_THREES_RANGES = listOf(
    listOf(1, 2, 3),
    listOf(4, 5, 6),
    listOf(7, 8, 9, 10)
)

private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"
private const val GROUPING_THREES_URL =
    HISTORYTRACERS_ORIGIN + "index.html?page=class_content&arg=931e0f32-bdc4-4f96-9039-e49cd9d06119"
private const val ALWAYS_PRESENT_URL =
    HISTORYTRACERS_ORIGIN + "index.html?page=class_content&arg=a9086b63-70d4-4984-a353-1118305128b6"
private const val TABLE_OF_THREE_URL =
    HISTORYTRACERS_ORIGIN + "index.html?page=class_content&arg=f7f4988d-1427-4e26-a6a8-c375c57924c7"

private data class GroupingThreesSource(val label: String, val url: String)

// Ports the "Grouping in Threes (Game)" web game (931e0f32). A number of elements
// (always a multiple of 3, from 3 to 30) is shown and the player says into how many
// groups of three they can be organized. Three levels: 1-3, 4-6 and 7-10 groups.
// The game opens on the level line, and the multiplication table of 3 is shown
// below as an aid.
@Composable
fun GroupingThreesScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToWhereAreWeFrom: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = groupingThreesScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var level by remember { mutableIntStateOf(0) }
    var groups by remember { mutableIntStateOf(1) }
    var iconIndex by remember { mutableIntStateOf(0) }
    var correct by remember { mutableStateOf(false) }
    var wrongChoices by remember { mutableStateOf(emptySet<Int>()) }
    var feedback by remember { mutableStateOf("") }
    var feedbackPositive by remember { mutableStateOf(false) }
    var wrongAwarded by remember { mutableStateOf(false) }
    var sectionMarked by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var activeSource by remember { mutableStateOf<GroupingThreesSource?>(null) }

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    fun award(points: Int) {
        if (points <= 0) return
        totalAwarded += points
        onScoreChanged(initialScore + totalAwarded)
    }

    fun startLevel(newLevel: Int) {
        val range = GROUPING_THREES_RANGES[newLevel]
        level = newLevel
        groups = range[Random.nextInt(range.size)]
        iconIndex = Random.nextInt(GROUPING_THREES_ICONS.size)
        correct = false
        wrongChoices = emptySet()
        feedback = ""
        feedbackPositive = false
        wrongAwarded = false
    }

    // This is the group's last screen, so it records the day's streak on arrival.
    LaunchedEffect(Unit) {
        preferences.recordLessonCompletion()
        startLevel(0)
    }

    val total = groups * 3
    val icon = GROUPING_THREES_ICONS[iconIndex]
    val isLastLevel = level == GROUPING_THREES_LAST_LEVEL

    fun answer(choice: Int) {
        if (correct) return
        if (choice == groups) {
            correct = true
            feedbackPositive = true
            feedback = xs.msgCorrect.format(total, groups) +
                if (isLastLevel) " " + xs.msgFinal else ""
            award(2)
            if (isLastLevel && !sectionMarked) {
                sectionMarked = true
                scope.launch { preferences.markWhereAreWeFromSectionCompleted(GROUPING_THREES_SECTION_ID) }
            }
        } else {
            wrongChoices = wrongChoices + choice
            feedbackPositive = false
            feedback = xs.msgWrong.format(total)
            if (!wrongAwarded) {
                wrongAwarded = true
                award(1)
            }
        }
    }

    val sources = listOf(
        GroupingThreesSource(xs.sourceAlwaysPresent, ALWAYS_PRESENT_URL),
        GroupingThreesSource(xs.sourceTable, TABLE_OF_THREE_URL),
        GroupingThreesSource(s.common.originalText, GROUPING_THREES_URL)
    )

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
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = xs.levelLabel.format(level + 1),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = xs.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = xs.totalLabel.format(total),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))

                if (correct) {
                    GroupedElements(icon = icon, groupCount = groups)
                } else {
                    PlainElements(icon = icon, total = total)
                }

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GROUPING_THREES_RANGES[level].forEach { value ->
                        Button(
                            onClick = { answer(value) },
                            enabled = !correct && value !in wrongChoices,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = value.toString(),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (feedback.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (feedbackPositive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilledTonalButton(
                        onClick = { startLevel(level) },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = xs.newExercise,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (correct) {
                        FilledTonalButton(
                            onClick = {
                                if (isLastLevel) startLevel(0) else startLevel(level + 1)
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (isLastLevel) xs.playAgain else xs.nextLevel,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                MultiplicationTableOfThree(title = xs.tableTitle)

                Spacer(Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = onNavigateToWhereAreWeFrom,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = hts.whereAreWeFrom,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Spacer(Modifier.height(48.dp))
            }
        }

        val uriHandler = LocalUriHandler.current
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp, start = 8.dp)
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
                        text = { Text(source.label) },
                        trailingIcon = {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                        },
                        onClick = { activeSource = source }
                    )
                }
            }

            DropdownMenu(
                expanded = showSourcesMenu && activeSource != null,
                onDismissRequest = { activeSource = null }
            ) {
                activeSource?.let { source ->
                    DropdownMenuItem(
                        text = { Text(s.common.copyUrl) },
                        onClick = {
                            showSourcesMenu = false
                            activeSource = null
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", source.url))
                            Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            activeSource = null
                            safeOpenUri(uriHandler, source.url)
                        }
                    )
                }
            }
        }
    }
}

/** The elements as they appear before answering: they wrap so none is ever cut off. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlainElements(icon: String, total: Int) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(total) {
            Text(text = icon, fontSize = GROUPING_THREES_PLAIN_ICON)
        }
    }
}

/**
 * After a correct answer the elements are shown already organized in threes. Each
 * group is a single item of the [FlowRow], so a group is never split between lines,
 * and the padding keeps the rectangle clear of the drawings.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GroupedElements(icon: String, groupCount: Int) {
    val borderColor = Color(0xFF4CAF50)
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            GROUPING_THREES_GROUP_SPACING,
            Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(groupCount) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                    .padding(
                        horizontal = GROUPING_THREES_GROUP_PADDING_H,
                        vertical = GROUPING_THREES_GROUP_PADDING_V
                    )
            ) {
                repeat(3) {
                    Text(text = icon, fontSize = GROUPING_THREES_GROUP_ICON)
                }
            }
        }
    }
}

@Composable
private fun MultiplicationTableOfThree(title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .border(1.dp, Color(0xFF9E9E9E), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        // Two columns of five lines: the table uses half the height, so the hub
        // button below it stays visible. Both columns share the width and centre
        // their text, so a very large font wraps instead of being clipped.
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (n in 1..5) TableLineOfThree(n)
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (n in 6..10) TableLineOfThree(n)
            }
        }
    }
}

@Composable
private fun TableLineOfThree(n: Int) {
    Text(
        text = "3 \u00d7 $n = ${n * 3}",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
    )
}
