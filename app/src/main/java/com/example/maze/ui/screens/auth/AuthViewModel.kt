package com.example.maze.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maze.data.model.UserAlreadyExistsException
import com.example.maze.data.model.UserContext
import com.example.maze.data.model.UserNotFoundException
import com.example.maze.data.network.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authService: AuthService) : ViewModel() {
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> get() = _loginSuccess

    private val _errorState = MutableStateFlow<String?>(null) //To propagate errors to AuthPage
    val errorState: StateFlow<String?> get() = _errorState

    fun login(username: String, rememberMe: Boolean) {
        viewModelScope.launch {
            try {
                val user = authService.login(username)
                if(rememberMe) {
                    UserContext.savePersistent(user.username, user.avatarColor)
                } else {
                    UserContext.saveSession(user.username, user.avatarColor)
                }
                _loginSuccess.value = true
            } catch (e: UserNotFoundException) {
                Log.e("MainActivity", e.message, e)
                _errorState.value = "User not found"
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to login: ${e.message}", e)
                _errorState.value = "Login failed"
            }
        }
    }

    fun register(username: String, color: Int) {
        viewModelScope.launch {
            try {
                authService.createUser(username, color)
                _errorState.value = "User registered" //Not an error but just to show a snackbar
            } catch (e : UserAlreadyExistsException) {
                Log.e("MainActivity", e.message,e)
                _errorState.value = "User already exists"
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to register: ${e.message}",e)
                _errorState.value = "Failed to register"
            }
        }
    }

    fun clearError() {
        _errorState.value = null
    }
}