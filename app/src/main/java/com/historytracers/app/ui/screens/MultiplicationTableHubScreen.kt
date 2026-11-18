// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.historytracers.app.R
import com.historytracers.app.data.LevelGroupController
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.AutoSizeTitleText
import com.historytracers.app.ui.features.miscStringsForLanguage
import com.historytracers.app.ui.features.multiplicationTableHubScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.FlagBlueDark
import com.historytracers.app.ui.theme.FlagBlueLight
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

// Points awarded the first time a "Next Level" flag is claimed.
private const val LEVEL_CLAIM_BONUS = 10

@Composable
fun MultiplicationTableHubScreen(
    scrollState: ScrollState = rememberScrollState(),
    onNavigateBack: () -> Unit = {},
    onNavigateToWhatIsIt: () -> Unit = {},
    onNavigateToTheSameResult: () -> Unit = {},
    onNavigateToEqualSides: () -> Unit = {},
    onNavigateToDoubleValues: () -> Unit = {},
    onNavigateToTripleValues: () -> Unit = {},
    onNavigateToFirstSquareNumber: () -> Unit = {},
    onNavigateToMyBody: () -> Unit = {},
    onNavigateToMultiplyingWithFlowers: () -> Unit = {},
    onNavigateToMatchingTables: () -> Unit = {},
    onNavigateToMatchingTables67: () -> Unit = {},
    onNavigateToMatchingTables24: () -> Unit = {},
    onNavigateToWeeksOfLife: () -> Unit = {},
    onNavigateToMultiplyingWithGroupsLevel2: () -> Unit = {},
    onNavigateToMyFirstCube: () -> Unit = {},
    onNavigateToMatchingTables248: () -> Unit = {},
    onNavigateToTheLast: () -> Unit = {},
    onNavigateToMatchingTables39: () -> Unit = {},
    onNavigateToCompleteTheMultiplicationTable: () -> Unit = {},
    onNavigateToPracticingMultiplicationByOneDigitNumbers: () -> Unit = {},
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
    val myBodyDone = "my_body_multiplication_table" in completedMultiplicationTableSections
    val multiplyingWithFlowersDone = "multiplying_with_flowers" in completedMultiplicationTableSections
    val matchingTablesDone = "matching_tables" in completedMultiplicationTableSections
    val matchingTables67Done = "matching_tables_6_7" in completedMultiplicationTableSections
    val matchingTables24Done = "matching_tables_2_4" in completedMultiplicationTableSections
    val weeksOfLifeDone = "weeks_of_life" in completedMultiplicationTableSections
    val multiplyingWithGroupsLevel2Done =
        "multiplying_with_groups_level2" in completedMultiplicationTableSections
    val myFirstCubeDone = "my_first_cube" in completedMultiplicationTableSections
    val matchingTables248Done = "matching_tables_2_4_8" in completedMultiplicationTableSections
    val theLastDone = "the_last" in completedMultiplicationTableSections
    val matchingTables39Done = "matching_tables_3_9" in completedMultiplicationTableSections
    val completeTheMultiplicationTableDone =
        "complete_the_multiplication_table" in completedMultiplicationTableSections
    val practicingMultiplicationDone =
        "practicing_multiplication_by_one_digit_numbers" in completedMultiplicationTableSections

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

    // Second "Next Level" group: every class after the first flag button.
    val levelGroup2Sections = remember {
        listOf(
            "first_square_number",
            "matching_tables_2_4",
            "my_body_multiplication_table",
            "multiplying_with_flowers",
            "matching_tables",
            "weeks_of_life",
            "matching_tables_6_7",
            "multiplying_with_groups_level2"
        )
    }
    val levelGroup2Controller = remember {
        LevelGroupController(levelGroup2Sections, completedMultiplicationTableSections)
    }
    LaunchedEffect(completedMultiplicationTableSections) {
        levelGroup2Controller.syncFromPersisted(completedMultiplicationTableSections)
    }
    val nextLevel2Unlocked = levelGroup2Sections.all { it in completedMultiplicationTableSections }

    // Third "Next Level" group: every class after the second flag button, up to
    // this new flag (the whole end of the hub).
    val levelGroup3Sections = remember {
        listOf(
            "my_first_cube",
            "matching_tables_2_4_8",
            "the_last",
            "matching_tables_3_9",
            "complete_the_multiplication_table"
        )
    }
    val levelGroup3Controller = remember {
        LevelGroupController(levelGroup3Sections, completedMultiplicationTableSections)
    }
    LaunchedEffect(completedMultiplicationTableSections) {
        levelGroup3Controller.syncFromPersisted(completedMultiplicationTableSections)
    }
    val nextLevel3Unlocked = levelGroup3Sections.all { it in completedMultiplicationTableSections }

    val claimedLevels by preferences.claimedLevels.collectAsState(initial = emptySet())

    fun claimLevel(levelId: String) {
        scope.launch {
            // Transactional + idempotent: only the first claim wins, so rapid
            // taps can never award the bonus twice. The bonus is added to the
            // stored score inside the same write, so a newer score is kept.
            preferences.claimLevel(levelId, LEVEL_CLAIM_BONUS)?.let { total ->
                onScoreChanged(total)
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
                AutoSizeTitleText(
                    text = mcs.multiplicationTable,
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

                Spacer(Modifier.height(48.dp))

                // "Matching the Tables of 2 and 4" (4 × N = 2 × (2 × N)).
                FilledIconButton(
                    onClick = onNavigateToMatchingTables24,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.matchingTables2And4 },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (matchingTables24Done) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "2 \u00d7 2 \u00d7 N\n= 4 \u00d7 N",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.matchingTables2And4,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "My Body, as a Multiplication Table" (multiplication table of five).
                FilledIconButton(
                    onClick = onNavigateToMyBody,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.myBodyAsMultiplicationTable },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (myBodyDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "5",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.myBodyAsMultiplicationTable,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Multiplying with Flowers" (multiplication table of six).
                FilledIconButton(
                    onClick = onNavigateToMultiplyingWithFlowers,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.multiplyingWithFlowers },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (multiplyingWithFlowersDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "6",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.multiplyingWithFlowers,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Matching the Tables of 2, 3, and 6" (commutativity bridge:
                // 2 × 3 = 3 × 2 = 6).
                FilledIconButton(
                    onClick = onNavigateToMatchingTables,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.matchingTables },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (matchingTablesDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "2 \u00d7 3\n= 3 \u00d7 2",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.matchingTables,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Weeks of Life" (multiplication table of seven).
                FilledIconButton(
                    onClick = onNavigateToWeeksOfLife,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.weeksOfLife },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (weeksOfLifeDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "7",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.weeksOfLife,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Matching the Tables of 6 and 7" (7 × N = 6 × N + N).
                FilledIconButton(
                    onClick = onNavigateToMatchingTables67,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.matchingTables6And7 },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (matchingTables67Done) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "6 \u00d7 N + N\n= 7 \u00d7 N",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.matchingTables6And7,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Multiplying with Groups (4 to 7)".
                FilledIconButton(
                    onClick = onNavigateToMultiplyingWithGroupsLevel2,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.multiplyingWithGroupsLevel2 },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (multiplyingWithGroupsLevel2Done) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "\u00D7 4, 5\n6, 7",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.multiplyingWithGroupsLevel2,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // Second "Next Level" flag button: unlocks when every class
                // between the first flag and this one is completed.
                FilledIconButton(
                    onClick = {
                        claimLevel("multiplication_table_group2")
                        onNavigateToCongratulation()
                    },
                    enabled = nextLevel2Unlocked,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = s.common.nextLevel },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("multiplication_table_group2" in claimedLevels) FlagBlueDark else FlagBlueLight,
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

                // "My First Cube" (multiplication table of eight).
                FilledIconButton(
                    onClick = onNavigateToMyFirstCube,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.myFirstCube },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (myFirstCubeDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "8",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.myFirstCube,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Matching the Tables of 2, 4 and 8" (8 × N = 2 × 2 × 2 × N = 4 × 2 × N).
                FilledIconButton(
                    onClick = onNavigateToMatchingTables248,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.matchingTables2And4And8 },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (matchingTables248Done) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "2 \u00D7 2 \u00D7 2 \u00D7 N =\n4 \u00D7 2 \u00D7 N =\n8 \u00D7 N",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.matchingTables2And4And8,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "The Last" (multiplication table of nine).
                FilledIconButton(
                    onClick = onNavigateToTheLast,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.theLast },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (theLastDone) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "9",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.theLast,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Matching the Tables of 3 and 9" (9 × N = 3 × 3 × N).
                FilledIconButton(
                    onClick = onNavigateToMatchingTables39,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.matchingTables3And9 },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (matchingTables39Done) ButtonYellowDark else ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "3 \u00D7 3 \u00D7 N =\n9 \u00D7 N",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.matchingTables3And9,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Complete the Multiplication Table".
                FilledIconButton(
                    onClick = onNavigateToCompleteTheMultiplicationTable,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = hss.completeTheMultiplicationTable },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (completeTheMultiplicationTableDone) {
                            ButtonYellowDark
                        } else {
                            ButtonYellow
                        },
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = "\u00D7",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.completeTheMultiplicationTable,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // Third "Next Level" flag button: unlocks when every class
                // between the previous flag and this one is completed.
                FilledIconButton(
                    onClick = {
                        claimLevel("multiplication_table_group3")
                        onNavigateToCongratulation()
                    },
                    enabled = nextLevel3Unlocked,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = s.common.nextLevel },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("multiplication_table_group3" in claimedLevels) FlagBlueDark else FlagBlueLight,
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

                // "Practicing Multiplication by One-Digit Numbers": the product
                // 2 x 9 written in the long-multiplication layout (first factor,
                // then the one-digit factor under a bar).
                FilledIconButton(
                    onClick = onNavigateToPracticingMultiplicationByOneDigitNumbers,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics {
                            contentDescription = hss.practicingMultiplicationByOneDigitNumbers
                        },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (practicingMultiplicationDone) {
                            ButtonYellowDark
                        } else {
                            ButtonYellow
                        },
                        contentColor = OnButtonYellow
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "2",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnButtonYellow
                        )
                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "\u00D7 9",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = OnButtonYellow
                            )
                            // Bar under the one-digit factor, like the line a
                            // child draws before writing the product.
                            Box(
                                modifier = Modifier
                                    .padding(top = 1.dp)
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(OnButtonYellow)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.practicingMultiplicationByOneDigitNumbers,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                // "Practicing Multiplication with Typed Values": 4 x 4 written
                // in the long-multiplication layout, with two empty boxes below
                // the bar where the product digits are typed.
                FilledIconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .size(96.dp)
                        .semantics {
                            contentDescription = hss.practicingMultiplicationWithTypedValues
                        },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "4",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnButtonYellow
                        )
                        Column(
                            modifier = Modifier.width(IntrinsicSize.Max),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "\u00D7 4",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = OnButtonYellow
                            )
                            // Bar under the one-digit factor, like the line a
                            // child draws before writing the product.
                            Box(
                                modifier = Modifier
                                    .padding(top = 1.dp)
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(OnButtonYellow)
                            )
                            // One box per digit of the product (4 x 4 = 16).
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                repeat(2) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = 15.dp, height = 13.dp)
                                            .border(
                                                width = 1.5.dp,
                                                color = OnButtonYellow,
                                                shape = RoundedCornerShape(2.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hss.practicingMultiplicationWithTypedValues,
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
