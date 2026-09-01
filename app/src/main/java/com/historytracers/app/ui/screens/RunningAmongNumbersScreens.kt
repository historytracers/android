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
import androidx.compose.ui.graphics.toArgb
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
import com.historytracers.app.ui.components.DateUtils
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.ResponsiveImage
import com.historytracers.app.ui.components.TextRenderer
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.runningAmongNumbersScreenStringsForLanguage
import com.historytracers.common.HTDate
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile

private const val SMARTPHONE_GAME_FILE = "95358a78-9431-4959-9e31-f64826ddef91"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"

@Composable
fun RunningAmongNumbersIntroScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    RunningAmongNumbersGameContent(
        contentId = "dbd36a73-6a7b-4a76-9da5-08465f9fb4f9",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun RunningAmongNumbersTempleScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    RunningAmongNumbersGameContent(
        contentId = "ed939a9e-83b3-49fb-b52e-17b4e92817f3",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun RunningAmongNumbersReconstructionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    RunningAmongNumbersGameContent(
        contentId = "4345bc25-d6d7-4713-9426-d0054286260f",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun RunningAmongNumbersAddingScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    RunningAmongNumbersGameContent(
        contentId = "d04e95d9-def6-4bb8-91a7-5fcda6985e7b",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun RunningAmongNumbersQuestionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    RunningAmongNumbersGameContent(
        contentId = "daf9d61a-a207-4ed5-aaa9-dd662b2174f9",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun RunningAmongNumbersConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToRoadToSomewhere: () -> Unit = {}
) {
    RunningAmongNumbersGameContent(
        contentId = "50c6f3af-0f85-4663-9231-0b34e5083624",
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
    text?.contains("<img") == true

private fun isAdditionSvg(text: String?): Boolean =
    text?.contains("<svg") == true

private fun isCaptionHtml(text: String?): Boolean =
    text?.contains("<htcite") == true || text?.contains("<htdate") == true

private val TAG_STRIP_REGEX = Regex("""<[^>]+>""")
private val HTCITE_REGEX = Regex("""<htcite(\d+)>""")
private val HTCITE_PLAIN_REGEX = Regex("""<htcite\d+>""")

private fun resolveCaptionText(
    text: String,
    dates: List<HTDate>?,
    sources: List<HTSource>?
): String {
    var result = text
    DateUtils.formatDate(dates)?.forEachIndexed { index, formatted ->
        result = result.replace("<htdate$index>", formatted)
    }
    result = HTCITE_REGEX.replace(result) { m ->
        val index = m.groupValues[1].toIntOrNull() ?: return@replace ""
        sources?.getOrNull(index)?.text ?: ""
    }
    result = HTCITE_PLAIN_REGEX.replace(result, "")
    return TAG_STRIP_REGEX.replace(result, "").trim()
}

@Composable
private fun CaptionText(text: String, modifier: Modifier = Modifier) {
    if (text.isEmpty()) return
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.fillMaxWidth()
    )
}

private data class SvgText(val x: Float, val y: Float, val size: Float, val color: String, val value: String)
private data class SvgLine(val x1: Float, val y1: Float, val x2: Float, val y2: Float, val color: String, val width: Float)

private val SVG_DIM_REGEX = Regex("""<svg[^>]*width="(\d+)"[^>]*height="(\d+)"[^>]*>""")
private val SVG_TEXT_TAG_REGEX = Regex("""<text\s+([^>]*)>([^<]*)</text>""")
private val SVG_LINE_TAG_REGEX = Regex("""<line\s+([^>]*)/>""")
private val SVG_ATTR_REGEX = Regex("""([\w-]+)="([^"]*)"""")

private fun parseSvgAttrs(attrs: String): Map<String, String> =
    SVG_ATTR_REGEX.findAll(attrs).associate { it.groupValues[1] to it.groupValues[2] }

private fun parseSvgTexts(html: String): List<SvgText> {
    return SVG_TEXT_TAG_REGEX.findAll(html).map { m ->
        val attrs = parseSvgAttrs(m.groupValues[1])
        SvgText(
            x = attrs["x"]?.toFloatOrNull() ?: 0f,
            y = attrs["y"]?.toFloatOrNull() ?: 0f,
            size = attrs["font-size"]?.toFloatOrNull() ?: 26f,
            color = attrs["fill"] ?: "#2C3E50",
            value = m.groupValues[2]
        )
    }.toList()
}

private fun parseSvgLines(html: String): List<SvgLine> {
    return SVG_LINE_TAG_REGEX.findAll(html).map { m ->
        val attrs = parseSvgAttrs(m.groupValues[1])
        SvgLine(
            x1 = attrs["x1"]?.toFloatOrNull() ?: 0f,
            y1 = attrs["y1"]?.toFloatOrNull() ?: 0f,
            x2 = attrs["x2"]?.toFloatOrNull() ?: 0f,
            y2 = attrs["y2"]?.toFloatOrNull() ?: 0f,
            color = attrs["stroke"] ?: "#2C3E50",
            width = attrs["stroke-width"]?.toFloatOrNull() ?: 3f
        )
    }.toList()
}

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

private fun svgCaption(html: String): String {
    val svgEnd = html.indexOf("</svg>")
    if (svgEnd == -1) return ""
    return html.substring(svgEnd + 6)
        .replace("</p>", "")
        .replace("<p class=\"desc\">", "")
        .replace("<b>", "")
        .replace("</b>", "")
        .trim()
}

@Composable
private fun ColumnAddition(html: String, modifier: Modifier = Modifier) {
    val texts = remember(html) { parseSvgTexts(html) }
    val lines = remember(html) { parseSvgLines(html) }
    val caption = remember(html) { svgCaption(html) }
    val dimensions = remember(html) {
        val m = SVG_DIM_REGEX.find(html)
        (m?.groupValues?.get(1)?.toFloatOrNull() ?: 480f) to (m?.groupValues?.get(2)?.toFloatOrNull() ?: 185f)
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(dimensions.first / dimensions.second)
        ) {
            val s = size.width / dimensions.first

            val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
            }
            lines.forEach { line ->
                linePaint.color = parseHexColor(line.color).toInt()
                linePaint.strokeWidth = line.width * s
                drawContext.canvas.nativeCanvas.drawLine(
                    line.x1 * s, line.y1 * s, line.x2 * s, line.y2 * s, linePaint
                )
            }

            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textAlign = Paint.Align.CENTER
                typeface = android.graphics.Typeface.MONOSPACE
            }
            texts.forEach { text ->
                textPaint.color = parseHexColor(text.color).toInt()
                textPaint.textSize = text.size * s
                drawContext.canvas.nativeCanvas.drawText(text.value, text.x * s, text.y * s, textPaint)
            }
        }

        if (caption.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RunningAmongNumbersGameContent(
    contentId: String,
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToRoadToSomewhere: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = runningAmongNumbersScreenStringsForLanguage(LocalAppLanguage.current)
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

    LaunchedEffect(content) {
        val node = content
        if (node != null && !arrivalHandled) {
            arrivalHandled = true
            award(node.score)
            if (onNavigateToRoadToSomewhere != null) {
                preferences.markRoadToSomewhereSectionCompleted("running_among_numbers")
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
                        when {
                            isImgHtml(text.text) -> ResponsiveImage(
                                html = text.text ?: "",
                                imgDesc = text.imgdesc
                            )
                            isAdditionSvg(text.text) -> ColumnAddition(
                                html = text.text ?: "",
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            isCaptionHtml(text.text) -> CaptionText(
                                text = resolveCaptionText(text.text ?: "", text.fillDates, text.source)
                            )
                            text.format?.contains("markdown") == true -> MarkdownText(text = text.text ?: "")
                            else -> TextRenderer(text = text, repo = repo)
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    if (content.answer != null) {
                        RunningAnswerSection(
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
                RunningSourcesMenu(
                    sources = sources,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun RunningAnswerSection(
    content: SMGameContent,
    onAnswered: (Int) -> Unit
) {
    val s = LocalUiStrings.current
    val xs = runningAmongNumbersScreenStringsForLanguage(LocalAppLanguage.current)
    var selected by remember { mutableStateOf<String?>(null) }
    var hasSubmitted by remember { mutableStateOf(false) }
    var awarded by remember { mutableStateOf(false) }

    val correctAnswer = when (val answer = content.answer) {
        is Boolean -> answer
        is String -> answer.equals("yes", ignoreCase = true)
        else -> null
    }

    fun submit(answer: String) {
        selected = answer
        hasSubmitted = true
        if (!awarded) {
            awarded = true
            val answeredCorrectly = (answer == "yes") == correctAnswer
            val points = if (answeredCorrectly) content.score else content.score / 2
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
        val isCorrect = (selected == "yes") == correctAnswer
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
private fun RunningSourcesMenu(sources: List<HTSource>, modifier: Modifier = Modifier) {
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
