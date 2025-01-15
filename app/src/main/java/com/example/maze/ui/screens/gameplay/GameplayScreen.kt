// ui/screens/gameplay/GameplayScreen.kt
package com.example.maze.ui.screens.gameplay

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maze.ui.screens.gameplay.components.Labyrinth3DRenderer
import com.example.maze.ui.screens.gameplay.components.PlayerBall

@Composable
fun GameplayScreen(
    labyrinthId: String,
    onGameComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: GameplayViewModel = viewModel(
        factory = GameplayViewModelFactory(labyrinthId)
    )

    val gameState by viewModel.gameState.collectAsState()
    val labyrinth by viewModel.labyrinth.collectAsState()
    val playerPosition by viewModel.playerPosition.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (gameState) {
            is GameState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is GameState.Playing -> {
                labyrinth?.let { maze ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Labyrinth3DRenderer(
                            labyrinth = maze.getStructureArray(),
                            playerPosition = playerPosition ?: maze.startPosition,
                            modifier = Modifier.fillMaxSize()
                        )

                        PlayerBall(
                            position = playerPosition ?: maze.startPosition,
                            color = Color.Blue,  // TODO get this from user preferences
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            is GameState.Won -> {
                LaunchedEffect(Unit) {
                    onGameComplete()
                }
            }

            is GameState.Error -> {
                Text(
                    text = (gameState as GameState.Error).message,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red
                )
            }
        }
    }
}
