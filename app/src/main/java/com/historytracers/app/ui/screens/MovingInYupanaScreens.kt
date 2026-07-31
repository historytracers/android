// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Paint
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.buildHandPath
import com.historytracers.app.ui.components.drawHandNumbers
import com.historytracers.app.ui.components.drawOneHand
import com.historytracers.app.ui.components.drawYupanaRow
import com.historytracers.app.ui.components.getMarkersForDigit
import com.historytracers.app.ui.features.movingInYupanaScreenStringsForLanguage
import com.historytracers.app.ui.theme.parseHexColor

private const val ORIGINAL_TEXT_URL = "https://www.historytracers.org/index.html?page=class_content&arg=c742c649-bf12-4d3f-ba8c-52f91455fc95"
private const val DHAVIT_PREM_URL = "https://www.researchgate.net/publication/334520917_TAWA_PUKLLAY_-_LA_ARITMETICA_INCA_DE_RECONOCIMIENTO_DE_FORMAS_Y_MOVIMIENTOS_OPERABLE_EN_PARALELO_Y_QUE_NO_REQUIERE_CALCULOS_NUMERICOS_MENTALES"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IskayMovementScreen(
    skinColor: String = "#A5672C",
    onNavigateBack: () -> Unit = {},
    onNavigateToKimsa: () -> Unit = {}
) {
    val xs = movingInYupanaScreenStringsForLanguage(LocalAppLanguage.current)
    YupanaMovementContent(
        title = xs.iskayTitle,
        description = xs.iskayDescription,
        equation = xs.iskayEquation,
        methodNote = xs.iskayMethodNote,
        rows = 1,
        originalLeftMarkers = getMarkersForDigit(2),
        originalRightMarkers = getMarkersForDigit(2),
        movedLeftMarkers = getMarkersForDigit(1),
        movedRightMarkers = getMarkersForDigit(3),
        originalLeftValue = 2,
        originalRightValue = 2,
        movedLeftValue = 1,
        movedRightValue = 3,
        skinColor = skinColor,
        onNavigateBack = onNavigateBack,
        nextLabel = xs.kimsaTitle,
        onNavigateNext = onNavigateToKimsa,
        leftNumberExtraOffsets = mapOf(1 to Offset(50f, 0f))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KimsaMovementScreen(
    skinColor: String = "#A5672C",
    onNavigateBack: () -> Unit = {},
    onNavigateToIskay: () -> Unit = {},
    onNavigateToPisqa: () -> Unit = {}
) {
    val xs = movingInYupanaScreenStringsForLanguage(LocalAppLanguage.current)
    YupanaMovementContent(
        title = xs.kimsaTitle,
        description = xs.kimsaDescription,
        equation = xs.kimsaEquation,
        methodNote = xs.kimsaMethodNote,
        rows = 1,
        originalLeftMarkers = getMarkersForDigit(3),
        originalRightMarkers = getMarkersForDigit(3),
        movedLeftMarkers = getMarkersForDigit(5),
        movedRightMarkers = getMarkersForDigit(1),
        originalLeftValue = 3,
        originalRightValue = 3,
        movedLeftValue = 5,
        movedRightValue = 1,
        skinColor = skinColor,
        onNavigateBack = onNavigateBack,
        prevLabel = xs.iskayTitle,
        nextLabel = xs.pisqaTitle,
        onNavigatePrev = onNavigateToIskay,
        onNavigateNext = onNavigateToPisqa,
        leftNumberExtraOffsets = mapOf(1 to Offset(40f, 0f), 3 to Offset(-20f, 0f), 4 to Offset(-30f, 0f), 5 to Offset(-30f, 0f))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PisqaMovementScreen(
    skinColor: String = "#A5672C",
    onNavigateBack: () -> Unit = {},
    onNavigateToKimsa: () -> Unit = {},
    onNavigateToPichana: () -> Unit = {}
) {
    val xs = movingInYupanaScreenStringsForLanguage(LocalAppLanguage.current)
    YupanaMovementContent(
        title = xs.pisqaTitle,
        description = xs.pisqaDescription,
        equation = xs.pisqaEquation,
        methodNote = "",
        rows = 2,
        originalLeftMarkers = getMarkersForDigit(5),
        originalRightMarkers = getMarkersForDigit(5),
        movedLeftMarkers = getMarkersForDigit(1),
        movedRightMarkers = emptySet(),
        originalLeftValue = 5,
        originalRightValue = 5,
        movedLeftValue = 5,
        movedRightValue = 5,
        skinColor = skinColor,
        onNavigateBack = onNavigateBack,
        prevLabel = xs.kimsaTitle,
        nextLabel = xs.pichanaTitle,
        onNavigatePrev = onNavigateToKimsa,
        onNavigateNext = onNavigateToPichana,
        belowContentOffset = (-20).dp,
        handOffsetY = 20.dp,
        leftNumberExtraOffsets = mapOf(1 to Offset(40f, 0f), 3 to Offset(-15f, 0f), 4 to Offset(-20f, 0f), 5 to Offset(-15f, 0f))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PichanaMovementScreen(
    skinColor: String = "#A5672C",
    onNavigateBack: () -> Unit = {},
    onNavigateToPisqa: () -> Unit = {},
    onNavigateToKinkin: () -> Unit = {}
) {
    val xs = movingInYupanaScreenStringsForLanguage(LocalAppLanguage.current)
    var useFirstOption by remember { mutableStateOf(true) }

    val leftOriginal = getMarkersForDigit(if (useFirstOption) 1 else 2)
    val rightOriginal = getMarkersForDigit(if (useFirstOption) 2 else 3)
    val leftMoved = getMarkersForDigit(if (useFirstOption) 3 else 5)
    val originalLeftValue = if (useFirstOption) 1 else 2
    val originalRightValue = if (useFirstOption) 2 else 3
    val movedLeftValue = if (useFirstOption) 3 else 5

    YupanaMovementContent(
        title = xs.pichanaTitle,
        description = xs.pichanaDescription,
        equation = if (useFirstOption) xs.pichanaEquation12 else xs.pichanaEquation23,
        methodNote = "",
        rows = 1,
        originalLeftMarkers = leftOriginal,
        originalRightMarkers = rightOriginal,
        movedLeftMarkers = leftMoved,
        movedRightMarkers = emptySet(),
        originalLeftValue = originalLeftValue,
        originalRightValue = originalRightValue,
        movedLeftValue = movedLeftValue,
        movedRightValue = 0,
        skinColor = skinColor,
        onNavigateBack = onNavigateBack,
        prevLabel = xs.pisqaTitle,
        nextLabel = xs.kinkinTitle,
        onNavigatePrev = onNavigateToPisqa,
        onNavigateNext = onNavigateToKinkin,
        optionSelector = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = useFirstOption,
                    onClick = { useFirstOption = true },
                    label = { Text("1 + 2 = 3") }
                )
                FilterChip(
                    selected = !useFirstOption,
                    onClick = { useFirstOption = false },
                    label = { Text("2 + 3 = 5") }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KinkinMovementScreen(
    skinColor: String = "#A5672C",
    onNavigateBack: () -> Unit = {},
    onNavigateToPichana: () -> Unit = {}
) {
    val xs = movingInYupanaScreenStringsForLanguage(LocalAppLanguage.current)
    YupanaMovementContent(
        title = xs.kinkinTitle,
        description = xs.kinkinDescription,
        equation = xs.kinkinEquation,
        methodNote = "",
        rows = 1,
        originalLeftMarkers = getMarkersForDigit(1),
        originalRightMarkers = getMarkersForDigit(1),
        movedLeftMarkers = getMarkersForDigit(2),
        movedRightMarkers = emptySet(),
        originalLeftValue = 1,
        originalRightValue = 1,
        movedLeftValue = 0,
        movedRightValue = 2,
        skinColor = skinColor,
        onNavigateBack = onNavigateBack,
        prevLabel = xs.pichanaTitle,
        onNavigatePrev = onNavigateToPichana
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YupanaMovementContent(
    title: String,
    description: String,
    equation: String,
    methodNote: String,
    rows: Int,
    originalLeftMarkers: Set<Int>,
    originalRightMarkers: Set<Int>,
    movedLeftMarkers: Set<Int>,
    movedRightMarkers: Set<Int>,
    originalLeftValue: Int,
    originalRightValue: Int,
    movedLeftValue: Int,
    movedRightValue: Int,
    skinColor: String,
    onNavigateBack: () -> Unit,
    prevLabel: String? = null,
    nextLabel: String? = null,
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    optionSelector: (@Composable () -> Unit)? = null,
    leftNumberExtraOffsets: Map<Int, Offset> = emptyMap(),
    belowContentOffset: Dp = (-70).dp,
    handOffsetY: Dp = 0.dp
) {
    val s = LocalUiStrings.current
    val xs = movingInYupanaScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    var moved by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    var showDhavitPremSubmenu by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val handColor = remember(skinColor) { parseHexColor(skinColor) }
    val handPath = remember { buildHandPath() }

    LaunchedEffect(Unit) {
        preferences.markYupanaSectionCompleted("moving_in_yupana")
    }

    val leftMarkers = if (moved) movedLeftMarkers else originalLeftMarkers
    val rightMarkers = if (moved) movedRightMarkers else originalRightMarkers
    val leftValue = if (moved) movedLeftValue else originalLeftValue
    val rightValue = if (moved) movedRightValue else originalRightValue

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
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
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF2E241F),
                ) {
                    Text(
                        text = equation,
                        color = Color(0xFFF2ECD8),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                if (optionSelector != null) {
                    Spacer(Modifier.height(12.dp))
                    optionSelector()
                }

                Spacer(Modifier.height(16.dp))

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5f + (rows - 1) * 0.5f)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
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
                            if (rows == 1) {
                                drawYupanaRow(
                                    cellOriginX = startX,
                                    cellOriginY = startY,
                                    cellWidth = colW,
                                    cellHeight = rowHeight,
                                    canvasSize = size,
                                    leftMarkers = leftMarkers,
                                    rightMarkers = rightMarkers
                                )
                            } else {
                                drawYupanaRow(
                                    cellOriginX = startX,
                                    cellOriginY = startY,
                                    cellWidth = colW,
                                    cellHeight = rowHeight,
                                    canvasSize = size,
                                    leftMarkers = if (moved) movedLeftMarkers else emptySet(),
                                    rightMarkers = emptySet()
                                )
                                drawYupanaRow(
                                    cellOriginX = startX,
                                    cellOriginY = startY + rowHeight,
                                    cellWidth = colW,
                                    cellHeight = rowHeight,
                                    canvasSize = size,
                                    leftMarkers = if (moved) emptySet() else originalLeftMarkers,
                                    rightMarkers = if (moved) emptySet() else originalRightMarkers
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.26f)
                                .align(Alignment.CenterStart)
                                .offset(y = handOffsetY)
                                .padding(2.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val s = minOf(size.width / 500f, size.height / 500f) * 2.4f
                                val cx = size.width * 0.42f
                                val rowsBandTop = 3f / 480f * size.height
                                val rowsBandHeight = (size.height - 6f / 480f * size.height) / 4f * rows
                                val cy = rowsBandTop + rowsBandHeight / 2f
                                drawOneHand(cx, cy, s, isLeft = true, paint, handPath)
                                drawHandNumbers(
                                    numbers = (1..leftValue).map { it to (5 - it) },
                                    cx = cx, cy = cy, handScale = s, isLeft = true,
                                    textPaint = numberPaint, strokePaint = numberStrokePaint,
                                    extraOffsets = leftNumberExtraOffsets
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.26f)
                                .align(Alignment.CenterEnd)
                                .offset(y = handOffsetY)
                                .padding(2.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val s = minOf(size.width / 500f, size.height / 500f) * 2.4f
                                val cx = size.width * 0.58f
                                val rowsBandTop = 3f / 480f * size.height
                                val rowsBandHeight = (size.height - 6f / 480f * size.height) / 4f * rows
                                val cy = rowsBandTop + rowsBandHeight / 2f
                                drawOneHand(cx, cy, s, isLeft = false, paint, handPath)
                                drawHandNumbers(
                                    numbers = (1..rightValue).map { it to (5 - it) },
                                    cx = cx, cy = cy, handScale = s, isLeft = false,
                                    textPaint = numberPaint, strokePaint = numberStrokePaint
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.offset(y = belowContentOffset),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "${xs.leftHand}: $leftValue    ${xs.rightHand}: $rightValue",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledIconButton(
                            onClick = { moved = true },
                            enabled = !moved,
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                disabledContainerColor = Color(0xFF4CAF50).copy(alpha = 0.4f)
                            )
                        ) {
                            Icon(
                                Icons.Filled.SwapHoriz,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Color.White
                            )
                        }

                        FilledIconButton(
                            onClick = { moved = false },
                            enabled = moved,
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = Color(0xFF2980B9),
                                disabledContainerColor = Color(0xFF2980B9).copy(alpha = 0.4f)
                            )
                        ) {
                            Icon(
                                Icons.Filled.Restore,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = xs.instructions,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (methodNote.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = methodNote,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (prevLabel != null && onNavigatePrev != null) {
                            FilledTonalButton(
                                onClick = onNavigatePrev,
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
                                Text(text = prevLabel, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (nextLabel != null && onNavigateNext != null) {
                            FilledTonalButton(
                                onClick = onNavigateNext,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFF4CAF50),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(text = nextLabel, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(48.dp))
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
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", ORIGINAL_TEXT_URL))
                            Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showMainTextSubmenu = false
                            uriHandler.openUri(ORIGINAL_TEXT_URL)
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
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", DHAVIT_PREM_URL))
                            Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(s.common.goToUrl) },
                        onClick = {
                            showSourcesMenu = false
                            showDhavitPremSubmenu = false
                            uriHandler.openUri(DHAVIT_PREM_URL)
                        }
                    )
                }
            }
        }
    }
}
