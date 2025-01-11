// data/network/NsdHelper.kt
package com.example.maze.data.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo

class NsdHelper(private val context: Context) {
    private val nsdManager: NsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val SERVICE_TYPE = "_maze._tcp."
    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null
    private var serviceFoundCallback: ((NsdServiceInfo) -> Unit)? = null

    fun setServiceFoundCallback(callback: (NsdServiceInfo) -> Unit) {
        serviceFoundCallback = callback
    }

    fun registerService(serviceName: String) {
        val serviceInfo = NsdServiceInfo().apply {
            this.serviceName = serviceName
            this.serviceType = SERVICE_TYPE
            this.port = 0 // Choose an available port
        }

        registrationListener = object : NsdManager.RegistrationListener {
            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {}
            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {}
        }

        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
    }

    fun discoverServices() {
        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                // Handle discovery start failure
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                // Handle discovery stop failure
            }

            override fun onDiscoveryStarted(serviceType: String) {
                // Handle discovery started
            }

            override fun onDiscoveryStopped(serviceType: String) {
                // Handle discovery stopped
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                serviceFoundCallback?.invoke(serviceInfo)
                nsdManager.resolveService(serviceInfo, createResolveListener())
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                // Handle service lost
            }
        }

        nsdManager.discoverServices(
            SERVICE_TYPE,
            NsdManager.PROTOCOL_DNS_SD,
            discoveryListener
        )
    }

    private fun createResolveListener(): NsdManager.ResolveListener {
        return object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Handle resolve failure
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                serviceFoundCallback?.invoke(serviceInfo)
            }
        }
    }

    fun tearDown() {
        try {
            registrationListener?.let {
                nsdManager.unregisterService(it)
            }
            discoveryListener?.let {
                nsdManager.stopServiceDiscovery(it)
            }
        } catch (e: Exception) {
            // Handle teardown exceptions
        } finally {
            registrationListener = null
            discoveryListener = null
            serviceFoundCallback = null
        }
    }
}
