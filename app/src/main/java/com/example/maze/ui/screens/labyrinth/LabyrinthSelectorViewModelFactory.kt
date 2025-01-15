package com.example.maze.ui.screens.labyrinth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.maze.data.network.FirebaseService
import com.example.maze.data.repository.LabyrinthRepository

class LabyrinthSelectorViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LabyrinthSelectorViewModel::class.java)) {
            val repository = LabyrinthRepository(FirebaseService())
            @Suppress("UNCHECKED_CAST")
            return LabyrinthSelectorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

