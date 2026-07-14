// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val BREAK_TIME_KEY = intPreferencesKey("break_time")
        private val LAST_ROUTE_KEY = stringPreferencesKey("last_route")
        private val SCORE_KEY = intPreferencesKey("score")
        private val STREAK_COUNT_KEY = intPreferencesKey("streak_count")
        private val LAST_COMPLETED_DATE_KEY = stringPreferencesKey("last_completed_date")
        private val COMPLETED_DATES_KEY = stringSetPreferencesKey("completed_dates")
        private val STREAK_DAYS_KEY = stringSetPreferencesKey("streak_days")
        private val REMINDER_ENABLED_KEY = booleanPreferencesKey("reminder_enabled")
        private val REMINDER_HOUR_KEY = intPreferencesKey("reminder_hour")
        private val REMINDER_MINUTE_KEY = intPreferencesKey("reminder_minute")
        private val BREAK_START_TIME_KEY = longPreferencesKey("break_start_time")
        private val SKIN_COLOR_KEY = stringPreferencesKey("skin_color")
        private val FIRST_STEPS_SCROLL_KEY = intPreferencesKey("first_steps_scroll")
        private val WORKOUT_SCROLL_KEY = intPreferencesKey("workout_scroll")
        private val ABACUS_SCROLL_KEY = intPreferencesKey("abacus_scroll")
    }

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "en-US"
    }

    val breakTime: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[BREAK_TIME_KEY] ?: 15
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    suspend fun setBreakTime(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[BREAK_TIME_KEY] = minutes
        }
    }

    val breakStartTime: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[BREAK_START_TIME_KEY] ?: 0L
    }

    suspend fun setBreakStartTime(time: Long) {
        context.dataStore.edit { preferences ->
            preferences[BREAK_START_TIME_KEY] = time
        }
    }

    val skinColor: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[SKIN_COLOR_KEY] ?: "#FFF8E0"
    }

    suspend fun setSkinColor(color: String) {
        context.dataStore.edit { preferences ->
            preferences[SKIN_COLOR_KEY] = color
        }
    }

    val lastRoute: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LAST_ROUTE_KEY] ?: "index"
    }

    suspend fun setLastRoute(route: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_ROUTE_KEY] = route
        }
    }

    val score: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[SCORE_KEY] ?: 0
    }

    suspend fun setScore(score: Int) {
        context.dataStore.edit { preferences ->
            preferences[SCORE_KEY] = score
        }
    }

    val streakCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[STREAK_COUNT_KEY] ?: 0
    }

    suspend fun setStreakCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[STREAK_COUNT_KEY] = count
        }
    }

    val lastCompletedDate: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LAST_COMPLETED_DATE_KEY]
    }

    suspend fun setLastCompletedDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_COMPLETED_DATE_KEY] = date
        }
    }

    val completedDates: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[COMPLETED_DATES_KEY] ?: emptySet()
    }

    suspend fun setCompletedDates(dates: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[COMPLETED_DATES_KEY] = dates
        }
    }

    val streakDays: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[STREAK_DAYS_KEY] ?: setOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY")
    }

    suspend fun setStreakDays(days: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[STREAK_DAYS_KEY] = days
        }
    }

    val reminderEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[REMINDER_ENABLED_KEY] ?: true
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_ENABLED_KEY] = enabled
        }
    }

    val reminderHour: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[REMINDER_HOUR_KEY] ?: 18
    }

    suspend fun setReminderHour(hour: Int) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_HOUR_KEY] = hour
        }
    }

    val reminderMinute: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[REMINDER_MINUTE_KEY] ?: 0
    }

    suspend fun setReminderMinute(minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_MINUTE_KEY] = minute
        }
    }

    val firstStepsScroll: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[FIRST_STEPS_SCROLL_KEY] ?: 0
    }

    suspend fun setFirstStepsScroll(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_STEPS_SCROLL_KEY] = value
        }
    }

    val workoutScroll: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[WORKOUT_SCROLL_KEY] ?: 0
    }

    suspend fun setWorkoutScroll(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[WORKOUT_SCROLL_KEY] = value
        }
    }

    val abacusScroll: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[ABACUS_SCROLL_KEY] ?: 0
    }

    suspend fun setAbacusScroll(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[ABACUS_SCROLL_KEY] = value
        }
    }

    suspend fun recordLessonCompletion() {
        val today = java.time.LocalDate.now()
        val todayStr = today.toString()
        context.dataStore.edit { prefs ->
            val currentDates = prefs[COMPLETED_DATES_KEY] ?: emptySet()
            if (todayStr in currentDates) return@edit
            val selectedDays = prefs[STREAK_DAYS_KEY] ?: setOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY")
            val lastDateStr = prefs[LAST_COMPLETED_DATE_KEY]
            val currentStreak = prefs[STREAK_COUNT_KEY] ?: 0
            var missedSelectedDay = false
            if (lastDateStr != null) {
                val lastDate = java.time.LocalDate.parse(lastDateStr)
                var checkDate = lastDate.plusDays(1)
                while (checkDate.isBefore(today)) {
                    val dayName = checkDate.dayOfWeek.name
                    if (dayName in selectedDays && checkDate.toString() !in currentDates) {
                        missedSelectedDay = true
                        break
                    }
                    checkDate = checkDate.plusDays(1)
                }
            }
            prefs[STREAK_COUNT_KEY] = if (missedSelectedDay) 1 else currentStreak + 1
            prefs[LAST_COMPLETED_DATE_KEY] = todayStr
            prefs[COMPLETED_DATES_KEY] = currentDates + todayStr
        }
    }
}
