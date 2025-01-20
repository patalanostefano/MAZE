package com.example.maze.ui.screens.multiplayer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maze.data.model.GameInvite
import com.example.maze.data.model.User
import com.example.maze.data.repository.MultiplayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



// ui/screens/multiplayer/MultiplayerViewModel.kt
class MultiplayerViewModel(private val repository: MultiplayerRepository) : ViewModel() {
    private val _initializationComplete = MutableStateFlow(false)
    val initializationComplete: StateFlow<Boolean> = _initializationComplete

    init {
        viewModelScope.launch {
            repository.currentUser.collect { user ->
                _initializationComplete.value = user != null
            }
        }
    }
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Initializing)
    val connectionState: StateFlow<ConnectionState> = _connectionState
    val availablePlayers = repository.availablePlayers
    val currentUser = repository.currentUser
    val gameInvites = repository.gameInvites


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
                _connectionState.value = ConnectionState.Initializing
                Log.d("MultiplayerViewModel", "Starting initialization for user: $userId")
                repository.initializeUser(userId)
                Log.d("MultiplayerViewModel", "User initialized, current user: ${repository.currentUser.value}")

                if (repository.currentUser.value != null) {
                    Log.d("MultiplayerViewModel", "Starting scanning after successful initialization")
                    startScanning()
                } else {
                    Log.e("MultiplayerViewModel", "User initialization failed - user is null")
                    _connectionState.value = ConnectionState.Error("Failed to initialize user")
                }
            } catch (e: Exception) {
                Log.e("MultiplayerViewModel", "Initialization error: ${e.message}")
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
    fun acceptInvite(invite: GameInvite) {
        repository.acceptInvite(invite)
    }

    fun declineInvite(invite: GameInvite) {
        repository.declineInvite(invite)
    }


    fun cleanup() {
        repository.cleanup()
    }


    override fun onCleared() {
        super.onCleared()
        repository.cleanup()
    }
}

