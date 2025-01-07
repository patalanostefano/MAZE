package com.example.maze.ui.screens.avatar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maze.data.model.Avatar
import com.example.maze.ui.screens.menu.MenuViewModel
import com.example.maze.ui.screens.menu.MenuViewModelFactory

@Composable
fun AvatarCreationScreen(
    onAvatarCreated: () -> Unit,
    menuViewModel: MenuViewModel = viewModel(factory = MenuViewModelFactory(LocalContext.current))
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ColorButton(color = Color.Red, menuViewModel, onAvatarCreated)
        ColorButton(color = Color.Blue, menuViewModel, onAvatarCreated)
        ColorButton(color = Color.Green, menuViewModel, onAvatarCreated)
        ColorButton(color = Color.Yellow, menuViewModel, onAvatarCreated)
    }
}

@Composable
private fun ColorButton(
    color: Color,
    menuViewModel: MenuViewModel,
    onAvatarCreated: () -> Unit
) {
    Button(
        onClick = {
            menuViewModel.saveAvatar(Avatar(color))
            onAvatarCreated()
        },
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(0.7f)
    ) {
        Text("${color.toString().substringAfter('(').substringBefore(')')} Avatar")
    }
}
