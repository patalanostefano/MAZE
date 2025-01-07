package com.example.maze

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.maze.navigation.Screen
import com.example.maze.ui.screens.avatar.AvatarCreationScreen
import com.example.maze.ui.screens.menu.MainMenuScreen
import com.example.maze.ui.theme.MAZETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MazeApp()
        }
    }
}

@Composable
fun MazeApp() {
    MAZETheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Screen.Menu.route
            ) {
                composable(Screen.Menu.route) {
                    MainMenuScreen(
                        onNavigateToAvatar = { navController.navigate(Screen.Avatar.route) },
                        onNavigateToPlay = { navController.navigate(Screen.Gameplay.route) },
                        onNavigateToMultiplayer = { navController.navigate(Screen.Multiplayer.route) }
                    )
                }
                composable(Screen.Avatar.route) {
                    AvatarCreationScreen(
                        onAvatarCreated = { navController.navigateUp() }
                    )
                }
                // Other screens
            }
        }
    }
}
