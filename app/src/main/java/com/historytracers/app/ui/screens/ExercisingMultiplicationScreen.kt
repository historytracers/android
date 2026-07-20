// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.coroutineScope
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

private data class MultiplicationProblem(
    val topValue: Int,
    val bottomValue: Int,
    val result: Int
)

private fun generateProblem(table: Int): MultiplicationProblem {
    val top = Random.nextInt(1, 10)
    val bottom = if (table == -1) Random.nextInt(1, 4) else table
    return MultiplicationProblem(top, bottom, top * bottom)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelationshipScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val handPath = remember { buildHandPath() }
    val footPath = remember {
        val scale = 0.1f
        val flipY = true
        val path = Path()
        val rect = android.graphics.RectF(Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE)
        for (svgD in footToePaths) {
            val sub = parseSvgPathToAndroidPath(svgD)
            val m = Matrix()
            m.preScale(scale, if (flipY) -scale else scale)
            val transformed = Path()
            sub.transform(m, transformed)
            val b = android.graphics.RectF()
            transformed.computeBounds(b, true)
            rect.left = minOf(rect.left, b.left)
            rect.top = minOf(rect.top, b.top)
            rect.right = maxOf(rect.right, b.right)
            rect.bottom = maxOf(rect.bottom, b.bottom)
            path.addPath(transformed)
        }
        Pair(path, rect)
    }
    val footMinX = footPath.second.left
    val footMaxX = footPath.second.right
    val footMinY = footPath.second.top
    val footMaxY = footPath.second.bottom

    var problem by remember { mutableStateOf(generateProblem(-1)) }
    var selectedTable by remember { mutableIntStateOf(-1) }
    var isPlaying by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }
    var clapCompleted by remember { mutableIntStateOf(0) }
    var stepsCompleted by remember { mutableIntStateOf(0) }
    var jumpsCompleted by remember { mutableIntStateOf(0) }
    var sliderPos by remember { mutableFloatStateOf(1100f) }
    var stepIsLeft by remember { mutableStateOf(true) }
    var jumpActive by remember { mutableStateOf(false) }
    var lastOneChoice by remember { mutableIntStateOf(-1) }
    var lastTwoChoice by remember { mutableIntStateOf(-1) }
    var lastThreeChoice by remember { mutableIntStateOf(-1) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val clapProgress = remember { Animatable(0f) }
    val footZoomProgress = remember { Animatable(0f) }
    val handHorizontalProgress = remember { Animatable(0f) }
    var handHorizontalIsLeft by remember { mutableStateOf(true) }
    val handSpreadProgress = remember { Animatable(0f) }

    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }

    val clapCycleTime = 2400f - sliderPos

    fun cycleTime() = clapCycleTime

    fun newMultiplication() {
        if (isPlaying) return
        problem = generateProblem(selectedTable)
        isDone = false
        clapCompleted = 0
        stepsCompleted = 0
        jumpsCompleted = 0
        lastOneChoice = -1
        lastTwoChoice = -1
        lastThreeChoice = -1
        scope.launch {
            clapProgress.snapTo(0f)
            footZoomProgress.snapTo(0f)
            handHorizontalProgress.snapTo(0f)
            handSpreadProgress.snapTo(0f)
        }
    }

    suspend fun animateClap(repetitions: Int) {
        val duration = (cycleTime() * 0.75f).toInt()
        val pause = (cycleTime() * 0.25f).toInt()
        for (i in 0 until repetitions) {
            clapProgress.snapTo(0f)
            clapProgress.animateTo(1f, tween(duration))
            clapProgress.snapTo(0f)
            clapCompleted++
            if (i < repetitions - 1) delay(pause.toLong())
        }
    }

    suspend fun animateSteps(repetitions: Int) {
        val duration = (cycleTime() * 0.4f).toInt()
        val pause = (duration * 0.2f).toInt()
        for (i in 0 until repetitions) {
            stepIsLeft = i % 2 == 0
            footZoomProgress.snapTo(0f)
            footZoomProgress.animateTo(1f, tween(duration))
            stepsCompleted++
            if (i < repetitions - 1) delay(pause.toLong())
        }
    }

    suspend fun animateJump(repetitions: Int) {
        val duration = (cycleTime() * 0.5f).toInt()
        val pause = (duration * 0.3f).toInt()
        jumpActive = true
        for (i in 0 until repetitions) {
            footZoomProgress.snapTo(0f)
            footZoomProgress.animateTo(1f, tween(duration))
            footZoomProgress.snapTo(0f)
            jumpsCompleted++
            if (i < repetitions - 1) delay(pause.toLong())
        }
        jumpActive = false
    }

    suspend fun animateHandHorizontalLeft(repetitions: Int) {
        val duration = (cycleTime() * 0.5f).toInt()
        val pause = (duration * 0.3f).toInt()
        handHorizontalIsLeft = true
        for (i in 0 until repetitions) {
            handHorizontalProgress.snapTo(0f)
            handHorizontalProgress.animateTo(1f, tween(duration))
            handHorizontalProgress.snapTo(0f)
            clapCompleted++
            if (i < repetitions - 1) delay(pause.toLong())
        }
    }

    suspend fun animateHandHorizontalRight(repetitions: Int) {
        val duration = (cycleTime() * 0.5f).toInt()
        val pause = (duration * 0.3f).toInt()
        handHorizontalIsLeft = false
        for (i in 0 until repetitions) {
            handHorizontalProgress.snapTo(0f)
            handHorizontalProgress.animateTo(1f, tween(duration))
            handHorizontalProgress.snapTo(0f)
            clapCompleted++
            if (i < repetitions - 1) delay(pause.toLong())
        }
    }

    suspend fun animateHandSpread(repetitions: Int) {
        val duration = (cycleTime() * 0.45f).toInt()
        val pause = (duration * 0.2f).toInt()
        for (i in 0 until repetitions) {
            handSpreadProgress.snapTo(0f)
            handSpreadProgress.animateTo(1f, tween(duration))
            handSpreadProgress.snapTo(0f)
            clapCompleted++
            if (i < repetitions - 1) delay(pause.toLong())
        }
    }

    suspend fun animateIsolatedStepLeft(repetitions: Int, showCounter: Boolean) {
        val duration = (cycleTime() * 0.75f).toInt()
        val pause = (cycleTime() * 0.25f).toInt()
        for (i in 0 until repetitions) {
            stepIsLeft = true
            footZoomProgress.snapTo(0f)
            footZoomProgress.animateTo(1f, tween(duration))
            if (showCounter) stepsCompleted++
            footZoomProgress.snapTo(0f)
            if (i < repetitions - 1) delay(pause.toLong())
        }
    }

    suspend fun animateIsolatedStepRight(repetitions: Int, showCounter: Boolean) {
        val duration = (cycleTime() * 0.75f).toInt()
        val pause = (cycleTime() * 0.25f).toInt()
        for (i in 0 until repetitions) {
            stepIsLeft = false
            footZoomProgress.snapTo(0f)
            footZoomProgress.animateTo(1f, tween(duration))
            if (showCounter) stepsCompleted++
            footZoomProgress.snapTo(0f)
            if (i < repetitions - 1) delay(pause.toLong())
        }
    }

    suspend fun executeOneMult() {
        val reps = problem.topValue
        val options = (0..3).filter { it != lastOneChoice }
        val choice = options[Random.nextInt(options.size)]
        lastOneChoice = choice
        when (choice) {
            0 -> animateIsolatedStepLeft(reps, true)
            1 -> animateIsolatedStepRight(reps, true)
            2 -> animateHandHorizontalLeft(reps)
            else -> animateHandHorizontalRight(reps)
        }
    }

    suspend fun executeTwoMult() {
        val reps = problem.topValue
        val options = (0..2).filter { it != lastTwoChoice }
        val choice = options[Random.nextInt(options.size)]
        lastTwoChoice = choice
        when (choice) {
            0 -> animateClap(reps)
            1 -> animateJump(reps)
            else -> animateHandSpread(reps)
        }
    }

    suspend fun executeThreeMult() {
        val reps = problem.topValue
        val options = (0..3).filter { it != lastThreeChoice }
        val choice = options[Random.nextInt(options.size)]
        lastThreeChoice = choice
        coroutineScope {
            when (choice) {
                0 -> {
                    launch { animateClap(reps) }
                    launch { animateIsolatedStepLeft(reps, true) }
                }
                1 -> {
                    launch { animateClap(reps) }
                    launch { animateIsolatedStepRight(reps, true) }
                }
                        2 -> {
                    launch { animateJump(reps) }
                    launch { animateHandHorizontalLeft(reps) }
                }
                else -> {
                    launch { animateJump(reps) }
                    launch { animateHandHorizontalRight(reps) }
                }
            }
        }
    }

    suspend fun executeMult() {
        when (problem.bottomValue) {
            1 -> executeOneMult()
            2 -> executeTwoMult()
            else -> executeThreeMult()
        }
    }

    fun startExercise() {
        if (isPlaying) return
        scope.launch {
            isPlaying = true
            isDone = false
            clapCompleted = 0
            stepsCompleted = 0
            jumpsCompleted = 0

            executeMult()

            isPlaying = false
            isDone = true
            onScoreChanged(currentScore + 2)
            preferences.markWorkoutSectionCompleted("relationship")
            preferences.markWorkoutSectionCompleted("exercising_multiplication")
            preferences.recordLessonCompletion()
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back)
                    }
                    Text(
                        text = s.exercisingMultiplication,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-10).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = s.relationshipReinforce,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.width(35.dp))
                    Text(
                        text = "${problem.topValue}",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 21.sp),
                        modifier = Modifier.width(35.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.width(35.dp), contentAlignment = Alignment.Center) {
                        Text("×", style = MaterialTheme.typography.headlineLarge.copy(fontSize = 21.sp))
                    }
                    Text(
                        text = "${problem.bottomValue}",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 21.sp),
                        modifier = Modifier.width(35.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Box(Modifier.width(120.dp)) { Divider(thickness = 4.dp) }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.width(35.dp))
                    Text(
                        text = "${problem.result}",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 21.sp),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(35.dp),
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
                    val cx = size.width * 0.5f + 90f * density - 20f * density
                    val cy = size.height * 0.5f

                    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.parseColor("#2E7D32")
                        style = Paint.Style.FILL
                        strokeJoin = Paint.Join.ROUND
                    }

                    val footScale = s * (0.125f / 0.6f) * 0.7f
                    val footY = cy + 60f * s + 30f * density - 70f * density + 20f * density + 20f * density + 20f * density - 70f * density + 20f * density - 10f * density
                    val footBaseX = cx - 20f * density
                    val handY = cy - 200f * s - 140f * density + 10f * density + 10f * density + 20f * density + 40f * density - 70f * density + 20f * density + 10f * density

                    val restOff = 120f * s
                    val prog = clapProgress.value
                    val handHorizProg = handHorizontalProgress.value
                    val handSpreadProg = handSpreadProgress.value

                    val leftOff: Float
                    val rightOff: Float

                    if (prog > 0f) {
                        leftOff = -restOff + restOff * prog
                        rightOff = restOff - restOff * prog
                    } else if (handSpreadProg > 0f) {
                        val spreadOff = restOff * handSpreadProg
                        leftOff = -restOff - spreadOff
                        rightOff = restOff + spreadOff
                    } else if (handHorizProg > 0f) {
                        val horizOff = restOff * handHorizProg
                        leftOff = if (handHorizontalIsLeft) -restOff - horizOff else -restOff
                        rightOff = if (!handHorizontalIsLeft) restOff + horizOff else restOff
                    } else {
                        leftOff = -restOff
                        rightOff = restOff
                    }

                    val leftMatrix = Matrix()
                    leftMatrix.setTranslate(cx + leftOff, handY)
                    leftMatrix.preScale(s * 0.7f, s * 0.7f)
                    val left = Path()
                    left.addPath(handPath, leftMatrix)
                    drawContext.canvas.nativeCanvas.drawPath(left, paint)

                    val rightMatrix = Matrix()
                    rightMatrix.setTranslate(cx + rightOff, handY)
                    rightMatrix.preScale(s * 0.7f, s * 0.7f)
                    val right = Path()
                    right.addPath(handPath, rightMatrix)
                    drawContext.canvas.nativeCanvas.drawPath(right, paint)

                    fun drawFoot(xOffset: Float, mirrorX: Boolean, scaleMul: Float = 1f, yOffset: Float = 0f) {
                        val m = Matrix()
                        val ms = footScale * scaleMul
                        val footCx = (footMinX + footMaxX) / 2f
                        val footCy = (footMinY + footMaxY) / 2f
                        m.setTranslate(footBaseX + xOffset - footCx * ms, footY + yOffset - footCy * ms)
                        m.preScale(if (mirrorX) -ms else ms, ms)
                        val fp = Path()
                        fp.addPath(footPath.first, m)
                        drawContext.canvas.nativeCanvas.drawPath(fp, paint)
                    }

                    val leftZoom: Float
                    val rightZoom: Float
                    val leftYOff: Float
                    val rightYOff: Float

                    val p = footZoomProgress.value
                    val stepPhase = if (p < 0.5f) p * 2f else (1f - (p - 0.5f) * 2f)
                    val rise = -40f * s * stepPhase
                    val zoom = 1f + stepPhase * 0.1f

                    if (isPlaying && p > 0f) {
                        if (jumpActive) {
                            leftZoom = 1f + p * 0.08f
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
                modifier = Modifier
                    .fillMaxWidth()
.offset(y = (-50).dp)
                .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.offset(y = (-40).dp)
                ) {
                    Text("${s.multiplicationTable}:", style = MaterialTheme.typography.bodySmall)

                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { if (!isPlaying) expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = if (selectedTable == -1) s.randomly else "${selectedTable}",
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isPlaying,
                            textStyle = MaterialTheme.typography.bodyMedium,
                            singleLine = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().width(120.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(s.randomly) },
                                onClick = { selectedTable = -1; expanded = false; newMultiplication() }
                            )
                            DropdownMenuItem(
                                text = { Text("1") },
                                onClick = { selectedTable = 1; expanded = false; newMultiplication() }
                            )
                            DropdownMenuItem(
                                text = { Text("2") },
                                onClick = { selectedTable = 2; expanded = false; newMultiplication() }
                            )
                            DropdownMenuItem(
                                text = { Text("3") },
                                onClick = { selectedTable = 3; expanded = false; newMultiplication() }
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.offset(y = (-40).dp)
                ) {
                    Text(s.slowly, style = MaterialTheme.typography.bodySmall)
                    Slider(
                        value = sliderPos,
                        onValueChange = { if (!isPlaying) sliderPos = it },
                        valueRange = 600f..1600f,
                        modifier = Modifier.width(150.dp),
                        enabled = !isPlaying
                    )
                    Text(s.fast, style = MaterialTheme.typography.bodySmall)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.offset(y = (-40).dp)
            ) {
                FilledTonalButton(
                    onClick = { newMultiplication() },
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

        if (isDone && !isPlaying) {
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
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=59648475-3d23-45fe-8d6e-f3def1a2729b"))
                        Toast.makeText(context, s.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=59648475-3d23-45fe-8d6e-f3def1a2729b")
                    }
                )
            }
        }
    }
}
