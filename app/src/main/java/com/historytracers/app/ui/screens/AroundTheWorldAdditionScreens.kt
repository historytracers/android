// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.AutoSizeTitleText
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.TextRenderer
import com.historytracers.app.ui.components.drawYupanaRow
import com.historytracers.app.ui.components.getMarkersForDigit
import com.historytracers.app.ui.components.safeOpenUri
import com.historytracers.app.ui.features.aroundTheWorldAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameFile
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.historytracers.app.ui.components.ScreenAudioButton

private const val ATW_ADD_SMARTPHONE_FILE = "49bf2fba-e9f4-4650-ab96-4d4901795477"
private const val ATW_ADD_SECTION_ID = "around_the_world_addition"

private const val ATW_ADD_INTRO = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a01"
private const val ATW_ADD_COMPLEMENT = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a02"
private const val ATW_ADD_AFRICA = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a03"
private const val ATW_ADD_AFRICA_GAME = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a04"
private const val ATW_ADD_QUIPU = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a05"
private const val ATW_ADD_QUIPU_GAME = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a06"
private const val ATW_ADD_MESO = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a07"
private const val ATW_ADD_MESO_GAME = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a08"
private const val ATW_ADD_EUROPE = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a09"
private const val ATW_ADD_EUROPE_GAME = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a0a"
private const val ATW_ADD_INDIA = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a0b"
private const val ATW_ADD_INDIA_GAME = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a0c"
private const val ATW_ADD_CHINA = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a0d"
private const val ATW_ADD_CHINA_GAME = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a0e"
private const val ATW_ADD_YUPANA = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a0f"
private const val ATW_ADD_YUPANA_GAME = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a10"
private const val ATW_ADD_JAPAN = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a11"
private const val ATW_ADD_JAPAN_GAME = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a12"
private const val ATW_ADD_RUSSIA = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a13"
private const val ATW_ADD_RUSSIA_GAME = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a14"
private const val ATW_ADD_CONCLUSION = "a8f3c1d2-5b64-4e17-9c2a-1d0f3b6e8a15"

private enum class AtwAddTool { BONES, QUIPU, MESO, CALCULI, TYPED, SUANPAN, YUPANA, SOROBAN, SCHYOTY }

@Composable
fun AroundTheWorldAdditionIntroScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_INTRO,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionComplementScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_COMPLEMENT,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionAfricaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_AFRICA,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionAfricaGameScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_AFRICA_GAME,
        tool = AtwAddTool.BONES,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionQuipuScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_QUIPU,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionQuipuGameScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_QUIPU_GAME,
        tool = AtwAddTool.QUIPU,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionMesoamericaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_MESO,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionMesoamericaGameScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_MESO_GAME,
        tool = AtwAddTool.MESO,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionEuropeScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_EUROPE,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionEuropeGameScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_EUROPE_GAME,
        tool = AtwAddTool.CALCULI,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionIndiaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_INDIA,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionIndiaGameScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_INDIA_GAME,
        tool = AtwAddTool.TYPED,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionChinaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_CHINA,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionChinaGameScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_CHINA_GAME,
        tool = AtwAddTool.SUANPAN,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionYupanaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_YUPANA,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionYupanaGameScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_YUPANA_GAME,
        tool = AtwAddTool.YUPANA,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionJapanScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_JAPAN,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionJapanGameScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_JAPAN_GAME,
        tool = AtwAddTool.SOROBAN,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionRussiaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_RUSSIA,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionRussiaGameScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_RUSSIA_GAME,
        tool = AtwAddTool.SCHYOTY,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldAdditionConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    AtwAdditionContent(
        contentId = ATW_ADD_CONCLUSION,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateToHome = onNavigateToHome
    )
}

@Composable
private fun AtwAdditionContent(
    contentId: String,
    tool: AtwAddTool?,
    currentScore: Int,
    onScoreChanged: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToHome: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = aroundTheWorldAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    var game by remember { mutableStateOf<SMGameFile?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(language) {
        when (val result = repo.loadAndParse("$language/$ATW_ADD_SMARTPHONE_FILE")) {
            is ContentResult.SMGame -> game = result.data
            is ContentResult.Error -> error = result.message
            else -> error = s.common.unsupportedContentType
        }
    }

    val content = game?.content?.firstOrNull { it.id == contentId }

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    fun award(points: Int) {
        if (points <= 0) return
        totalAwarded += points
        onScoreChanged(initialScore + totalAwarded)
    }

    LaunchedEffect(content) {
        if (content != null && onNavigateToHome != null) {
            preferences.recordLessonCompletion()
            preferences.markAroundTheWorldAdditionSectionCompleted(ATW_ADD_SECTION_ID)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
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
                    text = hts.aroundTheWorldAddition,
                    modifier = Modifier.padding(start = 8.dp).weight(1f)
                )
                ScreenAudioButton(
                    lessonId = ATW_ADD_SMARTPHONE_FILE,
                    contentId = contentId
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
                        val raw = text.text ?: ""
                        if (text.format == "markdown") {
                            MarkdownText(text = raw)
                        } else {
                            TextRenderer(text = text, repo = repo)
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    if (contentId == ATW_ADD_COMPLEMENT) {
                        AtwAddTenGroupVisual()
                        Spacer(Modifier.height(12.dp))
                    }

                    if (tool != null) {
                        Spacer(Modifier.height(8.dp))
                        AtwAdditionExercise(tool = tool, onSolved = { award(2) })
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

                    if (onNavigateToHome != null) {
                        Spacer(Modifier.height(16.dp))
                        FilledTonalButton(
                            onClick = onNavigateToHome,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(xs.home, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(48.dp))
                }
            }

            content?.sourceMenu?.takeIf { it.isNotEmpty() }?.let { sources ->
                AtwAdditionSourcesMenu(
                    sources = sources,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun AtwAddTenGroupVisual() {
    val xs = aroundTheWorldAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val baseSquares = 9
    val addedSquares = 3

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 12.dp)
        ) {
            val pad = size.width * 0.02f
            val usable = size.width - 2f * pad
            val gap = usable * 0.012f
            val squareSize = (usable - 11f * gap) / 12f
            val total = baseSquares + addedSquares
            val rowWidth = total * squareSize + (total - 1) * gap
            val startX = (size.width - rowWidth) / 2f
            val top = (size.height - squareSize) / 2f

            for (index in 0 until total) {
                val left = startX + index * (squareSize + gap)
                drawRect(
                    color = if (index < baseSquares) Color(0xFFADD8E6) else Color(0xFFFFFFE0),
                    topLeft = Offset(left, top),
                    size = Size(squareSize, squareSize)
                )
                drawRect(
                    color = Color(0xFF37474F),
                    topLeft = Offset(left, top),
                    size = Size(squareSize, squareSize),
                    style = Stroke(width = 2f)
                )
            }

            val groupWidth = 10 * squareSize + 9 * gap
            drawRoundRect(
                color = Color(0xFFC0392B),
                topLeft = Offset(startX - gap * 0.7f, top - gap * 0.7f),
                size = Size(groupWidth + gap * 1.4f, squareSize + gap * 1.4f),
                cornerRadius = CornerRadius(squareSize * 0.12f),
                style = Stroke(
                    width = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
            )
        }

        Text(
            text = xs.complementCaption,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = xs.complementNote,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun AtwAdditionExercise(tool: AtwAddTool, onSolved: () -> Unit) {
    val s = LocalUiStrings.current
    val xs = aroundTheWorldAdditionScreenStringsForLanguage(LocalAppLanguage.current)

    var addends by remember { mutableStateOf(newAtwAddends()) }
    val a = addends.first
    val b = addends.second
    val sum = a + b

    var value by remember { mutableIntStateOf(0) }
    var solved by remember { mutableStateOf(false) }
    var exerciseKey by remember { mutableIntStateOf(0) }
    var step by remember { mutableIntStateOf(1) }
    var stepCompleted by remember { mutableStateOf(false) }
    val isStepTool = tool == AtwAddTool.CALCULI || tool == AtwAddTool.SUANPAN ||
        tool == AtwAddTool.SOROBAN || tool == AtwAddTool.SCHYOTY

    LaunchedEffect(value, sum, solved, step, stepCompleted, tool) {
        when {
            solved -> Unit
            tool == AtwAddTool.TYPED || tool == AtwAddTool.YUPANA -> Unit
            isStepTool -> {
                val target = if (step == 1) a else sum
                if (!stepCompleted && value == target) {
                    if (step == 1) {
                        stepCompleted = true
                    } else {
                        solved = true
                        onSolved()
                    }
                }
            }
            else -> {
                if (value == sum) {
                    solved = true
                    onSolved()
                }
            }
        }
    }

    fun newExercise() {
        addends = newAtwAddends()
        value = 0
        solved = false
        step = 1
        stepCompleted = false
        exerciseKey++
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isStepTool) {
            Text(
                text = xs.represent.format(a, b),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Text(
            text = "$a + $b = ?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        if (isStepTool) {
            Text(
                text = "${s.common.stepPrefix}$step/2",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = when (tool) {
                    AtwAddTool.CALCULI -> if (step == 1) {
                        xs.calculiStep1.format(a)
                    } else {
                        xs.calculiStep2.format(b, a, b, sum, 10 - b, sum)
                    }
                    AtwAddTool.SCHYOTY -> if (step == 1) {
                        xs.schyotyStep1.format(a)
                    } else {
                        xs.schyotyStep2.format(b, a, b, sum, 10 - b, sum)
                    }
                    else -> if (step == 1) {
                        xs.abacusStep1.format(a)
                    } else {
                        xs.abacusStep2.format(b, a, b, sum, 10 - b, sum)
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        when (tool) {
            AtwAddTool.BONES -> {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    drawBoneWithMarks(value)
                }
                AtwAdditionValueStepper(
                    value = value,
                    enabled = !solved,
                    onChange = { value = it }
                )
            }

            AtwAddTool.QUIPU -> {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    drawAtwAddQuipu(value)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${xs.tens}: ${value / 10}", fontWeight = FontWeight.Bold)
                    Text("${xs.units}: ${value % 10}", fontWeight = FontWeight.Bold)
                }
                AtwAdditionValueStepper(
                    value = value,
                    enabled = !solved,
                    onChange = { value = it }
                )
            }

            AtwAddTool.MESO -> {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    drawAtwAddMeso(value)
                }
                AtwAdditionValueStepper(
                    value = value,
                    enabled = !solved,
                    onChange = { value = it }
                )
            }

            AtwAddTool.YUPANA -> {
                AtwYupanaExercise(
                    a = a,
                    b = b,
                    solved = solved,
                    exerciseKey = exerciseKey,
                    onCorrect = {
                        if (!solved) {
                            solved = true
                            onSolved()
                        }
                    }
                )
            }

            AtwAddTool.CALCULI -> {
                AbacusApp(
                    columns = 6,
                    upperMax = 1,
                    lowerMax = 4,
                    columnHeadings = calculiHeadings,
                    columnPlaces = calculiPlaces,
                    frozen = solved || stepCompleted,
                    resetKey = exerciseKey,
                    showReset = false,
                    onValueChange = { value = it.toInt() }
                )
            }

            AtwAddTool.SUANPAN -> {
                AbacusApp(
                    columns = 2,
                    upperMax = 2,
                    lowerMax = 5,
                    columnHeadings = listOf(xs.tens, xs.units),
                    frozen = solved || stepCompleted,
                    resetKey = exerciseKey,
                    showReset = false,
                    onValueChange = { value = it.toInt() }
                )
            }

            AtwAddTool.SOROBAN -> {
                AbacusApp(
                    columns = 2,
                    upperMax = 1,
                    lowerMax = 4,
                    columnHeadings = listOf(xs.tens, xs.units),
                    frozen = solved || stepCompleted,
                    resetKey = exerciseKey,
                    showReset = false,
                    onValueChange = { value = it.toInt() }
                )
            }

            AtwAddTool.SCHYOTY -> {
                SchyotyAbacus(
                    frozen = solved || stepCompleted,
                    resetKey = exerciseKey,
                    onValueChange = { value = it.toInt() }
                )
            }

            AtwAddTool.TYPED -> {
                AtwAdditionTypedExercise(
                    a = a,
                    b = b,
                    solved = solved,
                    exerciseKey = exerciseKey,
                    onCorrect = {
                        if (!solved) {
                            solved = true
                            onSolved()
                        }
                    }
                )
            }
        }

        if (isStepTool && stepCompleted && !solved) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = xs.stepCorrect,
                color = Color(0xFF2E7D32),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            FilledTonalButton(
                onClick = {
                    step = 2
                    stepCompleted = false
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = s.common.nextStep,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (solved) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = xs.correctMessage,
                color = Color(0xFF2E7D32),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (tool != AtwAddTool.TYPED || solved) {
            Spacer(Modifier.height(12.dp))

            FilledTonalButton(
                onClick = { newExercise() },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Text(
                    text = s.common.newExercise,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun toggleAtwMarker(markers: Set<Int>, column: Int): Set<Int> =
    if (column in markers) markers - column else markers + column

@Composable
private fun AtwYupanaExercise(
    a: Int,
    b: Int,
    solved: Boolean,
    exerciseKey: Int,
    onCorrect: () -> Unit
) {
    val s = LocalUiStrings.current
    val xs = aroundTheWorldAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val sum = a + b
    var phase by remember(exerciseKey) { mutableIntStateOf(0) }
    var redMarkers by remember(exerciseKey) { mutableStateOf(emptySet<Int>()) }
    var blueMarkers by remember(exerciseKey) { mutableStateOf(emptySet<Int>()) }
    var greenMarkers by remember(exerciseKey) { mutableStateOf(emptySet<Int>()) }
    var carryPlaced by remember(exerciseKey) { mutableStateOf(false) }
    var phaseCompleted by remember(exerciseKey) { mutableStateOf(false) }
    var feedback by remember(exerciseKey) { mutableStateOf("") }

    val redExpected = getMarkersForDigit(a)
    val blueExpected = getMarkersForDigit(b)
    val greenExpected = getMarkersForDigit(sum % 10)

    fun complete() {
        feedback = xs.yupanaPerfect.format(a, b, sum)
        onCorrect()
    }

    fun toggleColumn(column: Int) {
        if (phaseCompleted || solved) return
        when (phase) {
            0 -> {
                val next = toggleAtwMarker(redMarkers, column)
                redMarkers = next
                if (next == redExpected) {
                    phaseCompleted = true
                    feedback = xs.stepCorrect
                }
            }

            1 -> {
                val next = toggleAtwMarker(blueMarkers, column)
                blueMarkers = next
                if (next == blueExpected) {
                    phaseCompleted = true
                    feedback = xs.stepCorrect
                }
            }

            else -> {
                val next = toggleAtwMarker(greenMarkers, column)
                greenMarkers = next
                if (next == greenExpected) {
                    if (carryPlaced) {
                        complete()
                    } else {
                        feedback = xs.yupanaCarryPrompt
                    }
                } else {
                    feedback = ""
                }
            }
        }
    }

    fun toggleCarry() {
        if (phase != 2 || solved) return
        val next = !carryPlaced
        carryPlaced = next
        if (next && greenMarkers == greenExpected) {
            complete()
        } else if (!next && greenMarkers == greenExpected) {
            feedback = xs.yupanaCarryPrompt
        } else {
            feedback = ""
        }
    }

    val stepText = when (phase) {
        0 -> xs.yupanaStep1.format(a)
        1 -> xs.yupanaStep2.format(b)
        else -> if (greenExpected.isEmpty()) {
            xs.yupanaStep3Zero
        } else {
            xs.yupanaStep3.format(sum % 10)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${s.common.stepPrefix}${phase + 1}/3",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stepText,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )

        Spacer(Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(horizontal = 8.dp)
                .pointerInput(
                    phase,
                    phaseCompleted,
                    solved,
                    exerciseKey,
                    redMarkers,
                    blueMarkers,
                    greenMarkers,
                    carryPlaced
                ) {
                    detectTapGestures { offset ->
                        if (phaseCompleted || solved) return@detectTapGestures
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()
                        val margin = 3f / 860f * w
                        val usable = w - 2f * margin
                        val colW = usable / 4f
                        val top = h * 0.05f
                        val totalH = h * 0.9f
                        val gap = h * 0.04f
                        val rowH = (totalH - gap) / 2f
                        val unitsTop = top + rowH + gap
                        when {
                            offset.y in top..(top + rowH) -> {
                                if (phase == 2) toggleCarry()
                            }

                            offset.y in unitsTop..(unitsTop + rowH) -> {
                                val col = (((offset.x - margin) / colW).toInt().coerceIn(0, 3)) + 1
                                toggleColumn(col)
                            }
                        }
                    }
                }
                .semantics {
                    customActions = buildList {
                        for (column in 1..4) {
                            add(
                                CustomAccessibilityAction(xs.yupanaColumn.format(column)) {
                                    toggleColumn(column)
                                    true
                                }
                            )
                        }
                        if (phase == 2) {
                            add(
                                CustomAccessibilityAction(xs.typedCarry) {
                                    toggleCarry()
                                    true
                                }
                            )
                        }
                    }
                }
        ) {
            drawAtwAddYupana(
                phase = phase,
                redMarkers = redMarkers,
                blueMarkers = blueMarkers,
                greenMarkers = greenMarkers,
                carryPlaced = carryPlaced
            )
        }

        if (feedback.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = feedback,
                color = Color(0xFF2E7D32),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (phaseCompleted && phase < 2 && !solved) {
            Spacer(Modifier.height(10.dp))
            FilledTonalButton(
                onClick = {
                    phase += 1
                    phaseCompleted = false
                    feedback = if (phase == 2 && greenExpected.isEmpty()) {
                        xs.yupanaCarryPrompt
                    } else {
                        ""
                    }
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = s.common.nextStep,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AtwAdditionValueStepper(
    value: Int,
    enabled: Boolean,
    onChange: (Int) -> Unit
) {
    val s = LocalUiStrings.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (enabled && value > 0) onChange(value - 1) },
            enabled = enabled && value > 0
        ) {
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFF1565C0),
                modifier = Modifier.size(44.dp)
            )
        }
        Text(
            text = "${s.common.value}: $value",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        IconButton(
            onClick = { if (enabled && value < 18) onChange(value + 1) },
            enabled = enabled && value < 18
        ) {
            Icon(
                Icons.Filled.KeyboardArrowUp,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(44.dp)
            )
        }
    }
}

private val AtwTypedBlue = Color(0xFF2C5F8A)
private val AtwTypedInputBackground = Color(0xFFEAF3FB)
private val AtwTypedActiveCell = Color(0xFFADD8E6)
private val AtwTypedBaseSquare = Color(0xFFADD8E6)
private val AtwTypedAddedSquare = Color(0xFFFFFFE0)
private val AtwTypedComplement = Color(0xFFE67E22)
private val AtwTypedTenGroup = Color(0xFFC0392B)
private val AtwTypedBorder = Color(0xFF333333)

@Composable
private fun AtwAdditionTypedExercise(
    a: Int,
    b: Int,
    solved: Boolean,
    exerciseKey: Int,
    onCorrect: () -> Unit
) {
    val xs = aroundTheWorldAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val sum = a + b
    var column by remember(exerciseKey) { mutableIntStateOf(0) }
    var digit by remember(exerciseKey) { mutableStateOf("") }
    var carry by remember(exerciseKey) { mutableStateOf("") }
    var tensDigit by remember(exerciseKey) { mutableStateOf("") }
    var unitsSolved by remember(exerciseKey) { mutableStateOf(false) }
    var feedback by remember(exerciseKey) { mutableStateOf("") }
    var feedbackIsError by remember(exerciseKey) { mutableStateOf(false) }

    fun evaluateUnits(newDigit: String, newCarry: String) {
        if (unitsSolved || solved) return
        val correctDigit = sum % 10
        val digitOk = newDigit.isNotEmpty() && newDigit.toIntOrNull() == correctDigit
        val carryOk = newCarry.isNotEmpty() && newCarry.toIntOrNull() == 1

        when {
            newDigit.isNotEmpty() && !digitOk -> {
                feedback = xs.typedWrongDigit.format(a, 10 - a, b)
                feedbackIsError = true
            }

            newCarry.isNotEmpty() && !carryOk -> {
                feedback = xs.typedWrongCarry.format(a, b)
                feedbackIsError = true
            }

            digitOk && carryOk -> {
                unitsSolved = true
                feedback = xs.typedNextColumnHint
                feedbackIsError = false
            }

            else -> {
                feedback = ""
                feedbackIsError = false
            }
        }
    }

    fun evaluateTens(newTens: String) {
        if (solved) return
        if (newTens.isEmpty()) {
            feedback = ""
            feedbackIsError = false
            return
        }
        if (newTens.toIntOrNull() == 1) {
            feedback = xs.typedMsgCarry.format(a, b, sum, sum % 10)
            feedbackIsError = false
            onCorrect()
        } else {
            feedback = xs.typedWrongTens
            feedbackIsError = true
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = xs.tens,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = xs.units,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when {
                        !solved && column == 0 && !unitsSolved -> {
                            AtwTypedInputCell(
                                value = carry,
                                onValueChange = {
                                    carry = it
                                    evaluateUnits(digit, it)
                                },
                                label = xs.typedCarry,
                                active = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        unitsSolved || solved || column == 1 -> {
                            AtwTypedTextCell(
                                text = "1",
                                active = column == 1,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        else -> {
                            AtwTypedTextCell(
                                text = "",
                                active = false,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    AtwTypedTextCell(text = "", active = false, modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AtwTypedTextCell(text = "", active = false, modifier = Modifier.weight(1f))
                    AtwTypedTextCell(text = a.toString(), active = true, modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AtwTypedTextCell(text = "", active = false, modifier = Modifier.weight(1f))
                    AtwTypedTextCell(text = "+ $b", active = true, modifier = Modifier.weight(1f))
                }

                Row(Modifier.fillMaxWidth()) {
                    Box(Modifier.weight(1f))
                    Box(
                        Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                            .height(2.dp)
                            .background(AtwTypedBorder)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when {
                        !solved && column == 1 -> {
                            AtwTypedInputCell(
                                value = tensDigit,
                                onValueChange = {
                                    tensDigit = it
                                    evaluateTens(it)
                                },
                                label = xs.typedTens,
                                active = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        else -> {
                            AtwTypedTextCell(
                                text = if (solved || column == 1) "1" else "",
                                active = column == 1,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    if (!solved) {
                        if (column == 0 && !unitsSolved) {
                            AtwTypedInputCell(
                                value = digit,
                                onValueChange = {
                                    digit = it
                                    evaluateUnits(it, carry)
                                },
                                label = xs.typedResult,
                                active = true,
                                modifier = Modifier.weight(1f)
                            )
                        } else if (unitsSolved || column == 1) {
                            AtwTypedTextCell(
                                text = (sum % 10).toString(),
                                active = false,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            AtwTypedTextCell(
                                text = "",
                                active = false,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        AtwTypedTextCell(
                            text = (sum % 10).toString(),
                            active = false,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        if (feedback.isNotEmpty()) {
            Text(
                text = feedback,
                color = if (feedbackIsError) MaterialTheme.colorScheme.error else Color(0xFF2E7D32),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (column == 0 && unitsSolved && !solved) {
            Spacer(Modifier.height(10.dp))
            FilledTonalButton(
                onClick = {
                    column = 1
                    feedback = ""
                    feedbackIsError = false
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = xs.nextColumn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        AtwTypedSquares(a = a, b = b, solved = solved)
    }
}

@Composable
private fun AtwTypedTextCell(
    text: String,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .background(
                if (active) AtwTypedActiveCell else Color.Transparent,
                RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AtwTypedInputCell(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .background(
                if (active) AtwTypedActiveCell else Color.Transparent,
                RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = { raw ->
                onValueChange(raw.filter { it.isDigit() }.takeLast(1))
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = AtwTypedBlue
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(34.dp)
                .height(34.dp)
                .background(AtwTypedInputBackground, RoundedCornerShape(4.dp))
                .border(2.dp, AtwTypedBlue, RoundedCornerShape(4.dp))
                .padding(2.dp)
                .semantics { contentDescription = label }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AtwTypedSquares(a: Int, b: Int, solved: Boolean) {
    val xs = aroundTheWorldAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val total = a + b
    val complement = 10 - a
    val outside = total - 10

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = xs.typedSquaresLabel,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(6.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .drawBehind {
                        drawRoundRect(
                            color = AtwTypedTenGroup,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f))
                            ),
                            cornerRadius = CornerRadius(6.dp.toPx())
                        )
                    }
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                for (index in 0 until 10) {
                    AtwTypedSquare(index = index, base = a)
                }
            }
            for (index in 10 until total) {
                AtwTypedSquare(index = index, base = a)
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = if (solved) "$a + $b = $total" else "$a + $b = ?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = if (solved) {
                xs.typedSquaresNote.format(a, complement, complement, outside)
            } else {
                xs.typedComplementPending.format(a, complement, complement)
            },
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = Color(0xFF8A4B08),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun AtwTypedSquare(index: Int, base: Int) {
    val isBase = index < base
    val isComplement = index >= base && index < 10

    Box(
        modifier = Modifier
            .size(24.dp)
            .background(
                if (isBase) AtwTypedBaseSquare else AtwTypedAddedSquare,
                RoundedCornerShape(4.dp)
            )
            .border(2.dp, AtwTypedBorder, RoundedCornerShape(4.dp))
    ) {
        if (isComplement) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .border(2.dp, AtwTypedComplement, RoundedCornerShape(2.dp))
            )
        }
    }
}

private fun newAtwAddends(): Pair<Int, Int> {
    while (true) {
        val a = Random.nextInt(1, 10)
        val b = Random.nextInt(1, 10)
        if (a + b >= 10) return a to b
    }
}

private fun DrawScope.drawAtwAddQuipu(value: Int) {
    val w = size.width
    val h = size.height
    val cord = Color(0xFFA1887F)
    val knot = Color(0xFF5D4037)
    val mainY = h * 0.10f
    val cordX = w * 0.5f
    val cordBottom = h * 0.95f

    drawLine(
        cord,
        Offset(w * 0.15f, mainY),
        Offset(w * 0.85f, mainY),
        strokeWidth = (h * 0.03f).coerceAtLeast(4f)
    )
    drawLine(
        cord,
        Offset(cordX, mainY),
        Offset(cordX, cordBottom),
        strokeWidth = (h * 0.016f).coerceAtLeast(3f)
    )

    val tens = (value / 10).coerceIn(0, 1)
    val units = (value % 10).coerceIn(0, 9)

    val tensTop = mainY + h * 0.08f
    val tensBottom = mainY + h * 0.28f
    val unitsTop = mainY + h * 0.40f
    val unitsBottom = cordBottom - h * 0.05f

    val tensGap = if (tens > 1) (tensBottom - tensTop) / (tens - 1) else 0f
    val tensRadius = minOf(h * 0.045f, if (tens > 1) tensGap * 0.45f else h * 0.045f)
    repeat(tens) { index ->
        drawCircle(
            knot,
            radius = tensRadius,
            center = Offset(cordX, tensTop + index * tensGap)
        )
    }

    val unitsGap = if (units > 1) (unitsBottom - unitsTop) / (units - 1) else 0f
    val unitsRadius = minOf(h * 0.035f, if (units > 1) unitsGap * 0.45f else h * 0.035f)
    repeat(units) { index ->
        drawCircle(
            knot,
            radius = unitsRadius,
            center = Offset(cordX, unitsTop + index * unitsGap)
        )
    }
}

private fun DrawScope.drawAtwAddMeso(value: Int) {
    val ink = Color(0xFF5A3F2C)
    val bars = (value / 5).coerceIn(0, 3)
    val dots = (value % 5).coerceIn(0, 4)
    val dotRadius = size.minDimension * 0.055f
    val gap = size.height * 0.045f
    val barWidth = 4 * dotRadius * 2f + 3 * gap
    val barHeight = size.height * 0.11f
    val barsHeight = if (bars > 0) bars * barHeight + (bars - 1) * gap else 0f
    val dotsHeight = if (dots > 0) dotRadius * 2f + gap else 0f
    val contentHeight = barsHeight + dotsHeight
    var y = size.height * 0.94f - contentHeight

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

private fun DrawScope.drawAtwAddYupana(
    phase: Int,
    redMarkers: Set<Int>,
    blueMarkers: Set<Int>,
    greenMarkers: Set<Int>,
    carryPlaced: Boolean
) {
    val margin = 3f / 860f * size.width
    val usableWidth = size.width - 2f * margin
    val colWidth = usableWidth / 4f
    val top = size.height * 0.05f
    val totalHeight = size.height * 0.9f
    val rowGap = size.height * 0.04f
    val rowHeight = (totalHeight - rowGap) / 2f

    drawYupanaRow(
        cellOriginX = margin,
        cellOriginY = top,
        cellWidth = colWidth,
        cellHeight = rowHeight,
        canvasSize = size,
        leftMarkers = emptySet(),
        rightMarkers = emptySet(),
        resultMarkers = if (carryPlaced) getMarkersForDigit(1) else emptySet(),
        carryMarker = phase == 2 && !carryPlaced
    )

    drawYupanaRow(
        cellOriginX = margin,
        cellOriginY = top + rowHeight + rowGap,
        cellWidth = colWidth,
        cellHeight = rowHeight,
        canvasSize = size,
        leftMarkers = if (phase == 0 || phase == 1) redMarkers else emptySet(),
        rightMarkers = if (phase == 1) blueMarkers else emptySet(),
        resultMarkers = if (phase == 2) greenMarkers else emptySet()
    )
}

@Composable
private fun AtwAdditionSourcesMenu(sources: List<HTSource>, modifier: Modifier = Modifier) {
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
            onDismissRequest = {
                activeSource = null
                showSourcesMenu = false
            }
        ) {
            activeSource?.let { source ->
                val url = atwAddSourceUrl(source.page)
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

private fun atwAddSourceUrl(page: String?): String {
    val value = page.orEmpty()
    return when {
        value.startsWith("index.html") -> "https://www.historytracers.org/$value"
        else -> value
    }
}
