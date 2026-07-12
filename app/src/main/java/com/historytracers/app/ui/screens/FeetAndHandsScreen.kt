// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.R
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.parseHexColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class Mode { CLAP, STEPS, JUMPS }

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

private data class Bounds(var minX: Float, var minY: Float, var maxX: Float, var maxY: Float)

private fun buildFootPath(scale: Float = 0.1f, flipY: Boolean = true): Pair<Path, Bounds> {
    val path = Path()
    val bounds = Bounds(Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE)
    for (svgD in footToePaths) {
        val sub = parseSvgPathToAndroidPath(svgD)
        val m = Matrix()
        m.preScale(scale, if (flipY) -scale else scale)
        val transformed = Path()
        sub.transform(m, transformed)
                        val b = android.graphics.RectF()
        transformed.computeBounds(b, true)
        bounds.minX = minOf(bounds.minX, b.left)
        bounds.minY = minOf(bounds.minY, b.top)
        bounds.maxX = maxOf(bounds.maxX, b.right)
        bounds.maxY = maxOf(bounds.maxY, b.bottom)
        path.addPath(transformed)
    }
    return Pair(path, bounds)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeetAndHandsScreen(
    skinColor: String = "#FFF8E0",
    onNavigateBack: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val handColor = remember(skinColor) { parseHexColor(skinColor) }
    val handPath = remember { buildHandPath() }
    val footPathResult = remember { buildFootPath() }
    val footPath = footPathResult.first
    val footBounds = footPathResult.second

    var mode by remember { mutableStateOf(Mode.CLAP) }
    var count by remember { mutableIntStateOf(5) }
    var sliderPos by remember { mutableFloatStateOf(1200f) }
    fun cycleTime() = 2400f - sliderPos
    var isPlaying by remember { mutableStateOf(false) }
    var clapCompleted by remember { mutableIntStateOf(0) }
    var stepsCompleted by remember { mutableIntStateOf(0) }
    var jumpsCompleted by remember { mutableIntStateOf(0) }

    val scope = rememberCoroutineScope()
    val clapProgress = remember { Animatable(0f) }
    val footZoomProgress = remember { Animatable(0f) }
    var stepIsLeft by remember { mutableStateOf(true) }

    val context = LocalContext.current
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
                IconButton(onClick = {
                    if (!isPlaying) onNavigateBack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back)
                }
                Text(
                    text = s.exercisingFeetAndHands,
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = s.clapSkinColorHint,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 4.dp)
                )
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2.2f)
                        .align(Alignment.Center)
                ) {
                    val canvasW = 600f
                    val canvasH = 600f
                    val s = minOf(size.width / canvasW, size.height / canvasH) * 1.6f
                    val cx = size.width * 0.5f
                    val cy = size.height * 0.55f

                    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = handColor.hashCode()
                        style = Paint.Style.FILL
                        strokeJoin = Paint.Join.ROUND
                    }

                    val footScale = s * (0.125f / 0.6f)
                    val footY = cy + 110f * s + 200f
                    val footBaseX = cx
                    val handY = cy - 240f * s

                    if (mode == Mode.CLAP) {
                        val restOff = 120f * s
                        val centerShift = 60f * s
                        val prog = clapProgress.value
                        val leftOff = -restOff + restOff * prog + centerShift
                        val rightOff = restOff - restOff * prog + centerShift

                        val leftMatrix = Matrix()
                        leftMatrix.setTranslate(cx + leftOff, handY)
                        leftMatrix.preScale(s, s)
                        val left = Path()
                        left.addPath(handPath, leftMatrix)
                        drawContext.canvas.nativeCanvas.drawPath(left, paint)

                        val rightMatrix = Matrix()
                        rightMatrix.setTranslate(cx + rightOff, handY)
                        rightMatrix.preScale(s, s)
                        val right = Path()
                        right.addPath(handPath, rightMatrix)
                        drawContext.canvas.nativeCanvas.drawPath(right, paint)
                    } else {
                        val restOff = 120f * s
                        val centerShift = 60f * s
                        val leftOff = -restOff + centerShift
                        val rightOff = restOff + centerShift

                        val leftMatrix = Matrix()
                        leftMatrix.setTranslate(cx + leftOff, handY)
                        leftMatrix.preScale(s, s)
                        val left = Path()
                        left.addPath(handPath, leftMatrix)
                        drawContext.canvas.nativeCanvas.drawPath(left, paint)

                        val rightMatrix = Matrix()
                        rightMatrix.setTranslate(cx + rightOff, handY)
                        rightMatrix.preScale(s, s)
                        val right = Path()
                        right.addPath(handPath, rightMatrix)
                        drawContext.canvas.nativeCanvas.drawPath(right, paint)
                    }

                    fun drawFoot(xOffset: Float, mirrorX: Boolean, scaleMul: Float = 1f) {
                        val m = Matrix()
                        val ms = footScale * scaleMul
                        val footCx = (footBounds.minX + footBounds.maxX) / 2f
                        val footCy = (footBounds.minY + footBounds.maxY) / 2f
                        m.setTranslate(footBaseX + xOffset - footCx * ms, footY - footCy * ms)
                        m.preScale(if (mirrorX) -ms else ms, ms)
                        val fp = Path()
                        fp.addPath(footPath, m)
                        drawContext.canvas.nativeCanvas.drawPath(fp, paint)
                    }

                    val leftZoom: Float
                    val rightZoom: Float
                    if (mode == Mode.STEPS && isPlaying) {
                        val p = footZoomProgress.value
                        val z = 1f + (if (p < 0.5f) p * 2f else (1f - (p - 0.5f) * 2f)) * 0.12f
                        leftZoom = if (stepIsLeft) z else 1f
                        rightZoom = if (!stepIsLeft) z else 1f
                    } else if (mode == Mode.JUMPS && isPlaying) {
                        val p = footZoomProgress.value
                        leftZoom = 1f + p * 0.08f
                        rightZoom = leftZoom
                    } else {
                        leftZoom = 1f
                        rightZoom = 1f
                    }

                    drawFoot(-45f * s, mirrorX = true, leftZoom)
                    drawFoot(45f * s, mirrorX = false, rightZoom)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 150.dp)
                ) {
                    FilledIconButton(
                        onClick = {
                            if (!isPlaying) {
                                mode = Mode.CLAP
                                scope.launch {
                                    if (count < 1) return@launch
                                    isPlaying = true
                                    clapCompleted = 0
                                    val dur = (cycleTime() * 0.75f).toInt()
                                    val pauseDur = (cycleTime() * 0.25f).toInt()
                                    for (i in 0 until count) {
                                        clapProgress.snapTo(0f)
                                        clapProgress.animateTo(1f, tween(dur))
                                        soundPool.play(clapSoundId, 1f, 1f, 1, 0, 1f)
                                        clapProgress.snapTo(1f)
                                        clapProgress.animateTo(0f, tween(dur))
                                        clapCompleted++
                                        if (i < count - 1) delay(pauseDur.toLong())
                                    }
                                    isPlaying = false
                                }
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        enabled = !isPlaying || mode == Mode.CLAP
                    ) {
                        Icon(
                            Icons.Default.PanTool,
                            contentDescription = s.clap,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    FilledIconButton(
                        onClick = {
                            if (!isPlaying) {
                                mode = Mode.STEPS
                                scope.launch {
                                    if (count < 1) return@launch
                                    isPlaying = true
                                    stepsCompleted = 0
                                    val dur = (cycleTime() * 0.4f).toInt()
                                    for (i in 0 until count) {
                                        stepIsLeft = i % 2 == 0
                                        footZoomProgress.snapTo(0f)
                                        footZoomProgress.animateTo(1f, tween(dur))
                                        footZoomProgress.snapTo(1f)
                                        footZoomProgress.animateTo(0f, tween(dur))
                                        stepsCompleted++
                                        if (i < count - 1) delay((dur * 0.2f).toLong())
                                    }
                                    isPlaying = false
                                }
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        enabled = !isPlaying || mode == Mode.STEPS
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.DirectionsRun,
                            contentDescription = s.steps,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    FilledIconButton(
                        onClick = {
                            if (!isPlaying) {
                                mode = Mode.JUMPS
                                scope.launch {
                                    if (count < 1) return@launch
                                    isPlaying = true
                                    jumpsCompleted = 0
                                    val dur = (cycleTime() * 0.5f).toInt()
                                    for (i in 0 until count) {
                                        footZoomProgress.snapTo(0f)
                                        footZoomProgress.animateTo(1f, tween(dur))
                                        footZoomProgress.snapTo(1f)
                                        footZoomProgress.animateTo(0f, tween(dur))
                                        jumpsCompleted++
                                        if (i < count - 1) delay((dur * 0.3f).toInt().toLong())
                                    }
                                    isPlaying = false
                                }
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        enabled = !isPlaying || mode == Mode.JUMPS
                    ) {
                        Icon(
                            Icons.Default.Accessibility,
                            contentDescription = s.jumps,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${s.clapCounter} $clapCompleted / $count", style = MaterialTheme.typography.bodySmall)
                Text("${s.stepsCounter} $stepsCompleted / $count", style = MaterialTheme.typography.bodySmall)
                Text("${s.jumpsCounter} $jumpsCompleted / $count", style = MaterialTheme.typography.bodySmall)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(s.slowly, style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = sliderPos,
                    onValueChange = { if (!isPlaying) sliderPos = it },
                    valueRange = 400f..2000f,
                    modifier = Modifier.width(180.dp),
                    enabled = !isPlaying
                )
                Text(s.fast, style = MaterialTheme.typography.bodySmall)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${s.numberOfClapsStepsJumps} ", style = MaterialTheme.typography.bodySmall)
                FilledIconButton(
                    onClick = { if (!isPlaying && count > 1) count-- },
                    modifier = Modifier.size(32.dp),
                    enabled = !isPlaying && count > 1
                ) {
                    Text("-", style = MaterialTheme.typography.titleSmall)
                }
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.Center
                )
                FilledIconButton(
                    onClick = { if (!isPlaying && count < 20) count++ },
                    modifier = Modifier.size(32.dp),
                    enabled = !isPlaying && count < 20
                ) {
                    Text("+", style = MaterialTheme.typography.titleSmall)
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
