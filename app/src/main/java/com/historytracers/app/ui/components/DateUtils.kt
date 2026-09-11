// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import com.historytracers.app.calendar.CalendarType
import com.historytracers.app.ui.AppCommonStrings
import com.historytracers.common.HTDate
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset

object DateUtils {

    fun formatDate(
        date: HTDate,
        calendarId: String = CalendarType.GREGORIAN.id,
        common: AppCommonStrings? = null
    ): String {
        return try {
            when (date.dateType) {
                "unix" -> formatUnix(date, calendarId, common)
                "julian" -> formatJulianEpoch(date, calendarId, common)
                else -> formatGregorian(date, calendarId, common)
            }
        } catch (e: Exception) {
            rawFallback(date)
        }
    }

    fun formatDate(
        date: List<HTDate>?,
        calendarId: String = CalendarType.GREGORIAN.id,
        common: AppCommonStrings? = null
    ): List<String>? = date?.map { formatDate(it, calendarId, common) }

    private fun formatGregorian(date: HTDate, calendarId: String, common: AppCommonStrings?): String {
        val rawYear = date.year?.trim().orEmpty()
        val year = rawYear.toIntOrNull()
        val month = date.month?.trim()?.toIntOrNull() ?: -1
        val day = date.day?.trim()?.toIntOrNull() ?: -1
        val hasFullDate = year != null && month in 1..12 && day in 1..31

        val gregorian = if (hasFullDate) {
            LocalDate.of(year!!, month, minOf(day, daysIn(year, month)))
        } else {
            val baseYear = when {
                year != null -> year
                rawYear.equals("now", ignoreCase = true) -> LocalDate.now().year
                else -> return rawFallback(date)
            }
            val today = LocalDate.now()
            val refMonth = today.monthValue
            val refDay = minOf(today.dayOfMonth, daysIn(baseYear, refMonth))
            LocalDate.of(baseYear, refMonth, refDay)
        }
        return render(gregorian, CalendarType.fromId(calendarId), hasFullDate, common)
    }

    private fun formatUnix(date: HTDate, calendarId: String, common: AppCommonStrings?): String {
        val epoch = date.year?.trim()?.toLongOrNull() ?: return rawFallback(date)
        val gregorian = Instant.ofEpochSecond(epoch).atZone(ZoneOffset.UTC).toLocalDate()
        return render(gregorian, CalendarType.fromId(calendarId), true, common)
    }

    private fun formatJulianEpoch(date: HTDate, calendarId: String, common: AppCommonStrings?): String {
        val jd = date.day?.trim()?.toDoubleOrNull() ?: return rawFallback(date)
        return render(CalendarType.jdToGregorian(jd), CalendarType.fromId(calendarId), true, common)
    }

    private fun render(
        gregorian: LocalDate,
        calendar: CalendarType,
        hasFullDate: Boolean,
        common: AppCommonStrings?
    ): String {
        if (calendar == CalendarType.JULIAN) {
            return julianDays(CalendarType.gregorianToJd(gregorian), common)
        }
        if (calendar == CalendarType.MAYAN || calendar == CalendarType.MAYAN_EXTENDED) {
            val jd = CalendarType.gregorianToJd(gregorian)
            val count = if (calendar == CalendarType.MAYAN_EXTENDED) {
                CalendarType.jdToExtendedMayanCount(jd)
            } else {
                CalendarType.jdToMayanCount(jd)
            }
            return count.joinToString(".")
        }
        val yearText = yearLabel(calendar.year(gregorian), common)
        if (!hasFullDate) return yearText
        return "${calendar.day(gregorian)} ${calendar.monthName(gregorian)} $yearText"
    }

    private fun julianDays(jd: Double, common: AppCommonStrings?): String {
        val label = common?.dateJulianDays?.takeIf { it.isNotBlank() }
            ?: "days since 1st January of year 4713 B.C."
        val number = if (jd.isFinite() && jd == Math.floor(jd)) jd.toLong().toString() else jd.toString()
        return "$number $label"
    }

    private fun yearLabel(year: Int, common: AppCommonStrings?): String {
        if (year >= 0) return year.toString()
        val label = common?.bce?.takeIf { it.isNotBlank() } ?: "BCE"
        return "${-year} $label"
    }

    private fun daysIn(year: Int, month: Int): Int = try {
        YearMonth.of(year, month).lengthOfMonth()
    } catch (e: Exception) {
        28
    }

    private fun rawFallback(date: HTDate): String {
        val year = date.year?.trim().orEmpty()
        val month = date.month?.trim().orEmpty()
        val day = date.day?.trim().orEmpty()
        return listOf(day, month, year)
            .filter { it.isNotEmpty() && it != "-1" }
            .joinToString(" ")
    }
}
