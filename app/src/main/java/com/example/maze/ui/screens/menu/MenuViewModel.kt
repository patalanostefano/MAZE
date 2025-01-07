package com.example.maze.ui.screens.menu

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.text.color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.maze.data.model.Avatar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Create the DataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "avatar_prefs")

class MenuViewModel(private val context: Context) : ViewModel() {

    // State variables
    var avatar by mutableStateOf<Avatar?>(null)
        private set

    var isAvatarCreated by mutableStateOf(false)
        private set

    // DataStore keys
    private val avatarColorKey = stringPreferencesKey("avatar_color")

    init {
        loadAvatar()
    }

    // Load avatar from DataStore
    private fun loadAvatar() {
        viewModelScope.launch {
            getAvatarFromDataStore().collect { colorString ->
                if (colorString.isNotEmpty()) {
                    val color = Color(android.graphics.Color.parseColor(colorString))
                    avatar = Avatar(color)
                    isAvatarCreated = true
                }
            }
        }
    }

    // Save avatar to DataStore
    fun saveAvatar(avatar: Avatar) {
        viewModelScope.launch {
            saveAvatarToDataStore(avatar)
            this@MenuViewModel.avatar = avatar
            isAvatarCreated = true
        }
    }

    // DataStore functions
    private suspend fun saveAvatarToDataStore(avatar: Avatar) {
        context.dataStore.edit { preferences ->
            preferences[avatarColorKey] = "#%06X".format(0xFFFFFF and avatar.color.toArgb())
        }
    }

    private fun getAvatarFromDataStore(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[avatarColorKey] ?: ""
        }
    }
}