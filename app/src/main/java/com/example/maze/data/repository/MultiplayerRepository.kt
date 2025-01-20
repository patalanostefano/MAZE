package com.example.maze.data.repository

import GameServer
import android.content.Context
import android.util.Log
import com.example.maze.data.model.GameInvite
import com.example.maze.data.model.GameState
import com.example.maze.data.model.Position
import com.example.maze.data.model.User
import com.example.maze.data.model.UserContext
import com.example.maze.data.network.UserActions
import com.example.maze.data.network.NsdHelper
import com.example.maze.data.network.ServerSocketHelper
import com.example.maze.data.network.SocketHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class MultiplayerRepository(
    context: Context,
    private val userActions: UserActions
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val serverSocketHelper = ServerSocketHelper()
    private val socketHelper = SocketHelper { message ->
        handleIncomingMessage(message)
    }
    private val nsdHelper = NsdHelper(context, serverSocketHelper)
    private val gameServer = GameServer()

    private val _gameInvites = MutableStateFlow<List<GameInvite>>(emptyList())
    val gameInvites: StateFlow<List<GameInvite>> = _gameInvites

    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState

    private val _availablePlayers = MutableStateFlow<List<User>>(emptyList())
    val availablePlayers: StateFlow<List<User>> = _availablePlayers

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    sealed class DiscoveryResult {
        data object Success : DiscoveryResult()
        data class Error(val message: String) : DiscoveryResult()
        data class PermissionError(val permissions: List<String>) : DiscoveryResult()
    }

    suspend fun initializeUser(userId: String) {
        try {
            val username = UserContext.username ?: throw IllegalStateException("Username is null")
            val color = UserContext.avatar
            _currentUser.value = color?.let { User(id = null, username = username,avatarColor = it) }
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
                    // Initialize server socket using the new ServerSocketHelper
                    val port = serverSocketHelper.initializeServer()
                    Log.d("MultiplayerRepository", "Server socket initialized on port: $port")

                    // Register service with the obtained port
                    nsdHelper.registerService("Player-${currentUser.id}")

                    nsdHelper.setServiceFoundCallback { serviceInfo ->
                        Log.d("MultiplayerRepository", "Service found: ${serviceInfo.serviceName} on port ${serviceInfo.port}")
                        val playerId = serviceInfo.serviceName.removePrefix("Player-")
                        coroutineScope.launch {
                            try {
                                val user = userActions.getUserById(playerId)
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
                val gameInvite = GameInvite(
                    fromUser = currentUser,
                    toUser = toUser
                )
                socketHelper.sendMessage(Json.encodeToString(gameInvite))
                Log.d("MultiplayerRepository", "Invite sent to: ${toUser.username}")
            } catch (e: Exception) {
                Log.e("MultiplayerRepository", "Error sending invite: ${e.message}")
                throw e
            }
        }
    }

    fun acceptInvite(invite: GameInvite) {
        coroutineScope.launch {
            try {
                gameServer.connect(
                    gameId = invite.gameId,
                    userId = _currentUser.value?.id ?: throw IllegalStateException("User not initialized")
                )

                gameServer.setOnGameUpdateListener { update ->
                    _gameState.value = update
                }

                _gameInvites.value = _gameInvites.value.filter { it.gameId != invite.gameId }
            } catch (e: Exception) {
                Log.e("MultiplayerRepository", "Error accepting invite: ${e.message}")
            }
        }
    }

    fun declineInvite(invite: GameInvite) {
        _gameInvites.value = _gameInvites.value.filter { it.gameId != invite.gameId }
    }

    private fun handleIncomingMessage(message: String) {
        try {
            val invite = Json.decodeFromString<GameInvite>(message)
            if (_currentUser.value?.id == invite.toUser.id) {
                _gameInvites.value = _gameInvites.value + invite
            }
        } catch (e: Exception) {
            Log.e("MultiplayerRepository", "Error handling message: ${e.message}")
        }
    }

    fun updatePosition(position: Position) {
        coroutineScope.launch {
            try {
                gameServer.updatePosition(position)
            } catch (e: Exception) {
                Log.e("MultiplayerRepository", "Error updating position: ${e.message}")
            }
        }
    }

    fun cleanup() {
        try {
            serverSocketHelper.close()
            nsdHelper.tearDown()
            socketHelper.close()
            Log.d("MultiplayerRepository", "Cleanup completed successfully")
        } catch (e: Exception) {
            Log.e("MultiplayerRepository", "Error during cleanup: ${e.message}")
        }
    }
}