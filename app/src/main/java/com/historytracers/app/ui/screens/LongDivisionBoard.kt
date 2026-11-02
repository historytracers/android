// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

internal enum class DivisionFormat { Below, Top }

/**
 * One digit of the classic long division algorithm.
 *
 * [partialValue] is the number being divided at this digit, [quotientDigit] is the digit of the
 * quotient that belongs to it, [product] is the value actually subtracted (already placed at its
 * place value, e.g. 800 instead of 8 when dividing the hundreds) and [remainder] is what is left
 * after the subtraction.
 */
internal data class DivisionStep(
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
internal fun scaledProduct(rawProduct: Int, working: Int, divisor: Int): Int {
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

internal fun computeDivisionSteps(dividend: Int, divisor: Int): List<DivisionStep> {
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

internal data class DivisionExercise(
    val dividend: Int,
    val divisor: Int,
    val steps: List<DivisionStep>,
    val firstPlayedStep: Int
)

internal fun randomDivisionExercise(): DivisionExercise {
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
internal fun placeRight(text: String, endColumn: Int): String {
    val padding = endColumn + 1 - text.length
    return if (padding <= 0) text else " ".repeat(padding) + text
}

internal val BOARD_TEXT_STYLE = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 17.sp,
    lineHeight = 23.sp,
    fontWeight = FontWeight.Medium
)

internal val TIMES_TABLE_TEXT_STYLE = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontSize = 13.sp,
    lineHeight = 16.sp,
    fontWeight = FontWeight.Medium
)

internal data class BoardLine(
    val text: String,
    val underline: Boolean = false,
    val bar: IntRange? = null
)

/** Width in pixels of one character of the division board. */
@Composable
internal fun rememberBoardCharWidth(): Float {
    val measurer = rememberTextMeasurer()
    return measurer.measure(text = AnnotatedString("0000000000"), style = BOARD_TEXT_STYLE)
        .size.width / 10f
}

/**
 * Draws the long division. [quotientContent] replaces the written quotient by custom content (the
 * typed boxes of the "writing the quotient" exercise); when it is null the quotient is written as
 * text.
 */
@Composable
internal fun DivisionBoard(
    dividend: Int,
    divisor: Int,
    committed: List<DivisionStep>,
    previewProduct: Int?,
    quotientText: String,
    totalSteps: Int,
    format: DivisionFormat,
    quotientContent: (@Composable () -> Unit)? = null
) {
    val dividendDigits = dividend.toString().length
    // The quotient is padded to its final width so each digit already appears above the place
    // value it belongs to while the division is still being played.
    val displayedQuotient = quotientText.padEnd(totalSteps).ifEmpty { " " }
    val rightColumnWidth = maxOf(
        divisor.toString().length,
        if (quotientContent != null) totalSteps else displayedQuotient.length
    )
    val lineColor = Color(0xFF1A1A1A)

    if (format == DivisionFormat.Top) {
        // The whole division area is reserved from the start, so the reader can see where every
        // product and every remainder will appear instead of watching the table grow.
        val width = 2 + dividendDigits
        val rightEdge = width - 1
        val lines = mutableListOf<BoardLine>()
        if (quotientContent == null) {
            // The quotient sits above a bar that separates the result from the values below it.
            lines += BoardLine(
                text = placeRight(displayedQuotient, rightEdge).padEnd(width),
                bar = 1..rightEdge
            )
        }
        lines += BoardLine(("$divisor|$dividend").padEnd(width))
        committed.forEach { step ->
            lines += BoardLine(placeRight("-" + step.product, rightEdge).padEnd(width), underline = true)
            lines += BoardLine(placeRight(step.remainder.toString(), rightEdge).padEnd(width))
        }
        if (previewProduct != null) {
            lines += BoardLine(placeRight("-$previewProduct", rightEdge).padEnd(width), underline = true)
        }
        val drawnStepLines = committed.size * 2 + if (previewProduct != null) 1 else 0
        repeat((2 * totalSteps - drawnStepLines).coerceAtLeast(0)) {
            lines += BoardLine("\u00A0".repeat(width))
        }
        if (quotientContent != null) {
            Column {
                QuotientRow(widthChars = width, content = quotientContent)
                MonospaceBoard(lines = lines)
            }
        } else {
            MonospaceBoard(lines = lines)
        }
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
            if (quotientContent != null) {
                Box(
                    modifier = Modifier.height(
                        with(LocalDensity.current) { BOARD_TEXT_STYLE.lineHeight.toDp() }
                    )
                ) {
                    quotientContent()
                }
            } else {
                Text(
                    text = displayedQuotient,
                    style = BOARD_TEXT_STYLE
                )
            }
        }
    }
}

/** Row that holds the typed quotient boxes, with the bar that separates it from the division. */
@Composable
private fun QuotientRow(
    widthChars: Int,
    content: @Composable () -> Unit
) {
    val charWidth = rememberBoardCharWidth()
    val density = LocalDensity.current
    val rowHeight = with(density) { BOARD_TEXT_STYLE.lineHeight.toDp() }
    val boardWidth = with(density) { (charWidth * widthChars).toDp() }
    val strokeWidth = with(density) { 1.5.dp.toPx() }
    val strokeOffset = with(density) { 3.5.dp.toPx() }

    Box(
        modifier = Modifier
            .width(boardWidth)
            .height(rowHeight)
            .drawBehind {
                drawLine(
                    color = Color(0xFF1A1A1A),
                    start = Offset(charWidth, size.height - strokeOffset),
                    end = Offset(size.width, size.height - strokeOffset),
                    strokeWidth = strokeWidth
                )
            },
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            content()
        }
    }
}

/**
 * Draws the monospace division lines and underlines the rows that represent a subtraction, the way
 * the web version does with a bottom border under each product. Every line is its own row with a
 * fixed height, so the reserved area always matches the number of rows of the division.
 */
@Composable
internal fun MonospaceBoard(lines: List<BoardLine>) {
    val charWidth = rememberBoardCharWidth()
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

/**
 * Times table of the divisor, shown in two columns so the reader can check the products without
 * scrolling. It is the mobile counterpart of the multiplication table that the web version writes
 * above the division.
 */
@Composable
internal fun TimesTable(
    titleFormat: String,
    entryFormat: String,
    divisor: Int
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 0.dp,
        color = Color.White,
        contentColor = Color(0xFF1A1A1A),
        border = BorderStroke(1.dp, Color(0xFFD6D6D6))
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
            Text(
                text = titleFormat.format(divisor),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F)
            )
            Spacer(Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                TimesTableColumn(entryFormat = entryFormat, divisor = divisor, values = (1..5).toList())
                TimesTableColumn(entryFormat = entryFormat, divisor = divisor, values = (6..10).toList())
            }
        }
    }
}

@Composable
private fun TimesTableColumn(
    entryFormat: String,
    divisor: Int,
    values: List<Int>
) {
    Column {
        values.forEach { multiplier ->
            Text(
                text = entryFormat.format(divisor, multiplier, divisor * multiplier),
                style = TIMES_TABLE_TEXT_STYLE
            )
        }
    }
}
