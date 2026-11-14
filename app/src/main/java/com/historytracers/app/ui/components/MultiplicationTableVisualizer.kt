// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.features.multiplicationTableVisualizerStringsForLanguage
import kotlinx.coroutines.launch

private val PANEL_BLUE = Color(0xFFADD8E6)
private val PANEL_TEXT = Color(0xFF1A2340)
private val SELECTED_ROW_BLUE = Color(0xFF8EC6E8)
private val EMPTY_GROUP_STROKE = Color(0xFF54708A)

/**
 * Objects drawn by the visualizer. This is the exact same UTF-8 character
 * list used by the web version's multiplication table apps
 * (`htMultiplicationTableObjects` in ht_math.js), in the same order.
 */
private val VISUALIZER_OBJECT_EMOJIS = listOf(
    "⭐", // star
    "🐶", // dog face
    "🐱", // cat face
    "🦄", // unicorn
    "🐼", // panda
    "🐸", // frog
    "🐢", // turtle
    "🐝", // honeybee
    "🐞", // lady beetle
    "🦕", // sauropod
    "🐠", // tropical fish
    "🍎", // apple
    "🍓", // strawberry
    "🍭", // lollipop
    "🍪", // cookie
    "🎈", // balloon
    "⚽", // soccer ball
    "🚀", // rocket
    "🌈", // rainbow
    "🎵"  // musical note
)

/**
 * Whether the screen is tall enough to keep the interactive table and the
 * navigation buttons visible at the same time. On shorter screens (for example
 * in landscape) the visualizing screen falls back to a normally scrolling page.
 */
@Composable
fun multiplicationTableFitsOnScreen(): Boolean =
    LocalConfiguration.current.screenHeightDp >= 480

/**
 * Interactive "multiplication table" app, analogous to the web version's
 * Table of N. The user selects one of the objects in the selector and taps
 * one of the multiplications (factor x 0 .. factor x 10); the area below then
 * shows the selected multiplier groups, each holding `factor` of the chosen
 * objects. For the table of zero every group is shown empty.
 *
 * Reused by the multiplication table screens (tables 0 to 5).
 */
@Composable
fun MultiplicationTableVisualizer(
    factor: Int,
    modifier: Modifier = Modifier
) {
    val vs = multiplicationTableVisualizerStringsForLanguage(LocalAppLanguage.current)
    // The panels scale with the screen instead of using a fixed height, so the
    // lesson text and the navigation buttons stay visible without scrolling.
    val panelHeight = (LocalConfiguration.current.screenHeightDp * 0.34f)
        .dp
        .coerceIn(190.dp, 340.dp)

    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val savedObject by preferences.multiplicationObject.collectAsState(initial = -1)

    var selectedObject by remember { mutableIntStateOf(0) }
    var selectedMultiplier by remember { mutableStateOf<Int?>(null) }

    // Sync the selection with the persisted preference: on first composition it
    // restores the last chosen object; afterwards it simply mirrors our writes.
    LaunchedEffect(savedObject) {
        if (savedObject >= 0) selectedObject = savedObject
    }

    val selectedEmoji = VISUALIZER_OBJECT_EMOJIS[
        selectedObject.coerceIn(0, VISUALIZER_OBJECT_EMOJIS.lastIndex)
    ]

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = vs.chooseObject,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center
        ) {
            VISUALIZER_OBJECT_EMOJIS.forEachIndexed { index, emoji ->
                val selected = selectedObject == index
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = 2.dp,
                            color = if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            selectedObject = index
                            scope.launch { preferences.setMultiplicationObject(index) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 20.sp)
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(panelHeight)
                    .clip(RoundedCornerShape(30.dp))
                    .background(PANEL_BLUE)
                    .padding(horizontal = 4.dp, vertical = 6.dp)
            ) {
                // The eleven multiplications always fit the panel: each row gets
                // an equal share of the available height, which also shrinks the
                // font on short screens.
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val rowHeight = maxHeight / 11
                    val rowFontSize = if (rowHeight < 22.dp) 12.sp else 14.sp

                    Column(modifier = Modifier.fillMaxSize()) {
                        (0..10).forEach { e ->
                            val selected = selectedMultiplier == e
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(rowHeight)
                                    .padding(vertical = 1.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selected) SELECTED_ROW_BLUE else Color.Transparent)
                                    .clickable { selectedMultiplier = e },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${factor * e} = $factor \u00D7 $e",
                                    fontSize = rowFontSize,
                                    maxLines = 1,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    color = PANEL_TEXT,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(panelHeight)
                    .clip(RoundedCornerShape(30.dp))
                    .background(PANEL_BLUE)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                val multiplier = selectedMultiplier
                if (multiplier == null) {
                    Text(
                        text = vs.tapHint,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PANEL_TEXT,
                        textAlign = TextAlign.Center
                    )
                } else {
                    // Large multipliers use smaller objects so the whole group
                    // set fits without needing to scroll.
                    val emojiSize = when {
                        multiplier <= 3 -> 20.sp
                        multiplier <= 6 -> 16.sp
                        else -> 12.sp
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        repeat(multiplier) {
                            MultiplicationGroup(factor, selectedEmoji, emojiSize)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MultiplicationGroup(count: Int, emoji: String, emojiSize: TextUnit = 20.sp) {
    if (count == 0) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(24.dp)
                .drawBehind {
                    drawRoundRect(
                        color = EMPTY_GROUP_STROKE,
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        style = Stroke(
                            width = 3f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 9f), 0f)
                        )
                    )
                }
        )
    } else {
        // FlowRow so large groups wrap onto extra lines instead of
        // overflowing the panel at large font scales.
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            repeat(count) {
                Text(text = emoji, fontSize = emojiSize)
            }
        }
    }
}

/**
 * Draws the same total (6) grouped in three different ways — 1 × 6, 2 × 3 and
 * 3 × 2 — using the object the user selected in the multiplication table
 * visualizer. If nothing was selected yet, the star (index 0) is used.
 *
 * Shared by the multiplication table screens that talk about common multiples.
 */
@Composable
fun MultiplicationTableCommonMultiples(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val savedObject by preferences.multiplicationObject.collectAsState(initial = -1)

    val emoji = VISUALIZER_OBJECT_EMOJIS[
        savedObject.coerceIn(0, VISUALIZER_OBJECT_EMOJIS.lastIndex)
    ]

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf(1 to 6, 2 to 3, 3 to 2).forEach { (groups, perGroup) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$groups \u00D7 $perGroup = 6",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                repeat(groups) {
                    MultiplicationGroup(perGroup, emoji)
                }
            }
        }
    }
}
