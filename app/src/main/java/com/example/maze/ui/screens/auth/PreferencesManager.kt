package com.example.maze.ui.screens.auth

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.maze.ui.screens.menu.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserPreferencesKeys {
    val REMEMBER_ME = booleanPreferencesKey("remember_me")
    val USERNAME = stringPreferencesKey("username")
}

/**
 * Saves user preferences to system.
 */
class PreferencesManager(private val context: Context) {
    suspend fun saveUserSession(rememberMe: Boolean, username: String?) {
        context.dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.REMEMBER_ME] = rememberMe
            if (username != null) {
                prefs[UserPreferencesKeys.USERNAME] = username
            }
        }
    }

    fun getUserSession(): Flow<Pair<Boolean, String?>> {
        return context.dataStore.data.map { prefs ->
            val rememberMe = prefs[UserPreferencesKeys.REMEMBER_ME] ?: false
            val username = prefs[UserPreferencesKeys.USERNAME]
            rememberMe to username
        }
    }
}