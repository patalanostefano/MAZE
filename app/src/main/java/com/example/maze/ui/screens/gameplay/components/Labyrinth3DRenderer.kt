// ui/screens/gameplay/components/Labyrinth3DRenderer.kt
package com.example.maze.ui.screens.gameplay.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.maze.data.model.Position
import kotlin.math.sqrt

@Composable
fun Labyrinth3DRenderer(
    labyrinth: List<List<Int>>,
    playerPosition: Position,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val horizonY = canvasHeight * 0.4f // Horizon line
        val floorEndY = canvasHeight // Bottom of screen

        // Draw background
        drawRect(
            color = Color.White,
            topLeft = Offset.Zero,
            size = size
        )

        // Draw floor (perspective grid)
        drawFloor(
            horizonY = horizonY,
            floorEndY = floorEndY,
            canvasWidth = canvasWidth
        )

        // Draw walls in perspective
        drawWalls(
            labyrinth = labyrinth,
            playerPosition = playerPosition,
            horizonY = horizonY,
            floorEndY = floorEndY,
            canvasWidth = canvasWidth
        )
    }
}

private fun DrawScope.drawFloor(
    horizonY: Float,
    floorEndY: Float,
    canvasWidth: Float
) {
    val gridLines = 10
    val centerX = canvasWidth / 2f

    // Draw perspective lines
    for (i in 0..gridLines) {
        val fraction = i.toFloat() / gridLines
        val startX = fraction * canvasWidth

        // Draw lines converging to center
        drawLine(
            color = Color.LightGray.copy(alpha = 0.3f),
            start = Offset(startX, floorEndY),
            end = Offset(centerX, horizonY),
            strokeWidth = 1f
        )
    }

    // Draw horizontal lines for depth
    for (i in 1..gridLines) {
        val y = lerp(floorEndY, horizonY, i.toFloat() / gridLines)
        val xOffset = (i.toFloat() / gridLines) * centerX

        drawLine(
            color = Color.LightGray.copy(alpha = 0.3f),
            start = Offset(centerX - xOffset, y),
            end = Offset(centerX + xOffset, y),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawWalls(
    labyrinth: List<List<Int>>,
    playerPosition: Position,
    horizonY: Float,
    floorEndY: Float,
    canvasWidth: Float
) {
    val centerX = canvasWidth / 2f
    val viewDistance = 4
    val wallSegmentWidth = canvasWidth / 8f

    // Get visible area around player
    val visibleRows = (playerPosition.y - viewDistance)..(playerPosition.y + viewDistance)
    val visibleCols = (playerPosition.x - viewDistance)..(playerPosition.x + viewDistance)

    for (row in visibleRows) {
        if (row < 0 || row >= labyrinth.size) continue

        for (col in visibleCols) {
            if (col < 0 || col >= labyrinth[row].size) continue

            if (labyrinth[row][col] == 1) {
                // Calculate relative position to player
                val relativeX = col - playerPosition.x
                val relativeY = row - playerPosition.y
                val distance = sqrt(relativeX * relativeX + relativeY * relativeY.toFloat())

                // Calculate wall positions in perspective
                val perspectiveScale = 1f - (distance / viewDistance).coerceIn(0f, 1f)
                val wallHeight = (floorEndY - horizonY) * perspectiveScale * 0.5f
                val wallWidth = wallSegmentWidth * perspectiveScale

                val wallX = centerX + (relativeX * wallSegmentWidth * perspectiveScale)
                val wallY = horizonY + (relativeY * wallHeight)

                // Draw wall
                drawRect(
                    color = Color.Black.copy(alpha = perspectiveScale),
                    topLeft = Offset(wallX - wallWidth/2, wallY),
                    size = androidx.compose.ui.geometry.Size(wallWidth, wallHeight)
                )
            }
        }
    }
}

private fun lerp(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}
