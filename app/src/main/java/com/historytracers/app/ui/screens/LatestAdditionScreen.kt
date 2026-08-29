// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.R
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.iAmNotLikeYouScreenStringsForLanguage
import com.historytracers.app.ui.features.latestAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.walkAmongNumbersScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

private data class LatestAdditionEntry(
    val sectionId: String,
    val label: String,
    val icon: @Composable () -> Unit,
    val isCompleted: () -> Boolean,
    val markCompleted: suspend () -> Unit,
    val onNavigate: () -> Unit
)

@Composable
fun LatestAdditionScreen(
    scrollState: ScrollState = rememberScrollState(),
    onNavigateBack: () -> Unit = {},
    onNavigateToWalkAmongNumbersIntro: () -> Unit = {},
    onNavigateToEqualityIntro: () -> Unit = {},
    onNavigateToTotallyEqualIntro: () -> Unit = {},
    onNavigateToHistoricalEqualityIntro: () -> Unit = {},
    onNavigateToHistoricalEqualityPyramidsIntro: () -> Unit = {},
    onNavigateToEqualSameGroupDifferent: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = latestAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val ials = iAmNotLikeYouScreenStringsForLanguage(LocalAppLanguage.current)
    val was = walkAmongNumbersScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedIAmNotLikeYou by preferences.completedIAmNotLikeYouSections.collectAsState(initial = emptySet())
    val completedRoadToSomewhere by preferences.completedRoadToSomewhereSections.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    val entries = listOf(
        LatestAdditionEntry(
            sectionId = "walk_among_numbers",
            label = was.title,
            icon = { Icon(Icons.AutoMirrored.Filled.DirectionsWalk, contentDescription = null, modifier = Modifier.size(48.dp), tint = OnButtonYellow) },
            isCompleted = { "walk_among_numbers" in completedRoadToSomewhere },
            markCompleted = {},
            onNavigate = onNavigateToWalkAmongNumbersIntro
        ),
        LatestAdditionEntry(
            sectionId = "equality_in_history",
            label = ials.equalityInHistoryPyramids,
            icon = { Icon(painterResource(R.drawable.ic_pyramid), contentDescription = null, modifier = Modifier.size(48.dp)) },
            isCompleted = { "equality_in_history" in completedIAmNotLikeYou },
            markCompleted = { preferences.markIAmNotLikeYouSectionCompleted("equality_in_history") },
            onNavigate = onNavigateToHistoricalEqualityPyramidsIntro
        ),
        LatestAdditionEntry(
            sectionId = "equality_in_history_metate",
            label = ials.equalityInHistoryMetate,
            icon = { Icon(painterResource(R.drawable.ic_metate), contentDescription = null, modifier = Modifier.size(48.dp)) },
            isCompleted = { "equality_in_history_metate" in completedIAmNotLikeYou },
            markCompleted = { preferences.markIAmNotLikeYouSectionCompleted("equality_in_history_metate") },
            onNavigate = onNavigateToHistoricalEqualityIntro
        ),
        LatestAdditionEntry(
            sectionId = "totally_equal",
            label = ials.totallyEqual,
            icon = { Text(xs.equalsSign, fontSize = 44.sp, textAlign = TextAlign.Center, color = OnButtonYellow) },
            isCompleted = { "totally_equal" in completedIAmNotLikeYou },
            markCompleted = { preferences.markIAmNotLikeYouSectionCompleted("totally_equal") },
            onNavigate = onNavigateToTotallyEqualIntro
        ),
        LatestAdditionEntry(
            sectionId = "equal_same_group_or_different",
            label = ials.equalSameGroupOrDifferent,
            icon = { Icon(painterResource(R.drawable.ic_square_circle), contentDescription = null, modifier = Modifier.size(48.dp)) },
            isCompleted = { "equal_same_group_or_different" in completedIAmNotLikeYou },
            markCompleted = { preferences.markIAmNotLikeYouSectionCompleted("equal_same_group_or_different") },
            onNavigate = onNavigateToEqualSameGroupDifferent
        )
    )

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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            entries.forEach { entry ->
                FilledIconButton(
                    onClick = {
                        scope.launch {
                            entry.markCompleted()
                            entry.onNavigate()
                        }
                    },
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = entry.label },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (entry.isCompleted()) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    entry.icon()
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = entry.label,
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
}
