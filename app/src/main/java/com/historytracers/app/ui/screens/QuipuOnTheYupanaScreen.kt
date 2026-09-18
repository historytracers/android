// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.drawYupanaBackground
import com.historytracers.app.ui.components.drawYupanaFrame
import com.historytracers.app.ui.components.drawYupanaRow
import com.historytracers.app.ui.features.quipuOnTheYupanaScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val ROWS = 4
private const val COLS = 4
private val COL_VALUES = intArrayOf(5, 3, 2, 1)
private val LEVEL_POSITIONS = intArrayOf(1, 2, 3)
private val LEVEL_MIN = intArrayOf(1, 10, 100)
private val LEVEL_MAX = intArrayOf(9, 99, 999)
private const val ORIGINAL_TEXT_URL = "https://www.historytracers.org/index.html?page=class_content&arg=0b6ac947-9ec3-4406-a329-54ca128b5e90"

private fun pow10(exp: Int): Int = when (exp) {
    0 -> 1
    1 -> 10
    2 -> 100
    else -> 1000
}

private fun emptyRed(): List<List<Boolean>> = List(ROWS) { List(COLS) { false } }

private fun randomTarget(level: Int): Int =
    Random.nextInt(LEVEL_MIN[level], LEVEL_MAX[level] + 1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuipuOnTheYupanaScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = quipuOnTheYupanaScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    fun award(points: Int) {
        if (points <= 0) return
        totalAwarded += points
        onScoreChanged(initialScore + totalAwarded)
    }

    var level by remember { mutableIntStateOf(0) }
    var target by remember { mutableIntStateOf(randomTarget(0)) }
    var red by remember { mutableStateOf(emptyRed()) }
    var message by remember { mutableStateOf("") }
    var levelDone by remember { mutableStateOf(false) }
    var sectionMarked by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }

    fun orderName(row: Int): String = when (row) {
        0 -> xs.orderUnits
        1 -> xs.orderTens
        2 -> xs.orderHundreds
        else -> xs.orderThousands
    }

    fun rowDigit(row: Int): Int {
        var value = 0
        for (c in 0 until COLS) if (red[row][c]) value += COL_VALUES[c]
        return value
    }

    fun totalValue(): Int {
        var total = 0
        for (r in 0 until ROWS) total += rowDigit(r) * pow10(r)
        return total
    }

    fun startLevel(newLevel: Int) {
        level = newLevel
        target = randomTarget(newLevel)
        red = emptyRed()
        levelDone = false
        message = xs.msgStart
    }

    fun toggle(row: Int, col: Int) {
        if (levelDone) return
        val candidate = red.map { it.toMutableList() }.toMutableList()
        candidate[row][col] = !candidate[row][col]

        val overflowRow = (0 until ROWS).firstOrNull { r ->
            var value = 0
            for (c in 0 until COLS) if (candidate[r][c]) value += COL_VALUES[c]
            value > 9
        }
        if (overflowRow != null) {
            message = xs.msgOverflow.format(orderName(overflowRow))
            return
        }

        red = candidate

        if (totalValue() == target) {
            levelDone = true
            val isLastLevel = level >= LEVEL_POSITIONS.size - 1
            message = if (isLastLevel) xs.msgAllLevels else xs.msgLevelComplete
            award(1)
            if (isLastLevel && !sectionMarked) {
                sectionMarked = true
                scope.launch { preferences.markYupanaSectionCompleted("quipu_on_the_yupana") }
                scope.launch { preferences.recordLessonCompletion() }
            }
        } else {
            message = xs.msgReading
        }
    }

    LaunchedEffect(Unit) {
        award(1)
        message = xs.msgStart
    }

    val positions = LEVEL_POSITIONS[level]
    val targetDigits = (0 until positions).map { (target / pow10(it)) % 10 }
    val markedDigits = (0 until positions).map { rowDigit(it) }
    val orderLabels = (0 until positions).map { orderName(it) }

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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.common.back)
                }
                Text(
                    text = xs.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QuipuBoard(
                    targetDigits = targetDigits,
                    markedDigits = markedDigits,
                    orderLabels = orderLabels,
                    done = levelDone
                )

                Spacer(Modifier.height(8.dp))

                YupanaBoard(red = red, onToggle = ::toggle)

                Spacer(Modifier.height(8.dp))

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { startLevel(level) },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Text(xs.newNumber, fontWeight = FontWeight.Bold)
                    }
                    FilledTonalButton(
                        onClick = {
                            val next = if (level >= LEVEL_POSITIONS.size - 1) 0 else level + 1
                            startLevel(next)
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Text(xs.nextLevel, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(16.dp))
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
                    expanded = showSourcesMenu,
                    onDismissRequest = { showSourcesMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(s.common.originalText) },
                        trailingIcon = {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                        },
                        onClick = {
                            showSourcesMenu = false
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", ORIGINAL_TEXT_URL))
                            Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuipuBoard(
    targetDigits: List<Int>,
    markedDigits: List<Int>,
    orderLabels: List<String>,
    done: Boolean,
    modifier: Modifier = Modifier
) {
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    Canvas(
        modifier = modifier
            .fillMaxWidth(0.78f)
            .aspectRatio(1.1f)
    ) {
        val w = size.width
        val h = size.height
        val mainY = h * 0.07f
        val cx = w * 0.62f
        val cordTop = mainY + h * 0.02f
        val cordBottom = h * 0.96f
        val positions = targetDigits.size

        val cordColor = Color(0xFF8B5E3C)
        val knotDefault = Color(0xFF8B5E3C)
        val knotMarked = Color(0xFFE67E22)
        val knotMatch = Color(0xFF27AE60)
        val knotOver = Color(0xFFC0392B)

        drawLine(
            color = cordColor,
            start = Offset(w * 0.08f, mainY),
            end = Offset(w * 0.92f, mainY),
            strokeWidth = h * 0.02f
        )
        drawLine(
            color = cordColor,
            start = Offset(cx, cordTop),
            end = Offset(cx, cordBottom),
            strokeWidth = h * 0.008f
        )

        val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = labelColor
            textSize = h * 0.035f
            textAlign = Paint.Align.RIGHT
        }

        val bandHeight = (cordBottom - cordTop) / positions
        for (p in positions - 1 downTo 0) {
            val bandFromTop = positions - 1 - p
            val bandTop = cordTop + bandFromTop * bandHeight
            val digit = targetDigits[p]
            val marked = if (done) digit else markedDigits.getOrElse(p) { 0 }
            val gap = bandHeight / (digit + 1)
            val radius = minOf(gap * 0.36f, w * 0.02f, h * 0.02f)

            for (k in 0 until digit) {
                val cy = bandTop + gap * (k + 1)
                val color = when {
                    done || (marked > 0 && marked == digit) -> knotMatch
                    marked > digit -> knotOver
                    k < marked -> knotMarked
                    else -> knotDefault
                }
                drawCircle(color = color, radius = radius, center = Offset(cx, cy))
                drawCircle(
                    color = Color.Black.copy(alpha = 0.2f),
                    radius = radius,
                    center = Offset(cx, cy),
                    style = Stroke(width = h * 0.002f)
                )
            }

            drawContext.canvas.nativeCanvas.drawText(
                orderLabels.getOrElse(p) { "" },
                cx - radius * 3f - w * 0.01f,
                bandTop + bandHeight / 2f,
                labelPaint
            )
        }
    }
}

@Composable
private fun YupanaBoard(
    red: List<List<Boolean>>,
    onToggle: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .aspectRatio(860f / 480f)
            .pointerInput(red) {
                detectTapGestures { offset ->
                    val margin = 3f / 860f * size.width
                    val usableWidth = size.width - 2f * margin
                    val colWidth = usableWidth / 4f
                    val rowHeight = (size.height - 6f / 480f * size.height) / ROWS
                    val startX = margin
                    val startY = 3f / 480f * size.height

                    if (offset.x in startX..(startX + 4f * colWidth) &&
                        offset.y in startY..(startY + ROWS * rowHeight)
                    ) {
                        val col = ((offset.x - startX) / colWidth).toInt().coerceIn(0, COLS - 1)
                        val displayRow = ((offset.y - startY) / rowHeight).toInt().coerceIn(0, ROWS - 1)
                        val stateRow = ROWS - 1 - displayRow
                        onToggle(stateRow, col)
                    }
                }
            }
    ) {
        drawYupanaBackground(size)
        drawYupanaFrame(size)

        val margin = 3f / 860f * size.width
        val usableWidth = size.width - 2f * margin
        val colWidth = usableWidth / 4f
        val rowHeight = (size.height - 6f / 480f * size.height) / ROWS
        val startX = margin
        val startY = 3f / 480f * size.height

        for (displayRow in 0 until ROWS) {
            val stateRow = ROWS - 1 - displayRow
            val markedCols = (0 until COLS)
                .filter { red[stateRow][it] }
                .map { it + 1 }
                .toSet()
            drawYupanaRow(
                cellOriginX = startX,
                cellOriginY = startY + displayRow * rowHeight,
                cellWidth = colWidth,
                cellHeight = rowHeight,
                canvasSize = size,
                leftMarkers = markedCols
            )
        }
    }
}
