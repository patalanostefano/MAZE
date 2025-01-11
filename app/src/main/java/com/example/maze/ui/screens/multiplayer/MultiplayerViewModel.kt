package com.example.maze.ui.screens.multiplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maze.data.model.User
import com.example.maze.data.repository.MultiplayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ConnectionState {
    object Scanning : ConnectionState()
    object Connected : ConnectionState()
    data class PermissionRequired(val permissions: List<String>) : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}

// ui/screens/multiplayer/MultiplayerViewModel.kt
class MultiplayerViewModel(
    private val repository: MultiplayerRepository
) : ViewModel() {
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Initializing)
    val connectionState: StateFlow<ConnectionState> = _connectionState
    val availablePlayers = repository.availablePlayers
    val currentUser = repository.currentUser

    sealed class ConnectionState {
        object Initializing : ConnectionState()
        object Scanning : ConnectionState()
        object Connected : ConnectionState()
        data class PermissionRequired(val permissions: List<String>) : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }

    fun initialize(userId: String) {
        viewModelScope.launch {
            try {
                repository.initializeUser(userId)
                startScanning()
            } catch (e: Exception) {
                _connectionState.value = ConnectionState.Error("Failed to initialize user")
            }
        }
    }

    fun startScanning() {
        viewModelScope.launch {
            _connectionState.value = ConnectionState.Scanning
            when (val result = repository.startDiscovery()) {
                is MultiplayerRepository.DiscoveryResult.Success -> {
                    _connectionState.value = ConnectionState.Connected
                }
                is MultiplayerRepository.DiscoveryResult.Error -> {
                    _connectionState.value = ConnectionState.Error(result.message)
                }
                is MultiplayerRepository.DiscoveryResult.PermissionError -> {
                    _connectionState.value = ConnectionState.PermissionRequired(result.permissions)
                }
            }
        }
    }

    fun sendInvite(toUser: User) {
        viewModelScope.launch {
            repository.sendInvite(toUser)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.cleanup()
    }
}

