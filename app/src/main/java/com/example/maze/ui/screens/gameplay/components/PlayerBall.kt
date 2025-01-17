// ui/screens/gameplay/components/PlayerBall.kt
package com.example.maze.ui.screens.gameplay.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
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
    Canvas(modifier.fillMaxSize()) {
        val ballRadius = 30f

        // Position ball in the screen based on model coordinates
        val ballCenter = Offset(
            x = position.x.toFloat(),
            y = position.y.toFloat()
        )

        // Draw the shadow and ball using method from your original PlayerBall.kt
        drawCircle(
            color = Color.Black.copy(alpha = 0.3f),
            radius = ballRadius * 1.1f,
            center = ballCenter + Offset(5f, 5f)
        )

        drawCircle(
            color = color,
            radius = ballRadius,
            center = ballCenter
        )

        // Optional light effect
        drawCircle(
            color = Color.White.copy(alpha = 0.5f),
            radius = ballRadius * 0.4f,
            center = ballCenter + Offset(-ballRadius * 0.3f, -ballRadius * 0.3f)
        )
    }
}

