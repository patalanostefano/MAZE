package com.example.maze.data.network
import com.example.maze.data.model.User

interface MongoDbService {
    suspend fun getUser(id: String): User?
    suspend fun createUser(username: String, avatarColor: Int): User
    suspend fun updateUser(user: User): Boolean
}