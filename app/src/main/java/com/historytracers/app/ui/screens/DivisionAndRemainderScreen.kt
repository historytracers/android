// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.divisionAndRemainderScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

private const val SECTION_ID = "division_and_remainder"
private const val POINTS_PER_DIVISION = 2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DivisionAndRemainderScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToLongDivision: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = divisionAndRemainderScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    val firstExercise = remember { randomDivisionExercise() }
    var dividend by remember { mutableIntStateOf(firstExercise.dividend) }
    var divisor by remember { mutableIntStateOf(firstExercise.divisor) }
    var steps by remember { mutableStateOf(firstExercise.steps) }
    var firstPlayedStep by remember { mutableIntStateOf(firstExercise.firstPlayedStep) }
    var stepIndex by remember { mutableIntStateOf(firstExercise.firstPlayedStep) }
    var committed by remember { mutableStateOf(listOf<DivisionStep>()) }
    var workingValue by remember { mutableIntStateOf(0) }
    var guessTouched by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    var format by remember { mutableStateOf(DivisionFormat.Below) }

    fun newDivision() {
        val exercise = randomDivisionExercise()
        dividend = exercise.dividend
        divisor = exercise.divisor
        steps = exercise.steps
        firstPlayedStep = exercise.firstPlayedStep
        stepIndex = exercise.firstPlayedStep
        committed = emptyList()
        workingValue = 0
        guessTouched = false
        finished = false
    }

    val currentStep = steps.getOrNull(stepIndex)
    val fullQuotient = if (divisor > 0) dividend / divisor else 0
    val fullRemainder = if (divisor > 0) dividend % divisor else 0
    val quotientText = if (finished) {
        fullQuotient.toString()
    } else {
        committed.joinToString("") { it.quotientDigit.toString() } +
            if (guessTouched) workingValue.toString() else ""
    }
    val currentWorking = committed.lastOrNull()?.remainder ?: dividend
    val previewProduct = if (!finished && guessTouched && workingValue > 0) {
        scaledProduct(divisor * workingValue, currentWorking, divisor)
    } else {
        null
    }

    fun completeDivision() {
        finished = true
        totalAwarded += POINTS_PER_DIVISION
        onScoreChanged(initialScore + totalAwarded)
        scope.launch {
            preferences.markLongDivisionSectionCompleted(SECTION_ID)
            preferences.recordLessonCompletion()
        }
    }

    fun changeGuess(delta: Int) {
        if (finished) return
        val step = steps.getOrNull(stepIndex) ?: return
        workingValue = (workingValue + delta).coerceIn(0, 9)
        guessTouched = true
        if (workingValue != step.quotientDigit) return

        committed = committed + step
        stepIndex += 1
        workingValue = 0
        guessTouched = false
        if (stepIndex >= steps.size) {
            completeDivision()
        }
    }

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = xs.formatLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(2.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = format == DivisionFormat.Below,
                    onClick = { format = DivisionFormat.Below },
                    label = { Text(xs.resultBelow) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ButtonYellow,
                        selectedLabelColor = OnButtonYellow
                    )
                )
                FilterChip(
                    selected = format == DivisionFormat.Top,
                    onClick = { format = DivisionFormat.Top },
                    label = { Text(xs.resultTop) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ButtonYellow,
                        selectedLabelColor = OnButtonYellow
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 0.dp,
                color = Color.White,
                contentColor = Color(0xFF1A1A1A),
                border = BorderStroke(1.dp, Color(0xFFD6D6D6))
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    DivisionBoard(
                        dividend = dividend,
                        divisor = divisor,
                        committed = committed,
                        previewProduct = previewProduct,
                        quotientText = quotientText,
                        totalSteps = steps.size - firstPlayedStep,
                        format = format
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (currentStep != null && !finished) {
                Text(
                    text = "${xs.promptPrefix} ${xs.promptDivision.format(currentStep.partialValue, divisor)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilledIconButton(
                        onClick = { changeGuess(1) },
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = xs.increase,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp
                    ) {
                        Text(
                            text = workingValue.toString(),
                            style = MaterialTheme.typography.displaySmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                        )
                    }

                    FilledIconButton(
                        onClick = { changeGuess(-1) },
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = xs.decrease,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                TimesTable(
                    titleFormat = xs.timesTableTitle,
                    entryFormat = xs.timesTableEntry,
                    divisor = divisor
                )
            }

            if (finished) {
                Text(
                    text = "\uD83C\uDFC5",
                    fontSize = 48.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = xs.resultFormat.format(dividend, divisor, fullQuotient, fullRemainder),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF2E7D32)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${xs.quotientLabel}: $fullQuotient   ${xs.remainderLabel}: $fullRemainder",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = { newDivision() },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = xs.newDivision,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            FilledTonalButton(
                onClick = onNavigateToLongDivision,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = hts.longDivision,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
