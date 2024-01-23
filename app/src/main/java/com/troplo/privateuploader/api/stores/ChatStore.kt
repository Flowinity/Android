package com.troplo.privateuploader.api

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.troplo.privateuploader.api.stores.AppStore
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
    val chats = mutableStateListOf<Chat>()
    var associationId = MutableStateFlow(0)
    var typers = MutableStateFlow(emptyList<Typing>())
    var jumpToMessage = MutableStateFlow(0)
    var hasInit = false
    val searchPanel = MutableStateFlow(false)

    fun initializeChats() {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val response = TpuApi.retrofitService.getChats().execute().body() ?: emptyList()
                chats.clear()
                chats.addAll(response)
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
                    chats.add(0, chat)
                }

                socket.on("removeChat") {
                    val jsonArray = it[0] as JSONObject
                    val payload = jsonArray.toString()
                    val chat = SocketHandler.gson.fromJson(payload, RemoveChatEvent::class.java)
                    val index = chats.indexOfFirst { it.id == chat.id }
                    chats.removeAt(index)
                }

                socket.on("removeChatUser") {
                    val jsonArray = it[0] as JSONObject
                    val payload = jsonArray.toString()
                    val assoc = SocketHandler.gson.fromJson(payload, RemoveChatUserEvent::class.java)
                    val chatIndex = chats.indexOfFirst { it.id == assoc.chatId }
                    if (chatIndex != -1) {
                        val chat = chats[chatIndex]
                        val userIndex = chat.users.indexOfFirst { it.id == assoc.id }
                        if (userIndex != -1) {
                            chats[chatIndex].users.toMutableList().removeAt(userIndex)
                        }
                    }
                }

                socket.on("addChatUsers") {
                    val jsonArray = it[0] as JSONObject
                    val payload = jsonArray.toString()
                    val users = SocketHandler.gson.fromJson(payload, AddChatUsersEvent::class.java)

                    val chatIndex = chats.indexOfFirst { it.id == users.chatId }
                    if (chatIndex != -1) {
                        val chat = chats[chatIndex]
                        chats[chatIndex] = chats[chatIndex].copy(users = chats[chatIndex].users.toMutableList().apply {
                            addAll(users.users)
                        })
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
        return chats.find { it.association?.id == associationId.value }
    }

    fun setAssociationId(id: Int, context: Context) {
        SessionManager(context).setLastChatId(id)
        associationId.value = id

        // Handle unread count, and init read receipt
        // Ensure that the app is in the foreground

        val socket = SocketHandler.getSocket()
        Log.d("MarkAsRead", "Foreground: ${AppStore.foreground}")
        if(AppStore.foreground) {
            socket?.emit("readChat", id)
            val chat = chats.find { it.association?.id == id }
            if (chat != null) {
                chat.unread = 0
            }
        }
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