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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.PlayingWithAxiomsScreenStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.playingWithAxiomsScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val ORIGINAL_TEXT_URL = "https://www.historytracers.org/index.html?page=class_content&arg=462b1750-2d39-454d-a780-f22d4bd154c3"

private const val TOTAL_ROUNDS = 3
private const val TOTAL_LEVELS = 3

private sealed class AxiomToken {
    data class Value(val v: Int) : AxiomToken()
    data class Op(val v: String) : AxiomToken()
    data class Edit(val idx: Int) : AxiomToken()
}

@Composable
fun PlayingWithAxiomsGameScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToRoadToSomewhere: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = playingWithAxiomsScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }

    var level by remember { mutableIntStateOf(1) }
    var round by remember { mutableIntStateOf(0) }
    var a by remember { mutableIntStateOf(0) }
    var b by remember { mutableIntStateOf(0) }
    var left by remember { mutableStateOf<List<AxiomToken>>(emptyList()) }
    var right by remember { mutableStateOf<List<AxiomToken>>(emptyList()) }
    var values by remember { mutableStateOf<List<Int>>(emptyList()) }
    var targets by remember { mutableStateOf<List<Int>>(emptyList()) }
    var locked by remember { mutableStateOf<List<Boolean>>(emptyList()) }
    var solved by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var correctShown by remember { mutableStateOf(false) }
    var levelCompleteShown by remember { mutableStateOf(false) }
    var gameCompleteShown by remember { mutableStateOf(false) }
    var gameCompletionHandled by remember { mutableStateOf(false) }

    fun setupRound() {
        if (level == 1) {
            a = Random.nextInt(1, 10)
            b = 0
            left = listOf(AxiomToken.Value(a), AxiomToken.Op("+"), AxiomToken.Value(0))
            right = listOf(AxiomToken.Edit(0))
            targets = listOf(a)
        } else {
            a = Random.nextInt(0, 10)
            do {
                b = Random.nextInt(0, 10)
            } while (a == b)
            left = listOf(AxiomToken.Value(a), AxiomToken.Op("+"), AxiomToken.Value(b))
            if (level == 2) {
                if (Random.nextInt(0, 2) == 0) {
                    right = listOf(AxiomToken.Value(b), AxiomToken.Op("+"), AxiomToken.Edit(0))
                    targets = listOf(a)
                } else {
                    right = listOf(AxiomToken.Edit(0), AxiomToken.Op("+"), AxiomToken.Value(a))
                    targets = listOf(b)
                }
            } else {
                right = listOf(AxiomToken.Edit(0), AxiomToken.Op("+"), AxiomToken.Edit(1))
                targets = listOf(b, a)
            }
        }
        values = targets.map { if (it == 0) 1 else 0 }
        locked = targets.map { false }
        solved = false
        round++
    }

    fun loadLevel() {
        round = 0
        score = 0
        correctShown = false
        levelCompleteShown = false
        gameCompleteShown = false
        setupRound()
    }

    fun finishLevel() {
        if (level >= TOTAL_LEVELS) {
            gameCompleteShown = true
        } else {
            levelCompleteShown = true
        }
    }

    fun next() {
        level = if (gameCompleteShown) 1 else level + 1
        loadLevel()
    }

    fun check() {
        if (locked.all { it }) {
            solved = true
            score++
            correctShown = true
        }
    }

    fun change(idx: Int, delta: Int) {
        if (solved || locked.getOrNull(idx) == true) return
        val newValues = values.toMutableList()
        newValues[idx] = (newValues[idx] + delta).coerceIn(0, 9)
        values = newValues
        if (values[idx] == targets.getOrNull(idx)) {
            locked = locked.toMutableList().also { it[idx] = true }
        }
        check()
    }

    LaunchedEffect(Unit) {
        loadLevel()
    }

    LaunchedEffect(correctShown) {
        if (correctShown) {
            delay(1100)
            if (round >= TOTAL_ROUNDS) {
                correctShown = false
                finishLevel()
            } else {
                correctShown = false
                setupRound()
            }
        }
    }

    LaunchedEffect(gameCompleteShown) {
        if (gameCompleteShown && !gameCompletionHandled) {
            gameCompletionHandled = true
            preferences.markRoadToSomewhereSectionCompleted("playing_with_axioms")
            preferences.recordLessonCompletion()
            onScoreChanged(currentScore + 2)
        }
    }

    val fullTokens = remember(left, right) {
        buildList {
            addAll(left)
            add(AxiomToken.Op("="))
            addAll(right)
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
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(4.dp))

                Text(
                    text = "${xs.levelWord} $level: ${levelDesc(xs, level)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = levelInstr(xs, level),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = xs.equationLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = xs.arrowsLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            fullTokens.forEach { token ->
                                AxiomTokenCell(
                                    token = token,
                                    values = values,
                                    locked = locked,
                                    modifier = Modifier.width(44.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(2.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            fullTokens.forEach { token ->
                                if (token is AxiomToken.Edit) {
                                    AxiomArrowCell(
                                        increaseLabel = xs.increase,
                                        decreaseLabel = xs.decrease,
                                        enabled = !solved && !locked.getOrElse(token.idx) { false },
                                        onIncrease = { change(token.idx, 1) },
                                        onDecrease = { change(token.idx, -1) },
                                        modifier = Modifier.width(44.dp)
                                    )
                                } else {
                                    Spacer(Modifier.width(44.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "${xs.exerciseWord} $round ${xs.of} $TOTAL_ROUNDS",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "${s.common.score}: $score/$TOTAL_ROUNDS",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                when {
                    correctShown -> FeedbackRow(
                        icon = { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        text = s.common.correct,
                        color = Color(0xFF2E7D32)
                    )
                    levelCompleteShown -> FeedbackRow(
                        icon = { Icon(Icons.Filled.EmojiEvents, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        text = s.common.levelComplete,
                        color = MaterialTheme.colorScheme.primary
                    )
                    gameCompleteShown -> FeedbackRow(
                        icon = { Icon(Icons.Filled.EmojiEvents, contentDescription = null, modifier = Modifier.size(20.dp)) },
                        text = xs.gameComplete,
                        color = Color(0xFF2E7D32)
                    )
                }

                Spacer(Modifier.height(8.dp))

                if (levelCompleteShown || gameCompleteShown) {
                    FilledTonalButton(
                        onClick = { next() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (gameCompleteShown) xs.playAgain else s.common.nextLevel,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                }

                FilledTonalButton(
                    onClick = { loadLevel() },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = xs.restart,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                if (onNavigateToRoadToSomewhere != null) {
                    Spacer(Modifier.height(16.dp))
                    FilledTonalButton(
                        onClick = onNavigateToRoadToSomewhere,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(hts.aRoadToSomewhere, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(48.dp))
            }
        }

        AxiomSourcesMenu(
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

private fun levelDesc(xs: PlayingWithAxiomsScreenStrings, level: Int): String = when (level) {
    1 -> xs.level1Desc
    2 -> xs.level2Desc
    else -> xs.level3Desc
}

private fun levelInstr(xs: PlayingWithAxiomsScreenStrings, level: Int): String = when (level) {
    1 -> xs.instr1
    2 -> xs.instr2
    else -> xs.instr3
}

@Composable
private fun RowScope.AxiomTokenCell(
    token: AxiomToken,
    values: List<Int>,
    locked: List<Boolean>,
    modifier: Modifier = Modifier
) {
    when (token) {
        is AxiomToken.Value -> Text(
            text = token.v.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = modifier
        )
        is AxiomToken.Op -> Text(
            text = token.v,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = modifier
        )
        is AxiomToken.Edit -> {
            val isLocked = locked.getOrElse(token.idx) { false }
            Box(
                modifier = modifier
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isLocked) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = if (isLocked) 2.dp else 1.dp,
                        color = if (isLocked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = values.getOrElse(token.idx) { 0 }.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isLocked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AxiomArrowCell(
    increaseLabel: String,
    decreaseLabel: String,
    enabled: Boolean,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledIconButton(
            onClick = onIncrease,
            enabled = enabled,
            modifier = Modifier.size(32.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                Icons.Filled.KeyboardArrowUp,
                contentDescription = increaseLabel,
                modifier = Modifier.size(20.dp)
            )
        }
        FilledIconButton(
            onClick = onDecrease,
            enabled = enabled,
            modifier = Modifier.size(32.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = decreaseLabel,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun FeedbackRow(
    icon: @Composable () -> Unit,
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = color,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AxiomSourcesMenu(modifier: Modifier = Modifier) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

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
                    clipboard.setPrimaryClip(ClipData.newPlainText("URL", ORIGINAL_TEXT_URL))
                    Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                }
            )
            DropdownMenuItem(
                text = { Text(s.common.goToUrl) },
                onClick = {
                    showSourcesMenu = false
                    showMainTextSubmenu = false
                    uriHandler.openUri(ORIGINAL_TEXT_URL)
                }
            )
        }
    }
}
