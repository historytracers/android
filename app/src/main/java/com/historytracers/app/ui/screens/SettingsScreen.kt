// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.historytracers.app.ui.LocalUiStrings

private val availableLanguages = listOf(
    "en-US" to "English (US)",
    "pt-BR" to "Portugu\u00eas (BR)",
    "es-ES" to "Espa\u00f1ol (ES)"
)

private val availableBreakTimes = listOf(15, 25, 30, 35, 45, 50, 60)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentLanguage: String,
    currentBreakTime: Int,
    onLanguageChanged: (String) -> Unit,
    onBreakTimeChanged: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val s = LocalUiStrings.current
    var languageExpanded by remember { mutableStateOf(false) }
    var breakTimeExpanded by remember { mutableStateOf(false) }

    val selectedLangLabel = availableLanguages.firstOrNull { it.first == currentLanguage }?.second
        ?: currentLanguage

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
                    text = s.settings,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = s.language,
                style = MaterialTheme.typography.titleMedium
            )

            ExposedDropdownMenuBox(
                expanded = languageExpanded,
                onExpandedChange = { languageExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedLangLabel,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = languageExpanded,
                    onDismissRequest = { languageExpanded = false }
                ) {
                    availableLanguages.forEach { (code, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                onLanguageChanged(code)
                                languageExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            Text(
                text = s.breakTime,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = s.breakTimeDesc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ExposedDropdownMenuBox(
                expanded = breakTimeExpanded,
                onExpandedChange = { breakTimeExpanded = it }
            ) {
                OutlinedTextField(
                    value = "$currentBreakTime ${s.minutes}",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = breakTimeExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = breakTimeExpanded,
                    onDismissRequest = { breakTimeExpanded = false }
                ) {
                    availableBreakTimes.forEach { minutes ->
                        DropdownMenuItem(
                            text = { Text("$minutes ${s.minutes}") },
                            onClick = {
                                onBreakTimeChanged(minutes)
                                breakTimeExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
