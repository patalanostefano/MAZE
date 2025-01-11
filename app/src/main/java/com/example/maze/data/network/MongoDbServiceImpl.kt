// data/network/MongoDbServiceImpl.kt
package com.example.maze.data.network

import com.example.maze.data.model.User

class MongoDbServiceImpl : MongoDbService {
    override suspend fun getUser(id: String): User? {
        // Temporary implementation for testing
        return User(
            id = id,
            username = "Test User",
            avatarColor = 0xFF0000FF.toInt()
        )
    }

    override suspend fun createUser(username: String, avatarColor: Int): User {
        // Temporary implementation for testing
        return User(
            id = java.util.UUID.randomUUID().toString(),
            username = username,
            avatarColor = avatarColor
        )
    }

    override suspend fun updateUser(user: User): Boolean {
        // Temporary implementation for testing
        return true
    }
}
