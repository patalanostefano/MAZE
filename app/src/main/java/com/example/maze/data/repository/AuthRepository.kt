package com.example.maze.data.repository

import android.util.Log
import com.example.maze.data.model.User
import com.example.maze.data.network.UserActions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Handles communication with Firestore.
 */
class AuthRepository : UserActions {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    private fun documentToUser(document: Map<String, Any>): User {
        return User(
            id = document["id"] as? String,
            username = document["username"] as String,
            avatarColor = (document["avatarColor"] as? Long)?.toInt() ?: 0
        )
    }

    private fun userToDocument(user: User): Map<String, Any> {
        return mapOf(
            "username" to user.username,
            "avatarColor" to user.avatarColor
        )
    }

    override suspend fun getUserByName(username: String): User? {
        val querySnapshot = usersCollection
            .whereEqualTo("username", username)
            .get()
            .await()

        return querySnapshot.documents.firstOrNull()?.let {
            documentToUser(it.data ?: emptyMap())
        }
    }

    override suspend fun getUserById(id: String): User? {
        val documentSnapshot = usersCollection.document(id).get().await()
        return if (documentSnapshot.exists()) {
            documentToUser(documentSnapshot.data ?: emptyMap())
        } else {
            null
        }
    }

    override suspend fun createUser(username: String, avatarColor: Int): User {
        val newUser = User(id = null, username = username, avatarColor = avatarColor)
        val documentRef = usersCollection.add(userToDocument(newUser)).await()

        Log.i("New user created in Firebase: $username",username)
        return newUser.copy(id = documentRef.id)
    }

    override suspend fun updateUser(user: User): Boolean {
        if (user.id == null) return false

        usersCollection.document(user.id)
            .set(userToDocument(user))
            .await()

        // Firestore doesn't return modified count; assume success if no exception
        return true
    }
}
