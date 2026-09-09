// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.HubTitleStrings
import com.historytracers.app.ui.features.SettingsScreenStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.settingsScreenStringsForLanguage
import com.historytracers.app.ui.theme.SkinColorPalette
import com.historytracers.app.ui.theme.parseHexColor
import com.historytracers.app.calendar.CalendarType

private fun availableLanguages(hts: HubTitleStrings) = listOf(
    "en-US" to hts.langEnUs,
    "pt-BR" to hts.langPtBr,
    "es-ES" to hts.langEsEs
)

private val availableBreakTimes = listOf(15, 25, 30, 35, 45, 50, 60)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentLanguage: String,
    currentBreakTime: Int,
    currentSkinColor: String,
    currentCalendar: String,
    onLanguageChanged: (String) -> Unit,
    onBreakTimeChanged: (Int) -> Unit,
    onSkinColorChanged: (String) -> Unit,
    onCalendarChanged: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onStartLearning: (() -> Unit)? = null
) {
    val s = LocalUiStrings.current
    val xs = settingsScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    var languageExpanded by remember { mutableStateOf(false) }
    var breakTimeExpanded by remember { mutableStateOf(false) }

    val selectedLangLabel = availableLanguages(hts).firstOrNull { it.first == currentLanguage }?.second
        ?: currentLanguage

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
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
                    .verticalScroll(scrollState)
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
                        availableLanguages(hts).forEach { (code, label) ->
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
                    text = s.common.calendar,
                    style = MaterialTheme.typography.titleMedium
                )

                val calendarListState = rememberLazyListState()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    LazyColumn(state = calendarListState) {
                        items(CalendarType.entries) { calendar ->
                            val isSelected = currentCalendar == calendar.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = isSelected,
                                        onClick = { onCalendarChanged(calendar.id) },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = null
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = calendarLabel(calendar, xs),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    CalendarScrollbar(
                        listState = calendarListState,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 2.dp)
                            .fillMaxHeight()
                    )
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

                onStartLearning?.let { onStart ->
                    Spacer(Modifier.height(8.dp))
                    Divider()
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onStart,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(xs.startLearning)
                    }
                }
            }
        }

        VerticalScrollbar(
            scrollState = scrollState,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 2.dp)
        )
    }
}

private fun calendarLabel(calendar: CalendarType, xs: SettingsScreenStrings): String = when (calendar) {
    CalendarType.GREGORIAN -> xs.calGregorian
    CalendarType.JULIAN -> xs.calJulian
    CalendarType.HEBREW -> xs.calHebrew
    CalendarType.ISLAMIC -> xs.calIslamic
    CalendarType.PERSIAN -> xs.calPersian
    CalendarType.PERSIAN_ASTRONOMICAL -> xs.calShaka
    CalendarType.MAYAN -> xs.calMesoamerican
    CalendarType.MAYAN_EXTENDED -> xs.calMesoamericanExt
    CalendarType.HISPANIC -> xs.calHispanic
    CalendarType.INDIAN_CIVIL -> xs.calIndianCivil
    CalendarType.FRENCH_REPUBLICAN -> xs.calFrenchRepublican
    CalendarType.CHINESE -> xs.calChinese
    CalendarType.AYMARA -> xs.calAymara
    CalendarType.MAPUCHE -> xs.calMapuche
    CalendarType.INCA -> xs.calInca
    CalendarType.JAVANESE -> xs.calJavanese
    CalendarType.JAPANESE -> xs.calJapanese
}

@Composable
private fun CalendarScrollbar(
    listState: androidx.compose.foundation.lazy.LazyListState,
    modifier: Modifier = Modifier
) {
    val itemCount = listState.layoutInfo.totalItemsCount
    if (itemCount <= 0) return

    val firstVisible = listState.firstVisibleItemIndex
    val visibleCount = listState.layoutInfo.visibleItemsInfo.size
    if (itemCount <= visibleCount) return

    val scrollbarAlpha by animateFloatAsState(
        targetValue = if (listState.isScrollInProgress) 1f else 0.4f,
        label = "calendarScrollbarAlpha"
    )

    val fraction = firstVisible.toFloat() / (itemCount - visibleCount).coerceAtLeast(1)
    val trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
    val thumbColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = scrollbarAlpha)

    Box(
        modifier = modifier
            .width(4.dp)
            .drawBehind {
                val barHeight = size.height * (visibleCount.toFloat() / itemCount).coerceIn(0.15f, 1f)
                val barTop = fraction * (size.height - barHeight)
                drawRoundRect(
                    color = trackColor,
                    cornerRadius = CornerRadius(2.dp.toPx()),
                    size = Size(size.width, size.height)
                )
                drawRoundRect(
                    color = thumbColor,
                    cornerRadius = CornerRadius(2.dp.toPx()),
                    topLeft = Offset(0f, barTop),
                    size = Size(size.width, barHeight)
                )
            }
    )
}

@Composable
private fun VerticalScrollbar(
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier
) {
    if (scrollState.maxValue <= 0) return

    val scrollbarAlpha by animateFloatAsState(
        targetValue = if (scrollState.isScrollInProgress) 1f else 0.4f,
        label = "scrollbarAlpha"
    )

    val fraction = scrollState.value.toFloat() / scrollState.maxValue
    val trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
    val thumbColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = scrollbarAlpha)

    Box(
        modifier = modifier
            .width(4.dp)
            .fillMaxHeight()
            .drawBehind {
                val barHeight = size.height * 0.3f
                val barTop = fraction * (size.height - barHeight)
                drawRoundRect(
                    color = trackColor,
                    cornerRadius = CornerRadius(2.dp.toPx()),
                    size = Size(size.width, size.height)
                )
                drawRoundRect(
                    color = thumbColor,
                    cornerRadius = CornerRadius(2.dp.toPx()),
                    topLeft = Offset(0f, barTop),
                    size = Size(size.width, barHeight)
                )
            }
    )
}
