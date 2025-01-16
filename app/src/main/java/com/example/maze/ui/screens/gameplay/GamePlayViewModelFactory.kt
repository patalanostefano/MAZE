// ui/screens/gameplay/GameplayViewModelFactory.kt
package com.example.maze.ui.screens.gameplay

import android.content.Context
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.maze.data.network.FirebaseService
import com.example.maze.data.repository.LabyrinthRepository

class GameplayViewModelFactory(
    private val labyrinthId: String,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameplayViewModel::class.java)) {
            val repository = LabyrinthRepository(FirebaseService())
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            @Suppress("UNCHECKED_CAST")
            return GameplayViewModel(labyrinthId, repository, sensorManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
