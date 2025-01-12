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
import com.example.maze.ui.screens.multiplayer.MultiplayerScreen
import com.example.maze.ui.theme.MAZETheme
import androidx.navigation.NavType
import androidx.navigation.navArgument

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
                        onNavigateToAvatar = {
                            navController.navigate(Screen.Avatar.route)
                        },
                        onNavigateToPlay = {
                            navController.navigate(Screen.Gameplay.route)
                        },
                        onNavigateToMultiplayer = {
                            // Assuming you'll get the userId from MenuViewModel
                            navController.navigate("${Screen.Multiplayer.route}/user123")
                        }
                    )
                }

                composable(Screen.Avatar.route) {
                    AvatarCreationScreen(
                        onAvatarCreated = {
                            navController.navigateUp()
                        }
                    )
                }

                composable(
                    route = "${Screen.Multiplayer.route}/{userId}",
                    arguments = listOf(
                        navArgument("userId") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                    MultiplayerScreen(
                        userId = userId,
                        onNavigateToGame = { gameInvite ->
                            navController.navigate("${Screen.Gameplay.route}/${gameInvite.gameId}")
                        }
                    )
                }

                composable(
                    route = "${Screen.Gameplay.route}/{gameId}",
                    arguments = listOf(
                        navArgument("gameId") {
                            type = NavType.StringType
                            nullable = true
                        }
                    )
                ) { backStackEntry ->
                    val gameId = backStackEntry.arguments?.getString("gameId")
                    // Your game screen implementation
                    // GameScreen(gameId = gameId)
                }
            }
        }
    }
}
