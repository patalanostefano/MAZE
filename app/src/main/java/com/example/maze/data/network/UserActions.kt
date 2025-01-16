package com.example.maze.data.network
import com.example.maze.data.model.User

interface UserActions {
    suspend fun getUserById(id: String): User?
    suspend fun getUserByName(name: String): User?
    suspend fun createUser(username: String, avatarColor: Int): User
    suspend fun updateUser(user: User): Boolean
}