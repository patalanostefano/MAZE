// ui/screens/gameplay/GameplayScreen.kt
package com.example.maze.ui.screens.gameplay

import com.example.maze.utils.ImageUtils
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maze.ui.screens.gameplay.components.Labyrinth2DRenderer
import com.example.maze.ui.screens.gameplay.components.PlayerBall
import com.example.maze.utils.LockScreenOrientation

@Composable
fun GameplayScreen(
    labyrinthId: String,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    val context = LocalContext.current
    val viewModel: GameplayViewModel = viewModel(
        factory = GameplayViewModelFactory(labyrinthId, context)
    )

    val gameState by viewModel.gameState.collectAsState()
    val labyrinth by viewModel.labyrinth.collectAsState()
    val playerPosition by viewModel.playerPosition.collectAsState()
    val isHintVisible by viewModel.isHintVisible.collectAsState()
    val currentQuadrant by viewModel.currentQuadrant.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }

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
                        // Background color
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                        )

                        // Use the 2D Renderer now
                        Labyrinth2DRenderer(
                            fullImageUrl = maze.fullImageUrl,
                            playerPosition = playerPosition ?: maze.startPosition,
                            modifier = Modifier.fillMaxSize()
                        )

                        // PlayerBall overlays the Labyrinth2DRenderer
                        PlayerBall(
                            position = playerPosition ?: maze.startPosition,
                            fullImageSize = Pair(maze.width, maze.height),
                            currentQuadrant = currentQuadrant, // Now using the collected state
                            color = Color.Blue,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.Center)
                        )

                        // Top-right buttons
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.showHint() },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(alpha = 0.7f), CircleShape)
                            ) {
                                Text(
                                    text = "H",
                                    color = Color.Black,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            IconButton(
                                onClick = { showExitDialog = true },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(alpha = 0.7f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Exit",
                                    tint = Color.Black
                                )
                            }
                        }

                        // Hint overlay
                        AnimatedVisibility(
                            visible = isHintVisible,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.8f))
                                    .padding(32.dp)
                            ) {
                                maze.fullImageUrl.takeIf { it.isNotEmpty() }?.let { imageUrl ->
                                    val bitmap = remember(imageUrl) {
                                        ImageUtils.decodeBase64ToBitmap(imageUrl)
                                    }

                                    bitmap?.let {
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = "Maze hint",
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }
                            }
                        }

                        // Exit confirmation dialog
                        if (showExitDialog) {
                            AlertDialog(
                                onDismissRequest = { showExitDialog = false },
                                title = { Text("Exit Game") },
                                text = { Text("Are you sure you want to exit the game?") },
                                confirmButton = {
                                    TextButton(onClick = onExit) {
                                        Text("Yes")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showExitDialog = false }) {
                                        Text("No")
                                    }
                                }
                            )
                        }
                    }
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
    }
}
