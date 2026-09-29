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
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppCalendar
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.DateUtils
import com.historytracers.app.ui.components.HtmlRenderer
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.TextRenderer
import com.historytracers.app.ui.components.drawYupanaRow
import com.historytracers.app.ui.components.getMarkersForDigit
import com.historytracers.app.ui.features.aroundTheWorldScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import com.historytracers.common.HTDate
import com.historytracers.common.HTSource
import com.historytracers.common.SMGameContent
import com.historytracers.common.SMGameFile
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val ATW_SMARTPHONE_FILE = "1071ad37-4ee3-477a-85bb-9cd3e40e3139"
private const val ATW_HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"
private const val ATW_SECTION_ID = "around_the_world"

private const val ATW_INTRO = "7844d3a8-bc63-4f04-9b3a-731efdcd095d"
private const val ATW_MATH = "6bd23cf5-8b30-470d-8e72-cc3d2e763a8a"
private const val ATW_BONES = "bead6121-9596-415c-9470-01fbb8936700"
private const val ATW_QUIPUS = "5f0d26b0-b463-4d1d-a643-d2bb53b7fa24"
private const val ATW_PRACTICE_QUIPU = "4cfea7ca-4581-4ce7-8b6b-7b0cb6c0ab4c"
private const val ATW_MESO = "9e1a6a43-6127-4773-96f8-b0c01afef69f"
private const val ATW_CALCULI = "1fac8da7-30e7-4191-88d0-77aad9f537ae"
private const val ATW_PRACTICE_CALCULI = "0204fa36-44ce-439a-95f4-506faf2071aa"
private const val ATW_INDIA = "fd121753-9c08-4aef-931c-a2e98ab0c827"
private const val ATW_CHINA = "f229d982-20c6-4e7f-8d88-e3440b45e356"
private const val ATW_PRACTICE_SUANPAN = "5bf9c904-3e80-4589-aabf-736e655e236a"
private const val ATW_YUPANA = "8b6f0933-069d-419b-aa69-586eac0110d8"
private const val ATW_PRACTICE_YUPANA = "baec6b37-e625-48b2-bbde-9ac643ed1962"
private const val ATW_JAPAN = "fbc0deff-4068-4126-8438-f689bd492740"
private const val ATW_PRACTICE_SOROBAN = "013cfc0f-9320-400f-9cc4-11f6331514fe"
private const val ATW_RUSSIA = "b37b0581-8eea-4c39-a0c7-3db8b9541afa"
private const val ATW_PRACTICE_SCHYOTY = "97e8f1aa-6a33-484f-8fc1-03a40b033f67"
private const val ATW_CONCLUSION = "82ccdca5-86a7-493b-b1d9-eab19b1fd555"

private enum class AtwTool { BONES, QUIPU, MESO, CALCULI, SUANPAN, SOROBAN, SCHYOTY, YUPANA, SEQUENCE }

@Composable
fun AroundTheWorldIntroScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_INTRO,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldMathEvolutionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_MATH,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldBonesScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_BONES,
        tool = AtwTool.BONES,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldQuipusScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_QUIPUS,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldPracticeQuipuScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_PRACTICE_QUIPU,
        tool = AtwTool.QUIPU,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldMesoamericanScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_MESO,
        tool = AtwTool.MESO,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldCalculiScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_CALCULI,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldPracticeCalculiScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_PRACTICE_CALCULI,
        tool = AtwTool.CALCULI,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldIndiaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_INDIA,
        tool = AtwTool.SEQUENCE,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldChinaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_CHINA,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldPracticeSuanpanScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_PRACTICE_SUANPAN,
        tool = AtwTool.SUANPAN,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldYupanaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_YUPANA,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldPracticeYupanaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_PRACTICE_YUPANA,
        tool = AtwTool.YUPANA,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldJapanScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_JAPAN,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldPracticeSorobanScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_PRACTICE_SOROBAN,
        tool = AtwTool.SOROBAN,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldRussiaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_RUSSIA,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldPracticeSchyotyScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_PRACTICE_SCHYOTY,
        tool = AtwTool.SCHYOTY,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    )
}

@Composable
fun AroundTheWorldConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    AtwContent(
        contentId = ATW_CONCLUSION,
        tool = null,
        currentScore = currentScore,
        onScoreChanged = onScoreChanged,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateToHome = onNavigateToHome
    )
}

private fun atwSmileEmoji(smile: String): String = when (smile) {
    "thinking", "think" -> "\uD83E\uDD14"
    "happy", "smile" -> "\uD83D\uDE0A"
    "nerd" -> "\uD83E\uDD13"
    "shocking", "surprise", "surprising" -> "\uD83D\uDE32"
    "party" -> "\uD83E\uDD73"
    "inlove", "loving" -> "\uD83D\uDE0D"
    else -> "\uD83D\uDE0A"
}

private fun atwSourceUrl(page: String): String =
    if (page.startsWith("index.html")) ATW_HISTORYTRACERS_ORIGIN + page else page

@Composable
private fun resolveAtwDates(text: String, dates: List<HTDate>?): String {
    if (!text.contains("<htdate")) return text
    val common = LocalUiStrings.current.common
    val calendar = LocalAppCalendar.current
    var result = text
    DateUtils.formatDate(dates, calendar, common)?.forEachIndexed { index, formatted ->
        result = result.replace("<htdate$index>", formatted)
    }
    return result
}

@Composable
private fun AtwContent(
    contentId: String,
    tool: AtwTool?,
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToHome: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = aroundTheWorldScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    var game by remember { mutableStateOf<SMGameFile?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        when (val result = repo.loadAndParse("$language/$ATW_SMARTPHONE_FILE")) {
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
        if (node != null && onNavigateToHome != null) {
            preferences.recordLessonCompletion()
            preferences.markAroundTheWorldSectionCompleted(ATW_SECTION_ID)
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
                    text = hts.aroundTheWorld,
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
                        val raw = text.text ?: ""
                        if (text.format == "markdown") {
                            MarkdownText(text = resolveAtwDates(raw, text.fillDates))
                        } else if (raw.contains("<svg")) {
                            HtmlRenderer(html = raw)
                        } else {
                            TextRenderer(text = text, repo = repo)
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    if (tool != null) {
                        Spacer(Modifier.height(8.dp))
                        AtwExercise(tool = tool, onSolved = { award(2) })
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

            content?.takeIf { it.smile?.isNotEmpty() == true }?.let { node ->
                Text(
                    text = atwSmileEmoji(node.smile),
                    fontSize = 40.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 8.dp, end = 8.dp)
                )
            }

            content?.sourceMenu?.takeIf { it.isNotEmpty() }?.let { sources ->
                AtwSourcesMenu(
                    sources = sources,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}

@Composable
private fun AtwExercise(tool: AtwTool, onSolved: () -> Unit) {
    val s = LocalUiStrings.current
    val xs = aroundTheWorldScreenStringsForLanguage(LocalAppLanguage.current)

    fun randomTarget(): Int = if (tool == AtwTool.YUPANA) Random.nextInt(1, 10) else Random.nextInt(1, 11)

    var target by remember { mutableStateOf(randomTarget()) }
    var seqStart by remember { mutableStateOf(Random.nextInt(1, 7)) }
    var missingIndex by remember { mutableStateOf(Random.nextInt(1, 4)) }
    var value by remember { mutableStateOf(0) }
    var solved by remember { mutableStateOf(false) }

    val seq = remember(seqStart) { (seqStart..seqStart + 4).toList() }
    val effectiveTarget = if (tool == AtwTool.SEQUENCE) seq[missingIndex] else target

    LaunchedEffect(value, effectiveTarget, solved) {
        if (!solved && value == effectiveTarget) {
            solved = true
            onSolved()
        }
    }

    fun newExercise() {
        target = randomTarget()
        seqStart = Random.nextInt(1, 7)
        missingIndex = Random.nextInt(1, 4)
        value = 0
        solved = false
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (tool == AtwTool.SEQUENCE) xs.missingInstruction else xs.targetLabel.format(effectiveTarget),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        if (tool == AtwTool.SEQUENCE) {
            Text(
                text = seq.mapIndexed { index, n ->
                    if (index == missingIndex) "?" else n.toString()
                }.joinToString("   "),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(12.dp))
            (1..10).toList().chunked(5).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { n ->
                        TextButton(onClick = { if (!solved) value = n }) {
                            Text(
                                text = n.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (value == n) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        } else if (tool == AtwTool.CALCULI || tool == AtwTool.SUANPAN || tool == AtwTool.SOROBAN) {
            AbacusApp(
                columns = if (tool == AtwTool.CALCULI) 6 else 9,
                upperMax = if (tool == AtwTool.SUANPAN) 2 else 1,
                lowerMax = if (tool == AtwTool.SUANPAN) 5 else 4,
                columnHeadings = if (tool == AtwTool.CALCULI) calculiHeadings else emptyList(),
                columnPlaces = if (tool == AtwTool.CALCULI) calculiPlaces else null,
                frozen = solved,
                resetKey = target,
                showReset = false,
                onValueChange = { value = it.toInt() }
            )
        } else if (tool == AtwTool.SCHYOTY) {
            SchyotyAbacus(
                frozen = solved,
                resetKey = target,
                onValueChange = { value = it.toInt() }
            )
        } else {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .padding(horizontal = 16.dp)
            ) {
                when (tool) {
                    AtwTool.BONES -> drawBoneWithMarks(value)
                    AtwTool.QUIPU -> drawAtwQuipu(value)
                    AtwTool.MESO -> drawAtwMeso(value)
                    AtwTool.CALCULI -> Unit
                    AtwTool.SUANPAN -> Unit
                    AtwTool.SOROBAN -> Unit
                    AtwTool.SCHYOTY -> Unit
                    AtwTool.YUPANA -> drawAtwYupana(value)
                    AtwTool.SEQUENCE -> Unit
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (!solved && value > 0) value-- }, enabled = !solved && value > 0) {
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
                IconButton(onClick = { if (!solved && value < 10) value++ }, enabled = !solved && value < 10) {
                    Icon(
                        Icons.Filled.KeyboardArrowUp,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(44.dp)
                    )
                }
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

        Spacer(Modifier.height(12.dp))

        OutlinedButton(onClick = { newExercise() }) {
            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(s.common.newExercise, fontWeight = FontWeight.Bold)
        }
    }
}

private fun DrawScope.drawAtwQuipu(value: Int) {
    val w = size.width
    val h = size.height
    val cord = Color(0xFFA1887F)
    val knot = Color(0xFF5D4037)
    val mainY = h * 0.14f
    val cordX = w * 0.5f
    drawLine(
        cord,
        Offset(w * 0.10f, mainY),
        Offset(w * 0.90f, mainY),
        strokeWidth = (h * 0.03f).coerceAtLeast(4f)
    )
    val cordBottom = h * 0.96f
    drawLine(
        cord,
        Offset(cordX, mainY),
        Offset(cordX, cordBottom),
        strokeWidth = (h * 0.016f).coerceAtLeast(3f)
    )
    val count = value.coerceIn(0, 10)
    if (count > 0) {
        val top = mainY + h * 0.05f
        val bottom = cordBottom - h * 0.05f
        val gap = if (count > 1) (bottom - top) / (count - 1) else 0f
        val radius = minOf(h * 0.04f, if (count > 1) gap * 0.45f else h * 0.04f)
        for (i in 0 until count) {
            val y = top + i * gap
            drawCircle(knot, radius = radius, center = Offset(cordX, y))
        }
    }
}

private fun DrawScope.drawAtwMeso(value: Int) {
    val ink = Color(0xFF5A3F2C)
    val bars = value / 5
    val dots = value % 5
    val dotRadius = size.minDimension * 0.07f
    val gap = size.height * 0.06f
    val barWidth = 4 * dotRadius * 2f + 3 * gap
    val barHeight = size.height * 0.14f
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

private fun DrawScope.drawAtwYupana(value: Int) {
    val margin = 3f / 860f * size.width
    val usableWidth = size.width - 2f * margin
    val colWidth = usableWidth / 4f
    drawYupanaRow(
        cellOriginX = margin,
        cellOriginY = size.height * 0.06f,
        cellWidth = colWidth,
        cellHeight = size.height * 0.88f,
        canvasSize = size,
        leftMarkers = getMarkersForDigit(value)
    )
}

@Composable
private fun AtwSourcesMenu(sources: List<HTSource>, modifier: Modifier = Modifier) {
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
                val url = atwSourceUrl(source.page)
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
