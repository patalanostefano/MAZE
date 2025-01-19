package com.example.maze.utils

import com.example.maze.data.model.Position

// data/physics/BallPhysics.kt
class BallPhysics(
    private val structure: List<List<Int>>,
    private val cellSize: Float
) {
    private val friction = 0.98f
    private val maxVelocity = 15f

    fun updateBallPosition(
        currentPosition: Position,
        currentVelocity: Position,
        accelerationX: Float,
        accelerationY: Float
    ): Pair<Position, Position> {
        var newVelocityX = (currentVelocity.x + accelerationX) * friction
        var newVelocityY = (currentVelocity.y + accelerationY) * friction

        // Limit maximum velocity
        newVelocityX = newVelocityX.coerceIn(-maxVelocity, maxVelocity)
        newVelocityY = newVelocityY.coerceIn(-maxVelocity, maxVelocity)

        // Calculate new position
        val newX = currentPosition.x + newVelocityX
        val newY = currentPosition.y + newVelocityY

        // Check for collisions
        if (isWall(newX / cellSize, currentPosition.y / cellSize)) {
            newVelocityX *= -0.5f
        }
        if (isWall(currentPosition.x / cellSize, newY / cellSize)) {
            newVelocityY *= -0.5f
        }

        return Position(newX, newY) to Position(newVelocityX, newVelocityY)
    }

    private fun isWall(x: Float, y: Float): Boolean {
        val gridX = x.toInt()
        val gridY = y.toInt()
        return structure.getOrNull(gridY)?.getOrNull(gridX) == 0
    }
}
