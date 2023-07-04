package com.troplo.privateuploader.api

import android.content.Context
import android.content.Intent
import android.util.Log
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

object SocketHandler {
    var baseUrl = BuildConfig.SERVER_URL

    private var socket: Socket? = null
    val gson = Gson()
    var connected = mutableStateOf(false)

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
                if (platform !== "android_kotlin_background_service") {
                    socket?.on(Socket.EVENT_CONNECT) {
                        this.connected.value = true
                        Log.d(
                            "TPU.Untagged",
                            "Socket connected ${socket?.isActive}, Connected: ${this.connected.value}"
                        )
                    }
                    socket?.on(Socket.EVENT_DISCONNECT) {
                        this.connected.value = false
                        Log.d(
                            "TPU.Untagged",
                            "Socket disconnected ${socket?.isActive}, Connected: ${this.connected.value}"
                        )
                    }
                    socket?.on(Socket.EVENT_CONNECT_ERROR) {
                        try {
                            this.connected.value = false
                            Log.d(
                                "TPU.Untagged",
                                "Socket connect error ${socket?.isActive}, Connected: ${this.connected.value}, Error: ${it[0]}"
                            )
                        } catch (e: Exception) {
                            //
                        }
                    }
                    socket?.on("message") { it ->
                        val jsonArray = it[0] as JSONObject
                        val payload = jsonArray.toString()
                        val messageEvent = gson.fromJson(payload, MessageEvent::class.java)

                        val message = messageEvent.message
                        Log.d("TPU.Untagged", "Message received (SocketHandler): $message")
                        val chat =
                            ChatStore.chats.value.find { it.association?.id == messageEvent.association.id }
                        if (messageEvent.association.id != ChatStore.associationId.value) {
                            // increase unread count
                            Log.d("TPU.Untagged", chat.toString())
                            if (chat != null) {
                                chat.unread = chat.unread?.plus(1)
                            }
                        } else if(chat != null) {
                            ChatStore.setChats(listOf(chat) + ChatStore.chats.value.filter { it.association?.id != messageEvent.association.id })
                        }
                    }
                    socket?.on("typing") { it ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val jsonArray = it[0] as JSONObject
                            val payload = jsonArray.toString()
                            val typeEvent = gson.fromJson(payload, Typing::class.java)
                            Log.d("TPU.Untagged", "TYPING EVENT: $typeEvent")
                            val find = ChatStore.typers.value.find { it.userId == typeEvent.userId }
                            if (find == null) {
                                ChatStore.typers.value = ChatStore.typers.value + typeEvent
                            } else {
                                ChatStore.typers.value =
                                    ChatStore.typers.value.filter { it.userId != typeEvent.userId } + typeEvent
                            }

                            // Remove the event
                            val scheduler = Executors.newSingleThreadScheduledExecutor()
                            scheduler.schedule({
                                if (ChatStore.typers.value.find { it.userId == typeEvent.userId }?.expires == typeEvent.expires) {
                                    ChatStore.typers.value =
                                        ChatStore.typers.value.filter { it.userId != typeEvent.userId }
                                }
                            }, 5, java.util.concurrent.TimeUnit.SECONDS)
                        }
                    }
                }
                Log.d("TPU.Untagged", "Socket connected ${socket?.isActive}")
            } else {
                Log.d("TPU.Untagged", "Socket is null")
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun getMessageReplyIntent(associationId: Int): Intent {
        return Intent()
            .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            .setAction("reply")
            .putExtra("associationId", associationId)
    }


    fun getSocket(): Socket? {
        return socket
    }

    fun closeSocket() {
        socket?.disconnect()
        socket = null
    }
}