package com.troplo.privateuploader.api

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.troplo.privateuploader.BuildConfig
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
import java.util.Collections

object SocketHandlerService {
    private var socket: Socket? = null
    val gson = Gson()
    var connected = mutableStateOf(false)
    var baseUrl = BuildConfig.SERVER_URL

    fun initializeSocket(token: String, context: Context, platform: String = "android_kotlin") {
        try {
            val options = IO.Options()
            options.forceNew = true
            options.reconnection = true
            options.auth = Collections.singletonMap("token", token)
            options.query = "platform=$platform&version=3"
            options.transports = arrayOf("websocket")
            options.reconnectionDelay = 1000
            options.reconnectionDelayMax = 5000
            options.reconnectionAttempts = 99999
            socket = IO.socket(baseUrl, options)
            if (socket != null) {
                socket?.open()
                Log.d("TPU.Untagged", "Socket connected ${socket?.isActive}")
            } else {
                Log.d("TPU.Untagged", "Socket is null")
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun getSocket(): Socket? {
        return socket
    }

    fun closeSocket() {
        socket?.disconnect()
        socket = null
    }
}