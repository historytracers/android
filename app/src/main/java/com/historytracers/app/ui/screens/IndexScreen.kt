// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.historytracers.app.R
import com.historytracers.app.data.LevelGroupController
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.indexScreenStringsForLanguage
import com.historytracers.app.ui.features.yupanaSharedStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.OnButtonYellow

private val firstStepsSectionIds = listOf(
    "i_dont_know", "learning_in_shells", "how_do_i_learn", "my_hands", "first_hands",
    "first_voice", "my_body", "drawing", "numbers", "the_zero", "sequence_game",
    "family_part1", "sequence_game_families", "building", "natural_families_part2",
    "sequence_game_orders", "going_to_infinity", "limits_min_max", "where_are_they"
)

private val iAmNotLikeYouSectionIds = listOf(
    "to_be_or_not_to_be", "totally_equal", "equality_in_history_metate",
    "equality_in_history", "equal_same_group_or_different"
)

private val workoutSectionIds = listOf(
    "exercising_hands", "exercising_feet_and_hands", "exercising_addition",
    "exercising_multiplication", "exercising_multiplication_l2"
)

private val abacusSectionIds = listOf(
    "soroban_writing", "suanpan_writing", "schyoty_writing", "large_numbers_writing",
    "adding_with_abacus", "complement_to_ten", "adding_large_numbers", "practicing_addition",
    "multiplication_table", "carrying", "multiplying_with_abacus", "multiplying_with_abacus_l2",
    "multiplying_without_limits", "subtracting_with_abacus"
)

private val yupanaSectionIds = listOf(
    "hands_on_yupana", "moving_in_yupana"
)

@Composable
fun IndexScreen(
    onNavigateToFirstSteps: () -> Unit = {},
    onNavigateToIAmNotLikeYou: () -> Unit = {},
    onNavigateToWorkout: () -> Unit = {},
    onNavigateToAbacus: () -> Unit = {},
    onNavigateToYupana: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val ys = yupanaSharedStringsForLanguage(LocalAppLanguage.current)
    val xs = indexScreenStringsForLanguage(LocalAppLanguage.current)

    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedFirstSteps by preferences.completedFirstStepsSections.collectAsState(initial = emptySet())
    val completedIAmNotLikeYou by preferences.completedIAmNotLikeYouSections.collectAsState(initial = emptySet())
    val completedWorkout by preferences.completedWorkoutSections.collectAsState(initial = emptySet())
    val completedAbacus by preferences.completedAbacusSections.collectAsState(initial = emptySet())
    val completedYupana by preferences.completedYupanaSections.collectAsState(initial = emptySet())

    val firstStepsController = remember { LevelGroupController(firstStepsSectionIds) }
    val iAmNotLikeYouController = remember { LevelGroupController(iAmNotLikeYouSectionIds) }
    val workoutController = remember { LevelGroupController(workoutSectionIds) }
    val abacusController = remember { LevelGroupController(abacusSectionIds) }
    val yupanaController = remember { LevelGroupController(yupanaSectionIds) }
    LaunchedEffect(completedFirstSteps, completedIAmNotLikeYou, completedWorkout, completedAbacus, completedYupana) {
        firstStepsController.syncFromPersisted(completedFirstSteps)
        iAmNotLikeYouController.syncFromPersisted(completedIAmNotLikeYou)
        workoutController.syncFromPersisted(completedWorkout)
        abacusController.syncFromPersisted(completedAbacus)
        yupanaController.syncFromPersisted(completedYupana)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FilledTonalButton(
                onClick = onNavigateToFirstSteps,
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (firstStepsController.allCompleted) ButtonYellowDark else ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.DirectionsWalk,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = hts.firstSteps,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = onNavigateToIAmNotLikeYou,
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (iAmNotLikeYouController.allCompleted) ButtonYellowDark else ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_square_circle),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = hts.iAmNotLikeYou,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = { /* TODO */ },
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_road),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = hts.aRoadToSomewhere,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = { /* TODO */ },
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_rabbit),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = xs.runningAndGrowing,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = { /* TODO */ },
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_galaxy),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = hts.whereAreWeFrom,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = { /* TODO */ },
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_turn_left),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = hts.returning,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = onNavigateToWorkout,
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (workoutController.allCompleted) ButtonYellowDark else ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = hts.workout,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = onNavigateToYupana,
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (yupanaController.allCompleted) ButtonYellowDark else ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_yupana),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = ys.yupana,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = onNavigateToAbacus,
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (abacusController.allCompleted) ButtonYellowDark else ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    Icons.Default.Calculate,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = hts.abacus,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
