// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
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
import com.historytracers.app.ui.components.ResponsiveImage
import com.historytracers.app.ui.components.TextRenderer
import com.historytracers.app.ui.components.drawYupanaBackground
import com.historytracers.app.ui.components.drawYupanaFrame
import com.historytracers.app.ui.components.drawYupanaRow
import com.historytracers.app.ui.components.getMarkersForDigit
import com.historytracers.app.ui.features.largeNumbersScreenStringsForLanguage
import com.historytracers.app.ui.features.yupanaSharedStringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile
import kotlinx.coroutines.launch

private const val SMARTPHONE_GAME_FILE = "2a1fc4cb-1047-48d0-be47-aa9c5d586430"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"

private const val INTRO_CONTENT_ID = "d3a26877-2ff4-4f8f-8ef8-ae39ee1de758"
private const val QUIPU_CONTENT_ID = "861e3e84-c579-451b-8621-b3550378f913"
private const val GROWING_CONTENT_ID = "d1b2a423-eba1-4e55-93fc-48a15142d23f"
private const val PATTERN_CONTENT_ID = "d3fbc16b-dd85-42b2-8f62-dacf76c584aa"
private const val APP_CONTENT_ID = "fd9bdaab-7b41-4237-8e9d-baf459c4ced4"
private const val CONCLUSION_CONTENT_ID = "b4862c32-8691-471c-98d6-bedb4705c1cb"

private const val QUIPU_MARKER = "data-custom=\"quipu-45\""
private const val YUPANA_APP_MARKER = "data-custom=\"yupana-app\""

@Composable
fun LargeNumbersIntroScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LargeNumbersGameContent(
        contentId = INTRO_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LargeNumbersQuipuScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LargeNumbersGameContent(
        contentId = QUIPU_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LargeNumbersGrowingScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LargeNumbersGameContent(
        contentId = GROWING_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LargeNumbersPatternScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LargeNumbersGameContent(
        contentId = PATTERN_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LargeNumbersAppScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LargeNumbersGameContent(
        contentId = APP_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LargeNumbersConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToYupana: () -> Unit = {}
) {
    LargeNumbersGameContent(
        contentId = CONCLUSION_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateToYupana = onNavigateToYupana
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

private fun isQuipuMarker(text: String?): Boolean = text?.contains(QUIPU_MARKER) == true

private fun isYupanaAppMarker(text: String?): Boolean = text?.contains(YUPANA_APP_MARKER) == true

private fun hasImgSrc(text: String?): Boolean =
    text?.contains("<img") == true

@Composable
private fun QuipuFortyFive(modifier: Modifier = Modifier) {
    val xs = largeNumbersScreenStringsForLanguage(LocalAppLanguage.current)
    val cordColor = Color(0xFF8B5E3C)
    val knotColor = Color(0xFFC0392B)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.4f)
        ) {
            val w = size.width
            val h = size.height
            val mainY = h * 0.12f
            val cx = w * 0.5f
            val cordBottom = h * 0.88f
            val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = labelColor
                textSize = h * 0.05f
                textAlign = Paint.Align.RIGHT
            }

            drawLine(
                color = cordColor,
                start = Offset(w * 0.08f, mainY),
                end = Offset(w * 0.92f, mainY),
                strokeWidth = h * 0.03f
            )
            drawLine(
                color = cordColor,
                start = Offset(cx, mainY),
                end = Offset(cx, cordBottom),
                strokeWidth = h * 0.014f
            )

            val knotRadius = h * 0.028f
            val tensStart = h * 0.28f
            val tensGap = h * 0.055f
            repeat(4) { i ->
                val y = tensStart + i * tensGap
                drawCircle(color = knotColor, radius = knotRadius, center = Offset(cx, y))
                drawCircle(
                    color = Color.Black.copy(alpha = 0.2f),
                    radius = knotRadius,
                    center = Offset(cx, y),
                    style = Stroke(width = h * 0.004f)
                )
            }

            val unitsStart = h * 0.62f
            val unitsGap = h * 0.055f
            repeat(5) { i ->
                val y = unitsStart + i * unitsGap
                drawCircle(color = knotColor, radius = knotRadius, center = Offset(cx, y))
                drawCircle(
                    color = Color.Black.copy(alpha = 0.2f),
                    radius = knotRadius,
                    center = Offset(cx, y),
                    style = Stroke(width = h * 0.004f)
                )
            }

            val tensCenter = tensStart + (3 * tensGap) / 2f
            val unitsCenter = unitsStart + (4 * unitsGap) / 2f
            drawContext.canvas.nativeCanvas.drawText(xs.tens, cx - knotRadius * 3.5f, tensCenter, labelPaint)
            drawContext.canvas.nativeCanvas.drawText(xs.units, cx - knotRadius * 3.5f, unitsCenter, labelPaint)
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "45",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun YupanaLargeNumbersApp(modifier: Modifier = Modifier) {
    val xs = largeNumbersScreenStringsForLanguage(LocalAppLanguage.current)
    val ys = yupanaSharedStringsForLanguage(LocalAppLanguage.current)
    val s = LocalUiStrings.current
    var value by remember { mutableIntStateOf(0) }

    val tens = value / 10
    val units = value % 10

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "${xs.tens}: $tens    ${xs.units}: $units",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
        ) {
            val margin = 3f / 860f * size.width
            val usableWidth = size.width - 2f * margin
            val colWidth = usableWidth / 4f
            val rowHeight = (size.height - 6f / 480f * size.height) / 2f
            val startX = margin
            val startY = 3f / 480f * size.height

            drawYupanaBackground(size)
            drawYupanaFrame(size)

            drawYupanaRow(
                cellOriginX = startX,
                cellOriginY = startY + rowHeight,
                cellWidth = colWidth,
                cellHeight = rowHeight,
                canvasSize = size,
                leftMarkers = getMarkersForDigit(units)
            )
            drawYupanaRow(
                cellOriginX = startX,
                cellOriginY = startY,
                cellWidth = colWidth,
                cellHeight = rowHeight,
                canvasSize = size,
                leftMarkers = getMarkersForDigit(tens)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "$value",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = { if (value > 0) value-- },
                enabled = value > 0,
                modifier = Modifier.size(56.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = s.common.previous)
            }
            FilledIconButton(
                onClick = { if (value < 99) value++ },
                enabled = value < 99,
                modifier = Modifier.size(56.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = s.common.next)
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = ys.yupana,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LargeNumbersGameContent(
    contentId: String,
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToYupana: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = largeNumbersScreenStringsForLanguage(LocalAppLanguage.current)
    val ys = yupanaSharedStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    var game by remember { mutableStateOf<SMGameFile?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(language) {
        game = null
        error = null
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

    var arrivalHandled by remember(contentId) { mutableStateOf(false) }
    var answeredHandled by remember(contentId) { mutableStateOf(false) }

    LaunchedEffect(content) {
        val node = content
        if (node != null && !arrivalHandled) {
            arrivalHandled = true
            award(node.score)
            if (onNavigateToYupana != null) {
                preferences.markYupanaSectionCompleted("large_numbers")
                preferences.recordLessonCompletion()
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
                        val html = text.text ?: ""
                        when {
                            isQuipuMarker(html) -> QuipuFortyFive()
                            isYupanaAppMarker(html) -> YupanaLargeNumbersApp()
                            text.format?.contains("markdown") == true -> MarkdownText(text = html)
                            hasImgSrc(html) -> ResponsiveImage(html = html, imgDesc = text.imgdesc)
                            else -> TextRenderer(text = text, repo = repo)
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    if (content.answer != null) {
                        LargeNumbersAnswerSection(
                            content = content,
                            onAnswered = { points ->
                                if (!answeredHandled) {
                                    answeredHandled = true
                                    award(points)
                                }
                            }
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

                    if (onNavigateToYupana != null) {
                        Spacer(Modifier.height(16.dp))
                        FilledTonalButton(
                            onClick = onNavigateToYupana,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = ys.yupana, fontWeight = FontWeight.Bold)
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
                LargeNumbersSourcesMenu(
                    sources = sources,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun LargeNumbersAnswerSection(
    content: SMGameContent,
    onAnswered: (Int) -> Unit
) {
    val s = LocalUiStrings.current
    val xs = largeNumbersScreenStringsForLanguage(LocalAppLanguage.current)
    var selected by remember { mutableStateOf<String?>(null) }
    var hasSubmitted by remember { mutableStateOf(false) }

    val correctAnswer = content.answer?.toString()?.lowercase()

    fun submit(answer: String) {
        selected = answer
        hasSubmitted = true
        val points = if (answer == correctAnswer) content.score else content.score / 2
        onAnswered(points)
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
private fun LargeNumbersSourcesMenu(sources: List<HTSource>, modifier: Modifier = Modifier) {
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
            onDismissRequest = { showSourcesMenu = false; activeSource = null }
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
