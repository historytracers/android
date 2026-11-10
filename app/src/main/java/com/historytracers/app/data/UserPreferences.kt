// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

fun detectDefaultLanguage(locale: Locale = Locale.getDefault()): String {
    val lang = locale.language.lowercase()
    return when {
        lang.startsWith("pt") -> "pt-BR"
        lang.startsWith("es") -> "es-ES"
        lang.startsWith("en") -> "en-US"
        else -> "en-US"
    }
}

fun detectDefaultCalendar(locale: Locale = Locale.getDefault()): String {
    val lang = locale.language.lowercase()
    return when {
        lang == "he" -> "hebrew"
        lang == "ar" -> "islamic"
        lang == "fa" -> "persian"
        lang == "zh" -> "chinese"
        lang == "ja" -> "japanese"
        lang == "hi" -> "indian_civil"
        lang.startsWith("es") -> "hispanic"
        else -> "gregorian"
    }
}

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
        private val YUPANA_SCROLL_KEY = intPreferencesKey("yupana_scroll")
        private val ANOTHER_WAY_TO_COUNT_SCROLL_KEY = intPreferencesKey("another_way_to_count_scroll")
        private val ROAD_TO_SOMEWHERE_SCROLL_KEY = intPreferencesKey("road_to_somewhere_scroll")
        private val WHERE_ARE_WE_FROM_SCROLL_KEY = intPreferencesKey("where_are_we_from_scroll")
        private val UNIVERSE_EXPANSION_STEP_KEY = intPreferencesKey("universe_expansion_step")
        private val QUIPU_PRACTICE_STATE_KEY = stringPreferencesKey("quipu_practice_state")
        private val WORKOUT_SECTIONS_KEY = stringSetPreferencesKey("workout_sections")
        private val FIRST_STEPS_SECTIONS_KEY = stringSetPreferencesKey("first_steps_sections")
        private val WHO_AM_I_SECTIONS_KEY = stringSetPreferencesKey("who_am_i_sections")
        private val WHO_AM_I_LESSONS_MIGRATED_KEY = booleanPreferencesKey("who_am_i_lessons_migrated")
        private val ABACUS_SECTIONS_KEY = stringSetPreferencesKey("abacus_sections")
        private val YUPANA_SECTIONS_KEY = stringSetPreferencesKey("yupana_sections")
        private val I_AM_NOT_LIKE_YOU_SECTIONS_KEY = stringSetPreferencesKey("i_am_not_like_you_sections")
        private val ROAD_TO_SOMEWHERE_SECTIONS_KEY = stringSetPreferencesKey("road_to_somewhere_sections")
        private val RETURNING_SECTIONS_KEY = stringSetPreferencesKey("returning_sections")
        private val MEASURING_SECTIONS_KEY = stringSetPreferencesKey("measuring_sections")
        private val RUNNING_AND_GROWING_SECTIONS_KEY = stringSetPreferencesKey("running_and_growing_sections")
        private val WHERE_ARE_WE_FROM_SECTIONS_KEY = stringSetPreferencesKey("where_are_we_from_sections")
        private val ANOTHER_WAY_TO_COUNT_SECTIONS_KEY = stringSetPreferencesKey("another_way_to_count_sections")
        private val AROUND_THE_WORLD_SECTIONS_KEY = stringSetPreferencesKey("around_the_world_sections")
        private val AROUND_THE_WORLD_ADDITION_SECTIONS_KEY = stringSetPreferencesKey("around_the_world_addition_sections")
        private val LONG_DIVISION_SECTIONS_KEY = stringSetPreferencesKey("long_division_sections")
        private val MULTIPLICATION_TABLE_SECTIONS_KEY = stringSetPreferencesKey("multiplication_table_sections")
        private val CLAIMED_LEVELS_KEY = stringSetPreferencesKey("claimed_levels")
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        private val SEEN_NEW_HUBS_KEY = stringSetPreferencesKey("seen_new_hubs")
        private val AWARDED_SCREENS_KEY = stringSetPreferencesKey("awarded_screens")
        private val ARRIVAL_AWARDED_SCREENS_KEY = stringSetPreferencesKey("arrival_awarded_screens")
        private val CALENDAR_KEY = stringPreferencesKey("calendar")
        private val YUPANA_LESSONS_MIGRATED_KEY = booleanPreferencesKey("yupana_lessons_migrated_to_another_way_to_count")

        private val DRIVE_SYNC_ENABLED_KEY = booleanPreferencesKey("drive_sync_enabled")
        private val DRIVE_SYNC_ACCOUNT_KEY = stringPreferencesKey("drive_sync_account")
        private val DRIVE_SYNC_LAST_SYNC_KEY = longPreferencesKey("drive_sync_last_sync")
        private val DRIVE_SYNC_RESET_PENDING_KEY = booleanPreferencesKey("drive_sync_reset_pending")
        private val DRIVE_SYNC_RESET_ACCOUNT_KEY = stringPreferencesKey("drive_sync_reset_account")
        private val DRIVE_SYNC_RESET_VERSION_KEY = intPreferencesKey("drive_sync_reset_version")

        private const val BACKUP_SCHEMA_VERSION = 1
        private val BACKUP_GSON = Gson()

        // Device-local settings: never uploaded to Drive and never restored.
        private val DRIVE_SYNC_LOCAL_ONLY_KEYS = setOf(
            DRIVE_SYNC_ENABLED_KEY.name,
            DRIVE_SYNC_ACCOUNT_KEY.name,
            DRIVE_SYNC_LAST_SYNC_KEY.name,
            DRIVE_SYNC_RESET_PENDING_KEY.name,
            DRIVE_SYNC_RESET_ACCOUNT_KEY.name,
            LAST_ROUTE_KEY.name,
            BREAK_START_TIME_KEY.name,
            FIRST_STEPS_SCROLL_KEY.name,
            WORKOUT_SCROLL_KEY.name,
            ABACUS_SCROLL_KEY.name,
            YUPANA_SCROLL_KEY.name,
            ANOTHER_WAY_TO_COUNT_SCROLL_KEY.name,
            ROAD_TO_SOMEWHERE_SCROLL_KEY.name,
            WHERE_ARE_WE_FROM_SCROLL_KEY.name
        )

        // Known preference names and their declared types. Used to reject imports that
        // try to store a value under a known name with the wrong type.
        private val INT_PREFERENCE_NAMES = setOf(
            BREAK_TIME_KEY.name, SCORE_KEY.name, STREAK_COUNT_KEY.name,
            REMINDER_HOUR_KEY.name, REMINDER_MINUTE_KEY.name,
            FIRST_STEPS_SCROLL_KEY.name, WORKOUT_SCROLL_KEY.name, ABACUS_SCROLL_KEY.name,
            YUPANA_SCROLL_KEY.name, ANOTHER_WAY_TO_COUNT_SCROLL_KEY.name,
            ROAD_TO_SOMEWHERE_SCROLL_KEY.name, WHERE_ARE_WE_FROM_SCROLL_KEY.name,
            UNIVERSE_EXPANSION_STEP_KEY.name, DRIVE_SYNC_RESET_VERSION_KEY.name
        )
        private val LONG_PREFERENCE_NAMES = setOf(
            BREAK_START_TIME_KEY.name, DRIVE_SYNC_LAST_SYNC_KEY.name
        )
        private val BOOLEAN_PREFERENCE_NAMES = setOf(
            REMINDER_ENABLED_KEY.name, ONBOARDING_COMPLETED_KEY.name,
            WHO_AM_I_LESSONS_MIGRATED_KEY.name, YUPANA_LESSONS_MIGRATED_KEY.name,
            DRIVE_SYNC_ENABLED_KEY.name, DRIVE_SYNC_RESET_PENDING_KEY.name
        )
        private val STRING_PREFERENCE_NAMES = setOf(
            LANGUAGE_KEY.name, LAST_ROUTE_KEY.name, LAST_COMPLETED_DATE_KEY.name,
            SKIN_COLOR_KEY.name, QUIPU_PRACTICE_STATE_KEY.name, CALENDAR_KEY.name,
            DRIVE_SYNC_ACCOUNT_KEY.name, DRIVE_SYNC_RESET_ACCOUNT_KEY.name
        )
        private val STRING_SET_PREFERENCE_NAMES = setOf(
            COMPLETED_DATES_KEY.name, STREAK_DAYS_KEY.name, WORKOUT_SECTIONS_KEY.name,
            FIRST_STEPS_SECTIONS_KEY.name, WHO_AM_I_SECTIONS_KEY.name, ABACUS_SECTIONS_KEY.name,
            YUPANA_SECTIONS_KEY.name, I_AM_NOT_LIKE_YOU_SECTIONS_KEY.name,
            ROAD_TO_SOMEWHERE_SECTIONS_KEY.name, RETURNING_SECTIONS_KEY.name,
            MEASURING_SECTIONS_KEY.name, RUNNING_AND_GROWING_SECTIONS_KEY.name,
            WHERE_ARE_WE_FROM_SECTIONS_KEY.name, ANOTHER_WAY_TO_COUNT_SECTIONS_KEY.name,
            AROUND_THE_WORLD_SECTIONS_KEY.name, AROUND_THE_WORLD_ADDITION_SECTIONS_KEY.name,
            LONG_DIVISION_SECTIONS_KEY.name, MULTIPLICATION_TABLE_SECTIONS_KEY.name, CLAIMED_LEVELS_KEY.name,
            SEEN_NEW_HUBS_KEY.name, AWARDED_SCREENS_KEY.name, ARRIVAL_AWARDED_SCREENS_KEY.name
        )

        // User-facing settings that must not be overwritten by an older remote backup.
        private val USER_SETTING_KEYS = setOf(
            LANGUAGE_KEY.name, BREAK_TIME_KEY.name, SKIN_COLOR_KEY.name,
            REMINDER_ENABLED_KEY.name, REMINDER_HOUR_KEY.name, REMINDER_MINUTE_KEY.name,
            CALENDAR_KEY.name
        )

        private fun declaredPreferenceType(name: String): String? = when (name) {
            in INT_PREFERENCE_NAMES -> "int"
            in LONG_PREFERENCE_NAMES -> "long"
            in BOOLEAN_PREFERENCE_NAMES -> "boolean"
            in STRING_PREFERENCE_NAMES -> "string"
            in STRING_SET_PREFERENCE_NAMES -> "stringSet"
            else -> null
        }

        private fun isValidIsoDate(value: String): Boolean =
            runCatching { java.time.LocalDate.parse(value) }.isSuccess

        private fun preferenceValueKey(value: Any): String = when (value) {
            is Set<*> -> value.map { it.toString() }.sorted().joinToString(",")
            else -> value.toString()
        }
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED_KEY] ?: false
    }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = true
        }
    }

    suspend fun initDefaultLanguage() {
        val current = context.dataStore.data.first()[LANGUAGE_KEY]
        if (current == null) {
            context.dataStore.edit { preferences ->
                preferences[LANGUAGE_KEY] = detectDefaultLanguage()
            }
        }
        val currentCalendar = context.dataStore.data.first()[CALENDAR_KEY]
        if (currentCalendar == null) {
            context.dataStore.edit { preferences ->
                preferences[CALENDAR_KEY] = detectDefaultCalendar()
            }
        }
    }

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "en-US"
    }

    val breakTime: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[BREAK_TIME_KEY] ?: 15
    }

    suspend fun setLanguage(language: String) {
        if (language !in setOf("en-US", "pt-BR", "es-ES")) return
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
        preferences[SKIN_COLOR_KEY] ?: "#A5672C"
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

    val yupanaScroll: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[YUPANA_SCROLL_KEY] ?: 0
    }

    suspend fun setYupanaScroll(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[YUPANA_SCROLL_KEY] = value
        }
    }

    val anotherWayToCountScroll: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[ANOTHER_WAY_TO_COUNT_SCROLL_KEY] ?: 0
    }

    suspend fun setAnotherWayToCountScroll(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[ANOTHER_WAY_TO_COUNT_SCROLL_KEY] = value
        }
    }

    val roadToSomewhereScroll: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[ROAD_TO_SOMEWHERE_SCROLL_KEY] ?: 0
    }

    suspend fun setRoadToSomewhereScroll(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[ROAD_TO_SOMEWHERE_SCROLL_KEY] = value
        }
    }

    val whereAreWeFromScroll: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[WHERE_ARE_WE_FROM_SCROLL_KEY] ?: 0
    }

    suspend fun setWhereAreWeFromScroll(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[WHERE_ARE_WE_FROM_SCROLL_KEY] = value
        }
    }

    val universeExpansionStep: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[UNIVERSE_EXPANSION_STEP_KEY] ?: 0
    }

    suspend fun setUniverseExpansionStep(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[UNIVERSE_EXPANSION_STEP_KEY] = value
        }
    }

    val quipuPracticeState: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[QUIPU_PRACTICE_STATE_KEY] ?: ""
    }

    suspend fun setQuipuPracticeState(value: String) {
        context.dataStore.edit { preferences ->
            preferences[QUIPU_PRACTICE_STATE_KEY] = value
        }
    }

    val completedWorkoutSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[WORKOUT_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markWorkoutSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[WORKOUT_SECTIONS_KEY] = (preferences[WORKOUT_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedFirstStepsSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[FIRST_STEPS_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markFirstStepsSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_STEPS_SECTIONS_KEY] = (preferences[FIRST_STEPS_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedWhoAmISections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[WHO_AM_I_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markWhoAmISectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[WHO_AM_I_SECTIONS_KEY] = (preferences[WHO_AM_I_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    suspend fun migrateMovedSelfAwarenessLessonsToWhoAmI() {
        val current = context.dataStore.data.first()
        if (current[WHO_AM_I_LESSONS_MIGRATED_KEY] == true) return
        val movedSections = setOf("i_dont_know", "learning_in_shells", "how_do_i_learn")
        val toCopy = movedSections intersect (current[FIRST_STEPS_SECTIONS_KEY] ?: emptySet())
        context.dataStore.edit { preferences ->
            if (toCopy.isNotEmpty()) {
                preferences[WHO_AM_I_SECTIONS_KEY] =
                    (preferences[WHO_AM_I_SECTIONS_KEY] ?: emptySet()) + toCopy
            }
            preferences[WHO_AM_I_LESSONS_MIGRATED_KEY] = true
        }
    }

    val completedAbacusSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[ABACUS_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markAbacusSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[ABACUS_SECTIONS_KEY] = (preferences[ABACUS_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedYupanaSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[YUPANA_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markYupanaSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[YUPANA_SECTIONS_KEY] = (preferences[YUPANA_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedIAmNotLikeYouSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[I_AM_NOT_LIKE_YOU_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markIAmNotLikeYouSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[I_AM_NOT_LIKE_YOU_SECTIONS_KEY] = (preferences[I_AM_NOT_LIKE_YOU_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedRoadToSomewhereSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[ROAD_TO_SOMEWHERE_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markRoadToSomewhereSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[ROAD_TO_SOMEWHERE_SECTIONS_KEY] = (preferences[ROAD_TO_SOMEWHERE_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedReturningSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[RETURNING_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markReturningSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[RETURNING_SECTIONS_KEY] = (preferences[RETURNING_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedMeasuringSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[MEASURING_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markMeasuringSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[MEASURING_SECTIONS_KEY] = (preferences[MEASURING_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedRunningAndGrowingSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[RUNNING_AND_GROWING_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markRunningAndGrowingSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[RUNNING_AND_GROWING_SECTIONS_KEY] = (preferences[RUNNING_AND_GROWING_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedWhereAreWeFromSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[WHERE_ARE_WE_FROM_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markWhereAreWeFromSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[WHERE_ARE_WE_FROM_SECTIONS_KEY] = (preferences[WHERE_ARE_WE_FROM_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedAnotherWayToCountSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[ANOTHER_WAY_TO_COUNT_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markAnotherWayToCountSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[ANOTHER_WAY_TO_COUNT_SECTIONS_KEY] = (preferences[ANOTHER_WAY_TO_COUNT_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedAroundTheWorldSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[AROUND_THE_WORLD_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markAroundTheWorldSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[AROUND_THE_WORLD_SECTIONS_KEY] = (preferences[AROUND_THE_WORLD_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedAroundTheWorldAdditionSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[AROUND_THE_WORLD_ADDITION_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markAroundTheWorldAdditionSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[AROUND_THE_WORLD_ADDITION_SECTIONS_KEY] =
                (preferences[AROUND_THE_WORLD_ADDITION_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedLongDivisionSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[LONG_DIVISION_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markLongDivisionSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[LONG_DIVISION_SECTIONS_KEY] =
                (preferences[LONG_DIVISION_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    val completedMultiplicationTableSections: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[MULTIPLICATION_TABLE_SECTIONS_KEY] ?: emptySet()
    }

    suspend fun markMultiplicationTableSectionCompleted(section: String) {
        context.dataStore.edit { preferences ->
            preferences[MULTIPLICATION_TABLE_SECTIONS_KEY] =
                (preferences[MULTIPLICATION_TABLE_SECTIONS_KEY] ?: emptySet()) + section
        }
    }

    suspend fun migrateMovedYupanaLessonsToAnotherWayToCount() {
        val current = context.dataStore.data.first()
        if (current[YUPANA_LESSONS_MIGRATED_KEY] == true) return
        val movedSections = setOf("hands_on_yupana", "large_numbers", "quipu_on_the_yupana")
        val toCopy = movedSections intersect (current[YUPANA_SECTIONS_KEY] ?: emptySet())
        context.dataStore.edit { preferences ->
            if (toCopy.isNotEmpty()) {
                preferences[ANOTHER_WAY_TO_COUNT_SECTIONS_KEY] =
                    (preferences[ANOTHER_WAY_TO_COUNT_SECTIONS_KEY] ?: emptySet()) + toCopy
            }
            preferences[YUPANA_LESSONS_MIGRATED_KEY] = true
        }
    }

    val awardedScreens: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[AWARDED_SCREENS_KEY] ?: emptySet()
    }

    suspend fun markScreenAwarded(screenId: String) {
        context.dataStore.edit { preferences ->
            preferences[AWARDED_SCREENS_KEY] = (preferences[AWARDED_SCREENS_KEY] ?: emptySet()) + screenId
        }
    }

    val arrivalAwardedScreens: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[ARRIVAL_AWARDED_SCREENS_KEY] ?: emptySet()
    }

    suspend fun markArrivalAwarded(screenId: String) {
        context.dataStore.edit { preferences ->
            preferences[ARRIVAL_AWARDED_SCREENS_KEY] = (preferences[ARRIVAL_AWARDED_SCREENS_KEY] ?: emptySet()) + screenId
        }
    }

    val calendar: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CALENDAR_KEY] ?: "gregorian"
    }

    suspend fun setCalendar(calendar: String) {
        if (com.historytracers.app.calendar.CalendarType.entries.none { it.id == calendar }) return
        context.dataStore.edit { preferences ->
            preferences[CALENDAR_KEY] = calendar
        }
    }

    suspend fun recordLessonCompletion() {
        context.dataStore.edit { prefs ->
            applyLessonCompletionStreak(prefs)
        }
    }

    private fun applyLessonCompletionStreak(prefs: MutablePreferences) {
        val today = java.time.LocalDate.now()
        val todayStr = today.toString()
        val currentDates = prefs[COMPLETED_DATES_KEY] ?: emptySet()
        if (todayStr in currentDates) return
        val selectedDays = prefs[STREAK_DAYS_KEY] ?: setOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY")
        val lastDateStr = prefs[LAST_COMPLETED_DATE_KEY]
        val currentStreak = prefs[STREAK_COUNT_KEY] ?: 0
        var missedSelectedDay = false
        val lastDate = lastDateStr?.let {
            runCatching { java.time.LocalDate.parse(it) }.getOrNull()
        }
        if (lastDate != null) {
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

    val claimedLevels: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[CLAIMED_LEVELS_KEY] ?: emptySet()
    }

    suspend fun markLevelClaimed(levelId: String) {
        context.dataStore.edit { preferences ->
            preferences[CLAIMED_LEVELS_KEY] = (preferences[CLAIMED_LEVELS_KEY] ?: emptySet()) + levelId
        }
    }

    suspend fun claimLevel(levelId: String, newScore: Int): Boolean {
        var claimed = false
        context.dataStore.edit { preferences ->
            val levelClaims = preferences[CLAIMED_LEVELS_KEY] ?: emptySet()
            if (levelId in levelClaims) return@edit
            preferences[CLAIMED_LEVELS_KEY] = levelClaims + levelId
            preferences[SCORE_KEY] = newScore
            claimed = true
        }
        return claimed
    }

    suspend fun claimReturningLevel(newScore: Int, requiredSections: List<String>): Boolean {
        var claimed = false
        context.dataStore.edit { preferences ->
            val completed = preferences[RETURNING_SECTIONS_KEY] ?: emptySet()
            if (!requiredSections.all { it in completed }) return@edit
            val levelClaims = preferences[CLAIMED_LEVELS_KEY] ?: emptySet()
            if ("returning" in levelClaims) return@edit
            preferences[CLAIMED_LEVELS_KEY] = levelClaims + "returning"
            preferences[SCORE_KEY] = newScore
            applyLessonCompletionStreak(preferences)
            claimed = true
        }
        return claimed
    }

    val seenNewHubs: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[SEEN_NEW_HUBS_KEY] ?: emptySet()
    }

    suspend fun markNewHubSeen(hubId: String) {
        context.dataStore.edit { preferences ->
            preferences[SEEN_NEW_HUBS_KEY] = (preferences[SEEN_NEW_HUBS_KEY] ?: emptySet()) + hubId
        }
    }

    val driveSyncEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DRIVE_SYNC_ENABLED_KEY] ?: false
    }

    suspend fun setDriveSyncEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DRIVE_SYNC_ENABLED_KEY] = enabled
        }
    }

    val driveSyncAccount: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DRIVE_SYNC_ACCOUNT_KEY] ?: ""
    }

    suspend fun setDriveSyncAccount(email: String) {
        context.dataStore.edit { preferences ->
            preferences[DRIVE_SYNC_ACCOUNT_KEY] = email
        }
    }

    val driveSyncLastSync: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[DRIVE_SYNC_LAST_SYNC_KEY] ?: 0L
    }

    suspend fun setDriveSyncLastSync(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[DRIVE_SYNC_LAST_SYNC_KEY] = timestamp
        }
    }

    /**
     * True after "Reset Classes" until the cleared state has been uploaded, so the next
     * sync overwrites the remote backup instead of merging old progress back in.
     */
    val driveSyncResetPending: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DRIVE_SYNC_RESET_PENDING_KEY] ?: false
    }

    suspend fun setDriveSyncResetPending(pending: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DRIVE_SYNC_RESET_PENDING_KEY] = pending
        }
    }

    /** Account the pending reset belongs to (blank when the reset happened before connecting). */
    val driveSyncResetAccount: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DRIVE_SYNC_RESET_ACCOUNT_KEY] ?: ""
    }

    suspend fun setDriveSyncResetAccount(email: String) {
        context.dataStore.edit { preferences ->
            preferences[DRIVE_SYNC_RESET_ACCOUNT_KEY] = email
        }
    }

    /**
     * Reset epoch, incremented on every reset. It is part of the backup so another
     * device can detect the reset and clear its stale local progress before merging.
     * It also prevents an upload from clearing a newer reset that happened during it.
     */
    val driveSyncResetVersion: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[DRIVE_SYNC_RESET_VERSION_KEY] ?: 0
    }

    suspend fun setDriveSyncResetVersion(version: Int) {
        context.dataStore.edit { preferences ->
            preferences[DRIVE_SYNC_RESET_VERSION_KEY] = version
        }
    }

    /**
     * Stable revision of the synced preference state. It changes whenever anything that
     * belongs to the Drive backup changes (score, streak, sections, settings, ...), so
     * automatic Sync can react to more than just score updates.
     */
    val syncRevision: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences.asMap().entries
            .asSequence()
            .filterNot { (key, _) -> key.name in DRIVE_SYNC_LOCAL_ONLY_KEYS }
            .sortedBy { (key, _) -> key.name }
            .joinToString("|") { (key, value) -> "${key.name}=${preferenceValueKey(value)}" }
            .hashCode()
    }

    /**
     * Serializes every synced preference (score, streak, completed sections, settings, ...)
     * into a JSON document suitable for storing in the user's own Google Drive.
     */
    suspend fun exportBackupJson(): String {
        val snapshot = context.dataStore.data.first()
        val values = JsonObject()
        snapshot.asMap().forEach { (key, value) ->
            if (key.name in DRIVE_SYNC_LOCAL_ONLY_KEYS) return@forEach
            values.add(key.name, encodePreferenceValue(value))
        }
        val backup = JsonObject().apply {
            addProperty("schema", BACKUP_SCHEMA_VERSION)
            addProperty("exportedAt", System.currentTimeMillis())
            add("values", values)
        }
        return BACKUP_GSON.toJson(backup)
    }

    /**
     * Merges a backup produced by [exportBackupJson] into the local preferences.
     * Progress is only ever increased: progress sets are unioned, the score keeps the
     * larger value, and the streak count follows the most recent completion date.
     * Invalid values (including dates and known names with the wrong type) are skipped.
     *
     * @return true when the document was a valid backup and was applied.
     */
    suspend fun mergeBackupJson(json: String): Boolean {
        val root = runCatching { JsonParser.parseString(json).asJsonObject }.getOrNull()
            ?: return false
        val schemaElement = root.get("schema") ?: return false
        if (!schemaElement.isJsonPrimitive) return false
        val schema = runCatching { schemaElement.asInt }.getOrNull() ?: return false
        if (schema > BACKUP_SCHEMA_VERSION) return false
        val valuesElement = root.get("values") ?: return false
        if (!valuesElement.isJsonObject) return false
        val values = valuesElement.asJsonObject

        val importedLastDate = values.get(LAST_COMPLETED_DATE_KEY.name)
            ?.takeIf { it.isJsonObject }
            ?.asJsonObject
            ?.takeIf { it.get("type")?.takeIf { type -> type.isJsonPrimitive }?.asString == "string" }
            ?.get("value")
            ?.takeIf { it.isJsonPrimitive }
            ?.let { runCatching { it.asString }.getOrNull() }
            ?.takeIf { isValidIsoDate(it) }
        val localSnapshot = context.dataStore.data.first()
        val localLastDate = localSnapshot[LAST_COMPLETED_DATE_KEY]?.takeIf { isValidIsoDate(it) }
        val localResetEpoch = localSnapshot[DRIVE_SYNC_RESET_VERSION_KEY] ?: 0
        val importedResetEpoch = values.get(DRIVE_SYNC_RESET_VERSION_KEY.name)
            ?.takeIf { it.isJsonObject }
            ?.asJsonObject
            ?.takeIf { it.get("type")?.takeIf { type -> type.isJsonPrimitive }?.asString == "int" }
            ?.get("value")
            ?.takeIf { it.isJsonPrimitive }
            ?.let { runCatching { it.asInt }.getOrNull() }
        val resetFromRemote = importedResetEpoch != null && importedResetEpoch > localResetEpoch
        val exportedAt = root.get("exportedAt")
            ?.takeIf { it.isJsonPrimitive }
            ?.let { runCatching { it.asLong }.getOrNull() }
            ?: 0L
        val lastSync = localSnapshot[DRIVE_SYNC_LAST_SYNC_KEY] ?: 0L
        val importSettings = lastSync == 0L || exportedAt > lastSync

        context.dataStore.edit { preferences ->
            if (resetFromRemote) {
                // Another device reset the classes: discard this device's stale progress
                // before applying the rest of the backup.
                clearProgress(preferences)
                preferences[DRIVE_SYNC_RESET_VERSION_KEY] = importedResetEpoch ?: localResetEpoch
            }
            values.entrySet().forEach { (name, element) ->
                if (name == DRIVE_SYNC_RESET_VERSION_KEY.name) return@forEach
                if (name in DRIVE_SYNC_LOCAL_ONLY_KEYS) return@forEach
                if (!element.isJsonObject) return@forEach
                // Ignore a single malformed entry instead of discarding the whole backup.
                runCatching {
                    applyBackupValue(
                        preferences = preferences,
                        name = name,
                        element = element.asJsonObject,
                        localLastDate = localLastDate,
                        importedLastDate = importedLastDate,
                        importSettings = importSettings
                    )
                }
            }
        }
        return true
    }

    private fun encodePreferenceValue(value: Any): JsonObject = JsonObject().apply {
        when (value) {
            is Int -> {
                addProperty("type", "int")
                addProperty("value", value)
            }
            is Long -> {
                addProperty("type", "long")
                addProperty("value", value)
            }
            is Float -> {
                addProperty("type", "float")
                addProperty("value", value)
            }
            is Double -> {
                addProperty("type", "double")
                addProperty("value", value)
            }
            is Boolean -> {
                addProperty("type", "boolean")
                addProperty("value", value)
            }
            is String -> {
                addProperty("type", "string")
                addProperty("value", value)
            }
            is Set<*> -> {
                addProperty("type", "stringSet")
                add("value", JsonArray().apply { value.forEach { add(it?.toString() ?: "") } })
            }
            else -> {
                addProperty("type", "string")
                addProperty("value", value.toString())
            }
        }
    }

    private fun applyBackupValue(
        preferences: MutablePreferences,
        name: String,
        element: JsonObject,
        localLastDate: String?,
        importedLastDate: String?,
        importSettings: Boolean
    ) {
        val type = element.get("type")?.takeIf { it.isJsonPrimitive }?.asString ?: return
        val expectedType = declaredPreferenceType(name)
        if (expectedType != null && expectedType != type) return
        val rawValue = element.get("value") ?: return

        when (type) {
            "int" -> {
                val imported = runCatching { rawValue.asInt }.getOrNull() ?: return
                val key = intPreferencesKey(name)
                if (!importSettings && name in USER_SETTING_KEYS && preferences[key] != null) return
                when (name) {
                    SCORE_KEY.name -> {
                        preferences[key] = maxOf(preferences[key] ?: 0, imported)
                    }
                    STREAK_COUNT_KEY.name -> {
                        val localCount = preferences[key] ?: 0
                        val storedDate = preferences[LAST_COMPLETED_DATE_KEY]
                            ?.takeIf { isValidIsoDate(it) }
                        val bestLocalDate = when {
                            localLastDate == null -> storedDate
                            storedDate == null -> localLastDate
                            storedDate > localLastDate -> storedDate
                            else -> localLastDate
                        }
                        preferences[key] = when {
                            importedLastDate == null -> localCount
                            bestLocalDate == null -> imported
                            importedLastDate > bestLocalDate -> imported
                            bestLocalDate > importedLastDate -> localCount
                            else -> maxOf(localCount, imported)
                        }
                    }
                    else -> preferences[key] = imported
                }
            }
            "long" -> {
                val key = longPreferencesKey(name)
                preferences[key] = runCatching { rawValue.asLong }.getOrNull() ?: return
            }
            "float" -> {
                val key = floatPreferencesKey(name)
                preferences[key] = runCatching { rawValue.asFloat }.getOrNull() ?: return
            }
            "double" -> {
                val key = doublePreferencesKey(name)
                preferences[key] = runCatching { rawValue.asDouble }.getOrNull() ?: return
            }
            "boolean" -> {
                val key = booleanPreferencesKey(name)
                if (!importSettings && name in USER_SETTING_KEYS && preferences[key] != null) return
                preferences[key] = runCatching { rawValue.asBoolean }.getOrNull() ?: return
            }
            "string" -> {
                val key = stringPreferencesKey(name)
                if (!importSettings && name in USER_SETTING_KEYS && preferences[key] != null) return
                val imported = runCatching { rawValue.asString }.getOrNull() ?: return
                if (name == LAST_COMPLETED_DATE_KEY.name) {
                    if (!isValidIsoDate(imported)) return
                    val current = preferences[key]?.takeIf { isValidIsoDate(it) }
                    preferences[key] = when {
                        current == null -> imported
                        imported > current -> imported
                        else -> current
                    }
                } else {
                    preferences[key] = imported
                }
            }
            "stringSet" -> {
                val key = stringSetPreferencesKey(name)
                val imported = runCatching {
                    rawValue.asJsonArray.map { it.asString }.toSet()
                }.getOrNull() ?: return
                preferences[key] = if (name == STREAK_DAYS_KEY.name) {
                    imported
                } else {
                    (preferences[key] ?: emptySet()) + imported
                }
            }
        }
    }

    private fun clearProgress(preferences: MutablePreferences) {
        preferences[FIRST_STEPS_SECTIONS_KEY] = emptySet()
        preferences[WHO_AM_I_SECTIONS_KEY] = emptySet()
        preferences[I_AM_NOT_LIKE_YOU_SECTIONS_KEY] = emptySet()
        preferences[WORKOUT_SECTIONS_KEY] = emptySet()
        preferences[ABACUS_SECTIONS_KEY] = emptySet()
        preferences[YUPANA_SECTIONS_KEY] = emptySet()
        preferences[ROAD_TO_SOMEWHERE_SECTIONS_KEY] = emptySet()
        preferences[RETURNING_SECTIONS_KEY] = emptySet()
        preferences[MEASURING_SECTIONS_KEY] = emptySet()
        preferences[RUNNING_AND_GROWING_SECTIONS_KEY] = emptySet()
        preferences[WHERE_ARE_WE_FROM_SECTIONS_KEY] = emptySet()
        preferences[ANOTHER_WAY_TO_COUNT_SECTIONS_KEY] = emptySet()
        preferences[AROUND_THE_WORLD_SECTIONS_KEY] = emptySet()
        preferences[AROUND_THE_WORLD_ADDITION_SECTIONS_KEY] = emptySet()
        preferences[LONG_DIVISION_SECTIONS_KEY] = emptySet()
        preferences[MULTIPLICATION_TABLE_SECTIONS_KEY] = emptySet()
        preferences[CLAIMED_LEVELS_KEY] = emptySet()
        preferences[AWARDED_SCREENS_KEY] = emptySet()
        preferences[ARRIVAL_AWARDED_SCREENS_KEY] = emptySet()
        preferences[UNIVERSE_EXPANSION_STEP_KEY] = 0
        preferences[QUIPU_PRACTICE_STATE_KEY] = ""
    }

    suspend fun resetAllClasses() {
        context.dataStore.edit { preferences ->
            clearProgress(preferences)
            // The next sync must overwrite the Drive backup instead of merging the
            // progress that was just cleared back into the local preferences. Remember
            // which account the reset belongs to so it cannot overwrite another account.
            // The epoch travels with the backup so other devices also clear their stale
            // progress instead of merging it back.
            preferences[DRIVE_SYNC_RESET_PENDING_KEY] = true
            preferences[DRIVE_SYNC_RESET_ACCOUNT_KEY] = preferences[DRIVE_SYNC_ACCOUNT_KEY] ?: ""
            preferences[DRIVE_SYNC_RESET_VERSION_KEY] =
                (preferences[DRIVE_SYNC_RESET_VERSION_KEY] ?: 0) + 1
        }
    }
}
