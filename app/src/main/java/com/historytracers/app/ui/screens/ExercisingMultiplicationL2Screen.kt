// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
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

private data class MultiplicationProblemL2(
    val topValue: Int,
    val bottomValue: Int,
    val result: Int
)

private fun generateProblem(table: Int): MultiplicationProblemL2 {
    val top = Random.nextInt(1, 10)
    val bottom = if (table == -1) Random.nextInt(4, 6) else table
    return MultiplicationProblemL2(top, bottom, top * bottom)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisingMultiplicationL2Screen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val handPath = remember { buildHandPath() }

    var problem by remember { mutableStateOf(generateProblem(-1)) }
    var selectedTable by remember { mutableIntStateOf(-1) }
    var isPlaying by remember { mutableStateOf(false) }
    var isDone by remember { mutableStateOf(false) }
    var sliderPos by remember { mutableFloatStateOf(1100f) }

    val scope = rememberCoroutineScope()
    val handUpDownProgress = remember { Animatable(0f) }
    val clapProgress = remember { Animatable(0f) }

    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }

    val cycleTime = 2400f - sliderPos

    fun newMultiplication() {
        if (isPlaying) return
        problem = generateProblem(selectedTable)
        isDone = false
        scope.launch {
            handUpDownProgress.snapTo(0f)
            clapProgress.snapTo(0f)
        }
    }

    suspend fun animateUpDown(repetitions: Int) {
        val duration = (cycleTime * 0.6f).toInt()
        val pause = (duration * 0.3f).toInt()
        for (i in 0 until repetitions) {
            handUpDownProgress.snapTo(0f)
            handUpDownProgress.animateTo(1f, tween(duration))
            handUpDownProgress.snapTo(0f)
            if (i < repetitions - 1) delay(pause.toLong())
        }
    }

    suspend fun animateUpDownThenClap(repetitions: Int) {
        val upDownDuration = (cycleTime * 0.5f).toInt()
        val clapDuration = (cycleTime * 0.75f).toInt()
        val pause = 80L
        for (i in 0 until repetitions) {
            handUpDownProgress.snapTo(0f)
            handUpDownProgress.animateTo(1f, tween(upDownDuration))
            handUpDownProgress.snapTo(0f)
            delay(pause)
            clapProgress.snapTo(0f)
            clapProgress.animateTo(1f, tween(clapDuration))
            clapProgress.snapTo(0f)
            if (i < repetitions - 1) delay(pause)
        }
    }

    suspend fun executeMult() {
        val reps = problem.topValue
        when (problem.bottomValue) {
            4 -> animateUpDown(reps)
            else -> animateUpDownThenClap(reps)
        }
    }

    fun startExercise() {
        if (isPlaying) return
        scope.launch {
            isPlaying = true
            isDone = false

            executeMult()

            isPlaying = false
            isDone = true
            onScoreChanged(currentScore + 2)
            preferences.recordLessonCompletion()
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back)
                }
                Text(
                    text = s.relationship,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = s.exercisingMultiplicationL2Description,
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
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 26.sp),
                    modifier = Modifier.width(35.dp),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.width(35.dp), contentAlignment = Alignment.Center) {
                    Text("×", style = MaterialTheme.typography.headlineLarge.copy(fontSize = 26.sp))
                }
                Text(
                    text = "${problem.bottomValue}",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 26.sp),
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
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 26.sp),
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
                    color = android.graphics.Color.parseColor("#F4C2A1")
                    style = Paint.Style.FILL
                    strokeJoin = Paint.Join.ROUND
                }

                val handY = cy - 200f * s - 140f * density + 10f * density + 10f * density + 20f * density + 40f * density - 70f * density + 30f * density
                val restOff = 120f * s
                val upDownProg = handUpDownProgress.value
                val clapProg = clapProgress.value

                val leftOff: Float
                val rightOff: Float
                val leftYOff: Float
                val rightYOff: Float

                if (clapProg > 0f) {
                    leftOff = -restOff + restOff * clapProg
                    rightOff = restOff - restOff * clapProg
                    leftYOff = 0f
                    rightYOff = 0f
                } else if (upDownProg > 0f) {
                    val phase = if (upDownProg < 0.5f) upDownProg * 2f else (1f - (upDownProg - 0.5f) * 2f)
                    val upY = -80f * s * phase
                    leftOff = -restOff
                    rightOff = restOff
                    leftYOff = upY
                    rightYOff = upY
                } else {
                    leftOff = -restOff
                    rightOff = restOff
                    leftYOff = 0f
                    rightYOff = 0f
                }

                val leftMatrix = Matrix()
                leftMatrix.setTranslate(cx + leftOff, handY + leftYOff)
                leftMatrix.preScale(s * 0.7f, s * 0.7f)
                val left = Path()
                left.addPath(handPath, leftMatrix)
                drawContext.canvas.nativeCanvas.drawPath(left, paint)

                val rightMatrix = Matrix()
                rightMatrix.setTranslate(cx + rightOff, handY + rightYOff)
                rightMatrix.preScale(s * 0.7f, s * 0.7f)
                val right = Path()
                right.addPath(handPath, rightMatrix)
                drawContext.canvas.nativeCanvas.drawPath(right, paint)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp)
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
                            text = { Text("4") },
                            onClick = { selectedTable = 4; expanded = false; newMultiplication() }
                        )
                        DropdownMenuItem(
                            text = { Text("5") },
                            onClick = { selectedTable = 5; expanded = false; newMultiplication() }
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
                modifier = Modifier.offset(y = (-20).dp)
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
                .fillMaxSize()
                .padding(bottom = 10.dp)
                .wrapContentHeight(Alignment.Bottom)
        )
    }
}
