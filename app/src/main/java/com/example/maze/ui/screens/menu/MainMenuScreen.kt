package com.example.maze.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maze.data.model.UserContext

@OptIn(ExperimentalMaterial3Api::class) //For TopAppBar
@Composable
fun MainMenuScreen(
    onNavigateToAvatar: () -> Unit,
    onNavigateToPlay: () -> Unit,
    onNavigateToMultiplayer: () -> Unit,
    onLogout: () -> Unit,
    menuViewModel: MenuViewModel = viewModel(factory = MenuViewModelFactory(LocalContext.current)) //Not necessary
) {
    val isAvatarCreated = UserContext.avatar != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = UserContext.username ?: "Guest")
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    Color(
                                        UserContext.avatar ?: Color.Transparent.hashCode()
                                    )
                                )
                                .padding(start = 8.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                Text("Update Avatar")
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

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(0.7f)

            ) {
                Text("Logout")
            }
        }
    }
}