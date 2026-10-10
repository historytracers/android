// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.historytracers.app.ui.features.advancingAndComplementingScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage

private const val AC_WEB_CLASS_URL = "https://www.historytracers.org/index.html?page=class_content&arg=cabc9843-35ef-4e4f-92fd-17eda864b55e"
private const val AC_FIRST_TERM = 6
private const val AC_COMPLEMENT = 4
private val AC_GREEN = Color(0xFF4CAF50)
private val AC_TEXT_GREEN = Color(0xFF2E7D32)

// MARK: - Screens

@Composable
fun AdvancingAndComplementingIntroScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val xs = advancingAndComplementingScreenStringsForLanguage(LocalAppLanguage.current)
    AcFrame(
        title = xs.introTitle,
        onNavigateBack = onNavigateBack,
        onNavigateNext = onNavigateNext
    ) {
        AcBody(xs.introBody)
    }
}

@Composable
fun AdvancingAndComplementingComplementScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val xs = advancingAndComplementingScreenStringsForLanguage(LocalAppLanguage.current)
    AcFrame(
        title = xs.complementTitle,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    ) {
        AcBody(xs.complementBody)
    }
}

@Composable
fun AdvancingAndComplementingOurselvesScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val xs = advancingAndComplementingScreenStringsForLanguage(LocalAppLanguage.current)
    AcFrame(
        title = xs.ourselvesTitle,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    ) {
        AcBody(xs.ourselvesBody)
        Spacer(Modifier.height(8.dp))
        CalculationOne(caption = xs.calculationCaption)
    }
}

@Composable
fun AdvancingAndComplementingLimitScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val xs = advancingAndComplementingScreenStringsForLanguage(LocalAppLanguage.current)
    AcFrame(
        title = xs.limitTitle,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    ) {
        AcBody(xs.limitBody)
        Spacer(Modifier.height(12.dp))
        FigureOne()
        Spacer(Modifier.height(8.dp))
        Text(
            text = xs.figureCaption,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Composable
fun AdvancingAndComplementingExercisingScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = advancingAndComplementingScreenStringsForLanguage(LocalAppLanguage.current)
    var addend by remember { mutableIntStateOf(0) }

    val feedback = when {
        addend < AC_COMPLEMENT -> xs.msgBelow
        addend == AC_COMPLEMENT -> xs.msgAt
        else -> xs.msgAbove
    }
    val feedbackColor = when {
        addend < AC_COMPLEMENT -> MaterialTheme.colorScheme.onSurfaceVariant
        addend == AC_COMPLEMENT -> AC_TEXT_GREEN
        else -> Color(0xFFE65100)
    }

    AcFrame(
        title = xs.exercisingTitle,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    ) {
        AcBody(xs.exercisingBody)
        AcBody(xs.exercisingInstruction, emphasis = true)

        Spacer(Modifier.height(12.dp))

        AcVerticalAddition(first = AC_FIRST_TERM, addend = addend)

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledIconButton(
                onClick = { if (addend > 0) addend-- },
                enabled = addend > 0,
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = AC_GREEN,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = s.common.previous,
                    modifier = Modifier.size(28.dp)
                )
            }
            FilledIconButton(
                onClick = { if (addend < 9) addend++ },
                enabled = addend < 9,
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = AC_GREEN,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = s.common.next,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = feedback,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = feedbackColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(16.dp))

        AcNumberAxis(value = addend)
    }
}

@Composable
fun AdvancingAndComplementingThinkingScreen(
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = advancingAndComplementingScreenStringsForLanguage(LocalAppLanguage.current)
    var selected by remember { mutableStateOf<String?>(null) }

    AcFrame(
        title = xs.thinkingTitle,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateNext = onNavigateNext
    ) {
        AcQuestion(xs.thinkingQuestion)

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { selected = "yes" },
                enabled = selected == null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AC_GREEN,
                    contentColor = Color.White
                )
            ) {
                Text(s.common.yes)
            }
            Button(
                onClick = { selected = "no" },
                enabled = selected == null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AC_GREEN,
                    contentColor = Color.White
                )
            ) {
                Text(s.common.no)
            }
        }

        if (selected != null) {
            val correct = selected == "no"
            Spacer(Modifier.height(16.dp))
            Text(
                text = if (correct) "\uD83C\uDF89 ${s.common.correct} \uD83C\uDF89" else xs.thinkingWrong,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (correct) AC_TEXT_GREEN else MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            if (correct) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = xs.thinkingCorrect,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@Composable
fun AdvancingAndComplementingConclusionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigatePrev: () -> Unit = {},
    onNavigateToRoadToSomewhere: () -> Unit = {}
) {
    val xs = advancingAndComplementingScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    var completionHandled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        preferences.markRoadToSomewhereSectionCompleted("advancing_and_complementing")
        preferences.recordLessonCompletion()
        if (!completionHandled) {
            completionHandled = true
            onScoreChanged(currentScore + 2)
        }
    }

    AcFrame(
        title = xs.conclusionTitle,
        onNavigateBack = onNavigateBack,
        onNavigatePrev = onNavigatePrev,
        onNavigateToRoadToSomewhere = onNavigateToRoadToSomewhere
    ) {
        AcBody(xs.conclusionBody)
    }
}

// MARK: - Shared frame and pieces

@Composable
private fun AcFrame(
    title: String,
    onNavigateBack: () -> Unit,
    onNavigatePrev: (() -> Unit)? = null,
    onNavigateNext: (() -> Unit)? = null,
    onNavigateToRoadToSomewhere: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val s = LocalUiStrings.current
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.common.back)
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))

                content()

                Spacer(Modifier.height(20.dp))

                if (onNavigatePrev != null || onNavigateNext != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onNavigatePrev != null) {
                            FilledTonalButton(
                                onClick = onNavigatePrev,
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = AC_GREEN,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(s.common.previous, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (onNavigateNext != null) {
                            FilledTonalButton(
                                onClick = onNavigateNext,
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = AC_GREEN,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(s.common.next, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(6.dp))
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                if (onNavigateToRoadToSomewhere != null) {
                    Spacer(Modifier.height(16.dp))
                    FilledTonalButton(
                        onClick = onNavigateToRoadToSomewhere,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = AC_GREEN,
                            contentColor = Color.White
                        )
                    ) {
                        Text(hts.aRoadToSomewhere, fontWeight = FontWeight.Bold)
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
            val uriHandler = LocalUriHandler.current

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
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", AC_WEB_CLASS_URL))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        uriHandler.openUri(AC_WEB_CLASS_URL)
                    }
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.AcBody(text: String, emphasis: Boolean = false) {
    Text(
        text = text,
        style = if (emphasis) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyLarge,
        fontWeight = if (emphasis) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
private fun ColumnScope.AcQuestion(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
    )
}

@Composable
private fun CalculationOne(caption: String) {
    val rows = listOf("5 + 5 = 10", "6 + 5 = 11", "7 + 5 = 12", "8 + 5 = 13", "9 + 5 = 14")
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            rows.forEach { row ->
                Text(
                    text = row,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FigureOne() {
    val blue = Color(0xFFADD8E6)
    val yellow = Color(0xFFFFF3B0)
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(AC_FIRST_TERM) { AcSquare(blue) }
        repeat(AC_COMPLEMENT) { AcSquare(yellow) }
    }
}

@Composable
private fun AcSquare(color: Color) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .background(color, RoundedCornerShape(4.dp))
            .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
    )
}

@Composable
private fun AcVerticalAddition(first: Int, addend: Int) {
    val sum = first + addend
    val carry = sum / 10
    val cellWidth = 46.dp
    val cellHeight = 38.dp

    @Composable
    fun cell(content: String, width: androidx.compose.ui.unit.Dp = cellWidth) {
        Box(
            modifier = Modifier
                .width(width)
                .height(cellHeight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Carry row: the 1 appears above the tens column.
        Row(verticalAlignment = Alignment.CenterVertically) {
            cell("", width = 30.dp)
            Box(
                modifier = Modifier
                    .width(cellWidth)
                    .height(cellHeight),
                contentAlignment = Alignment.Center
            ) {
                if (carry > 0) {
                    Text(
                        text = "1",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AC_TEXT_GREEN
                    )
                }
            }
            cell("")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            cell("", width = 30.dp)
            cell("")
            cell("$first")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            cell("+", width = 30.dp)
            cell("")
            cell("$addend")
        }
        Box(
            modifier = Modifier
                .width(cellWidth * 3)
                .height(2.dp)
                .background(MaterialTheme.colorScheme.onSurface)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            cell("", width = 30.dp)
            Box(
                modifier = Modifier
                    .width(cellWidth)
                    .height(cellHeight),
                contentAlignment = Alignment.Center
            ) {
                if (carry > 0) {
                    Text(
                        text = "$carry",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AC_TEXT_GREEN
                    )
                }
            }
            cell("${sum % 10}")
        }
    }
}

@Composable
private fun AcNumberAxis(value: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        for (v in 0..9) {
            val color = when {
                v < AC_COMPLEMENT -> Color(0xFF9E9E9E)
                v == AC_COMPLEMENT -> AC_TEXT_GREEN
                else -> Color(0xFFE65100)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (v == value) "\u25BC" else " ",
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$v",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
