// ui/screens/gameplay/components/Labyrinth3DRenderer.kt
package com.example.maze.ui.screens.gameplay.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.maze.data.model.Position

@Composable
fun Labyrinth3DRenderer(
    labyrinth: List<List<Int>>,
    playerPosition: Position,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val horizonY = canvasHeight * 0.4f // Horizon line at 40% from the top
        val perspectivePoint = Offset(canvasWidth / 2f, horizonY)
        val viewDistance = 10 // Number of cells ahead to render

        // Iterate through cells in front of the player within the view distance
        for (distance in 1..viewDistance) {
            // Calculate the row in front based on distance
            val currentRow = playerPosition.y - distance
            if (currentRow < 0 || currentRow >= labyrinth.size) continue

            val numberOfWalls = labyrinth[currentRow].size
            for (x in 0 until numberOfWalls) {
                if (labyrinth[currentRow][x] == 1) {
                    // Calculate the relative position of the wall to the player
                    val relativeX = x - playerPosition.x

                    // Simple perspective scaling based on distance
                    val scale = 1 / distance.toFloat()

                    // Define wall width and height based on perspective
                    val wallWidth = 100f * scale
                    val wallHeight = 200f * scale

                    // Calculate the screen positions
                    val screenX = perspectivePoint.x + relativeX * 150f * scale
                    val screenY = horizonY + wallHeight / 2

                    // Define the wall's four corners to form a rectangle
                    val wallPath = Path().apply {
                        moveTo(screenX - wallWidth / 2, screenY - wallHeight / 2)
                        lineTo(screenX + wallWidth / 2, screenY - wallHeight / 2)
                        lineTo(screenX + wallWidth / 2, screenY + wallHeight / 2)
                        lineTo(screenX - wallWidth / 2, screenY + wallHeight / 2)
                        close()
                    }

                    // Draw the wall with shading based on distance
                    drawPath(
                        path = wallPath,
                        color = Color.Black.copy(alpha = 1f - (distance.toFloat() / viewDistance))
                    )
                }
            }
        }

        // Optionally, draw the floor grid for better depth perception
        drawFloorGrid(
            perspectivePoint = perspectivePoint,
            bottomY = canvasHeight,
            gridLines = viewDistance,
            cellSize = 150f
        )
    }
}

private fun DrawScope.drawFloorGrid(
    perspectivePoint: Offset,
    bottomY: Float,
    gridLines: Int,
    cellSize: Float
) {
    val lineColor = Color.LightGray.copy(alpha = 0.3f)
    for (i in 1..gridLines) {
        val distance = i.toFloat()
        val scale = 1 / distance

        // Horizontal grid lines
        val y = bottomY - (bottomY - perspectivePoint.y) * scale
        drawLine(
            color = lineColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )

        // Vertical grid lines
        val xStart = perspectivePoint.x - (size.width / 2) * scale
        val xEnd = perspectivePoint.x + (size.width / 2) * scale
        drawLine(
            color = lineColor,
            start = Offset(xStart, y),
            end = Offset(xEnd, y),
            strokeWidth = 1f
        )
    }
}
