// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.DashPathEffect
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
import androidx.compose.ui.geometry.Offset
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
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.ResponsiveImage
import com.historytracers.app.ui.components.TextRenderer
import com.historytracers.app.ui.components.drawHandNumbers
import com.historytracers.app.ui.components.drawOneHand
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.walkAmongNumbersScreenStringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile

private const val SMARTPHONE_GAME_FILE = "a4e7b3c1-8f2d-4a5e-9b6c-1d3e5f7a9b0c"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"

@Composable
fun WalkAmongNumbersIntroScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    WalkAmongNumbersGameContent(
        contentId = "b1a2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun WalkAmongNumbersRoadsScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    WalkAmongNumbersGameContent(
        contentId = "c2b3a4d5-f6e7-4b8c-9d0e-1f2a3b4c5d6e",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun WalkAmongNumbersAxesScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    WalkAmongNumbersGameContent(
        contentId = "d3c4b5a6-e7f8-4c9d-0e1f-2a3b4c5d6e7f",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun WalkAmongNumbersIncaRoadsScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    WalkAmongNumbersGameContent(
        contentId = "b76b8c5f-4479-4b74-9556-9d722b4a4ed6",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun WalkAmongNumbersHandsScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    WalkAmongNumbersGameContent(
        contentId = "e4d5c6b7-f8e9-4d0e-1f2a-3b4c5d6e7f80",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun WalkAmongNumbersQuestionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    WalkAmongNumbersGameContent(
        contentId = "f5e6d7c8-09fa-4e1f-2a3b-4c5d6e7f8091",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun WalkAmongNumbersConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToRoadToSomewhere: () -> Unit = {}
) {
    WalkAmongNumbersGameContent(
        contentId = "06f7e8d9-1a0b-4f2c-3d4e-5f6a7b8c9d0e",
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

private fun isHandsSvg(text: String?): Boolean =
    text?.contains("<svg") == true && text.contains("hand-shape")

private fun isRoadSvg(text: String?): Boolean =
    text?.contains("<svg") == true && text.contains("class=\"road\"")

private fun buildHandPath(): Path {
    return Path().apply {
        moveTo(-268.1f, 338f)
        rLineTo(21.7f, -21.7f)
        rCubicTo(2.3f, -2.3f, 3.5f, -5.3f, 3.5f, -8.5f)
        rLineTo(0f, -55.7f)
        rCubicTo(0f, -5.6f, 2.2f, -10.9f, 6.2f, -14.9f)
        rLineTo(32.4f, -32.4f)
        rCubicTo(2.1f, -2.1f, 5.8f, -1.8f, 7.4f, 0.8f)
        rCubicTo(2.8f, 4.4f, 5f, 11.4f, -1.4f, 18.9f)
        rCubicTo(-5.1f, 5.9f, -10.3f, 10.9f, -13.7f, 14.1f)
        rCubicTo(-2.2f, 2f, -2.2f, 5.4f, -0.1f, 7.5f)
        rCubicTo(2f, 2f, 5.3f, 2f, 7.3f, 0f)
        rLineTo(87.6f, -87.6f)
        rCubicTo(4.6f, -4.6f, 12.2f, -4.6f, 16.8f, 0f)
        rCubicTo(4.6f, 4.6f, 4.6f, 12.2f, 0f, 16.8f)
        rLineTo(-66.2f, 66.2f)
        rCubicTo(-2.4f, 2.4f, -2.4f, 6.4f, 0f, 8.8f)
        rCubicTo(2.4f, 2.4f, 6.4f, 2.4f, 8.8f, 0f)
        rLineTo(74.5f, -75.3f)
        rCubicTo(4.6f, -4.7f, 12.2f, -4.7f, 16.9f, 0f)
        rLineTo(1.8f, 1.8f)
        rCubicTo(4.6f, 4.6f, 4.6f, 12.1f, 0.1f, 16.8f)
        rLineTo(-70.4f, 71.3f)
        rCubicTo(-2.2f, 2.3f, -2.2f, 5.9f, 0f, 8.2f)
        rCubicTo(2.3f, 2.3f, 5.9f, 2.3f, 8.2f, 0f)
        rLineTo(61.2f, -61.2f)
        rCubicTo(4.6f, -4.6f, 12.2f, -4.6f, 16.8f, 0f)
        rLineTo(0.2f, 0.2f)
        rCubicTo(4.6f, 4.6f, 4.6f, 12.2f, 0f, 16.8f)
        rLineTo(-67.1f, 67.1f)
        rCubicTo(-1.7f, 1.7f, -1.7f, 4.6f, 0f, 6.3f)
        rCubicTo(1.7f, 1.7f, 4.6f, 1.7f, 6.3f, 0f)
        rLineTo(50.7f, -50.7f)
        rCubicTo(3.9f, -3.9f, 10.1f, -3.9f, 13.9f, 0f)
        rCubicTo(3.9f, 3.9f, 3.9f, 10.1f, 0f, 13.9f)
        rLineTo(-98.2f, 98.2f)
        close()
    }
}

private data class AxisLine(val x1: Float, val y1: Float, val x2: Float, val y2: Float, val hasArrow: Boolean)

private data class AxisText(val x: Float, val y: Float, val label: String)

private val AXIS_LINE_REGEX = Regex("""<line\s+x1="([\d.]+)"\s+y1="([\d.]+)"\s+x2="([\d.]+)"\s+y2="([\d.]+)"([^>]*)>""")
private val AXIS_TEXT_REGEX = Regex("""<text\s+x="([\d.]+)"\s+y="([\d.]+)"[^>]*>([^<]+)</text>""")

private fun parseAxisLines(html: String): List<AxisLine> {
    return AXIS_LINE_REGEX.findAll(html).map { m ->
        AxisLine(
            m.groupValues[1].toFloat(),
            m.groupValues[2].toFloat(),
            m.groupValues[3].toFloat(),
            m.groupValues[4].toFloat(),
            m.groupValues[5].contains("marker-end")
        )
    }.toList()
}

private fun parseAxisTexts(html: String): List<AxisText> {
    return AXIS_TEXT_REGEX.findAll(html).map { m ->
        AxisText(m.groupValues[1].toFloat(), m.groupValues[2].toFloat(), m.groupValues[3])
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

@Composable
private fun HandsPair(modifier: Modifier = Modifier) {
    val handPath = remember { buildHandPath() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f)
    ) {
        val scale = size.width / 600f
        val cx = size.width / 2f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color(0xFFF4C2A1).toArgb()
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL
        }
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.CENTER
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }

        val handScale = 0.55f * scale
        val handCy = 120f * scale
        val leftCx = cx - 100.dp.toPx()
        val rightCx = cx + 100.dp.toPx()

        drawOneHand(leftCx, handCy, handScale, isLeft = true, paint, handPath)
        drawOneHand(rightCx, handCy, handScale, isLeft = false, paint, handPath)

        val rightNums = (1..5).map { it to (5 - it) }
        val numberExtraOffsets = mapOf(
            1 to Offset(5f, 0f),
            2 to Offset(5f, 0f),
        )
        drawHandNumbers(
            numbers = rightNums,
            cx = rightCx, cy = handCy, handScale = handScale, isLeft = false,
            textPaint = textPaint, strokePaint = strokePaint,
            extraOffsets = numberExtraOffsets
        )
    }
}

private val ROAD_PATH_REGEX = Regex("""<path\s+d="([^"]*)"\s+fill="none"\s+stroke="([^"]*)"\s+stroke-width="([\d.]+)"([^>]*)""")
private val ROAD_CIRCLE_REGEX = Regex("""<circle\s+cx="([\d.]+)"\s+cy="([\d.]+)"\s+r="([\d.]+)"\s+fill="([^"]*)"\s*/>""")

private data class RoadPathData(
    val d: String,
    val stroke: String,
    val width: Float,
    val dashed: Boolean
)

private fun parseRoadPaths(html: String): List<RoadPathData> {
    return ROAD_PATH_REGEX.findAll(html).map { m ->
        RoadPathData(
            d = m.groupValues[1],
            stroke = m.groupValues[2],
            width = m.groupValues[3].toFloat(),
            dashed = m.groupValues[4].contains("stroke-dasharray")
        )
    }.toList()
}

private fun buildRoadPath(d: String, s: Float): Path {
    val path = Path()
    val tokens = Regex("""[MCL]\s+-?[\d.]+(?:[,\s]+-?[\d.]+)*""")
        .findAll(d)
        .map { it.value.trim() }
        .toList()
    for (token in tokens) {
        val cmd = token[0]
        val nums = Regex("""-?[\d.]+""").findAll(token).map { it.value.toFloat() }.toList()
        when (cmd) {
            'M' -> if (nums.size >= 2) path.moveTo(nums[0] * s, nums[1] * s)
            'L' -> if (nums.size >= 2) path.lineTo(nums[0] * s, nums[1] * s)
            'C' -> if (nums.size >= 6) path.cubicTo(nums[0] * s, nums[1] * s, nums[2] * s, nums[3] * s, nums[4] * s, nums[5] * s)
        }
    }
    return path
}

private val HEX_COLOR_REGEX = Regex("^#?([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$")

private fun parseHexColor(hex: String): Long {
    val match = HEX_COLOR_REGEX.matchEntire(hex.trim()) ?: return 0xFF8B5E3CL
    val digits = match.groupValues[1]
    val expanded = if (digits.length == 3) {
        digits.map { "$it$it" }.joinToString("")
    } else {
        digits
    }
    return 0xFF000000L or expanded.toLong(16)
}

private fun roadEndPoint(d: String, s: Float): Pair<Float, Float>? {
    val tokens = Regex("""[MLC]\s+-?[\d.]+(?:[,\s]+-?[\d.]+)*""")
        .findAll(d)
        .map { it.value.trim() }
        .toList()
    var last: Pair<Float, Float>? = null
    var current = 0f to 0f
    for (token in tokens) {
        val cmd = token[0]
        val nums = Regex("""-?[\d.]+""").findAll(token).map { it.value.toFloat() }.toList()
        when (cmd) {
            'M' -> if (nums.size >= 2) {
                current = nums[0] to nums[1]
                last = current
            }
            'L' -> if (nums.size >= 2) {
                current = nums[0] to nums[1]
                last = current
            }
            'C' -> if (nums.size >= 6) {
                current = nums[4] to nums[5]
                last = current
            }
        }
    }
    return last?.let { it.first * s to it.second * s }
}

@Composable
private fun RoadSvg(html: String, modifier: Modifier = Modifier) {
    val paths = remember(html) { parseRoadPaths(html) }
    val arrowPathData = remember(html) { (paths.firstOrNull { !it.dashed } ?: paths.firstOrNull())?.d ?: "" }
    val circles = remember(html) { ROAD_CIRCLE_REGEX.findAll(html).map { m -> Triple(m.groupValues[1].toFloat(), m.groupValues[2].toFloat(), m.groupValues[3].toFloat()) }.toList() }
    val caption = remember(html) { axisCaption(html) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(450f / 120f)
        ) {
            val s = size.width / 450f

            paths.forEach { p ->
                val path = buildRoadPath(p.d, s)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = parseHexColor(p.stroke).toInt()
                    style = Paint.Style.STROKE
                    strokeWidth = p.width * s
                    strokeCap = Paint.Cap.ROUND
                    strokeJoin = Paint.Join.ROUND
                    if (p.dashed) {
                        pathEffect = DashPathEffect(floatArrayOf(0.1f * s, 14f * s), 0f)
                    }
                }
                drawContext.canvas.nativeCanvas.drawPath(path, paint)
            }

            val cityPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color(0xFFB48B5A).toArgb()
                style = Paint.Style.FILL
            }
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.BLACK
                style = Paint.Style.FILL
            }

            circles.forEach { (cx, cy, r) ->
                drawContext.canvas.nativeCanvas.drawCircle(cx * s, cy * s, r * s, cityPaint)
            }

            roadEndPoint(arrowPathData, s)?.let { (ex, ey) ->
                val arrow = Path().apply {
                    moveTo(ex, ey - 8f * s)
                    lineTo(ex + 12f * s, ey)
                    lineTo(ex, ey + 8f * s)
                    close()
                }
                drawContext.canvas.nativeCanvas.drawPath(arrow, fillPaint)
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
private fun WalkAmongNumbersGameContent(
    contentId: String,
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToRoadToSomewhere: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = walkAmongNumbersScreenStringsForLanguage(LocalAppLanguage.current)
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
                preferences.markRoadToSomewhereSectionCompleted("walk_among_numbers")
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
                            text.format?.contains("markdown") == true -> MarkdownText(text = text.text ?: "")
                            isAxisSvg(text.text) -> NumberAxis(
                                html = text.text ?: "",
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            isHandsSvg(text.text) -> HandsPair()
                            isRoadSvg(text.text) -> RoadSvg(
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
    val xs = walkAmongNumbersScreenStringsForLanguage(LocalAppLanguage.current)
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
