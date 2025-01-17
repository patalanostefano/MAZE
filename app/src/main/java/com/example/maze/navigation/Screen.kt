package com.example.maze.navigation


sealed class Screen(val route: String) {
    object Menu : Screen("menu")
    object Auth : Screen("auth")
    object Avatar : Screen("avatar")
    object Gameplay : Screen("gameplay/{labyrinthId}") {
        fun createRoute(labyrinthId: String) = "gameplay/$labyrinthId"
    }
    object MultiplayerGameplay : Screen("multiplayer_gameplay/{gameId}") {
        fun createRoute(gameId: String) = "multiplayer_gameplay/$gameId"
    }
    object Multiplayer : Screen("multiplayer/{userId}")
    object LabyrinthSelector : Screen("labyrinth_selector")
}