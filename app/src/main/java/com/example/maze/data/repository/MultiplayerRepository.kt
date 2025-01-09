package com.example.maze.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.example.maze.data.model.GameInvite
import com.example.maze.data.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.channels.Channel

class MultiplayerRepository(private val context: Context) {
    private val wifiP2pManager: WifiP2pManager by lazy {
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }

    private val channel: WifiP2pManager.Channel by lazy {
        wifiP2pManager.initialize(context, Looper.getMainLooper(), null)
    }

    private val _availablePlayers = MutableStateFlow<List<WifiP2pDevice>>(emptyList())
    val availablePlayers: StateFlow<List<WifiP2pDevice>> = _availablePlayers

    private val inviteChannel = Channel<GameInvite>()

    sealed class DiscoveryResult {
        object Success : DiscoveryResult()
        data class PermissionError(val permissions: List<String>) : DiscoveryResult()
        data class Error(val message: String) : DiscoveryResult()
    }

    fun startDiscovery(): DiscoveryResult {
        // Check permissions first
        val missingPermissions = REQUIRED_PERMISSIONS.filter {
            ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            return DiscoveryResult.PermissionError(missingPermissions)
        }

        try {
            // Safe to call after permission check
            @Suppress("MissingPermission")
            wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    // Discovery started successfully
                    requestPeers()
                }
                override fun onFailure(reasonCode: Int) {
                    // Handle failure
                }
            })
            return DiscoveryResult.Success
        } catch (e: Exception) {
            return DiscoveryResult.Error(e.message ?: "Unknown error occurred")
        }
    }

    private fun requestPeers() {
        if (!hasRequiredPermissions()) return

        @Suppress("MissingPermission")
        wifiP2pManager.requestPeers(channel) { peers ->
            _availablePlayers.value = peers.deviceList.toList()
        }
    }

    fun sendInvite(invite: GameInvite): DiscoveryResult {
        if (!hasRequiredPermissions()) {
            return DiscoveryResult.PermissionError(
                REQUIRED_PERMISSIONS.filter {
                    ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
                }
            )
        }

        try {
            // Implement sending invite using WiFi P2P
            return DiscoveryResult.Success
        } catch (e: Exception) {
            return DiscoveryResult.Error(e.message ?: "Unknown error occurred")
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.NEARBY_WIFI_DEVICES,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        )
    }
}
