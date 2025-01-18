package com.example.maze.ui.screens.avatar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.maze.data.model.UserContext

@Composable
fun AvatarCreationScreen(onAvatarCreated: () -> Unit) {
    // Gestione dello stato locale con mutableStateOf
    var selectedColor by remember { mutableStateOf(Color.Transparent) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Anteprima dell'avatar senza animazione
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(selectedColor)
                .border(2.dp, Color.Black)
                .padding(16.dp)
        )

        // Pulsanti per la selezione dei colori
        val avatarColors = listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow)
        avatarColors.forEach { color ->
            ColorButton(
                color = color,
                onClick = {
                    selectedColor = color
                    UserContext.avatar = color.hashCode()
                    if (UserContext.isLoggedIn) {
                        UserContext.updateAvatar(color.hashCode())
                    }

                    onAvatarCreated()
                }
            )
        }
    }
}

@Composable
fun ColorButton(
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(0.7f)
    ) {
        Text("${getColorName(color)} Avatar")
    }
}

fun getColorName(color: Color): String {
    return when (color) {
        Color.Red -> "Red"
        Color.Blue -> "Blue"
        Color.Green -> "Green"
        Color.Yellow -> "Yellow"
        else -> "Unknown"
    }
}

@Preview(name = "Avatar Creation Screen", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun AvatarCreationScreenPreview() {
    AvatarCreationScreen(onAvatarCreated = { /* No-op */ })
}
