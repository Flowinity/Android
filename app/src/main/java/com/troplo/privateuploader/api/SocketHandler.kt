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
import io.socket.client.Manager
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URI
import java.net.URISyntaxException
import java.util.Collections
import java.util.concurrent.Executors


object SocketHandler {
    var baseUrl = BuildConfig.SERVER_URL

    private var chatSocket: Socket? = null
    private var gallerySocket: Socket? = null

    private var manager: Manager? = null
    val gson = Gson()
    var connected = mutableStateOf(false)

    fun initializeSocket(token: String, context: Context, platform: String = "android_kotlin") {
        try {
            val options = IO.Options()
            options.forceNew = true
            options.reconnection = true
            Log.d("TPU.SocketToken", token)
            options.auth = Collections.singletonMap("token", token)
            options.query = "platform=$platform&version=4"
            options.transports = arrayOf("websocket")
            options.reconnectionDelay = 1000
            options.reconnectionDelayMax = 5000
            options.reconnectionAttempts = 9999
            options.path = "/gateway"
            manager = Manager(URI(baseUrl), options)
            chatSocket = manager!!.socket("/chat", options)
            gallerySocket = manager!!.socket("/gallery", options)
            if(gallerySocket != null) {
                gallerySocket?.open()
            }
            if (chatSocket != null) {
                chatSocket?.open()
                if (platform !== "android_kotlin_background_service") {
                    chatSocket?.on(Socket.EVENT_CONNECT) {
                        this.connected.value = true
                        Log.d(
                            "TPU.Untagged",
                            "Socket connected ${chatSocket?.isActive}, Connected: ${this.connected.value}"
                        )
                    }
                    chatSocket?.on(Socket.EVENT_DISCONNECT) {
                        this.connected.value = false
                        Log.d(
                            "TPU.Untagged",
                            "Socket disconnected ${chatSocket?.isActive}, Connected: ${this.connected.value}, Error: ${it[0]}"
                        )
                    }
                    chatSocket?.on(Socket.EVENT_CONNECT_ERROR) {
                        try {
                            this.connected.value = false
                            Log.d(
                                "TPU.Untagged",
                                "Socket connect error ${chatSocket?.isActive}, Connected: ${this.connected.value}, Error: ${it[0]}, URL: ${baseUrl}/gateway"
                            )
                        } catch (e: Exception) {
                            //
                        }
                    }
                    chatSocket?.on("message") { it ->
                        val jsonArray = it[0] as JSONObject
                        val payload = jsonArray.toString()
                        val messageEvent = gson.fromJson(payload, MessageEvent::class.java)

                        // Change in v4 uses uppercase type
                        val message = messageEvent.message.copy(
                            type = messageEvent.message.type?.lowercase()
                        )

                        Log.d("TPU.Untagged", "Message received (SocketHandler): $message")
                        val chat =
                            ChatStore.chats.find { it.association?.id?.toInt() == messageEvent.associationId.toInt() }
                        val unread = chat?.unread ?: 0
                        Log.d("MessageStore", "${messageEvent.associationId}, ${ChatStore.associationId.value}")
                        Log.d("MessageStore", "Chat: $chat")
                        if (messageEvent.associationId != ChatStore.associationId.value) {
                            // increase unread count
                            Log.d("MessageStore", "Increasing unread count: ${chat?.unread?.plus(1)}")
                            unread.plus(1)
                        }
                        Log.d("MessageStore", "Unread; $unread, Chat: $chat")
                        if(chat != null) {
                            val index = ChatStore.chats.indexOfFirst { it.id == chat.id }
                            ChatStore.chats.removeAt(index)
                            ChatStore.chats.add(0, chat.copy(unread = unread))
                        }
                    }
                    chatSocket?.on("typing") { it ->
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
                Log.d("TPU.Untagged", "Socket connected ${chatSocket?.isActive}")
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
        return chatSocket
    }

    fun getGallerySocket(): Socket? {
        return gallerySocket
    }

    fun closeSocket() {
        chatSocket?.disconnect()
        chatSocket = null
        gallerySocket?.disconnect()
        gallerySocket = null
    }
}