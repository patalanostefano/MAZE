package com.example.maze.ui.screens.multiplayer

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maze.data.model.Player
import com.example.maze.data.model.GameInvite
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun MultiplayerScreen(
    onNavigateToGame: (GameInvite) -> Unit,
    viewModel: MultiplayerViewModel = viewModel(factory = MultiplayerViewModelFactory(LocalContext.current))
) {
    val context = LocalContext.current
    val availablePlayers by viewModel.availablePlayers.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()

    // Permission launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        if (permissionsMap.all { it.value }) {
            viewModel.startScanning()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Available Players",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (val state = connectionState) {
            is ConnectionState.Scanning -> CircularProgressIndicator()
            is ConnectionState.PermissionRequired -> {
                Column {
                    Text("Permissions required")
                    Button(onClick = {
                        launcher.launch(state.permissions.toTypedArray())
                    }) {
                        Text("Grant Permissions")
                    }
                }
            }
            is ConnectionState.Error -> Text("Error: ${state.message}")
            is ConnectionState.Connected -> {
                PlayerList(
                    players = availablePlayers.map { device ->
                        Player(
                            id = device.deviceAddress,
                            username = device.deviceName,
                            avatarColor = 0
                        )
                    },
                    onInvite = { player -> viewModel.sendInvite(player) }
                )
            }
        }
    }
}


@Composable
private fun PlayerList(
    players: List<Player>,
    onInvite: (Player) -> Unit
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
    player: Player,
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
