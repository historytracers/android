// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.TextRenderer
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.measuringOneDirectionScreenStringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile

private const val SMARTPHONE_GAME_FILE = "e609a3e8-7a6b-4d38-b74c-3cece49c66d1"
private const val SECTION_ID = "measuring_one_direction"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"

private const val INTRO_ID = "7169ab5b-222f-41f3-b15a-7a5f8405350e"
private const val STEP_BY_STEP_ID = "4ef7ea19-2c9d-4b04-8e9b-6973ddf0876b"
private const val EXAMPLE_ID = "104de17e-0acb-4a22-8f10-1b42b01b6bab"
private const val THINKING_ID = "e785556b-f538-4891-aa47-99f6fde8a5c9"
private const val COMPARING_ID = "68b7c7d1-39ee-4b1b-ba17-c74698eefcde"
private const val CONCLUSION_ID = "f8225205-eea8-4f31-a661-7be7a218e4a0"

@Composable
fun MeasuringOneDirectionIntroScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MeasuringOneDirectionGameContent(
        contentId = INTRO_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MeasuringOneDirectionStepByStepScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MeasuringOneDirectionGameContent(
        contentId = STEP_BY_STEP_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MeasuringOneDirectionExampleScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MeasuringOneDirectionGameContent(
        contentId = EXAMPLE_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MeasuringOneDirectionThinkingScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MeasuringOneDirectionGameContent(
        contentId = THINKING_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MeasuringOneDirectionComparingScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MeasuringOneDirectionGameContent(
        contentId = COMPARING_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MeasuringOneDirectionConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToMeasuring: () -> Unit = {}
) {
    MeasuringOneDirectionGameContent(
        contentId = CONCLUSION_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateToMeasuring = onNavigateToMeasuring
    )
}

private fun smileEmoji(smile: String): String = when (smile) {
    "thinking", "think" -> "\uD83E\uDD14"
    "happy", "smile" -> "\uD83D\uDE0A"
    "nerd" -> "\uD83E\uDD13"
    "shocking", "surprise", "surprising" -> "\uD83D\uDE32"
    "party" -> "\uD83E\uDD73"
    "inlove", "loving" -> "\uD83D\uDE0D"
    else -> "\uD83D\uDE0A"
}

private fun sourceUrl(page: String): String =
    if (page.startsWith("index.html")) HISTORYTRACERS_ORIGIN + page else page

@Composable
private fun MeasuringOneDirectionGameContent(
    contentId: String,
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToMeasuring: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = measuringOneDirectionScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }
    var game by remember { mutableStateOf<SMGameFile?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        when (val result = repo.loadAndParse("$language/$SMARTPHONE_GAME_FILE")) {
            is ContentResult.SMGame -> game = result.data
            is ContentResult.Error -> error = result.message
            else -> error = s.common.unsupportedContentType
        }
    }

    val contentList = game?.content ?: emptyList()
    val content = contentList.firstOrNull { it.id == contentId }

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    fun award(points: Int) {
        if (points <= 0) return
        totalAwarded += points
        onScoreChanged(initialScore + totalAwarded)
    }

    LaunchedEffect(content) {
        val node = content
        if (node != null) {
            award(node.score)
            if (onNavigateToMeasuring != null) {
                preferences.recordLessonCompletion()
                preferences.markMeasuringSectionCompleted(SECTION_ID)
            }
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
                Text(
                    text = xs.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
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
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    content.text?.forEach { text ->
                        if (text.format == "markdown") {
                            MarkdownText(text = text.text ?: "")
                        } else {
                            TextRenderer(text = text, repo = repo)
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    when (content.id) {
                        EXAMPLE_ID -> {
                            Spacer(Modifier.height(8.dp))
                            RulerWithString()
                        }
                        COMPARING_ID -> {
                            Spacer(Modifier.height(8.dp))
                            ComparingStringsApp()
                        }
                    }

                    if (content.answer != null) {
                        AnswerSection(
                            content = content,
                            onAnswered = { points -> award(points) },
                            wrongAnswerMessage = xs.wrongAnswerMessage,
                            scoreDoubledMessage = xs.scoreDoubledMessage
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onNavigatePrev != null) {
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
                        }
                        if (onNavigateNext != null) {
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
                        }
                    }

                    if (onNavigateToMeasuring != null) {
                        Spacer(Modifier.height(16.dp))
                        FilledTonalButton(
                            onClick = onNavigateToMeasuring,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(hts.measuring, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(48.dp))
                }
            }

            content?.takeIf { it.smile.isNotEmpty() }?.let { node ->
                Text(
                    text = smileEmoji(node.smile),
                    fontSize = 40.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 8.dp, end = 8.dp)
                )
            }

            content?.sourceMenu?.takeIf { it.isNotEmpty() }?.let { sources ->
                SourcesMenu(
                    sources = sources,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun RulerWithString(modifier: Modifier = Modifier) {
    val textPaint = remember {
        Paint().apply {
            color = android.graphics.Color.DKGRAY
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
    }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 12.dp)
    ) {
        textPaint.textSize = 12.dp.toPx()

        val w = size.width
        val h = size.height
        val left = 4f
        val right = w - 4f
        val units = 10
        val step = (right - left) / units
        val rulerTop = h * 0.50f
        val rulerHeight = h * 0.26f

        // Ruler body.
        drawRoundRect(
            color = Color(0xFFF3E3B3),
            topLeft = Offset(left, rulerTop),
            size = Size(right - left, rulerHeight),
            cornerRadius = CornerRadius(6f)
        )
        drawRoundRect(
            color = Color(0xFF9E7B3A),
            topLeft = Offset(left, rulerTop),
            size = Size(right - left, rulerHeight),
            cornerRadius = CornerRadius(6f),
            style = Stroke(width = 3f)
        )

        // Tick marks and numbers.
        for (i in 0..units) {
            val x = left + i * step
            val longTick = i % 5 == 0
            val tickHeight = if (longTick) rulerHeight * 0.65f else rulerHeight * 0.38f
            drawLine(
                color = Color(0xFF6D4C41),
                start = Offset(x, rulerTop),
                end = Offset(x, rulerTop + tickHeight),
                strokeWidth = if (longTick) 4f else 2f
            )
            drawContext.canvas.nativeCanvas.drawText(
                i.toString(),
                x,
                rulerTop + rulerHeight + 12.dp.toPx(),
                textPaint
            )
        }

        // The string, starting at 0 and ending at 5.
        val stringY = rulerTop - h * 0.16f
        drawLine(
            color = Color(0xFFC62828),
            start = Offset(left, stringY),
            end = Offset(left + 5 * step, stringY),
            strokeWidth = h * 0.055f,
            cap = StrokeCap.Round
        )
        drawCircle(color = Color(0xFFC62828), radius = h * 0.028f, center = Offset(left, stringY))
        drawCircle(color = Color(0xFFC62828), radius = h * 0.028f, center = Offset(left + 5 * step, stringY))
    }
}

@Composable
private fun ComparingStringsApp(modifier: Modifier = Modifier) {
    val ss = measuringOneDirectionScreenStringsForLanguage(LocalAppLanguage.current)

    // Two of the strings share the same color, so that equal length alone does not
    // guarantee full equality: the color must match too.
    val colorA = Color(0xFFC62828)
    val colorB = Color(0xFFC62828)
    val colorC = Color(0xFF1565C0)

    var lenA by remember { mutableIntStateOf(2) }
    var lenB by remember { mutableIntStateOf(5) }
    var lenC by remember { mutableIntStateOf(8) }

    fun message(
        label1: String, length1: Int, color1: Color,
        label2: String, length2: Int, color2: Color
    ): String {
        val equal = length1 == length2 && color1 == color2
        return if (equal) ss.stringsEqual.format(label1, label2)
        else ss.stringsDifferent.format(label1, label2)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StringRow(
            label = ss.labelA,
            length = lenA,
            color = colorA,
            onMinus = { if (lenA > 1) lenA-- },
            onPlus = { if (lenA < 10) lenA++ }
        )
        Spacer(Modifier.height(8.dp))
        StringRow(
            label = ss.labelB,
            length = lenB,
            color = colorB,
            onMinus = { if (lenB > 1) lenB-- },
            onPlus = { if (lenB < 10) lenB++ }
        )
        Spacer(Modifier.height(8.dp))
        StringRow(
            label = ss.labelC,
            length = lenC,
            color = colorC,
            onMinus = { if (lenC > 1) lenC-- },
            onPlus = { if (lenC < 10) lenC++ }
        )

        Spacer(Modifier.height(16.dp))

        val messages = listOf(
            message(ss.labelA, lenA, colorA, ss.labelB, lenB, colorB),
            message(ss.labelA, lenA, colorA, ss.labelC, lenC, colorC),
            message(ss.labelB, lenB, colorB, ss.labelC, lenC, colorC)
        )
        messages.forEach { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun StringRow(
    label: String,
    length: Int,
    color: Color,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(20.dp)
        )
        IconButton(onClick = onMinus, enabled = length > 1) {
            Icon(Icons.Default.Remove, contentDescription = null)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(16.dp)
                .background(color.copy(alpha = 0.20f), RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(length / 10f)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(8.dp))
            )
        }
        Text(
            text = length.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(28.dp)
        )
        IconButton(onClick = onPlus, enabled = length < 10) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }
}

@Composable
private fun AnswerSection(
    content: SMGameContent,
    onAnswered: (Int) -> Unit,
    wrongAnswerMessage: String,
    scoreDoubledMessage: String
) {
    val s = LocalUiStrings.current
    var selected by remember { mutableStateOf<String?>(null) }
    var hasSubmitted by remember { mutableStateOf(false) }
    var awarded by remember { mutableStateOf(false) }

    val correctAnswer = content.answer?.toString()?.lowercase()

    fun submit(answer: String) {
        selected = answer
        hasSubmitted = true
        if (!awarded) {
            awarded = true
            val points = if (answer == correctAnswer) content.score else content.score / 2
            onAnswered(points)
        }
    }

    Spacer(Modifier.height(16.dp))

    if (!hasSubmitted) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { submit("yes") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(s.common.yes)
            }
            Button(
                onClick = { submit("no") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(s.common.no)
            }
        }
    } else {
        val isCorrect = selected == correctAnswer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = if (isCorrect) "\uD83C\uDF89 ${s.common.correct} \uD83C\uDF89" else wrongAnswerMessage,
                color = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            if (isCorrect) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = scoreDoubledMessage,
                    color = Color(0xFF2E7D32),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SourcesMenu(sources: List<HTSource>, modifier: Modifier = Modifier) {
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
            onDismissRequest = { activeSource = null }
        ) {
            activeSource?.let { source ->
                val url = sourceUrl(source.page)
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
