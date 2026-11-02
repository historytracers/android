// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.divisionAndRemainderScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val SECTION_ID = "division_and_remainder"
private const val POINTS_PER_DIVISION = 2

private val BOARD_TEXT_STYLE = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 20.sp,
    lineHeight = 30.sp,
    fontWeight = FontWeight.Medium
)

private enum class DivisionFormat { Below, Top }

/**
 * One digit of the classic long division algorithm.
 *
 * [partialValue] is the number being divided at this digit, [quotientDigit] is the digit of the
 * quotient that belongs to it, [product] is [quotientDigit] times the divisor and [remainder] is
 * what is left after the subtraction. [digitIndex] is the position of the last digit of
 * [partialValue] inside the dividend (0 is the leftmost digit).
 */
private data class DivisionStep(
    val digitIndex: Int,
    val partialValue: Int,
    val quotientDigit: Int,
    val product: Int,
    val remainder: Int
)

private fun computeDivisionSteps(dividend: Int, divisor: Int): List<DivisionStep> {
    if (divisor <= 0) return emptyList()
    val steps = mutableListOf<DivisionStep>()
    var current = 0
    dividend.toString().forEachIndexed { index, character ->
        current = current * 10 + (character - '0')
        val quotientDigit = current / divisor
        val product = quotientDigit * divisor
        steps += DivisionStep(
            digitIndex = index,
            partialValue = current,
            quotientDigit = quotientDigit,
            product = product,
            remainder = current - product
        )
        current -= product
    }
    return steps
}

private data class DivisionExercise(
    val dividend: Int,
    val divisor: Int,
    val steps: List<DivisionStep>,
    val firstPlayedStep: Int
)

private fun randomDivisionExercise(): DivisionExercise {
    val dividend = Random.nextInt(10, 1000)
    val divisor = Random.nextInt(1, 10)
    val steps = computeDivisionSteps(dividend, divisor)
    // Leading steps whose quotient digit is zero are not played: they would only add zeros in
    // front of the quotient, so the exercise starts at the first meaningful digit.
    val firstPlayed = steps.indexOfFirst { it.quotientDigit > 0 }
    return DivisionExercise(
        dividend = dividend,
        divisor = divisor,
        steps = steps,
        firstPlayedStep = if (firstPlayed < 0) 0 else firstPlayed
    )
}

/** Places [text] so that its last character sits at [endColumn], padding with spaces on the left. */
private fun placeRight(text: String, endColumn: Int): String {
    val padding = endColumn + 1 - text.length
    return if (padding <= 0) text else " ".repeat(padding) + text
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DivisionAndRemainderScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = divisionAndRemainderScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    val firstExercise = remember { randomDivisionExercise() }
    var dividend by remember { mutableIntStateOf(firstExercise.dividend) }
    var divisor by remember { mutableIntStateOf(firstExercise.divisor) }
    var steps by remember { mutableStateOf(firstExercise.steps) }
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
    val previewProduct = if (!finished && guessTouched && workingValue > 0) {
        divisor * workingValue
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = xs.formatLabel,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

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

            Spacer(Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                    DivisionBoard(
                        dividend = dividend,
                        divisor = divisor,
                        committed = committed,
                        previewProduct = previewProduct,
                        previewDigitIndex = currentStep?.digitIndex ?: 0,
                        quotientText = quotientText,
                        format = format
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (currentStep != null && !finished) {
                Text(
                    text = xs.promptPrefix,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = xs.promptDivision.format(currentStep.partialValue, divisor),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilledIconButton(
                        onClick = { changeGuess(1) },
                        modifier = Modifier.size(64.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = xs.increase,
                            modifier = Modifier.size(40.dp)
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
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                        )
                    }

                    FilledIconButton(
                        onClick = { changeGuess(-1) },
                        modifier = Modifier.size(64.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = xs.decrease,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
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

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DivisionBoard(
    dividend: Int,
    divisor: Int,
    committed: List<DivisionStep>,
    previewProduct: Int?,
    previewDigitIndex: Int,
    quotientText: String,
    format: DivisionFormat
) {
    val dividendDigits = dividend.toString().length
    val displayedQuotient = quotientText.ifEmpty { " " }
    val rightColumnWidth = maxOf(divisor.toString().length, displayedQuotient.length)

    if (format == DivisionFormat.Top) {
        val lastColumn = 2 + dividendDigits - 1
        val lines = mutableListOf<String>()
        lines += placeRight(displayedQuotient, lastColumn)
        lines += "  " + dividend.toString()
        committed.forEach { step ->
            lines += placeRight("-" + step.product, 2 + step.digitIndex)
            lines += placeRight(step.remainder.toString(), 2 + step.digitIndex)
        }
        if (previewProduct != null) {
            lines += placeRight("-$previewProduct", 2 + previewDigitIndex)
        }
        Text(
            text = lines.joinToString("\n"),
            style = BOARD_TEXT_STYLE
        )
        return
    }

    // "Result below" writes the quotient under the divisor, next to the subtraction steps.
    val leftWidth = dividendDigits + 1
    val lines = mutableListOf<String>()
    lines += placeRight(dividend.toString(), dividendDigits).padEnd(leftWidth)
    committed.forEach { step ->
        lines += placeRight("-" + step.product, 1 + step.digitIndex).padEnd(leftWidth)
        lines += placeRight(step.remainder.toString(), 1 + step.digitIndex).padEnd(leftWidth)
    }
    if (previewProduct != null) {
        lines += placeRight("-$previewProduct", 1 + previewDigitIndex).padEnd(leftWidth)
    }

    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Text(
            text = lines.joinToString("\n"),
            style = BOARD_TEXT_STYLE
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 10.dp)
                .width(2.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = divisor.toString(),
                style = BOARD_TEXT_STYLE
            )
            Box(
                modifier = Modifier
                    .width((rightColumnWidth * 13).dp)
                    .padding(vertical = 2.dp)
                    .height(2.dp)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(
                text = displayedQuotient,
                style = BOARD_TEXT_STYLE
            )
        }
    }
}
