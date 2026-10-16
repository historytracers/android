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
import com.historytracers.app.ui.features.measuringTwoDirectionsScreenStringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile

private const val SMARTPHONE_GAME_FILE_TWO = "add8bb98-aa8c-4373-ad82-3c22b9bf945e"
private const val SECTION_ID_TWO = "measuring_two_directions"
private const val HISTORYTRACERS_ORIGIN_TWO = "https://www.historytracers.org/"

private const val INTRO_ID_TWO = "405d1952-f32c-4b24-8e17-5ce1b4076c66"
private const val STEP_BY_STEP_ID_TWO = "deead311-df32-4b5e-9995-175f732b879f"
private const val EXAMPLE_ID_TWO = "35b25e72-c6ff-471e-948a-f39d31513af4"
private const val THINKING_ID_TWO = "49878049-6282-4917-95d3-e3b1a9ffc550"
private const val COMPARING_ID_TWO = "6a69817b-3b2e-40d3-9fb4-a02d28e666aa"
private const val PRACTICE_ID_TWO = "c6038674-67ac-41ce-b1e2-938746e02bd3"
private const val CONCLUSION_ID_TWO = "3746e6db-7e99-4886-b182-b1de80658a29"

@Composable
fun MeasuringTwoDirectionsIntroScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MeasuringTwoDirectionsGameContent(
        contentId = INTRO_ID_TWO,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MeasuringTwoDirectionsStepByStepScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MeasuringTwoDirectionsGameContent(
        contentId = STEP_BY_STEP_ID_TWO,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MeasuringTwoDirectionsExampleScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MeasuringTwoDirectionsGameContent(
        contentId = EXAMPLE_ID_TWO,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MeasuringTwoDirectionsThinkingScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MeasuringTwoDirectionsGameContent(
        contentId = THINKING_ID_TWO,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MeasuringTwoDirectionsComparingScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MeasuringTwoDirectionsGameContent(
        contentId = COMPARING_ID_TWO,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MeasuringTwoDirectionsPracticeScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MeasuringTwoDirectionsGameContent(
        contentId = PRACTICE_ID_TWO,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MeasuringTwoDirectionsConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToMeasuring: () -> Unit = {}
) {
    MeasuringTwoDirectionsGameContent(
        contentId = CONCLUSION_ID_TWO,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateToMeasuring = onNavigateToMeasuring
    )
}

private fun smileEmojiTwo(smile: String): String = when (smile) {
    "thinking", "think" -> "\uD83E\uDD14"
    "happy", "smile" -> "\uD83D\uDE0A"
    "nerd" -> "\uD83E\uDD13"
    "shocking", "surprise", "surprising" -> "\uD83D\uDE32"
    "party" -> "\uD83E\uDD73"
    "inlove", "loving" -> "\uD83D\uDE0D"
    else -> "\uD83D\uDE0A"
}

private fun sourceUrlTwo(page: String): String =
    if (page.startsWith("index.html")) HISTORYTRACERS_ORIGIN_TWO + page else page

@Composable
private fun MeasuringTwoDirectionsGameContent(
    contentId: String,
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToMeasuring: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = measuringTwoDirectionsScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }
    var game by remember { mutableStateOf<SMGameFile?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        when (val result = repo.loadAndParse("$language/$SMARTPHONE_GAME_FILE_TWO")) {
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
                preferences.markMeasuringSectionCompleted(SECTION_ID_TWO)
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
                        EXAMPLE_ID_TWO -> {
                            Spacer(Modifier.height(8.dp))
                            RulerWithSquare()
                        }
                        PRACTICE_ID_TWO -> {
                            Spacer(Modifier.height(8.dp))
                            ComparingRectanglesApp()
                        }
                    }

                    if (content.answer != null) {
                        AnswerSectionTwo(
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
                    text = smileEmojiTwo(node.smile),
                    fontSize = 40.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 8.dp, end = 8.dp)
                )
            }

            content?.sourceMenu?.takeIf { it.isNotEmpty() }?.let { sources ->
                SourcesMenuTwo(
                    sources = sources,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun RulerWithSquare(modifier: Modifier = Modifier) {
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
            .height(300.dp)
            .padding(horizontal = 12.dp)
    ) {
        textPaint.textSize = 12.dp.toPx()

        val w = size.width
        val h = size.height
        // Vertical ruler on the left, from 0 (bottom) to 10 (top).
        val units = 10
        val rulerLeft = w * 0.12f
        val rulerWidth = w * 0.22f
        val top = h * 0.05f
        val bottom = h * 0.95f
        val step = (bottom - top) / units

        // Ruler body.
        drawRoundRect(
            color = Color(0xFFF3E3B3),
            topLeft = Offset(rulerLeft, top),
            size = Size(rulerWidth, bottom - top),
            cornerRadius = CornerRadius(6f)
        )
        drawRoundRect(
            color = Color(0xFF9E7B3A),
            topLeft = Offset(rulerLeft, top),
            size = Size(rulerWidth, bottom - top),
            cornerRadius = CornerRadius(6f),
            style = Stroke(width = 3f)
        )

        // Tick marks and numbers: 0 at the bottom, 10 at the top.
        for (i in 0..units) {
            val y = bottom - i * step
            val longTick = i % 5 == 0
            val tickWidth = if (longTick) rulerWidth * 0.65f else rulerWidth * 0.38f
            drawLine(
                color = Color(0xFF6D4C41),
                start = Offset(rulerLeft, y),
                end = Offset(rulerLeft + tickWidth, y),
                strokeWidth = if (longTick) 4f else 2f
            )
            drawContext.canvas.nativeCanvas.drawText(
                i.toString(),
                rulerLeft + rulerWidth + 14.dp.toPx(),
                y + 4.dp.toPx(),
                textPaint
            )
        }

        // The square: height 5, sitting on 0 and reaching 5.
        val squareLeft = rulerLeft + rulerWidth + 36.dp.toPx()
        val squareSize = 5 * step
        val squareTop = bottom - squareSize
        drawRoundRect(
            color = Color(0xFF2F6FB3),
            topLeft = Offset(squareLeft, squareTop),
            size = Size(squareSize, squareSize),
            cornerRadius = CornerRadius(4f)
        )
        drawRoundRect(
            color = Color(0xFF1B4A7A),
            topLeft = Offset(squareLeft, squareTop),
            size = Size(squareSize, squareSize),
            cornerRadius = CornerRadius(4f),
            style = Stroke(width = 3f)
        )
    }
}

@Composable
private fun ComparingRectanglesApp(modifier: Modifier = Modifier) {
    val ss = measuringTwoDirectionsScreenStringsForLanguage(LocalAppLanguage.current)
    val rectColor = Color(0xFF2F6FB3)
    val max = 5

    var len1 by remember { mutableIntStateOf(2) }
    var hei1 by remember { mutableIntStateOf(3) }
    var len2 by remember { mutableIntStateOf(4) }
    var hei2 by remember { mutableIntStateOf(1) }

    fun feedback(): String = when {
        len1 == len2 && hei1 == hei2 -> ss.rectsEqual.format(len1, hei1)
        len1 == len2 -> ss.rectsSameLengthDiffHeight.format(len1, hei1, hei2)
        hei1 == hei2 -> ss.rectsDiffLengthSameHeight.format(hei1, len1, len2)
        else -> ss.rectsDiffLengthDiffHeight.format(len1, len2, hei1, hei2)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RectangleBlock(
            label = ss.labelOne,
            length = len1,
            height = hei1,
            lengthName = ss.length,
            heightName = ss.height,
            color = rectColor,
            max = max,
            onLenMinus = { if (len1 > 1) len1-- },
            onLenPlus = { if (len1 < max) len1++ },
            onHeiMinus = { if (hei1 > 1) hei1-- },
            onHeiPlus = { if (hei1 < max) hei1++ }
        )
        Spacer(Modifier.height(16.dp))
        RectangleBlock(
            label = ss.labelTwo,
            length = len2,
            height = hei2,
            lengthName = ss.length,
            heightName = ss.height,
            color = rectColor,
            max = max,
            onLenMinus = { if (len2 > 1) len2-- },
            onLenPlus = { if (len2 < max) len2++ },
            onHeiMinus = { if (hei2 > 1) hei2-- },
            onHeiPlus = { if (hei2 < max) hei2++ }
        )
        Spacer(Modifier.height(16.dp))
        val isEqual = len1 == len2 && hei1 == hei2
        Text(
            text = feedback(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isEqual) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun RectangleBlock(
    label: String,
    length: Int,
    height: Int,
    lengthName: String,
    heightName: String,
    color: Color,
    max: Int,
    onLenMinus: () -> Unit,
    onLenPlus: () -> Unit,
    onHeiMinus: () -> Unit,
    onHeiPlus: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RectangleGrid(
                length = length,
                height = height,
                color = color,
                max = max
            )
            Spacer(Modifier.width(12.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DimControlSide(
                    name = lengthName,
                    value = length,
                    min = 1,
                    max = max,
                    onMinus = onLenMinus,
                    onPlus = onLenPlus
                )
                Spacer(Modifier.height(4.dp))
                DimControlSide(
                    name = heightName,
                    value = height,
                    min = 1,
                    max = max,
                    onMinus = onHeiMinus,
                    onPlus = onHeiPlus
                )
            }
        }
    }
}

@Composable
private fun RectangleGrid(
    length: Int,
    height: Int,
    color: Color,
    max: Int
) {
    Row(verticalAlignment = Alignment.Top) {
        // Vertical ruler: max at top, 1 at bottom.
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(top = 20.dp, end = 4.dp)
        ) {
            for (i in max downTo 1) {
                Text(
                    text = i.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.height(24.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Column(horizontalAlignment = Alignment.Start) {
            // Horizontal ruler on top.
            Row {
                for (i in 1..max) {
                    Text(
                        text = i.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            // Grid: rows top to bottom, filled from bottom-left.
            Column {
                for (row in 0 until max) {
                    Row {
                        for (col in 0 until max) {
                            val filled = (col < length) && (row >= (max - height))
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(1.dp)
                                    .background(
                                        if (filled) color else color.copy(alpha = 0.12f),
                                        RoundedCornerShape(3.dp)
                                    )
                            )
                        }
                    }
                }
            }
            // Horizontal ruler at the bottom.
            Row {
                for (i in 1..max) {
                    Text(
                        text = i.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DimControlSide(
    name: String,
    value: Int,
    min: Int,
    max: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onMinus,
                enabled = value > min,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = null)
            }
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(28.dp)
            )
            IconButton(
                onClick = onPlus,
                enabled = value < max,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    }
}

@Composable
private fun AnswerSectionTwo(
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
            val points = if (answer == correctAnswer) content.score else 1
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
private fun SourcesMenuTwo(sources: List<HTSource>, modifier: Modifier = Modifier) {
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
                val url = sourceUrlTwo(source.page)
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
