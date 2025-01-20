package com.example.maze.ui.screens.gameplay

import android.content.pm.ActivityInfo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.draw.alpha

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maze.data.model.GameState
import com.example.maze.ui.screens.gameplay.components.*
import com.example.maze.utils.ImageUtils
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

    val showHint by viewModel.showHint.collectAsState()
    val hintTimeRemaining by viewModel.hintTimeRemaining.collectAsState()
    val hintUsed by viewModel.hintUsed.collectAsState()

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


                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Hint Button
                        FloatingActionButton(
                            onClick = {
                                if (!hintUsed) {
                                    viewModel.showHint()
                                }
                            },
                            modifier = Modifier.alpha(if (hintUsed) 0.5f else 1f),
                            elevation = FloatingActionButtonDefaults.elevation(8.dp),
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = "H",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        // Exit Button
                        FloatingActionButton(
                            onClick = { showExitDialog = true },
                            elevation = FloatingActionButtonDefaults.elevation(8.dp),
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit",
                                tint = Color.White
                            )
                        }
                    }

                    // Hint Overlay
                    if (showHint) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .fillMaxHeight(0.8f)
                                .align(Alignment.Center),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                labyrinth?.fullImageUrl?.let { imageUrl ->
                                    val bitmap = ImageUtils.decodeBase64ToBitmap(imageUrl)
                                    bitmap?.let {
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = "Labyrinth Hint",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }

                                Text(
                                    text = "Time remaining: $hintTimeRemaining",
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(16.dp),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }

                is GameState.Won -> {
                    VictoryScreen(onExit = onExit)
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

@Composable
fun VictoryScreen(onExit: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Congratulations!",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "You completed the labyrinth!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(
                    text = "Exit",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}
@Composable
fun VictoryScreen(
    elapsedTime: Long,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "You Won!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Time: ${elapsedTime}s",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onExit) {
            Text("Exit")
        }
    }
}
