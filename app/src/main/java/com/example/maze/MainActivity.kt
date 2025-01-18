package com.example.maze

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
import com.example.maze.data.model.UserAlreadyExistsException
import com.example.maze.data.model.UserNotFoundException
import com.example.maze.data.network.AuthService
import com.example.maze.data.repository.AuthRepository
import com.example.maze.ui.screens.auth.AuthPage
import com.example.maze.ui.screens.auth.PreferencesManager
import com.example.maze.ui.screens.gameplay.GameplayScreen
import com.example.maze.ui.screens.labyrinth.LabyrinthSelectorScreen
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var authService: AuthService
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        try {
            Firebase.initialize(this)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing Firebase: ${e.message}", e)
        }

        //Initialize Firestore
        try {
            authRepository = AuthRepository()
            authService = AuthService(authRepository)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing Auth: ${e.message}", e)
        }

        setContent {
            MazeApp(authService)
        }
    }
}

@Composable
fun MazeApp(authService: AuthService) {
    val snackbar = remember { SnackbarHostState() }
    MAZETheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            val loggedIn = false

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
                            navController.navigate("${Screen.Multiplayer.route}/${"user1"}") //Gets username from saved state
                        }
                    )
                }

                composable(Screen.Auth.route) {
                    val coroutineScope = rememberCoroutineScope()

                    AuthPage(
                        onLogin = { userName ->
                            coroutineScope.launch {
                                try {
                                    authService.login(userName)
                                    navController.navigate(Screen.Menu.route)
                                } catch (e: UserNotFoundException) {
                                    Log.e("MainActivity",e.message,e)
                                    snackbar.showSnackbar("User not found")
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Failed to login: ${e.message}", e)
                                    snackbar.showSnackbar("Failed to login")
                                }
                            }
                        },
                        onRegister = { userName ->
                            coroutineScope.launch {
                                try {
                                    authService.createUser(userName, 1)
                                    snackbar.showSnackbar("User registered")
                                } catch (e : UserAlreadyExistsException) {
                                    Log.e("MainActivity", e.message,e)
                                    snackbar.showSnackbar("User already exists")
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Failed to register: ${e.message}",e)
                                    snackbar.showSnackbar("Failed to register")
                                }
                            }
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
        SnackbarHost(hostState = snackbar)
    }
}