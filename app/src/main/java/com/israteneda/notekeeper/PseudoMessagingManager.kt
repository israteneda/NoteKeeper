package com.israteneda.notekeeper

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

class PseudoMessagingManager(private val context: Context) {
    private val tag = this::class.simpleName

    private val connectionCallbackMilliseconds = 5000L
    private val postHandler = Handler(Looper.getMainLooper())

    fun connect(connectionCallback: (PseudoMessagingConnection) -> Unit) {
        Log.d(tag, "Initiating connection...")
        postHandler.postDelayed(
            {
                Log.d(tag, "Connection established")
                connectionCallback(PseudoMessagingConnection())
            },
            connectionCallbackMilliseconds)
    }
}

class PseudoMessagingConnection {
    private val tag = this::class.simpleName

    fun send(message: String) {
        Log.d(tag, message)
    }

    fun disconnect() {
        Log.d(tag, "Disconnected")
    }
}