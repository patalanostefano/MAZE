package com.example.maze.data.network

import android.util.Log
import com.example.maze.data.model.Labyrinth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val db = FirebaseFirestore.getInstance()
    private val labyrinthsCollection = db.collection("labyrinths")

    suspend fun getAllLabyrinths(): List<Labyrinth> {
        return try {
            Log.d("FirebaseService", "Fetching labyrinths...")
            val documents = labyrinthsCollection.get().await().documents
            Log.d("FirebaseService", "Found ${documents.size} documents")
            documents.mapNotNull { doc ->
                try {
                    doc.toObject<Labyrinth>()?.also {
                        Log.d("FirebaseService", "Successfully parsed labyrinth: ${it.id}")
                    }
                } catch (e: Exception) {
                    Log.e("FirebaseService", "Error parsing document: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error fetching labyrinths: ${e.message}")
            emptyList()
        }
    }

    suspend fun getLabyrinth(id: String): Labyrinth? {
        return try {
            Log.d("FirebaseService", "Fetching labyrinth with id: $id")
            val document = labyrinthsCollection.document(id).get().await()
            document.toObject<Labyrinth>()?.also {
                Log.d("FirebaseService", "Successfully fetched labyrinth: ${it.id}")
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error fetching labyrinth: ${e.message}")
            null
        }
    }
}
