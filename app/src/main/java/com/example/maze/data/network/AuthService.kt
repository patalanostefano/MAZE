// data/network/MongoDbServiceImpl.kt
package com.example.maze.data.network

import com.example.maze.data.model.User
import com.example.maze.data.model.UserNotFoundException
import com.example.maze.data.repository.AuthRepository

class AuthService(
    private val authRepository: AuthRepository
) : UserActions {
    override suspend fun getUserById(id: String): User? {
        return authRepository.getUserById(id)
    }

    override suspend fun getUserByName(name: String): User? {
        return authRepository.getUserByName(name)
    }

    override suspend fun createUser(username: String, avatarColor: Int): User { //this doesn't return a user _id. lmk if you want that
        return authRepository.createUser(username, avatarColor)
    }

    override suspend fun updateUser(user: User): Boolean {
        return authRepository.updateUser(user)
    }

    //Extremely basic.
    suspend fun login(username: String): User { //Rename vars?
        val user = getUserByName(username)

        if(user == null) {
            throw UserNotFoundException("User not found")
        }

        return user
    }
 }
