package com.example.maze.data.repository

import android.content.Context
import android.net.nsd.NsdServiceInfo
import com.example.maze.data.model.User
import com.example.maze.data.network.MongoDbService
import com.example.maze.data.network.NsdHelper
import com.example.maze.data.network.SocketHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MultiplayerRepository(
    private val context: Context,
    private val mongoDbService: MongoDbService
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val nsdHelper = NsdHelper(context)
    private var socketHelper: SocketHelper? = null

    private val _availablePlayers = MutableStateFlow<List<User>>(emptyList())
    val availablePlayers: StateFlow<List<User>> = _availablePlayers

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    sealed class DiscoveryResult {
        object Success : DiscoveryResult()
        data class Error(val message: String) : DiscoveryResult()
        data class PermissionError(val permissions: List<String>) : DiscoveryResult()
    }

    init {
        socketHelper = SocketHelper { message ->
            handleIncomingMessage(message)
        }
    }

    suspend fun initializeUser(userId: String) {
        _currentUser.value = mongoDbService.getUser(userId)
    }

    suspend fun startDiscovery(): DiscoveryResult {
        return try {
            val currentUser = _currentUser.value ?: throw IllegalStateException("User not initialized")

            nsdHelper.registerService("Player-${currentUser.id}")

            // Update NsdHelper to handle service resolution
            nsdHelper.setServiceFoundCallback { serviceInfo: NsdServiceInfo ->
                val playerId = serviceInfo.serviceName.removePrefix("Player-")
                coroutineScope.launch {
                    val user = mongoDbService.getUser(playerId)
                    user?.let { updateAvailablePlayers(it) }
                }
            }

            nsdHelper.discoverServices()
            DiscoveryResult.Success
        } catch (e: Exception) {
            DiscoveryResult.Error(e.message ?: "Unknown error")
        }
    }

    private fun updateAvailablePlayers(user: User) {
        val currentList = _availablePlayers.value.toMutableList()
        if (!currentList.contains(user)) {
            currentList.add(user)
            _availablePlayers.value = currentList
        }
    }

    suspend fun sendInvite(toUser: User) {
        val currentUser = _currentUser.value ?: return
        socketHelper?.sendMessage("INVITE:${currentUser.id}:${toUser.id}")
    }

    private fun handleIncomingMessage(message: String) {
        val parts = message.split(":")
        when (parts[0]) {
            "INVITE" -> {
                val fromUserId = parts[1]
                val toUserId = parts[2]
                coroutineScope.launch {
                    val fromUser = mongoDbService.getUser(fromUserId)
                    // Handle invite UI update
                }
            }
        }
    }

    fun cleanup() {
        nsdHelper.tearDown()
        socketHelper?.close()
    }
}
