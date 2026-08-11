// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.R
import com.historytracers.app.data.LevelGroupController
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.firstStepsScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.FlagBlueDark
import com.historytracers.app.ui.theme.FlagBlueLight
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

@Composable
fun FirstStepsScreen(
    scrollState: ScrollState = rememberScrollState(),
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToCongratulation: () -> Unit = {},
    onNavigateToSocrates: () -> Unit = {},
    onNavigateToLearningInLayers: () -> Unit = {},
    onNavigateToHowDoILearn: () -> Unit = {},
    onNavigateToMyHands: () -> Unit = {},
    onNavigateToMyBody: () -> Unit = {},
    onNavigateToFirstHands: () -> Unit = {},
    onNavigateToFirstVoice: () -> Unit = {},
    onNavigateToDrawingAndCouting: () -> Unit = {},
    onNavigateToNumbers: () -> Unit = {},
    onNavigateToFamilyPart1: () -> Unit = {},
    onNavigateToTheZero: () -> Unit = {},
    onNavigateToSequenceGame: () -> Unit = {},
    onNavigateToSequenceGameOrders: () -> Unit = {},
    onNavigateToSequenceGameFamilies: () -> Unit = {},
    onNavigateToBuildingGame: () -> Unit = {},
    onNavigateToNaturalFamiliesPart2: () -> Unit = {},
    onNavigateToTowardInfinity: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val xs = firstStepsScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedSections by preferences.completedFirstStepsSections.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    val controller = remember {
        LevelGroupController(
            listOf("i_dont_know", "learning_in_shells", "how_do_i_learn", "my_hands", "my_body", "drawing", "numbers", "sequence_game", "family_part1", "building", "natural_families_part2", "going_to_infinity"),
            completedSections
        )
    }
    LaunchedEffect(completedSections) {
        controller.syncFromPersisted(completedSections)
    }

    val claimedLevels by preferences.claimedLevels.collectAsState(initial = emptySet())

    fun claimFirstStepsLevel() {
        if ("first_steps" in claimedLevels) return
        onScoreChanged(currentScore + 10)
        scope.launch { preferences.markLevelClaimed("first_steps") }
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
                    text = hts.firstSteps,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
            modifier = Modifier.verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FilledIconButton(
                onClick = {
                    controller.markCompleted("i_dont_know")
                    scope.launch { preferences.markFirstStepsSectionCompleted("i_dont_know") }
                    onNavigateToSocrates()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("i_dont_know")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    Icons.Default.Psychology,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = hts.iDontKnow,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("learning_in_shells")
                    scope.launch { preferences.markFirstStepsSectionCompleted("learning_in_shells") }
                    onNavigateToLearningInLayers()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("learning_in_shells")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Canvas(modifier = Modifier.size(52.dp)) {
                    val strokeWidth = size.width * 0.06f
                    val center = this.center
                    val half = size.minDimension / 2f
                    val spacing = size.width * 0.12f
                    listOf(0f, spacing, 2 * spacing).forEach { inset ->
                        val h = half - inset
                        drawRect(
                            color = OnButtonYellow,
                            topLeft = Offset(center.x - h, center.y - h),
                            size = Size(2 * h, 2 * h),
                            style = Stroke(width = strokeWidth)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.learningInLayers,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("how_do_i_learn")
                    scope.launch { preferences.markFirstStepsSectionCompleted("how_do_i_learn") }
                    onNavigateToHowDoILearn()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("how_do_i_learn")) ButtonYellowDark else ButtonYellow
                )
            ) {
                val textMeasurer = rememberTextMeasurer()
                Canvas(modifier = Modifier.size(52.dp)) {
                    val strokeWidth = size.width * 0.06f
                    val headRadius = size.width * 0.26f
                    val headCenter = Offset(center.x, center.y + size.height * 0.10f)
                    drawCircle(
                        color = OnButtonYellow,
                        radius = headRadius,
                        center = headCenter,
                        style = Stroke(width = strokeWidth)
                    )
                    val eyeRadius = size.width * 0.045f
                    val eyeY = headCenter.y - headRadius * 0.25f
                    drawCircle(
                        color = OnButtonYellow,
                        radius = eyeRadius,
                        center = Offset(headCenter.x - headRadius * 0.4f, eyeY)
                    )
                    drawCircle(
                        color = OnButtonYellow,
                        radius = eyeRadius,
                        center = Offset(headCenter.x + headRadius * 0.4f, eyeY)
                    )
                    drawArc(
                        color = OnButtonYellow,
                        startAngle = 15f,
                        sweepAngle = 150f,
                        useCenter = false,
                        topLeft = Offset(headCenter.x - headRadius * 0.3f, headCenter.y + headRadius * 0.15f),
                        size = Size(headRadius * 0.6f, headRadius * 0.45f),
                        style = Stroke(width = strokeWidth)
                    )
                    drawCircle(
                        color = OnButtonYellow,
                        radius = size.width * 0.04f,
                        center = Offset(center.x - size.width * 0.12f, center.y - size.height * 0.18f)
                    )
                    drawCircle(
                        color = OnButtonYellow,
                        radius = size.width * 0.06f,
                        center = Offset(center.x - size.width * 0.02f, center.y - size.height * 0.30f)
                    )
                    val qm = textMeasurer.measure(
                        text = "?",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OnButtonYellow)
                    )
                    drawText(
                        textLayoutResult = qm,
                        topLeft = Offset(
                            center.x + size.width * 0.10f - qm.size.width / 2f,
                            center.y - size.height * 0.42f - qm.size.height / 2f
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.howDoILearn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("my_hands")
                    scope.launch { preferences.markFirstStepsSectionCompleted("my_hands") }
                    onNavigateToMyHands()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("my_hands")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    Icons.Default.Handshake,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = hts.myHands,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("first_hands")
                    scope.launch { preferences.markFirstStepsSectionCompleted("first_hands") }
                    onNavigateToFirstHands()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("first_hands")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_hand_bones),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.firstHands,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("first_voice")
                    scope.launch { preferences.markFirstStepsSectionCompleted("first_voice") }
                    onNavigateToFirstVoice()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("first_voice")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Canvas(modifier = Modifier.size(52.dp)) {
                    val strokeWidth = size.width * 0.06f
                    val headRadius = size.width * 0.26f
                    val headCenter = Offset(center.x, center.y + size.height * 0.10f)
                    drawCircle(
                        color = OnButtonYellow,
                        radius = headRadius,
                        center = headCenter,
                        style = Stroke(width = strokeWidth)
                    )
                    val eyeRadius = size.width * 0.045f
                    val eyeY = headCenter.y - headRadius * 0.25f
                    drawCircle(
                        color = OnButtonYellow,
                        radius = eyeRadius,
                        center = Offset(headCenter.x - headRadius * 0.4f, eyeY)
                    )
                    drawCircle(
                        color = OnButtonYellow,
                        radius = eyeRadius,
                        center = Offset(headCenter.x + headRadius * 0.4f, eyeY)
                    )
                    drawCircle(
                        color = OnButtonYellow,
                        radius = headRadius * 0.14f,
                        center = Offset(headCenter.x, headCenter.y + headRadius * 0.35f),
                        style = Stroke(width = strokeWidth)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.firstVoice,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("my_body")
                    scope.launch { preferences.markFirstStepsSectionCompleted("my_body") }
                    onNavigateToMyBody()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("my_body")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_body),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = hts.myBody,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("drawing")
                    scope.launch { preferences.markFirstStepsSectionCompleted("drawing") }
                    onNavigateToDrawingAndCouting()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("drawing")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = hts.drawing,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("numbers")
                    scope.launch { preferences.markFirstStepsSectionCompleted("numbers") }
                    onNavigateToNumbers()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("numbers")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Text(
                    text = "9",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = hts.numbers,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("the_zero")
                    scope.launch { preferences.markFirstStepsSectionCompleted("the_zero") }
                    onNavigateToTheZero()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("the_zero")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Text(
                    text = "0",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.weHaveZero,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("sequence_game")
                    scope.launch { preferences.markFirstStepsSectionCompleted("sequence_game") }
                    onNavigateToSequenceGame()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("sequence_game")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Text(
                    text = "3 _  5",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = hts.sequenceGame,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("family_part1")
                    scope.launch { preferences.markFirstStepsSectionCompleted("family_part1") }
                    onNavigateToFamilyPart1()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("family_part1")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    Icons.Default.People,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = hts.familyPart1,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = { onNavigateToSequenceGameFamilies() },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("sequence_game_families")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Text(
                    text = "_ 50 51",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.sequenceGameFamilies,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("building")
                    scope.launch { preferences.markFirstStepsSectionCompleted("building") }
                    onNavigateToBuildingGame()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("building")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_brick),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.building,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("natural_families_part2")
                    scope.launch { preferences.markFirstStepsSectionCompleted("natural_families_part2") }
                    onNavigateToNaturalFamiliesPart2()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("natural_families_part2")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_tree),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = hts.naturalFamiliesPart2,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = { onNavigateToSequenceGameOrders() },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("sequence_game_orders")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Text(
                    text = "${xs.ordersUnits}\n${xs.ordersTens}\n${xs.ordersMore}",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnButtonYellow,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.sequenceGameOrders,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("going_to_infinity")
                    scope.launch { preferences.markFirstStepsSectionCompleted("going_to_infinity") }
                    onNavigateToTowardInfinity()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("going_to_infinity")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Text(
                    text = "\u221E",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = hts.goingToInfinity,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    scope.launch { preferences.markFirstStepsSectionCompleted("limits_min_max") }
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("limits_min_max")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "|",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnButtonYellow
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Default.Accessibility,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = OnButtonYellow
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "|",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnButtonYellow
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.limitsMinMax,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    scope.launch { preferences.markFirstStepsSectionCompleted("where_are_they") }
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("where_are_they")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Text(
                    text = "?",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.whereAreThey,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    claimFirstStepsLevel()
                    onNavigateToCongratulation()
                },
                enabled = controller.allCompleted,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if ("first_steps" in claimedLevels) FlagBlueDark else FlagBlueLight,
                    disabledContainerColor = FlagBlueLight
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_flag),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.common.nextLevel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
    }
}