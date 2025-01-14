package com.example.maze.navigation

sealed class Screen(val route: String) {
    object Menu : Screen("menu")
    object Avatar : Screen("avatar")
    object Gameplay : Screen("gameplay")
    object Multiplayer : Screen("multiplayer")
    object LabyrinthSelector : Screen("labyrinth_selector")
}
