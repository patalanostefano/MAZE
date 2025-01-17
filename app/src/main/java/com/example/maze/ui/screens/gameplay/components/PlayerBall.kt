// ui/screens/gameplay/components/PlayerBall.kt
package com.example.maze.ui.screens.gameplay.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.maze.data.model.Position

@Composable
fun PlayerBall(
    position: Position,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val horizonY = size.height * 0.4f
        val ballRadius = 30f

        // Position ball near bottom center of screen
        val ballCenter = Offset(
            x = size.width / 2f,
            y = size.height - ballRadius * 2f
        )

        // Draw shadow
        drawCircle(
            color = Color.Black.copy(alpha = 0.3f),
            radius = ballRadius * 1.1f,
            center = ballCenter.copy(x = ballCenter.x + 5f, y = ballCenter.y + 5f)
        )

        // Draw main ball
        drawCircle(
            color = color,
            radius = ballRadius,
            center = ballCenter
        )

        // Draw highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = ballRadius * 0.4f,
            center = ballCenter.copy(
                x = ballCenter.x - ballRadius * 0.3f,
                y = ballCenter.y - ballRadius * 0.3f
            )
        )
    }
}
