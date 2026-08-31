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
import androidx.compose.material.icons.filled.Calculate
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
import com.historytracers.app.R
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.carryingInAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.iAmNotLikeYouScreenStringsForLanguage
import com.historytracers.app.ui.features.latestAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.orderOfAdditionScreenStringsForLanguage
import com.historytracers.app.ui.features.practicingAdditionRoadScreenStringsForLanguage
import com.historytracers.app.ui.features.roadToSomewhereScreenStringsForLanguage
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
    onNavigateToOrderOfAdditionIntro: () -> Unit = {},
    onNavigateToWalkAmongNumbersIntro: () -> Unit = {},
    onNavigateToCarryingInAdditionIntro: () -> Unit = {},
    onNavigateToPracticingAdditionRoad: () -> Unit = {},
    onNavigateToEqualityIntro: () -> Unit = {},
    onNavigateToHistoricalEqualityIntro: () -> Unit = {},
    onNavigateToHistoricalEqualityPyramidsIntro: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = latestAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val ials = iAmNotLikeYouScreenStringsForLanguage(LocalAppLanguage.current)
    val was = walkAmongNumbersScreenStringsForLanguage(LocalAppLanguage.current)
    val cas = carryingInAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val pras = practicingAdditionRoadScreenStringsForLanguage(LocalAppLanguage.current)
    val oas = orderOfAdditionScreenStringsForLanguage(LocalAppLanguage.current)
    val rts = roadToSomewhereScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedIAmNotLikeYou by preferences.completedIAmNotLikeYouSections.collectAsState(initial = emptySet())
    val completedRoadToSomewhere by preferences.completedRoadToSomewhereSections.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    val entries = listOf(
        LatestAdditionEntry(
            sectionId = "order_of_addition",
            label = oas.title,
            icon = {
                Text(
                    text = rts.commutativeExpression,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow,
                    textAlign = TextAlign.Center
                )
            },
            isCompleted = { "order_of_addition" in completedRoadToSomewhere },
            markCompleted = {},
            onNavigate = onNavigateToOrderOfAdditionIntro
        ),
        LatestAdditionEntry(
            sectionId = "practicing_addition",
            label = pras.title,
            icon = { Icon(Icons.Filled.Calculate, contentDescription = null, modifier = Modifier.size(48.dp), tint = OnButtonYellow) },
            isCompleted = { "practicing_addition" in completedRoadToSomewhere },
            markCompleted = {},
            onNavigate = onNavigateToPracticingAdditionRoad
        ),
        LatestAdditionEntry(
            sectionId = "carrying_in_addition",
            label = cas.title,
            icon = {
                NumberOneOnStairs(
                    color = OnButtonYellow,
                    label = xs.numberOne,
                    modifier = Modifier.size(48.dp)
                )
            },
            isCompleted = { "carrying_in_addition" in completedRoadToSomewhere },
            markCompleted = {},
            onNavigate = onNavigateToCarryingInAdditionIntro
        ),
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
