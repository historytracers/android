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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.RepresentYouScreenStrings
import com.historytracers.app.ui.features.representYouScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlin.random.Random
import kotlinx.coroutines.launch

private const val MAX_LEVEL = 4
private const val PAIR_COUNT = 5
private const val SECTION_ID = "i_represent_you"
private const val ORIGINAL_TEXT_URL =
    "https://www.historytracers.org/index.html?page=class_content&arg=07666919-8290-4aee-a9fd-af148508f754"

private val romanValues = listOf(
    1000 to "M", 900 to "CM", 500 to "D", 400 to "CD",
    100 to "C", 90 to "XC", 50 to "L", 40 to "XL",
    10 to "X", 9 to "IX", 5 to "V", 4 to "IV", 1 to "I"
)

private fun toRomanBasic(value: Int): String {
    var remaining = value
    val builder = StringBuilder()
    for ((number, symbol) in romanValues) {
        while (remaining >= number) {
            builder.append(symbol)
            remaining -= number
        }
    }
    return builder.toString()
}

// Thousands are written as overlined I..IX (I = 1000, II = 2000, ... IX = 9000),
// never as a long run of M. Returns (thousands part, remainder part).
private fun toRomanParts(value: Int): Pair<String, String> =
    toRomanBasic(value / 1000) to toRomanBasic(value % 1000)

// Subtractive pairs (a smaller symbol before a bigger one) are described by naming
// the two symbols, e.g. "I before V", never as a subtraction. Each entry maps the
// pair to (big, small).
private val representSubtractiveParts = mapOf(
    "IV" to ("V" to "I"), "IX" to ("X" to "I"), "XL" to ("L" to "X"),
    "XC" to ("C" to "X"), "CD" to ("D" to "C"), "CM" to ("M" to "C")
)

// Break a value into the same pieces the game draws: overlined thousands, additive
// symbol groups (e.g. II) and subtractive pairs (e.g. IV).
private sealed interface RepresentPart {
    data class Thousands(val symbols: String) : RepresentPart
    data class Subtractive(val symbols: String, val small: String, val big: String) : RepresentPart
    data class Additive(val symbols: String, val count: Int, val value: Int) : RepresentPart
}

private fun decomposeRepresentParts(value: Int): List<RepresentPart> {
    val parts = mutableListOf<RepresentPart>()
    var remaining = value
    if (remaining >= 1000) {
        val thousands = remaining / 1000
        parts.add(RepresentPart.Thousands(toRomanBasic(thousands)))
        remaining %= 1000
    }
    for ((number, symbol) in romanValues) {
        if (number >= 1000 || remaining <= 0) continue
        if (remaining < number) continue
        val sub = representSubtractiveParts[symbol]
        if (sub != null) {
            parts.add(RepresentPart.Subtractive(symbol, small = sub.second, big = sub.first))
            remaining -= number
        } else {
            val count = remaining / number
            parts.add(RepresentPart.Additive(symbol.repeat(count), count, number))
            remaining -= count * number
        }
    }
    return parts
}

private sealed interface FeedbackSeg {
    data class Plain(val text: String) : FeedbackSeg
    data class Overlined(val text: String) : FeedbackSeg
}

private fun representPlaceName(xs: RepresentYouScreenStrings, symbolValue: Int): String = when {
    symbolValue >= 100 -> xs.placeHundreds
    symbolValue >= 10 -> xs.placeTens
    else -> xs.placeUnits
}

private fun representGroupPhrase(
    xs: RepresentYouScreenStrings,
    count: Int,
    symbolValue: Int,
    withMore: Boolean
): String {
    val template = if (withMore) xs.groupMore else xs.group
    val countWord = when (count) {
        2 -> xs.countTwo
        3 -> xs.countThree
        else -> ""
    }
    return template.replace("%COUNT%", countWord).replace("%PLACE%", representPlaceName(xs, symbolValue))
}

// Describe a value in words, e.g. "V and two more units II" or "I before V".
private fun explainRepresent(value: Int, xs: RepresentYouScreenStrings): List<FeedbackSeg> {
    val fragments = mutableListOf<List<FeedbackSeg>>()
    decomposeRepresentParts(value).forEachIndexed { index, part ->
        when (part) {
            is RepresentPart.Thousands -> fragments.add(
                listOf(
                    FeedbackSeg.Overlined(part.symbols),
                    FeedbackSeg.Plain(" " + xs.withBar)
                )
            )
            is RepresentPart.Subtractive -> fragments.add(
                listOf(FeedbackSeg.Plain(part.small + " " + xs.before + " " + part.big))
            )
            is RepresentPart.Additive -> if (part.count == 1) {
                fragments.add(listOf(FeedbackSeg.Plain(part.symbols)))
            } else {
                fragments.add(
                    listOf(
                        FeedbackSeg.Plain(
                            representGroupPhrase(xs, part.count, part.value, index > 0) + " " + part.symbols
                        )
                    )
                )
            }
        }
    }
    val out = mutableListOf<FeedbackSeg>()
    fragments.forEachIndexed { index, fragment ->
        if (index > 0) out.add(FeedbackSeg.Plain(" " + xs.and + " "))
        out.addAll(fragment)
    }
    return out
}

private fun romanFeedbackSegs(value: Int): List<FeedbackSeg> {
    val (thousands, remainder) = toRomanParts(value)
    return if (thousands.isEmpty()) {
        listOf(FeedbackSeg.Plain(remainder))
    } else {
        listOf(FeedbackSeg.Overlined(thousands), FeedbackSeg.Plain(remainder))
    }
}

private val feedbackTokenRegex = Regex("%[A-Z]+%")

private fun substituteFeedback(
    template: String,
    subs: Map<String, List<FeedbackSeg>>
): List<FeedbackSeg> {
    val out = mutableListOf<FeedbackSeg>()
    var pos = 0
    for (match in feedbackTokenRegex.findAll(template)) {
        if (match.range.first > pos) {
            out.add(FeedbackSeg.Plain(template.substring(pos, match.range.first)))
        }
        out.addAll(subs[match.value] ?: listOf(FeedbackSeg.Plain(match.value)))
        pos = match.range.last + 1
    }
    if (pos < template.length) {
        out.add(FeedbackSeg.Plain(template.substring(pos)))
    }
    return out
}

// Explain the first selection in words: a Hindu-Arabic number is shown as the
// Roman numerals that build it, a Roman numeral is shown as its number.
private fun wrongPairFeedback(
    leftId: Int,
    rightId: Int,
    firstSide: String?,
    xs: RepresentYouScreenStrings
): List<FeedbackSeg> =
    if (firstSide == "Left") {
        substituteFeedback(
            xs.wrongPairLeft,
            mapOf(
                "%ROMAN%" to romanFeedbackSegs(leftId),
                "%VALUE%" to listOf(FeedbackSeg.Plain(leftId.toString()))
            )
        )
    } else {
        substituteFeedback(
            xs.wrongPair,
            mapOf(
                "%VALUE%" to listOf(FeedbackSeg.Plain(rightId.toString())),
                "%EXPLANATION%" to explainRepresent(rightId, xs),
                "%ROMAN%" to romanFeedbackSegs(rightId)
            )
        )
    }

private fun windowsForLevel(level: Int): List<IntRange> = when (level) {
    2 -> listOf(10..30, 20..40, 40..60, 60..80, 80..99)
    3 -> listOf(100..300, 200..400, 400..600, 600..800, 800..999)
    4 -> listOf(1000..3000, 2000..4000, 4000..6000, 6000..8000, 8000..9999)
    else -> emptyList()
}

private fun randomIn(window: IntRange): Int =
    window.first + Random.nextInt(window.last - window.first + 1)

private fun generateValues(level: Int): List<Int> {
    if (level <= 1) return (1..10).shuffled().take(PAIR_COUNT)
    val chosen = LinkedHashSet<Int>()
    for (window in windowsForLevel(level)) {
        var candidate = randomIn(window)
        var attempts = 0
        while (candidate in chosen && attempts < 50) {
            candidate = randomIn(window)
            attempts++
        }
        if (candidate in chosen) {
            candidate = window.first { it !in chosen }
        }
        chosen.add(candidate)
    }
    return chosen.toList()
}

private data class RepresentLeftItem(val thousands: String, val remainder: String, val id: Int)
private data class RepresentRightItem(val text: String, val id: Int)

@Composable
private fun OverlinedText(
    text: String,
    style: TextStyle,
    lineColor: Color,
    contentColor: Color = Color.Unspecified
) {
    val density = LocalDensity.current
    var textWidth by remember { mutableStateOf(0f) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(with(density) { textWidth.toDp() })
                .height(2.dp)
                .background(lineColor)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = text,
            style = style,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            onTextLayout = { textWidth = it.size.width.toFloat() }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RepresentFeedback(segs: List<FeedbackSeg>) {
    val style = MaterialTheme.typography.bodyMedium
    val color = MaterialTheme.colorScheme.error
    FlowRow(
        modifier = Modifier.padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center
    ) {
        segs.forEach { seg ->
            when (seg) {
                is FeedbackSeg.Plain -> Text(
                    text = seg.text,
                    style = style,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                is FeedbackSeg.Overlined -> OverlinedText(
                    text = seg.text,
                    style = style,
                    lineColor = color,
                    contentColor = color
                )
            }
        }
    }
}

@Composable
private fun RepresentItemButton(
    text: String,
    overlineThousands: String,
    selected: Boolean,
    matched: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = when {
        matched -> ButtonYellowDark
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val content = when {
        selected -> Color.White
        matched -> OnButtonYellow
        else -> MaterialTheme.colorScheme.onSurface
    }
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        enabled = !matched,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = container,
            disabledContentColor = content
        )
    ) {
        val style = MaterialTheme.typography.titleMedium
        if (overlineThousands.isEmpty()) {
            Text(
                text = text,
                style = style,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        } else {
            Row(verticalAlignment = Alignment.Bottom) {
                OverlinedText(
                    text = overlineThousands,
                    style = style,
                    lineColor = content
                )
                if (text.isNotEmpty()) {
                    Text(
                        text = text,
                        style = style,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepresentYouScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = representYouScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    val initialValues = remember { generateValues(1) }
    var level by remember { mutableIntStateOf(1) }
    var leftItems by remember {
        mutableStateOf(
            initialValues.shuffled().map { value ->
                val (thousands, remainder) = toRomanParts(value)
                RepresentLeftItem(thousands, remainder, value)
            }
        )
    }
    var rightItems by remember {
        mutableStateOf(initialValues.shuffled().map { RepresentRightItem(it.toString(), it) })
    }
    var matchedIds by remember { mutableStateOf(emptySet<Int>()) }
    var selectedLeftId by remember { mutableStateOf<Int?>(null) }
    var selectedRightId by remember { mutableStateOf<Int?>(null) }
    var firstSide by remember { mutableStateOf<String?>(null) }
    var feedbackSegs by remember { mutableStateOf<List<FeedbackSeg>?>(null) }
    var levelCompleted by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    fun startLevel(newLevel: Int) {
        val values = generateValues(newLevel)
        leftItems = values.shuffled().map { value ->
            val (thousands, remainder) = toRomanParts(value)
            RepresentLeftItem(thousands, remainder, value)
        }
        rightItems = values.shuffled().map { RepresentRightItem(it.toString(), it) }
        matchedIds = emptySet()
        selectedLeftId = null
        selectedRightId = null
        firstSide = null
        feedbackSegs = null
        levelCompleted = false
    }

    fun evaluatePair(leftId: Int, rightId: Int) {
        if (leftId == rightId) {
            val newMatched = matchedIds + leftId
            matchedIds = newMatched
            selectedLeftId = null
            selectedRightId = null
            firstSide = null
            feedbackSegs = null
            if (newMatched.size == leftItems.size) {
                levelCompleted = true
                totalAwarded += 2
                onScoreChanged(initialScore + totalAwarded)
                if (level == MAX_LEVEL) {
                    scope.launch {
                        preferences.recordLessonCompletion()
                        preferences.markAnotherWayToCountSectionCompleted(SECTION_ID)
                    }
                }
            }
        } else {
            feedbackSegs = wrongPairFeedback(leftId, rightId, firstSide, xs)
            selectedLeftId = null
            selectedRightId = null
            firstSide = null
        }
    }

    fun onLeftTap(item: RepresentLeftItem) {
        if (levelCompleted) return
        if (item.id in matchedIds) return
        feedbackSegs = null
        if (firstSide == null) {
            firstSide = "Left"
        }
        selectedLeftId = item.id
        val rightSel = selectedRightId
        if (rightSel != null) {
            evaluatePair(item.id, rightSel)
        }
    }

    fun onRightTap(item: RepresentRightItem) {
        if (levelCompleted) return
        if (item.id in matchedIds) return
        feedbackSegs = null
        if (firstSide == null) {
            firstSide = "Right"
        }
        selectedRightId = item.id
        val leftSel = selectedLeftId
        if (leftSel != null) {
            evaluatePair(leftSel, item.id)
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = xs.instruction,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "${s.common.levelPrefix}$level/$MAX_LEVEL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Text(
                        text = xs.roman,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(170.dp)
                    )
                    Text(
                        text = xs.hinduArabic,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(120.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        leftItems.forEach { item ->
                            RepresentItemButton(
                                text = item.remainder,
                                overlineThousands = item.thousands,
                                selected = selectedLeftId == item.id,
                                matched = item.id in matchedIds,
                                onClick = { onLeftTap(item) },
                                modifier = Modifier.width(170.dp)
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        rightItems.forEach { item ->
                            RepresentItemButton(
                                text = item.text,
                                overlineThousands = "",
                                selected = selectedRightId == item.id,
                                matched = item.id in matchedIds,
                                onClick = { onRightTap(item) },
                                modifier = Modifier.width(120.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (feedbackSegs != null) {
                    Spacer(Modifier.height(4.dp))
                    RepresentFeedback(segs = feedbackSegs!!)
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { startLevel(level) },
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

                    if (levelCompleted && level < MAX_LEVEL) {
                        FilledTonalButton(
                            onClick = {
                                level += 1
                                startLevel(level)
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = s.common.nextLevel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    if (levelCompleted && level == MAX_LEVEL) {
                        FilledTonalButton(
                            onClick = {
                                level = 1
                                startLevel(1)
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = xs.playAgain,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (levelCompleted) {
                    Text(
                        text = if (level == MAX_LEVEL) {
                            xs.finalCongrats
                        } else {
                            xs.levelComplete.format(level, MAX_LEVEL)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
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
