package com.example.maze.data.repository

import com.example.maze.data.model.Labyrinth
import com.example.maze.data.network.FirebaseService

class LabyrinthRepository(
    private val firebaseService: FirebaseService
) {
    suspend fun getAllLabyrinths(): List<Labyrinth> {
        return firebaseService.getAllLabyrinths()
    }

    suspend fun getLabyrinth(id: String): Labyrinth? {
        return firebaseService.getLabyrinth(id)
    }
}
