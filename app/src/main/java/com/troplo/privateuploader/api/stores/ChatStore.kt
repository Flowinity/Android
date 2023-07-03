package com.troplo.privateuploader.api

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.troplo.privateuploader.data.model.AddChatUsersEvent
import com.troplo.privateuploader.data.model.Chat
import com.troplo.privateuploader.data.model.PinRequest
import com.troplo.privateuploader.data.model.RemoveChatEvent
import com.troplo.privateuploader.data.model.RemoveChatUserEvent
import com.troplo.privateuploader.data.model.Typing
import com.troplo.privateuploader.data.model.UploadTarget
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
    var hasInit = false
    val searchPanel = MutableStateFlow(false)

    // To upload to TPU, uses URI Android system
    var attachmentsToUpload = mutableStateListOf<UploadTarget>()

    val chats: StateFlow<List<Chat>>
        get() = _chats

    fun initializeChats() {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val response = TpuApi.retrofitService.getChats().execute().body() ?: emptyList()
                _chats.value = response
            }
            if(hasInit) return
            hasInit = true
            val socket: Socket? = SocketHandler.getSocket()
            if (socket != null) {
                socket.on("chatCreated") {
                    val jsonArray = it[0] as JSONObject
                    val payload = jsonArray.toString()
                    val chat = SocketHandler.gson.fromJson(payload, Chat::class.java)
                    // add it to the top
                    _chats.value = listOf(chat).plus(_chats.value)
                }

                socket.on("removeChat") {
                    val jsonArray = it[0] as JSONObject
                    val payload = jsonArray.toString()
                    val chat = SocketHandler.gson.fromJson(payload, RemoveChatEvent::class.java)
                    _chats.value = _chats.value.filter { it.id != chat.id }
                }

                socket.on("removeChatUser") {
                    val jsonArray = it[0] as JSONObject
                    val payload = jsonArray.toString()
                    val assoc = SocketHandler.gson.fromJson(payload, RemoveChatUserEvent::class.java)
                    val chatIndex = _chats.value.indexOfFirst { it.id == assoc.chatId }
                    if (chatIndex != -1) {
                        val chat = _chats.value[chatIndex]
                        val userIndex = chat.users.indexOfFirst { it.id == assoc.id }
                        if (userIndex != -1) {
                            _chats.value = _chats.value.toMutableList().apply {
                                this[chatIndex] = chat.copy(users = chat.users.toMutableList().apply {
                                    this.removeAt(userIndex)
                                })
                            }
                        }
                    }
                }

                socket.on("addChatUsers") {
                    val jsonArray = it[0] as JSONObject
                    val payload = jsonArray.toString()
                    val users = SocketHandler.gson.fromJson(payload, AddChatUsersEvent::class.java)

                    val chatIndex = _chats.value.indexOfFirst { it.id == users.chatId }
                    if (chatIndex != -1) {
                        val chat = _chats.value[chatIndex]
                        _chats.value = _chats.value.toMutableList().apply {
                            this[chatIndex] = chat.copy(users = chat.users.toMutableList().apply {
                                this.addAll(users.users)
                            })
                        }
                    }
                }
            } else {
                Log.d("TPU.Untagged", "Socket is null")
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

    fun pinMessage(messageId: Int, pinned: Boolean) {
        if (messageId == 0 || associationId.value == 0) return

        CoroutineScope(Dispatchers.IO).launch {
            TpuApi.retrofitService.pinMessage(associationId.value, PinRequest(
                messageId,
                pinned
            )).execute()
        }
    }
}