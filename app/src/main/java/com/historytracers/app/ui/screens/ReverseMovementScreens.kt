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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.historytracers.app.ui.features.reverseMovementScreenStringsForLanguage
import com.historytracers.app.ui.features.yupanaSharedStringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile

private const val SMARTPHONE_GAME_FILE = "fd5649cc-eab6-438a-b626-65b2f8840f52"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"
private const val YUPANA_SECTION_ID = "reverse_movements"

private const val INTRO_CONTENT_ID = "a7bb2faa-228f-4ccd-a198-ac570e92ab86"
private const val ISKAY_CONTENT_ID = "256dd146-84e1-498f-bef3-aeea246f2d83"
private const val KIMSA_CONTENT_ID = "452c772c-e5ee-4e64-b187-b4f90de106aa"
private const val PISQA_CONTENT_ID = "fcaffdc7-d56e-4e9e-b015-3e25a5561efe"
private const val PICHANA_CONTENT_ID = "afae4ac0-b0e1-4d09-b0cc-d7f0b0dd392c"
private const val CANCELLATION_CONTENT_ID = "584f9b39-9879-4e40-bf17-9f6393e4f130"
private const val QUESTION_CONTENT_ID = "9ffe0bb7-4fe6-4b14-88f5-a8c52fda831a"
private const val CONCLUSION_CONTENT_ID = "1793892a-0bef-4cf5-87a4-750f9c1ed379"

private const val REVERSE_ISKAY = "data-custom=\"reverse-iskay\""
private const val REVERSE_KIMSA = "data-custom=\"reverse-kimsa\""
private const val REVERSE_PISQA = "data-custom=\"reverse-pisqa\""
private const val REVERSE_PICHANA = "data-custom=\"reverse-pichana\""
private const val REVERSE_CANCELLATION = "data-custom=\"reverse-cancellation\""

private data class YupanaRowMarkers(
    val left: Set<Int> = emptySet(),
    val right: Set<Int> = emptySet(),
    val result: Set<Int> = emptySet(),
)

private data class ReverseVisual(
    val before: List<YupanaRowMarkers>,
    val after: List<YupanaRowMarkers>,
)

private val MARKER_TOKENS = listOf(
    REVERSE_ISKAY, REVERSE_KIMSA, REVERSE_PISQA, REVERSE_PICHANA, REVERSE_CANCELLATION
)

private fun reverseVisualFor(marker: String): ReverseVisual? = when (marker) {
    REVERSE_ISKAY -> ReverseVisual(
        before = listOf(YupanaRowMarkers(left = setOf(2), right = setOf(4))),
        after = listOf(YupanaRowMarkers(left = setOf(3)))
    )
    REVERSE_KIMSA -> ReverseVisual(
        before = listOf(YupanaRowMarkers(left = setOf(1), right = setOf(4))),
        after = listOf(YupanaRowMarkers(left = setOf(2, 4)))
    )
    REVERSE_PISQA -> ReverseVisual(
        before = listOf(
            YupanaRowMarkers(left = setOf(4)),
            YupanaRowMarkers(right = setOf(1))
        ),
        after = listOf(YupanaRowMarkers(left = setOf(1)))
    )
    else -> null
}

@Composable
fun ReverseMovementIntroScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    ReverseMovementGameContent(
        contentId = INTRO_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun ReverseMovementIskayScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    ReverseMovementGameContent(
        contentId = ISKAY_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun ReverseMovementKimsaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    ReverseMovementGameContent(
        contentId = KIMSA_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun ReverseMovementPisqaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    ReverseMovementGameContent(
        contentId = PISQA_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun ReverseMovementPichanaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    ReverseMovementGameContent(
        contentId = PICHANA_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun ReverseMovementCancellationScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    ReverseMovementGameContent(
        contentId = CANCELLATION_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun ReverseMovementQuestionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    ReverseMovementGameContent(
        contentId = QUESTION_CONTENT_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun ReverseMovementConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToYupana: () -> Unit = {}
) {
    ReverseMovementGameContent(
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

@Composable
private fun YupanaBoard(rows: List<YupanaRowMarkers>, modifier: Modifier = Modifier) {
    val rowCount = rows.size.coerceAtLeast(1)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .aspectRatio(860f / (120f * rowCount + 60f))
    ) {
        val margin = 3f / 860f * size.width
        val usableWidth = size.width - 2f * margin
        val colWidth = usableWidth / 4f
        val rowHeight = (size.height - 6f / 480f * size.height) / rowCount
        val startX = margin
        val startY = 3f / 480f * size.height

        drawYupanaBackground(size)
        drawYupanaFrame(size)
        rows.forEachIndexed { index, row ->
            drawYupanaRow(
                cellOriginX = startX,
                cellOriginY = startY + index * rowHeight,
                cellWidth = colWidth,
                cellHeight = rowHeight,
                canvasSize = size,
                leftMarkers = row.left,
                rightMarkers = row.right,
                resultMarkers = row.result
            )
        }
    }
}

@Composable
private fun ReverseMovementBoard(visual: ReverseVisual, modifier: Modifier = Modifier) {
    val xs = reverseMovementScreenStringsForLanguage(LocalAppLanguage.current)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = xs.before,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        YupanaBoard(rows = visual.before)
        Spacer(Modifier.height(8.dp))
        Icon(
            Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = xs.after,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        YupanaBoard(rows = visual.after)
    }
}

@Composable
private fun PichanaReverseBoard(modifier: Modifier = Modifier) {
    val xs = reverseMovementScreenStringsForLanguage(LocalAppLanguage.current)
    var useFirstOption by remember { mutableStateOf(true) }
    val visual = if (useFirstOption) {
        ReverseVisual(
            before = listOf(YupanaRowMarkers(left = setOf(2), right = setOf(3))),
            after = listOf(YupanaRowMarkers(left = setOf(4)))
        )
    } else {
        ReverseVisual(
            before = listOf(YupanaRowMarkers(left = setOf(3), right = setOf(4))),
            after = listOf(YupanaRowMarkers(left = setOf(4)))
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = useFirstOption,
                    onClick = { useFirstOption = true }
                )
                Text(
                    text = xs.pichanaEquation32,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !useFirstOption,
                    onClick = { useFirstOption = false }
                )
                Text(
                    text = xs.pichanaEquation21,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        ReverseMovementBoard(visual = visual)
    }
}

@Composable
private fun CancellationReverseBoard(modifier: Modifier = Modifier) {
    val xs = reverseMovementScreenStringsForLanguage(LocalAppLanguage.current)
    var selected by remember { mutableIntStateOf(5) }
    val col = when (selected) {
        5 -> 1
        3 -> 2
        2 -> 3
        else -> 4
    }
    val visual = ReverseVisual(
        before = listOf(YupanaRowMarkers(left = setOf(col), right = setOf(col))),
        after = listOf(YupanaRowMarkers())
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CancellationOption(xs.cancellationEquation5, selected == 5) { selected = 5 }
            CancellationOption(xs.cancellationEquation3, selected == 3) { selected = 3 }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CancellationOption(xs.cancellationEquation2, selected == 2) { selected = 2 }
            CancellationOption(xs.cancellationEquation1, selected == 1) { selected = 1 }
        }

        Spacer(Modifier.height(8.dp))

        ReverseMovementBoard(visual = visual)
    }
}

@Composable
private fun CancellationOption(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ReverseMovementGameContent(
    contentId: String,
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToYupana: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = reverseMovementScreenStringsForLanguage(LocalAppLanguage.current)
    val ys = yupanaSharedStringsForLanguage(LocalAppLanguage.current)
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
            if (onNavigateToYupana != null) {
                preferences.markYupanaSectionCompleted(YUPANA_SECTION_ID)
                preferences.recordLessonCompletion()
            }
            award(node.score)
            preferences.markArrivalAwarded(node.id)
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
                        val marker = MARKER_TOKENS.firstOrNull { html.contains(it) }
                        val visual = marker?.let { reverseVisualFor(it) }
                        when {
                            marker == REVERSE_PICHANA -> PichanaReverseBoard()
                            marker == REVERSE_CANCELLATION -> CancellationReverseBoard()
                            visual != null -> ReverseMovementBoard(visual)
                            text.format?.contains("markdown") == true -> MarkdownText(text = html)
                            html.contains("<img") -> ResponsiveImage(html = html, imgDesc = text.imgdesc)
                            else -> TextRenderer(text = text, repo = repo)
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    if (content.answer != null) {
                        ReverseMovementAnswerSection(
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
                ReverseMovementSourcesMenu(
                    sources = sources,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun ReverseMovementAnswerSection(
    content: SMGameContent,
    onAnswered: (Int) -> Unit
) {
    val s = LocalUiStrings.current
    val xs = reverseMovementScreenStringsForLanguage(LocalAppLanguage.current)
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
private fun ReverseMovementSourcesMenu(sources: List<HTSource>, modifier: Modifier = Modifier) {
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
