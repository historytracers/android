// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.subjectsScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import kotlinx.coroutines.launch

private data class SubjectItem(
    val title: String,
    val tagline: String,
    val icons: List<String>,
    val onClick: () -> Unit,
)

@Composable
fun SubjectsScreen(
    onNavigateToFundamentals: () -> Unit = {},
    onNavigateToPhysics: () -> Unit = {},
    onNavigateToChemistry: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val ss = subjectsScreenStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showResetMenu by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    fun showComingSoon() {
        scope.launch { snackbarHostState.showSnackbar(ss.comingSoon) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SubjectGrid(
            subjects = listOf(
                SubjectItem(
                    title = ss.fundamentals,
                    tagline = ss.fundamentalsTagline,
                    icons = listOf("\u2B50", "\uD83E\uDDEC", "\u2795"),
                    onClick = onNavigateToFundamentals
                ),
                SubjectItem(
                    title = ss.physics,
                    tagline = ss.physicsTagline,
                    icons = listOf("\u269B\uFE0F", "\u2B50"),
                    onClick = onNavigateToPhysics
                ),
                SubjectItem(
                    title = ss.chemistry,
                    tagline = ss.chemistryTagline,
                    icons = listOf("\uD83E\uDDEA"),
                    onClick = onNavigateToChemistry
                ),
                SubjectItem(
                    title = ss.life,
                    tagline = ss.lifeTagline,
                    icons = listOf("\uD83E\uDDEC"),
                    onClick = { showComingSoon() }
                )
            ),
            modifier = Modifier.fillMaxSize()
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
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

@Composable
private fun SubjectGrid(
    subjects: List<SubjectItem>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val horizontalPadding = 24.dp
        val itemGap = 24.dp
        val circleSize = ((maxWidth - horizontalPadding * 2 - itemGap) / 2)
            .coerceIn(96.dp, 156.dp)
        val rows = subjects.chunked(2)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                rows.forEachIndexed { rowIndex, row ->
                    if (rowIndex > 0) {
                        Spacer(Modifier.height(28.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(itemGap, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.Top
                    ) {
                        row.forEach { subject ->
                            SubjectButton(
                                subject = subject,
                                circleSize = circleSize,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectButton(
    subject: SubjectItem,
    circleSize: Dp,
    modifier: Modifier = Modifier
) {
    val emojiSize = when {
        subject.icons.size >= 3 -> 24.sp
        subject.icons.size == 2 -> 30.sp
        else -> 38.sp
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(circleSize)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(ButtonYellow)
                .clickable(role = Role.Button, onClick = subject.onClick)
                .semantics {
                    contentDescription = "${subject.title}. ${subject.tagline}"
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                subject.icons.forEach { icon ->
                    Text(text = icon, fontSize = emojiSize)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = subject.tagline,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = circleSize + 16.dp)
        )
    }
}
