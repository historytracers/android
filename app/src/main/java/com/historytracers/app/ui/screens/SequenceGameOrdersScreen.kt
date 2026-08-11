// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.sequenceGameOrdersScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

private val allOrderNumbers = listOf(
    1L, 10L, 100L, 1000L, 10000L, 100000L,
    1000000L, 10000000L, 100000000L, 1000000000L
)

private data class OrderLeftItem(val text: String, val id: Int)
private data class OrderRightItem(val text: String, val id: Int)

private fun selectedOrderIds(): List<Int> =
    allOrderNumbers.indices.shuffled().take(6)

private fun formatOrderNumber(value: Long, language: String): String {
    val separator = if (language == "en-US") "," else "."
    return value.toString().reversed().chunked(3).joinToString(separator).reversed()
}

private fun buildLeftItems(ids: List<Int>, language: String): List<OrderLeftItem> =
    ids.shuffled().map { OrderLeftItem(formatOrderNumber(allOrderNumbers[it], language), it) }

private fun buildRightItems(names: List<String>, ids: List<Int>): List<OrderRightItem> =
    ids.shuffled().map { OrderRightItem(names[it], it) }

@Composable
private fun OrderItemButton(
    text: String,
    selected: Boolean,
    matched: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = when {
        matched -> ButtonYellowDark
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val content = when {
        selected -> Color.White
        matched -> OnButtonYellow
        else -> MaterialTheme.colorScheme.onSurface
    }
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        enabled = !matched,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = container,
            disabledContentColor = content
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SequenceGameOrdersScreen(
    onNavigateBack: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = sequenceGameOrdersScreenStringsForLanguage(LocalAppLanguage.current)
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    val allOrderNames = listOf(
        xs.units, xs.tens, xs.hundreds, xs.thousands, xs.tenThousands, xs.hundredThousands,
        xs.millions, xs.tenMillions, xs.hundredMillions, xs.billions
    )

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    val initialOrderIds = remember { selectedOrderIds() }
    var orderIds by remember { mutableStateOf(initialOrderIds) }
    var leftItems by remember { mutableStateOf(buildLeftItems(initialOrderIds, language)) }
    var rightItems by remember { mutableStateOf(buildRightItems(allOrderNames, initialOrderIds)) }
    var matchedIds by remember { mutableStateOf(emptySet<Int>()) }
    var selectedLeftId by remember { mutableStateOf<Int?>(null) }
    var selectedRightId by remember { mutableStateOf<Int?>(null) }
    var completed by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    fun evaluatePair(leftId: Int, rightId: Int) {
        if (leftId == rightId) {
            matchedIds = matchedIds + leftId
            selectedLeftId = null
            selectedRightId = null
            if (matchedIds.size == orderIds.size) {
                completed = true
                totalAwarded += 2
                onScoreChanged(initialScore + totalAwarded)
                scope.launch {
                    preferences.recordLessonCompletion()
                    preferences.markFirstStepsSectionCompleted("sequence_game_orders")
                }
            }
        } else {
            selectedLeftId = null
            selectedRightId = null
        }
    }

    fun onLeftTap(item: OrderLeftItem) {
        if (completed) return
        if (item.id in matchedIds) return
        selectedLeftId = item.id
        val rightSel = selectedRightId
        if (rightSel != null) {
            evaluatePair(item.id, rightSel)
        }
    }

    fun onRightTap(item: OrderRightItem) {
        if (completed) return
        if (item.id in matchedIds) return
        selectedRightId = item.id
        val leftSel = selectedLeftId
        if (leftSel != null) {
            evaluatePair(leftSel, item.id)
        }
    }

    fun newGame() {
        val ids = selectedOrderIds()
        orderIds = ids
        leftItems = buildLeftItems(ids, language)
        rightItems = buildRightItems(allOrderNames, ids)
        matchedIds = emptySet()
        selectedLeftId = null
        selectedRightId = null
        completed = false
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
                        text = xs.title,
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
                    text = xs.instruction,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 2.dp)
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        leftItems.forEach { item ->
                            OrderItemButton(
                                text = item.text,
                                selected = selectedLeftId == item.id,
                                matched = item.id in matchedIds,
                                onClick = { onLeftTap(item) },
                                modifier = Modifier.width(140.dp)
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        rightItems.forEach { item ->
                            OrderItemButton(
                                text = item.text,
                                selected = selectedRightId == item.id,
                                matched = item.id in matchedIds,
                                onClick = { onRightTap(item) },
                                modifier = Modifier.width(220.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = { newGame() },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = xs.newGame,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                if (completed) {
                    Text(
                        text = xs.completionMessage,
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
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", "https://www.historytracers.org/index.html?page=class_content&arg=5f349c3b-944c-42ae-a7f0-00a4c8d4ba10"))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        uriHandler.openUri("https://www.historytracers.org/index.html?page=class_content&arg=5f349c3b-944c-42ae-a7f0-00a4c8d4ba10")
                    }
                )
            }
        }
    }
}
