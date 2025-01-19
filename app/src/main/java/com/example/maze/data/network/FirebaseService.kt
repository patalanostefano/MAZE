package com.example.maze.data.network
import android.util.Log
import com.example.maze.data.model.Labyrinth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
class FirebaseService {
    private val db = Firebase.firestore
    private val labyrinthsCollection = db.collection("labyrinths")

    suspend fun getLabyrinth(id: String): Labyrinth? {
        return try {
            Log.d("FirebaseService", "Fetching labyrinth with id: $id")
            val document = labyrinthsCollection.document(id).get().await()
            document.data?.let { data ->
                Log.d("FirebaseService", "Raw structure data: ${data["structure"]}")
                Labyrinth.fromFirestore(data).also { labyrinth ->
                    Log.d("FirebaseService", "Parsed structure size: ${labyrinth.structure.size}")
                    Log.d("FirebaseService", "First row size: ${labyrinth.structure.firstOrNull()?.size}")
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error fetching labyrinth: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun getAllLabyrinths(): List<Labyrinth> {
        return try {
            Log.d("FirebaseService", "Fetching labyrinths...")
            val snapshot = labyrinthsCollection.get().await()
            Log.d("FirebaseService", "Found ${snapshot.size()} documents")

            snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Labyrinth.fromFirestore(it) }
            }.also { labyrinths ->
                labyrinths.forEach { labyrinth ->
                    Log.d("FirebaseService", "Successfully parsed labyrinth: ${labyrinth.id}")
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error fetching labyrinths: ${e.message}")
            emptyList()
        }
    }
}
