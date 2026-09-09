// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.calendar

import java.time.LocalDate

enum class CalendarType(val id: String, val displayName: String) {
    GREGORIAN("gregorian", "Gregorian"),
    JULIAN("julian", "Julian"),
    HEBREW("hebrew", "Hebrew"),
    ISLAMIC("islamic", "Islamic"),
    PERSIAN("persian", "Persian"),
    PERSIAN_ASTRONOMICAL("persian_astronomical", "Shaka"),
    MAYAN("mayan", "Mesoamerican"),
    MAYAN_EXTENDED("mesoamerican_ext", "Mesoamerican (Ext.)"),
    HISPANIC("hispanic", "Hispanic"),
    INDIAN_CIVIL("indian_civil", "Indian Civil"),
    FRENCH_REPUBLICAN("french_republican", "French Republican"),
    CHINESE("chinese", "Chinese"),
    AYMARA("aymara", "Aymara"),
    MAPUCHE("mapuche", "Mapuche"),
    INCA("inca", "Inca"),
    JAVANESE("javanese", "Javanese"),
    JAPANESE("japanese", "Japanese");

    fun displayName(): String = displayName

    fun year(gregorian: LocalDate): Int {
        val jd = gregorianToJd(gregorian)
        return when (this) {
            GREGORIAN -> gregorian.year
            JULIAN -> jdToJulian(jd)[0]
            HEBREW -> jdToHebrew(jd)[0]
            ISLAMIC -> jdToIslamic(jd)[0]
            PERSIAN -> jdToPersian(jd)[0]
            PERSIAN_ASTRONOMICAL -> jdToPersiana(jd)[0]
            MAYAN -> jdToMayanCount(jd)[3]
            MAYAN_EXTENDED -> jdToExtendedMayanCount(jd)[3]
            HISPANIC -> gregorian.year + 38
            INDIAN_CIVIL -> jdToIndianCivil(jd)[0]
            FRENCH_REPUBLICAN -> jdToFrenchRepublican(jd)[0]
            CHINESE -> jdToChinese(jd)[0]
            AYMARA -> jdToAymara(jd)[0]
            MAPUCHE -> jdToMapuche(jd)[0]
            INCA -> jdToInca(jd)[0]
            JAVANESE -> jdToJavanese(jd)[0]
            JAPANESE -> jdToJapanese(jd)[0]
        }
    }

    fun month(gregorian: LocalDate): Int {
        val jd = gregorianToJd(gregorian)
        return when (this) {
            GREGORIAN -> gregorian.monthValue
            JULIAN -> jdToJulian(jd)[1]
            HEBREW -> jdToHebrew(jd)[1]
            ISLAMIC -> jdToIslamic(jd)[1]
            PERSIAN -> jdToPersian(jd)[1]
            PERSIAN_ASTRONOMICAL -> jdToPersiana(jd)[1]
            MAYAN -> jdToMayanCount(jd)[4]
            MAYAN_EXTENDED -> jdToExtendedMayanCount(jd)[4]
            HISPANIC -> gregorian.monthValue
            INDIAN_CIVIL -> jdToIndianCivil(jd)[1]
            FRENCH_REPUBLICAN -> jdToFrenchRepublican(jd)[1]
            CHINESE -> jdToChinese(jd)[1]
            AYMARA -> jdToAymara(jd)[1]
            MAPUCHE -> jdToMapuche(jd)[1]
            INCA -> jdToInca(jd)[1]
            JAVANESE -> jdToJavanese(jd)[1]
            JAPANESE -> jdToJapanese(jd)[1]
        }
    }

    fun day(gregorian: LocalDate): Int {
        val jd = gregorianToJd(gregorian)
        return when (this) {
            GREGORIAN -> gregorian.dayOfMonth
            JULIAN -> jdToJulian(jd)[2]
            HEBREW -> jdToHebrew(jd)[2]
            ISLAMIC -> jdToIslamic(jd)[2]
            PERSIAN -> jdToPersian(jd)[2]
            PERSIAN_ASTRONOMICAL -> jdToPersiana(jd)[2]
            MAYAN -> jdToMayanCount(jd)[7]
            MAYAN_EXTENDED -> jdToExtendedMayanCount(jd)[7]
            HISPANIC -> gregorian.dayOfMonth
            INDIAN_CIVIL -> jdToIndianCivil(jd)[2]
            FRENCH_REPUBLICAN -> jdToFrenchRepublican(jd)[3]
            CHINESE -> jdToChinese(jd)[2]
            AYMARA -> jdToAymara(jd)[2]
            MAPUCHE -> jdToMapuche(jd)[2]
            INCA -> jdToInca(jd)[2]
            JAVANESE -> jdToJavanese(jd)[2]
            JAPANESE -> jdToJapanese(jd)[2]
        }
    }

    fun monthName(gregorian: LocalDate): String {
        val names = monthNames()
        val m = month(gregorian)
        return when (this) {
            MAYAN, MAYAN_EXTENDED -> {
                val count = jdToMayanCount(gregorianToJd(gregorian))
                "Tzolkin ${count[6]} ${MAYAN_TZOLKIN_MONTHS[count[6] - 1]}"
            }
            CHINESE -> {
                val parts = jdToChinese(gregorianToJd(gregorian))
                val leap = if (parts[3] == 1) " (leap)" else ""
                "Month ${parts[1]}$leap"
            }
            else -> if (m in names.indices) names[m] else "Month $m"
        }
    }

    fun monthNames(): List<String> = when (this) {
        GREGORIAN -> listOf("", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        JULIAN -> listOf("", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        HEBREW -> listOf("", "Tishrei", "Cheshvan", "Kislev", "Tevet", "Shevat", "Adar I",
            "Adar", "Nisan", "Iyyar", "Sivan", "Tammuz", "Av", "Elul")
        ISLAMIC -> listOf("", "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
            "Jumada al-Ula", "Jumada al-Thani", "Rajab", "Sha'ban", "Ramadan",
            "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah")
        PERSIAN -> listOf("", "Farvardin", "Ordibehesht", "Khordad", "Tir", "Mordad",
            "Shahrivar", "Mehr", "Aban", "Azar", "Dey", "Bahman", "Esfand")
        PERSIAN_ASTRONOMICAL -> PERSIAN.monthNames()
        MAYAN, MAYAN_EXTENDED -> listOf("") // Tzolkin and Haab handled specially
        HISPANIC -> GREGORIAN.monthNames()
        INDIAN_CIVIL -> listOf("", "Chaitra", "Vaishakha", "Jyaistha", "Ashadha",
            "Shravana", "Bhadra", "Ashvina", "Kartika", "Agrahayana", "Pausha",
            "Magha", "Phalguna")
        FRENCH_REPUBLICAN -> listOf("", "Vendemiaire", "Brumaire", "Frimaire",
            "Nivose", "Pluviose", "Ventose", "Germinal", "Floreal", "Prairial",
            "Messidor", "Thermidor", "Fructidor", "Sansculottides")
        CHINESE -> listOf("") // Handled specially
        AYMARA -> listOf("", "Willka Kuti", "Jach'a Willka", "Sata", "Taypi Sata",
            "Lapaka", "Jallu Qallta", "Chinuqa", "Anata", "Achuqa", "Qasawi",
            "Llamayu", "Mara T'aqa")
        MAPUCHE -> listOf("", "We Tripantu", "Llitunul Wilki", "Llitun Pofpof Anumka",
            "Rayen Awar", "Longkon Kachilla", "Karu Kachilla", "Kudewallung",
            "Puramuwun Kachilla", "Truntaru", "Ngulliw", "Mallin Ko",
            "Tranglin", "Mawun Kuruf")
        INCA -> listOf("", "Capac Raymi", "Camay", "Hatun Pucuy", "Pucuy Pucuy",
            "Ayrihuay", "Aymoray", "Inti Raymi", "Anta Situa", "Cusqui Raymi",
            "Coya Raymi", "Uma Raymi", "Ayamarca")
        JAVANESE -> listOf("", "Sura", "Sapar", "Mulud", "Bakda Mulud",
            "Jumadilawal", "Jumadilakir", "Rejeb", "Ruwah", "Pasa", "Sawal",
            "Sela", "Besar")
        JAPANESE -> listOf("", "Mutsuki", "Kisaragi", "Yayoi", "Uzuki",
            "Satsuki", "Minazuki", "Fumizuki", "Hazuki", "Nagatsuki",
            "Kannazuki", "Shimotsuki", "Shiwasu")
    }

    fun daysInMonth(year: Int, month: Int): Int = when (this) {
        GREGORIAN -> when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (leapGregorian(year)) 29 else 28
            else -> 30
        }
        JULIAN -> when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (mod(year, 4) == (if (year > 0) 0 else 3)) 29 else 28
            else -> 30
        }
        HEBREW -> hebrewMonthDays(year, month)
        ISLAMIC -> when (month) {
            2, 4, 6, 10, 13 -> 29
            12 -> if (!leapIslamic(year)) 29 else 30
            else -> 30
        }
        PERSIAN, PERSIAN_ASTRONOMICAL -> if (month <= 7) 31 else 30
        MAYAN, MAYAN_EXTENDED -> 20
        HISPANIC -> when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (leapGregorian(year - 38)) 29 else 28
            else -> 30
        }
        INDIAN_CIVIL -> when (month) {
            1 -> if (leapGregorian(year + 78)) 31 else 30
            in 2..6 -> 31
            else -> 30
        }
        FRENCH_REPUBLICAN -> if (month <= 12) 30 else sansculottidesDays(year)
        CHINESE -> 29
        AYMARA -> AYMARA_MONTH_DAYS[month]
        MAPUCHE -> if (month == 9 && mapucheLeapYear(year)) 30 else MAPUCHE_MONTH_DAYS[month]
        INCA -> if (month == 3 && incaLeapYear(year)) INCA_MONTH_DAYS[month] + 1 else INCA_MONTH_DAYS[month]
        JAVANESE -> if (month in listOf(2, 4, 6, 8, 10, 12)) 29 else 30
        JAPANESE -> when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (leapGregorian(year - 660)) 29 else 28
            else -> 30
        }
    }

    fun firstDayOfMonth(year: Int, month: Int, day: Int = 1): LocalDate {
        val jd = when (this) {
            GREGORIAN -> gregorianToJd(LocalDate.of(year, month, day))
            JULIAN -> julianToJd(year, month, day)
            HEBREW -> hebrewToJd(year, month, day)
            ISLAMIC -> islamicToJd(year, month, day)
            PERSIAN -> persianToJd(year, month, day)
            PERSIAN_ASTRONOMICAL -> persianaToJd(year, month, day)
            MAYAN -> mayanCountToJd(0, 0, year, month - 1, day - 1)
            MAYAN_EXTENDED -> extendedMayanCountToJd(0, 0, 0, year, month - 1, day - 1, 0, 0)
            HISPANIC -> gregorianToJd(LocalDate.of(year - 38, month, day))
            INDIAN_CIVIL -> indianCivilToJd(year, month, day)
            FRENCH_REPUBLICAN -> frenchRepublicanToJd(year, month, 1, day)
            CHINESE -> chineseToJd(year, month, day, 0)
            AYMARA -> aymaraToJd(year, month, day)
            MAPUCHE -> mapucheToJd(year, month, day)
            INCA -> incaToJd(year, month, day)
            JAVANESE -> javaneseToJd(year, month, day)
            JAPANESE -> japaneseToJd(year, month, day)
        }
        return jdToGregorian(jd)
    }

    fun previousMonth(year: Int, month: Int): Pair<Int, Int> {
        val maxM = maxMonth()
        return if (month == 1) Pair(year - 1, maxM) else Pair(year, month - 1)
    }

    fun nextMonth(year: Int, month: Int): Pair<Int, Int> {
        val maxM = maxMonth()
        return if (month == maxM) Pair(year + 1, 1) else Pair(year, month + 1)
    }

    fun maxMonth(): Int = when (this) {
        GREGORIAN, JULIAN, ISLAMIC, PERSIAN, PERSIAN_ASTRONOMICAL, INDIAN_CIVIL,
        FRENCH_REPUBLICAN, AYMARA, INCA, JAVANESE, JAPANESE, HISPANIC -> 12
        HEBREW -> 13
        MAYAN, MAYAN_EXTENDED -> 18
        CHINESE -> 12
        MAPUCHE -> 13
    }

    companion object {
        private const val GREGORIAN_EPOCH = 1721425.5
        private val MAYAN_HAAB_MONTHS = listOf("Pop", "Uo", "Zip", "Zotz", "Tzec", "Xul",
            "Yaxkin", "Mol", "Chen", "Yax", "Zac", "Ceh", "Mac", "Kankin", "Muan",
            "Pax", "Kayab", "Cumku", "Uayeb")
        private val MAYAN_TZOLKIN_MONTHS = listOf("Imix", "Ik", "Akbal", "Kan", "Chicchan",
            "Cimi", "Manik", "Lamat", "Muluc", "Oc", "Chuen", "Eb", "Ben", "Ix", "Men",
            "Cib", "Caban", "Etznab", "Cauac", "Ahau")
        private val AYMARA_MONTH_DAYS = intArrayOf(0, 31, 31, 31, 30, 31, 30, 31, 31, 28, 31, 30, 30)
        private val MAPUCHE_MONTH_DAYS = intArrayOf(0, 28, 28, 28, 28, 28, 28, 28, 28, 29, 28, 28, 28, 28)
        private val INCA_MONTH_DAYS = intArrayOf(0, 31, 30, 30, 30, 30, 31, 30, 30, 30, 30, 30, 33)

        fun fromId(id: String): CalendarType = entries.firstOrNull { it.id == id } ?: GREGORIAN

        fun mayanHaabMonths(): List<String> = MAYAN_HAAB_MONTHS

        fun gregorianToJd(date: LocalDate): Double {
            val y = date.year
            val m = date.monthValue
            val d = date.dayOfMonth
            val correction = if (m <= 2) 0 else if (leapGregorian(y)) -1 else -2
            val dayPart = Math.floor(((367.0 * m) - 362) / 12 + correction + d)
            return (GREGORIAN_EPOCH - 1) +
                    365.0 * (y - 1) +
                    Math.floor((y - 1) / 4.0) -
                    Math.floor((y - 1) / 100.0) +
                    Math.floor((y - 1) / 400.0) +
                    dayPart
        }

        fun jdToGregorian(jd: Double): LocalDate {
            val wjd = Math.floor(jd - 0.5) + 0.5
            val depoch = wjd - 1721425.5
            val quadricent = Math.floor(depoch / 146097)
            val dqc = mod(depoch, 146097.0)
            val cent = Math.floor(dqc / 36524)
            val dcent = mod(dqc, 36524.0)
            val quad = Math.floor(dcent / 1461)
            val dquad = mod(dcent, 1461.0)
            val yindex = Math.floor(dquad / 365)
            var year = (quadricent * 400 + cent * 100 + quad * 4 + yindex).toInt()
            if (!(cent == 4.0 || yindex == 4.0)) year++
            val yearday = wjd - gregorianToJd(LocalDate.of(year, 1, 1))
            val leapadj = if (wjd < gregorianToJd(LocalDate.of(year, 3, 1))) 0.0
            else if (leapGregorian(year)) 1.0 else 2.0
            val month = (((yearday + leapadj) * 12 + 373) / 367).toInt()
            val day = (wjd - gregorianToJd(LocalDate.of(year, month, 1)) + 1).toInt()
            return LocalDate.of(year, month, day)
        }

        private fun mod(a: Double, b: Double): Double {
            var result = a % b
            if (result < 0) result += b
            return result
        }

        private fun mod(a: Int, b: Int): Int {
            var result = a % b
            if (result < 0) result += b
            return result
        }

        private fun leapGregorian(year: Int): Boolean =
            (year % 4 == 0) && (!(year % 100 == 0) || (year % 400 == 0))

        private fun julianToJd(year: Int, month: Int, day: Int): Double {
            var y = year
            var m = month
            if (y < 1) y++
            if (m <= 2) { y--; m += 12 }
            return Math.floor(365.25 * (y + 4716)) + Math.floor(30.6001 * (m + 1)) + day - 1524.5
        }

        private fun jdToJulian(jd: Double): IntArray {
            val td = jd + 0.5
            val z = Math.floor(td).toInt()
            val a = z
            val b = a + 1524
            val c = Math.floor((b - 122.1) / 365.25).toInt()
            val d = Math.floor(365.25 * c).toInt()
            val e = Math.floor((b - d) / 30.6001).toInt()
            val month = if (e < 14) e - 1 else e - 13
            val year = if (month > 2) c - 4716 else c - 4715
            val day = b - d - Math.floor(30.6001 * e).toInt()
            return intArrayOf(if (year < 1) year - 1 else year, month, day)
        }

        private fun hebrewLeap(year: Int): Boolean = mod((year * 7) + 1, 19) < 7

        private fun hebrewYearMonths(year: Int): Int = if (hebrewLeap(year)) 13 else 12

        private fun hebrewDelay1(year: Int): Int {
            val months = Math.floor(((235 * year) - 234) / 19.0).toInt()
            val parts = 12084 + 13753 * months
            var day = months * 29 + Math.floor(parts / 25920.0).toInt()
            if (mod(3 * (day + 1), 7) < 3) day++
            return day
        }

        private fun hebrewDelay2(year: Int): Int {
            val last = hebrewDelay1(year - 1)
            val present = hebrewDelay1(year)
            val next = hebrewDelay1(year + 1)
            return if ((next - present) == 356) 2 else if ((present - last) == 382) 1 else 0
        }

        private fun hebrewYearDays(year: Int): Int =
            (hebrewToJd(year + 1, 7, 1) - hebrewToJd(year, 7, 1)).toInt()

        private fun hebrewMonthDays(year: Int, month: Int): Int {
            if (month in listOf(2, 4, 6, 10, 13)) return 29
            if (month == 12 && !hebrewLeap(year)) return 29
            if (month == 8 && (mod(hebrewYearDays(year), 10) != 5)) return 29
            if (month == 9 && (mod(hebrewYearDays(year), 10) == 3)) return 29
            return 30
        }

        private fun hebrewToJd(year: Int, month: Int, day: Int): Double {
            var jd = 347995.5 + hebrewDelay1(year) + hebrewDelay2(year) + day + 1
            val months = hebrewYearMonths(year)
            if (month < 7) {
                for (mon in 7..months) jd += hebrewMonthDays(year, mon)
                for (mon in 1 until month) jd += hebrewMonthDays(year, mon)
            } else {
                for (mon in 7 until month) jd += hebrewMonthDays(year, mon)
            }
            return jd
        }

        private fun jdToHebrew(jd: Double): IntArray {
            val j = Math.floor(jd) + 0.5
            var count = Math.floor(((j - 347995.5) * 98496.0) / 35975351.0).toInt()
            var year = count - 1
            var i = count
            while (j >= hebrewToJd(i, 7, 1)) { year++; i++ }
            val first = if (j < hebrewToJd(year, 1, 1)) 7 else 1
            var month = first
            i = first
            while (j > hebrewToJd(year, i, hebrewMonthDays(year, i))) { month++; i++ }
            val day = (j - hebrewToJd(year, month, 1) + 1).toInt()
            return intArrayOf(year, month, day)
        }

        private fun leapIslamic(year: Int): Boolean = (((year * 11) + 14) % 30) < 11

        private fun islamicToJd(year: Int, month: Int, day: Int): Double {
            return day + Math.ceil(29.5 * (month - 1)) + (year - 1) * 354 +
                    Math.floor((3 + (11 * year)) / 30.0) + 1948439.5 - 1
        }

        private fun jdToIslamic(jd: Double): IntArray {
            val j = Math.floor(jd) + 0.5
            val year = Math.floor(((30 * (j - 1948439.5)) + 10646) / 10631.0).toInt()
            val month = Math.min(12, Math.ceil((j - (29 + islamicToJd(year, 1, 1))) / 29.5).toInt() + 1)
            val day = (j - islamicToJd(year, month, 1) + 1).toInt()
            return intArrayOf(year, month, day)
        }

        private fun persianToJd(year: Int, month: Int, day: Int): Double {
            val epbase = year - if (year >= 0) 474 else 473
            val epyear = 474 + mod(epbase, 2820)
            return (day + (if (month <= 7) (month - 1) * 31 else (month - 1) * 30 + 6) +
                    Math.floor(((epyear * 682) - 110) / 2816.0) + (epyear - 1) * 365 +
                    Math.floor(epbase / 2820.0) * 1029983 + (1948320.5 - 1))
        }

        private fun jdToPersian(jd: Double): IntArray {
            val j = Math.floor(jd) + 0.5
            val depoch = j - persianToJd(475, 1, 1)
            val cycle = Math.floor(depoch / 1029983.0).toInt()
            val cyear = mod(depoch, 1029983.0).toInt()
            val ycycle = if (cyear == 1029982) 2820
            else {
                val aux1 = Math.floor(cyear / 366.0).toInt()
                val aux2 = mod(cyear, 366)
                Math.floor(((2134 * aux1) + (2816 * aux2) + 2815) / 1028522.0).toInt() + aux1 + 1
            }
            var year = ycycle + 2820 * cycle + 474
            if (year <= 0) year--
            val yday = (j - persianToJd(year, 1, 1) + 1).toInt()
            val month = if (yday <= 186) Math.ceil(yday / 31.0).toInt() else Math.ceil((yday - 6) / 30.0).toInt()
            val day = (j - persianToJd(year, month, 1) + 1).toInt()
            return intArrayOf(year, month, day)
        }

        private fun tehranEquinox(year: Int): Double {
            val equJED = equinox(year, 0)
            val equJD = equJED - deltat(year) / 86400.0
            val equAPP = equJD + equationOfTime(equJED)
            return equAPP + (52 + 30 / 60.0) / 360
        }

        private fun tehranEquinoxJd(year: Int): Double = Math.floor(tehranEquinox(year))

        private fun equinox(year: Int, equinoxType: Int): Double {
            return gregorianToJd(LocalDate.of(year, 3, 20)) + 689779.45 / 133685.0 +
                    0.002033 * (year - 1900) + 0.51348 * (equinoxType + 1) -
                    0.000039 * (year - 1900) * (year - 1900) + deltat(year) / 86400.0
        }

        private fun deltat(year: Int): Double {
            return if (year >= 2100) 32.16 + 3.2 * (year - 2100)
            else 32.16 + 3.2 * (year - 1987)
        }

        private fun equationOfTime(jd: Double): Double {
            val t = (jd - 2451545.0) / 36525.0
            return 2.0 * Math.PI * (0.000075 + 0.001868 * Math.cos(Math.toRadians(359.991 * t)) -
                    0.032077 * Math.sin(Math.toRadians(359.991 * t)) +
                    0.000295 * Math.cos(Math.toRadians(360.0 * t)) -
                    0.001326 * Math.sin(Math.toRadians(360.0 * t))) / Math.PI
        }

        private fun persianaToJd(year: Int, month: Int, day: Int): Double {
            val guess = 1948320.5 + 365.24219 * (year - 1 - 1)
            var lasteq = tehranEquinoxJd(guess.toInt() - 2)
            var g = guess.toInt() - 2
            while (lasteq > guess) { g--; lasteq = tehranEquinoxJd(g) }
            var nexteq = lasteq - 1
            while (!((lasteq <= guess) && (guess < nexteq))) {
                lasteq = nexteq; g++; nexteq = tehranEquinoxJd(g)
            }
            val eq = lasteq
            return eq + (if (month <= 7) (month - 1) * 31 else (month - 1) * 30 + 6) + day - 1
        }

        private fun persianaYear(jd: Double): IntArray {
            val guess = jdToGregorian(jd).year - 2
            var lasteq = tehranEquinoxJd(guess)
            var g = guess
            while (lasteq > jd) { g--; lasteq = tehranEquinoxJd(g) }
            var nexteq = lasteq - 1
            while (!((lasteq <= jd) && (jd < nexteq))) {
                lasteq = nexteq; g++; nexteq = tehranEquinoxJd(g)
            }
            return intArrayOf(Math.round((lasteq - 1948320.5) / 365.24219).toInt() + 1, lasteq.toInt())
        }

        private fun jdToPersiana(jd: Double): IntArray {
            val j = Math.floor(jd) + 0.5
            val adr = persianaYear(j)
            val year = adr[0]
            val yday = (j - persianaToJd(year, 1, 1) + 1).toInt()
            val month = if (yday <= 186) Math.ceil(yday / 31.0).toInt() else Math.ceil((yday - 6) / 30.0).toInt()
            val day = (j - persianaToJd(year, month, 1) + 1).toInt()
            return intArrayOf(year, month, day)
        }

        fun mayanCountToJd(baktun: Int, katun: Int, tun: Int, uinal: Int, kin: Int): Double {
            return 584282.5 + baktun * 144000 + katun * 7200 + tun * 360 + uinal * 20 + kin
        }

        fun jdToMayanCount(jd: Double): IntArray {
            var d = Math.floor(jd).toInt()
            if (d < 584282) d = 584282
            val v = d - 584282
            val baktun = v / 144000
            val r1 = v % 144000
            val katun = r1 / 7200
            val r2 = r1 % 7200
            val tun = r2 / 360
            val r3 = r2 % 360
            val uinal = r3 / 20
            val kin = r3 % 20
            return intArrayOf(0, 0, 0, baktun, katun, tun, uinal, kin)
        }

        fun extendedMayanCountToJd(kinchiltun: Int, calabtun: Int, pictun: Int, baktun: Int, katun: Int, tun: Int, uinal: Int, kin: Int): Double {
            val common = mayanCountToJd(baktun, katun, tun, uinal, kin)
            if (kinchiltun == 0 && calabtun == 0 && pictun == 0) return common
            val excess = (kinchiltun * 1152000000) + (calabtun * 57600000) + (pictun * 2880000)
            return common - excess
        }

        fun jdToExtendedMayanCount(jd: Double): IntArray {
            var d = Math.floor(jd).toInt()
            val kinchiltun = d / 1152000000
            d = mod(d, 1152000000)
            val calabtun = d / 57600000
            d = mod(d, 57600000)
            val pictun = d / 2880000
            d = mod(d, 2880000)
            val baktun = d / 144000
            d = mod(d, 144000)
            val katun = d / 7200
            d = mod(d, 7200)
            val tun = d / 360
            d = mod(d, 360)
            val uinal = d / 20
            val kin = mod(d, 20)
            return intArrayOf(kinchiltun, calabtun, pictun, baktun, katun, tun, uinal, kin)
        }

        private fun indianCivilToJd(year: Int, month: Int, day: Int): Double {
            val gyear = year + 78
            val leap = leapGregorian(gyear)
            val start = gregorianToJd(LocalDate.of(gyear, 3, if (leap) 21 else 22))
            val Caitra = if (leap) 31 else 30
            return if (month == 1) start + day - 1
            else {
                var jd = start + Caitra
                var m = month - 2
                m = Math.min(m, 5)
                jd += m * 31
                if (month >= 8) { m = month - 7; jd += m * 30 }
                jd + day - 1
            }
        }

        private fun jdToIndianCivil(jd: Double): IntArray {
            val j = Math.floor(jd) + 0.5
            val greg = jdToGregorian(j)
            val leap = leapGregorian(greg.year)
            val Saka = 78
            val start = 80
            var year = greg.year - Saka
            val greg0 = gregorianToJd(LocalDate.of(greg.year, 1, 1))
            val yday = (j - greg0).toInt()
            val Caitra = if (leap) 31 else 30
            var actualYday = yday
            var actualYear = year
            if (yday < start) {
                actualYear--
                actualYday += Caitra + 31 * 5 + 30 * 3 + 10 + start
            }
            actualYday -= start
            return if (actualYday < Caitra) intArrayOf(actualYear, 1, actualYday + 1)
            else {
                val mday = actualYday - Caitra
                if (mday < 31 * 5) intArrayOf(actualYear, mday / 31 + 2, mday % 31 + 1)
                else {
                    val mday2 = mday - 31 * 5
                    intArrayOf(actualYear, mday2 / 30 + 7, mday2 % 30 + 1)
                }
            }
        }

        private fun leapFrenchRepublican(year: Int): Boolean {
            val drift = (year * 303 + 3) / 1205
            return mod(year * 303 + 3 - drift, 1205) == 0
        }

        private fun sansculottidesDays(year: Int): Int = if (leapFrenchRepublican(year)) 6 else 5

        private fun frenchRepublicanToJd(year: Int, month: Int, decade: Int, day: Int): Double {
            val guess = 2375839.5 + 365.2422 * (year - 1 - 1)
            var g = (guess - 2).toInt()
            var lasteq = parisEquinoxeJd(g)
            while (lasteq > guess) { g--; lasteq = parisEquinoxeJd(g) }
            var nexteq = lasteq - 1
            while (!((lasteq <= guess) && (guess < nexteq))) {
                lasteq = nexteq; g++; nexteq = parisEquinoxeJd(g)
            }
            val equinoxe = lasteq
            return equinoxe + (30 * (month - 1)) + (10 * (decade - 1)) + (day - 1)
        }

        private fun parisEquinoxeJd(year: Int): Double = Math.floor(equinoxeAParis(year) - 0.5) + 0.5

        private fun equinoxeAParis(year: Int): Double {
            val equJED = equinox(year, 2)
            val equJD = equJED - deltat(year) / 86400.0
            val equAPP = equJD + equationOfTime(equJED)
            return equAPP + (2 + 20 / 60.0 + 15 / 3600.0) / 360
        }

        private fun jdToFrenchRepublican(jd: Double): IntArray {
            val j = Math.floor(jd) + 0.5
            val guess = jdToGregorian(j).year - 2
            var g = guess
            var lasteq = parisEquinoxeJd(g)
            while (lasteq > j) { g--; lasteq = parisEquinoxeJd(g) }
            var nexteq = lasteq - 1
            while (!((lasteq <= j) && (j < nexteq))) {
                lasteq = nexteq; g++; nexteq = parisEquinoxeJd(g)
            }
            val an = Math.round((lasteq - 2375839.5) / 365.2422).toInt() + 1
            val equinoxe = lasteq
            val mois = Math.floor((j - equinoxe) / 30).toInt() + 1
            val jour = ((j - equinoxe) % 30).toInt()
            val decade = jour / 10 + 1
            val day = jour % 10 + 1
            return intArrayOf(an, mois, decade, day)
        }

        private fun chineseToJd(year: Int, month: Int, day: Int, leap: Int): Double {
            val yG = year - 2698
            val jd = gregorianToJd(LocalDate.of(if (yG < 0) yG - 1 else yG, 2, 1))
            val ws1 = chineseWinterSolstice(jd - 180)
            val ws2 = chineseWinterSolstice(ws1 + 360)
            val result = buildChineseMonthMap(ws1, ws2)
            for (m in result.monthMap) {
                if (m.monthNum == month && m.isLeap == (leap == 1)) {
                    return m.nmStart + day - 1
                }
            }
            return Math.floor(jd) + 0.5
        }

        fun jdToChinese(jd: Double): IntArray {
            val jdNoon = Math.floor(jd) + 0.5
            val yG = jdToGregorian(jdNoon).year
            var ws1 = chineseWinterSolstice(gregorianToJd(LocalDate.of(yG - 1, 6, 1)))
            var ws2 = chineseWinterSolstice(ws1 + 360)
            if (jdNoon < ws1) {
                ws1 = chineseWinterSolstice(ws1 - 360)
                ws2 = chineseWinterSolstice(ws1 + 360)
            } else if (jdNoon >= ws2) {
                ws1 = ws2
                ws2 = chineseWinterSolstice(ws1 + 360)
            }
            val result = buildChineseMonthMap(ws1, ws2)
            var bestMonth = result.monthMap.firstOrNull { it.nmStart <= jdNoon && jdNoon < it.nmEnd }
            if (bestMonth == null && result.monthMap.isNotEmpty()) {
                bestMonth = result.monthMap.last()
            }
            if (bestMonth != null) {
                val month = bestMonth.monthNum
                val isLeap = if (bestMonth.isLeap) 1 else 0
                val day = (jdNoon - bestMonth.nmStart).toInt() + 1
                val base = jdToGregorian(ws1).year + 2698
                val y = if (month >= 11) base else base + 1
                return intArrayOf(y, month, day, isLeap)
            }
            return intArrayOf(0, 0, 0, 0)
        }

        private data class ChineseMonth(
            val nmStart: Double,
            val nmEnd: Double,
            val hasMajor: Boolean,
            var monthNum: Int = 0,
            var isLeap: Boolean = false
        )

        private data class ChineseMonthMapResult(val monthMap: List<ChineseMonth>, val eleventhIdx: Int)

        private fun buildChineseMonthMap(ws1: Double, ws2: Double): ChineseMonthMapResult {
            val nmList = mutableListOf<Double>()
            var nm = newMoonBefore(ws1)
            while (nm < ws2) { nmList.add(nm); nm = newMoonAtOrAfter(nm + 20) }
            nmList.add(nm)
            var eleventhIdx = 0
            while (eleventhIdx + 1 < nmList.size && nmList[eleventhIdx] <= ws1 && nmList[eleventhIdx + 1] <= ws1) {
                eleventhIdx++
            }
            val monthMap = mutableListOf<ChineseMonth>()
            for (i in 0 until nmList.size - 1) {
                monthMap.add(ChineseMonth(nmList[i], nmList[i + 1], monthHasMajorTerm(nmList[i], nmList[i + 1])))
            }
            var curNum = 11
            for (i in eleventhIdx until monthMap.size) {
                if (i > eleventhIdx) { curNum++; if (curNum > 12) curNum = 1 }
                if (!monthMap[i].hasMajor) {
                    monthMap[i].isLeap = true
                    monthMap[i].monthNum = curNum - 1
                    if (monthMap[i].monthNum < 1) monthMap[i].monthNum = 12
                } else {
                    monthMap[i].isLeap = false
                    monthMap[i].monthNum = curNum
                }
            }
            curNum = 10
            for (i in eleventhIdx - 1 downTo 0) {
                if (!monthMap[i].hasMajor) {
                    monthMap[i].isLeap = true
                    monthMap[i].monthNum = curNum
                } else {
                    monthMap[i].isLeap = false
                    monthMap[i].monthNum = curNum
                    curNum--
                    if (curNum < 1) curNum = 12
                }
            }
            return ChineseMonthMapResult(monthMap, eleventhIdx)
        }

        private fun monthHasMajorTerm(nmStart: Double, nmEnd: Double): Boolean {
            val sStart = mod(sunLongitude(nmStart), 360.0)
            var sEnd = sunLongitude(nmEnd)
            while (sEnd < sStart) sEnd += 360
            for (m in 0 until 12) {
                var term = (m * 30).toDouble()
                if (term < sStart) term += 360
                if (term >= sStart && term < sEnd) return true
            }
            return false
        }

        private fun sunLongitude(jd: Double): Double {
            val t = (jd - 2451545.0) / 36525.0
            val t2 = t * t
            val l0 = 280.46646 + 36000.76983 * t + 0.0003032 * t2
            val m = Math.toRadians(mod(357.52911 + 35999.05029 * t - 0.0001537 * t2, 360.0))
            val c = (1.9146 - 0.004817 * t - 0.000014 * t2) * Math.sin(m) +
                    (0.019993 - 0.000101 * t) * Math.sin(2 * m) + 0.00029 * Math.sin(3 * m)
            return mod(l0 + c, 360.0)
        }

        private fun chineseWinterSolstice(jd: Double): Double {
            val y = jdToGregorian(jd).year
            var guess = gregorianToJd(LocalDate.of(y, 12, 1)) + 20
            var s = sunLongitude(guess)
            var iter = 0
            while ((s > 275 || s < 265) && iter < 50) {
                iter++
                var diff = 270.0 - s
                if (diff > 180) diff -= 360
                if (diff < -180) diff += 360
                guess += diff / 0.986
                s = sunLongitude(guess)
            }
            return Math.floor(guess) + 0.5
        }

        private fun newMoonTime(k: Int): Double {
            val T = k / 1236.85
            val T2 = T * T
            val T3 = T2 * T
            val T4 = T3 * T
            var JDE = 2451550.09766 + 29.530588861 * k + 0.00015437 * T2 - 0.00000015 * T3 + 0.00000000073 * T4
            val E = 1 - 0.002516 * T - 0.0000074 * T2
            val Mm = Math.toRadians(mod(201.5643 + 385.81693528 * k + 0.0107438 * T2 + 0.00001239 * T3, 360.0))
            val M = Math.toRadians(mod(2.5534 + 29.10535670 * k - 0.0000014 * T2, 360.0))
            val F = Math.toRadians(mod(160.7108 + 390.67050284 * k - 0.0016118 * T2, 360.0))
            val Om = Math.toRadians(mod(124.7746 - 1.56375588 * k + 0.0020672 * T2, 360.0))
            val coefs = doubleArrayOf(-0.40720, 0.17241, 0.01608, 0.01039, 0.00739,
                -0.00514, 0.00208, -0.00111, -0.00057, 0.00056,
                -0.00042, 0.00042, 0.00038, -0.00024, -0.00017,
                -0.00007, 0.00004, 0.00004, 0.00003, 0.00003,
                -0.00003, 0.00003, -0.00002, -0.00002, 0.00002)
            val args = doubleArrayOf(Mm, M, 2 * Mm, 2 * F, Mm - M, Mm + M, 2 * M, Mm - 2 * F,
                Mm + 2 * F, 2 * Mm + M, 3 * Mm, M + 2 * F, M - 2 * F, 2 * Mm - M, Om,
                Mm + 2 * M, 2 * Mm - 2 * F, 3 * M, Mm + M - 2 * F, 2 * Mm + 2 * F,
                Mm + M + 2 * F, Mm - M + 2 * F, Mm - M - 2 * F, 3 * Mm + M, 4 * Mm)
            for (i in coefs.indices) JDE += coefs[i] * Math.sin(args[i])
            return JDE
        }

        private fun newMoonBefore(jd: Double): Double {
            var k = Math.round((jd - 2451550.09766) / 29.530588861).toInt()
            var nm = newMoonTime(k)
            if (nm > jd) nm = newMoonTime(k - 1)
            return nm
        }

        private fun newMoonAtOrAfter(jd: Double): Double {
            var k = Math.round((jd - 2451550.09766) / 29.530588861).toInt()
            var nm = newMoonTime(k)
            if (nm < jd) nm = newMoonTime(k + 1)
            return nm
        }

        private fun aymaraLeapYear(year: Int): Boolean = leapGregorian(year - 3507)

        private fun aymaraToJd(year: Int, month: Int, day: Int): Double {
            val gregYear = year - 3508
            val jdStart = gregorianToJd(LocalDate.of(gregYear, 6, 21))
            var doy = 0
            for (m in 1 until month) {
                doy += AYMARA_MONTH_DAYS[m]
                if (m == 9 && aymaraLeapYear(year)) doy++
            }
            return jdStart + doy + day - 1
        }

        private fun jdToAymara(jd: Double): IntArray {
            val j = Math.floor(jd) + 0.5
            val greg = jdToGregorian(j)
            var gregYear = greg.year
            var jdStart = gregorianToJd(LocalDate.of(gregYear, 6, 21))
            if (j < jdStart) { gregYear--; jdStart = gregorianToJd(LocalDate.of(gregYear, 6, 21)) }
            val year = gregYear + 3508
            val doy = (j - jdStart).toInt()
            var month = 1
            var day = 1
            var cum = 0
            for (m in 1..12) {
                var md = AYMARA_MONTH_DAYS[m]
                if (m == 9 && aymaraLeapYear(year)) md++
                if (doy < cum + md) { month = m; day = doy - cum + 1; break }
                cum += md
            }
            return intArrayOf(year, month, day)
        }

        private fun mapucheLeapYear(year: Int): Boolean = leapGregorian(year - 10466)

        private fun mapucheToJd(year: Int, month: Int, day: Int): Double {
            val gregYear = year - 10467
            val jdStart = gregorianToJd(LocalDate.of(gregYear, 6, 21))
            var doy = 0
            for (m in 1 until month) {
                doy += MAPUCHE_MONTH_DAYS[m]
                if (m == 9 && mapucheLeapYear(year)) doy++
            }
            return jdStart + doy + day - 1
        }

        private fun jdToMapuche(jd: Double): IntArray {
            val j = Math.floor(jd) + 0.5
            val greg = jdToGregorian(j)
            var gregYear = greg.year
            var jdStart = gregorianToJd(LocalDate.of(gregYear, 6, 21))
            if (j < jdStart) { gregYear--; jdStart = gregorianToJd(LocalDate.of(gregYear, 6, 21)) }
            val year = gregYear + 10467
            val doy = (j - jdStart).toInt()
            var month = 1
            var day = 1
            var cum = 0
            for (m in 1..13) {
                var md = MAPUCHE_MONTH_DAYS[m]
                if (m == 9 && mapucheLeapYear(year)) md++
                if (doy < cum + md) { month = m; day = doy - cum + 1; break }
                cum += md
            }
            return intArrayOf(year, month, day)
        }

        private fun incaLeapYear(year: Int): Boolean = leapGregorian(year + 1438)

        private fun incaToJd(year: Int, month: Int, day: Int): Double {
            val gregYear = year + 1437
            val jdStart = gregorianToJd(LocalDate.of(gregYear, 12, 21))
            var doy = 0
            for (m in 1 until month) {
                doy += INCA_MONTH_DAYS[m]
                if (m == 3 && incaLeapYear(year)) doy++
            }
            return jdStart + doy + day - 1
        }

        private fun jdToInca(jd: Double): IntArray {
            val j = Math.floor(jd) + 0.5
            val greg = jdToGregorian(j)
            var gregYear = greg.year
            var jdStart = gregorianToJd(LocalDate.of(gregYear, 12, 21))
            if (j < jdStart) { gregYear--; jdStart = gregorianToJd(LocalDate.of(gregYear, 12, 21)) }
            val year = gregYear - 1437
            val doy = (j - jdStart).toInt()
            var month = 1
            var day = 1
            var cum = 0
            for (m in 1..12) {
                var md = INCA_MONTH_DAYS[m]
                if (m == 3 && incaLeapYear(year)) md++
                if (doy < cum + md) { month = m; day = doy - cum + 1; break }
                cum += md
            }
            return intArrayOf(year, month, day)
        }

        private fun javaneseToJd(year: Int, month: Int, day: Int): Double =
            islamicToJd(year - 512, month, day)

        private fun jdToJavanese(jd: Double): IntArray {
            val isl = jdToIslamic(jd)
            return intArrayOf(isl[0] + 512, isl[1], isl[2])
        }

        private fun japaneseToJd(year: Int, month: Int, day: Int): Double =
            gregorianToJd(LocalDate.of(year - 660, month, day))

        private fun jdToJapanese(jd: Double): IntArray {
            val greg = jdToGregorian(jd)
            return intArrayOf(greg.year + 660, greg.monthValue, greg.dayOfMonth)
        }
    }
}
