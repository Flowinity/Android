package com.troplo.privateuploader.api

import android.content.Context
import com.troplo.privateuploader.data.model.Chat
import com.troplo.privateuploader.data.model.User
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URI
import java.net.URISyntaxException
import java.util.Collections


object ChatStore {
    private val _chats: MutableStateFlow<List<Chat>> = MutableStateFlow(emptyList())
    private var associationId = 0

    val chats: StateFlow<List<Chat>>
        get() = _chats

    fun initializeChats(token: String) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val response = TpuApi.retrofitService.getChats(token).execute().body() ?: emptyList()
                _chats.value = response
                println("Chats: $response")
            }

            val socket: Socket? = SocketHandler.getSocket()
            if (socket != null) {
                socket.on("chatCreated") {
                    println("Socket $it")
                }
            } else {
                println("Socket is null")
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun getChat(): Chat? {
        return chats.value.find { it.association?.id == associationId }
    }

    fun setAssociationId(id: Int) {
        associationId = id
    }

    fun setChats(chats: List<Chat>) {
        _chats.value = chats
    }
}