// data/network/ServerSocketHelper.kt
package com.example.maze.data.network

import java.net.ServerSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServerSocketHelper {
    private var serverSocket: ServerSocket? = null

    suspend fun initializeServer(): Int = withContext(Dispatchers.IO) {
        try {
            serverSocket = ServerSocket(0) // 0 lets the system pick an available port
            return@withContext serverSocket?.localPort ?: throw Exception("Failed to initialize server socket")
        } catch (e: Exception) {
            throw Exception("Failed to create server socket: ${e.message}")
        }
    }

    fun getPort(): Int = serverSocket?.localPort ?: throw Exception("Server socket not initialized")

    fun close() {
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            // Handle close exception
        } finally {
            serverSocket = null
        }
    }
}
