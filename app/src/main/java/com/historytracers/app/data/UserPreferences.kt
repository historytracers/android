// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val BREAK_TIME_KEY = intPreferencesKey("break_time")
        private val LAST_ROUTE_KEY = stringPreferencesKey("last_route")
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
}
