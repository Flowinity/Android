package com.troplo.privateuploader.screens

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.chat.Message
import com.troplo.privateuploader.components.chat.MessageActions
import com.troplo.privateuploader.components.core.NavRoute
import com.troplo.privateuploader.components.core.OverlappingPanelsState
import com.troplo.privateuploader.data.model.ChatAssociation
import com.troplo.privateuploader.data.model.DeleteEvent
import com.troplo.privateuploader.data.model.EditEvent
import com.troplo.privateuploader.data.model.EditRequest
import com.troplo.privateuploader.data.model.Embed
import com.troplo.privateuploader.data.model.EmbedFail
import com.troplo.privateuploader.data.model.EmbedResolutionEvent
import com.troplo.privateuploader.data.model.Message
import com.troplo.privateuploader.data.model.MessageEvent
import com.troplo.privateuploader.data.model.MessageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.util.Date
import com.troplo.privateuploader.data.model.Message as MessageModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    chatId: Int?,
    panelsState: OverlappingPanelsState,
) {
    var associationId = chatId
    val loading = remember { mutableStateOf(true) }
    val context = LocalContext.current
    val token = SessionManager(context).getAuthToken() ?: ""
    val chatViewModel = remember { ChatViewModel() }
    val messages = remember { mutableStateOf(chatViewModel.messages) }
    val message = remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var initialLoad = remember { mutableStateOf(false) }
    val messageCtx = remember { mutableStateOf(false) }
    val messageCtxMessage: MutableState<Message?> = remember { mutableStateOf(null) }
    val editId = remember { mutableIntStateOf(0) }
    // track ChatStore.chats.chat.typers
    val typers = remember { mutableStateOf(ChatStore.getChat()?.typers) }

    if (associationId == 0 || associationId == null) {
        val lastChatId = SessionManager(context).getLastChatId()
        associationId = lastChatId
        initialLoad.value = true
    }
    if (associationId == 0) return
    ChatStore.setAssociationId(associationId, context)
    chatViewModel.associationId = associationId
    LaunchedEffect(messages.value.value?.size) {
        listState.animateScrollToItem(0)
    }

    LaunchedEffect(Unit) {
        chatViewModel.getMessages(token, associationId).also {
            loading.value = false
        }
    }

    Scaffold(
        bottomBar = {
            if (editId.value != 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Editing message",
                            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                        )

                        IconButton(
                            onClick = {
                                editId.value = 0
                                message.value = ""
                            },
                            modifier = Modifier.padding(end = 16.dp, bottom = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Stop editing",
                                modifier = Modifier.padding(end = 16.dp, bottom = 4.dp)
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    val focusRequester = FocusRequester()
                    val keyboardController = LocalSoftwareKeyboardController.current

                    OutlinedTextField(
                        value = message.value,
                        onValueChange = { message.value = it },
                        label = { Text("Message") },
                        placeholder = { Text("Keep it civil") },
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(8.dp)
                          .padding(top = 16.dp)
                          .focusRequester(focusRequester),
                        supportingText = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val typersState = ChatStore.typers.collectAsState()
                                typersState.value.let { typers ->
                                    if (typers.isNotEmpty()) {
                                        val names =
                                            typers.filter { it.chatId == ChatStore.getChat()?.id }
                                                .map { it.user.username }
                                        Text(
                                            text = "${names.joinToString(", ")} is typing..."
                                        )
                                    } else {
                                        Text(
                                            text = ""
                                        )
                                    }
                                }
                                Text("${message.value.length}/4000")
                            }
                        },
                        keyboardActions = KeyboardActions(
                            onAny = {
                                chatViewModel.typing(associationId)
                            }
                        ),
                    )

                    // hide keyboard when sidebar is open
                    // TODO: fix
                  /*  if (panelsState.isStartPanelOpen || panelsState.isEndPanelOpen) {
                        keyboardController?.hide()
                    }*/

                    // Autofocus the input on mount
                    if (!initialLoad.value) {
                        DisposableEffect(Unit) {
                            focusRequester.requestFocus()
                            onDispose { }
                        }
                        initialLoad.value = true
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(top = 32.dp, end = 8.dp)
                ) {
                    IconButton(
                        onClick = {
                            chatViewModel.sendMessage(
                                token,
                                associationId,
                                message.value,
                                context,
                                editId.value
                            )
                            message.value = ""
                            editId.value = 0
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Send message"
                        )
                    }
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(bottom = it.calculateBottomPadding())
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                reverseLayout = true,
                state = listState
            ) {
                messages.value.value?.forEach { msg ->
                    item(
                        key = msg.id
                    ) {
                        Message(
                            msg,
                            compact(msg, messages.value.value!!),
                            messageCtx,
                            messageCtxMessage
                        )
                    }
                }
            }
        }
    }

    if (messageCtx.value) {
        MessageActions(messageCtxMessage, messageCtx, editId, message)
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
        socket?.on("message") {
            println("Message received" + it[0])

            val jsonArray = it[0] as JSONObject
            val payload = jsonArray.toString()
            val messageEvent = gson.fromJson(payload, MessageEvent::class.java)

            val message = messageEvent.message

            if (associationId != messageEvent.association.id) {
                println("Message not for this association, ${messageEvent.association.id} != $associationId")
                return@on
            }
            // see if the message is already in the list
            val existingMessage =
                messages.value?.find { e -> e.id == message.id || (e.content == message.content && e.pending == true && e.userId == message.userId) }
            println("Message for this association, ${messageEvent.association.id} == $associationId, $existingMessage")
            if (existingMessage == null) {
                // add to start of list
                messages.value = listOf(message, *messages.value.orEmpty().toTypedArray())
            } else {
                val index = messages.value.orEmpty().indexOf(existingMessage)
                val newMessage = message.copy(
                    pending = false,
                    error = false,
                    content = message.content,
                    createdAt = message.createdAt
                )
                messages.value = messages.value.orEmpty().toMutableList().also { msg ->
                    msg[index] = newMessage
                }
            }
        }

        var embedFails: Array<EmbedFail> = arrayOf()

        fun embedResolution(data: Array<Any>) {
            val chatId = ChatStore.chats.value.find { it.association?.id == associationId }?.id
            val jsonArray = data[0] as JSONObject
            val payload = jsonArray.toString()
            val embed = gson.fromJson(payload, EmbedResolutionEvent::class.java)
            if (chatId != embed.chatId) {
                println("Embed not for this chat, ${embed.chatId} != $chatId")
                return
            }


            val index = messages.value.orEmpty().indexOfFirst { e -> e.id == embed.id }
            if (index != -1) {
                // update the message embeds with copy
                val message = messages.value.orEmpty()[index]
                val newMessage = message.copy(embeds = embed.embeds)
                messages.value = messages.value.orEmpty().toMutableList().also {
                    it[index] = newMessage
                }
            } else {
                // find in embedFails
                val index = embedFails.indexOfFirst { e -> e.data.id == embed.id }
                println("Embed not found in messages, ${embed.id}, $index")
                if (index == -1) {
                    // add to embedFails
                    embedFails += EmbedFail(
                        retries = 1,
                        data = embed
                    )
                } else {
                    val count = embedFails[index].retries
                    if (count !== null && count > 5) {
                        embedFails = embedFails.filterIndexed { i, _ -> i != index }.toTypedArray()
                        return
                    }
                    // update the count
                    embedFails[index] = EmbedFail(
                        retries = embedFails[index].retries + 1,
                        data = embedFails[index].data
                    )
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    embedResolution(data)
                }, 50)
            }
        }

        socket?.on("embedResolution") { it ->
            println("Embed resolution received " + it[0])
            embedResolution(it)
        }

        socket?.on("messageDelete") { it ->
            println("Message delete received " + it[0])
            val jsonArray = it[0] as JSONObject
            val payload = jsonArray.toString()
            val message = gson.fromJson(payload, DeleteEvent::class.java)

            val chatId = ChatStore.getChat()?.id
            if (chatId != message.chatId) {
                println("Message delete not for this chat, ${message.chatId} != $chatId")
                return@on
            }

            val index = messages.value.orEmpty().indexOfFirst { e -> e.id == message.id }
            if (index != -1) {
                // remove the message
                messages.value = messages.value.orEmpty().toMutableList().also {
                    it.removeAt(index)
                }
            }
        }

        socket?.on("edit") { it ->
            println("Message edit received " + it[0])
            val jsonArray = it[0] as JSONObject
            val payload = jsonArray.toString()
            val editEvent = gson.fromJson(payload, EditEvent::class.java)

            val chatId = ChatStore.getChat()?.id
            if (chatId != editEvent.chatId) {
                println("Message edit not for this chat, ${editEvent.chatId} != $chatId")
                return@on
            }

            val index = messages.value.orEmpty().indexOfFirst { e -> e.id == editEvent.id }
            if (index != -1) {
                // update the message
                messages.value = messages.value.orEmpty().toMutableList().also {
                    it[index] = it[index].copy(
                        content = editEvent.content,
                        edited = editEvent.edited,
                        editedAt = editEvent.editedAt
                    )
                }
            }
        }
    }

    fun getMessages(token: String, associationId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.getMessages(associationId).execute()
            withContext(Dispatchers.Main) {
                messages.value = response.body()
            }
        }
    }

    fun sendMessage(
        token: String,
        associationId: Int,
        message: String,
        context: Context,
        editId: Int = 0,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = UserStore.getUser()
            if (user != null) {

                if (editId == 0) {
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
                    messages.value =
                        listOf(pendingMessage, *messages.value.orEmpty().toTypedArray())
                    try {
                        val response = TpuApi.retrofitService.sendMessage(
                            associationId, MessageRequest(
                                message
                            )
                        ).execute()
                        launch(Dispatchers.IO) {
                            if (!response.isSuccessful) {
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
                } else {
                    // User has edited a message
                    try {
                        val response = TpuApi.retrofitService.editMessage(
                            associationId, EditRequest(
                                message,
                                editId
                            )
                        ).execute()
                        launch(Dispatchers.IO) {
                            if (!response.isSuccessful) {
                                val error: JSONObject =
                                    JSONObject(response.errorBody()?.string() ?: "{}")
                                Toast.makeText(
                                    context,
                                    error.getJSONArray("errors").getJSONObject(0)
                                        .getString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: HttpException) {
                        //
                    }
                }
            }
        }
    }

    fun typing(associationId: Int) {
        socket?.emit("typing", associationId)
    }
}

// returns "compact", "separator", or "none"
private fun compact(message: Message, messages: List<Message>): String {
    val index = messages.indexOf(message)
    if (index == messages.size - 1) return "none"
    val previousMessage = messages[index + 1]
    val fmt = SimpleDateFormat("yyyyMMdd")
    return if (fmt.format(message.createdAt) != fmt.format(previousMessage.createdAt)) {
        "separator"
    } else if (message.userId == previousMessage.userId && Date(message.createdAt.toString()).time - Date(
            previousMessage.createdAt.toString()
        ).time < 7 * 60 * 1000
    ) {
        "compact"
    } else {
        "none"
    }
}