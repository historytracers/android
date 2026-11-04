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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.R
import com.historytracers.app.data.LevelGroupController
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.longDivisionScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.FlagBlueDark
import com.historytracers.app.ui.theme.FlagBlueLight
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val DIVISION_AND_REMAINDER_SECTION_ID = "division_and_remainder"
private const val WRITING_THE_QUOTIENT_SECTION_ID = "writing_the_quotient"
private const val WRITING_QUOTIENT_AND_REMAINDER_SECTION_ID = "writing_quotient_and_remainder"
private const val CHOOSING_DIVIDEND_AND_DIVISOR_SECTION_ID = "choosing_dividend_and_divisor"
private const val MULTIPLICATION_AND_DIVISION_RELATIONSHIP_SECTION_ID = "multiplication_and_division_relationship"
private const val LONG_DIVISION_LEVEL_ID = "long_division"

private val LONG_DIVISION_SECTION_IDS = listOf(
    MULTIPLICATION_AND_DIVISION_RELATIONSHIP_SECTION_ID,
    DIVISION_AND_REMAINDER_SECTION_ID,
    WRITING_THE_QUOTIENT_SECTION_ID,
    WRITING_QUOTIENT_AND_REMAINDER_SECTION_ID,
    CHOOSING_DIVIDEND_AND_DIVISOR_SECTION_ID
)

@Composable
fun LongDivisionScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToCongratulation: () -> Unit = {},
    onNavigateToDivisionAndRemainder: () -> Unit = {},
    onNavigateToWritingTheQuotient: () -> Unit = {},
    onNavigateToWritingQuotientAndRemainder: () -> Unit = {},
    onNavigateToChoosingDividendAndDivisor: () -> Unit = {},
    onNavigateToMultiplicationAndDivisionRelationship: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val xs = longDivisionScreenStringsForLanguage(LocalAppLanguage.current)

    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val completedLongDivision by preferences.completedLongDivisionSections.collectAsState(initial = emptySet())
    val divisionAndRemainderDone = DIVISION_AND_REMAINDER_SECTION_ID in completedLongDivision
    val writingTheQuotientDone = WRITING_THE_QUOTIENT_SECTION_ID in completedLongDivision
    val writingQuotientAndRemainderDone = WRITING_QUOTIENT_AND_REMAINDER_SECTION_ID in completedLongDivision
    val choosingDividendAndDivisorDone = CHOOSING_DIVIDEND_AND_DIVISOR_SECTION_ID in completedLongDivision
    val multiplicationAndDivisionRelationshipDone =
        MULTIPLICATION_AND_DIVISION_RELATIONSHIP_SECTION_ID in completedLongDivision

    val levelGroupController = remember {
        LevelGroupController(
            LONG_DIVISION_SECTION_IDS,
            completedLongDivision
        )
    }
    LaunchedEffect(completedLongDivision) {
        levelGroupController.syncFromPersisted(completedLongDivision)
    }
    val levelGroupCompleted by levelGroupController.completed.collectAsState()
    val allLevelGroupSectionsCompleted = LONG_DIVISION_SECTION_IDS.all { it in levelGroupCompleted }

    val claimedLevels by preferences.claimedLevels.collectAsState(initial = emptySet())
    var claimingLevel by remember { mutableStateOf(false) }

    fun claimLevel(levelId: String) {
        if (claimingLevel || levelId in claimedLevels) return
        claimingLevel = true
        scope.launch {
            val claimed = withContext(NonCancellable) {
                preferences.claimLevel(levelId, currentScore + 10)
            }
            claimingLevel = false
            if (claimed) {
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
                    text = hts.longDivision,
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
                    onClick = onNavigateToMultiplicationAndDivisionRelationship,
                    modifier = Modifier
                        .size(112.dp)
                        .semantics { contentDescription = xs.multiplicationAndDivisionRelationship },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (multiplicationAndDivisionRelationshipDone) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Text(
                        text = xs.multiplicationAndDivisionExample,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = OnButtonYellow
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.multiplicationAndDivisionRelationship,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToDivisionAndRemainder,
                    modifier = Modifier.size(112.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (divisionAndRemainderDone) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Text(
                        text = xs.divisionExample,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = OnButtonYellow
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.divisionAndRemainder,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToWritingTheQuotient,
                    modifier = Modifier.size(112.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (writingTheQuotientDone) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Text(
                        text = xs.writingTheQuotientExample,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = OnButtonYellow
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.writingTheQuotient,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToWritingQuotientAndRemainder,
                    modifier = Modifier.size(112.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (writingQuotientAndRemainderDone) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Text(
                        text = xs.writingQuotientAndRemainderExample,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = OnButtonYellow
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.writingQuotientAndRemainder,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToChoosingDividendAndDivisor,
                    modifier = Modifier.size(112.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (choosingDividendAndDivisorDone) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Text(
                        text = xs.choosingDividendAndDivisorExample,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = OnButtonYellow
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.choosingDividendAndDivisor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = {
                        claimLevel(LONG_DIVISION_LEVEL_ID)
                        onNavigateToCongratulation()
                    },
                    enabled = allLevelGroupSectionsCompleted,
                    modifier = Modifier.size(112.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (LONG_DIVISION_LEVEL_ID in claimedLevels) FlagBlueDark else FlagBlueLight,
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
