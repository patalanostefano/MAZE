package com.example.maze.data.repository

import android.content.Context
import android.util.Log
import com.example.maze.data.model.User
import com.example.maze.data.network.MongoDbService
import com.example.maze.data.network.NsdHelper
import com.example.maze.data.network.ServerSocketHelper
import com.example.maze.data.network.SocketHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MultiplayerRepository(
    context: Context,
    private val mongoDbService: MongoDbService
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val serverSocketHelper = ServerSocketHelper()
    private val nsdHelper = NsdHelper(context, serverSocketHelper)
    private var socketHelper: SocketHelper? = null

    private val _availablePlayers = MutableStateFlow<List<User>>(emptyList())
    val availablePlayers: StateFlow<List<User>> = _availablePlayers

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    sealed class DiscoveryResult {
        data object Success : DiscoveryResult()
        data class Error(val message: String) : DiscoveryResult()
        data class PermissionError(val permissions: List<String>) : DiscoveryResult()
    }

    init {
        socketHelper = SocketHelper { message ->
            handleIncomingMessage(message)
        }
    }

    suspend fun initializeUser(userId: String) {
        try {
            _currentUser.value = mongoDbService.getUser(userId)
        } catch (e: Exception) {
            Log.e("MultiplayerRepository", "Failed to initialize user: ${e.message}")
            throw e
        }
    }

    suspend fun startDiscovery(): DiscoveryResult {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = _currentUser.value ?: throw IllegalStateException("User not initialized")

                try {
                    // Initialize server socket first
                    val port = serverSocketHelper.initializeServer()
                    Log.d("MultiplayerRepository", "Server socket initialized on port: $port")

                    // Register service with the obtained port
                    nsdHelper.registerService("Player-${currentUser.id}")

                    nsdHelper.setServiceFoundCallback { serviceInfo ->
                        Log.d("MultiplayerRepository", "Service found: ${serviceInfo.serviceName} on port ${serviceInfo.port}")
                        val playerId = serviceInfo.serviceName.removePrefix("Player-")
                        coroutineScope.launch {
                            try {
                                val user = mongoDbService.getUser(playerId)
                                user?.let { updateAvailablePlayers(it) }
                            } catch (e: Exception) {
                                Log.e("MultiplayerRepository", "Error processing found service: ${e.message}")
                            }
                        }
                    }

                    nsdHelper.discoverServices()
                    DiscoveryResult.Success
                } catch (e: Exception) {
                    Log.e("MultiplayerRepository", "Network service error: ${e.message}")
                    DiscoveryResult.Error("Network service error: ${e.message}")
                }
            } catch (e: Exception) {
                Log.e("MultiplayerRepository", "Discovery error: ${e.message}")
                DiscoveryResult.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun updateAvailablePlayers(user: User) {
        val currentList = _availablePlayers.value.toMutableList()
        if (!currentList.contains(user)) {
            currentList.add(user)
            _availablePlayers.value = currentList
            Log.d("MultiplayerRepository", "Added player: ${user.username}")
        }
    }

    suspend fun sendInvite(toUser: User) {
        withContext(Dispatchers.IO) {
            try {
                val currentUser = _currentUser.value ?: throw IllegalStateException("Current user not initialized")
                socketHelper?.sendMessage("INVITE:${currentUser.id}:${toUser.id}")
                Log.d("MultiplayerRepository", "Invite sent to: ${toUser.username}")
            } catch (e: Exception) {
                Log.e("MultiplayerRepository", "Error sending invite: ${e.message}")
                throw e
            }
        }
    }

    private fun handleIncomingMessage(message: String) {
        try {
            val parts = message.split(":")
            if (parts[0] == "INVITE") {
                val fromUserId = parts[1]
                coroutineScope.launch {
                    try {
                        val fromUser = mongoDbService.getUser(fromUserId)
                        Log.d("MultiplayerRepository", "Received invite from: ${fromUser?.username}")
                        // Handle invite UI update
                    } catch (e: Exception) {
                        Log.e("MultiplayerRepository", "Error processing invite: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MultiplayerRepository", "Error handling message: ${e.message}")
        }
    }

    fun cleanup() {
        try {
            serverSocketHelper.close()
            nsdHelper.tearDown()
            socketHelper?.close()
            Log.d("MultiplayerRepository", "Cleanup completed successfully")
        } catch (e: Exception) {
            Log.e("MultiplayerRepository", "Error during cleanup: ${e.message}")
        }
    }
}
