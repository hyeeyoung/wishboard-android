package com.hyeeyoung.wishboard.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import com.hyeeyoung.wishboard.util.extension.toStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow

class NetworkMonitor(private val context: Context, private val coroutineScope: CoroutineScope) {
    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val request = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    val isConnected: StateFlow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
                super.onLost(network)
            }
        }

        trySend(ConnectedCompat.isConnected(connectivityManager))
        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.toStateFlow(coroutineScope, false)
}