// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.UiStrings
import com.historytracers.app.ui.theme.SkinColorPalette
import com.historytracers.app.ui.theme.parseHexColor

private fun availableLanguages(s: UiStrings) = listOf(
    "en-US" to s.titles.langEnUs,
    "pt-BR" to s.titles.langPtBr,
    "es-ES" to s.titles.langEsEs
)

private val availableBreakTimes = listOf(15, 25, 30, 35, 45, 50, 60)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentLanguage: String,
    currentBreakTime: Int,
    currentSkinColor: String,
    onLanguageChanged: (String) -> Unit,
    onBreakTimeChanged: (Int) -> Unit,
    onSkinColorChanged: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val s = LocalUiStrings.current
    var languageExpanded by remember { mutableStateOf(false) }
    var breakTimeExpanded by remember { mutableStateOf(false) }

    val selectedLangLabel = availableLanguages(s).firstOrNull { it.first == currentLanguage }?.second
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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.common.back)
                }
                Text(
                    text = s.common.settings,
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
                text = s.common.language,
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
                    availableLanguages(s).forEach { (code, label) ->
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
                text = s.common.breakTime,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = s.common.breakTimeDesc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ExposedDropdownMenuBox(
                expanded = breakTimeExpanded,
                onExpandedChange = { breakTimeExpanded = it }
            ) {
                OutlinedTextField(
                    value = "$currentBreakTime ${s.common.minutes}",
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
                            text = { Text("$minutes ${s.common.minutes}") },
                            onClick = {
                                onBreakTimeChanged(minutes)
                                breakTimeExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            Text(
                text = s.common.skinColor,
                style = MaterialTheme.typography.titleMedium
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SkinColorPalette) { (color, hex) ->
                    val isSelected = currentSkinColor.equals(hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color, CircleShape)
                            .then(
                                if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                else Modifier
                            )
                            .clickable { onSkinColorChanged(hex) }
                    )
                }
            }
        }
    }
}
