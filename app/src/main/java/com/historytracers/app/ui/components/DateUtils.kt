package com.historytracers.app.ui.components

import com.historytracers.common.HTDate

object DateUtils {

    fun formatDate(date: HTDate): String {
        val y = date.year ?: "?"
        val m = date.month?.let { monthNumber(it) } ?: "?"
        val d = date.day ?: "?"
        return when (date.dateType) {
            "gregory" -> "$d $m $y"
            "julian" -> "$d $m $y (Julian)"
            "unix" -> formatUnixTimestamp(y)
            else -> "$y-$m-$d"
        }
    }

    fun formatDate(date: List<HTDate>?): List<String>? =
        date?.map { formatDate(it) }

    private fun monthNumber(month: String): String {
        val names = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val idx = month.toIntOrNull()?.minus(1) ?: return month
        return names.getOrNull(idx) ?: month
    }

    private fun formatUnixTimestamp(year: String): String {
        val seconds = year.toLongOrNull() ?: return year
        val date = java.util.Date(seconds * 1000)
        val fmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        return fmt.format(date)
    }
}
