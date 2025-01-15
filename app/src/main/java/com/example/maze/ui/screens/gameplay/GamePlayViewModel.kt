package com.example.maze.ui.screens.gameplay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maze.data.model.Labyrinth
import com.example.maze.data.model.Position
import com.example.maze.data.repository.LabyrinthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameplayViewModel(
    private val labyrinthId: String,
    private val repository: LabyrinthRepository
) : ViewModel() {
    private val _labyrinth = MutableStateFlow<Labyrinth?>(null)
    val labyrinth: StateFlow<Labyrinth?> = _labyrinth.asStateFlow()

    private val _playerPosition = MutableStateFlow<Position?>(null)
    val playerPosition: StateFlow<Position?> = _playerPosition.asStateFlow()

    private val _gameState = MutableStateFlow<GameState>(GameState.Loading)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    init {
        loadLabyrinth()
    }

    private fun loadLabyrinth() {
        viewModelScope.launch {
            try {
                val loadedLabyrinth = repository.getLabyrinth(labyrinthId)
                _labyrinth.value = loadedLabyrinth
                _playerPosition.value = loadedLabyrinth?.startPosition
                _gameState.value = GameState.Playing
            } catch (e: Exception) {
                _gameState.value = GameState.Error(e.message ?: "Error loading labyrinth")
            }
        }
    }

    fun updatePlayerPosition(newPosition: Position) {
        val currentLabyrinth = _labyrinth.value ?: return

        if (currentLabyrinth.isValidPosition(newPosition)) {
            _playerPosition.value = newPosition

            // Check win condition
            if (newPosition == currentLabyrinth.endPosition) {
                _gameState.value = GameState.Won
            }
        }
    }
}

sealed class GameState {
    object Loading : GameState()
    object Playing : GameState()
    object Won : GameState()
    data class Error(val message: String) : GameState()
}
