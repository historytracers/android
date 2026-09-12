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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.MarkdownText
import com.historytracers.app.ui.components.ResponsiveImage
import com.historytracers.app.ui.components.buildHandPath
import com.historytracers.app.ui.features.countingWithBonesScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.theme.Brown40
import kotlin.random.Random

private const val MAX_BONE_MARKS = 20
private const val MAX_BONE_LEVELS = 4
private const val LEBOMBO_BONE_IMAGE_URL = "https://www.historytracers.org/images/ResearchGate/Figura-9-Hueso-de-Lebombo.png"
private const val ORIGINAL_TEXT_URL = "https://www.historytracers.org/index.html?page=class_content&arg=7d2fd4de-5c10-4811-957c-8233bc8d21de"
private const val GONZALEZ_REDONDO_2010_URL = "https://doi.org/10.4321/S0211-95362010000100007"
private const val GONZALEZ_REDONDO_2024_URL = "https://www.youtube.com/watch?v=RFui5Z52pUY"
private const val BARRIGA_2020_URL = "https://play.google.com/store/books/details/Tsik_Los_n%C3%BAmeros_y_la_numerolog%C3%ADa_entre_los_mayas?id=eKr7DwAAQBAJ&hl=az&gl=US&pli=1"

private data class BoneSource(val label: String, val url: String)

private val boneFingerTips = listOf(
    Offset(-240f, 243f),
    Offset(-170f, 233f),
    Offset(-100f, 228f),
    Offset(-35f, 233f),
    Offset(50f, 258f),
)

private val boneFingerTipNudges = listOf(
    Offset(15f, -19f),
    Offset(27f, -35f),
    Offset(10f, -25f),
    Offset(-8f, -9f),
    Offset(-44f, 0f),
)

@Composable
fun CountingWithBonesIntroScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = countingWithBonesScreenStringsForLanguage(LocalAppLanguage.current)
    val sources = listOf(
        BoneSource("González Redondo, Francisco A., et al. (2010)", GONZALEZ_REDONDO_2010_URL),
        BoneSource("González Redondo, Francisco A. (2024)", GONZALEZ_REDONDO_2024_URL),
        BoneSource("Puente, Francisco Barriga (2020)", BARRIGA_2020_URL),
        BoneSource(s.common.originalText, ORIGINAL_TEXT_URL)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            BoneTopBar(title = xs.title, onNavigateBack = onNavigateBack, backDescription = s.common.back)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MarkdownText(
                    text = "#### ${xs.whyTitle}",
                    modifier = Modifier.fillMaxWidth()
                )

                ResponsiveImage(
                    html = "<img src=\"$LEBOMBO_BONE_IMAGE_URL\">",
                    imgDesc = xs.boneFigureCaption,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = xs.boneFigureCaption,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))
                MarkdownText(
                    text = xs.boneLookText,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))
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

                Spacer(Modifier.height(48.dp))
            }
        }

        BoneSourcesMenu(
            sources = sources,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

@Composable
fun CountingWithBonesGameScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = countingWithBonesScreenStringsForLanguage(LocalAppLanguage.current)
    val random = remember { Random(System.nanoTime()) }

    var level by remember { mutableIntStateOf(1) }
    var target by remember { mutableIntStateOf(0) }
    var marks by remember { mutableIntStateOf(0) }
    var handDigits by remember { mutableStateOf<List<Int>>(emptyList()) }

    fun newChallenge(newLevel: Int) {
        level = newLevel
        val min = newLevel * 5 - 4
        val max = newLevel * 5
        val newTarget = min + random.nextInt(max - min + 1)
        target = newTarget
        val digits = MutableList(newLevel) { 5 }
        digits[newLevel - 1] = newTarget - 5 * (newLevel - 1)
        handDigits = digits
        marks = 0
    }

    LaunchedEffect(Unit) { newChallenge(1) }

    val won = target > 0 && marks == target

    Column(modifier = Modifier.fillMaxSize()) {
        BoneTopBar(title = xs.title, onNavigateBack = onNavigateBack, backDescription = s.common.back)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = xs.prompt,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                handDigits.withIndex().chunked(2).forEach { rowHands ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (rowHands.size == 1) Spacer(Modifier.weight(1f))
                        rowHands.forEach { (index, digit) ->
                            BoneHand(
                                count = digit,
                                isLeft = index % 2 == 0,
                                contentDescription = String.format(xs.raisedHandDesc, digit),
                                modifier = Modifier
                                    .weight(if (rowHands.size == 1) 2f else 1f)
                                    .fillMaxHeight()
                            )
                        }
                        if (rowHands.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BoneWithMarks(
                    markCount = marks,
                    contentDescription = String.format(xs.boneDesc, marks),
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { if (!won && marks < target) marks++ },
                        enabled = !won && marks < target
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = xs.addMark,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(
                        onClick = { if (!won && marks > 0) marks-- },
                        enabled = !won && marks > 0
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = xs.removeMark,
                            tint = Color(0xFF1565C0),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            if (won) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "\uD83C\uDFC5 ${xs.congrats} \uD83C\uDFC5",
                    color = Color(0xFF2E7D32),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                if (level == MAX_BONE_LEVELS) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "\uD83C\uDF89 ${xs.allLevelsCongrats} \uD83C\uDF89",
                        color = Color(0xFF2E7D32),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (won) {
                Spacer(Modifier.height(16.dp))
                FilledTonalButton(
                    onClick = onNavigateNext,
                    enabled = won && level == MAX_BONE_LEVELS,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text(s.common.nextLevel, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(onClick = { newChallenge(level) }) {
                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(xs.newRound, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
fun CountingWithBonesPracticeScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToFirstSteps: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = countingWithBonesScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }

    LaunchedEffect(Unit) {
        preferences.markFirstStepsSectionCompleted("counting_with_bones")
        preferences.recordLessonCompletion()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BoneTopBar(title = xs.title, onNavigateBack = onNavigateBack, backDescription = s.common.back)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoneSectionTitle(xs.practiceTitle)
            BodyText(xs.practiceText)

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun BoneTopBar(title: String, onNavigateBack: () -> Unit, backDescription: String) {
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = backDescription)
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun BoneSectionTitle(text: String, alignStart: Boolean = false) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        textAlign = if (alignStart) TextAlign.Start else TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    )
}

@Composable
private fun BodyText(text: String, alignStart: Boolean = false) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = if (alignStart) TextAlign.Start else TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
private fun BoneHand(
    count: Int,
    isLeft: Boolean,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val handPath = remember { buildHandPath() }
    Canvas(
        modifier = modifier.semantics { this.contentDescription = contentDescription ?: "" }
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color(0xFFF4C2A1).toArgb()
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
        }
        val bounds = RectF()
        handPath.computeBounds(bounds, true)
        val pathWidth = bounds.width().coerceAtLeast(1f)
        val pathHeight = bounds.height().coerceAtLeast(1f)
        val scale = minOf(size.width / (pathWidth * 1.05f), size.height / (pathHeight * 1.05f))
        val cx = size.width / 2f
        val cy = size.height / 2f
        val pathCenterX = bounds.centerX()
        val pathCenterY = bounds.centerY()

        val matrix = Matrix().apply {
            setTranslate(cx, cy)
            if (isLeft) preScale(-scale, scale) else preScale(scale, scale)
            preTranslate(-pathCenterX, -pathCenterY)
        }
        val transformed = Path().apply { addPath(handPath, matrix) }
        drawContext.canvas.nativeCanvas.drawPath(transformed, paint)

        val tipRadius = minOf(size.width, size.height) * 0.09f
        for (index in 0 until count.coerceIn(0, boneFingerTips.size)) {
            val tip = boneFingerTips[index]
            val nudge = boneFingerTipNudges.getOrNull(index)
            val pathDx = if (nudge != null) nudge.x.dp.toPx() / scale else 0f
            val pathDy = if (nudge != null) nudge.y.dp.toPx() / scale else 0f
            val x = cx + (if (isLeft) -1f else 1f) * (tip.x + pathDx - pathCenterX) * scale
            val y = cy + (tip.y + pathDy - pathCenterY) * scale
            drawCircle(color = Color(0xFFC0392B), radius = tipRadius, center = Offset(x, y))
            drawCircle(color = Color.White, radius = tipRadius * 0.45f, center = Offset(x, y))
        }
    }
}

@Composable
private fun BoneWithMarks(
    markCount: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.semantics { this.contentDescription = contentDescription ?: "" }
    ) {
        val w = size.width
        val h = size.height
        val boneFill = Color(0xFFF3E3C3)
        val boneStroke = Color(0xFF8D6E63)
        val markColor = Brown40

        val left = w * 0.10f
        val right = w * 0.90f
        val shaftHeight = h * 0.34f
        val shaftTop = (h - shaftHeight) / 2f
        val border = h * 0.025f
        val knobRadius = shaftHeight * 0.58f
        val knobOffsets = listOf(-1f, 1f)
        val knobCenters = listOf(left, right).flatMap { ex ->
            knobOffsets.map { dir -> Offset(ex, shaftTop + shaftHeight * 0.5f + dir * shaftHeight * 0.45f) }
        }

        drawRoundRect(
            color = boneStroke,
            topLeft = Offset(left - border, shaftTop - border),
            size = Size(right - left + 2 * border, shaftHeight + 2 * border),
            cornerRadius = CornerRadius(shaftHeight / 2f + border)
        )
        knobCenters.forEach { drawCircle(boneStroke, radius = knobRadius + border, center = it) }

        drawRoundRect(
            color = boneFill,
            topLeft = Offset(left, shaftTop),
            size = Size(right - left, shaftHeight),
            cornerRadius = CornerRadius(shaftHeight / 2f)
        )
        knobCenters.forEach { drawCircle(boneFill, radius = knobRadius, center = it) }

        val markHalf = shaftHeight * 0.64f
        val markWidth = (w * 0.012f).coerceAtLeast(2f)
        val markHalfWidth = markWidth / 2f
        val knobOuter = knobRadius + border
        val markStart = left + knobOuter + markHalfWidth
        val markEnd = right - knobOuter - markHalfWidth
        val markStep = if (MAX_BONE_MARKS > 1) (markEnd - markStart) / (MAX_BONE_MARKS - 1) else 0f
        val centerY = shaftTop + shaftHeight / 2f
        for (index in 0 until markCount.coerceIn(0, MAX_BONE_MARKS)) {
            val x = markStart + index * markStep
            drawLine(
                color = markColor,
                start = Offset(x, centerY - markHalf),
                end = Offset(x, centerY + markHalf),
                strokeWidth = markWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun BoneSourcesMenu(sources: List<BoneSource>, modifier: Modifier = Modifier) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var showSourcesMenu by remember { mutableStateOf(false) }
    var activeSource by remember { mutableStateOf<BoneSource?>(null) }

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
