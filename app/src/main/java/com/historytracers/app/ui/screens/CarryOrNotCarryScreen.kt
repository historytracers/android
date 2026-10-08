// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.carryOrNotCarryScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val CARRY_MAX_LEVEL = 3

private data class CarryNumbers(val first: Int, val addend: Int)

private fun carryPow10(exp: Int): Int {
    var result = 1
    repeat(exp) { result *= 10 }
    return result
}

private fun carryDigitAt(value: Int, order: Int): Int = (value / carryPow10(order)) % 10

// Builds two whole numbers of the order being studied (level 0 = units .. 3 = thousands).
private fun generateCarryNumbers(level: Int): CarryNumbers {
    val digits = level + 1
    var first = 0
    var addend = 0
    for (i in 0 until digits) {
        val isLeading = i == digits - 1
        val minDigit = if (isLeading && digits > 1) 1 else 0
        first += Random.nextInt(minDigit, 10) * carryPow10(i)
        addend += Random.nextInt(minDigit, 10) * carryPow10(i)
    }
    return CarryNumbers(first, addend)
}

@Composable
fun CarryOrNotCarryScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToRoadToSomewhere: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = carryOrNotCarryScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var level by remember { mutableIntStateOf(0) }
    var column by remember { mutableIntStateOf(0) }
    var incoming by remember { mutableIntStateOf(0) }
    var numbers by remember { mutableStateOf(generateCarryNumbers(0)) }
    var carries by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var results by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var answered by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf("") }
    var feedbackPositive by remember { mutableStateOf(false) }
    var lastCarry by remember { mutableIntStateOf(0) }
    var showSquares by remember { mutableStateOf(false) }
    var allLevelsDone by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    // The group's last screen records the day's streak.
    LaunchedEffect(Unit) {
        preferences.recordLessonCompletion()
    }

    // Values of the column currently being studied.
    val firstDigit = carryDigitAt(numbers.first, column)
    val addendDigit = carryDigitAt(numbers.addend, column)
    val base = incoming + firstDigit
    val comp = 10 - base
    val columnSum = base + addendDigit
    val carriesOne = columnSum >= 10

    fun orderName(index: Int): String = when (index) {
        0 -> xs.orderUnits
        1 -> xs.orderTens
        2 -> xs.orderHundreds
        3 -> xs.orderThousands
        else -> xs.orderTenThousands
    }

    fun resetColumnUi() {
        answered = false
        feedback = ""
        feedbackPositive = false
        showSquares = false
    }

    fun startLevel(newLevel: Int) {
        numbers = generateCarryNumbers(newLevel)
        level = newLevel
        column = 0
        incoming = 0
        carries = emptyMap()
        results = emptyMap()
        lastCarry = 0
        allLevelsDone = false
        resetColumnUi()
    }

    fun answer(choseCarry: Boolean) {
        if (answered) return
        val correct = choseCarry == carriesOne
        val template = when {
            correct && carriesOne -> xs.msgCarryCorrect
            correct && !carriesOne -> xs.msgNotCorrect
            !correct && carriesOne -> xs.msgNotWrong
            else -> xs.msgCarryWrong
        }
        feedback = template.format(base, addendDigit, comp, columnSum, orderName(column))
        feedbackPositive = correct
        carries = carries + (column to if (carriesOne) 1 else 0)
        results = results + (column to (columnSum % 10))
        lastCarry = if (carriesOne) 1 else 0
        answered = true
        showSquares = true
    }

    fun advance() {
        if (!answered) return
        if (allLevelsDone) {
            startLevel(0)
            return
        }
        if (column < level) {
            column += 1
            incoming = lastCarry
            resetColumnUi()
        } else if (column == level && lastCarry == 1) {
            // The last column carried: verify that the new order does not carry again.
            column = level + 1
            incoming = 1
            resetColumnUi()
        } else if (level >= CARRY_MAX_LEVEL) {
            allLevelsDone = true
            scope.launch { preferences.markRoadToSomewhereSectionCompleted("carry_or_not_carry") }
            onScoreChanged(currentScore + 3)
        } else {
            startLevel(level + 1)
        }
    }

    val nextLabel = when {
        column < level || (column == level && lastCarry == 1) -> xs.nextColumn
        level >= CARRY_MAX_LEVEL -> xs.playAgain
        else -> xs.nextLevel
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
                    text = xs.levelTemplate.format(level + 1),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = xs.questionTemplate.format(orderName(column)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                if (incoming > 0) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = xs.incomingNote.format(firstDigit, base),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                CarryAdditionBoard(
                    level = level,
                    column = column,
                    carries = carries,
                    results = results,
                    first = numbers.first,
                    addend = numbers.addend
                )

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { answer(true) },
                        enabled = !answered,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(xs.carry)
                    }
                    Button(
                        onClick = { answer(false) },
                        enabled = !answered,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(xs.notCarry)
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (feedback.isNotEmpty()) {
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (feedbackPositive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                if (showSquares) {
                    Spacer(Modifier.height(16.dp))
                    ComplementSquaresRow(
                        label = xs.complementToTen,
                        base = base,
                        comp = comp,
                        caption = xs.squaresCaption.format(base, comp)
                    )
                }

                if (answered) {
                    Spacer(Modifier.height(16.dp))
                    FilledTonalButton(
                        onClick = { advance() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = nextLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                if (allLevelsDone) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = xs.allLevelsDone,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    FilledTonalButton(
                        onClick = onNavigateToRoadToSomewhere,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = hts.aRoadToSomewhere,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp, start = 8.dp)
        ) {
            val uriHandler = LocalUriHandler.current
            val sourceUrl = "https://www.historytracers.org/index.html?page=class_content&arg=5539096c-dd7d-4f9c-a137-f0465725b9f4"

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
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", sourceUrl))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        uriHandler.openUri(sourceUrl)
                    }
                )
            }
        }
    }
}

@Composable
private fun CarryAdditionBoard(
    level: Int,
    column: Int,
    carries: Map<Int, Int>,
    results: Map<Int, Int>,
    first: Int,
    addend: Int,
    modifier: Modifier = Modifier
) {
    val digits = level + 1
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CarryBoardCell(text = "", active = false, isOperator = true)
            for (order in digits downTo 0) {
                val carryText = if (order >= 1 && carries[order - 1] == 1) "1" else ""
                CarryBoardCell(text = carryText, active = order == column)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            CarryBoardCell(text = "", active = false, isOperator = true)
            for (order in digits downTo 0) {
                CarryBoardCell(
                    text = if (order < digits) carryDigitAt(first, order).toString() else "",
                    active = order == column
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            CarryBoardCell(text = "+", active = false, isOperator = true)
            for (order in digits downTo 0) {
                CarryBoardCell(
                    text = if (order < digits) carryDigitAt(addend, order).toString() else "",
                    active = order == column
                )
            }
        }
        Box(
            modifier = Modifier
                .width(22.dp + 40.dp * (digits + 1))
                .height(2.dp)
                .background(MaterialTheme.colorScheme.onSurface)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            CarryBoardCell(text = "", active = false, isOperator = true)
            for (order in digits downTo 0) {
                CarryBoardCell(text = results[order]?.toString() ?: "", active = order == column)
            }
        }
    }
}

@Composable
private fun CarryBoardCell(
    text: String,
    active: Boolean,
    isOperator: Boolean = false
) {
    Box(
        modifier = Modifier
            .width(if (isOperator) 22.dp else 40.dp)
            .height(34.dp)
            .background(
                color = if (active) Color(0xFFFFF0A8) else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ComplementSquaresRow(
    label: String,
    base: Int,
    comp: Int,
    caption: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            repeat(base) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(Color(0xFF8FB8D8), RoundedCornerShape(3.dp))
                )
            }
            repeat(comp) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(Color(0xFFF2D06B), RoundedCornerShape(3.dp))
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = caption,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
