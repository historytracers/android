// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.R
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.features.equalSameGroupDifferentScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val TOTAL_LEVELS = 7
private const val QUESTIONS_PER_LEVEL = 3

private val compColors = listOf(
    Color(0xFF66cc90),
    Color(0xFF87ceeb),
    Color(0xFFFFB6C1),
    Color(0xFFFFD700),
    Color(0xFFDDA0DD),
    Color(0xFFFFA500),
    Color(0xFF98FB98),
)

private enum class Answer { EQUAL, GROUP, NEITHER }

private sealed class GameItem {
    data class Number(val value: Int) : GameItem()
    data class Maya(val value: Int) : GameItem()
    data class Circle(val color: Color) : GameItem()
    data class Square(val color: Color) : GameItem()
    data object Egypt : GameItem()
    data object Meso : GameItem()
    data object Pyramid : GameItem()
}

private data class Question(
    val left: GameItem,
    val right: GameItem,
    val answer: Answer,
)

private fun randomColor(): Color = compColors[Random.nextInt(compColors.size)]

private fun randomValue(level: Int): Int = when {
    level <= 1 -> Random.nextInt(0, 10)
    level == 2 -> Random.nextInt(10, 1000001)
    else -> Random.nextInt(1, 20)
}

private fun makeItem(level: Int, value: Int): GameItem =
    if (level <= 2) GameItem.Number(value) else GameItem.Maya(value)

private fun makeMixedItem(type: String): GameItem {
    if (type == "number") return GameItem.Number(Random.nextInt(0, 10))
    val color = randomColor()
    return if (type == "circle") GameItem.Circle(color) else GameItem.Square(color)
}

private fun buildMixedPair(answer: Answer): Pair<GameItem, GameItem> {
    val shapes = listOf("circle", "square")
    return when (answer) {
        Answer.NEITHER -> {
            val r = shapes[Random.nextInt(shapes.size)]
            makeMixedItem("number") to makeMixedItem(r)
        }
        Answer.GROUP -> {
            if (Random.nextInt(0, 3) == 2) {
                val s = randomColor()
                GameItem.Circle(s) to GameItem.Square(s)
            } else {
                val o = shapes[Random.nextInt(shapes.size)]
                val i = makeMixedItem(o)
                var n = randomColor()
                while (n == (i as? GameItem.Circle)?.color ?: (i as GameItem.Square).color) n = randomColor()
                val right = if (o == "circle") GameItem.Circle(n) else GameItem.Square(n)
                i to right
            }
        }
        Answer.EQUAL -> {
            val c = shapes[Random.nextInt(shapes.size)]
            val a = makeMixedItem(c)
            val clone = when (a) {
                is GameItem.Circle -> GameItem.Circle(a.color)
                is GameItem.Square -> GameItem.Square(a.color)
                else -> a
            }
            a to clone
        }
    }
}

private fun buildPyramidPair(answer: Answer): Pair<GameItem, GameItem> =
    if (answer == Answer.GROUP) {
        GameItem.Egypt to GameItem.Pyramid
    } else {
        if (Random.nextInt(0, 2) == 0) {
            GameItem.Meso to GameItem.Pyramid
        } else {
            GameItem.Meso to GameItem.Egypt
        }
    }

private fun buildPair(level: Int, answer: Answer): Question {
    if (level <= 3) {
        return if (answer == Answer.EQUAL) {
            val v = randomValue(level)
            Question(makeItem(level, v), makeItem(level, v), answer)
        } else {
            val n = randomValue(level)
            var s = randomValue(level)
            while (s == n) s = randomValue(level)
            Question(makeItem(level, n), makeItem(level, s), answer)
        }
    }
    val pair = if (level <= 6) buildMixedPair(answer) else buildPyramidPair(answer)
    return Question(pair.first, pair.second, answer)
}

private fun answerOptions(level: Int): List<Answer> = when {
    level <= 3 -> listOf(Answer.EQUAL, Answer.NEITHER)
    level <= 6 -> listOf(Answer.EQUAL, Answer.GROUP, Answer.NEITHER)
    else -> listOf(Answer.GROUP, Answer.NEITHER)
}

private fun isValidSequence(seq: List<Answer>): Boolean {
    for (i in 2 until seq.size) {
        if (seq[i] == seq[i - 1] && seq[i] == seq[i - 2]) return false
    }
    return true
}

private fun generateSequence(level: Int): List<Answer> {
    val s = answerOptions(level)
    val r = QUESTIONS_PER_LEVEL
    var n = emptyList<Answer>()
    var attempts = 0
    do {
        val t = s.toMutableList()
        while (t.size < r) t.add(s[Random.nextInt(s.size)])
        t.shuffle()
        n = t
        attempts++
    } while (!isValidSequence(n) && attempts < 200)
    if (!isValidSequence(n)) {
        n = (0 until r).map { s[it % s.size] }
    }
    return n
}

private fun buildPyramidScenario(scenario: String): Question = when (scenario) {
    "egypt_pyramid" -> Question(GameItem.Egypt, GameItem.Pyramid, Answer.GROUP)
    "meso_pyramid" -> Question(GameItem.Meso, GameItem.Pyramid, Answer.NEITHER)
    else -> Question(GameItem.Meso, GameItem.Egypt, Answer.NEITHER)
}

private fun maybeSwap(q: Question): Question =
    if (Random.nextInt(0, 2) == 0) q else q.copy(left = q.right, right = q.left)

private fun buildLevelQuestions(level: Int): List<Question> {
    if (level >= 7) {
        val scenarios = listOf("egypt_pyramid", "meso_pyramid", "meso_egypt").shuffled()
        return scenarios.map { maybeSwap(buildPyramidScenario(it)) }
    }
    return generateSequence(level).map { answer ->
        maybeSwap(buildPair(level, answer))
    }
}

@Composable
private fun CompFigure(item: GameItem, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(110.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(2.dp, Color(0xFF444444), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        when (item) {
            is GameItem.Number -> Text(
                text = item.value.toString(),
                fontSize = if (item.value >= 100000) 18.sp else 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5A3F2C),
                textAlign = TextAlign.Center
            )
            is GameItem.Maya -> MayaNumber(item.value, Modifier.size(64.dp))
            is GameItem.Circle -> Box(
                Modifier
                    .size(60.dp)
                    .background(item.color, CircleShape)
            )
            is GameItem.Square -> Box(
                Modifier
                    .size(60.dp)
                    .background(item.color, RoundedCornerShape(8.dp))
            )
            GameItem.Egypt -> Icon(
                painterResource(R.drawable.ic_egypt_pyramid),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Unspecified
            )
            GameItem.Meso -> Icon(
                painterResource(R.drawable.ic_meso_witz),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Unspecified
            )
            GameItem.Pyramid -> Icon(
                painterResource(R.drawable.ic_pyramid),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
private fun MayaNumber(value: Int, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val ink = Color(0xFF5A3F2C)
        val dotRadius = size.minDimension * 0.07f
        val gap = size.height * 0.06f
        val barWidth = 4 * dotRadius * 2f + 3 * gap
        val barHeight = size.height * 0.14f
        val bars = value / 5
        val dots = value % 5
        if (value == 0) {
            drawOval(color = ink, style = Stroke(width = size.minDimension * 0.04f))
            return@Canvas
        }
        val barsHeight = if (bars > 0) bars * barHeight + (bars - 1) * gap else 0f
        val dotsHeight = if (dots > 0) dotRadius * 2f + gap else 0f
        val contentHeight = barsHeight + dotsHeight
        var y = size.height - size.height * 0.05f - contentHeight
        if (dots > 0) {
            val totalWidth = dots * dotRadius * 2f + (dots - 1) * gap
            var x = center.x - totalWidth / 2f + dotRadius
            repeat(dots) {
                drawCircle(color = ink, radius = dotRadius, center = Offset(x, y + dotRadius))
                x += dotRadius * 2f + gap
            }
            y += dotRadius * 2f + gap
        }
        repeat(bars) {
            drawRoundRect(
                color = ink,
                topLeft = Offset(center.x - barWidth / 2f, y),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barHeight / 2f)
            )
            y += barHeight + gap
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualSameGroupDifferentScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = equalSameGroupDifferentScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }
    var level by remember { mutableIntStateOf(1) }
    var questions by remember { mutableStateOf(buildLevelQuestions(1)) }
    var qIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var answering by remember { mutableStateOf(false) }
    var showCorrect by remember { mutableStateOf(false) }
    var showWrong by remember { mutableStateOf(false) }
    var levelFinished by remember { mutableStateOf(false) }
    var gameFinished by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    fun loadLevel(newLevel: Int) {
        level = newLevel
        questions = buildLevelQuestions(newLevel)
        qIndex = 0
        score = 0
        answering = false
        showCorrect = false
        showWrong = false
        levelFinished = false
        gameFinished = false
    }

    fun showQuestion() {
        showCorrect = false
        showWrong = false
        answering = false
    }

    fun nextQuestion() {
        qIndex++
        if (qIndex >= questions.size) {
            answering = false
            showCorrect = false
            showWrong = false
            if (level >= TOTAL_LEVELS) {
                gameFinished = true
                scope.launch {
                    preferences.markIAmNotLikeYouSectionCompleted("equal_same_group_or_different")
                }
            } else {
                levelFinished = true
            }
        } else {
            showQuestion()
        }
    }

    fun answer(choice: Answer) {
        if (answering) return
        if (qIndex >= questions.size) return
        val q = questions[qIndex]
        if (choice == q.answer) {
            answering = true
            score++
            totalAwarded += 2
            onScoreChanged(initialScore + totalAwarded)
            showCorrect = true
            scope.launch {
                delay(1200)
                nextQuestion()
            }
        } else {
            showWrong = true
            scope.launch {
                delay(1200)
                showWrong = false
            }
        }
    }

    val levelOptions = answerOptions(level)
    val currentQuestion = questions.getOrNull(qIndex)
    val buttonsEnabled = !answering && !levelFinished && !gameFinished && qIndex < questions.size

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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(Modifier.height(8.dp))

                MarkdownText(
                    text = xs.instruction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "${s.common.level} $level: ${xs.levelNames[level - 1]}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5A3F2C),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(Modifier.height(16.dp))

                if (currentQuestion != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompFigure(currentQuestion.left)
                        CompFigure(currentQuestion.right)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "${xs.questionWord} ${qIndex + 1} ${xs.ofWord} ${questions.size}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5A3F2C)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "${s.common.score}: $score/${questions.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5A3F2C)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (Answer.EQUAL in levelOptions) {
                        Button(
                            onClick = { answer(Answer.EQUAL) },
                            enabled = buttonsEnabled,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB9F6CA),
                                contentColor = Color(0xFF2E241F),
                                disabledContainerColor = Color(0xFFB9F6CA).copy(alpha = 0.55f),
                                disabledContentColor = Color(0xFF2E241F).copy(alpha = 0.55f)
                            )
                        ) {
                            Text(xs.equal, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (Answer.GROUP in levelOptions) {
                        Button(
                            onClick = { answer(Answer.GROUP) },
                            enabled = buttonsEnabled,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFF9C4),
                                contentColor = Color(0xFF2E241F),
                                disabledContainerColor = Color(0xFFFFF9C4).copy(alpha = 0.55f),
                                disabledContentColor = Color(0xFF2E241F).copy(alpha = 0.55f)
                            )
                        ) {
                            Text(xs.sameGroup, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (Answer.NEITHER in levelOptions) {
                        Button(
                            onClick = { answer(Answer.NEITHER) },
                            enabled = buttonsEnabled,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFCDD2),
                                contentColor = Color(0xFF2E241F),
                                disabledContainerColor = Color(0xFFFFCDD2).copy(alpha = 0.55f),
                                disabledContentColor = Color(0xFF2E241F).copy(alpha = 0.55f)
                            )
                        ) {
                            Text(xs.neither, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        showCorrect -> Text(
                            text = s.common.correct,
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        showWrong -> Text(
                            text = xs.wrongMessage,
                            color = Color(0xFFC62828),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        levelFinished -> Text(
                            text = xs.levelCompleteMessage,
                            color = Color(0xFFB8860B),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        gameFinished -> Text(
                            text = xs.gameCompleteMessage,
                            color = Color(0xFFB8860B),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (levelFinished || gameFinished) {
                        FilledTonalButton(
                            onClick = {
                                if (gameFinished) {
                                    loadLevel(1)
                                } else {
                                    loadLevel(level + 1)
                                }
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = ButtonYellow,
                                contentColor = OnButtonYellow
                            )
                        ) {
                            Text(
                                text = if (gameFinished) xs.playAgain else s.common.nextLevel,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = { loadLevel(level) },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Text(xs.restart, fontWeight = FontWeight.Bold)
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
                val url = "https://www.historytracers.org/index.html?page=class_content&arg=00513774-3f13-422c-8d4a-de9001df335f"
                DropdownMenuItem(
                    text = { Text(s.common.copyUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", url))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        uriHandler.openUri(url)
                    }
                )
            }
        }
    }
}
