package com.example.maze.ui.screens.gameplay.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.example.maze.data.model.GameState
import com.example.maze.data.model.Labyrinth
import com.example.maze.ui.screens.gameplay.GameplayViewModel

// ui/screens/gameplay/components/LabyrinthRenderer.kt


@Composable
fun LabyrinthRenderer(
    labyrinth: Labyrinth,
    gameState: GameState.Playing,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val cellSize = GameplayViewModel.CELL_SIZE
        val structure = labyrinth.structure

        // Calculate visible cells based on viewport offset
        val startX = (gameState.viewportOffset.x / cellSize).toInt()
        val startY = (gameState.viewportOffset.y / cellSize).toInt()
        val visibleCells = GameplayViewModel.VISIBLE_CELLS

        // Draw visible portion of the maze
        for (y in startY until (startY + visibleCells)) {
            for (x in startX until (startX + visibleCells)) {
                if (y in structure.indices && x in structure[0].indices && structure[y][x] == 1) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(
                            (x - startX) * cellSize,
                            (y - startY) * cellSize
                        ),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }

        // Draw ball
        drawCircle(
            color = Color.Blue,
            radius = cellSize / 3,
            center = Offset(
                gameState.screenPosition.x,
                gameState.screenPosition.y
            )
        )

        // Draw entrance and exit
        val entranceColor = Color.Green
        val exitColor = Color.Red
    }
}
