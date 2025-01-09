package com.example.maze.ui.screens.multiplayer

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.maze.data.repository.MultiplayerRepository

class MultiplayerViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MultiplayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MultiplayerViewModel(MultiplayerRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
