package com.example.maze.ui.screens.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainMenuScreen(
    onNavigateToAvatar: () -> Unit,
    onNavigateToPlay: () -> Unit,
    onNavigateToMultiplayer: () -> Unit,
    menuViewModel: MenuViewModel = viewModel(factory = MenuViewModelFactory(LocalContext.current))
) {
    val isAvatarCreated = menuViewModel.isAvatarCreated

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onNavigateToAvatar,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.7f)
        ) {
            Text("Avatar")
        }

        Button(
            onClick = onNavigateToPlay,
            enabled = isAvatarCreated,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.7f)
        ) {
            Text("Play")
        }

        Button(
            onClick = onNavigateToMultiplayer,
            enabled = isAvatarCreated,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.7f)
        ) {
            Text("Multiplayer")
        }
    }
}
