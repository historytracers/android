// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
 * Interactive "multiplication table" app, analogous to the web version's
 * Table of N. The user selects one of the objects in the selector and taps
 * one of the multiplications (factor x 0 .. factor x 9); the area below then
 * shows the selected multiplier groups, each holding `factor` of the chosen
 * objects. For the table of zero every group is shown empty.
 *
 * Reused by the multiplication table screens (0..9).
 */
@Composable
fun MultiplicationTableVisualizer(
    factor: Int,
    modifier: Modifier = Modifier
) {
    val vs = multiplicationTableVisualizerStringsForLanguage(LocalAppLanguage.current)

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

        Spacer(Modifier.height(8.dp))

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
                        .padding(4.dp)
                        .size(44.dp)
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
                    Text(text = emoji, fontSize = 22.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(30.dp))
                    .background(PANEL_BLUE)
                    .padding(horizontal = 6.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = vs.tableOf.format(factor),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PANEL_TEXT
                )

                Spacer(Modifier.height(4.dp))

                (0..9).forEach { e ->
                    val selected = selectedMultiplier == e
                    Text(
                        text = "${factor * e} = $factor \u00D7 $e",
                        fontSize = 16.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = PANEL_TEXT,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selected) SELECTED_ROW_BLUE else Color.Transparent)
                            .clickable { selectedMultiplier = e }
                            .padding(vertical = 4.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(30.dp))
                    .background(PANEL_BLUE)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                val multiplier = selectedMultiplier
                if (multiplier == null) {
                    Text(
                        text = vs.tapHint,
                        style = MaterialTheme.typography.bodyLarge,
                        color = PANEL_TEXT,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(multiplier) {
                            MultiplicationGroup(
                                factor,
                                VISUALIZER_OBJECT_EMOJIS[
                                    selectedObject.coerceIn(0, VISUALIZER_OBJECT_EMOJIS.lastIndex)
                                ]
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MultiplicationGroup(count: Int, emoji: String) {
    if (count == 0) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(32.dp)
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
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(count) {
                Text(text = emoji, fontSize = 20.sp)
            }
        }
    }
}
