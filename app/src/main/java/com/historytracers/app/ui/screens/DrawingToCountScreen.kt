// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
import android.graphics.Path
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.buildHandPath
import com.historytracers.app.ui.components.drawHandNumbers
import com.historytracers.app.ui.components.drawOneHand
import com.historytracers.app.ui.components.drawYupanaRow
import com.historytracers.app.ui.components.getMarkersForDigit
import com.historytracers.app.ui.features.drawingToCountScreenStringsForLanguage
import com.historytracers.app.ui.features.yupanaSharedStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import com.historytracers.app.ui.theme.parseHexColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingToCountScreen(
    skinColor: String = "#A5672C",
    onNavigateBack: () -> Unit = {},
    onNavigateToHandsOnYupana: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = drawingToCountScreenStringsForLanguage(LocalAppLanguage.current)
    val ys = yupanaSharedStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    var counter by remember { mutableIntStateOf(0) }
    var nineReached by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    var showDhavitPremSubmenu by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val handColor = remember(skinColor) { parseHexColor(skinColor) }
    val handPath = remember { buildHandPath() }

    fun updateCounter(newValue: Int) {
        counter = newValue.coerceIn(0, 9)
        if (counter == 9 && !nineReached) {
            nineReached = true
            onScoreChanged(currentScore + 1)
            scope.launch { preferences.recordLessonCompletion() }
        } else if (counter == 0) {
            nineReached = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ys.handsOnYupana) },
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
                    text = ys.handsOnYupana,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = xs.drawingToCountDescription,
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
                                leftMarkers = getMarkersForDigit(counter)
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
                        text = ys.tawantsuyu,
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
                    expanded = showSourcesMenu && !showMainTextSubmenu && !showDhavitPremSubmenu,
                    onDismissRequest = { showSourcesMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Dhavit Prem") },
                        trailingIcon = {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                        },
                        onClick = { showDhavitPremSubmenu = true }
                    )
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

                DropdownMenu(
                    expanded = showSourcesMenu && showDhavitPremSubmenu,
                    onDismissRequest = { showDhavitPremSubmenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(s.common.copyUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showDhavitPremSubmenu = false
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.youtube.com/watch?v=qynAx9YBO1Y"))
                            Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showDhavitPremSubmenu = false
                            uriHandler.openUri("https://www.youtube.com/watch?v=qynAx9YBO1Y")
                        }
                    )
                }
            }
        }
    }
}
