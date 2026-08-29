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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.practicingAdditionRoadScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlin.random.Random

private const val ORIGINAL_TEXT_URL = "https://www.historytracers.org/index.html?page=class_content&arg=a4caa9fe-de1e-49c5-92de-50ed9c22a42d"

private data class RoadAdditionProblem(val a: Int, val b: Int) {
    val sum: Int get() = a + b
    val digitsA: List<Int> get() = listOf(a / 100, (a / 10) % 10, a % 10)
    val digitsB: List<Int> get() = listOf(b / 100, (b / 10) % 10, b % 10)
}

private fun newRoadAdditionProblem(): RoadAdditionProblem =
    RoadAdditionProblem(Random.nextInt(100, 1000), Random.nextInt(100, 1000))

private fun computeExpected(topDigit: Int, bottomDigit: Int): Pair<Int, Int> {
    val total = topDigit + bottomDigit
    return if (total >= 10) (total - 10) to 1 else total to 0
}

@Composable
private fun RowScope.NumberCell(
    text: String,
    isActive: Boolean = false,
    textColor: Color = Color.Unspecified,
    bold: Boolean = false
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .then(
                if (isActive) Modifier.background(Color(0xFFC8E6C9), RoundedCornerShape(8.dp))
                else Modifier
            )
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CarryRow(carryCells: List<Int?>) {
    Row(Modifier.fillMaxWidth()) {
        for (col in 0 until 4) {
            val value = carryCells[col]
            NumberCell(
                text = value?.toString() ?: "",
                textColor = Color(0xFFC62828),
                bold = true
            )
        }
    }
}

@Composable
private fun TopRow(digitsA: List<Int>) {
    Row(Modifier.fillMaxWidth()) {
        NumberCell("")
        for (col in 1 until 4) {
            NumberCell(digitsA[col - 1].toString(), bold = true)
        }
    }
}

@Composable
private fun BottomRow(digitsB: List<Int>) {
    Row(Modifier.fillMaxWidth()) {
        NumberCell("+", textColor = Color(0xFF2E7D32), bold = true)
        for (col in 1 until 4) {
            NumberCell(digitsB[col - 1].toString(), bold = true)
        }
    }
}

@Composable
private fun ResultRow(resultCells: List<Int?>, activeColumn: Int, finished: Boolean) {
    Row(Modifier.fillMaxWidth()) {
        for (col in 0 until 4) {
            val value = resultCells[col]
            NumberCell(
                text = value?.toString() ?: "",
                isActive = !finished && col == activeColumn,
                bold = true
            )
        }
    }
}

@Composable
private fun AdditionPracticeTable(
    problem: RoadAdditionProblem,
    carryCells: List<Int?>,
    resultCells: List<Int?>,
    activeColumn: Int,
    finished: Boolean,
    message: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val xs = practicingAdditionRoadScreenStringsForLanguage(LocalAppLanguage.current)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFDF6E3),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Box(Modifier.weight(4f).height(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = xs.sumOfNumbers,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(Modifier.weight(2f).height(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = xs.controls,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Divider(color = Color(0xFFC9A86B))

            Row(Modifier.fillMaxWidth().height(180.dp)) {
                Column(Modifier.weight(4f)) {
                    CarryRow(carryCells)
                    TopRow(problem.digitsA)
                    BottomRow(problem.digitsB)
                    Divider(
                        color = Color(0xFF333333),
                        thickness = 1.5.dp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    ResultRow(resultCells, activeColumn, finished)
                }
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FilledIconButton(
                        onClick = onIncrement,
                        enabled = !finished,
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (finished) MaterialTheme.colorScheme.surfaceVariant else Color(0xFF2E7D32),
                            contentColor = if (finished) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                        )
                    ) {
                        Icon(Icons.Filled.KeyboardArrowUp, contentDescription = xs.increase)
                    }
                    Spacer(Modifier.height(4.dp))
                    FilledIconButton(
                        onClick = onDecrement,
                        enabled = !finished,
                        modifier = Modifier.size(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (finished) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFC62828),
                            contentColor = if (finished) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                        )
                    ) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = xs.decrease)
                    }
                }
            }

            Divider(color = Color(0xFFC9A86B))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (finished) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = xs.medal, fontSize = 40.sp)
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PracticingAdditionRoadScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = practicingAdditionRoadScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }

    var problem by remember { mutableStateOf(newRoadAdditionProblem()) }
    var resultCells by remember { mutableStateOf(listOf<Int?>(null, null, null, null)) }
    var carryCells by remember { mutableStateOf(listOf<Int?>(null, null, null, null)) }
    var activeColumn by remember { mutableIntStateOf(3) }
    var workingValue by remember { mutableIntStateOf(0) }
    var expectedValue by remember { mutableIntStateOf(0) }
    var pendingCarry by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var completionHandled by remember { mutableStateOf(false) }
    var arrivalAwarded by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    fun resetState() {
        resultCells = listOf(null, null, null, null)
        carryCells = listOf(null, null, null, null)
        activeColumn = 3
        workingValue = 0
        expectedValue = 0
        pendingCarry = 0
        finished = false
        completionHandled = false
        val (expected, carry) = computeExpected(problem.digitsA[2], problem.digitsB[2])
        expectedValue = expected
        pendingCarry = carry
        resultCells = listOf(null, null, null, 0)
        message = xs.addStepIntro.format(problem.a, problem.b, problem.digitsA[2], problem.digitsB[2])
    }

    fun resetExercise() {
        problem = newRoadAdditionProblem()
        resetState()
    }

    fun repeatExercise() {
        resetState()
    }

    fun advanceColumn() {
        val prevCarry = pendingCarry
        val col = activeColumn
        activeColumn = col - 1
        if (activeColumn >= 1) {
            workingValue = 0
            resultCells = resultCells.toMutableList().also { it[activeColumn] = 0 }
        } else {
            workingValue = 0
            finished = true
        }
        if (prevCarry == 1) {
            carryCells = carryCells.toMutableList().also { it[activeColumn] = 1 }
        }
        if (!finished) {
            val digitIndex = activeColumn - 1
            val bottomWithCarry = problem.digitsB[digitIndex] + prevCarry
            val (expected, carry) = computeExpected(problem.digitsA[digitIndex], bottomWithCarry)
            expectedValue = expected
            pendingCarry = carry
            message = if (prevCarry == 1) {
                xs.addStepCarry.format(problem.digitsA[digitIndex], problem.digitsB[digitIndex], prevCarry)
            } else {
                xs.addStepContinue.format(problem.digitsA[digitIndex], problem.digitsB[digitIndex])
            }
        } else {
            if (prevCarry == 1) {
                resultCells = resultCells.toMutableList().also { it[0] = 1 }
            }
            pendingCarry = 0
            message = xs.congratsMessage.format(problem.a, problem.b, problem.sum)
        }
    }

    fun adjustValue(delta: Int) {
        if (finished) return
        if (workingValue == expectedValue) {
            advanceColumn()
            return
        }
        workingValue = (workingValue + delta).coerceIn(0, 9)
        resultCells = resultCells.toMutableList().also { it[activeColumn] = workingValue }
        if (workingValue == expectedValue) advanceColumn()
    }

    LaunchedEffect(finished) {
        if (finished && !completionHandled) {
            completionHandled = true
            preferences.markRoadToSomewhereSectionCompleted("practicing_addition")
        }
    }

    LaunchedEffect(Unit) {
        resetExercise()
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

                AdditionPracticeTable(
                    problem = problem,
                    carryCells = carryCells,
                    resultCells = resultCells,
                    activeColumn = activeColumn,
                    finished = finished,
                    message = message,
                    onIncrement = { adjustValue(1) },
                    onDecrement = { adjustValue(-1) }
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = xs.tableDescription,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    FilledTonalButton(
                        onClick = { repeatExercise() },
                        enabled = finished,
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
                        onClick = { resetExercise() },
                        enabled = finished,
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
