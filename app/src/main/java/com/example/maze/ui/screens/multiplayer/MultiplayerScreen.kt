package com.example.maze.ui.screens.multiplayer

import android.util.Log
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maze.data.model.User
import com.example.maze.data.model.GameInvite

@Composable
fun MultiplayerScreen(
    userId: String,
    onNavigateToGame: (GameInvite) -> Unit,
    viewModel: MultiplayerViewModel = viewModel(factory = MultiplayerViewModelFactory(LocalContext.current))
) {
    val availablePlayers by viewModel.availablePlayers.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(userId) {
        try {
            viewModel.initialize(userId)
        } catch (e: Exception) {
            Log.e("MultiplayerScreen", "Error initializing: ${e.message}", e)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (connectionState) {
            is MultiplayerViewModel.ConnectionState.Initializing -> {
                LoadingState(message = "Initializing...")
            }
            is MultiplayerViewModel.ConnectionState.Scanning -> {
                LoadingState(message = "Scanning for nearby players...")
            }
            is MultiplayerViewModel.ConnectionState.Connected -> {
                ConnectedState(
                    currentUser = currentUser,
                    availablePlayers = availablePlayers,
                    onInvite = { player ->
                        try {
                            viewModel.sendInvite(player)
                            // When invite is accepted, navigate to game
                            currentUser?.let { current ->
                                onNavigateToGame(GameInvite(
                                    fromUser = current,
                                    toUser = player
                                ))
                            }
                        } catch (e: Exception) {
                            Log.e("MultiplayerScreen", "Error sending invite: ${e.message}", e)
                        }
                    }
                )
            }
            is MultiplayerViewModel.ConnectionState.Error -> {
                ErrorState(
                    message = (connectionState as MultiplayerViewModel.ConnectionState.Error).message,
                    onRetry = { viewModel.startScanning() }
                )
            }
            is MultiplayerViewModel.ConnectionState.PermissionRequired -> {
                PermissionState(
                    permissions = (connectionState as MultiplayerViewModel.ConnectionState.PermissionRequired).permissions
                )
            }
        }
    }
}


@Composable
private fun LoadingState(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message)
    }
}

@Composable
private fun ConnectedState(
    currentUser: User?,
    availablePlayers: List<User>,
    onInvite: (User) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        currentUser?.let { user ->
            Text(
                text = "Connected as: ${user.username}",
                modifier = Modifier.padding(bottom = 16.dp)
            )
            PlayerList(
                players = availablePlayers.filter { it.id != user.id },
                onInvite = onInvite
            )
        } ?: Text("Error: User not initialized")
    }
}

@Composable
private fun PlayerList(
    players: List<User>,
    onInvite: (User) -> Unit
) {
    if (players.isEmpty()) {
        Text(
            text = "No players found nearby",
            modifier = Modifier.padding(16.dp)
        )
    } else {
        LazyColumn {
            items(players) { player ->
                PlayerCard(
                    player = player,
                    onInvite = { onInvite(player) }
                )
            }
        }
    }
}

@Composable
private fun PlayerCard(
    player: User,
    onInvite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = player.username)
            Button(onClick = onInvite) {
                Text("Invite")
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Error: $message")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun PermissionState(permissions: List<String>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Required Permissions:")
        permissions.forEach { permission ->
            Text("• $permission")
        }
    }
}
