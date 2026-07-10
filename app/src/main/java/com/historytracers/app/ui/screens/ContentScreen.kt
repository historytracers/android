// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import com.historytracers.app.ui.UiStrings
import com.historytracers.app.ui.components.DateUtils
import com.historytracers.app.ui.components.TextRenderer
import com.historytracers.common.*

@Composable
fun ContentScreen(
    fileName: String,
    language: String,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val s = LocalUiStrings.current
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    var result by remember { mutableStateOf<ContentResult?>(null) }

    LaunchedEffect(fileName, language) {
        result = repo.loadAndParse("$language/$fileName")
    }

    LaunchedEffect(result) {
        if (result != null) {
            val prefs = UserPreferences(context)
            prefs.recordLessonCompletion()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NavigationBar(
            onBack = onNavigateBack,
            onHome = onNavigateHome,
            backLabel = s.back,
            homeLabel = s.home
        )

        when (val res = result) {
            null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is ContentResult.Error -> Text(
                "${s.error}: ${res.message}",
                modifier = Modifier.padding(16.dp)
            )
            is ContentResult.ClassContent -> ClassScreen(res.data, repo, s)
            is ContentResult.AtlasContent -> AtlasScreen(res.data, repo, s)
            is ContentResult.FamilyTree -> FamilyTreeScreen(res.data, repo, s)
            is ContentResult.SMGame -> SMGameScreen(res.data, repo, s)
            is ContentResult.IndexClass -> IndexContentScreen(res.data, repo, s)
            else -> Text("${s.error}: unsupported content type", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun NavigationBar(onBack: () -> Unit, onHome: () -> Unit, backLabel: String, homeLabel: String) {
    Surface(
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = backLabel)
            }
            IconButton(onClick = onHome) {
                Icon(Icons.Default.Home, contentDescription = homeLabel)
            }
        }
    }
}

@Composable
private fun ClassScreen(data: ClassTemplateFile, repo: ContentRepository, s: UiStrings) {
    LaunchedEffect(data.sources) { repo.loadSources(data.sources) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = data.title ?: "", style = MaterialTheme.typography.titleLarge)
        data.header?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, style = MaterialTheme.typography.bodyLarge)
        }

        data.content?.forEach { content ->
            content.text?.forEach { text ->
                Spacer(Modifier.height(8.dp))
                TextRenderer(text = text, repo = repo)
            }
        }

        data.exercises?.let { exercises ->
            Spacer(Modifier.height(16.dp))
            Text(
                s.exercises,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            exercises.forEach { exercise ->
                ExerciseCard(exercise, s)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ExerciseCard(exercise: HTExercise, s: UiStrings) {
    var answered by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = exercise.question ?: "",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))

            if (!answered) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            isCorrect = exercise.yesNoAnswer == "yes"
                            answered = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButtonYellow
                        )
                    ) {
                        Text(s.yes)
                    }
                    OutlinedButton(
                        onClick = {
                            isCorrect = exercise.yesNoAnswer == "no"
                            answered = true
                        }
                    ) {
                        Text(s.no)
                    }
                }
            } else {
                Text(
                    text = if (isCorrect) s.correct else s.incorrect,
                    color = if (isCorrect) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                exercise.additionalInfo?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(4.dp))
                OutlinedButton(
                    onClick = {
                        answered = false
                        isCorrect = false
                    }
                ) {
                    Text(s.retry)
                }
            }
        }
    }
}

@Composable
private fun AtlasScreen(data: AtlasTemplateFile, repo: ContentRepository, s: UiStrings) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = data.title ?: "", style = MaterialTheme.typography.titleLarge)
        data.content?.forEach { content ->
            content.text?.forEach { text ->
                Spacer(Modifier.height(8.dp))
                TextRenderer(text = text, repo = repo)
            }
        }
    }
}

@Composable
private fun FamilyTreeScreen(data: Family, repo: ContentRepository, s: UiStrings) {
    LaunchedEffect(data.sources) { repo.loadSources(data.sources) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = data.title ?: "", style = MaterialTheme.typography.titleLarge)
        data.header?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, style = MaterialTheme.typography.bodyLarge)
        }

        data.families?.forEach { family ->
            Spacer(Modifier.height(16.dp))
            FamilySection(family, repo, s)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FamilySection(family: FamilyBody, repo: ContentRepository, s: UiStrings) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = family.name ?: family.id ?: "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (expanded) {
                family.history?.forEach { text ->
                    Spacer(Modifier.height(4.dp))
                    TextRenderer(text = text, repo = repo)
                }
                family.people?.forEach { person ->
                    Spacer(Modifier.height(8.dp))
                    PersonSection(person, repo, s)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonSection(person: FamilyPerson, repo: ContentRepository, s: UiStrings) {
    var showDetails by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { showDetails = !showDetails }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = person.name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (showDetails) "\u25B2" else "\u25BC",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (showDetails) {
                person.history?.forEach { text ->
                    Spacer(Modifier.height(4.dp))
                    TextRenderer(text = text, repo = repo)
                }
                person.birth?.forEach { event ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${s.birth}: ${event.date?.let { DateUtils.formatDate(it) } ?: s.unknown}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                person.death?.forEach { event ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${s.death}: ${event.date?.let { DateUtils.formatDate(it) } ?: s.unknown}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun SMGameScreen(data: SMGameFile, repo: ContentRepository, s: UiStrings) {
    val contentList = data.content ?: emptyList()
    val contentMap = remember(contentList) { contentList.associateBy { it.id } }
    val levelMap = remember(data) {
        data.levels?.associateBy { it.id } ?: emptyMap()
    }

    var currentNodeId by remember { mutableStateOf(contentList.firstOrNull()?.id) }
    var score by remember { mutableIntStateOf(0) }
    var answeredNodes by remember { mutableStateOf(setOf<String>()) }
    var selectedLevelId by remember { mutableStateOf<String?>(null) }

    val currentNode = currentNodeId?.let { contentMap[it] }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${s.score}: $score",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (contentList.size > 0 && currentNodeId != null) {
                        "${s.step} ${contentList.indexOfFirst { it.id == currentNodeId } + 1}/${contentList.size}"
                    } else "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (selectedLevelId == null && data.levels != null && data.levels.isNotEmpty()) {
            LevelSelectionScreen(
                levels = data.levels,
                onSelectLevel = { levelId ->
                    selectedLevelId = levelId
                    val level = levelMap[levelId]
                    currentNodeId = level?.loadID ?: level?.id ?: contentList.firstOrNull()?.id
                },
                s = s
            )
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                currentNode?.text?.forEach { text ->
                    TextRenderer(text = text, repo = repo)
                }

                currentNode?.let { node ->
                    if (node.answer != null && node.id !in answeredNodes) {
                        Spacer(Modifier.height(16.dp))
                        AnswerSection(
                            answer = node.answer,
                            nodeId = node.id,
                            scoreValue = node.score,
                            onCorrect = {
                                score += node.score
                                answeredNodes = answeredNodes + node.id
                            },
                            s = s
                        )
                    }
                }
            }

            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    currentNode?.prev?.let { prevId ->
                        Button(
                            onClick = { currentNodeId = prevId },
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) {
                            Text(s.previous)
                        }
                    }
                    currentNode?.jumpTo?.let { jumpId ->
                        OutlinedButton(
                            onClick = { currentNodeId = jumpId },
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                        ) {
                            Text(s.jump)
                        }
                    }
                    currentNode?.next?.let { nextId ->
                        Button(
                            onClick = { currentNodeId = nextId },
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) {
                            Text(s.next)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelSelectionScreen(
    levels: List<SMGameLevel>,
    onSelectLevel: (String) -> Unit,
    s: UiStrings
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = s.selectLevel,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))
        levels.filter { it.id != null }.forEach { level ->
            Button(
                onClick = { onSelectLevel(level.id!!) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(level.name ?: level.id ?: "")
            }
            level.desc?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AnswerSection(
    answer: Any?,
    nodeId: String,
    scoreValue: Int,
    onCorrect: () -> Unit,
    s: UiStrings
) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var hasSubmitted by remember { mutableStateOf(false) }

    val correctAnswer = when (answer) {
        is String -> answer.lowercase()
        is List<*> -> answer.firstOrNull()?.toString()?.lowercase()
        else -> answer?.toString()?.lowercase()
    }

    val options = when (answer) {
        is List<*> -> answer.map { it.toString() }
        is String -> listOf(answer)
        else -> listOf(answer?.toString() ?: "")
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${s.answer} (+$scoreValue pts)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            if (!hasSubmitted) {
                options.forEach { option ->
                    Button(
                        onClick = {
                            selectedAnswer = option.lowercase()
                            hasSubmitted = true
                            if (selectedAnswer == correctAnswer) {
                                onCorrect()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedAnswer == option.lowercase())
                                ButtonYellow
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(option)
                    }
                }
            } else {
                val isCorrect = selectedAnswer == correctAnswer
                Text(
                    text = if (isCorrect) "${s.correct} +$scoreValue pts"
                    else "${s.incorrect}. ${s.answer}: $correctAnswer",
                    color = if (isCorrect) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                OutlinedButton(onClick = {
                    selectedAnswer = null
                    hasSubmitted = false
                }) {
                    Text(s.tryAgain)
                }
            }
        }
    }
}

@Composable
private fun IndexContentScreen(data: ClassIdx, repo: ContentRepository, s: UiStrings) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = data.title ?: "", style = MaterialTheme.typography.titleLarge)
        data.header?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, style = MaterialTheme.typography.bodyLarge)
        }

        data.content?.forEach { content ->
            Spacer(Modifier.height(12.dp))
            content.id?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            content.value?.forEach { value ->
                Spacer(Modifier.height(4.dp))
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = value.name ?: value.id ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        value.desc?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
