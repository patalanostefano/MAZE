package com.example.maze.ui.screens.multiplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maze.data.model.Player
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

class MultiplayerViewModel(
    private val repository: MultiplayerRepository
) : ViewModel() {
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Scanning)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    val availablePlayers = repository.availablePlayers

    init {
        startScanning()
    }

    fun startScanning() {
        viewModelScope.launch {
            when (val result = repository.startDiscovery()) {
                is MultiplayerRepository.DiscoveryResult.Success -> {
                    _connectionState.value = ConnectionState.Connected
                }
                is MultiplayerRepository.DiscoveryResult.PermissionError -> {
                    _connectionState.value = ConnectionState.PermissionRequired(result.permissions)
                }
                is MultiplayerRepository.DiscoveryResult.Error -> {
                    _connectionState.value = ConnectionState.Error(result.message)
                }
            }
        }
    }

    fun sendInvite(player: Player) {
        viewModelScope.launch {
            // Implement invite logic using repository
        }
    }
}
