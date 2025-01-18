package com.example.maze.ui.screens.menu

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maze.data.model.UserContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    private val _username = MutableStateFlow(UserContext.username ?: "Guest")
    val username: StateFlow<String> get() = _username

    private val _avatarColor = MutableStateFlow(UserContext.avatar ?: Color.Transparent.hashCode())
    val avatarColor: StateFlow<Int> get() = _avatarColor

    fun refreshUserState() {
        viewModelScope.launch {
            _username.value = UserContext.username ?: "Guest"
            _avatarColor.value = UserContext.avatar ?: Color.Transparent.hashCode()
        }
    }
}