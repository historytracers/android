// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.WbSunny
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
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.indexScreenStringsForLanguage
import com.historytracers.app.ui.features.yupanaSharedStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

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

// Hubs flagged as "new" (sun badge in the top-right corner until first accessed).
// This list is intentionally empty: no main-screen hub button was newly introduced
// by the current release (the five wired hubs already existed), and the four
// placeholder buttons are not complete, so they must NOT be added here.
// When a new, fully functional hub button is added to this screen, insert its id
// (e.g. "workout") here so isNewHub flags it; the badge hides once the user taps
// it (persisted via UserPreferences.markNewHubSeen).
private val newHubIds = setOf<String>()

private fun isNewHub(hubId: String, seenNewHubs: Set<String>): Boolean =
    hubId in newHubIds && hubId !in seenNewHubs

@Composable
private fun BoxScope.NewHubSunBadge() {
    Icon(
        Icons.Filled.WbSunny,
        contentDescription = null,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .size(28.dp)
            .padding(top = 4.dp, end = 4.dp),
        tint = Color(0xFFFFA000)
    )
}

@Composable
fun IndexScreen(
    onNavigateToFirstSteps: () -> Unit = {},
    onNavigateToIAmNotLikeYou: () -> Unit = {},
    onNavigateToWorkout: () -> Unit = {},
    onNavigateToAbacus: () -> Unit = {},
    onNavigateToYupana: () -> Unit = {},
    onNavigateToRoadToSomewhere: () -> Unit = {}
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
    val seenNewHubs by preferences.seenNewHubs.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    val firstStepsDone = firstStepsSectionIds.all { it in completedFirstSteps }
    val iAmNotLikeYouDone = iAmNotLikeYouSectionIds.all { it in completedIAmNotLikeYou }
    val workoutDone = workoutSectionIds.all { it in completedWorkout }
    val abacusDone = abacusSectionIds.all { it in completedAbacus }
    val yupanaDone = yupanaSectionIds.all { it in completedYupana }

    val firstStepsNew = isNewHub("first_steps", seenNewHubs)
    val iAmNotLikeYouNew = isNewHub("i_am_not_like_you", seenNewHubs)
    val workoutNew = isNewHub("workout", seenNewHubs)
    val yupanaNew = isNewHub("yupana", seenNewHubs)
    val abacusNew = isNewHub("abacus", seenNewHubs)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                FilledTonalButton(
                    onClick = {
                        if (firstStepsNew) scope.launch { preferences.markNewHubSeen("first_steps") }
                        onNavigateToFirstSteps()
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (firstStepsNew) ButtonYellow else if (firstStepsDone) ButtonYellowDark else ButtonYellow,
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
                if (firstStepsNew) NewHubSunBadge()
            }

            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                FilledTonalButton(
                    onClick = {
                        if (iAmNotLikeYouNew) scope.launch { preferences.markNewHubSeen("i_am_not_like_you") }
                        onNavigateToIAmNotLikeYou()
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (iAmNotLikeYouNew) ButtonYellow else if (iAmNotLikeYouDone) ButtonYellowDark else ButtonYellow,
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
                if (iAmNotLikeYouNew) NewHubSunBadge()
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = { onNavigateToRoadToSomewhere() },
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

            Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                FilledTonalButton(
                    onClick = {
                        if (workoutNew) scope.launch { preferences.markNewHubSeen("workout") }
                        onNavigateToWorkout()
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (workoutNew) ButtonYellow else if (workoutDone) ButtonYellowDark else ButtonYellow,
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
                if (workoutNew) NewHubSunBadge()
            }

            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                FilledTonalButton(
                    onClick = {
                        if (yupanaNew) scope.launch { preferences.markNewHubSeen("yupana") }
                        onNavigateToYupana()
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (yupanaNew) ButtonYellow else if (yupanaDone) ButtonYellowDark else ButtonYellow,
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
                if (yupanaNew) NewHubSunBadge()
            }

            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.padding(horizontal = 32.dp)) {
                FilledTonalButton(
                    onClick = {
                        if (abacusNew) scope.launch { preferences.markNewHubSeen("abacus") }
                        onNavigateToAbacus()
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (abacusNew) ButtonYellow else if (abacusDone) ButtonYellowDark else ButtonYellow,
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
                if (abacusNew) NewHubSunBadge()
            }
        }
    }
}
