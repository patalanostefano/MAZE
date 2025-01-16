package com.example.maze.ui.screens.multiplayer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.maze.data.repository.MultiplayerRepository
import com.example.maze.data.network.AuthService
import com.example.maze.data.repository.AuthRepository

class MultiplayerViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MultiplayerViewModel::class.java)) {
            val authRepository = AuthRepository()
            val authService = AuthService(authRepository) // Implement this
            val repository = MultiplayerRepository(context, authService)
            @Suppress("UNCHECKED_CAST")
            return MultiplayerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
