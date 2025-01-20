package com.example.maze.ui.screens.gameplay

import android.hardware.SensorManager
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maze.data.model.GameState
import com.example.maze.data.model.Labyrinth
import com.example.maze.data.model.Position
import com.example.maze.data.model.UserContext
import com.example.maze.data.repository.LabyrinthRepository
import com.example.maze.utils.getWeightedMovement

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ui/screens/gameplay/GameplayViewModel.kt


import com.example.maze.utils.combinedSensorFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

class GameplayViewModel(
    labyrinthId: String,
    private val labyrinthRepository: LabyrinthRepository,
    sensorManager: SensorManager
) : ViewModel() {

    private val _gameState = MutableStateFlow<GameState>(GameState.Loading)
    val gameState = _gameState.asStateFlow()

    private val _labyrinth = MutableStateFlow<Labyrinth?>(null)
    val labyrinth = _labyrinth.asStateFlow()

    private val _avatarColor = MutableStateFlow(UserContext.avatar ?: Color.Black.hashCode())
    val avatarColor = _avatarColor.asStateFlow()

    // Add these to GameplayViewModel
    private val _showHint = MutableStateFlow(false)
    val showHint = _showHint.asStateFlow()

    private val _hintTimeRemaining = MutableStateFlow(10)
    val hintTimeRemaining = _hintTimeRemaining.asStateFlow()

    private val _hintUsed = MutableStateFlow(false)
    val hintUsed = _hintUsed.asStateFlow()

    // Add function to handle hint
    fun showHint() {
        if (!_hintUsed.value) {
            _hintUsed.value = true
            _showHint.value = true
            viewModelScope.launch {
                for (i in 10 downTo 0) {
                    _hintTimeRemaining.value = i
                    delay(1000)
                }
                _showHint.value = false
            }
        }
    }

    companion object {
        const val CELL_SIZE = 40f
        const val VISIBLE_CELLS = 30
    }

    init {
        viewModelScope.launch {
            fetchLabyrinth(labyrinthId)
        }
        initializeSensorCollection(sensorManager)
    }

    private fun initializeSensorCollection(sensorManager: SensorManager) {
        viewModelScope.launch {
            sensorManager.combinedSensorFlow().collect { sensorData ->
                val movement = sensorData.getWeightedMovement()
                updateBallPosition(movement.first, movement.second)
            }
        }
    }

    private suspend fun fetchLabyrinth(id: String) {
        try {
            val fetchedLabyrinth = labyrinthRepository.getLabyrinth(id)

            // Validate labyrinth structure
            if (fetchedLabyrinth == null ||
                fetchedLabyrinth.structure.isEmpty() ||
                fetchedLabyrinth.structure[0].isEmpty()) {
                _gameState.value = GameState.Error("Invalid labyrinth structure")
                return
            }

            _labyrinth.value = fetchedLabyrinth

            // Validate entrance coordinates
            if (fetchedLabyrinth.entrance.size < 2) {
                _gameState.value = GameState.Error("Invalid entrance coordinates")
                return
            }

            // Calculate initial positions
            val startPosition = Position(
                x = fetchedLabyrinth.entrance[0] * CELL_SIZE,
                y = fetchedLabyrinth.entrance[1] * CELL_SIZE
            )

            val screenCenter = Position(
                x = CELL_SIZE * VISIBLE_CELLS / 2,
                y = CELL_SIZE * VISIBLE_CELLS / 2
            )

            val initialViewportOffset = Position(
                x = startPosition.x - screenCenter.x,
                y = startPosition.y - screenCenter.y
            )

            _gameState.value = GameState.Playing(
                absolutePosition = startPosition,
                screenPosition = screenCenter,
                viewportOffset = initialViewportOffset
            )
        } catch (e: Exception) {
            _gameState.value = GameState.Error("Failed to load labyrinth: ${e.message}")
        }
    }

    private fun updateBallPosition(dx: Float, dy: Float) {
        val currentState = _gameState.value as? GameState.Playing ?: return
        val labyrinth = _labyrinth.value ?: return

        // Add safety checks for structure
        if (labyrinth.structure.isEmpty() || labyrinth.structure[0].isEmpty()) {
            _gameState.value = GameState.Error("Invalid labyrinth structure")
            return
        }

        // Calculate new absolute position
        val newAbsolutePosition = Position(
            x = currentState.absolutePosition.x + dx,
            y = currentState.absolutePosition.y + dy
        )

        // Check collision with walls
        val cellX = (newAbsolutePosition.x / CELL_SIZE).toInt()
        val cellY = (newAbsolutePosition.y / CELL_SIZE).toInt()

        // Ensure position is within bounds and not in a wall
        if (cellX in labyrinth.structure[0].indices &&
            cellY in labyrinth.structure.indices &&
            labyrinth.structure[cellY][cellX] != 0) {

            // Update viewport offset to keep ball centered
            val newViewportOffset = calculateViewportOffset(newAbsolutePosition)

            // Update game state
            _gameState.value = currentState.copy(
                absolutePosition = newAbsolutePosition,
                viewportOffset = newViewportOffset
            )

            // Check if reached exit
            if (cellX == labyrinth.exit[0] && cellY == labyrinth.exit[1]) {
                _gameState.value = GameState.Won
            }
        }
    }

    private fun calculateViewportOffset(absolutePosition: Position): Position {
        val centerX = absolutePosition.x - (VISIBLE_CELLS * CELL_SIZE / 2)
        val centerY = absolutePosition.y - (VISIBLE_CELLS * CELL_SIZE / 2)
        return Position(centerX, centerY)
    }
}
