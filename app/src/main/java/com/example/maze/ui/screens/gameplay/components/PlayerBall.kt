// ui/screens/gameplay/components/PlayerBall.kt
package com.example.maze.ui.screens.gameplay.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.maze.data.model.Position


// ui/screens/gameplay/components/PlayerBall.kt
@Composable
fun PlayerBall(
    position: Position,
    fullImageSize: Pair<Int, Int>,
    currentQuadrant: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val quadrantWidth = size.width
        val quadrantHeight = size.height

        // Adjust position based on quadrant
        val quadrantOffsetX = (currentQuadrant % 2) * (fullImageSize.first / 2)
        val quadrantOffsetY = (currentQuadrant / 2) * (fullImageSize.second / 2)

        // Calculate relative position within the quadrant
        val relativeX = position.x - quadrantOffsetX
        val relativeY = position.y - quadrantOffsetY

        // Convert to screen coordinates
        val screenX = (relativeX.toFloat() / (fullImageSize.first / 2)) * quadrantWidth
        val screenY = (relativeY.toFloat() / (fullImageSize.second / 2)) * quadrantHeight

        drawCircle(
            color = color,
            radius = 20f,
            center = Offset(screenX, screenY)
        )
    }
}
