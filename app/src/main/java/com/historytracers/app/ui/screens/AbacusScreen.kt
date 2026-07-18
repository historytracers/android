// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.ScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun AbacusScreen(
    scrollState: ScrollState = rememberScrollState(),
    onNavigateBack: () -> Unit = {},
    onNavigateToCongratulation: () -> Unit = {},
    onNavigateToSorobanWriting: () -> Unit = {},
    onNavigateToSuanpanWriting: () -> Unit = {},
    onNavigateToLargeNumbersWriting: () -> Unit = {},
    onNavigateToPracticingAddition: () -> Unit = {},
    onNavigateToMultiplicationTable: () -> Unit = {},
    onNavigateToMultiplyingWithAbacus: () -> Unit = {},
    onNavigateToMultiplyingWithAbacusLevel2: () -> Unit = {},
    onNavigateToMultiplyingWithoutLimits: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedSections by preferences.completedAbacusSections.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    val group1Controller = remember {
        LevelGroupController(
            listOf("history", "soroban_writing", "suanpan_writing", "large_numbers_writing"),
            completedSections
        )
    }
    val group2Controller = remember {
        LevelGroupController(
            listOf("adding_two_numbers", "adding_large_numbers", "practicing_addition"),
            completedSections
        )
    }
    val group3Controller = remember {
        LevelGroupController(
            listOf("multiplication_table", "abacus_in_rereading", "multiplying_with_abacus", "multiplying_with_abacus_l2", "multiplying_without_limits"),
            completedSections
        )
    }
    LaunchedEffect(completedSections) {
        group1Controller.syncFromPersisted(completedSections)
        group2Controller.syncFromPersisted(completedSections)
        group3Controller.syncFromPersisted(completedSections)
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
                    text = s.abacus,
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
                    group1Controller.markCompleted("history")
                    scope.launch { preferences.markAbacusSectionCompleted("history") }
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_feather),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.history,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(32.dp))

            FilledIconButton(
                onClick = onNavigateToSorobanWriting,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = "\u7B97\u76E4",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.writingToSoroban,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(32.dp))

            FilledIconButton(
                onClick = onNavigateToSuanpanWriting,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = "\u7B97\u76E4",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.writingToSuanpan,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(32.dp))

            FilledIconButton(
                onClick = onNavigateToLargeNumbersWriting,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = "99",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.largeNumbers,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = onNavigateToCongratulation,
                enabled = group1Controller.allCompleted,
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

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    group2Controller.markCompleted("adding_two_numbers")
                    scope.launch { preferences.markAbacusSectionCompleted("adding_two_numbers") }
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = "0 + 1",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.addingTwoNumbers,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    group2Controller.markCompleted("adding_large_numbers")
                    scope.launch { preferences.markAbacusSectionCompleted("adding_large_numbers") }
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = "8... + 9...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.addingLargeNumbers,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = onNavigateToPracticingAddition,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = "\u7B97\u76E4",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.practicingAddition,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = onNavigateToCongratulation,
                enabled = group2Controller.allCompleted,
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

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = onNavigateToMultiplicationTable,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = "8 \u00D7 8",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.multiplicationTable,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = {
                    group3Controller.markCompleted("abacus_in_rereading")
                    scope.launch { preferences.markAbacusSectionCompleted("abacus_in_rereading") }
                },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = s.abacusInOrdersReading,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.abacusInRereading,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = onNavigateToMultiplyingWithAbacus,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = s.multiplyingWithAbacus,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.multiplyingWithAbacusDescription,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = onNavigateToMultiplyingWithAbacusLevel2,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = "98... \u00D7 9",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.mw2Title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = onNavigateToMultiplyingWithoutLimits,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = "78.. \u00D7 89..",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.mw3Title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = onNavigateToCongratulation,
                enabled = group3Controller.allCompleted,
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