// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.complementToTenScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplementToTenScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val cs = complementToTenScreenStringsForLanguage(LocalAppLanguage.current)
    var x by remember { mutableIntStateOf(0) }
    val y = 10 - x
    var visitedX by remember { mutableStateOf(setOf(0)) }
    var completed by remember { mutableStateOf(false) }
    var showCongrats by remember { mutableStateOf(false) }
    var wasAtExtreme by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(visitedX) {
        if (!completed && visitedX.size == 11) {
            completed = true
            showCongrats = true
            preferences.recordLessonCompletion()
            preferences.markAbacusSectionCompleted("complement_to_ten")
            onScoreChanged(currentScore + 2)
        }
    }

    fun moveTo(nx: Int) {
        x = nx
        visitedX = visitedX + nx
        if (completed) showCongrats = false
        if (nx == 10) wasAtExtreme = true
        if (nx == 0 && wasAtExtreme) {
            wasAtExtreme = false
            showCongrats = true
            onScoreChanged(currentScore + 2)
        }
    }

    @Composable
    fun arrowButtonColors(
        atLimit: Boolean
    ): Pair<Color, Color> = when {
        completed -> ButtonYellowDark to OnButtonYellow
        atLimit -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        else -> ButtonYellow to OnButtonYellow
    }

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
                        text = cs.title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = cs.instruction,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp)
                )

                Text(
                    text = cs.hint,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp)
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = cs.yLabel,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "$y",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC9A05A)
                        )
                    }
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = cs.xLabel,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "$x",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3A6068)
                        )
                    }
                    Text(
                        text = "=",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "10",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(10) { i ->
                        val isX = i < x
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (isX) Color(0xFF3A6068) else Color(0xFFC9A05A))
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val downColors = arrowButtonColors(x <= 0)
                    FilledIconButton(
                        onClick = { if (x > 0) moveTo(x - 1) },
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = downColors.first,
                            contentColor = downColors.second
                        )
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = cs.downArrow,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    val upColors = arrowButtonColors(x >= 10)
                    FilledIconButton(
                        onClick = { if (x < 10) moveTo(x + 1) },
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = upColors.first,
                            contentColor = upColors.second
                        )
                    ) {
                        Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = cs.upArrow,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = cs.complementText.format(x, y),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(Modifier.height(16.dp))

                if (showCongrats) {
                    Text(
                        text = cs.completionMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp, start = 8.dp)
        ) {
            val uriHandler = LocalUriHandler.current

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { showSourcesMenu = true }
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Filled.Book,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = s.common.sources,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            DropdownMenu(
                expanded = showSourcesMenu && !showMainTextSubmenu,
                onDismissRequest = { showSourcesMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(s.common.originalText) },
                    trailingIcon = {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    },
                    onClick = { showMainTextSubmenu = true }
                )
            }

            DropdownMenu(
                expanded = showSourcesMenu && showMainTextSubmenu,
                onDismissRequest = { showMainTextSubmenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(s.common.copyUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=6d9857d3-a1af-4411-b358-b97a7ea02515"))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=6d9857d3-a1af-4411-b358-b97a7ea02515")
                    }
                )
            }
        }
    }
}
