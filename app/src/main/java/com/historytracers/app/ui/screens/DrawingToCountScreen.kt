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

private val fingerTips = listOf(
    Offset(-240f, 243f),
    Offset(-170f, 233f),
    Offset(-100f, 228f),
    Offset(-35f, 233f),
    Offset(50f, 258f),
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
    onNavigateBack: () -> Unit = {},
    onNavigateToHandsOnYupana: () -> Unit = {}
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
                title = { Text(s.yupana.handsOnYupana) },
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
                    text = s.yupana.handsOnYupana,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = s.yupana.drawingToCountDescription,
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
                val numberPaint = remember {
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.WHITE
                        textAlign = Paint.Align.CENTER
                        style = Paint.Style.FILL
                    }
                }
                val numberStrokePaint = remember {
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.BLACK
                        textAlign = Paint.Align.CENTER
                        style = Paint.Style.STROKE
                        strokeWidth = 3f
                    }
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5f)
                ) {
                    val rowBottomRatio = 3f / 480f + (1f - 6f / 480f) / 4f
                    val contentOffset = maxHeight * rowBottomRatio + 50.dp

                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp)
                        ) {
                            val margin = 3f / 860f * size.width
                            val usableWidth = size.width - 2f * margin
                            val colW = usableWidth / 4f
                            val startX = margin
                            val rowHeight = (size.height - 6f / 480f * size.height) / 4f
                            val startY = 3f / 480f * size.height
                            drawYupanaRow(
                                cellOriginX = startX,
                                cellOriginY = startY,
                                cellWidth = colW,
                                cellHeight = rowHeight,
                                canvasSize = size,
                                markers = getMarkersForDigit(counter)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.26f)
                                .align(Alignment.CenterStart)
                                .padding(2.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val s = minOf(size.width / 500f, size.height / 500f) * 2.4f
                                val cx = size.width * 0.42f
                                val ypRowCenter = 3f / 480f * size.height + ((size.height - 6f / 480f * size.height) / 4f) / 2f
                                val cy = ypRowCenter
                                drawOneHand(cx, cy, s, isLeft = true, paint, handPath)
                                drawHandNumbers(
                                    numbers = listOf(6 to 0, 7 to 1, 8 to 2, 9 to 3).filter { (n, _) -> n <= counter },
                                    cx = cx, cy = cy, handScale = s, isLeft = true,
                                    textPaint = numberPaint, strokePaint = numberStrokePaint
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.26f)
                                .align(Alignment.CenterEnd)
                                .padding(2.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val s = minOf(size.width / 500f, size.height / 500f) * 2.4f
                                val cx = size.width * 0.58f
                                val ypRowCenter = 3f / 480f * size.height + ((size.height - 6f / 480f * size.height) / 4f) / 2f
                                val cy = ypRowCenter
                                drawOneHand(cx, cy, s, isLeft = false, paint, handPath)
                                drawHandNumbers(
                                    numbers = listOf(1 to 4, 2 to 3, 3 to 2, 4 to 1, 5 to 0).filter { (n, _) -> n <= counter },
                                    cx = cx, cy = cy, handScale = s, isLeft = false,
                                    textPaint = numberPaint, strokePaint = numberStrokePaint
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(y = contentOffset)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${s.common.number} $counter",
                                style = MaterialTheme.typography.headlineSmall,
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
                                    modifier = Modifier.size(56.dp),
                                    shape = CircleShape,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = ButtonYellow
                                    )
                                ) {
                                    Icon(
                                        Icons.Filled.KeyboardArrowUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp),
                                        tint = OnButtonYellow
                                    )
                                }

                                FilledIconButton(
                                    onClick = { updateCounter(counter - 1) },
                                    enabled = counter > 0,
                                    modifier = Modifier.size(56.dp),
                                    shape = CircleShape,
                                    colors = IconButtonDefaults.filledIconButtonColors(
                                        containerColor = ButtonYellow
                                    )
                                ) {
                                    Icon(
                                        Icons.Filled.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp),
                                        tint = OnButtonYellow
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = onNavigateToHandsOnYupana,
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
                    Text(
                        text = s.yupana.tawantsuyu,
                        fontWeight = FontWeight.Bold
                    )
                }
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
    isLeft: Boolean, fillPaint: Paint, handPath: Path
) {
    val mirror = if (isLeft) -1f else 1f
    val m = Matrix()
    m.setTranslate(cx, cy)
    if (isLeft) {
        m.preScale(-scale * 0.7f, scale * 0.7f)
    } else {
        m.preScale(scale * 0.7f, scale * 0.7f)
    }
    val hp = Path()
    hp.addPath(handPath, m)
    drawContext.canvas.nativeCanvas.drawPath(hp, fillPaint)
}

private fun DrawScope.drawHandNumbers(
    numbers: List<Pair<Int, Int>>,
    cx: Float, cy: Float, handScale: Float,
    isLeft: Boolean,
    textPaint: Paint, strokePaint: Paint,
) {
    val s = handScale * 0.7f
    val textSize = 28f * handScale
    textPaint.textSize = textSize
    strokePaint.textSize = textSize
    val numberOffsets = mapOf(
        1 to Offset(-24f, 0f),
        2 to Offset(-5f, -15f),
        3 to Offset(7f, -28f),
        4 to Offset(14f, -31f),
        5 to Offset(11f, -19f),
        6 to Offset(-10f, -18f),
        7 to Offset(-20f, -30f),
        8 to Offset(-9f, -25f),
        9 to Offset(0f, -15f),
    )
    for ((num, fi) in numbers) {
        val f = fingerTips[fi]
        val off = numberOffsets[num]
        val dx = if (off != null) with(density) { off.x.dp.toPx() } else 0f
        val dy = if (off != null) with(density) { off.y.dp.toPx() } else 0f
        val x = cx + (if (isLeft) -f.x else f.x) * s + dx
        val y = cy + f.y * s + textSize * 0.35f + dy
        drawContext.canvas.nativeCanvas.drawText(num.toString(), x, y, strokePaint)
        drawContext.canvas.nativeCanvas.drawText(num.toString(), x, y, textPaint)
    }
}

private fun DrawScope.drawYupanaRow(
    cellOriginX: Float,
    cellOriginY: Float,
    cellWidth: Float,
    cellHeight: Float,
    canvasSize: Size,
    markers: Set<Int>,
) {
    val cw = canvasSize.width
    val ch = canvasSize.height

    val cornerRadius = CornerRadius(6f / 860f * cw)
    val borderWidth = 1.2f / 480f * ch
    val shadowOffset = 2f / 480f * ch

    for (col in 0..3) {
        val cellLeft = cellOriginX + col * cellWidth
        val cellTop = cellOriginY

        drawRoundRect(
            color = Color(0xFF000000).copy(alpha = 0.1f),
            topLeft = Offset(cellLeft + shadowOffset, cellTop + shadowOffset),
            size = Size(cellWidth, cellHeight),
            cornerRadius = cornerRadius
        )

        drawRoundRect(
            color = Color(0xFFFEF8E8),
            topLeft = Offset(cellLeft, cellTop),
            size = Size(cellWidth, cellHeight),
            cornerRadius = cornerRadius
        )

        drawRoundRect(
            color = Color(0xFFB48B5A),
            topLeft = Offset(cellLeft, cellTop),
            size = Size(cellWidth, cellHeight),
            cornerRadius = cornerRadius,
            style = Stroke(width = borderWidth)
        )
    }

    val dotRadius = minOf(cellWidth * 0.18f, cellHeight * 0.18f, 9f / 860f * cw)
    val markerRadius = dotRadius * 0.9f
    val markerGap = cellHeight * 0.12f
    val extraPx = with(density) { 3.dp.toPx() }
    val gapOffset = with(density) { 2.5.dp.toPx() }

    val dotPositionsByCol = listOf(
        listOf(
            Offset(-dotRadius * 1.5f, -dotRadius * 2f),
            Offset(-dotRadius * 1.5f, 0f),
            Offset(-dotRadius * 1.5f, dotRadius * 2f),
            Offset(dotRadius * 1.5f, -dotRadius * 0.8f - gapOffset),
            Offset(dotRadius * 1.5f, dotRadius * 0.8f + gapOffset),
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
        val cx = cellOriginX + col * cellWidth + cellWidth / 2f
        val cy = cellOriginY + cellHeight / 2f
        val colNum = col + 1
        val hasMarker = colNum in markers
        val drawPositions = dotPositionsByCol[col]

        val topEdge = cellOriginY + cellHeight * 0.08f
        val topMarkerY = topEdge + markerGap

        if (hasMarker) {
            val my = topMarkerY - extraPx
            drawCircle(
                color = Color(0xFFC0392B),
                radius = markerRadius,
                center = Offset(cx, my)
            )
            drawCircle(
                color = Color(0xFF000000).copy(alpha = 0.2f),
                radius = markerRadius,
                center = Offset(cx, my),
                style = Stroke(width = 0.8f / 480f * ch)
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
                style = Stroke(width = 0.6f / 440f * ch)
            )
        }
    }
}
