// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.media.AudioAttributes
import android.media.SoundPool
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.R
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import com.historytracers.app.ui.theme.parseHexColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

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

private data class ExerciseBounds(var minX: Float, var minY: Float, var maxX: Float, var maxY: Float)

private fun buildFootPath(scale: Float = 0.1f, flipY: Boolean = true): Pair<Path, ExerciseBounds> {
    val path = Path()
    val bounds = ExerciseBounds(Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE)
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

private enum class ExercisePhase { CLAP_TOP, CLAP_BOTTOM, STEP_RESULT, DONE }

private data class AdditionProblem(
    val topValue: Int,
    val bottomValue: Int,
    val result: Int,
    val nextValue: Int
)

private fun generateProblem(): AdditionProblem {
    val top = Random.nextInt(1, 10)
    val bottom = Random.nextInt(1, 10)
    val result = top + bottom
    return AdditionProblem(
        topValue = top,
        bottomValue = bottom,
        result = result,
        nextValue = if (result > 9) 1 else 0
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisingAdditionScreen(
    skinColor: String = "#A5672C",
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val handColor = remember(skinColor) { parseHexColor(skinColor) }
    val handPath = remember { buildHandPath() }
    val footPathResult = remember { buildFootPath() }
    val footPath = footPathResult.first
    val footBounds = footPathResult.second

    var problem by remember { mutableStateOf(generateProblem()) }
    var phase by remember { mutableStateOf(ExercisePhase.CLAP_TOP) }
    var isPlaying by remember { mutableStateOf(false) }
    var clapCompleted by remember { mutableIntStateOf(0) }
    var stepsCompleted by remember { mutableIntStateOf(0) }
    var jumpsCompleted by remember { mutableIntStateOf(0) }
    var sliderPos by remember { mutableFloatStateOf(1200f) }
    var stepIsLeft by remember { mutableStateOf(true) }
    var handsDown by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    val additionTextStyle = MaterialTheme.typography.headlineLarge.copy(fontSize = MaterialTheme.typography.headlineLarge.fontSize * 0.7f)

    fun cycleTime() = 2400f - sliderPos

    val scope = rememberCoroutineScope()
    val clapProgress = remember { Animatable(0f) }
    val footZoomProgress = remember { Animatable(0f) }

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
    val stepSoundId = remember { soundPool.load(context, R.raw.step, 1) }
    val jumpSoundId = remember { soundPool.load(context, R.raw.jump, 1) }
    DisposableEffect(Unit) {
        onDispose { soundPool.release() }
    }

    fun startExercise() {
        if (isPlaying) return
        scope.launch {
            isPlaying = true
            phase = ExercisePhase.CLAP_TOP
            clapCompleted = 0
            stepsCompleted = 0
            jumpsCompleted = 0
            handsDown = false
            preferences.recordLessonCompletion()

            val clapDur = (cycleTime() * 0.75f).toInt()
            val clapPause = (cycleTime() * 0.25f).toInt()
            val stepDur = (cycleTime() * 0.4f).toInt()
            val stepPause = (stepDur * 0.2f).toInt()
            val jumpDur = (cycleTime() * 0.5f).toInt()
            val jumpPause = (jumpDur * 0.3f).toInt()

            val result = problem.result

            for (i in 0 until problem.topValue) {
                clapProgress.snapTo(0f)
                clapProgress.animateTo(1f, tween(clapDur))
                soundPool.play(clapSoundId, 1f, 1f, 1, 0, 1f)
                clapCompleted++
                if (i < problem.topValue - 1) delay(clapPause.toLong())
            }

            handsDown = true

            for (i in 0 until problem.bottomValue) {
                clapProgress.snapTo(0f)
                clapProgress.animateTo(1f, tween(clapDur))
                soundPool.play(clapSoundId, 1f, 1f, 1, 0, 1f)
                clapCompleted++
                if (i < problem.bottomValue - 1) delay(clapPause.toLong())
            }

            handsDown = false

            phase = ExercisePhase.STEP_RESULT

            for (i in 0 until result) {
                if (result > 9 && i == 9) {
                    footZoomProgress.snapTo(0f)
                    footZoomProgress.animateTo(1f, tween(jumpDur))
                    soundPool.play(jumpSoundId, 1f, 1f, 1, 0, 1f)
                    footZoomProgress.snapTo(1f)
                    footZoomProgress.animateTo(0f, tween(jumpDur))
                    jumpsCompleted++
                    if (i < result - 1) delay(jumpPause.toLong())
                } else {
                    stepIsLeft = i % 2 == 0
                    footZoomProgress.snapTo(0f)
                    footZoomProgress.animateTo(1f, tween(stepDur))
                    soundPool.play(stepSoundId, 1f, 1f, 1, 0, 1f)
                    stepsCompleted++
                    if (i < result - 1) delay(stepPause.toLong())
                }
            }

            phase = ExercisePhase.DONE
            isPlaying = false
            onScoreChanged(currentScore + 2)
            preferences.markWorkoutSectionCompleted("exercising_addition")
        }
    }

    fun newProblem() {
        if (isPlaying) return
        problem = generateProblem()
        phase = ExercisePhase.CLAP_TOP
        clapCompleted = 0
        stepsCompleted = 0
        jumpsCompleted = 0
        handsDown = false
        scope.launch { clapProgress.snapTo(0f) }
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back)
                    }
                    Text(
                        text = s.exercisingAddition,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Text(
                text = s.exercisingAdditionInstruction,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (problem.nextValue != 0) {
                    Row(Modifier.width(104.dp)) {
                        Spacer(Modifier.width(36.dp))
                        Text(
                            text = "${problem.nextValue}",
                            style = additionTextStyle,
                            modifier = Modifier.width(32.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.width(36.dp))
                    }
                }
                Row(Modifier.width(104.dp)) {
                    Spacer(Modifier.width(36.dp))
                    Spacer(Modifier.width(32.dp))
                    Text(
                        text = "${problem.topValue}",
                        style = additionTextStyle,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Row(Modifier.width(104.dp)) {
                    Text(
                        text = "+ ",
                        style = additionTextStyle,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.End
                    )
                    Spacer(Modifier.width(32.dp))
                    Text(
                        text = "${problem.bottomValue}",
                        style = additionTextStyle,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Box(Modifier.width(104.dp).padding(start = 36.dp)) {
                    Divider(thickness = 4.dp)
                }
                Row(Modifier.width(104.dp)) {
                    Spacer(Modifier.width(36.dp))
                    if (problem.result > 9) {
                        Text(
                            text = "${problem.result / 10}",
                            style = additionTextStyle,
                            modifier = Modifier.width(32.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Spacer(Modifier.width(32.dp))
                    }
                    Text(
                        text = "${problem.result % 10}",
                        style = additionTextStyle,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val s = minOf(size.width / 600f, size.height / 600f)
                    val cx = size.width * 0.5f + 90f * density
                    val cy = size.height * 0.5f

                    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = handColor.hashCode()
                        style = Paint.Style.FILL
                        strokeJoin = Paint.Join.ROUND
                    }

                    val footScale = s * (0.125f / 0.6f) * 0.7f
                    val footY = cy + 60f * s + 30f * density - 70f * density + 20f * density + 20f * density
                    val footBaseX = cx - 20f * density
                    val handY = cy - 200f * s - 140f * density + 10f * density + 10f * density

                    val restOff = 120f * s
                    val handDownOff = 58f * s * 0.7f + 20f * density
                    val prog = clapProgress.value

                    val leftOff: Float
                    val rightOff: Float
                    val leftYOffHand: Float
                    val rightYOffHand: Float

                    if (phase == ExercisePhase.CLAP_TOP && !handsDown) {
                        leftOff = -restOff + restOff * prog
                        rightOff = restOff - restOff * prog
                        leftYOffHand = 0f
                        rightYOffHand = 0f
                    } else if (handsDown) {
                        leftOff = -restOff + restOff * prog
                        rightOff = restOff - restOff * prog
                        leftYOffHand = handDownOff
                        rightYOffHand = handDownOff
                    } else {
                        leftOff = -restOff
                        rightOff = restOff
                        leftYOffHand = 0f
                        rightYOffHand = 0f
                    }

                    val leftMatrix = Matrix()
                    leftMatrix.setTranslate(cx + leftOff, handY + leftYOffHand)
                    leftMatrix.preScale(s * 0.7f, s * 0.7f)
                    val left = Path()
                    left.addPath(handPath, leftMatrix)
                    drawContext.canvas.nativeCanvas.drawPath(left, paint)

                    val rightMatrix = Matrix()
                    rightMatrix.setTranslate(cx + rightOff, handY + rightYOffHand)
                    rightMatrix.preScale(s * 0.7f, s * 0.7f)
                    val right = Path()
                    right.addPath(handPath, rightMatrix)
                    drawContext.canvas.nativeCanvas.drawPath(right, paint)

                    fun drawFoot(xOffset: Float, mirrorX: Boolean, scaleMul: Float = 1f, yOffset: Float = 0f) {
                        val m = Matrix()
                        val ms = footScale * scaleMul
                        val footCx = (footBounds.minX + footBounds.maxX) / 2f
                        val footCy = (footBounds.minY + footBounds.maxY) / 2f
                        m.setTranslate(footBaseX + xOffset - footCx * ms, footY + yOffset - footCy * ms)
                        m.preScale(if (mirrorX) -ms else ms, ms)
                        val fp = Path()
                        fp.addPath(footPath, m)
                        drawContext.canvas.nativeCanvas.drawPath(fp, paint)
                    }

                    val leftZoom: Float
                    val rightZoom: Float
                    val leftYOff: Float
                    val rightYOff: Float
                    if (phase == ExercisePhase.STEP_RESULT && isPlaying) {
                        val p = footZoomProgress.value
                        val stepPhase = if (p < 0.5f) p * 2f else (1f - (p - 0.5f) * 2f)
                        val rise = -40f * s * stepPhase
                        val zoom = 1f + stepPhase * 0.1f
                        val isJumpPhase = stepsCompleted + jumpsCompleted >= 9 && problem.result > 9
                        if (isJumpPhase && stepsCompleted + jumpsCompleted == 9) {
                            val jp = footZoomProgress.value
                            leftZoom = 1f + jp * 0.08f
                            rightZoom = leftZoom
                            leftYOff = 0f
                            rightYOff = 0f
                        } else {
                            leftZoom = if (stepIsLeft) zoom else 1f
                            rightZoom = if (!stepIsLeft) zoom else 1f
                            leftYOff = if (stepIsLeft) rise else 0f
                            rightYOff = if (!stepIsLeft) rise else 0f
                        }
                    } else {
                        leftZoom = 1f
                        rightZoom = 1f
                        leftYOff = 0f
                        rightYOff = 0f
                    }

                    drawFoot(-45f * s, mirrorX = true, leftZoom, leftYOff)
                    drawFoot(45f * s, mirrorX = false, rightZoom, rightYOff)
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().offset(y = -100.dp).padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${s.clapCounter} $clapCompleted", style = MaterialTheme.typography.bodySmall)
                    Text("${s.stepsCounter} $stepsCompleted", style = MaterialTheme.typography.bodySmall)
                    Text("${s.jumpsCounter} $jumpsCompleted", style = MaterialTheme.typography.bodySmall)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { newProblem() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        ),
                        enabled = !isPlaying
                    ) {
                        Text(
                            text = s.newExercise,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    FilledTonalButton(
                        onClick = { startExercise() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        ),
                        enabled = !isPlaying
                    ) {
                        Text(
                            text = s.doExercise,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        if (phase == ExercisePhase.DONE && !isPlaying) {
            Text(
                text = s.exercisingAdditionCompletionMessage,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp)
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
                    text = s.sources,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = showSourcesMenu && !showMainTextSubmenu,
                onDismissRequest = { showSourcesMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(s.mainText) },
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
                    text = { Text(s.copyUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=48f8f1cd-5036-4140-aafc-697963fe5dfb"))
                        Toast.makeText(context, s.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=48f8f1cd-5036-4140-aafc-697963fe5dfb")
                    }
                )
            }
        }
    }
}
