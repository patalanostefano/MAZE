package com.example.maze.ui.screens.gameplay.components

import android.util.Log
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
import kotlin.math.floor

// ui/screens/gameplay/components/LabyrinthRenderer.kt
@Composable
fun LabyrinthRenderer(
    labyrinth: Labyrinth,
    gameState: GameState.Playing,
    modifier: Modifier = Modifier,
    color: Color
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
                if (y in structure.indices && x in structure[0].indices) {
                    val isEntrance = x == labyrinth.entrance[0] && y == labyrinth.entrance[1]
                    val isExit = x == labyrinth.exit[0] && y == labyrinth.exit[1]

                    when {
                        isEntrance -> {
                            // Draw entrance cell
                            drawRect(
                                color = Color.Green,
                                topLeft = Offset(
                                    (x - startX) * cellSize,
                                    (y - startY) * cellSize
                                ),
                                size = Size(cellSize, cellSize)
                            )
                        }
                        isExit -> {
                            // Draw exit cell
                            drawRect(
                                color = Color.Red,
                                topLeft = Offset(
                                    (x - startX) * cellSize,
                                    (y - startY) * cellSize
                                ),
                                size = Size(cellSize, cellSize)
                            )
                        }
                        structure[y][x] == 0 -> {
                            // Draw wall
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
            }
        }

        //HIGHLY VOLATILE. 40 depends on cell size (?)
        val xposit = floor(gameState.absolutePosition.x/40)
        val yposit = floor(gameState.absolutePosition.y/40)
        // Draw ball
        drawCircle(
            color = color,
            radius = cellSize / 3,
            center = Offset(
                //AGAIN VOLATILE. 15 is half the size of currently used maze matrix
                if(xposit < 15) gameState.screenPosition.x - 20 else gameState.screenPosition.x + 20,
                if(yposit < 15) gameState.screenPosition.y - 20 else gameState.screenPosition.y + 20
            )
        )
    }
}


