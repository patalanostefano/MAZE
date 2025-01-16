package com.example.maze.ui.screens.gameplay.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.maze.data.model.Position
import kotlin.math.pow
import androidx.compose.ui.graphics.Path
import kotlin.math.pow
import androidx.compose.ui.graphics.drawscope.Fill


@Composable
fun Labyrinth3DRenderer(
    labyrinth: List<List<Int>>,
    playerPosition: Position,
    modifier: Modifier = Modifier
) {
    val vanishingPoint = remember {
        Offset(500f, -300f) // Adjusted for better perspective
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val cellSize = size.width / labyrinth[0].size
        val wallHeight = cellSize * 1.5f // Taller walls
        val viewDistance = 8 // Increased view distance

        // Calculate visible area
        val startY = maxOf(0, playerPosition.y - viewDistance)
        val endY = minOf(labyrinth.size - 1, playerPosition.y + viewDistance)
        val startX = maxOf(0, playerPosition.x - viewDistance)
        val endX = minOf(labyrinth[0].size - 1, playerPosition.x + viewDistance)

        // Draw walls from back to front
        for (y in startY..endY) {
            for (x in startX..endX) {
                if (labyrinth[y][x] == 1) {
                    val wallTop = transformPoint(
                        Offset(x * cellSize, y * cellSize),
                        vanishingPoint,
                        size.height
                    )
                    val wallBottom = transformPoint(
                        Offset(x * cellSize, y * cellSize + wallHeight),
                        vanishingPoint,
                        size.height
                    )

                    // Draw wall with shading based on distance
                    val distance = kotlin.math.sqrt(
                        (x - playerPosition.x).toFloat().pow(2) +
                                (y - playerPosition.y).toFloat().pow(2)
                    )
                    val opacity = (1f - (distance / viewDistance)).coerceIn(0.2f, 1f)

                    drawRect(
                        color = Color.Black.copy(alpha = opacity),
                        topLeft = wallTop,
                        size = androidx.compose.ui.geometry.Size(
                            width = cellSize,
                            height = wallBottom.y - wallTop.y
                        )
                    )
                }
            }
        }
    }
}

private fun transformPoint(point: Offset, vanishingPoint: Offset, screenHeight: Float): Offset {
    val distance = point.y - vanishingPoint.y
    val scaleFactor = 1f - (distance / screenHeight)
    return Offset(
        vanishingPoint.x + (point.x - vanishingPoint.x) * scaleFactor,
        vanishingPoint.y + distance * scaleFactor
    )
}