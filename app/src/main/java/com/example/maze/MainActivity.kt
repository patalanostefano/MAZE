package com.example.maze

import android.os.Bundle
import android.util.Log
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
import com.example.maze.data.model.UserContext
import com.example.maze.data.network.AuthService
import com.example.maze.ui.screens.auth.AuthPage
import com.example.maze.ui.screens.gameplay.GameplayScreen
import com.example.maze.ui.screens.labyrinth.LabyrinthSelectorScreen
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import org.opencv.android.OpenCVLoader


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.e("MainActivity", "Failed to load OpenCV")
        } else {
            Log.d("MainActivity", "OpenCV loaded successfully: ${OpenCVLoader.OPENCV_VERSION}")
        }

        // Initialize Firebase
        try {
            Firebase.initialize(this)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing Firebase: ${e.message}", e)
        }

        //Initialize user context
        UserContext.init(this)

        setContent {
            MazeApp()
        }
    }

    companion object {
        // Load OpenCV native library
        init {
            System.loadLibrary("opencv_java4")
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
            val loggedIn = UserContext.isLoggedIn

            NavHost(
                navController = navController,
                startDestination = if (!loggedIn) Screen.Auth.route else Screen.Menu.route
            ) {
                composable(Screen.Menu.route) {
                    MainMenuScreen(
                        onNavigateToAvatar = {
                            navController.navigate(Screen.Avatar.route)
                        },
                        onNavigateToPlay = {
                            navController.navigate(Screen.LabyrinthSelector.route)
                        },
                        onNavigateToMultiplayer = {
                            navController.navigate("${Screen.Multiplayer.route}/${UserContext.username}") //Gets username from local state
                        },
                        onLogout = {
                            navController.navigate(Screen.Auth.route)
                            UserContext.clearSession()
                        }
                    )
                }

                composable(Screen.Auth.route) {
                    AuthPage(
                        onLogin = {
                            navController.navigate(Screen.Menu.route)
                        },
                        getAvatar = {
                            navController.navigate(Screen.Avatar.route)
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

                composable(Screen.LabyrinthSelector.route) {
                    LabyrinthSelectorScreen(
                        onLabyrinthSelected = { labyrinthId ->
                            navController.navigate(Screen.Gameplay.createRoute(labyrinthId))
                        }
                    )
                }

                composable(
                    route = Screen.Gameplay.route,
                    arguments = listOf(
                        navArgument("labyrinthId") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val labyrinthId = backStackEntry.arguments?.getString("labyrinthId")
                        ?: return@composable
                    GameplayScreen(
                        labyrinthId = labyrinthId,
                        onExit = {
                            navController.navigate(Screen.Menu.route) {
                                popUpTo(Screen.Menu.route) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                composable(
                    route = "${Screen.Multiplayer.route}/{userId}",
                    arguments = listOf(
                        navArgument("userId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")
                        ?: return@composable
                    MultiplayerScreen(
                        userId = userId,
                        onNavigateToGame = { gameInvite ->
                            navController.navigate(
                                Screen.MultiplayerGameplay.createRoute(gameInvite.gameId)
                            )
                        }
                    )
                }

                composable(
                    route = Screen.MultiplayerGameplay.route,
                    arguments = listOf(
                        navArgument("gameId") {
                            type = NavType.StringType
                            nullable = true
                        }
                    )
                ) { backStackEntry ->
                    val gameId = backStackEntry.arguments?.getString("gameId")
                        ?: return@composable
                    // Multiplayer game implementation
                }
            }
        }
    }
}