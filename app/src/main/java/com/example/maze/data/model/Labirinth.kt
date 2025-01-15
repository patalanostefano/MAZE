package com.example.maze.data.model

import kotlin.math.abs

data class Labyrinth(
    val id: String = "",
    val name: String = "",
    val dimensions: Dimensions = Dimensions(),
    val structure: Map<String, Map<String, Int>> = emptyMap(),  // Firestore format
    val startPosition: Position = Position(),
    val endPosition: Position = Position(),
    val fullImageUrl: String = "",  // Base64 encoded image
    val imageFormat: String = ""
) {
    fun getStructureArray(): List<List<Int>> {
        return structure.keys.sortedBy { it.toInt() }.map { rowKey ->
            val row = structure[rowKey] ?: emptyMap()
            row.keys.sortedBy { it.toInt() }.map { colKey ->
                row[colKey] ?: 0
            }
        }
    }

    // Check if a position is within bounds and not a wall
    fun isValidPosition(position: Position): Boolean {
        val grid = getStructureArray()
        return position.x in 0 until dimensions.width &&
                position.y in 0 until dimensions.height &&
                grid.getOrNull(position.y)?.getOrNull(position.x) == 0
    }

    // Check if moving from current position to new position is valid
    fun canMoveTo(from: Position, to: Position): Boolean {
        if (!isValidPosition(to)) return false

        // Only allow movement to adjacent cells (no diagonal movement)
        val dx = abs(to.x - from.x)
        val dy = abs(to.y - from.y)
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1)
    }

    // Get available moves from current position
    fun getValidMoves(currentPosition: Position): List<Position> {
        val possibleMoves = listOf(
            Position(currentPosition.x + 1, currentPosition.y),  // Right
            Position(currentPosition.x - 1, currentPosition.y),  // Left
            Position(currentPosition.x, currentPosition.y + 1),  // Down
            Position(currentPosition.x, currentPosition.y - 1)   // Up
        )

        return possibleMoves.filter { isValidPosition(it) }
    }

    // Check if position is the end position
    fun isEndPosition(position: Position): Boolean {
        return position == endPosition
    }
}

data class Dimensions(
    val width: Int = 0,
    val height: Int = 0
)

data class Position(
    val x: Int = 0,
    val y: Int = 0
) {
    operator fun plus(other: Position) = Position(x + other.x, y + other.y)
    operator fun minus(other: Position) = Position(x - other.x, y - other.y)
}
