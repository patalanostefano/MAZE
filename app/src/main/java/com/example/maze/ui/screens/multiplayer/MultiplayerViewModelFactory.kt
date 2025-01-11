package com.example.maze.ui.screens.multiplayer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.maze.data.repository.MultiplayerRepository
import com.example.maze.data.network.MongoDbServiceImpl

class MultiplayerViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MultiplayerViewModel::class.java)) {
            val mongoDbService = MongoDbServiceImpl() // Implement this
            val repository = MultiplayerRepository(context, mongoDbService)
            @Suppress("UNCHECKED_CAST")
            return MultiplayerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
