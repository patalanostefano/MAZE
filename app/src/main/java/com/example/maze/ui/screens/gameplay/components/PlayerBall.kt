// ui/screens/gameplay/components/PlayerBall.kt
package com.example.maze.ui.screens.gameplay.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.maze.data.model.Position

@Composable
fun PlayerBall(
    position: Position,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawBall(position, color)
    }
}

private fun DrawScope.drawBall(position: Position, color: Color) {
    val cellSize = size.width / 30f  // Adjust based on your labyrinth size
    val radius = cellSize / 2

    // Add shadow for 3D effect
    drawCircle(
        color = Color.Black.copy(alpha = 0.3f),
        radius = radius * 1.1f,
        center = Offset(
            x = (position.x * cellSize) + cellSize / 2 + radius * 0.2f,
            y = (position.y * cellSize) + cellSize / 2 + radius * 0.2f
        )
    )

    // Draw the ball with gradient for 3D effect
    drawCircle(
        color = color,
        radius = radius,
        center = Offset(
            x = (position.x * cellSize) + cellSize / 2,
            y = (position.y * cellSize) + cellSize / 2
        )
    )

    // Add highlight for 3D effect
    drawCircle(
        color = Color.White.copy(alpha = 0.5f),
        radius = radius * 0.5f,
        center = Offset(
            x = (position.x * cellSize) + cellSize / 3,
            y = (position.y * cellSize) + cellSize / 3
        )
    )
}
