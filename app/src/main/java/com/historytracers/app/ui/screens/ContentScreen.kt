// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.app.ui.components.TextRenderer

@Composable
fun ContentScreen(fileName: String) {
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    var result by remember { mutableStateOf<ContentResult?>(null) }

    LaunchedEffect(fileName) {
        result = repo.loadAndParse("en-US/$fileName")
    }

    when (val res = result) {
        null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is ContentResult.Error -> Text("Error: ${res.message}", modifier = Modifier.padding(16.dp))
        is ContentResult.ClassContent -> ClassScreen(res.data, repo)
        is ContentResult.AtlasContent -> AtlasScreen(res.data, repo)
        is ContentResult.FamilyTree -> FamilyTreeScreen(res.data, repo)
        is ContentResult.SMGame -> SMGameScreen(res.data, repo)
        is ContentResult.IndexClass -> IndexContentScreen(res.data, repo)
        else -> Text("Unsupported content type", modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun ClassScreen(data: com.historytracers.common.ClassTemplateFile, repo: ContentRepository) {
    LaunchedEffect(data.sources) { repo.loadSources(data.sources) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(text = data.title ?: "", style = MaterialTheme.typography.titleLarge)
        data.header?.let { Spacer(Modifier.height(8.dp)); Text(it, style = MaterialTheme.typography.bodyLarge) }

        data.content?.forEach { content ->
            content.text?.forEach { text ->
                TextRenderer(text = text, repo = repo)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AtlasScreen(data: com.historytracers.common.AtlasTemplateFile, repo: ContentRepository) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(text = data.title ?: "", style = MaterialTheme.typography.titleLarge)
        data.content?.forEach { content ->
            content.text?.forEach { text ->
                TextRenderer(text = text, repo = repo)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FamilyTreeScreen(data: com.historytracers.common.Family, repo: ContentRepository) {
    LaunchedEffect(data.sources) { repo.loadSources(data.sources) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(text = data.title ?: "", style = MaterialTheme.typography.titleLarge)
        data.header?.let { Spacer(Modifier.height(8.dp)); Text(it, style = MaterialTheme.typography.bodyLarge) }

        data.families?.forEach { family ->
            Spacer(Modifier.height(16.dp))
            Text(text = family.name ?: family.id ?: "", style = MaterialTheme.typography.titleMedium)

            family.people?.forEach { person ->
                Spacer(Modifier.height(4.dp))
                Text(text = person.name ?: "", style = MaterialTheme.typography.bodyLarge)
                person.history?.forEach { text -> TextRenderer(text = text, repo = repo) }
            }
        }
    }
}

@Composable
private fun SMGameScreen(data: com.historytracers.common.SMGameFile, repo: ContentRepository) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp)) {
        Text(text = "Scientific Method Game", style = MaterialTheme.typography.titleLarge)

        data.content?.forEach { content ->
            Spacer(Modifier.height(12.dp))
            content.text?.forEach { text ->
                TextRenderer(text = text, repo = repo)
            }
            content.audio?.let { Text("Audio: $it", style = MaterialTheme.typography.bodySmall) }
        }
    }
}

@Composable
private fun IndexContentScreen(data: com.historytracers.common.ClassIdx, repo: ContentRepository) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text(text = data.title ?: "", style = MaterialTheme.typography.titleLarge)
        data.header?.let { Spacer(Modifier.height(8.dp)); Text(it, style = MaterialTheme.typography.bodyLarge) }

        data.content?.forEach { content ->
            content.value?.forEach { value ->
                Spacer(Modifier.height(4.dp))
                Text(text = value.name ?: value.id ?: "", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
