package com.troplo.privateuploader.screens

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.UserHandler
import com.troplo.privateuploader.components.chat.Message
import com.troplo.privateuploader.components.core.NavRoute
import com.troplo.privateuploader.data.model.ChatAssociation
import com.troplo.privateuploader.data.model.Embed
import com.troplo.privateuploader.data.model.EmbedResolutionEvent
import com.troplo.privateuploader.data.model.Message
import com.troplo.privateuploader.data.model.Message as MessageModel
import com.troplo.privateuploader.data.model.MessageEvent
import com.troplo.privateuploader.data.model.MessageRequest
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    associationId: Int
) {
    val loading = remember { mutableStateOf(true) }
    val token = SessionManager(LocalContext.current).fetchAuthToken() ?: ""
    val chatViewModel = remember { ChatViewModel() }
    val messages = remember { mutableStateOf(chatViewModel.messages) }
    val message = remember { mutableStateOf("") }
    val context = LocalContext.current
    val listState = rememberLazyListState()
    chatViewModel.associationId = associationId
    LaunchedEffect(messages.value.value?.size) {
        listState.animateScrollToItem(0)
    }

    LaunchedEffect(Unit) {
        chatViewModel.getMessages(token, associationId).also {
            loading.value = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            reverseLayout = true,
            state = listState
        ) {
            messages.value.value?.forEach {
                item(
                    key = it.id
                ) {
                    Message(it, compact(it, messages.value.value!!))
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier.weight(1f),
            ) {
                OutlinedTextField(
                    value = message.value,
                    onValueChange = { message.value = it },
                    label = { Text("Message") },
                    placeholder = { Text("Keep it civil") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(top = 16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                IconButton(
                    onClick = {
                        chatViewModel.sendMessage(token, associationId, message.value, context)
                        message.value = ""
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send message"
                    )
                }
            }
        }
    }
}

class ChatViewModel : ViewModel() {
    // listen for messages
    private val socket = SocketHandler.getSocket()
    var associationId = 0
    val messages = mutableStateOf<List<Message>?>(null)

    init {
        println("Route Param: ${NavRoute.Chat}}")
        val gson = Gson()
        val chatId = ChatStore.chats.value.find { it.association?.id == associationId }?.id
        socket?.on("message") {
            println("Message received" + it[0])

            val jsonArray = it[0] as JSONObject
            val payload = jsonArray.toString()
            val messageEvent = gson.fromJson(payload, MessageEvent::class.java)

            val message = messageEvent.message

            if(associationId != messageEvent.association.id) {
                println("Message not for this association, ${messageEvent.association.id} != $associationId")
                return@on
            }
            // see if the message is already in the list
            val existingMessage = messages.value?.find { e -> e.id == message.id }
            println("Message for this association, ${messageEvent.association.id} == $associationId, $existingMessage")
            if(existingMessage == null) {
                // add to start of list
                messages.value = listOf(message, *messages.value.orEmpty().toTypedArray())
            }
        }

        socket?.on("embedResolution") {
            println("Embed resolution received " + it[0])

            val jsonArray = it[0] as JSONObject
            val payload = jsonArray.toString()
            val embed = gson.fromJson(payload, EmbedResolutionEvent::class.java)
            val index = messages.value.orEmpty().indexOfFirst { e -> e.id == embed.id }
            if(index != -1) {
                // update the message's embeds
                messages.value = messages.value.orEmpty().toMutableList().also {
                    it[index] = it[index].copy(embeds = embed.embeds)
                }
            }
        }
    }

    fun getMessages(token: String, associationId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.getMessages(token, associationId).execute()
            withContext(Dispatchers.Main) {
                messages.value = response.body()
            }
        }
    }

    fun sendMessage(token: String, associationId: Int, message: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = UserHandler.getUser()
            if(user != null) {
                // add it to the list as pending
                val id = Date().time.toInt()
                val pendingMessage = MessageModel(
                    id,
                    chatId = associationId,
                    userId = user.id,
                    content = message,
                    createdAt = Date(),
                    updatedAt = Date(),
                    user = user,
                    pending = true,
                    edited = false,
                    editedAt = null,
                    embeds = emptyList<Embed>(),
                    error = false,
                    legacyUser = null,
                    legacyUserId = null,
                    pinned = false,
                    readReceipts = emptyList<ChatAssociation>(),
                    reply = null,
                    replyId = null,
                    tpuUser = user,
                    type = "message"
                )
                messages.value = listOf(pendingMessage, *messages.value.orEmpty().toTypedArray())
                try {
                    val response = TpuApi.retrofitService.sendMessage(
                        token, associationId, MessageRequest(
                            message
                        )
                    ).execute()
                    launch(Dispatchers.Main) {
                        if (!response.isSuccessful) {
                            val error: JSONObject =
                                JSONObject(response.errorBody()?.string() ?: "{}")
                            Toast.makeText(
                                context,
                                error.getJSONArray("errors").getJSONObject(0).getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                            val oldMessageIndex = messages.value?.indexOfFirst { it.id == id }
                            if (oldMessageIndex != -1 && oldMessageIndex != null) {
                                messages.value = messages.value?.toMutableList()?.apply {
                                    set(
                                        oldMessageIndex,
                                        pendingMessage.copy(error = true, pending = false)
                                    )
                                }
                            }
                        } else {
                            // update the message object
                            val messageResponse = response.body()!!
                            val oldMessageIndex = messages.value?.indexOfFirst { it.id == id }
                            if (oldMessageIndex != -1 && oldMessageIndex != null) {
                                messages.value = messages.value?.toMutableList()?.apply {
                                    set(
                                        oldMessageIndex,
                                        messageResponse
                                    )
                                }
                            }
                        }
                    }
                } catch (e: HttpException) {
                    val oldMessageIndex = messages.value?.indexOfFirst { it.id == id }
                    if (oldMessageIndex != -1 && oldMessageIndex != null) {
                        messages.value = messages.value?.toMutableList()?.apply {
                            set(
                                oldMessageIndex,
                                pendingMessage.copy(error = true, pending = false)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    PrivateUploaderTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ChatScreen(1)
        }
    }
}

// returns "compact", "separator", or "none"
private fun compact(message: Message, messages: List<Message>): String {
    val index = messages.indexOf(message)
    if(index == messages.size - 1) return "none"
    val previousMessage = messages[index + 1]
    val fmt = SimpleDateFormat("yyyyMMdd")
    return if(fmt.format(message.createdAt) != fmt.format(previousMessage.createdAt)) {
        "separator"
    } else if (message.userId == previousMessage.userId) {
        "compact"
    } else {
        "none"
    }
}