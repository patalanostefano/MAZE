// ui/screens/gameplay/GameplayScreen.kt
package com.example.maze.ui.screens.gameplay

import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maze.data.model.GameState
import com.example.maze.ui.screens.gameplay.components.*
import com.example.maze.utils.LockScreenOrientation


@Composable
fun GameplayScreen(
    labyrinthId: String,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    val viewModel: GameplayViewModel = viewModel(
        factory = GameplayViewModelFactory(labyrinthId, LocalContext.current)
    )

    val gameState by viewModel.gameState.collectAsState()
    val labyrinth by viewModel.labyrinth.collectAsState()
    val avatarColor by viewModel.avatarColor.collectAsState()

    var showExitDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White // Enforce background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (gameState) {
                is GameState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is GameState.Playing -> {
                    val playingState = gameState as GameState.Playing
                    labyrinth?.let { currentLabyrinth ->
                        LabyrinthRenderer(
                            labyrinth = currentLabyrinth,
                            gameState = playingState,
                            modifier = Modifier.fillMaxSize(),
                            color = Color(avatarColor)
                        )
                    }

                    // Exit button
                    IconButton(
                        onClick = { showExitDialog = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit"
                        )
                    }
                }

                is GameState.Won -> {
                    LaunchedEffect(Unit) {
                        onExit()
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

            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    title = { Text("Exit Game") },
                    text = { Text("Are you sure you want to exit the game?") },
                    confirmButton = {
                        TextButton(onClick = onExit) { Text("Yes") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExitDialog = false }) { Text("No") }
                    }
                )
            }
        }
    }
}
