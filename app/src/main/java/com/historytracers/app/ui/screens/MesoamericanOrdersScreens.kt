// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.R
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.ResponsiveImage
import com.historytracers.app.ui.components.TextRenderer
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.mesoamericanOrdersScreenStringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val SMARTPHONE_GAME_FILE = "1a5c6dc9-a609-4fce-b360-54738e823cfc"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"

private const val INTRO_CONTENT_ID = "fd8f0583-8c83-4a8a-8c85-1cc9c2f678f7"
private const val FIRST_VALUE_CONTENT_ID = "fbca5819-1091-45e8-a52f-444143d991ec"
private const val LAST_VALUE_CONTENT_ID = "ea4c0c93-f9a5-4278-bdbd-901b69454731"
private const val ANCIENT_CALENDAR_CONTENT_ID = "a142150e-aaf9-42fd-899d-195df4952cf4"
private const val DIFFERENT_CONTENT_ID = "195b484f-a787-4698-a1f9-364c159ed45b"
private const val NEXT_ORDERS_CONTENT_ID = "9d3215fc-dbe3-4bc4-9211-f2b66d887396"
private const val THINKING_CONTENT_ID = "e0033a5d-d2a9-4c7e-9ee0-50ae2a13c512"
private const val NOT_LIKE_CONTENT_ID = "41901a66-53a2-42e0-b6bf-2a250e0f5e1e"
private const val CONCLUSION_CONTENT_ID = "e705ab48-d22e-4070-a5fe-7ab739a8acd1"

private const val MARKER_TWENTY = "maya-orders-20"
private const val MARKER_THREE_FIFTY_NINE = "maya-orders-359"
private const val MARKER_TABLE = "maya-orders-table"

@Composable
fun MesoamericanOrdersIntroScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MesoamericanOrdersGameContent(
        contentId = INTRO_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MesoamericanOrdersFirstValueScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MesoamericanOrdersGameContent(
        contentId = FIRST_VALUE_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MesoamericanOrdersLastValueScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MesoamericanOrdersGameContent(
        contentId = LAST_VALUE_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MesoamericanOrdersAncientCalendarScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MesoamericanOrdersGameContent(
        contentId = ANCIENT_CALENDAR_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MesoamericanOrdersDifferentIsNotWrongScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MesoamericanOrdersGameContent(
        contentId = DIFFERENT_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MesoamericanOrdersNextOrdersScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MesoamericanOrdersGameContent(
        contentId = NEXT_ORDERS_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MesoamericanOrdersThinkingScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MesoamericanOrdersGameContent(
        contentId = THINKING_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MesoamericanOrdersItIsNotLikeThisScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MesoamericanOrdersGameContent(
        contentId = NOT_LIKE_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MesoamericanOrdersConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToAnotherWayToCount: () -> Unit = {}
) {
    MesoamericanOrdersGameContent(
        contentId = CONCLUSION_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateToAnotherWayToCount = onNavigateToAnotherWayToCount
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

private fun containsMarker(text: String?, marker: String): Boolean =
    text?.contains("data-custom=\"$marker\"") == true

private fun hasImgSrc(text: String?): Boolean = text?.contains("<img") == true

@Composable
private fun MayaNumber(value: Int, modifier: Modifier = Modifier) {
    val ink = Color(0xFF5A3F2C)
    if (value == 0) {
        Image(
            painter = painterResource(R.drawable.ic_maya_zero_shell),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(ink),
            modifier = modifier
        )
        return
    }
    Canvas(modifier = modifier) {
        val dotRadius = size.minDimension * 0.07f
        val gap = size.height * 0.06f
        val barWidth = 4 * dotRadius * 2f + 3 * gap
        val barHeight = size.height * 0.14f
        val bars = value / 5
        val dots = value % 5
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
                cornerRadius = CornerRadius(barHeight / 2f)
            )
            y += barHeight + gap
        }
    }
}

@Composable
private fun MayaOrderSymbol(value: Int, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = modifier
    ) {
        Box(contentAlignment = Alignment.Center) {
            MayaNumber(value = value, modifier = Modifier.size(52.dp))
        }
    }
}

@Composable
private fun MayaVerticalNumber(
    orders: List<Int>,
    hinduArabic: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            orders.forEach { value ->
                MayaOrderSymbol(value = value, modifier = Modifier.size(72.dp))
            }
        }
        if (hinduArabic != null) {
            Text(
                text = "$hinduArabic",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun MayaOrdersTable(modifier: Modifier = Modifier) {
    val xs = mesoamericanOrdersScreenStringsForLanguage(LocalAppLanguage.current)

    val rows = listOf(
        Triple(xs.units, "1", "1"),
        Triple(xs.tens, "10", "20"),
        Triple(xs.hundreds, "100", "360"),
        Triple(xs.thousands, "1,000", "7,200"),
        Triple(xs.tenThousands, "10,000", "144,000")
    )
    val mayaOrders = listOf("K'iin", "Winal", "Tun", "K'atun", "Bak'atun")

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TableCell(text = xs.orderLabel, header = true, weight = 1.4f)
                TableCell(text = "${xs.hinduArabicLabel} ${xs.factorLabel}", header = true, weight = 1f)
                TableCell(text = xs.mesoamericanLabel, header = true, weight = 1.4f)
                TableCell(text = xs.factorLabel, header = true, weight = 1f)
            }
            rows.forEachIndexed { index, row ->
                val (order, decimalFactor, mesoFactor) = row
                Row(modifier = Modifier.fillMaxWidth()) {
                    TableCell(text = order, weight = 1.4f)
                    TableCell(text = decimalFactor, weight = 1f)
                    TableCell(text = mayaOrders[index], weight = 1.4f)
                    TableCell(text = mesoFactor, weight = 1f)
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.TableCell(
    text: String,
    header: Boolean = false,
    weight: Float = 1f
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
        color = if (header) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .weight(weight)
            .padding(horizontal = 2.dp, vertical = 6.dp)
    )
}

@Composable
private fun MesoamericanOrdersGameContent(
    contentId: String,
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToAnotherWayToCount: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = mesoamericanOrdersScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
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
    val awardedScreens by preferences.awardedScreens.collectAsState(initial = emptySet())

    LaunchedEffect(content) {
        val node = content
        if (node != null && !arrivalHandled) {
            arrivalHandled = true
            if (onNavigateToAnotherWayToCount != null) {
                preferences.markAnotherWayToCountSectionCompleted("mesoamerican_orders")
                preferences.recordLessonCompletion()
            }
            val arrivalAlreadyAwarded = preferences.arrivalAwardedScreens.first().contains(node.id)
            if (!arrivalAlreadyAwarded) {
                award(node.score)
                preferences.markArrivalAwarded(node.id)
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
                            containsMarker(html, MARKER_TWENTY) -> MayaVerticalNumber(orders = listOf(1, 0))
                            containsMarker(html, MARKER_THREE_FIFTY_NINE) -> MayaVerticalNumber(orders = listOf(17, 19), hinduArabic = 359)
                            containsMarker(html, MARKER_TABLE) -> MayaOrdersTable()
                            text.format?.contains("markdown") == true -> MarkdownText(text = html)
                            hasImgSrc(html) -> ResponsiveImage(html = html, imgDesc = text.imgdesc)
                            else -> TextRenderer(text = text, repo = repo)
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    if (content.answer != null) {
                        MesoamericanOrdersAnswerSection(
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

                    if (onNavigateToAnotherWayToCount != null) {
                        Spacer(Modifier.height(16.dp))
                        FilledTonalButton(
                            onClick = onNavigateToAnotherWayToCount,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = hts.anotherWayToCount, fontWeight = FontWeight.Bold)
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
                MesoamericanOrdersSourcesMenu(
                    sources = sources,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun MesoamericanOrdersAnswerSection(
    content: SMGameContent,
    onAnswered: (Int) -> Unit
) {
    val s = LocalUiStrings.current
    val xs = mesoamericanOrdersScreenStringsForLanguage(LocalAppLanguage.current)
    var selected by remember { mutableStateOf<String?>(null) }
    var hasSubmitted by remember { mutableStateOf(false) }

    val correctAnswer = when (val answer = content.answer) {
        is Boolean -> answer
        is String -> answer.equals("yes", ignoreCase = true)
        else -> null
    }

    fun submit(answer: String) {
        selected = answer
        hasSubmitted = true
        val answeredCorrectly = (answer == "yes") == correctAnswer
        val points = if (answeredCorrectly) content.score else content.score / 2
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
private fun MesoamericanOrdersSourcesMenu(sources: List<HTSource>, modifier: Modifier = Modifier) {
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
