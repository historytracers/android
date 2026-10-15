// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.greaterOrSmallerGameScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val GAME_FILE = "09f835ae-8810-44fc-b50e-e3583f18d967"
private const val MIN_LEVEL = 1
private const val MAX_LEVEL = 4
private const val SECTION_ID = "greater_or_smaller_game"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"

private fun randomPair(level: Int): Pair<Long, Long> = when (level) {
    1 -> {
        var a = Random.nextLong(0L, 26L)
        var b = Random.nextLong(0L, 26L)
        while (a == b) b = Random.nextLong(0L, 26L)
        a to b
    }
    2 -> {
        var a = Random.nextLong(100L, 1001L)
        var b = Random.nextLong(100L, 1001L)
        while (a == b) b = Random.nextLong(100L, 1001L)
        a to b
    }
    3 -> {
        val small = Random.nextLong(0L, 10L)
        val big = Random.nextLong(1_000_000L, 10_000_000L)
        if (Random.nextBoolean()) small to big else big to small
    }
    else -> {
        val small = Random.nextLong(10L, 100L)
        val big = Random.nextLong(1_000_000L, 10_000_000L)
        if (Random.nextBoolean()) small to big else big to small
    }
}

private fun formatNumber(value: Long): String = "%,d".format(value)

@Composable
fun GreaterOrSmallerGameScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = greaterOrSmallerGameScreenStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    var level by remember { mutableIntStateOf(MIN_LEVEL) }
    var left by remember { mutableStateOf(0L) }
    var right by remember { mutableStateOf(0L) }
    var exerciseKey by remember { mutableIntStateOf(0) }
    var answered by remember { mutableStateOf(false) }
    var wasCorrect by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf("") }
    var finalCongratsShown by remember { mutableStateOf(false) }
    var levelRewarded by remember { mutableStateOf(false) }

    var instructionText by remember { mutableStateOf<String?>(null) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    LaunchedEffect(language) {
        when (val result = repo.loadAndParse("$language/$GAME_FILE")) {
            is ContentResult.SMGame ->
                instructionText = result.data?.content?.firstOrNull()?.text?.firstOrNull()?.text
            else -> instructionText = null
        }
    }

    fun newPair() {
        val (a, b) = randomPair(level)
        left = a
        right = b
    }

    fun newExercise() {
        exerciseKey++
        newPair()
        answered = false
        wasCorrect = false
        feedback = ""
        finalCongratsShown = false
    }

    fun startLevel(newLevel: Int) {
        level = newLevel
        levelRewarded = false
        newExercise()
    }

    LaunchedEffect(Unit) {
        // Ensure the initial exercise has a valid pair before first composition of the cards.
        newPair()
    }

    fun submit(userSaysInOrder: Boolean) {
        if (answered) return
        val inOrder = right > left
        val correct = userSaysInOrder == inOrder
        answered = true
        wasCorrect = correct

        val bigNumber = formatNumber(maxOf(left, right))
        val smallNumber = formatNumber(minOf(left, right))
        val much = level >= 3

        feedback = if (correct) {
            if (much) xs.correctMuchBigger.format(bigNumber, smallNumber)
            else xs.correctBigger.format(bigNumber, smallNumber)
        } else {
            if (much) xs.wrongMuchSmaller.format(smallNumber, bigNumber)
            else xs.wrongSmaller.format(smallNumber, bigNumber)
        }

        if (correct) {
            // Award and record the level's completion only once, so repeatedly pressing
            // "New Exercise" within the same level cannot grant extra points.
            if (!levelRewarded) {
                levelRewarded = true
                totalAwarded += 2
                onScoreChanged(initialScore + totalAwarded)
                if (level == MAX_LEVEL) {
                    scope.launch {
                        preferences.recordLessonCompletion()
                        preferences.markMeasuringSectionCompleted(SECTION_ID)
                    }
                }
            }
            if (level == MAX_LEVEL) {
                finalCongratsShown = true
                feedback = xs.finalCongratulations
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))

                val instruction = instructionText
                if (instruction != null) {
                    MarkdownText(text = instruction)
                } else {
                    Text(
                        text = xs.instruction,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "${s.common.levelPrefix}$level/$MAX_LEVEL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    key(exerciseKey) {
                        NumberCard(label = xs.labelA, value = formatNumber(left))
                        Text(
                            text = if (level >= 3) "≪" else "<",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        NumberCard(label = xs.labelB, value = formatNumber(right))
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { submit(true) },
                        enabled = !answered,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = xs.inOrder,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    FilledTonalButton(
                        onClick = { submit(false) },
                        enabled = !answered,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = xs.notInOrder,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                if (feedback.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (finalCongratsShown || wasCorrect) Color(0xFF2E7D32)
                        else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { newExercise() },
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

                    if (answered && wasCorrect && level < MAX_LEVEL) {
                        FilledTonalButton(
                            onClick = { startLevel(level + 1) },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
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

                    if (finalCongratsShown) {
                        FilledTonalButton(
                            onClick = { startLevel(MIN_LEVEL) },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = xs.playAgain,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp, start = 8.dp)
        ) {
            val uriHandler = LocalUriHandler.current
            val sourceUrl = HISTORYTRACERS_ORIGIN + "index.html?page=class_content&arg=" + GAME_FILE

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
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", sourceUrl))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        safeOpenUri(uriHandler, sourceUrl)
                    }
                )
            }
        }
    }
}

@Composable
private fun NumberCard(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
