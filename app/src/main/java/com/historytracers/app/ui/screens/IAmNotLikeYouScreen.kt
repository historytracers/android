// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.R
import com.historytracers.app.data.LevelGroupController
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.iAmNotLikeYouScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.FlagBlueDark
import com.historytracers.app.ui.theme.FlagBlueLight
import kotlinx.coroutines.launch

@Composable
fun IAmNotLikeYouScreen(
    scrollState: ScrollState = rememberScrollState(),
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToEquality: () -> Unit = {},
    onNavigateToEqualSameGroupDifferent: () -> Unit = {},
    onNavigateToCongratulation: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val xs = iAmNotLikeYouScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedSections by preferences.completedIAmNotLikeYouSections.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    val controller = remember {
        LevelGroupController(
            listOf("to_be_or_not_to_be", "totally_equal", "equality_in_history", "equal_same_group_or_different"),
            completedSections
        )
    }
    LaunchedEffect(completedSections) {
        controller.syncFromPersisted(completedSections)
    }

    val claimedLevels by preferences.claimedLevels.collectAsState(initial = emptySet())

    fun claimLevel() {
        if ("i_am_not_like_you" in claimedLevels) return
        onScoreChanged(currentScore + 10)
        scope.launch { preferences.markLevelClaimed("i_am_not_like_you") }
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
                    text = hts.iAmNotLikeYou,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FilledIconButton(
                onClick = {
                    controller.markCompleted("to_be_or_not_to_be")
                    scope.launch { preferences.markIAmNotLikeYouSectionCompleted("to_be_or_not_to_be") }
                    onNavigateToEquality()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("to_be_or_not_to_be")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Text(
                    text = xs.thinkingEmoji,
                    fontSize = 44.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.toBeOrNotToBe,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("totally_equal")
                    scope.launch { preferences.markIAmNotLikeYouSectionCompleted("totally_equal") }
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("totally_equal")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Text(
                    text = "=",
                    fontSize = 44.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.totallyEqual,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("equality_in_history")
                    scope.launch { preferences.markIAmNotLikeYouSectionCompleted("equality_in_history") }
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("equality_in_history")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_pyramid),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.equalityInHistoryPyramids,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    controller.markCompleted("equal_same_group_or_different")
                    scope.launch { preferences.markIAmNotLikeYouSectionCompleted("equal_same_group_or_different") }
                    onNavigateToEqualSameGroupDifferent()
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (completedSections.contains("equal_same_group_or_different")) ButtonYellowDark else ButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_square_circle),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = xs.equalSameGroupOrDifferent,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    claimLevel()
                    onNavigateToCongratulation()
                },
                enabled = controller.allCompleted,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if ("i_am_not_like_you" in claimedLevels) FlagBlueDark else FlagBlueLight,
                    disabledContainerColor = FlagBlueLight
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_flag),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = androidx.compose.ui.graphics.Color.Unspecified
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

            Spacer(Modifier.height(48.dp))
        }
    }
}
