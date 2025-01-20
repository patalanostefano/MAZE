package com.example.maze.data.model


// data/model/GameUpdate.kt
data class GameUpdate(
    val userId: String,
    val position: Position,
    val timestamp: Long = System.currentTimeMillis()
)

data class GameConnectionRequest(
    val gameId: String,
    val userId: String
)

data class PositionUpdate(
    val position: Position
)
