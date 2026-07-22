// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

private const val COLUMNS = 9
private const val UPPER_MAX = 2
private const val LOWER_MAX = 5

private data class SuanpanColumnState(val upper: Int = 0, val lower: Int = 0) {
    val value: Int get() = upper * 5 + lower
}

private fun SuanpanValue(state: List<SuanpanColumnState>): Long {
    var result = 0L
    for (col in state) {
        result = result * 10 + col.value.coerceIn(0, 9)
    }
    return result
}

@Composable
fun SuanpanWritingScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val state = remember { mutableStateOf<List<SuanpanColumnState>>(List(COLUMNS) { SuanpanColumnState() }) }
    val targetValue = remember { mutableStateOf(Random.nextInt(1, 10)) }
    val showCongrats = remember { mutableStateOf(false) }
    val currentValue = SuanpanValue(state.value)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    var showAPalSubmenu by remember { mutableStateOf(false) }

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
                    text = s.writingToSuanpan,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = s.suanpanWritingInstruction,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp).offset(y = (-100).dp)
            )

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier.offset(y = (-60).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .aspectRatio(860f / 400f)
                    .offset(y = (-20).dp)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val cw = size.width.toFloat()
                            val ch = size.height.toFloat()
                            val margin = 28f / 860f * cw
                            val usableWidth = cw - 2f * margin
                            val colW = usableWidth / COLUMNS
                            val startX = margin + colW / 2f
                            val beamY = ch / 2f
                            val bR = minOf(colW * 0.38f, 10f / 400f * ch, 10f / 860f * cw)
                            val dtt = beamY - 28f / 400f * ch
                            val dtb = beamY + 28f / 400f * ch
                            val x = offset.x
                            val y = offset.y

                            var colHit = -1
                            for (i in 0 until COLUMNS) {
                                if (abs(x - (startX + i * colW)) < colW * 0.45f) {
                                    colHit = i
                                    break
                                }
                            }

                            if (colHit < 0) return@detectTapGestures

                            val cx = startX + colHit * colW
                            var handled = false

                            for (bi in 0 until UPPER_MAX) {
                                val activeY = dtt - 6f / 400f * ch - bi * 22f / 400f * ch
                                val inactiveY = dtt - 38f / 400f * ch - bi * 22f / 400f * ch
                                val beadY = if (bi < state.value[colHit].upper) activeY else inactiveY
                                if (sqrt((x - cx) * (x - cx) + (y - beadY) * (y - beadY)) < bR + 8f / 400f * ch && y < dtt - 2f / 400f * ch) {
                                    val cur = state.value[colHit].upper
                                    val newUpper = if (bi < cur) bi else bi + 1
                                    state.value = state.value.toMutableList().also {
                                        it[colHit] = it[colHit].copy(upper = newUpper.coerceIn(0, UPPER_MAX))
                                    }
                                    handled = true
                                    break
                                }
                            }

                            if (!handled) {
                                for (bi in 0 until LOWER_MAX) {
                                    val activeY = dtb + 8f / 400f * ch + bi * 22f / 400f * ch
                                    val inactiveY = activeY + 28f / 400f * ch
                                    val beadY = if (bi < state.value[colHit].lower) activeY else inactiveY
                                    if (sqrt((x - cx) * (x - cx) + (y - beadY) * (y - beadY)) < bR + 8f / 400f * ch && y > dtb + 2f / 400f * ch) {
                                        val cur = state.value[colHit].lower
                                        val newLower = if (bi < cur) bi else bi + 1
                                        state.value = state.value.toMutableList().also {
                                            it[colHit] = it[colHit].copy(lower = newLower.coerceIn(0, LOWER_MAX))
                                        }
                                        break
                                    }
                                }
                            }
                        }
                    }
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val margin = 28f / 860f * canvasWidth
                val usableWidth = canvasWidth - 2f * margin
                val colWidth = usableWidth / COLUMNS
                val startX = margin + colWidth / 2f
                val beamY = canvasHeight / 2f
                val ballRadius = minOf(colWidth * 0.38f, 10f / 400f * canvasHeight, 10f / 860f * canvasWidth)
                val decimalTrackTop = beamY - 28f / 400f * canvasHeight
                val decimalTrackBottom = beamY + 28f / 400f * canvasHeight

                drawRect(color = Color(0xFFFEF5E0), size = size)

                drawRect(
                    color = Color(0xFFDAC894).copy(alpha = 0.4f),
                    topLeft = Offset(5f / 860f * canvasWidth, decimalTrackTop),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 10f / 860f * canvasWidth, decimalTrackBottom - decimalTrackTop)
                )
                drawRect(
                    color = Color(0xFFB59762),
                    topLeft = Offset(6f / 860f * canvasWidth, decimalTrackTop + 2f / 400f * canvasHeight),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 12f / 860f * canvasWidth, decimalTrackBottom - decimalTrackTop - 4f / 400f * canvasHeight),
                    style = Stroke(width = 2f / 400f * canvasHeight)
                )

                drawLine(
                    color = Color(0xFFC9A05A),
                    start = Offset(8f / 860f * canvasWidth, beamY),
                    end = Offset(canvasWidth - 8f / 860f * canvasWidth, beamY),
                    strokeWidth = 3f / 400f * canvasHeight
                )

                drawRect(
                    color = Color(0xFFC9A86B),
                    topLeft = Offset(5f / 860f * canvasWidth, beamY - 6f / 400f * canvasHeight),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 10f / 860f * canvasWidth, 12f / 400f * canvasHeight)
                )
                drawRect(
                    color = Color(0xFFE5C28E),
                    topLeft = Offset(5f / 860f * canvasWidth, beamY - 4f / 400f * canvasHeight),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 10f / 860f * canvasWidth, 8f / 400f * canvasHeight)
                )
                drawRect(
                    color = Color(0xFFF5E2B0),
                    topLeft = Offset(5f / 860f * canvasWidth, beamY - 2f / 400f * canvasHeight),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 10f / 860f * canvasWidth, 4f / 400f * canvasHeight)
                )

                for (col in 0 until COLUMNS) {
                    drawSuanpanColumn(
                        cx = startX + col * colWidth,
                        canvasWidth = canvasWidth,
                        canvasHeight = canvasHeight,
                        ballRadius = ballRadius,
                        decimalTrackTop = decimalTrackTop,
                        decimalTrackBottom = decimalTrackBottom,
                        upperCount = state.value[col].upper,
                        lowerCount = state.value[col].lower
                    )
                }

                drawRect(
                    color = Color(0xFFF9EEC7),
                    topLeft = Offset(5f / 860f * canvasWidth, 5f / 400f * canvasHeight),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 10f / 860f * canvasWidth, canvasHeight - 10f / 400f * canvasHeight),
                    style = Stroke(width = 2.5f / 400f * canvasHeight)
                )
                drawRect(
                    color = Color(0xFFB48B5A),
                    topLeft = Offset(3f / 860f * canvasWidth, 3f / 400f * canvasHeight),
                    size = androidx.compose.ui.geometry.Size(canvasWidth - 6f / 860f * canvasWidth, canvasHeight - 6f / 400f * canvasHeight),
                    style = Stroke(width = 1.8f / 400f * canvasHeight)
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(40.dp),
                    color = Color(0xFF2E241F),
                ) {
                    Text(
                        text = "${s.value}: $currentValue",
                        color = Color(0xFFF2ECD8),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(40.dp),
                    color = Color(0xFFFFF9E6),
                ) {
                    Text(
                        text = "${s.write}: ${targetValue.value}",
                        color = Color(0xFF2E241F),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            FilledTonalButton(
                onClick = {
                    state.value = List(COLUMNS) { SuanpanColumnState() }
                    targetValue.value = Random.nextInt(1, 10)
                    showCongrats.value = false
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Text(
                    text = s.newExercise,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showCongrats.value) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${s.congratulationTitle} \uD83C\uDF89",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = s.resetHint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }

    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(bottom = 8.dp, start = 8.dp)
    ) {
        val uriHandler = LocalUriHandler.current
        val ctx = LocalContext.current

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
            expanded = showSourcesMenu && !showMainTextSubmenu && !showAPalSubmenu,
            onDismissRequest = { showSourcesMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(s.originalText) },
                trailingIcon = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                },
                onClick = { showMainTextSubmenu = true }
            )
            DropdownMenuItem(
                text = { Text(s.aPal) },
                trailingIcon = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                },
                onClick = { showAPalSubmenu = true }
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
                    val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=716d6fe5-78cc-4fef-85da-11daff12399d"))
                    Toast.makeText(ctx, s.copyUrl, Toast.LENGTH_SHORT).show()
                }
            )
            DropdownMenuItem(
                text = { Text(s.goToUrl) },
                onClick = {
                    showSourcesMenu = false
                    showMainTextSubmenu = false
                    uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=716d6fe5-78cc-4fef-85da-11daff12399d")
                }
            )
        }

        DropdownMenu(
            expanded = showSourcesMenu && showAPalSubmenu,
            onDismissRequest = { showAPalSubmenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(s.copyUrl) },
                onClick = {
                    showSourcesMenu = false
                    showAPalSubmenu = false
                    val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.apalconnect.org/wp-content/uploads/2018/12/Chinese-Abacus-Introduction.pdf"))
                    Toast.makeText(ctx, s.copyUrl, Toast.LENGTH_SHORT).show()
                }
            )
            DropdownMenuItem(
                text = { Text(s.goToUrl) },
                onClick = {
                    showSourcesMenu = false
                    showAPalSubmenu = false
                    uriHandler.openUri("https://www.apalconnect.org/wp-content/uploads/2018/12/Chinese-Abacus-Introduction.pdf")
                }
            )
        }
    }
}

if (currentValue == targetValue.value.toLong() && !showCongrats.value) {
        showCongrats.value = true
        onScoreChanged(currentScore + 2)
        scope.launch { preferences.recordLessonCompletion() }
    scope.launch { preferences.markAbacusSectionCompleted("suanpan_writing") }
    }
}

private fun DrawScope.drawSuanpanColumn(
    cx: Float,
    canvasWidth: Float,
    canvasHeight: Float,
    ballRadius: Float,
    decimalTrackTop: Float,
    decimalTrackBottom: Float,
    upperCount: Int,
    lowerCount: Int
) {
    drawLine(
        color = Color(0xFFB08054),
        start = Offset(cx, 8f / 860f * canvasWidth),
        end = Offset(cx, canvasHeight - 10f / 400f * canvasHeight),
        strokeWidth = 3f / 400f * canvasHeight
    )

    for (i in 0 until UPPER_MAX) {
        val activeY = decimalTrackTop - 6f / 400f * canvasHeight - i * 22f / 400f * canvasHeight
        val inactiveY = decimalTrackTop - 38f / 400f * canvasHeight - i * 22f / 400f * canvasHeight
        val beadActive = i < upperCount
        val by = if (beadActive) activeY else inactiveY

        drawCircle(color = Color(0xFFC03A28), radius = ballRadius, center = Offset(cx, by))
        drawCircle(color = Color(0xFFF06A50), radius = ballRadius * 0.85f, center = Offset(cx, by))
        drawCircle(color = Color(0xFF4A2018), radius = ballRadius, center = Offset(cx, by), style = Stroke(width = 1.5f / 400f * canvasHeight))
        drawCircle(color = Color(0xFFFFEAD4), radius = 3f / 860f * canvasWidth, center = Offset(cx - 3f / 860f * canvasWidth, by - 3f / 400f * canvasHeight))
    }

    for (i in 0 until LOWER_MAX) {
        val activeY = decimalTrackBottom + 8f / 400f * canvasHeight + i * 22f / 400f * canvasHeight
        val inactiveY = activeY + 28f / 400f * canvasHeight
        val beadActive = i < lowerCount
        val by = if (beadActive) activeY else inactiveY

        drawCircle(color = Color(0xFF3A6068), radius = ballRadius - 0.5f / 400f * canvasHeight, center = Offset(cx, by))
        drawCircle(color = Color(0xFF7DA0AE), radius = (ballRadius - 0.5f / 400f * canvasHeight) * 0.85f, center = Offset(cx, by))
        drawCircle(color = Color(0xFF1A3A3A), radius = ballRadius - 0.5f / 400f * canvasHeight, center = Offset(cx, by), style = Stroke(width = 1.2f / 400f * canvasHeight))
        drawCircle(color = Color(0xFFC8E2EC), radius = 2.5f / 860f * canvasWidth, center = Offset(cx - 2.5f / 860f * canvasWidth, by - 2.5f / 400f * canvasHeight))
    }
}
