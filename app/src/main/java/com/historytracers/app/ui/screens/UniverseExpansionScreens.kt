// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.universeExpansionScreenStringsForLanguage

private const val UNIVERSE_IMAGE_URL = "https://www.historytracers.org/images/ESA/Planck_history_of_Universe.jpg"
private const val ORIGINAL_TEXT_URL = "https://www.historytracers.org/index.html?page=class_content&arg=57091b10-84a4-468c-b81a-56bccfa2cd4a"
private const val UNIVERSE_SOURCE_WIDTH = 5305f

private val universeEraRanges = listOf(
    3000f to 5000f,
    2050f to 3000f,
    1700f to 2050f,
    1150f to 1700f,
    750f to 1150f,
    650f to 750f,
    350f to 650f,
)

private data class UniverseSource(val label: String, val url: String)

@Composable
fun UniverseExpansionScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToWhereAreWeFrom: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = universeExpansionScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val sources = listOf(
        UniverseSource(xs.imageSource, UNIVERSE_IMAGE_URL),
        UniverseSource(s.common.originalText, ORIGINAL_TEXT_URL)
    )

    var step by remember { mutableIntStateOf(0) }
    val questionStep = xs.eraTitles.size
    val conclusionStep = questionStep + 1
    val lastStep = conclusionStep

    LaunchedEffect(step) {
        if (step == conclusionStep) {
            preferences.markWhereAreWeFromSectionCompleted("everything_together")
            preferences.recordLessonCompletion()
        }
    }

    val configuration = LocalConfiguration.current
    val viewHeight = (configuration.screenHeightDp * 0.42f).dp
    val eraRange = if (step == questionStep) {
        0f to UNIVERSE_SOURCE_WIDTH
    } else {
        universeEraRanges.getOrElse(step) { universeEraRanges.last() }
    }
    val eraStart = eraRange.first / UNIVERSE_SOURCE_WIDTH
    val eraEnd = eraRange.second / UNIVERSE_SOURCE_WIDTH
    val animatedCenter by animateFloatAsState(targetValue = (eraStart + eraEnd) / 2f)
    val animatedSpan by animateFloatAsState(targetValue = eraEnd - eraStart)
    val universeImage = remember {
        context.assets.open("ESA/planck_history_of_universe.jpg").use { input ->
            BitmapFactory.decodeStream(input)?.asImageBitmap()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (step != conclusionStep) {
                    universeImage?.let { image ->
                        val canvasModifier = if (step == questionStep) {
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(image.width.toFloat() / image.height.toFloat())
                        } else {
                            Modifier
                                .fillMaxWidth()
                                .height(viewHeight)
                        }
                        Canvas(
                            modifier = canvasModifier
                                .clipToBounds()
                                .semantics { contentDescription = xs.title }
                        ) {
                            val bitmapWidth = image.width.toFloat()
                            val bitmapHeight = image.height.toFloat()
                            val spanPx = (animatedSpan * bitmapWidth).coerceIn(1f, bitmapWidth)
                            val centerPx = animatedCenter * bitmapWidth
                            val srcLeft = (centerPx - spanPx / 2f)
                                .coerceIn(0f, (bitmapWidth - spanPx).coerceAtLeast(0f))
                            val scale = minOf(size.width / spanPx, size.height / bitmapHeight)
                            val dstWidth = spanPx * scale
                            val dstHeight = bitmapHeight * scale
                            val dstLeft = (size.width - dstWidth) / 2f
                            val dstTop = (size.height - dstHeight) / 2f
                            drawImage(
                                image = image,
                                srcOffset = IntOffset(srcLeft.toInt(), 0),
                                srcSize = IntSize(spanPx.toInt().coerceAtLeast(1), image.height),
                                dstOffset = IntOffset(dstLeft.toInt(), dstTop.toInt()),
                                dstSize = IntSize(dstWidth.toInt().coerceAtLeast(1), dstHeight.toInt().coerceAtLeast(1)),
                                filterQuality = FilterQuality.Medium
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = xs.caption,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(16.dp))
                when {
                    step < questionStep -> {
                        Text(
                            text = String.format(xs.stepCounter, step + 1, xs.eraTitles.size),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = xs.eraTitles[step],
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = xs.eraTexts[step],
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    step == questionStep -> {
                        Text(
                            text = xs.questionText,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = xs.questionPrompt,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                        UniverseQuestionSection(
                            correctAnswer = true,
                            wrongAnswerMessage = xs.wrongAnswerMessage,
                            scoreDoubledMessage = xs.scoreDoubledMessage,
                            onCorrect = { onScoreChanged(currentScore + 1) }
                        )
                    }
                    else -> {
                        Text(
                            text = xs.conclusionTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = xs.conclusionText,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { if (step > 0) step-- },
                        enabled = step > 0,
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
                    FilledTonalButton(
                        onClick = {
                            if (step < lastStep) {
                                onScoreChanged(currentScore + 1)
                                step++
                            }
                        },
                        enabled = step < lastStep,
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

                if (step == lastStep) {
                    Spacer(Modifier.height(16.dp))
                    FilledTonalButton(
                        onClick = onNavigateToWhereAreWeFrom,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(hts.whereAreWeFrom, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(48.dp))
            }
        }

        UniverseSourcesMenu(
            sources = sources,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

@Composable
private fun UniverseQuestionSection(
    correctAnswer: Boolean,
    wrongAnswerMessage: String,
    scoreDoubledMessage: String,
    onCorrect: () -> Unit
) {
    val s = LocalUiStrings.current
    var selected by remember { mutableStateOf<String?>(null) }
    var hasSubmitted by remember { mutableStateOf(false) }
    var awarded by remember { mutableStateOf(false) }

    fun submit(answer: String) {
        selected = answer
        hasSubmitted = true
        if (!awarded) {
            awarded = true
            if ((answer == "yes") == correctAnswer) {
                onCorrect()
            }
        }
    }

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
                text = if (isCorrect) "\uD83C\uDF89 ${s.common.correct} \uD83C\uDF89" else wrongAnswerMessage,
                color = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            if (isCorrect) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = scoreDoubledMessage,
                    color = Color(0xFF2E7D32),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun UniverseSourcesMenu(sources: List<UniverseSource>, modifier: Modifier = Modifier) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var showSourcesMenu by remember { mutableStateOf(false) }
    var activeSource by remember { mutableStateOf<UniverseSource?>(null) }

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
                    text = { Text(source.label) },
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
                DropdownMenuItem(
                    text = { Text(s.common.copyUrl) },
                    onClick = {
                        showSourcesMenu = false
                        activeSource = null
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", source.url))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        activeSource = null
                        uriHandler.openUri(source.url)
                    }
                )
            }
        }
    }
}
