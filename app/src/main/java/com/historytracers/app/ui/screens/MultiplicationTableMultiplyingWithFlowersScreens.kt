// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
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
import com.historytracers.app.ui.components.AutoSizeTitleText
import com.historytracers.app.ui.components.HtmlRenderer
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.MultiplicationTableVisualizer
import com.historytracers.app.ui.components.ScreenAudioButton
import com.historytracers.app.ui.components.multiplicationTableFitsOnScreen
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.miscStringsForLanguage
import com.historytracers.app.ui.features.multiplicationTableMultiplyingWithFlowersScreenStringsForLanguage
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile

private const val SMARTPHONE_GAME_FILE = "600c0c57-4120-478f-b5fa-66e7ecb6372d"
private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"

private const val INTRODUCTION_ID = "fde4a250-0c33-4b21-b6dd-429adea22814"
private const val FLOWERS_ID = "4484c9e7-6eb3-48c5-a831-a20b0b1421a1"
private const val THINKING_ID = "7235e420-34bb-432d-b1aa-9d70756ae790"
private const val TIMES_TWO_ID = "8b239792-6587-4527-bc11-69bc22f76882"
private const val TIMES_THREE_ID = "cab2a6d1-8bbb-4387-9db8-7cb9d9d9771b"
private const val VISUALIZING_ID = "debac559-c847-4893-be19-33886ae7e701"
private const val A_RULE_ID = "5a4efea5-990b-49b2-aa23-d9123d736b99"
private const val READING_THE_RULE_ID = "51539922-43bd-4a41-90d8-4bf246939a61"
private const val CONCLUSION_ID = "47cf1e8f-d95d-483c-83d1-506cb7a7e52e"

private const val TABLE_OF_SIX = 6

private const val SECTION_ID = "multiplying_with_flowers"

@Composable
fun MultiplicationTableMultiplyingWithFlowersIntroductionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MultiplicationTableMultiplyingWithFlowersGameContent(
        contentId = INTRODUCTION_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MultiplicationTableMultiplyingWithFlowersFlowersScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MultiplicationTableMultiplyingWithFlowersGameContent(
        contentId = FLOWERS_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MultiplicationTableMultiplyingWithFlowersThinkingScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MultiplicationTableMultiplyingWithFlowersGameContent(
        contentId = THINKING_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MultiplicationTableMultiplyingWithFlowersTimesTwoScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MultiplicationTableMultiplyingWithFlowersGameContent(
        contentId = TIMES_TWO_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MultiplicationTableMultiplyingWithFlowersTimesThreeScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MultiplicationTableMultiplyingWithFlowersGameContent(
        contentId = TIMES_THREE_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MultiplicationTableMultiplyingWithFlowersVisualizingScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MultiplicationTableMultiplyingWithFlowersGameContent(
        contentId = VISUALIZING_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext,
        showVisualizer = true
    )
}

@Composable
fun MultiplicationTableMultiplyingWithFlowersRuleScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MultiplicationTableMultiplyingWithFlowersGameContent(
        contentId = A_RULE_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MultiplicationTableMultiplyingWithFlowersReadingRuleScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    MultiplicationTableMultiplyingWithFlowersGameContent(
        contentId = READING_THE_RULE_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun MultiplicationTableMultiplyingWithFlowersConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToMultiplicationTableHub: () -> Unit = {}
) {
    MultiplicationTableMultiplyingWithFlowersGameContent(
        contentId = CONCLUSION_ID,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateToMultiplicationTableHub = onNavigateToMultiplicationTableHub
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
private fun MultiplicationTableMultiplyingWithFlowersGameContent(
    contentId: String,
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    showVisualizer: Boolean = false,
    onNavigateToMultiplicationTableHub: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = multiplicationTableMultiplyingWithFlowersScreenStringsForLanguage(LocalAppLanguage.current)
    val mcs = miscStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }
    // On short screens the flower drawings are made smaller so the whole
    // screen fits without scrolling.
    val shortScreen = LocalConfiguration.current.screenHeightDp < 700
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

    val showTable = showVisualizer && contentId == VISUALIZING_ID

    // Every screen keeps its navigation buttons visible: only the lesson text
    // scrolls, and only when the screen is too short to hold it. On very short
    // windows (for example in landscape) the whole page scrolls instead of
    // clipping the content.
    val fitsOnScreen = multiplicationTableFitsOnScreen()

    fun award(points: Int) {
        if (points <= 0) return
        // Award on top of the latest shared score instead of the snapshot taken
        // when the screen opened, so a score raised meanwhile is never overwritten.
        onScoreChanged(currentScore + points)
    }

    // Visit-local arrival guard: switching the app language reloads the content
    // object, which would re-run this effect and award the same visit again.
    val arrivalAwarded = remember { mutableSetOf<String>() }

    LaunchedEffect(content) {
        val node = content
        if (node != null && node.id !in arrivalAwarded) {
            arrivalAwarded.add(node.id)
            award(node.score)
            preferences.markArrivalAwarded(node.id)
            if (onNavigateToMultiplicationTableHub != null) {
                preferences.markMultiplicationTableSectionCompleted(SECTION_ID)
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
                AutoSizeTitleText(
                    text = xs.title,
                    modifier = Modifier.padding(start = 8.dp).weight(1f)
                )
                ScreenAudioButton(
                    // Per-screen narration is not published for this class
                    // yet, so the button plays the class-level audio.
                    lessonId = SMARTPHONE_GAME_FILE,
                    contentId = null
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
                        .then(
                            if (fitsOnScreen) Modifier
                            else Modifier.verticalScroll(rememberScrollState())
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // The lesson text is the only part that may scroll, and only
                    // on screens too short to hold it, so the navigation buttons
                    // always stay visible.
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (fitsOnScreen) {
                                    Modifier
                                        .weight(1f, fill = false)
                                        .verticalScroll(rememberScrollState())
                                } else {
                                    Modifier
                                }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        content.text?.forEach { text ->
                            if (text == null) return@forEach
                            val html = text.text ?: ""
                            val isHtml = text.format?.contains("html") == true
                            // The single six-petal flower is a tall drawing, so it
                            // is shown smaller than the strip with five flowers.
                            val isSingleFlower = isHtml &&
                                contentId == FLOWERS_ID &&
                                html.contains("viewBox=\"0 0 360 230\"")
                            val isFlowerStrip = isHtml &&
                                contentId == FLOWERS_ID &&
                                html.contains("viewBox=\"0 0 640 240\"")
                            when {
                                isSingleFlower -> HtmlRenderer(
                                    html = html,
                                    modifier = Modifier.fillMaxWidth(if (shortScreen) 0.45f else 0.6f)
                                )
                                isFlowerStrip -> HtmlRenderer(
                                    html = html,
                                    modifier = Modifier.fillMaxWidth(if (shortScreen) 0.7f else 1f)
                                )
                                isHtml -> HtmlRenderer(html = html)
                                else -> MarkdownText(text = html)
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        if (content.answer != null) {
                            MultiplicationTableMultiplyingWithFlowersAnswerSection(
                                content = content,
                                onAnswered = { points -> award(points) }
                            )
                        }
                    }

                    if (showTable) {
                        MultiplicationTableVisualizer(
                            factor = TABLE_OF_SIX,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(if (fitsOnScreen) 12.dp else 24.dp))

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

                    if (onNavigateToMultiplicationTableHub != null) {
                        Spacer(Modifier.height(16.dp))
                        FilledTonalButton(
                            onClick = onNavigateToMultiplicationTableHub,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(mcs.multiplicationTable, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(if (fitsOnScreen) 8.dp else 48.dp))
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
                MultiplicationTableMultiplyingWithFlowersSourcesMenu(
                    sources = sources,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun MultiplicationTableMultiplyingWithFlowersAnswerSection(
    content: SMGameContent,
    onAnswered: (Int) -> Unit
) {
    val s = LocalUiStrings.current
    val xs = multiplicationTableMultiplyingWithFlowersScreenStringsForLanguage(LocalAppLanguage.current)
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
private fun MultiplicationTableMultiplyingWithFlowersSourcesMenu(
    sources: List<HTSource>,
    modifier: Modifier = Modifier
) {
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
            onDismissRequest = { activeSource = null; showSourcesMenu = false }
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
