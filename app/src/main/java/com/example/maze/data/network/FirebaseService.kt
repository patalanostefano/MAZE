package com.example.maze.data.network

// data/network/FirebaseService.kt
import com.example.maze.data.model.Labyrinth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val db = FirebaseFirestore.getInstance()
    private val labyrinthsCollection = db.collection("labyrinths")

    suspend fun getAllLabyrinths(): List<Labyrinth> {
        return try {
            labyrinthsCollection.get().await().documents.mapNotNull {
                it.toObject<Labyrinth>()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getLabyrinth(id: String): Labyrinth? {
        return try {
            labyrinthsCollection.document(id).get().await().toObject<Labyrinth>()
        } catch (e: Exception) {
            null
        }
    }
}
