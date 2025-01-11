package com.example.maze.data.network

import java.net.Socket

class SocketHelper(
    private val messageCallback: (String) -> Unit
) {
    private var socket: Socket? = null

    fun connect(host: String, port: Int) {
        socket = Socket(host, port)
        // Implement connection logic
    }

    fun sendMessage(message: String) {
        socket?.getOutputStream()?.write(message.toByteArray())
    }

    fun close() {
        socket?.close()
    }
}