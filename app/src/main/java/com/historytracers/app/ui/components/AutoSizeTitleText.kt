// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.takeOrElse

private data class TitleFit(val fontSize: TextUnit, val singleLine: Boolean)

/**
 * Screen title for the top bar that always fits the space the bar offers.
 *
 * Instead of cutting the title with an ellipsis ("My Body, as multipli..."),
 * the text is measured against the available width and the font size is
 * reduced until the whole title fits on one line. If even the smallest size is
 * not enough (very long title, very narrow window or very large system font),
 * the title is allowed to wrap onto a second line so that it is still shown in
 * full. Normal-length titles keep the original [style] size.
 *
 * The calculation runs again whenever the text, the available width or the
 * density changes, so rotation, split-screen and system font-size changes are
 * handled automatically.
 */
@Composable
fun AutoSizeTitleText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = Color.Unspecified,
    minFontSize: TextUnit = 8.sp
) {
    // rememberTextMeasurer() is recreated when the density (display size or the
    // user's font scale) changes, so keeping it in the keys below makes the
    // title recalculate on rotation, split screen and font-size changes.
    val measurer = rememberTextMeasurer()
    val maxFontSize = style.fontSize.takeOrElse { 22.sp }
    val effectiveMinFontSize = minOf(minFontSize.value, maxFontSize.value).sp

    BoxWithConstraints(modifier = modifier) {
        val availableWidth = constraints.maxWidth
        val fit = remember(
            text,
            availableWidth,
            maxFontSize,
            effectiveMinFontSize,
            style,
            measurer
        ) {
            if (availableWidth <= 0 || availableWidth == Constraints.Infinity) {
                TitleFit(maxFontSize, singleLine = true)
            } else {
                fun widthAt(size: TextUnit): Int = measurer.measure(
                    text = AnnotatedString(text),
                    style = style.copy(fontSize = size),
                    maxLines = 1,
                    softWrap = false,
                    constraints = Constraints()
                ).size.width

                var candidate = maxFontSize
                val fullWidth = widthAt(candidate)
                if (fullWidth > availableWidth) {
                    val ratio = availableWidth.toFloat() / fullWidth.toFloat()
                    candidate = (maxFontSize.value * ratio).coerceAtLeast(effectiveMinFontSize.value).sp
                    while (candidate.value > effectiveMinFontSize.value && widthAt(candidate) > availableWidth) {
                        candidate = (candidate.value - 0.5f).sp
                    }
                }
                TitleFit(
                    fontSize = candidate,
                    singleLine = widthAt(candidate) <= availableWidth
                )
            }
        }

        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            style = style.copy(fontSize = fit.fontSize),
            color = color,
            maxLines = if (fit.singleLine) 1 else 2,
            softWrap = !fit.singleLine,
            // The title normally fits; the ellipsis is only a last resort for
            // titles that not even two lines at the minimum size can show.
            overflow = TextOverflow.Ellipsis
        )
    }
}
