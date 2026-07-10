// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
    }

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "en-US"
    }

    val breakTime: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[BREAK_TIME_KEY] ?: 30
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

    suspend fun recordLessonCompletion() {
        val today = java.time.LocalDate.now().toString()
        val yesterday = java.time.LocalDate.now().minusDays(1).toString()
        context.dataStore.edit { prefs ->
            val currentDates = prefs[COMPLETED_DATES_KEY] ?: emptySet()
            if (today in currentDates) return@edit
            val lastDate = prefs[LAST_COMPLETED_DATE_KEY]
            val currentStreak = prefs[STREAK_COUNT_KEY] ?: 0
            val newStreak = if (lastDate == null || lastDate == yesterday) currentStreak + 1
            else 1
            prefs[STREAK_COUNT_KEY] = newStreak
            prefs[LAST_COMPLETED_DATE_KEY] = today
            prefs[COMPLETED_DATES_KEY] = currentDates + today
        }
    }
}
