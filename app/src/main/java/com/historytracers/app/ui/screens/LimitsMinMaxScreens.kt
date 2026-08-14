// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.historytracers.app.ui.components.ResponsiveImage
import com.historytracers.app.ui.components.TextRenderer
import com.historytracers.app.ui.components.buildHandPath
import com.historytracers.app.ui.components.drawHandNumbers
import com.historytracers.app.ui.components.drawOneHand
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.limitsMinMaxScreenStringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile

private const val SMARTPHONE_GAME_FILE = "a3946fd3-a94c-410b-aec8-3dec5055cdf3"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"

@Composable
fun LimitsMinMaxBetweenBothScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LimitsMinMaxGameContent(
        contentId = "e97ef9a7-4180-4783-8688-9ace7416744b",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LimitsMinMaxQuestionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LimitsMinMaxGameContent(
        contentId = "3339d6e5-3719-48ab-af4d-12665450593d",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LimitsMinMaxTendingScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LimitsMinMaxGameContent(
        contentId = "3574b650-a3c7-4b6f-a50e-5a500a32040e",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LimitsMinMaxHandsScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    LimitsMinMaxGameContent(
        contentId = "cf776dbb-ab51-4a8c-9984-749e367c34bf",
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun LimitsMinMaxConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToFirstSteps: () -> Unit = {}
) {
    LimitsMinMaxGameContent(
        contentId = "08f1f8a2-4b40-44a9-974d-64a3e2e7b75c",
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

private fun isHandsFeetSvg(text: String?): Boolean =
    text?.contains("<svg") == true && text.contains("hand-shape")

private fun isSvgStyle(text: String?): Boolean =
    text?.startsWith("<style>") == true

private fun isImgHtml(text: String?): Boolean =
    text?.startsWith("<img") == true

private val footToePaths: List<String> by lazy {
    listOf(
        "M4085 12793 c-172 -22 -364 -101 -472 -194 -249 -216 -357 -646 -272 -1087 54 -282 193 -513 391 -649 95 -65 140 -88 248 -123 237 -77 493 -56 727 62 219 109 355 309 419 613 26 124 26 421 1 549 -68 338 -211 559 -461 708 -160 97 -394 145 -581 121z",
        "M6475 12530 c-133 -28 -252 -96 -360 -205 -159 -159 -231 -332 -242 -586 -19 -422 142 -773 431 -938 208 -119 426 -143 616 -69 111 43 231 136 322 248 292 362 202 1110 -167 1387 -73 54 -219 127 -300 148 -70 19 -243 28 -300 15z",
        "M1431 12289 c-195 -26 -436 -111 -604 -211 -143 -87 -205 -136 -338 -268 -413 -412 -577 -982 -439 -1525 135 -534 568 -929 1118 -1020 152 -25 427 -17 572 18 467 112 849 400 1087 821 89 156 169 393 192 569 14 102 14 324 0 423 -81 588 -511 1051 -1087 1175 -113 24 -383 34 -501 18z",
        "M8183 11316 c-189 -48 -313 -184 -375 -412 -28 -107 -31 -316 -4 -419 25 -97 76 -200 130 -263 244 -284 631 -355 877 -161 139 109 230 395 201 631 -23 187 -82 308 -209 428 -143 136 -288 200 -468 206 -61 2 -120 -2 -152 -10z",
        "M9535 10154 c-265 -67 -470 -327 -515 -656 -35 -250 64 -519 232 -631 153 -101 332 -102 500 -1 377 228 478 860 184 1158 -39 41 -85 74 -129 96 -59 29 -79 33 -160 36 -50 2 -101 1 -112 -2z",
        "M5075 10674 c-433 -34 -787 -104 -1124 -224 -429 -152 -712 -325 -968 -594 -193 -202 -326 -394 -458 -661 -214 -430 -315 -855 -315 -1321 0 -698 265 -1234 747 -1513 164 -94 375 -159 610 -186 799 -93 1448 -394 1788 -830 189 -242 284 -488 316 -815 42 -441 -70 -770 -532 -1570 -84 -146 -209 -362 -277 -480 -233 -404 -405 -779 -467 -1015 -109 -421 -6 -814 286 -1090 414 -390 1188 -481 2077 -245 347 93 635 271 937 581 285 293 518 634 755 1109 523 1044 877 2425 1002 3910 20 243 17 1017 -5 1190 -84 652 -225 1241 -393 1640 -307 730 -823 1239 -1670 1646 -602 289 -1062 418 -1663 464 -121 9 -544 11 -646 4z"
    )
}

private data class LimitsFootBounds(var minX: Float, var minY: Float, var maxX: Float, var maxY: Float)

private fun parseSvgPathToAndroidPath(svgD: String): Path {
    val path = Path()
    var i = 0
    var curX = 0f
    var curY = 0f
    while (i < svgD.length) {
        val ch = svgD[i]
        if (ch.isWhitespace()) { i++; continue }
        when (ch) {
            'M' -> {
                i++
                val nums = mutableListOf<Float>()
                while (i < svgD.length) {
                    if (svgD[i].isWhitespace() || svgD[i] == ',') { i++; continue }
                    if (svgD[i] in 'a'..'z' || svgD[i] in 'A'..'Z') break
                    val sb = StringBuilder()
                    while (i < svgD.length && (svgD[i] == '-' || svgD[i] == '.' || svgD[i].isDigit())) {
                        sb.append(svgD[i]); i++
                    }
                    if (sb.isNotEmpty()) nums.add(sb.toString().toFloat())
                }
                if (nums.size >= 2) {
                    curX = nums[0]; curY = nums[1]
                    path.moveTo(curX, curY)
                }
            }
            'c' -> {
                i++
                val nums = mutableListOf<Float>()
                while (i < svgD.length) {
                    if (svgD[i].isWhitespace() || svgD[i] == ',') { i++; continue }
                    if (svgD[i] in 'a'..'z' || svgD[i] in 'A'..'Z') break
                    val sb = StringBuilder()
                    if (svgD[i] == '-') { sb.append('-'); i++ }
                    while (i < svgD.length && (svgD[i] == '.' || svgD[i].isDigit())) {
                        sb.append(svgD[i]); i++
                    }
                    if (sb.isNotEmpty()) nums.add(sb.toString().toFloat())
                }
                var j = 0
                while (j + 5 < nums.size) {
                    path.rCubicTo(nums[j], nums[j + 1], nums[j + 2], nums[j + 3], nums[j + 4], nums[j + 5])
                    curX += nums[j + 4]; curY += nums[j + 5]
                    j += 6
                }
            }
            'z', 'Z' -> { path.close(); i++ }
            else -> i++
        }
    }
    return path
}

private fun buildFootPath(scale: Float = 0.1f, flipY: Boolean = true): Pair<Path, LimitsFootBounds> {
    val path = Path()
    val bounds = LimitsFootBounds(Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE)
    for (svgD in footToePaths) {
        val sub = parseSvgPathToAndroidPath(svgD)
        val m = Matrix()
        m.preScale(scale, if (flipY) -scale else scale)
        val transformed = Path()
        sub.transform(m, transformed)
        val b = RectF()
        transformed.computeBounds(b, true)
        bounds.minX = minOf(bounds.minX, b.left)
        bounds.minY = minOf(bounds.minY, b.top)
        bounds.maxX = maxOf(bounds.maxX, b.right)
        bounds.maxY = maxOf(bounds.maxY, b.bottom)
        path.addPath(transformed)
    }
    return Pair(path, bounds)
}

private fun buildFootToeTips(scale: Float = 0.1f, flipY: Boolean = true): List<Offset> {
    return footToePaths.take(5).map { svgD ->
        val sub = parseSvgPathToAndroidPath(svgD)
        val m = Matrix()
        m.preScale(scale, if (flipY) -scale else scale)
        val transformed = Path()
        sub.transform(m, transformed)
        val b = RectF()
        transformed.computeBounds(b, true)
        Offset((b.left + b.right) / 2f, b.top)
    }
}

@Composable
private fun CountingHandsAndFeet(modifier: Modifier = Modifier) {
    val s = LocalUiStrings.current
    val handPath = remember { buildHandPath() }
    val footResult = remember { buildFootPath() }
    val footPath = footResult.first
    val footBounds = footResult.second
    val toeTips = remember { buildFootToeTips() }

    var count by remember { mutableIntStateOf(0) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
        ) {
            val scale = size.width / 600f
            val cx = size.width / 2f

            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color(0xFFF4C2A1).hashCode()
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

            val raised = minOf(count, 10)
            val rightNums = (1..minOf(raised, 5)).map { it to (5 - it) }
            val leftNums = if (raised > 5) (6..raised).map { it to (it - 6) } else emptyList()
            drawHandNumbers(
                numbers = rightNums,
                cx = rightCx, cy = handCy, handScale = handScale, isLeft = false,
                textPaint = textPaint, strokePaint = strokePaint
            )
            drawHandNumbers(
                numbers = leftNums,
                cx = leftCx, cy = handCy, handScale = handScale, isLeft = true,
                textPaint = textPaint, strokePaint = strokePaint
            )

            if (count > 10) {
                val feetRaised = count - 10
                val footScale = scale * (0.125f / 0.6f) * 0.7f
                val footCx = (footBounds.minX + footBounds.maxX) / 2f
                val footCy = (footBounds.minY + footBounds.maxY) / 2f
                val footY = 245f * scale
                val feetRightPx = with(density) { 30.dp.toPx() }

                fun drawFoot(xOffset: Float, mirrorX: Boolean) {
                    val m = Matrix()
                    m.setTranslate(cx + xOffset + feetRightPx - footCx * footScale, footY - footCy * footScale)
                    m.preScale(if (mirrorX) -footScale else footScale, footScale)
                    val fp = Path()
                    fp.addPath(footPath, m)
                    drawContext.canvas.nativeCanvas.drawPath(fp, paint)
                }

                drawFoot(-55f * scale, mirrorX = true)
                drawFoot(55f * scale, mirrorX = false)

                val sortedTips = toeTips.sortedBy { it.x }
                val toeTextSize = 26f * scale
                textPaint.textSize = toeTextSize
                strokePaint.textSize = toeTextSize

                fun drawToeNumber(number: Int, tip: Offset, xOffset: Float, mirrorX: Boolean) {
                    val sx = tip.x * (if (mirrorX) -footScale else footScale)
                    val sy = tip.y * footScale
                    val x = cx + xOffset + feetRightPx - footCx * footScale + sx
                    val y = footY - footCy * footScale + sy + toeTextSize * 0.35f
                    drawContext.canvas.nativeCanvas.drawText(number.toString(), x, y, strokePaint)
                    drawContext.canvas.nativeCanvas.drawText(number.toString(), x, y, textPaint)
                }

                for (k in 1..minOf(feetRaised, 5)) {
                    drawToeNumber(10 + k, sortedTips[k - 1], 55f * scale, mirrorX = false)
                }
                for (k in 1..maxOf(0, feetRaised - 5)) {
                    drawToeNumber(15 + k, sortedTips[k - 1], -55f * scale, mirrorX = true)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = { if (count > 0) count-- },
                enabled = count > 0
            ) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = s.common.previous)
            }
            FilledIconButton(
                onClick = { if (count < 20) count++ },
                enabled = count < 20
            ) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = s.common.next)
            }
        }
    }
}

@Composable
private fun LimitsMinMaxGameContent(
    contentId: String,
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToFirstSteps: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = limitsMinMaxScreenStringsForLanguage(LocalAppLanguage.current)
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
                            text.format?.contains("markdown") == true -> MarkdownText(text = text.text ?: "")
                            isHandsFeetSvg(text.text) -> CountingHandsAndFeet(
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            isSvgStyle(text.text) -> Unit
                            isImgHtml(text.text) -> ResponsiveImage(
                                html = text.text ?: "",
                                imgDesc = text.imgdesc
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
    val xs = limitsMinMaxScreenStringsForLanguage(LocalAppLanguage.current)
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
