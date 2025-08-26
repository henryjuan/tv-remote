package com.example.tvremote.webrtc

import android.content.Context
import org.webrtc.PeerConnectionFactory

class WebRTCClient(
    private val context: Context,
    private val signalingClient: com.example.tvremote.network.SignalingClient
) {

    init {
        initPeerConnectionFactory()
    }

    private fun initPeerConnectionFactory() {
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)
    }

    fun createPeerConnection() {
        // TODO: Implement PeerConnection creation
    }

    fun close() {
        // TODO: Implement closing PeerConnection
    }
}
