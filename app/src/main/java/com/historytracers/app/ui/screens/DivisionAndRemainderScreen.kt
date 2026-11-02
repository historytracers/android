// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.DivisionAndRemainderScreenStrings
import com.historytracers.app.ui.features.divisionAndRemainderScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val SECTION_ID = "division_and_remainder"
private const val POINTS_PER_DIVISION = 2

private val BOARD_TEXT_STYLE = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 17.sp,
    lineHeight = 23.sp,
    fontWeight = FontWeight.Medium
)

private val TIMES_TABLE_TEXT_STYLE = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 13.sp,
    lineHeight = 16.sp,
    fontWeight = FontWeight.Medium
)

private enum class DivisionFormat { Below, Top }

/**
 * One digit of the classic long division algorithm.
 *
 * [partialValue] is the number being divided at this digit, [quotientDigit] is the digit of the
 * quotient that belongs to it, [product] is the value actually subtracted (already placed at its
 * place value, e.g. 800 instead of 8 when dividing the hundreds) and [remainder] is what is left
 * after the subtraction.
 */
private data class DivisionStep(
    val quotientDigit: Int,
    val partialValue: Int,
    val product: Int,
    val remainder: Int
)

/**
 * Places [rawProduct] at the place value of the subtraction, as the web version does: while the
 * product is much smaller than the value being divided, it is multiplied by ten, and it is divided
 * by ten when it would not fit. This is what makes the app subtract 800 from 869 instead of 8.
 */
private fun scaledProduct(rawProduct: Int, working: Int, divisor: Int): Int {
    var product = rawProduct
    if (working - product > divisor) {
        val targetLength = working.toString().length
        var length = product.toString().length
        while (length < targetLength) {
            product *= 10
            length += 1
        }
    }
    if (working - product < 0) {
        product /= 10
    }
    return product
}

private fun computeDivisionSteps(dividend: Int, divisor: Int): List<DivisionStep> {
    if (divisor <= 0) return emptyList()
    val steps = mutableListOf<DivisionStep>()
    var prefix = 0
    var working = dividend
    dividend.toString().forEach { character ->
        prefix = prefix * 10 + (character - '0')
        val quotientDigit = prefix / divisor
        val product = scaledProduct(quotientDigit * divisor, working, divisor)
        working -= product
        steps += DivisionStep(
            quotientDigit = quotientDigit,
            partialValue = prefix,
            product = product,
            remainder = working
        )
        prefix -= quotientDigit * divisor
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

                TimesTable(divisor = divisor)
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

private data class BoardLine(
    val text: String,
    val underline: Boolean = false,
    val bar: IntRange? = null
)

/**
 * Times table of the divisor, shown in two columns so the reader can check the products without
 * scrolling. It is the mobile counterpart of the multiplication table that the web version writes
 * above the division.
 */
@Composable
private fun TimesTable(divisor: Int) {
    val xs = divisionAndRemainderScreenStringsForLanguage(LocalAppLanguage.current)
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 0.dp,
        color = Color.White,
        contentColor = Color(0xFF1A1A1A),
        border = BorderStroke(1.dp, Color(0xFFD6D6D6))
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
            Text(
                text = xs.timesTableTitle.format(divisor),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F)
            )
            Spacer(Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                TimesTableColumn(xs = xs, divisor = divisor, values = (1..5).toList())
                TimesTableColumn(xs = xs, divisor = divisor, values = (6..10).toList())
            }
        }
    }
}

@Composable
private fun TimesTableColumn(
    xs: DivisionAndRemainderScreenStrings,
    divisor: Int,
    values: List<Int>
) {
    Column {
        values.forEach { multiplier ->
            Text(
                text = xs.timesTableEntry.format(divisor, multiplier, divisor * multiplier),
                style = TIMES_TABLE_TEXT_STYLE
            )
        }
    }
}

@Composable
private fun DivisionBoard(
    dividend: Int,
    divisor: Int,
    committed: List<DivisionStep>,
    previewProduct: Int?,
    quotientText: String,
    totalSteps: Int,
    format: DivisionFormat
) {
    val dividendDigits = dividend.toString().length
    // The quotient is padded to its final width so each digit already appears above the place
    // value it belongs to while the division is still being played.
    val displayedQuotient = quotientText.padEnd(totalSteps).ifEmpty { " " }
    val rightColumnWidth = maxOf(divisor.toString().length, displayedQuotient.length)
    val lineColor = Color(0xFF1A1A1A)

    if (format == DivisionFormat.Top) {
        // The whole division area is reserved from the start, so the reader can see where every
        // product and every remainder will appear instead of watching the table grow.
        val width = 2 + dividendDigits
        val rightEdge = width - 1
        val lines = mutableListOf<BoardLine>()
        // The quotient sits above a bar that separates the result from the values below it.
        lines += BoardLine(
            text = placeRight(displayedQuotient, rightEdge).padEnd(width),
            bar = 1..rightEdge
        )
        lines += BoardLine(("$divisor|${dividend}").padEnd(width))
        committed.forEach { step ->
            lines += BoardLine(placeRight("-" + step.product, rightEdge).padEnd(width), underline = true)
            lines += BoardLine(placeRight(step.remainder.toString(), rightEdge).padEnd(width))
        }
        if (previewProduct != null) {
            lines += BoardLine(placeRight("-$previewProduct", rightEdge).padEnd(width), underline = true)
        }
        val drawnLines = 2 + committed.size * 2 + if (previewProduct != null) 1 else 0
        repeat((2 + totalSteps * 2 - drawnLines).coerceAtLeast(0)) {
            lines += BoardLine("\u00A0".repeat(width))
        }
        MonospaceBoard(lines = lines)
        return
    }

    // "Result below" writes the quotient under the divisor, next to the subtraction steps.
    val leftWidth = dividendDigits + 1
    val rightEdge = dividendDigits
    val lines = mutableListOf<BoardLine>()
    lines += BoardLine(placeRight(dividend.toString(), rightEdge).padEnd(leftWidth))
    committed.forEach { step ->
        lines += BoardLine(placeRight("-" + step.product, rightEdge).padEnd(leftWidth), underline = true)
        lines += BoardLine(placeRight(step.remainder.toString(), rightEdge).padEnd(leftWidth))
    }
    if (previewProduct != null) {
        lines += BoardLine(placeRight("-$previewProduct", rightEdge).padEnd(leftWidth), underline = true)
    }
    val drawnLines = 1 + committed.size * 2 + if (previewProduct != null) 1 else 0
    repeat((1 + totalSteps * 2 - drawnLines).coerceAtLeast(0)) {
        lines += BoardLine("\u00A0".repeat(leftWidth))
    }

    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        MonospaceBoard(lines = lines)

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 10.dp)
                .width(2.dp)
                .background(lineColor)
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
                    .background(lineColor)
            )
            Text(
                text = displayedQuotient,
                style = BOARD_TEXT_STYLE
            )
        }
    }
}

/**
 * Draws the monospace division lines and underlines the rows that represent a subtraction, the way
 * the web version does with a bottom border under each product. Every line is its own row with a
 * fixed height, so the reserved area always matches the number of rows of the division.
 */
@Composable
private fun MonospaceBoard(lines: List<BoardLine>) {
    val measurer = rememberTextMeasurer()
    val charWidth = measurer.measure(text = AnnotatedString("0000000000"), style = BOARD_TEXT_STYLE)
        .size.width / 10f
    val strokeWidth = with(LocalDensity.current) { 1.5.dp.toPx() }
    val strokeOffset = with(LocalDensity.current) { 3.5.dp.toPx() }
    val underlineColor = Color(0xFF1A1A1A)

    Column {
        lines.forEach { line ->
            val columns = line.bar ?: if (line.underline) {
                val first = line.text.indexOfFirst { it != ' ' && it != '\u00A0' }
                val last = line.text.indexOfLast { it != ' ' && it != '\u00A0' }
                if (first < 0 || last < 0) null else first..last
            } else {
                null
            }
            Text(
                text = line.text,
                style = BOARD_TEXT_STYLE,
                modifier = Modifier.drawBehind {
                    if (columns != null) {
                        val y = size.height - strokeOffset
                        drawLine(
                            color = underlineColor,
                            start = Offset(columns.first * charWidth, y),
                            end = Offset((columns.last + 1) * charWidth, y),
                            strokeWidth = strokeWidth
                        )
                    }
                }
            )
        }
    }
}
