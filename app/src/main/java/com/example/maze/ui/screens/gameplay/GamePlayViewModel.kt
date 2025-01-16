// ui/screens/gameplay/GameplayViewModel.kt
package com.example.maze.ui.screens.gameplay

import android.content.Context
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maze.data.model.Labyrinth
import com.example.maze.data.model.Position
import com.example.maze.data.repository.LabyrinthRepository
import com.example.maze.utils.accelerometerFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameplayViewModel(
    private val labyrinthId: String,
    private val repository: LabyrinthRepository,
    sensorManager: SensorManager // Changed from context to direct SensorManager
) : ViewModel() {
    private val sensorManager = sensorManager

    private val _labyrinth = MutableStateFlow<Labyrinth?>(null)
    val labyrinth: StateFlow<Labyrinth?> = _labyrinth.asStateFlow()

    private val _playerPosition = MutableStateFlow<Position?>(null)
    val playerPosition: StateFlow<Position?> = _playerPosition.asStateFlow()

    private val _gameState = MutableStateFlow<GameState>(GameState.Loading)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _ballVelocity = MutableStateFlow(Velocity(0f, 0f))
    private val _isHintVisible = MutableStateFlow(false)
    val isHintVisible: StateFlow<Boolean> = _isHintVisible.asStateFlow()

    init {
        loadLabyrinth()
        setupSensorCollection()
    }

    private fun setupSensorCollection() {
        viewModelScope.launch {
            sensorManager.accelerometerFlow().collect { (x, y) ->
                updateBallPosition(x, y)
            }
        }
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

    private fun updateBallPosition(accelerationX: Float, accelerationY: Float) {
        val currentPosition = _playerPosition.value ?: return
        val velocity = _ballVelocity.value

        // Update velocity with acceleration
        val newVelocity = Velocity(
            x = velocity.x + accelerationX * FRAME_TIME,
            y = velocity.y + accelerationY * FRAME_TIME
        )

        // Calculate new position
        val newPosition = Position(
            x = currentPosition.x + (newVelocity.x * FRAME_TIME).toInt(),
            y = currentPosition.y + (newVelocity.y * FRAME_TIME).toInt()
        )

        if (_labyrinth.value?.isValidPosition(newPosition) == true) {
            _playerPosition.value = newPosition
            _ballVelocity.value = newVelocity

            // Check win condition
            if (_labyrinth.value?.isEndPosition(newPosition) == true) {
                _gameState.value = GameState.Won
            }
        } else {
            // Collision with wall - stop movement
            _ballVelocity.value = Velocity(0f, 0f)
        }
    }

    fun showHint() {
        viewModelScope.launch {
            _isHintVisible.value = true
            delay(5000)
            _isHintVisible.value = false
        }
    }

    // In GameplayViewModel
    override fun onCleared() {
        super.onCleared()
        // Clean up sensor listeners by passing null as SensorEventListener
        sensorManager.unregisterListener(null as SensorEventListener?)
    }

    companion object {
        private const val FRAME_TIME = 0.016f // ~60 FPS
    }
}

data class Velocity(val x: Float, val y: Float)

sealed class GameState {
    data object Loading : GameState()
    data object Playing : GameState()
    data object Won : GameState()
    data class Error(val message: String) : GameState()
}
