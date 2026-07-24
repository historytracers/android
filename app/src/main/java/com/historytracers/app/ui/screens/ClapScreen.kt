// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Matrix
import android.media.AudioAttributes
import android.media.SoundPool
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import com.historytracers.app.R
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.parseHexColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun buildAndroidPath(): Path {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClapScreen(
    skinColor: String = "#A5672C",
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val handColor = remember(skinColor) { parseHexColor(skinColor) }

    val handPath = remember { buildAndroidPath() }

    var clapCount by remember { mutableIntStateOf(5) }
    var sliderPos by remember { mutableFloatStateOf(1200f) }
    fun cycleTime() = 2400f - sliderPos
    var isPlaying by remember { mutableStateOf(false) }
    var completed by remember { mutableIntStateOf(0) }
    var target by remember { mutableIntStateOf(0) }
    var showCompletionMessage by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val animationProgress = remember { Animatable(0f) }

    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    }
    val clapSoundId = remember { soundPool.load(context, R.raw.clap, 1) }
    DisposableEffect(Unit) {
        onDispose { soundPool.release() }
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
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.common.back)
                    }
                    Text(
                        text = s.body.exercisingHands,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = s.body.clapReinforce,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Text(
                    text = s.body.clapInstructions,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Text(
                    text = s.body.clapSkinColorHint,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f)
                            .offset(y = (-50).dp)
                    ) {
                        val canvasW = 600f
                        val canvasH = 600f
                        val s = minOf(size.width / canvasW, size.height / canvasH) * 1.8f
                        val cx = size.width * 0.5f + 70f * density
                        val cy = size.height * 0.6f

                        val restOff = 140f * s
                        val prog = animationProgress.value

                        val leftOff = -restOff + restOff * prog
                        val rightOff = restOff - restOff * prog

                        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = handColor.hashCode()
                            style = Paint.Style.FILL
                            strokeJoin = Paint.Join.ROUND
                        }

                        val leftMatrix = Matrix()
                        leftMatrix.setTranslate(cx + leftOff, cy)
                        leftMatrix.preScale(s, s)
                        val left = Path()
                        left.addPath(handPath, leftMatrix)
                        drawContext.canvas.nativeCanvas.drawPath(left, paint)

                        val rightMatrix = Matrix()
                        rightMatrix.setTranslate(cx + rightOff, cy)
                        rightMatrix.preScale(s, s)
                        val right = Path()
                        right.addPath(handPath, rightMatrix)
                        drawContext.canvas.nativeCanvas.drawPath(right, paint)
                    }

                Spacer(Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(s.common.numberOfClaps, style = MaterialTheme.typography.bodyLarge)

                    FilledIconButton(
                        onClick = { if (!isPlaying && clapCount > 1) clapCount-- },
                        modifier = Modifier.size(40.dp),
                        enabled = !isPlaying && clapCount > 1
                    ) {
                        Text("-", style = MaterialTheme.typography.titleMedium)
                    }

                    Text(
                        text = clapCount.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(48.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    FilledIconButton(
                        onClick = { if (!isPlaying && clapCount < 9) clapCount++ },
                        modifier = Modifier.size(40.dp),
                        enabled = !isPlaying && clapCount < 9
                    ) {
                        Text("+", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(s.body.speedSlow, style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = sliderPos,
                        onValueChange = { if (!isPlaying) sliderPos = it },
                        valueRange = 400f..2000f,
                        modifier = Modifier.width(200.dp),
                        enabled = !isPlaying
                    )
                    Text(s.body.speedFast, style = MaterialTheme.typography.bodySmall)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilledIconButton(
                        onClick = {
                            if (isPlaying) return@FilledIconButton
                            if (clapCount < 1) {
                                completed = 0
                                target = 0
                                return@FilledIconButton
                            }
                            isPlaying = true
                            completed = 0
                            target = clapCount
                            showCompletionMessage = false
                            scope.launch { preferences.recordLessonCompletion() }
                            scope.launch {
                                val animDuration = (cycleTime() * 0.75f).toInt()
                                val pauseDuration = (cycleTime() * 0.25f).toInt()
                                for (i in 0 until clapCount) {
                                    animationProgress.snapTo(0f)
                                    animationProgress.animateTo(
                                        targetValue = 1f,
                                        animationSpec = tween(durationMillis = animDuration)
                                    )
                                    soundPool.play(clapSoundId, 1f, 1f, 1, 0, 1f)
                                    animationProgress.snapTo(1f)
                                    animationProgress.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(durationMillis = animDuration)
                                    )
                                    completed++
                                    if (i < clapCount - 1) {
                                        delay(pauseDuration.toLong())
                                    }
                                }
                                isPlaying = false
                                onScoreChanged(currentScore + 2)
                                preferences.markWorkoutSectionCompleted("exercising_hands")
                                showCompletionMessage = true
                            }
                        },
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        enabled = !isPlaying
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Text(
                        text = "$completed / $target",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (showCompletionMessage) {
            Text(
                text = s.body.exercisingHandsCompletionMessage,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 70.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp, start = 8.dp)
        ) {
            val uriHandler = LocalUriHandler.current
            val context = LocalContext.current

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
                expanded = showSourcesMenu && !showMainTextSubmenu,
                onDismissRequest = { showSourcesMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(s.common.originalText) },
                    trailingIcon = {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    },
                    onClick = { showMainTextSubmenu = true }
                )
            }

            DropdownMenu(
                expanded = showSourcesMenu && showMainTextSubmenu,
                onDismissRequest = { showMainTextSubmenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(s.common.copyUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=f6587eb2-c0db-4ce0-bed7-901835116466"))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=f6587eb2-c0db-4ce0-bed7-901835116466")
                    }
                )
            }
        }
    }
}
