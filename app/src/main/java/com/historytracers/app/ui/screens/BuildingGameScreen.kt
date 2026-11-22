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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.AutoSizeTitleText
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.ScreenAudioButton
import com.historytracers.app.ui.features.buildingGameScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.FlagBlueDark
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.historytracers.app.ui.components.safeOpenUri

private const val BUILDING_GAME_FILE = "5bf4e241-c031-4a54-85e1-b8e6b0aa4e06"

private const val MAX_LEVEL = 3
private const val ROWS = 10
private const val COLUMNS = 10

/** Light-blue background for a normal cell that shows a number. */
private val NumberCellBlue = Color(0xFFADD8E6)

/** A different blue used to keep a discovered (revealed) number highlighted. */
private val DiscoveredCellBlue = FlagBlueDark

private fun hiddenPositionsFor(level: Int): Set<Int> {
    val hidden = mutableSetOf<Int>()
    for (row in 0 until ROWS) {
        val columns = (0 until COLUMNS).shuffled(Random).take(level)
        columns.forEach { hidden.add(row * COLUMNS + it) }
    }
    return hidden
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingGameScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = buildingGameScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    val language = LocalAppLanguage.current
    val repo = remember { ContentRepository(context) }
    var contentText by remember { mutableStateOf<String?>(null) }
    var contentId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(language) {
        when (val result = repo.loadAndParse("$language/$BUILDING_GAME_FILE")) {
            is ContentResult.SMGame -> {
                val content = result.data.content?.firstOrNull()
                contentText = content?.text
                    ?.firstOrNull { it.format?.contains("markdown") == true }
                    ?.text
                contentId = content?.id
            }
            else -> {
                contentText = null
                contentId = null
            }
        }
    }

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(1) }
    var hiddenPositions by remember { mutableStateOf(hiddenPositionsFor(1)) }
    var revealed by remember { mutableStateOf(mutableSetOf<Int>()) }
    var completed by remember { mutableStateOf(false) }
    var showFinalCongrats by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var isFeedbackPositive by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    fun startLevel(newLevel: Int) {
        level = newLevel
        hiddenPositions = hiddenPositionsFor(newLevel)
        revealed = mutableSetOf()
        completed = false
        showFinalCongrats = false
        feedbackMessage = ""
        isFeedbackPositive = false
    }

    LaunchedEffect(Unit) {
        startLevel(1)
    }

    fun revealCell(position: Int) {
        if (completed) return
        if (position !in hiddenPositions || position in revealed) return
        revealed = (revealed + position).toMutableSet()
        if (revealed.size == hiddenPositions.size) {
            completed = true
            totalAwarded += 2
            onScoreChanged(initialScore + totalAwarded)
            feedbackMessage = xs.completionMessage
            isFeedbackPositive = true
            if (level == MAX_LEVEL) {
                scope.launch {
                    preferences.recordLessonCompletion()
                    preferences.markFirstStepsSectionCompleted("building")
                }
            }
        }
    }

    fun toggleLevel() {
        if (!completed) return
        if (level == MAX_LEVEL) {
            if (!showFinalCongrats) {
                showFinalCongrats = true
                feedbackMessage = xs.congratsMessage
                isFeedbackPositive = true
                return
            }
            startLevel(1)
        } else {
            startLevel(level + 1)
        }
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
                    AutoSizeTitleText(
                        text = xs.title,
                        modifier = Modifier.padding(start = 8.dp).weight(1f)
                    )
                    contentId?.let { id ->
                        ScreenAudioButton(
                            lessonId = BUILDING_GAME_FILE,
                            contentId = id
                        )
                    }
                }
            }

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val fontScale = LocalDensity.current.fontScale
                val compact = maxHeight < 400.dp || fontScale > 1.3f
                // Keep the 10x10 grid, the level and the buttons on screen while
                // leaving room for the text on top, even on short phones.
                val gridMaxWidth = if (compact) 10000.dp else maxHeight * 0.55f

                if (compact) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(top = 8.dp, bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        BuildingGameIntro(contentText = contentText)
                        Spacer(Modifier.height(16.dp))
                        BuildingGameBoard(
                            level = level,
                            hiddenPositions = hiddenPositions,
                            revealed = revealed,
                            completed = completed,
                            feedbackMessage = feedbackMessage,
                            isFeedbackPositive = isFeedbackPositive,
                            onRevealCell = { revealCell(it) },
                            onNewExercise = { startLevel(level) },
                            onNextLevel = { toggleLevel() },
                            gridMaxWidth = gridMaxWidth
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false)
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            BuildingGameIntro(contentText = contentText)
                        }
                        BuildingGameBoard(
                            level = level,
                            hiddenPositions = hiddenPositions,
                            revealed = revealed,
                            completed = completed,
                            feedbackMessage = feedbackMessage,
                            isFeedbackPositive = isFeedbackPositive,
                            onRevealCell = { revealCell(it) },
                            onNewExercise = { startLevel(level) },
                            onNextLevel = { toggleLevel() },
                            gridMaxWidth = gridMaxWidth
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
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
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=e434ae19-4f25-4880-8227-0970cd27ccb6"))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        safeOpenUri(uriHandler, "https://www.historytracers.org/index.html?page=class_content&arg=e434ae19-4f25-4880-8227-0970cd27ccb6")
                    }
                )
            }
        }
    }
}


@Composable
private fun BuildingGameIntro(
    contentText: String?,
    modifier: Modifier = Modifier
) {
    val xs = buildingGameScreenStringsForLanguage(LocalAppLanguage.current)
    val intro = contentText
    if (intro != null) {
        MarkdownText(
            text = intro,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
    } else {
        Text(
            text = xs.instruction,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.padding(horizontal = 24.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun BuildingGameBoard(
    level: Int,
    hiddenPositions: Set<Int>,
    revealed: Set<Int>,
    completed: Boolean,
    feedbackMessage: String,
    isFeedbackPositive: Boolean,
    onRevealCell: (Int) -> Unit,
    onNewExercise: () -> Unit,
    onNextLevel: () -> Unit,
    gridMaxWidth: Dp,
    modifier: Modifier = Modifier
) {
    val s = LocalUiStrings.current
    val xs = buildingGameScreenStringsForLanguage(LocalAppLanguage.current)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${s.common.levelPrefix}$level",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .widthIn(max = gridMaxWidth)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            for (row in 0 until ROWS) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0 until COLUMNS) {
                        val position = row * COLUMNS + col
                        val isHidden = position in hiddenPositions
                        val isRevealed = position in revealed
                        val showQuestion = isHidden && !isRevealed
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        showQuestion -> ButtonYellow
                                        isRevealed -> DiscoveredCellBlue
                                        else -> NumberCellBlue
                                    }
                                )
                                .border(
                                    width = if (isHidden) 2.dp else 1.dp,
                                    color = if (isHidden) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = showQuestion && !completed) { onRevealCell(position) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (showQuestion) "?" else position.toString(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isHidden) FontWeight.Bold else FontWeight.Normal,
                                color = if (showQuestion) MaterialTheme.colorScheme.primary else OnButtonYellow
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalButton(
                onClick = onNewExercise,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Text(
                    text = s.common.newExercise,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            FilledTonalButton(
                onClick = onNextLevel,
                enabled = completed,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Text(
                    text = s.common.nextLevel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (feedbackMessage.isNotEmpty()) {
            Text(
                text = feedbackMessage,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isFeedbackPositive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )
        }
    }
}
