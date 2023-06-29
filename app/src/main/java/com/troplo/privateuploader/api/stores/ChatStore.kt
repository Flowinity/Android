package com.troplo.privateuploader.api

import android.content.Context
import com.troplo.privateuploader.data.model.Chat
import com.troplo.privateuploader.data.model.SettingsPayload
import com.troplo.privateuploader.data.model.Typing
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException


object ChatStore {
    private val _chats: MutableStateFlow<List<Chat>> = MutableStateFlow(emptyList())
    var associationId = MutableStateFlow(0)
    var typers = MutableStateFlow(emptyList<Typing>())
    var jumpToMessage = MutableStateFlow(0)

    val chats: StateFlow<List<Chat>>
        get() = _chats

    fun initializeChats(token: String) {
        try {
            if (_chats.value.isNotEmpty()) return
            CoroutineScope(Dispatchers.IO).launch {
                val response = TpuApi.retrofitService.getChats().execute().body() ?: emptyList()
                _chats.value = response
            }

            val socket: Socket? = SocketHandler.getSocket()
            if (socket != null) {
                socket.on("chatCreated") {
                    val jsonArray = it[0] as JSONObject
                    val payload = jsonArray.toString()
                    val chat = SocketHandler.gson.fromJson(payload, Chat::class.java)
                    // add it to the top
                    _chats.value = listOf(chat).plus(_chats.value)
                }
            } else {
                println("Socket is null")
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun getChat(): Chat? {
        return chats.value.find { it.association?.id == associationId.value }
    }

    fun setAssociationId(id: Int, context: Context) {
        SessionManager(context).setLastChatId(id)
        associationId.value = id

        // Handle unread count, and init read receipt
        val socket = SocketHandler.getSocket()
        socket?.emit("readChat", id)
        val chat = chats.value.find { it.association?.id == id }
        if (chat != null) {
            chat.unread = 0
        }
    }

    fun setChats(chats: List<Chat>) {
        _chats.value = chats
    }

    fun deleteMessage(messageId: Int) {
        if (messageId == 0 || associationId.value == 0) return

        CoroutineScope(Dispatchers.IO).launch {
            TpuApi.retrofitService.deleteMessage(associationId.value, messageId).execute()
        }
    }
}