// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

private val HEADING_REGEX = Regex("^(#{1,6})\\s+(.+)$")
private val CODE_FENCE_REGEX = Regex("^\\s*```")

// A line that starts with a single "*" (closing "*" optional) and contains no
// other asterisks is an image caption: rendered centered, italic, and smaller
// than the normal body text.
private fun isCaptionLine(line: String): Boolean {
    if (!line.startsWith("*") || line.startsWith("**")) return false
    val rest = line.drop(1)
    val content = if (rest.endsWith("*") && !rest.endsWith("**")) rest.dropLast(1) else rest
    return '*' !in content
}

private fun captionText(line: String): String {
    val rest = line.drop(1)
    return if (rest.endsWith("*") && !rest.endsWith("**")) rest.dropLast(1) else rest
}

private fun isCodeFence(line: String): Boolean = CODE_FENCE_REGEX.containsMatchIn(line)

@Composable
fun MarkdownText(text: String, modifier: Modifier = Modifier) {
    val paragraphs = text.split(Regex("\\n\\s*\\n"))
    Column(modifier = modifier) {
        paragraphs.forEachIndexed { paragraphIndex, paragraph ->
            if (paragraphIndex > 0) Spacer(Modifier.height(12.dp))
            val paragraphLines = paragraph.lines()
            val inCodeBlock = paragraphLines.any { isCodeFence(it) }
            if (inCodeBlock) {
                val codeLines = paragraphLines.filterNot { isCodeFence(it) }
                if (codeLines.isNotEmpty()) {
                    Text(
                        text = codeLines.joinToString("\n"),
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                paragraphLines.forEach { line ->
                    val trimmed = line.trim()
                    if (trimmed.isEmpty()) return@forEach
                    val heading = HEADING_REGEX.find(trimmed)
                    if (heading != null) {
                        val level = heading.groupValues[1].length
                        Text(
                            text = parseMarkdownInline(heading.groupValues[2]),
                            style = headingTextStyle(level)
                        )
                    } else if (isCaptionLine(trimmed)) {
                        Text(
                            text = parseMarkdownInline(captionText(trimmed)),
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = parseMarkdownInline(trimmed),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun headingTextStyle(level: Int) = when (level) {
    1, 2 -> MaterialTheme.typography.headlineSmall
    3, 4 -> MaterialTheme.typography.titleLarge
    else -> MaterialTheme.typography.titleMedium
}.copy(fontWeight = FontWeight.Bold)

private fun parseMarkdownInline(text: String): AnnotatedString = buildAnnotatedString {
    appendMarkdownInline(text)
}

private fun AnnotatedString.Builder.appendMarkdownInline(text: String) {
    var i = 0
    while (i < text.length) {
        when {
            text.startsWith("===", i) -> {
                val end = text.indexOf("===", i + 3)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        appendMarkdownInline(text.substring(i + 3, end))
                    }
                    i = end + 3
                    continue
                }
            }
            text.startsWith("**", i) -> {
                val end = text.indexOf("**", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        appendMarkdownInline(text.substring(i + 2, end))
                    }
                    i = end + 2
                    continue
                }
            }
            text[i] == '*' -> {
                val end = text.indexOf('*', i + 1)
                if (end != -1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        appendMarkdownInline(text.substring(i + 1, end))
                    }
                    i = end + 1
                    continue
                }
            }
        }
        append(text[i])
        i++
    }
}
