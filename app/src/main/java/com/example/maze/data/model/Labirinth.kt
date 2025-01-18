package com.example.maze.data.model

data class Labyrinth(
    val id: String = "",
    val name: String = "",
    val startPosition: Position = Position(),
    val endPosition: Position = Position(),
    val fullImageUrl: String = "",  // Base64 encoded image
    val width: Int = 0,  // Image width
    val height: Int = 0,  // Image height
    val mazeGrid: List<List<Int>> = emptyList() // 2D grid for walls and paths
) {
    // Check if position is the end position
    fun isEndPosition(position: Position): Boolean {
        val endZoneRadius = 30  // Adjust this value based on your needs
        val dx = position.x - endPosition.x
        val dy = position.y - endPosition.y
        return (dx * dx + dy * dy) <= endZoneRadius * endZoneRadius
    }
}

data class Position(
    val x: Int = 0,
    val y: Int = 0
) {
    operator fun plus(other: Position) = Position(x + other.x, y + other.y)
    operator fun minus(other: Position) = Position(x - other.x, y - other.y)
}
