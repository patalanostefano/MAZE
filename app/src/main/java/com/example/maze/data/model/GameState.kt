package com.example.maze.data.model

sealed class GameState {
    data object Loading : GameState()
    data class Playing(
        val absolutePosition: Position,  // Position in the complete labyrinth
        val screenPosition: Position,    // Position on the visible screen
        val viewportOffset: Position     // Offset of the viewport in the labyrinth
    ) : GameState()
    data object Won : GameState()
    data class Error(val message: String) : GameState()
}