package com.example.maze.ui.screens.gameplay.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.maze.data.model.Position
import kotlin.math.max
import kotlin.math.min

@Composable
fun Labyrinth3DRenderer(
    labyrinth: List<List<Int>>,
    playerPosition: Position,
    modifier: Modifier = Modifier
) {
    val vanishingPoint = remember {
        Offset(0f, -500f) // Adjust for desired perspective
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val cellSize = size.width / labyrinth[0].size
        val viewDistance = 5 // Number of cells visible around player

        // Calculate visible area
        val startY = maxOf(0, playerPosition.y - viewDistance)
        val endY = minOf(labyrinth.size - 1, playerPosition.y + viewDistance)
        val startX = maxOf(0, playerPosition.x - viewDistance)
        val endX = minOf(labyrinth[0].size - 1, playerPosition.x + viewDistance)

        // Draw visible walls
        for (y in startY..endY) {
            for (x in startX..endX) {
                if (labyrinth[y][x] == 1) {
                    // Calculate wall corners
                    val topLeft = transformPoint(
                        Offset(x * cellSize, y * cellSize),
                        vanishingPoint,
                        size.height
                    )
                    val topRight = transformPoint(
                        Offset((x + 1) * cellSize, y * cellSize),
                        vanishingPoint,
                        size.height
                    )
                    val bottomLeft = transformPoint(
                        Offset(x * cellSize, (y + 1) * cellSize),
                        vanishingPoint,
                        size.height
                    )
                    val bottomRight = transformPoint(
                        Offset((x + 1) * cellSize, (y + 1) * cellSize),
                        vanishingPoint,
                        size.height
                    )

                    // Draw wall
                    drawLine(Color.Black, topLeft, topRight, 5f)
                    drawLine(Color.Black, topRight, bottomRight, 5f)
                    drawLine(Color.Black, bottomRight, bottomLeft, 5f)
                    drawLine(Color.Black, bottomLeft, topLeft, 5f)
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
