// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.R
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.whoAmIScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

@Composable
fun WhoAmIScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToSocrates: () -> Unit = {},
    onNavigateToLearningInLayers: () -> Unit = {},
    onNavigateToHowDoILearn: () -> Unit = {},
    onNavigateToSunTzuLearning: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val xs = whoAmIScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedSections by preferences.completedWhoAmISections.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

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
                    text = hts.whoAmI,
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
                    onClick = onNavigateToSocrates,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (completedSections.contains("i_dont_know")) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = OnButtonYellow
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = hts.iDontKnow,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToLearningInLayers,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (completedSections.contains("learning_in_shells")) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Canvas(modifier = Modifier.size(52.dp)) {
                        val strokeWidth = size.width * 0.06f
                        val center = this.center
                        val half = size.minDimension / 2f
                        val spacing = size.width * 0.12f
                        listOf(0f, spacing, 2 * spacing).forEach { inset ->
                            val h = half - inset
                            drawRect(
                                color = OnButtonYellow,
                                topLeft = Offset(center.x - h, center.y - h),
                                size = Size(2 * h, 2 * h),
                                style = Stroke(width = strokeWidth)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.learningInLayers,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToHowDoILearn,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (completedSections.contains("how_do_i_learn")) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    val textMeasurer = rememberTextMeasurer()
                    Canvas(modifier = Modifier.size(52.dp)) {
                        val strokeWidth = size.width * 0.06f
                        val headRadius = size.width * 0.26f
                        val headCenter = Offset(center.x, center.y + size.height * 0.10f)
                        drawCircle(
                            color = OnButtonYellow,
                            radius = headRadius,
                            center = headCenter,
                            style = Stroke(width = strokeWidth)
                        )
                        val eyeRadius = size.width * 0.045f
                        val eyeY = headCenter.y - headRadius * 0.25f
                        drawCircle(
                            color = OnButtonYellow,
                            radius = eyeRadius,
                            center = Offset(headCenter.x - headRadius * 0.4f, eyeY)
                        )
                        drawCircle(
                            color = OnButtonYellow,
                            radius = eyeRadius,
                            center = Offset(headCenter.x + headRadius * 0.4f, eyeY)
                        )
                        val qm = textMeasurer.measure(
                            text = "?",
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OnButtonYellow)
                        )
                        drawText(
                            textLayoutResult = qm,
                            topLeft = Offset(
                                center.x + size.width * 0.10f - qm.size.width / 2f,
                                center.y - size.height * 0.42f - qm.size.height / 2f
                            )
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.howDoILearn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = onNavigateToSunTzuLearning,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (completedSections.contains("sun_tzu_and_learning")) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Text(
                        text = "孫子",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnButtonYellow
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.sunTzuAndLearning,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(16.dp))
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
