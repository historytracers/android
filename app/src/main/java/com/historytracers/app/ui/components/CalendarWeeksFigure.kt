// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.ui.LocalAppCalendar
import com.historytracers.app.ui.LocalAppLanguage
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

private val CALENDAR_HEADER = Color(0xFFE8F5E9)
private val CALENDAR_BORDER = Color(0xFF2E7D32)
private val CALENDAR_ROW_LINE = Color(0xFFA5D6A7)

/** Day names of the Islamic week, from Sunday (Ahad) to Saturday (Sabt). */
private val ISLAMIC_WEEK = listOf("Ahad", "Ithnayn", "Thulatha", "Arba'a", "Khamis", "Jum'a", "Sabt")

/** Day names of the Hebrew week, from Sunday (Rishon) to Saturday (Shabbat). */
private val HEBREW_WEEK = listOf("Rishon", "Sheni", "Shlishi", "Revi'i", "Chamishi", "Shishi", "Shabbat")

/** Day names of the Persian week, which starts on Saturday (Shanbeh). */
private val PERSIAN_WEEK = listOf(
    "Shanbeh", "Yekshanbeh", "Doshanbeh", "Seshanbeh",
    "Chaharshanbeh", "Panjshanbeh", "Jomeh"
)

/** Day names of the Indian civil (Saka) week, from Sunday (Ravivara). */
private val SAKA_WEEK = listOf(
    "Ravivara", "Somavara", "Mangalavara", "Budhavara",
    "Guruvara", "Shukravara", "Shanivara"
)

/** Day names of the Javanese week, from Sunday (Ahad) to Saturday (Setu). */
private val JAVANESE_WEEK = listOf("Ahad", "Senen", "Selasa", "Rebo", "Kemis", "Jumat", "Setu")

/**
 * Draws a simple calendar with [weeks] rows of seven days, the same way the
 * web version's four-week calendar figure does. The seven day names follow the
 * calendar selected in the settings: the Islamic, Hebrew, Persian, Indian civil
 * and Javanese calendars use their own names, while the calendars that share
 * the ordinary week (Gregorian, Julian, and the others) use the short names of
 * the language selected in the app.
 */
@Composable
fun CalendarWeeksFigure(
    weeks: Int,
    weekLabelFormat: String,
    modifier: Modifier = Modifier
) {
    val language = LocalAppLanguage.current
    val calendar = LocalAppCalendar.current
    val locale = remember(language) { Locale.forLanguageTag(language) }
    val localisedDayNames = remember(locale) {
        val firstDayOfWeek = WeekFields.of(locale).firstDayOfWeek
        (0..6).map { offset ->
            firstDayOfWeek.plus(offset.toLong()).getDisplayName(TextStyle.SHORT, locale)
        }
    }
    val dayNames = remember(calendar, localisedDayNames) {
        when (calendar) {
            "islamic" -> ISLAMIC_WEEK
            "hebrew" -> HEBREW_WEEK
            "persian", "persian_astronomical" -> PERSIAN_WEEK
            "indian_civil" -> SAKA_WEEK
            "javanese" -> JAVANESE_WEEK
            else -> localisedDayNames
        }
    }
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, CALENDAR_BORDER, shape)
    ) {
        Row(modifier = Modifier.fillMaxWidth().background(CALENDAR_HEADER)) {
            Spacer(Modifier.weight(1.5f))
            dayNames.forEach { name ->
                Text(
                    text = name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = CALENDAR_BORDER,
                    modifier = Modifier.weight(1f).padding(vertical = 5.dp)
                )
            }
        }

        repeat(weeks) { week ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(CALENDAR_ROW_LINE)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weekLabelFormat.format(week + 1),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = CALENDAR_BORDER,
                    modifier = Modifier
                        .weight(1.5f)
                        .padding(horizontal = 6.dp, vertical = 5.dp)
                )
                (1..7).forEach { day ->
                    Text(
                        text = "${week * 7 + day}",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).padding(vertical = 5.dp)
                    )
                }
            }
        }
    }
}
