// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Matrix
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import com.historytracers.app.ui.theme.parseHexColor
import kotlin.math.min

private val yupanaSelectors = listOf(
    -1, 4, 3, 2, 4, 1, 1, 1, 1, 1,
    -1, -1, -1, -1, 2, -1, 4, 3, 2, 2,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, 4
)

private fun getMarkersForDigit(digit: Int): Set<Int> {
    val cols = mutableSetOf<Int>()
    for (offset in 0..2) {
        val idx = digit + offset * 10
        if (idx >= yupanaSelectors.size) break
        val col = yupanaSelectors[idx]
        if (col > 0) cols.add(col)
    }
    return cols
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingToCountScreen(
    skinColor: String = "#A5672C",
    onNavigateBack: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    var counter by remember { mutableIntStateOf(0) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val handColor = remember(skinColor) { parseHexColor(skinColor) }
    val handPath = remember { buildHandPath() }

    fun updateCounter(newValue: Int) {
        counter = newValue.coerceIn(0, 9)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.common.drawToCount) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.common.back)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = s.common.drawToCount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = s.yupana.handsOnYupanaDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val leftFingers = counter / 5
                val rightFingers = counter % 5
                val paint = remember(handColor) {
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = handColor.hashCode()
                        style = Paint.Style.FILL
                        strokeJoin = Paint.Join.ROUND
                    }
                }
                val dotPaint = remember {
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.argb(200, 200, 50, 50)
                        style = Paint.Style.FILL
                    }
                }
                val emptyDotPaint = remember {
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.argb(80, 150, 150, 150)
                        style = Paint.Style.STROKE
                        strokeWidth = 3f
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(0.35f)
                                .fillMaxHeight()
                                .padding(4.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val s = minOf(size.width / 350f, size.height / 480f) * 1.6f
                                val cx = size.width * 0.5f
                                val cy = size.height * 0.5f + 40f * s
                                drawOneHand(cx, cy, s, isLeft = true, raisedFingers = leftFingers, paint, dotPaint, emptyDotPaint, handPath)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(0.30f)
                                .fillMaxHeight()
                                .padding(4.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawYupanaColumn(size, markers = getMarkersForDigit(counter))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(0.35f)
                                .fillMaxHeight()
                                .padding(4.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val s = minOf(size.width / 350f, size.height / 480f) * 1.6f
                                val cx = size.width * 0.5f
                                val cy = size.height * 0.5f + 40f * s
                                drawOneHand(cx, cy, s, isLeft = false, raisedFingers = rightFingers, paint, dotPaint, emptyDotPaint, handPath)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "${s.common.number} $counter",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = { updateCounter(counter + 1) },
                        enabled = counter < 9,
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = ButtonYellow
                        )
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = OnButtonYellow
                        )
                    }

                    FilledIconButton(
                        onClick = { updateCounter(counter - 1) },
                        enabled = counter > 0,
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = ButtonYellow
                        )
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = OnButtonYellow
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "${s.common.valuePrefix} $counter",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(48.dp))
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 8.dp, start = 8.dp)
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
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=687ee328-19bb-4a65-ab46-7d707a2e11dc"))
                            Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showMainTextSubmenu = false
                            uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=687ee328-19bb-4a65-ab46-7d707a2e11dc")
                        }
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawOneHand(
    cx: Float, cy: Float, scale: Float,
    isLeft: Boolean, raisedFingers: Int,
    fillPaint: Paint, dotPaint: Paint, emptyDotPaint: Paint,
    handPath: Path
) {
    val mirror = if (isLeft) -1f else 1f
    val matrix = Matrix()
    matrix.setTranslate(cx, cy)
    matrix.preScale(mirror * scale, scale)

    val hp = Path()
    hp.addPath(handPath, matrix)
    drawContext.canvas.nativeCanvas.drawPath(hp, fillPaint)

    val fingerTips = listOf(
        Pair(-110f, -150f),
        Pair(-40f, -180f),
        Pair(30f, -185f),
        Pair(100f, -160f),
    )
    for (i in fingerTips.indices) {
        val (fx, fy) = fingerTips[i]
        val fx2 = cx + fx * scale * mirror
        val fy2 = cy + fy * scale
        val isRaised = i < raisedFingers
        if (isRaised) {
            drawContext.canvas.nativeCanvas.drawCircle(fx2, fy2, 12f * scale, dotPaint)
        } else {
            drawContext.canvas.nativeCanvas.drawCircle(fx2, fy2, 12f * scale, emptyDotPaint)
        }
    }

    val thumbTip = if (isLeft) Pair(200f, -10f) else Pair(-200f, -10f)
    val tx = cx + thumbTip.first * scale * mirror
    val ty = cy + thumbTip.second * scale
    if (raisedFingers >= 1) {
        drawContext.canvas.nativeCanvas.drawCircle(tx, ty, 14f * scale, dotPaint)
    } else {
        drawContext.canvas.nativeCanvas.drawCircle(tx, ty, 14f * scale, emptyDotPaint)
    }
}

private fun DrawScope.drawYupanaColumn(size: Size, markers: Set<Int>) {
    val cw = size.width
    val ch = size.height
    val margin = cw * 0.04f
    val usableWidth = cw - 2f * margin
    val colW = usableWidth / 4f
    val startX = margin
    val startY = ch * 0.04f
    val rowHeight = ch - 2f * startY

    val cornerRadius = CornerRadius(cw * 0.008f)
    val shadowOffset = ch * 0.008f

    for (col in 0..3) {
        val cellLeft = startX + col * colW
        val cellTop = startY

        drawRoundRect(
            color = Color(0xFF000000).copy(alpha = 0.1f),
            topLeft = Offset(cellLeft + shadowOffset, cellTop + shadowOffset),
            size = Size(colW, rowHeight),
            cornerRadius = cornerRadius
        )
        drawRoundRect(
            color = Color(0xFFFEF8E8),
            topLeft = Offset(cellLeft, cellTop),
            size = Size(colW, rowHeight),
            cornerRadius = cornerRadius
        )
        drawRoundRect(
            color = Color(0xFFB48B5A),
            topLeft = Offset(cellLeft, cellTop),
            size = Size(colW, rowHeight),
            cornerRadius = cornerRadius,
            style = Stroke(width = cw * 0.003f)
        )
    }

    val dotRadius = min(colW * 0.15f, rowHeight * 0.15f)
    val coloredDotRadius = dotRadius * 0.9f

    val dotPositionsByCol = listOf(
        listOf(
            Offset(-dotRadius * 1.5f, -dotRadius * 2f),
            Offset(-dotRadius * 1.5f, 0f),
            Offset(-dotRadius * 1.5f, dotRadius * 2f),
            Offset(dotRadius * 1.5f, -dotRadius * 0.8f),
            Offset(dotRadius * 1.5f, dotRadius * 0.8f),
        ),
        listOf(
            Offset(0f, -dotRadius * 1.8f),
            Offset(0f, 0f),
            Offset(0f, dotRadius * 1.8f),
        ),
        listOf(
            Offset(0f, -dotRadius * 1.2f),
            Offset(0f, dotRadius * 1.2f),
        ),
        listOf(
            Offset(0f, 0f),
        ),
    )

    for (col in 0..3) {
        val cx = startX + col * colW + colW / 2f
        val cy = startY + rowHeight / 2f
        val colNum = col + 1
        val hasMarker = colNum in markers
        val drawPositions = dotPositionsByCol[col]

        if (hasMarker) {
            val my = cy - rowHeight * 0.26f
            drawCircle(
                color = Color(0xFFC0392B),
                radius = coloredDotRadius,
                center = Offset(cx, my)
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.2f),
                radius = coloredDotRadius,
                center = Offset(cx, my),
                style = Stroke(width = cw * 0.002f)
            )
        }

        val dotColor = when (col) {
            0 -> Color(0xFF6B3A1A)
            1 -> Color(0xFF5B2E12)
            2 -> Color(0xFF4A2210)
            3 -> Color(0xFF3A1808)
            else -> Color.Gray
        }

        for (pos in drawPositions) {
            val dotCenter = Offset(cx + pos.x, cy + pos.y)
            drawCircle(
                color = dotColor,
                radius = dotRadius * 0.8f,
                center = dotCenter
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.15f),
                radius = dotRadius * 0.8f,
                center = dotCenter,
                style = Stroke(width = cw * 0.0015f)
            )
        }
    }
}
