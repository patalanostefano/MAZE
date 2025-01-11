package com.example.maze.ui.screens.multiplayer
import androidx.compose.foundation.lazy.items
import com.example.maze.data.model.User
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maze.data.model.GameInvite


@Composable
fun MultiplayerScreen(
    userId: String,
    onNavigateToGame: (GameInvite) -> Unit,
    viewModel: MultiplayerViewModel = viewModel(factory = MultiplayerViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val availablePlayers by viewModel.availablePlayers.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(userId) {
        viewModel.initialize(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (val state = connectionState) {
            is MultiplayerViewModel.ConnectionState.Initializing -> {
                CircularProgressIndicator()
                Text("Initializing...")
            }

            is MultiplayerViewModel.ConnectionState.Scanning -> {
                CircularProgressIndicator()
                Text("Scanning for nearby players...")
            }

            is MultiplayerViewModel.ConnectionState.Connected -> {
                currentUser?.let { user ->
                    Text("Connected as: ${user.username}")
                    PlayerList(
                        players = availablePlayers.filter { it.id != user.id },
                        onInvite = { player -> viewModel.sendInvite(player) }
                    )
                }
            }

            is MultiplayerViewModel.ConnectionState.Error -> {  // Use the correct namespace
                Text("Error: ${state.message}")
                Button(onClick = { viewModel.startScanning() }) {
                    Text("Retry")
                }
            }

            is MultiplayerViewModel.ConnectionState.PermissionRequired -> {
                // Handle other states
            }
        }
    }

}

    @Composable
    private fun PlayerList(
        players: List<User>,
        onInvite: (User) -> Unit
    ) {
        LazyColumn {
            items(players) { player ->
                PlayerCard(
                    player = player,
                    onInvite = { onInvite(player) }
                )
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = player.username)
                Button(onClick = onInvite) {
                    Text("Invite")
                }
            }
        }
    }