// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
import android.graphics.Path
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
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
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.orderOfAdditionScreenStringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val SMARTPHONE_GAME_FILE = "687a38b5-de0e-4d16-a4d0-8ec87a545ccb"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"

@Composable
fun OrderOfAdditionIntroScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    OrderOfAdditionGameContent(
        contentId = "e6b831de-1aa5-45b6-b75a-2c2fb5cf395e",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun OrderOfAdditionWhereScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    OrderOfAdditionGameContent(
        contentId = "3c4de149-3d19-4695-9282-e30e3dcdba24",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun OrderOfAdditionCommutativeScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    OrderOfAdditionGameContent(
        contentId = "649d5a22-f27a-454f-a8f0-3cbeae630186",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun OrderOfAdditionQuestionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    OrderOfAdditionGameContent(
        contentId = "f389d75c-c708-4ea5-85bd-a54a81c6661b",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun OrderOfAdditionConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToRoadToSomewhere: () -> Unit = {}
) {
    OrderOfAdditionGameContent(
        contentId = "bc8091be-beba-4c48-bd41-0c636583de6f",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateToRoadToSomewhere = onNavigateToRoadToSomewhere
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

private fun isImgHtml(text: String?): Boolean =
    text?.startsWith("<img") == true

private fun isAxisSvg(text: String?): Boolean =
    text?.contains("<svg") == true && text.contains("marker-end")

private fun isFruitsSvg(text: String?): Boolean =
    text?.contains("<svg") == true && text.contains("<circle")

private data class OrderOfAdditionAxisLine(val x1: Float, val y1: Float, val x2: Float, val y2: Float, val hasArrow: Boolean)

private data class OrderOfAdditionAxisText(val x: Float, val y: Float, val label: String)

private val AXIS_LINE_REGEX = Regex("""<line\s+x1="([\d.]+)"\s+y1="([\d.]+)"\s+x2="([\d.]+)"\s+y2="([\d.]+)"([^>]*)>""")
private val AXIS_TEXT_REGEX = Regex("""<text\s+x="([\d.]+)"\s+y="([\d.]+)"[^>]*>([^<]+)</text>""")

private fun parseAxisLines(html: String): List<OrderOfAdditionAxisLine> {
    return AXIS_LINE_REGEX.findAll(html).map { m ->
        OrderOfAdditionAxisLine(
            m.groupValues[1].toFloat(),
            m.groupValues[2].toFloat(),
            m.groupValues[3].toFloat(),
            m.groupValues[4].toFloat(),
            m.groupValues[5].contains("marker-end")
        )
    }.toList()
}

private fun parseAxisTexts(html: String): List<OrderOfAdditionAxisText> {
    return AXIS_TEXT_REGEX.findAll(html).map { m ->
        OrderOfAdditionAxisText(m.groupValues[1].toFloat(), m.groupValues[2].toFloat(), m.groupValues[3])
    }.toList()
}

private fun axisCaption(html: String): String {
    val svgEnd = html.indexOf("</svg>")
    if (svgEnd == -1) return ""
    return html.substring(svgEnd + 6)
        .replace("</p>", "")
        .replace("<p class=\"desc\">", "")
        .replace("<b>", "")
        .replace("</b>", "")
        .trim()
        .trim('"')
}

@Composable
private fun NumberAxis(html: String, modifier: Modifier = Modifier) {
    val lines = remember(html) { parseAxisLines(html) }
    val texts = remember(html) { parseAxisTexts(html) }
    val caption = remember(html) { axisCaption(html) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(450f / 100f)
        ) {
            val s = size.width / 450f

            val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 3f * s
                strokeCap = Paint.Cap.ROUND
            }
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.BLACK
                style = Paint.Style.FILL
            }
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.BLACK
                textSize = 24f * s
                textAlign = Paint.Align.CENTER
            }

            lines.forEach { line ->
                drawContext.canvas.nativeCanvas.drawLine(
                    line.x1 * s, line.y1 * s, line.x2 * s, line.y2 * s, linePaint
                )
                if (line.hasArrow) {
                    val arrow = Path().apply {
                        moveTo(line.x2 * s, line.y2 * s - 6f * s)
                        lineTo(line.x2 * s + 10f * s, line.y2 * s)
                        lineTo(line.x2 * s, line.y2 * s + 6f * s)
                        close()
                    }
                    drawContext.canvas.nativeCanvas.drawPath(arrow, fillPaint)
                }
            }

            texts.forEach { text ->
                drawContext.canvas.nativeCanvas.drawText(text.label, text.x * s, text.y * s, textPaint)
            }
        }

        if (caption.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private val FRUIT_CIRCLE_REGEX = Regex("""<circle\s+cx="([\d.]+)"\s+cy="([\d.]+)"\s+r="([\d.]+)"\s+fill="([^"]*)"\s*/>""")
private val FRUIT_TEXT_REGEX = Regex("""<text\s+x="([\d.]+)"\s+y="([\d.]+)"([^>]*)>([^<]+)</text>""")

private data class FruitCircle(val cx: Float, val cy: Float, val r: Float, val fill: Long)

private data class FruitText(val x: Float, val y: Float, val label: String, val size: Float, val fill: Long)

private val HEX_COLOR_REGEX = Regex("^#?([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$")

private fun parseHexColor(hex: String): Long {
    val match = HEX_COLOR_REGEX.matchEntire(hex.trim()) ?: return 0xFF2C3E50L
    val digits = match.groupValues[1]
    val expanded = if (digits.length == 3) {
        digits.map { "$it$it" }.joinToString("")
    } else {
        digits
    }
    return 0xFF000000L or expanded.toLong(16)
}

private fun parseFruitCircles(html: String): List<FruitCircle> {
    return FRUIT_CIRCLE_REGEX.findAll(html).map { m ->
        FruitCircle(
            cx = m.groupValues[1].toFloat(),
            cy = m.groupValues[2].toFloat(),
            r = m.groupValues[3].toFloat(),
            fill = parseHexColor(m.groupValues[4])
        )
    }.toList()
}

private fun parseFruitTexts(html: String): List<FruitText> {
    return FRUIT_TEXT_REGEX.findAll(html).map { m ->
        val attrs = m.groupValues[3]
        val sizeMatch = Regex("""font-size="([\d.]+)""").find(attrs)
        val fillMatch = Regex("""fill="(#?[0-9a-fA-F]{3}|#?[0-9a-fA-F]{6})""").find(attrs)
        FruitText(
            x = m.groupValues[1].toFloat(),
            y = m.groupValues[2].toFloat(),
            label = m.groupValues[4],
            size = sizeMatch?.groupValues?.get(1)?.toFloat() ?: 22f,
            fill = if (fillMatch != null) parseHexColor(fillMatch.groupValues[1]) else 0xFF2C3E50L
        )
    }.toList()
}

private data class FruitBounds(val minX: Float, val minY: Float, val maxX: Float, val maxY: Float) {
    val width: Float get() = maxX - minX
    val height: Float get() = maxY - minY
}

private fun computeFruitBounds(circles: List<FruitCircle>, texts: List<FruitText>): FruitBounds {
    var minX = Float.MAX_VALUE
    var minY = Float.MAX_VALUE
    var maxX = Float.MIN_VALUE
    var maxY = Float.MIN_VALUE
    circles.forEach { c ->
        minX = minOf(minX, c.cx - c.r)
        maxX = maxOf(maxX, c.cx + c.r)
        minY = minOf(minY, c.cy - c.r)
        maxY = maxOf(maxY, c.cy + c.r)
    }
    texts.forEach { t ->
        val halfWidth = t.size * t.label.length * 0.55f / 2f
        minX = minOf(minX, t.x - halfWidth)
        maxX = maxOf(maxX, t.x + halfWidth)
        minY = minOf(minY, t.y - t.size)
        maxY = maxOf(maxY, t.y)
    }
    return FruitBounds(minX, minY, maxX, maxY)
}

@Composable
private fun FruitsDiagram(html: String, modifier: Modifier = Modifier) {
    val circles = remember(html) { parseFruitCircles(html) }
    val texts = remember(html) { parseFruitTexts(html) }
    val caption = remember(html) { axisCaption(html) }
    val bounds = remember(circles, texts) { computeFruitBounds(circles, texts) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (bounds.width > 0f && bounds.height > 0f) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(bounds.width / bounds.height)
            ) {
                val s = size.width / bounds.width
                val dx = -bounds.minX * s
                val dy = -bounds.minY * s

                circles.forEach { circle ->
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = circle.fill.toInt()
                        style = Paint.Style.FILL
                    }
                    drawContext.canvas.nativeCanvas.drawCircle(
                        circle.cx * s + dx, circle.cy * s + dy, circle.r * s, paint
                    )
                }

                texts.forEach { text ->
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = text.fill.toInt()
                        textSize = text.size * s
                        textAlign = Paint.Align.CENTER
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        text.label, text.x * s + dx, text.y * s + dy, paint
                    )
                }
            }
        }

        if (caption.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OrderOfAdditionGameContent(
    contentId: String,
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToRoadToSomewhere: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = orderOfAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }
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
    val scope = rememberCoroutineScope()
    val awardedScreens by preferences.awardedScreens.collectAsState(initial = emptySet())

    LaunchedEffect(content) {
        val node = content
        if (node != null && !arrivalHandled) {
            arrivalHandled = true
            if (onNavigateToRoadToSomewhere != null) {
                preferences.markRoadToSomewhereSectionCompleted("order_of_addition")
                preferences.recordLessonCompletion()
            }
            val alreadyScored = preferences.awardedScreens.first().contains(node.id)
            if (alreadyScored) return@LaunchedEffect
            if (node.answer == null) {
                award(1)
                preferences.markScreenAwarded(node.id)
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
                            isImgHtml(text.text) -> ResponsiveImage(
                                html = text.text ?: "",
                                imgDesc = text.imgdesc
                            )
                            text.format?.contains("markdown") == true -> MarkdownText(text = text.text ?: "")
                            isAxisSvg(text.text) -> NumberAxis(
                                html = text.text ?: "",
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            isFruitsSvg(text.text) -> FruitsDiagram(
                                html = text.text ?: "",
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            else -> TextRenderer(text = text, repo = repo)
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    if (content.answer != null) {
                        AnswerSection(
                            content = content,
                            onAnswered = { points ->
                                if (content.id !in awardedScreens) {
                                    award(points)
                                    scope.launch { preferences.markScreenAwarded(content.id) }
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
    val xs = orderOfAdditionScreenStringsForLanguage(LocalAppLanguage.current)
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
