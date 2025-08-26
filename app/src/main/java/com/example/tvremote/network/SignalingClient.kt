package com.example.tvremote.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class SignalingClient {

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    fun connect() {
        // TODO: Implement WebSocket connection
    }

    fun send(message: String) {
        // TODO: Implement sending message
    }

    fun disconnect() {
        coroutineScope.cancel()
    }
}
