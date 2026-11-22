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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.AutoSizeTitleText
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.ScreenAudioButton
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.sequenceGameScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.historytracers.app.ui.components.safeOpenUri

private const val SEQUENCE_GAME_FILE = "32cf89a4-e1ba-48e4-8c53-f55ab6cc7d84"

private fun generateRound(previousMissingIndex: Int): Pair<List<Int>, Int> {
    val start = Random.nextInt(0, 8)
    val seq = listOf(start, start + 1, start + 2)
    var missingIndex = Random.nextInt(0, 3)
    while (missingIndex == previousMissingIndex) missingIndex = Random.nextInt(0, 3)
    return Pair(seq, missingIndex)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SequenceGameScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val xs = sequenceGameScreenStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val repo = remember { ContentRepository(context) }
    val scope = rememberCoroutineScope()

    var contentText by remember { mutableStateOf<String?>(null) }
    var contentId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(language) {
        when (val result = repo.loadAndParse("$language/$SEQUENCE_GAME_FILE")) {
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
    var sequence by remember { mutableStateOf(listOf(3, 4, 5)) }
    var missingIndex by remember { mutableIntStateOf(1) }
    var guess by remember { mutableStateOf<Int?>(null) }
    var completed by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val (newSeq, newMissing) = generateRound(-1)
        sequence = newSeq
        missingIndex = newMissing
        guess = null
    }

    fun startNewGame() {
        val (newSeq, newMissing) = generateRound(missingIndex)
        sequence = newSeq
        missingIndex = newMissing
        guess = null
        completed = false
    }

    fun adjustGuess(delta: Int) {
        if (completed) return
        val base = if (guess == null && delta < 0) 9 else (guess ?: 0)
        val newGuess = (base + delta).coerceIn(0, 9)
        guess = newGuess
        if (newGuess == sequence[missingIndex]) {
            completed = true
            totalAwarded += 2
            onScoreChanged(initialScore + totalAwarded)
            scope.launch {
                preferences.recordLessonCompletion()
                preferences.markFirstStepsSectionCompleted("sequence_game")
            }
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
                        text = hts.sequenceGame,
                        modifier = Modifier.padding(start = 8.dp).weight(1f)
                    )
                    contentId?.let { id ->
                        ScreenAudioButton(
                            lessonId = SEQUENCE_GAME_FILE,
                            contentId = id
                        )
                    }
                }
            }

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val fontScale = LocalDensity.current.fontScale
                val compact = maxHeight < 400.dp || fontScale > 1.3f

                if (compact) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(top = 8.dp, bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SequenceGameIntro(contentText = contentText)
                        Spacer(Modifier.height(16.dp))
                        SequenceGameBoard(
                            sequence = sequence,
                            missingIndex = missingIndex,
                            guess = guess,
                            completed = completed,
                            onAdjustGuess = { adjustGuess(it) },
                            onNewGame = { startNewGame() }
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
                            SequenceGameIntro(contentText = contentText)
                        }
                        SequenceGameBoard(
                            sequence = sequence,
                            missingIndex = missingIndex,
                            guess = guess,
                            completed = completed,
                            onAdjustGuess = { adjustGuess(it) },
                            onNewGame = { startNewGame() }
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
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=fb9dca2c-cd76-4951-b7d0-4b2c694e8056"))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        safeOpenUri(uriHandler, "https://www.historytracers.org/index.html?page=class_content&arg=fb9dca2c-cd76-4951-b7d0-4b2c694e8056")
                    }
                )
            }
        }
    }
}


@Composable
private fun SequenceGameIntro(
    contentText: String?,
    modifier: Modifier = Modifier
) {
    val xs = sequenceGameScreenStringsForLanguage(LocalAppLanguage.current)
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
private fun SequenceGameBoard(
    sequence: List<Int>,
    missingIndex: Int,
    guess: Int?,
    completed: Boolean,
    onAdjustGuess: (Int) -> Unit,
    onNewGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val s = LocalUiStrings.current
    val xs = sequenceGameScreenStringsForLanguage(LocalAppLanguage.current)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0..2) {
                val isMissing = i == missingIndex
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isMissing) ButtonYellow else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = if (isMissing) 3.dp else 1.dp,
                            color = if (isMissing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isMissing) (guess?.toString() ?: "?") else "${sequence[i]}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isMissing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "${s.common.number} ${guess?.toString() ?: "?"}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = { onAdjustGuess(-1) },
                enabled = !completed && (guess == null || (guess ?: 0) > 0),
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = xs.downArrow,
                    modifier = Modifier.size(36.dp)
                )
            }
            FilledIconButton(
                onClick = { onAdjustGuess(1) },
                enabled = !completed && (guess == null || (guess ?: 9) < 9),
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    Icons.Filled.KeyboardArrowUp,
                    contentDescription = xs.upArrow,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        FilledTonalButton(
            onClick = onNewGame,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = ButtonYellow,
                contentColor = OnButtonYellow
            )
        ) {
            Text(
                text = xs.newGame,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        if (completed) {
            Text(
                text = xs.completionMessage,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )
        }
    }
}
