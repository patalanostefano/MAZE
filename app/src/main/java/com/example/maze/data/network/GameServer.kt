// data/network/GameServer.kt

import android.util.Log
import com.example.maze.data.model.GameConnectionRequest
import com.example.maze.data.model.GameState
import com.example.maze.data.model.Position
import com.example.maze.data.model.PositionUpdate
import com.example.maze.data.network.SocketHelper
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class GameServer {
    companion object {
        const val SERVER_HOST = "your-pythonanywhere-domain.com"
        const val SERVER_PORT = 8000
    }

    private var onGameUpdateListener: ((GameState) -> Unit)? = null
    private val socket = SocketHelper { message ->
        handleServerMessage(message)
    }

    private fun handleServerMessage(message: String) {
        try {
            val gameState = Json.decodeFromString<GameState>(message)
            onGameUpdateListener?.invoke(gameState)
        } catch (e: Exception) {
            Log.e("GameServer", "Error parsing message: $e")
        }
    }

    fun setOnGameUpdateListener(listener: (GameState) -> Unit) {
        onGameUpdateListener = listener
    }

    fun connect(gameId: String, userId: String) {
        socket.connect(SERVER_HOST, SERVER_PORT)
        socket.sendMessage(Json.encodeToString(
            GameConnectionRequest(gameId = gameId, userId = userId)
        ))
    }

    fun updatePosition(position: Position) {
        socket.sendMessage(Json.encodeToString(
            PositionUpdate(position = position)
        ))
    }

    fun disconnect() {
        socket.close()
    }
}
