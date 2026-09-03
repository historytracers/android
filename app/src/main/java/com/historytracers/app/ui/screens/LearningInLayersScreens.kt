// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
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
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.learningInLayersScreenStringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile

private const val SMARTPHONE_GAME_FILE = "f71d5b32-0c99-44c8-bd41-be4527352acb"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"

@Composable
fun LearningInLayersIntroScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LearningInLayersGameContent(
        contentId = "aa6ed0c4-557d-4747-884d-9eeb4a5af2a9",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LearningInLayersToyScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LearningInLayersGameContent(
        contentId = "163a3e9d-c8b9-4af2-9a29-ded4ba0b16a6",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LearningInLayersStagesScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LearningInLayersGameContent(
        contentId = "659691a8-4075-449a-baa2-d6198a72926b",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LearningInLayersQuestionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LearningInLayersGameContent(
        contentId = "73823770-faa3-4112-a1eb-928cedcc53d9",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LearningInLayersPlayingScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LearningInLayersGameContent(
        contentId = "81808640-c7df-47c4-95cf-fbbfeca4c456",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LearningInLayersConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToFirstSteps: () -> Unit = {}
) {
    LearningInLayersGameContent(
        contentId = "c33765d4-e5ce-4384-9cbc-86a7768f790f",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateToFirstSteps = onNavigateToFirstSteps
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

private fun isMatryoshkaSvg(text: String?): Boolean =
    text?.contains("<svg viewBox=\"0 0 520 220\"") == true

@Composable
private fun MatryoshkaDolls(modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(520f / 220f)
    ) {
        val scale = size.width / 520f

        fun px(v: Float) = v * scale

        drawLine(
            color = Color(0xFF999999),
            start = Offset(px(20f), px(200f)),
            end = Offset(px(500f), px(200f)),
            strokeWidth = px(1.5f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(px(4f), px(2f)))
        )

        data class Doll(val tx: Float, val ty: Float, val sc: Float, val fill: Color, val stroke: Color)

        val dolls = listOf(
            Doll(95f, 85f, 1.6f, Color(0xFFc0392b), Color(0xFFa93226)),
            Doll(195f, 114f, 1.2f, Color(0xFF2980b9), Color(0xFF2471a3)),
            Doll(285f, 139f, 0.85f, Color(0xFF27ae60), Color(0xFF1e8449)),
            Doll(365f, 160f, 0.55f, Color(0xFF8e44ad), Color(0xFF7d3c98))
        )

        dolls.forEach { doll ->
            val k = doll.sc * scale
            val cx = px(doll.tx)
            val cy = px(doll.ty)

            val body = Path().apply {
                moveTo(cx, cy - 48f * k)
                cubicTo(cx + 14f * k, cy - 48f * k, cx + 28f * k, cy - 36f * k, cx + 28f * k, cy - 16f * k)
                cubicTo(cx + 28f * k, cy + 4f * k, cx + 26f * k, cy + 30f * k, cx + 20f * k, cy + 54f * k)
                lineTo(cx + 14f * k, cy + 72f * k)
                lineTo(cx - 14f * k, cy + 72f * k)
                lineTo(cx - 20f * k, cy + 54f * k)
                cubicTo(cx - 26f * k, cy + 30f * k, cx - 28f * k, cy + 4f * k, cx - 28f * k, cy - 16f * k)
                cubicTo(cx - 28f * k, cy - 36f * k, cx - 14f * k, cy - 48f * k, cx, cy - 48f * k)
                close()
            }
            drawPath(body, color = doll.fill)
            drawPath(body, color = doll.stroke, style = Stroke(width = 2f * k))

            drawOval(
                color = Color.White,
                topLeft = Offset(cx - 13f * k, cy - 35f * k),
                size = Size(26f * k, 30f * k)
            )

            val bow = Path().apply {
                moveTo(cx - 11f * k, cy - 34f * k)
                quadraticBezierTo(cx, cy - 42f * k, cx + 11f * k, cy - 34f * k)
            }
            drawPath(bow, color = Color(0xFFf1c40f), style = Stroke(width = 2.5f * k, cap = StrokeCap.Round))

            val smileLine = Path().apply {
                moveTo(cx - 15f * k, cy - 1f * k)
                quadraticBezierTo(cx, cy - 5f * k, cx + 15f * k, cy - 1f * k)
            }
            drawPath(smileLine, color = Color(0xFFf1c40f), style = Stroke(width = 2.5f * k, cap = StrokeCap.Round))

            drawCircle(Color(0xFF2c3e50), radius = 1.8f * k, center = Offset(cx - 5f * k, cy - 24f * k))
            drawCircle(Color(0xFF2c3e50), radius = 1.8f * k, center = Offset(cx + 5f * k, cy - 24f * k))

            drawCircle(Color(0x80FF9696), radius = 1.5f * k, center = Offset(cx - 10f * k, cy - 18f * k))
            drawCircle(Color(0x80FF9696), radius = 1.5f * k, center = Offset(cx + 10f * k, cy - 18f * k))

            val mouth = Path().apply {
                moveTo(cx - 3f * k, cy - 18f * k)
                quadraticBezierTo(cx, cy - 15f * k, cx + 3f * k, cy - 18f * k)
            }
            drawPath(mouth, color = Color(0xFF2c3e50), style = Stroke(width = 1.2f * k, cap = StrokeCap.Round))

            val sumText = textMeasurer.measure(
                text = "\u2211",
                style = TextStyle(
                    fontSize = (18f * k).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.Serif
                )
            )
            drawText(
                textLayoutResult = sumText,
                topLeft = Offset(
                    cx - sumText.size.width / 2f,
                    cy + 38f * k - sumText.size.height / 2f
                )
            )

            drawOval(
                color = Color(0xFFf1c40f),
                topLeft = Offset(cx - 14f * k, cy + 59f * k),
                size = Size(28f * k, 6f * k),
                style = Stroke(width = 1.5f * k)
            )
        }
    }
}

@Composable
private fun LearningInLayersGameContent(
    contentId: String,
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToFirstSteps: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = learningInLayersScreenStringsForLanguage(LocalAppLanguage.current)
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
            if (onNavigateToFirstSteps != null) {
                preferences.recordLessonCompletion()
                preferences.markFirstStepsSectionCompleted("learning_in_shells")
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
                        if (text == null) return@forEach
                        when {
                            text.format == "markdown" -> MarkdownText(text = text.text ?: "")
                            isMatryoshkaSvg(text.text) -> MatryoshkaDolls(
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            else -> TextRenderer(text = text, repo = repo)
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    if (content.answer != null) {
                        AnswerSection(
                            content = content,
                            onAnswered = { points -> award(points) }
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

                    if (onNavigateToFirstSteps != null) {
                        Spacer(Modifier.height(16.dp))
                        FilledTonalButton(
                            onClick = onNavigateToFirstSteps,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(hts.firstSteps, fontWeight = FontWeight.Bold)
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
private fun AnswerSection(
    content: SMGameContent,
    onAnswered: (Int) -> Unit
) {
    val s = LocalUiStrings.current
    val xs = learningInLayersScreenStringsForLanguage(LocalAppLanguage.current)
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
                text = if (isCorrect) "\uD83C\uDF89 ${s.common.correct} \uD83C\uDF89" else xs.wrongAnswerMessage,
                color = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            if (isCorrect) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = xs.scoreDoubledMessage,
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
                        uriHandler.openUri(url)
                    }
                )
            }
        }
    }
}
