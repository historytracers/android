// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.R
import com.historytracers.app.data.LevelGroupController
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

@Composable
fun FirstStepsScreen(
    scrollState: ScrollState = rememberScrollState(),
    onNavigateBack: () -> Unit = {},
    onNavigateToCongratulation: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedSections by preferences.completedFirstStepsSections.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    val controller = remember {
        LevelGroupController(
            listOf("i_dont_know", "my_hands", "my_body", "drawing", "numbers", "sequence_game", "family_part1", "building", "natural_families_part2", "going_to_infinity"),
            completedSections
        )
    }
    LaunchedEffect(completedSections) {
        controller.syncFromPersisted(completedSections)
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.back)
                }
                Text(
                    text = s.firstSteps,
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
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
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
                text = s.iDontKnow,
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
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
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
                text = s.myHands,
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
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
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
                text = s.myBody,
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
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
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
                text = s.drawing,
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
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
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
                text = s.numbers,
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
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
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
                text = s.sequenceGame,
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
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
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
                text = s.familyPart1,
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
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
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
                text = s.building,
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
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
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
                text = s.naturalFamiliesPart2,
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
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
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
                text = s.goingToInfinity,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = onNavigateToCongratulation,
                enabled = controller.allCompleted,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
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
                text = s.nextLevel,
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