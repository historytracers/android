// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PanTool
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
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.bodyExerciseStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.FlagBlueDark
import com.historytracers.app.ui.theme.FlagBlueLight
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

@Composable
fun WorkoutScreen(
    scrollState: ScrollState = rememberScrollState(),
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToClap: () -> Unit = {},
    onNavigateToFeetAndHands: () -> Unit = {},
    onNavigateToCongratulation: () -> Unit = {},
    onNavigateToExercisingAddition: () -> Unit = {},
    onNavigateToRelationship: () -> Unit = {},
    onNavigateToExercisingMultiplicationL2: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val bs = bodyExerciseStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedSections by preferences.completedWorkoutSections.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    val group1Controller = remember {
        LevelGroupController(
            listOf("exercising_hands", "exercising_feet_and_hands"),
            completedSections
        )
    }
    val group2Controller = remember {
        LevelGroupController(
            listOf("exercising_addition"),
            completedSections
        )
    }
    val group3Controller = remember {
        LevelGroupController(
            listOf("exercising_multiplication", "exercising_multiplication_l2"),
            completedSections
        )
    }
    LaunchedEffect(completedSections) {
        group1Controller.syncFromPersisted(completedSections)
        group2Controller.syncFromPersisted(completedSections)
        group3Controller.syncFromPersisted(completedSections)
    }

    val claimedLevels by preferences.claimedLevels.collectAsState(initial = emptySet())
    var showResetMenu by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    fun claimLevel(levelId: String) {
        if (levelId in claimedLevels) return
        onScoreChanged(currentScore + 10)
        scope.launch { preferences.markLevelClaimed(levelId) }
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
                    text = hts.workout,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(Modifier.weight(1f))
                Box {
                    IconButton(onClick = { showResetMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = s.common.menu,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = showResetMenu,
                        onDismissRequest = { showResetMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(s.common.resetClasses) },
                            onClick = {
                                showResetMenu = false
                                showResetDialog = true
                            }
                        )
                    }
                }
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
                onClick = onNavigateToClap,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("exercising_hands")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    Icons.Default.PanTool,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = bs.exercisingHands,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(32.dp))

            FilledIconButton(
                onClick = onNavigateToFeetAndHands,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("exercising_feet_and_hands")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    Icons.Default.Accessibility,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = bs.exercisingFeetAndHands,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    claimLevel("workout_group1")
                    onNavigateToCongratulation()
                },
                enabled = group1Controller.allCompleted,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if ("workout_group1" in claimedLevels) FlagBlueDark else FlagBlueLight,
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

            Spacer(Modifier.height(32.dp))

            FilledIconButton(
                onClick = onNavigateToExercisingAddition,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("exercising_addition")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_person_arms_up),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = Color.Black
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = bs.exercisingAddition,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(32.dp))

            FilledIconButton(
                onClick = {
                    claimLevel("workout_group2")
                    onNavigateToCongratulation()
                },
                enabled = group2Controller.allCompleted,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if ("workout_group2" in claimedLevels) FlagBlueDark else FlagBlueLight,
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

            Spacer(Modifier.height(32.dp))

            FilledIconButton(
                onClick = onNavigateToRelationship,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("exercising_multiplication")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_person_arms_up),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = bs.exercisingMultiplication,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(32.dp))

            FilledIconButton(
                onClick = onNavigateToExercisingMultiplicationL2,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("exercising_multiplication_l2")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Box(
                    modifier = Modifier.size(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(R.drawable.ic_person_arms_up),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = "2",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = bs.exercisingMultiplicationL2,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(32.dp))

            FilledIconButton(
                onClick = {
                    claimLevel("workout_group3")
                    onNavigateToCongratulation()
                },
                enabled = group3Controller.allCompleted,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if ("workout_group3" in claimedLevels) FlagBlueDark else FlagBlueLight,
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

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(s.common.resetClasses) },
            text = { Text(s.common.resetClassesMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        scope.launch { preferences.resetAllClasses() }
                    }
                ) {
                    Text(s.common.ok)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(s.common.cancel)
                }
            }
        )
    }
}