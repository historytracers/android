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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import com.historytracers.app.ui.features.PracticingWithQuipusScreenStrings
import com.historytracers.app.ui.features.practicingWithQuipusScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlin.random.Random

private const val HISTORYTRACERS_ORIGIN = "https://www.historytracers.org/"
private const val QUIPU_LESSON_UUID = "7aec6487-c20c-490d-a9af-21e0b8bcc7f7"

private const val STRING_WIDTH_DP = 84
private const val ORDER_HEIGHT_DP = 120
private const val KNOT_SIZE_DP = 10
private const val KNOT_GAP_DP = 2

private val CORD_COLOR = Color(0xFFA1887F)
private val KNOT_COLOR = Color(0xFF5D4037)
private val ACTIVE_COLOR = Color(0xFF1565C0)
private val DONE_COLOR = Color(0xFF2E7D32)

private data class QuipuLevel(val strings: Int, val positions: Int, val min: Int, val max: Int)

private val QUIPU_LEVELS = listOf(
    QuipuLevel(strings = 1, positions = 1, min = 1, max = 9),
    QuipuLevel(strings = 2, positions = 2, min = 10, max = 99),
    QuipuLevel(strings = 3, positions = 3, min = 100, max = 999),
)

private data class QuipuStringState(
    val target: Int,
    val digits: List<Int>,
    val knots: List<Int>,
    val active: Int,
    val done: Boolean,
)

private fun digitsOf(value: Int, positions: Int): List<Int> {
    var v = value
    return List(positions) { v % 10.also { v /= 10 } }
}

private fun withActive(state: QuipuStringState): QuipuStringState {
    for (i in state.digits.indices) {
        if (state.knots[i] < state.digits[i]) {
            return state.copy(active = i, done = false)
        }
    }
    return state.copy(active = state.digits.size - 1, done = true)
}

private fun generateStates(level: QuipuLevel): List<QuipuStringState> =
    List(level.strings) {
        val value = Random.nextInt(level.min, level.max + 1)
        withActive(
            QuipuStringState(
                target = value,
                digits = digitsOf(value, level.positions),
                knots = List(level.positions) { 0 },
                active = 0,
                done = false,
            )
        )
    }

private fun orderName(position: Int, xs: PracticingWithQuipusScreenStrings): String = when (position) {
    0 -> xs.orderUnits
    1 -> xs.orderTens
    else -> xs.orderHundreds
}

@Composable
fun PracticingWithQuipusScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = practicingWithQuipusScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }

    var levelIndex by remember { mutableIntStateOf(0) }
    var states by remember { mutableStateOf(generateStates(QUIPU_LEVELS[0])) }
    var message by remember { mutableStateOf("") }
    var showCongrats by remember { mutableStateOf(false) }
    var finalCongratsShown by remember { mutableStateOf(false) }

    LaunchedEffect(finalCongratsShown) {
        if (finalCongratsShown) {
            preferences.markAnotherWayToCountSectionCompleted("practicing_with_quipus")
            preferences.recordLessonCompletion()
            onScoreChanged(currentScore + 2)
        }
    }

    val level = QUIPU_LEVELS[levelIndex]
    val activeIndex = states.indexOfFirst { !it.done }
    val canAdd = activeIndex >= 0
    val canRemove = states.any { st -> st.knots.any { it > 0 } }

    fun newNumber(levelToUse: QuipuLevel) {
        states = generateStates(levelToUse)
        message = ""
        showCongrats = false
        finalCongratsShown = false
    }

    fun nextLevel() {
        val newIndex = (levelIndex + 1) % QUIPU_LEVELS.size
        levelIndex = newIndex
        newNumber(QUIPU_LEVELS[newIndex])
    }

    fun addKnot() {
        val sIdx = states.indexOfFirst { !it.done }
        if (sIdx < 0) return
        val state = states[sIdx]
        val position = state.active
        if (state.knots[position] >= state.digits[position]) return
        val newKnots = state.knots.toMutableList().also { it[position]++ }
        val previousActive = state.active
        val updated = withActive(state.copy(knots = newKnots))
        val filled = newKnots[position] == state.digits[position]
        states = states.toMutableList().also { it[sIdx] = updated }

        if (states.all { it.done }) {
            message = ""
            showCongrats = true
            if (levelIndex == QUIPU_LEVELS.size - 1) finalCongratsShown = true
        } else if (updated.done) {
            message = xs.feedbackString
        } else if (filled && updated.active > previousActive) {
            message = xs.feedbackOrder
        } else {
            message = ""
        }
    }

    fun removeKnot() {
        var sIdx = -1
        for (i in states.indices.reversed()) {
            if (states[i].knots.any { it > 0 }) {
                sIdx = i
                break
            }
        }
        if (activeIndex >= 0 && states[activeIndex].knots.any { it > 0 }) {
            sIdx = activeIndex
        }
        if (sIdx < 0) return
        val state = states[sIdx]
        val newKnots = state.knots.toMutableList()
        for (i in newKnots.indices.reversed()) {
            if (newKnots[i] > 0) {
                newKnots[i]--
                break
            }
        }
        states = states.toMutableList().also { it[sIdx] = withActive(state.copy(knots = newKnots)) }
        showCongrats = false
        message = ""
    }

    val congratsText = if (levelIndex == QUIPU_LEVELS.size - 1) xs.feedbackAllLevels else xs.feedbackLevel

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

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = xs.instruction,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "${s.common.levelPrefix}${levelIndex + 1}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { removeKnot() },
                        enabled = canRemove,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = xs.removeKnot,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    FilledTonalButton(
                        onClick = { addKnot() },
                        enabled = canAdd,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
                        )
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = xs.addKnot,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                QuipuBoard(states = states, positions = level.positions, activeIndex = activeIndex, xs = xs)

                Spacer(Modifier.height(16.dp))

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = ACTIVE_COLOR,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }

                if (showCongrats) {
                    Text(
                        text = congratsText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = DONE_COLOR,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { newNumber(level) },
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
                    FilledTonalButton(
                        onClick = { nextLevel() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ButtonYellow,
                            contentColor = OnButtonYellow
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

                Spacer(Modifier.height(48.dp))
            }

            PracticingWithQuipusSourcesMenu(
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
private fun QuipuBoard(
    states: List<QuipuStringState>,
    positions: Int,
    activeIndex: Int,
    xs: PracticingWithQuipusScreenStrings
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(CORD_COLOR, RoundedCornerShape(3.dp))
        )

        Spacer(Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            states.forEachIndexed { index, state ->
                val isActive = index == activeIndex && !state.done
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(STRING_WIDTH_DP.dp)
                        .then(
                            if (isActive) {
                                Modifier
                                    .border(2.dp, ACTIVE_COLOR, RoundedCornerShape(8.dp))
                                    .padding(6.dp)
                            } else {
                                Modifier.padding(8.dp)
                            }
                        )
                ) {
                    Text(
                        text = state.target.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (state.done) DONE_COLOR else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(6.dp))

                    val cordHeight = (ORDER_HEIGHT_DP * positions).dp
                    Box(
                        modifier = Modifier.height(cordHeight),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(cordHeight)
                                .background(CORD_COLOR, RoundedCornerShape(2.dp))
                                .align(Alignment.TopCenter)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            for (p in positions - 1 downTo 0) {
                                QuipuOrder(
                                    knots = state.knots[p],
                                    active = isActive && p == state.active,
                                    done = state.knots[p] == state.digits[p],
                                    label = orderName(p, xs),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuipuOrder(
    knots: Int,
    active: Boolean,
    done: Boolean,
    label: String,
) {
    Box(
        modifier = Modifier
            .height(ORDER_HEIGHT_DP.dp)
            .width(STRING_WIDTH_DP.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(KNOT_GAP_DP.dp)
        ) {
            repeat(knots) {
                Box(
                    modifier = Modifier
                        .size(KNOT_SIZE_DP.dp)
                        .background(KNOT_COLOR, CircleShape)
                )
            }
        }

        Text(
            text = label,
            fontSize = 9.sp,
            color = when {
                active -> ACTIVE_COLOR
                done -> DONE_COLOR
                else -> Color(0xFF757575)
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(30.dp),
            textAlign = TextAlign.Start
        )
    }
}

private data class QuipuSource(val label: String, val url: String)

@Composable
private fun PracticingWithQuipusSourcesMenu(modifier: Modifier = Modifier) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var showSourcesMenu by remember { mutableStateOf(false) }
    var activeSource by remember { mutableStateOf<QuipuSource?>(null) }

    val sources = listOf(
        QuipuSource(
            "Ascher, M., & Ascher, R.",
            "https://books.google.com.uy/books?hl=en&lr=&id=h169R-sN0toC"
        ),
        QuipuSource(
            s.common.originalText,
            HISTORYTRACERS_ORIGIN + "index.html?page=class_content&arg=" + QUIPU_LESSON_UUID
        ),
    )

    Box(
        modifier = modifier.padding(bottom = 8.dp, start = 8.dp)
    ) {
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
            expanded = showSourcesMenu && activeSource == null,
            onDismissRequest = { showSourcesMenu = false }
        ) {
            sources.forEach { source ->
                DropdownMenuItem(
                    text = { Text(source.label) },
                    trailingIcon = {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    },
                    onClick = { activeSource = source }
                )
            }
        }

        DropdownMenu(
            expanded = showSourcesMenu && activeSource != null,
            onDismissRequest = { activeSource = null }
        ) {
            activeSource?.let { source ->
                DropdownMenuItem(
                    text = { Text(s.common.copyUrl) },
                    onClick = {
                        showSourcesMenu = false
                        activeSource = null
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", source.url))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        activeSource = null
                        uriHandler.openUri(source.url)
                    }
                )
            }
        }
    }
}
