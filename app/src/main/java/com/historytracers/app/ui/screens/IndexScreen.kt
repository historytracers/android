package com.historytracers.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.ContentRepository
import com.historytracers.app.data.ContentResult
import com.historytracers.common.ClassIdx

@Composable
fun IndexScreen(onContentClick: (String) -> Unit) {
    val context = LocalContext.current
    val repo = remember { ContentRepository(context) }
    var index by remember { mutableStateOf<ClassIdx?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        when (val result = repo.loadAndParse("en-US/index")) {
            is ContentResult.IndexClass -> index = result.data
            is ContentResult.Error -> error = result.message
            else -> error = "Unexpected content type"
        }
    }

    error?.let {
        Text("Error: $it", modifier = Modifier.padding(16.dp))
        return
    }

    index?.let { idx ->
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                idx.header?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            idx.content?.forEach { content ->
                item {
                    Text(
                        text = content.id ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                content.value?.let { values ->
                    items(values) { value ->
                        ListItem(
                            headlineContent = { Text(value.name ?: value.id ?: "") },
                            supportingContent = value.desc?.let { { Text(it) } },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onContentClick(value.id ?: return@clickable) }
                        )
                    }
                }
            }
        }
    }
}
