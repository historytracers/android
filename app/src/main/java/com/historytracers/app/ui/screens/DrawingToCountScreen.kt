// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingToCountScreen(
    onNavigateBack: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    var counter by remember { mutableIntStateOf(0) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

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
                val skinColor = Color(0xFFC68642)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2.2f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(0.4f)
                                .fillMaxHeight()
                                .padding(4.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawYupanaColumn(size, markers = getMarkersForDigit(counter))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(0.3f)
                                .fillMaxHeight()
                                .padding(4.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawHand(leftFingers, isLeft = true, handColor = skinColor, canvasSize = size)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(0.3f)
                                .fillMaxHeight()
                                .padding(4.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawHand(rightFingers, isLeft = false, handColor = skinColor, canvasSize = size)
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

private fun DrawScope.drawHand(
    raisedFingers: Int,
    isLeft: Boolean,
    handColor: Color,
    canvasSize: Size
) {
    val cw = canvasSize.width
    val ch = canvasSize.height
    val outlineColor = handColor.copy(alpha = 0.7f)
    val palmWidth = cw * 0.55f
    val palmHeight = ch * 0.55f
    val palmCx = cw / 2f
    val palmTop = ch * 0.50f
    val fingerWidth = cw * 0.12f
    val fingerRaisedLen = ch * 0.38f
    val fingerLoweredLen = ch * 0.08f
    val cornerR = cw * 0.04f

    val palmPath = Path().apply {
        val left = palmCx - palmWidth / 2f
        val right = palmCx + palmWidth / 2f
        val top = palmTop
        val bottom = palmTop + palmHeight
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                left, top, right, bottom,
                topLeftCornerRadius = CornerRadius(cornerR),
                topRightCornerRadius = CornerRadius(cornerR),
                bottomRightCornerRadius = CornerRadius(cornerR * 1.5f),
                bottomLeftCornerRadius = CornerRadius(cornerR * 1.5f)
            )
        )
    }
    drawPath(palmPath, handColor)
    drawPath(palmPath, outlineColor, style = Stroke(width = cw * 0.01f))

    val fingerXs: List<Float>
    if (isLeft) {
        fingerXs = listOf(
            palmCx + palmWidth * 0.35f,
            palmCx + palmWidth * 0.12f,
            palmCx - palmWidth * 0.08f,
            palmCx - palmWidth * 0.28f,
        )
    } else {
        fingerXs = listOf(
            palmCx - palmWidth * 0.35f,
            palmCx - palmWidth * 0.12f,
            palmCx + palmWidth * 0.08f,
            palmCx + palmWidth * 0.28f,
        )
    }

    for (i in 0..3) {
        val isRaised = i < raisedFingers
        val len = if (isRaised) fingerRaisedLen else fingerLoweredLen
        val fingerTop = palmTop - len
        val fw = fingerWidth * (1f - i * 0.08f)
        val fx = fingerXs[i] - fw / 2f

        val fingerPath = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    fx, fingerTop, fx + fw, palmTop,
                    topLeftCornerRadius = CornerRadius(cornerR * 0.6f),
                    topRightCornerRadius = CornerRadius(cornerR * 0.6f),
                    bottomLeftCornerRadius = CornerRadius(0f),
                    bottomRightCornerRadius = CornerRadius(0f)
                )
            )
        }
        drawPath(fingerPath, handColor)
        drawPath(fingerPath, outlineColor, style = Stroke(width = cw * 0.01f))
    }

    val thumbRaised = raisedFingers >= 1
    val thumbLen = if (thumbRaised) ch * 0.30f else ch * 0.10f
    val thumbDir = if (isLeft) 1f else -1f
    val thumbX = palmCx + thumbDir * (palmWidth / 2f)
    val thumbW = cw * 0.13f
    val thumbPath = Path().apply {
        val tx = thumbX + thumbDir * (if (thumbRaised) -thumbLen * 0.35f else 0f)
        val ty = palmTop + palmHeight * 0.25f
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                minOf(thumbX, tx), ty,
                maxOf(thumbX, tx) + thumbW, ty + thumbW * 1.3f,
                cornerRadius = CornerRadius(cornerR * 0.5f)
            )
        )
    }
    drawPath(thumbPath, handColor)
    drawPath(thumbPath, outlineColor, style = Stroke(width = cw * 0.01f))
}
