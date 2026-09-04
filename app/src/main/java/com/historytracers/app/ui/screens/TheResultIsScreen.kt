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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.TheResultIsScreenStrings
import com.historytracers.app.ui.features.theResultIsScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlin.random.Random

private const val ORIGINAL_TEXT_URL = "https://www.historytracers.org/index.html?page=class_content&arg=71a07afa-964d-4e43-93c1-b2e0ed330acb"
private const val SECTION_ID = "the_result_is"

private val CardBg = Color(0xFFFDF6E3)
private val NumCellBg = Color(0xFFFFF8E1)
private val AnswerActiveBg = Color(0xFFFFF9C4)
private val AnswerSolvedBg = Color(0xFFC8E6C9)
private val CellBorder = Color(0xFFD7CCC8)
private val GreenText = Color(0xFF2E7D32)
private val RedButton = Color(0xFFC62828)

private data class ResultSet(val multiple: Int, val base: Int) {
    val results: List<Int> = listOf(
        multiple * base,
        multiple * (base + 1),
        multiple * (base + 2)
    )
}

private fun newResultSet(): ResultSet =
    ResultSet(Random.nextInt(1, 10), Random.nextInt(1, 8))

@Composable
private fun EquationCell(
    text: String,
    modifier: Modifier = Modifier,
    bg: Color = NumCellBg,
    textColor: Color = Color.Unspecified,
    bold: Boolean = false,
    borderColor: Color = CellBorder,
    borderWidth: Dp = 1.dp
) {
    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(6.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 2.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun EquationRow(
    lead: String,
    op: String,
    second: String,
    answer: String,
    isSolved: Boolean,
    showControls: Boolean,
    xs: TheResultIsScreenStrings,
    onUp: () -> Unit,
    onDown: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(3.2f).fillMaxHeight()) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                EquationCell(text = lead, modifier = Modifier.weight(1f))
                EquationCell(
                    text = op,
                    modifier = Modifier.weight(0.8f),
                    textColor = GreenText,
                    bold = true
                )
                EquationCell(text = second, modifier = Modifier.weight(1f))
                EquationCell(
                    text = "=",
                    modifier = Modifier.weight(0.8f),
                    textColor = GreenText,
                    bold = true
                )
                val answerBg = when {
                    isSolved -> AnswerSolvedBg
                    showControls -> AnswerActiveBg
                    else -> NumCellBg
                }
                val answerColor = if (isSolved) GreenText else Color.Unspecified
                val borderColor = if (showControls) GreenText else CellBorder
                val borderWidth = if (showControls) 2.dp else 1.dp
                EquationCell(
                    text = answer,
                    modifier = Modifier.weight(1.2f),
                    bg = answerBg,
                    textColor = answerColor,
                    bold = isSolved,
                    borderColor = borderColor,
                    borderWidth = borderWidth
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
                .padding(start = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            if (showControls) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    FilledIconButton(
                        onClick = onUp,
                        modifier = Modifier.size(38.dp),
                        shape = RoundedCornerShape(19.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = GreenText,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Filled.KeyboardArrowUp, contentDescription = xs.increase)
                    }
                    Spacer(Modifier.height(4.dp))
                    FilledIconButton(
                        onClick = onDown,
                        modifier = Modifier.size(38.dp),
                        shape = RoundedCornerShape(19.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = RedButton,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = xs.decrease)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculationCard(
    set: ResultSet,
    currentRow: Int,
    currentValue: Int,
    solvedRows: List<Boolean>,
    xs: TheResultIsScreenStrings,
    onUp: () -> Unit,
    onDown: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBg,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
            for (i in 0 until 3) {
                val lead = when {
                    i == 0 -> set.multiple.toString()
                    solvedRows[i - 1] -> set.results[i - 1].toString()
                    else -> ""
                }
                val op = if (i == 0) "\u00D7" else "+"
                val second = if (i == 0) set.base.toString() else set.multiple.toString()
                val isSolved = solvedRows[i]
                val isActive = i == currentRow
                val answer = when {
                    isSolved -> set.results[i].toString()
                    isActive -> currentValue.toString()
                    else -> ""
                }
                EquationRow(
                    lead = lead,
                    op = op,
                    second = second,
                    answer = answer,
                    isSolved = isSolved,
                    showControls = isActive && !isSolved,
                    xs = xs,
                    onUp = onUp,
                    onDown = onDown
                )
                if (i < 2) Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TimesTableCard(
    set: ResultSet,
    xs: TheResultIsScreenStrings,
    modifier: Modifier = Modifier
) {
    val m = set.multiple
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBg,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = xs.tableTitle.format(m),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            for (r in 0 until 5) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TimesTableCell(m, r + 1, xs, modifier = Modifier.weight(1f))
                    TimesTableCell(m, r + 6, xs, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TimesTableCell(
    multiple: Int,
    factor: Int,
    xs: TheResultIsScreenStrings,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(30.dp)
            .background(NumCellBg, RoundedCornerShape(6.dp))
            .border(1.dp, CellBorder, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = xs.tableLine.format(multiple, factor, multiple * factor),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun TheResultIsScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = theResultIsScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }

    var set by remember { mutableStateOf(newResultSet()) }
    var solvedRows by remember { mutableStateOf(listOf(false, false, false)) }
    var currentRow by remember { mutableIntStateOf(0) }
    var currentValue by remember { mutableIntStateOf(0) }
    var roundComplete by remember { mutableStateOf(false) }
    var completionHandled by remember { mutableStateOf(false) }
    var arrivalAwarded by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    fun resetRound(reuse: Boolean) {
        if (!reuse) set = newResultSet()
        solvedRows = listOf(false, false, false)
        currentRow = 0
        currentValue = 0
        roundComplete = false
    }

    fun completeRow() {
        val solvedIndex = currentRow
        solvedRows = solvedRows.toMutableList().also { it[solvedIndex] = true }
        currentRow++
        if (currentRow >= 3) {
            currentRow = 2
            currentValue = 0
            roundComplete = true
        } else {
            currentValue = set.results[solvedIndex]
        }
    }

    fun adjustValue(delta: Int) {
        if (roundComplete) return
        if (currentValue == set.results[currentRow]) {
            completeRow()
            return
        }
        var next = currentValue + delta * set.multiple
        if (next < 0) next = 0
        currentValue = next
        if (currentValue == set.results[currentRow]) completeRow()
    }

    val stepMessage = when {
        roundComplete -> xs.congratsMessage.format(set.multiple, set.multiple)
        currentRow == 0 -> xs.multiplyStep.format(set.multiple, set.base, set.multiple)
        else -> xs.additionStep.format(set.multiple, set.results[currentRow - 1], set.multiple, set.multiple)
    }

    LaunchedEffect(roundComplete) {
        if (roundComplete && !completionHandled) {
            completionHandled = true
            preferences.markRunningAndGrowingSectionCompleted(SECTION_ID)
        }
    }

    LaunchedEffect(Unit) {
        if (!arrivalAwarded) {
            arrivalAwarded = true
            onScoreChanged(currentScore + 2)
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.common.back)
                    }
                    Text(
                        text = xs.title,
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

                Text(
                    text = xs.instruction,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )

                Spacer(Modifier.height(12.dp))

                if (!roundComplete) {
                    CalculationCard(
                        set = set,
                        currentRow = currentRow,
                        currentValue = currentValue,
                        solvedRows = solvedRows,
                        xs = xs,
                        onUp = { adjustValue(1) },
                        onDown = { adjustValue(-1) }
                    )
                } else {
                    TimesTableCard(set = set, xs = xs)
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = if (roundComplete) xs.medal else stepMessage,
                    fontSize = if (roundComplete) 40.sp else MaterialTheme.typography.bodyMedium.fontSize,
                    textAlign = TextAlign.Center
                )

                if (roundComplete) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stepMessage,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = GreenText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    FilledTonalButton(
                        onClick = { resetRound(reuse = true) },
                        enabled = roundComplete,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = xs.repeat,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    FilledTonalButton(
                        onClick = { resetRound(reuse = false) },
                        enabled = roundComplete,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Text(
                            text = s.common.newExercise,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
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
        }
    }
}
