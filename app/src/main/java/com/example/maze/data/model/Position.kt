// data/model/Position.kt
package com.example.maze.data.model

data class Position(
    val x: Float,
    val y: Float
) {
    operator fun plus(other: Position) = Position(x + other.x, y + other.y)
    operator fun minus(other: Position) = Position(x - other.x, y - other.y)
}

data class Velocity(
    val x: Float = 0f,
    val y: Float = 0f
)
