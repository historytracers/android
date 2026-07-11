// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.ui.LocalUiStrings
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakScreen(
    streakCount: Int,
    completedDates: Set<String>,
    streakDays: Set<String>,
    onStreakDaysChanged: (Set<String>) -> Unit,
    onNavigateBack: () -> Unit
) {
    val s = LocalUiStrings.current
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

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
                    text = s.streak,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StreakCounter(streakCount)

            Spacer(Modifier.height(24.dp))

            WeekDaySelector(streakDays, onStreakDaysChanged)

            Spacer(Modifier.height(24.dp))

            CalendarHeader(currentMonth, onPreviousMonth = {
                currentMonth = currentMonth.minusMonths(1)
            }, onNextMonth = {
                currentMonth = currentMonth.plusMonths(1)
            })

            Spacer(Modifier.height(8.dp))

            CalendarGrid(currentMonth, completedDates)
        }
    }
}

@Composable
private fun StreakCounter(count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocalFireDepartment,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (count == 1) "day" else "days",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeekDaySelector(selectedDays: Set<String>, onSelectedDaysChanged: (Set<String>) -> Unit) {
    val days = DayOfWeek.entries

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Evaluate on:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { day ->
                val dayName = day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                val isSelected = day.name in selectedDays
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onSelectedDaysChanged(
                            if (isSelected) selectedDays - day.name
                            else selectedDays + day.name
                        )
                    },
                    label = { Text(dayName, fontSize = 11.sp) }
                )
            }
        }
    }
}

@Composable
private fun CalendarHeader(month: YearMonth, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
        }
        Text(
            text = month.format(formatter).replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
        }
    }
}

@Composable
private fun CalendarGrid(month: YearMonth, completedDates: Set<String>) {
    val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    val firstOfMonth = month.atDay(1)
    val firstDayOfWeek = firstOfMonth.dayOfWeek.value % 7
    val daysInMonth = month.lengthOfMonth()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        var dayCounter = 1
        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    if (cellIndex < firstDayOfWeek || dayCounter > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = month.atDay(dayCounter)
                        val dateStr = date.toString()
                        val isCompleted = dateStr in completedDates
                        val isToday = date == LocalDate.now()

                        DayCell(
                            modifier = Modifier.weight(1f),
                            day = dayCounter,
                            isCompleted = isCompleted,
                            isToday = isToday
                        )
                        dayCounter++
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(modifier: Modifier = Modifier, day: Int, isCompleted: Boolean, isToday: Boolean) {
    val bgColor = when {
        isCompleted -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isCompleted -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .then(
                if (isCompleted) Modifier.clip(CircleShape).background(bgColor)
                else Modifier
            )
            .then(
                if (isToday && !isCompleted) Modifier
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isToday || isCompleted) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}
