// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.oddOrEvenScreenStringsForLanguage
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val ODD_OR_EVEN_SECTION_ID = "odd_or_even_game"
private const val ODD_OR_EVEN_MAX_NUMBER = 20

// Ports the "Odd or even? (Game)" web game (8dfdd5e0): a number between 0 and 20
// appears and its circles are shared into two sides. The player says whether the
// number is odd or even, and the game explains the result with the remainder.
@Composable
fun OddOrEvenScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToWhereAreWeFrom: () -> Unit = {},
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = oddOrEvenScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    var number by remember { mutableIntStateOf(Random.nextInt(0, ODD_OR_EVEN_MAX_NUMBER + 1)) }
    var answered by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf("") }
    var feedbackPositive by remember { mutableStateOf(false) }
    var sectionMarked by remember { mutableStateOf(false) }
    var showSourcesMenu by remember { mutableStateOf(false) }
    var showMainTextSubmenu by remember { mutableStateOf(false) }

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    fun award(points: Int) {
        if (points <= 0) return
        totalAwarded += points
        onScoreChanged(initialScore + totalAwarded)
    }

    // This is the group's last screen, so it records the day's streak on arrival.
    LaunchedEffect(Unit) {
        preferences.recordLessonCompletion()
    }

    val isOdd = number % 2 == 1
    val quotient = number / 2
    val leftSide = (number + 1) / 2
    val rightSide = number / 2

    fun newExercise() {
        number = Random.nextInt(0, ODD_OR_EVEN_MAX_NUMBER + 1)
        answered = false
        feedback = ""
        feedbackPositive = false
    }

    fun answer(choseOdd: Boolean) {
        if (answered) return
        val correct = choseOdd == isOdd
        feedback = when {
            correct && isOdd -> xs.msgOddCorrect.format(number, quotient)
            correct && !isOdd -> xs.msgEvenCorrect.format(number, quotient)
            !correct && isOdd -> xs.msgOddWrong.format(number, quotient)
            else -> xs.msgEvenWrong.format(number, quotient)
        }
        feedbackPositive = correct
        answered = true
        award(if (correct) 2 else 1)
        // Finishing the first round lets the class button outside change colour.
        if (!sectionMarked) {
            sectionMarked = true
            scope.launch { preferences.markWhereAreWeFromSectionCompleted(ODD_OR_EVEN_SECTION_ID) }
        }
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = xs.howToPlay,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(Modifier.height(16.dp))

                OddEvenBoard(
                    number = number,
                    leftSide = leftSide,
                    rightSide = rightSide,
                    numberHeader = xs.numberHeader,
                    shareHeader = xs.shareHeader,
                    side1 = xs.side1,
                    side2 = xs.side2
                )

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { answer(true) },
                        enabled = !answered,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(xs.odd)
                    }
                    Button(
                        onClick = { answer(false) },
                        enabled = !answered,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(xs.even)
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (feedback.isNotEmpty()) {
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (feedbackPositive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }

                if (answered) {
                    Spacer(Modifier.height(16.dp))
                    FilledTonalButton(
                        onClick = { newExercise() },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = xs.newExercise,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = onNavigateToWhereAreWeFrom,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = hts.whereAreWeFrom,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 8.dp, start = 8.dp)
        ) {
            val uriHandler = LocalUriHandler.current
            val sourceUrl = "https://www.historytracers.org/index.html?page=class_content&arg=8dfdd5e0-01e3-4ebc-b0c2-38462a54aa98"

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
                        clipboard.setPrimaryClip(ClipData.newPlainText("URL", sourceUrl))
                        Toast.makeText(context, s.common.copyUrl, Toast.LENGTH_SHORT).show()
                    }
                )
                DropdownMenuItem(
                    text = { Text(s.common.goToUrl) },
                    onClick = {
                        showSourcesMenu = false
                        showMainTextSubmenu = false
                        uriHandler.openUri(sourceUrl)
                    }
                )
            }
        }
    }
}

@Composable
private fun OddEvenBoard(
    number: Int,
    leftSide: Int,
    rightSide: Int,
    numberHeader: String,
    shareHeader: String,
    side1: String,
    side2: String
) {
    val cellBorder = Color(0xFF9E9E9E)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            BoardHeaderCell(text = numberHeader, width = 80.dp, border = cellBorder)
            BoardHeaderCell(text = shareHeader, width = 160.dp, border = cellBorder)
        }
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .border(1.dp, cellBorder)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier
                    .width(160.dp)
                    .border(1.dp, cellBorder),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    BoardHeaderCell(text = side1, width = 79.dp, border = cellBorder)
                    BoardHeaderCell(text = side2, width = 79.dp, border = cellBorder)
                }
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (row in 0 until leftSide) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            OddEvenCircle()
                            if (row < rightSide) {
                                OddEvenCircle()
                            } else {
                                Spacer(Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardHeaderCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    border: Color
) {
    Box(
        modifier = Modifier
            .width(width)
            .border(1.dp, border)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OddEvenCircle() {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(Color(0xFF8FB8D8), CircleShape)
            .border(1.dp, Color(0xFF5B87A8), CircleShape)
    )
}
