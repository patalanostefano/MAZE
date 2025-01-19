package com.example.maze.data.model

import android.content.Context
import android.content.SharedPreferences

/**
 * Singleton for maintaining user state & saving state locally
 */
object UserContext {
    private const val PREFS_NAME = "user_context_prefs"
    private const val KEY_USERNAME = "key_username"
    private const val KEY_AVATAR = "key_avatar"
    private const val KEY_IS_LOGGED_IN = "key_is_logged_in"

    private lateinit var sharedPreferences: SharedPreferences

    var username: String? = null
    var avatar: Int? = null
    var isLoggedIn: Boolean = false

    // Initialize SharedPreferences
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadFromPreferences()
    }

    // Save session state to only memory
    fun saveSession(username: String, avatar: Int) {
        this.username = username
        this.avatar = avatar
    }

    // Save session state to storage
    fun savePersistent(username: String, avatar: Int) {
        this.username = username
        this.avatar = avatar
        this.isLoggedIn = true

        sharedPreferences.edit().apply {
            putString(KEY_USERNAME, username)
            putInt(KEY_AVATAR, avatar)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // Update the avatar
    fun updateAvatar(avatar: Int) {
        this.avatar = avatar

        sharedPreferences.edit().apply {
            putInt(KEY_AVATAR, avatar)
            apply()
        }
    }

    // Load session state from SharedPreferences
    private fun loadFromPreferences() {
        username = sharedPreferences.getString(KEY_USERNAME, null)
        avatar = sharedPreferences.getInt(KEY_AVATAR, -1).takeIf { it != -1 }
        isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Clear session data (logout)
    fun clearSession() {
        username = null
        avatar = null
        isLoggedIn = false

        sharedPreferences.edit().clear().apply()
    }
}
