// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.historytracers.app.R
import com.historytracers.app.data.LevelGroupController
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.miscStringsForLanguage
import com.historytracers.app.ui.features.multiplicationTableHubScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.FlagBlueDark
import com.historytracers.app.ui.theme.FlagBlueLight
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

@Composable
fun MultiplicationTableHubScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToWhatIsIt: () -> Unit = {},
    onNavigateToTheSameResult: () -> Unit = {},
    onNavigateToEqualSides: () -> Unit = {},
    onNavigateToDoubleValues: () -> Unit = {},
    onNavigateToTripleValues: () -> Unit = {},
    onNavigateToFirstSquareNumber: () -> Unit = {},
    onNavigateToMultiplyingWithGroups: () -> Unit = {},
    onNavigateToCongratulation: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val mcs = miscStringsForLanguage(LocalAppLanguage.current)
    val hss = multiplicationTableHubScreenStringsForLanguage(LocalAppLanguage.current)

    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val completedMultiplicationTableSections by preferences.completedMultiplicationTableSections.collectAsState(
        initial = emptySet()
    )
    val whatIsItDone = "what_is_it" in completedMultiplicationTableSections
    val theSameResultDone = "the_same_result" in completedMultiplicationTableSections
    val equalSidesDone = "equal_sides" in completedMultiplicationTableSections
    val doubleValuesDone = "double_values" in completedMultiplicationTableSections
    val tripleValuesDone = "triple_values" in completedMultiplicationTableSections
    val multiplyingWithGroupsDone = "multiplying_with_groups" in completedMultiplicationTableSections
    val firstSquareNumberDone = "first_square_number" in completedMultiplicationTableSections

    // One "Next Level" flag button covering every class in this hub.
    val levelGroupSections = remember {
        listOf(
            "what_is_it",
            "the_same_result",
            "equal_sides",
            "double_values",
            "triple_values",
            "multiplying_with_groups"
        )
    }
    val levelGroupController = remember {
        LevelGroupController(levelGroupSections, completedMultiplicationTableSections)
    }
    LaunchedEffect(completedMultiplicationTableSections) {
        levelGroupController.syncFromPersisted(completedMultiplicationTableSections)
    }
    // Compose-observed gate: derives straight from the collected persisted set
    // so the button unlocks as soon as the sixth section is completed.
    val nextLevelUnlocked = levelGroupSections.all { it in completedMultiplicationTableSections }

    val claimedLevels by preferences.claimedLevels.collectAsState(initial = emptySet())

    fun claimLevel(levelId: String) {
        scope.launch {
            // Transactional + idempotent: only the first claim wins, so rapid
            // taps can never award the bonus twice.
            if (preferences.claimLevel(levelId, currentScore + 10)) {
                onScoreChanged(currentScore + 10)
            }
        }
    }

    var showResetMenu by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

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
                    text = mcs.multiplicationTable,
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
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                FilledIconButton(
                    onClick = onNavigateToWhatIsIt,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.whatIsIt },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (whatIsItDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.whatIsIt,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "The Same Result" (multiplication table of zero).
                FilledIconButton(
                    onClick = onNavigateToTheSameResult,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.theSameResult },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (theSameResultDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.theSameResult,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Equal Sides" (multiplication table of one).
                FilledIconButton(
                    onClick = onNavigateToEqualSides,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.equalSides },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (equalSidesDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "1",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.equalSides,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Double Values" (multiplication table of two).
                FilledIconButton(
                    onClick = onNavigateToDoubleValues,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.doubleValues },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (doubleValuesDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "2",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.doubleValues,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Triple Values" (multiplication table of three).
                FilledIconButton(
                    onClick = onNavigateToTripleValues,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.tripleValues },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (tripleValuesDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "3",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.tripleValues,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Multiplying with Groups (0 to 3)".
                FilledIconButton(
                    onClick = onNavigateToMultiplyingWithGroups,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.multiplyingWithGroups },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (multiplyingWithGroupsDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "\u00D7 0, 1\n2, 3",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.multiplyingWithGroups,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Next Level" flag button: unlocks when every class above is
                // completed and awards its bonus points once.
                FilledIconButton(
                    onClick = {
                        claimLevel("multiplication_table_group1")
                        onNavigateToCongratulation()
                    },
                    enabled = nextLevelUnlocked,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = s.common.nextLevel },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("multiplication_table_group1" in claimedLevels) FlagBlueDark else FlagBlueLight,
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
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "The First Square Number" (multiplication table of four).
                FilledIconButton(
                    onClick = onNavigateToFirstSquareNumber,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.firstSquareNumber },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (firstSquareNumberDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "4",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.firstSquareNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
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
