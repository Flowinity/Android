package com.troplo.privateuploader.api

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.troplo.privateuploader.BuildConfig
import com.troplo.privateuploader.data.model.MessageEvent
import com.troplo.privateuploader.data.model.Typing
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.Collections
import java.util.concurrent.Executors

object SocketHandlerService {
    private const val SERVER_URL = BuildConfig.SERVER_URL

    private var socket: Socket? = null
    val gson = Gson()
    var connected = mutableStateOf(false)

    fun initializeSocket(token: String, context: Context, platform: String = "android_kotlin") {
        try {
            val options = IO.Options()
            options.forceNew = true
            options.reconnection = true
            options.auth = Collections.singletonMap("token", token)
            options.query = "platform=$platform"
            options.transports = arrayOf("websocket")
            options.reconnectionDelay = 1000
            options.reconnectionDelayMax = 5000
            options.reconnectionAttempts = 99999
            socket = IO.socket(SERVER_URL, options)
            if (socket != null) {
                socket?.open()
                println("Socket connected ${socket?.isActive}")
            } else {
                println("Socket is null")
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