// ui/screens/gameplay/GameplayViewModel.kt
package com.example.maze.ui.screens.gameplay

import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maze.data.model.Labyrinth
import com.example.maze.data.model.Position
import com.example.maze.data.repository.LabyrinthRepository
import com.example.maze.utils.ImageUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.maze.utils.combinedSensorFlow
import com.example.maze.utils.getWeightedMovement


class GameplayViewModel(
    private val labyrinthId: String,
    private val repository: LabyrinthRepository,
    private val sensorManager: SensorManager
) : ViewModel() {

    private val _labyrinth = MutableStateFlow<Labyrinth?>(null)
    val labyrinth: StateFlow<Labyrinth?> = _labyrinth.asStateFlow()

    private val _playerPosition = MutableStateFlow<Position?>(null)
    val playerPosition: StateFlow<Position?> = _playerPosition.asStateFlow()

    private val _gameState = MutableStateFlow<GameState>(GameState.Loading)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _currentQuadrant = MutableStateFlow(0) // 0: top-left, 1: top-right, 2: bottom-left, 3: bottom-right
    val currentQuadrant: StateFlow<Int> = _currentQuadrant.asStateFlow()

    private val _ballVelocity = MutableStateFlow(Velocity(0f, 0f))
    private val _isHintVisible = MutableStateFlow(false)
    val isHintVisible: StateFlow<Boolean> = _isHintVisible.asStateFlow()

    private var mazeBitmap: Bitmap? = null

    companion object {
        private const val FRAME_TIME = 0.016f // ~60 FPS
        private const val MAX_VELOCITY = 5f
        private const val ACCELERATION_SCALE = 0.3f
        private const val BALL_RADIUS = 20
        private const val BOUNCE_DAMPENING = 0.5f
    }

    init {
        loadLabyrinth()
        setupSensorCollection()
    }

    private fun setupSensorCollection() {
        viewModelScope.launch {
            sensorManager.combinedSensorFlow().collect { sensorData ->
                val (weightedX, weightedY) = sensorData.getWeightedMovement()
                updateBallPosition(weightedX, weightedY)
            }
        }
    }


    private fun loadLabyrinth() {
        viewModelScope.launch {
            try {
                val loadedLabyrinth = repository.getLabyrinth(labyrinthId)
                _labyrinth.value = loadedLabyrinth

                // Load and store the maze bitmap for collision detection
                loadedLabyrinth?.fullImageUrl?.let { imageUrl ->
                    mazeBitmap = ImageUtils.decodeBase64ToBitmap(imageUrl)
                }

                // Set initial position and quadrant
                _playerPosition.value = loadedLabyrinth?.startPosition
                updateCurrentQuadrant(loadedLabyrinth?.startPosition)
                _gameState.value = GameState.Playing
            } catch (e: Exception) {
                _gameState.value = GameState.Error(e.message ?: "Error loading labyrinth")
            }
        }
    }

    private fun updateCurrentQuadrant(position: Position?) {
        position?.let { pos ->
            mazeBitmap?.let { bitmap ->
                val quadrantX = if (pos.x < bitmap.width / 2) 0 else 1
                val quadrantY = if (pos.y < bitmap.height / 2) 0 else 1
                _currentQuadrant.value = quadrantY * 2 + quadrantX
            }
        }
    }

    private fun isWall(x: Int, y: Int): Boolean {
        return mazeBitmap?.let { bitmap ->
            if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val brightness = Color.red(pixel) * 0.299f +
                        Color.green(pixel) * 0.587f +
                        Color.blue(pixel) * 0.114f
                brightness < 128 // Consider dark pixels as walls
            } else true // Consider out of bounds as walls
        } ?: true
    }

    private fun canMove(newPosition: Position): Boolean {
        // Check multiple points around the ball for collision
        for (angle in 0 until 360 step 45) {
            val checkX = (newPosition.x + BALL_RADIUS * kotlin.math.cos(Math.toRadians(angle.toDouble()))).toInt()
            val checkY = (newPosition.y + BALL_RADIUS * kotlin.math.sin(Math.toRadians(angle.toDouble()))).toInt()

            if (isWall(checkX, checkY)) {
                return false
            }
        }
        return true
    }

    private fun updateBallPosition(accelerationX: Float, accelerationY: Float) {
        val currentPosition = _playerPosition.value ?: return
        val velocity = _ballVelocity.value

        // Calculate new velocity
        val newVelocity = Velocity(
            x = (velocity.x - accelerationX * ACCELERATION_SCALE * FRAME_TIME)
                .coerceIn(-MAX_VELOCITY, MAX_VELOCITY),
            y = (velocity.y + accelerationY * ACCELERATION_SCALE * FRAME_TIME)
                .coerceIn(-MAX_VELOCITY, MAX_VELOCITY)
        )

        // Calculate new position
        val newPosition = Position(
            x = currentPosition.x + (newVelocity.x * FRAME_TIME).toInt(),
            y = currentPosition.y + (newVelocity.y * FRAME_TIME).toInt()
        )

        // Check collision and update position
        if (canMove(newPosition)) {
            _playerPosition.value = newPosition
            _ballVelocity.value = newVelocity
            updateCurrentQuadrant(newPosition)

            if (_labyrinth.value?.isEndPosition(newPosition) == true) {
                _gameState.value = GameState.Won
            }
        } else {
            // Bounce effect
            _ballVelocity.value = Velocity(
                x = if (newPosition.x != currentPosition.x) -velocity.x * BOUNCE_DAMPENING else velocity.x,
                y = if (newPosition.y != currentPosition.y) -velocity.y * BOUNCE_DAMPENING else velocity.y
            )
        }
    }

    fun showHint() {
        viewModelScope.launch {
            _isHintVisible.value = true
            delay(5000)
            _isHintVisible.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(null as SensorEventListener?)
        mazeBitmap?.recycle()
        mazeBitmap = null
    }
}

data class Velocity(val x: Float, val y: Float)

sealed class GameState {
    data object Loading : GameState()
    data object Playing : GameState()
    data object Won : GameState()
    data class Error(val message: String) : GameState()
}
