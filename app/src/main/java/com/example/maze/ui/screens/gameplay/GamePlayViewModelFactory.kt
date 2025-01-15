// ui/screens/gameplay/GameplayViewModelFactory.kt
package com.example.maze.ui.screens.gameplay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.maze.data.network.FirebaseService
import com.example.maze.data.repository.LabyrinthRepository

class GameplayViewModelFactory(
    private val labyrinthId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameplayViewModel::class.java)) {
            val repository = LabyrinthRepository(FirebaseService())
            @Suppress("UNCHECKED_CAST")
            return GameplayViewModel(labyrinthId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
