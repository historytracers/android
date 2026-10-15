// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.R
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.whereAreWeFromScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import kotlinx.coroutines.launch

@Composable
fun WhereAreWeFromScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToSharedOrigin: () -> Unit = {},
    onNavigateToMatterAndEnergy: () -> Unit = {},
    onNavigateToEverythingWasTogether: () -> Unit = {},
    onNavigateToSharingWithWhom: () -> Unit = {},
    onNavigateToTheSameResult: () -> Unit = {},
    onNavigateToHalfPiece: () -> Unit = {},
    onNavigateToOddOrEvenGame: () -> Unit = {},
    onNavigateToExpanding: () -> Unit = {},
    scrollState: ScrollState = rememberScrollState()
) {
    val s = LocalUiStrings.current
    val xs = whereAreWeFromScreenStringsForLanguage(LocalAppLanguage.current)

    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedSections by preferences.completedWhereAreWeFromSections.collectAsState(initial = emptySet())
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
                    text = xs.title,
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
                modifier = Modifier.verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                FilledIconButton(
                    onClick = onNavigateToSharedOrigin,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("shared_origin" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_cmb),
                        contentDescription = xs.sharedOrigin,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.sharedOrigin,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))

                FilledIconButton(
                    onClick = onNavigateToMatterAndEnergy,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("matter_energy" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_matter_energy),
                        contentDescription = xs.matterAndEnergy,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.matterAndEnergy,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))

                FilledIconButton(
                    onClick = onNavigateToEverythingWasTogether,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("everything_together" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_universe_expansion),
                        contentDescription = xs.everythingWasTogether,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.everythingWasTogether,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))

                FilledIconButton(
                    onClick = onNavigateToSharingWithWhom,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = xs.sharedWithWho },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("sharing_with_whom" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Text(
                        text = "?",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.sharedWithWho,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))

                FilledIconButton(
                    onClick = onNavigateToTheSameResult,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = xs.theSameResult },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("the_same_result" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "÷",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "1",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(
                            text = "×",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.theSameResult,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))

                FilledIconButton(
                    onClick = onNavigateToHalfPiece,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = xs.halfPiece },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("half_piece" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val sheetModifier = Modifier
                            .size(width = 24.dp, height = 32.dp)
                            .background(Color.White, RoundedCornerShape(2.dp))
                            .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(2.dp))
                        Box(sheetModifier)
                        Spacer(Modifier.width(6.dp))
                        Box(sheetModifier)
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.halfPiece,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))

                Surface(
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = xs.oddOrEven },
                    shape = CircleShape,
                    color = ButtonYellow
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "20?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.oddOrEven,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))

                FilledIconButton(
                    onClick = onNavigateToOddOrEvenGame,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = xs.oddOrEvenGame },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("odd_or_even_game" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(Color(0xFF333333), CircleShape)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.oddOrEvenGame,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))

                FilledIconButton(
                    onClick = onNavigateToExpanding,
                    modifier = Modifier
                        .size(96.dp)
                        .semantics { contentDescription = xs.expanding },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("expanding" in completedSections) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Canvas(modifier = Modifier.size(52.dp)) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val center = Offset(cx, cy)
                        val s = size.minDimension * 0.22f
                        val dx = s * 0.7f
                        val dy = -s * 0.7f
                        val axisLen = s * 1.8f
                        val axisStroke = size.minDimension * 0.045f
                        val cubeStroke = size.minDimension * 0.05f

                        // All three axes visible through the cube.
                        drawLine(
                            color = Color(0xFFE53935),
                            start = Offset(cx - axisLen, cy),
                            end = Offset(cx + axisLen, cy),
                            strokeWidth = axisStroke
                        )
                        drawLine(
                            color = Color(0xFF43A047),
                            start = Offset(cx, cy + axisLen),
                            end = Offset(cx, cy - axisLen),
                            strokeWidth = axisStroke
                        )
                        val zDir = Offset(dx, dy)
                        val zLen = kotlin.math.sqrt(zDir.x * zDir.x + zDir.y * zDir.y)
                        val zUnit = if (zLen > 0f) Offset(zDir.x / zLen, zDir.y / zLen) else Offset(1f, -1f)
                        drawLine(
                            color = Color(0xFF1E88E5),
                            start = center - zUnit * axisLen,
                            end = center + zUnit * axisLen,
                            strokeWidth = axisStroke
                        )

                        // Wireframe cube: front square, back square, connectors.
                        val a = Offset(cx - s, cy - s)
                        val b = Offset(cx + s, cy - s)
                        val c = Offset(cx + s, cy + s)
                        val d = Offset(cx - s, cy + s)
                        val a2 = a + Offset(dx, dy)
                        val b2 = b + Offset(dx, dy)
                        val c2 = c + Offset(dx, dy)
                        val d2 = d + Offset(dx, dy)
                        val cubeColor = Color(0xFF212121)
                        drawLine(cubeColor, a, b, strokeWidth = cubeStroke)
                        drawLine(cubeColor, b, c, strokeWidth = cubeStroke)
                        drawLine(cubeColor, c, d, strokeWidth = cubeStroke)
                        drawLine(cubeColor, d, a, strokeWidth = cubeStroke)
                        drawLine(cubeColor, a2, b2, strokeWidth = cubeStroke)
                        drawLine(cubeColor, b2, c2, strokeWidth = cubeStroke)
                        drawLine(cubeColor, c2, d2, strokeWidth = cubeStroke)
                        drawLine(cubeColor, d2, a2, strokeWidth = cubeStroke)
                        drawLine(cubeColor, a, a2, strokeWidth = cubeStroke)
                        drawLine(cubeColor, b, b2, strokeWidth = cubeStroke)
                        drawLine(cubeColor, c, c2, strokeWidth = cubeStroke)
                        drawLine(cubeColor, d, d2, strokeWidth = cubeStroke)
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.expanding,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
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
