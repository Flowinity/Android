package com.troplo.privateuploader.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.components.chat.ChatActions
import com.troplo.privateuploader.components.chat.ChatItem
import com.troplo.privateuploader.components.chat.dialogs.NewChatDialog
import com.troplo.privateuploader.components.core.OverlappingPanelsState
import com.troplo.privateuploader.data.model.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    openChat: (Int) -> Unit = {},
    panelState: OverlappingPanelsState?,
    navController: NavController
) {
    val loading = remember { mutableStateOf(true) }
    val token = SessionManager(LocalContext.current).getAuthToken() ?: ""
    val chatViewModel = remember { ChatHomeViewModel() }
    val chatStore = ChatStore
    val chats = chatStore.chats.collectAsState()
    val createChat = remember { mutableStateOf(false) }
    val chatActions = remember { mutableStateOf(false) }
    val chat = remember { mutableStateOf<Chat?>(null) }

    LaunchedEffect(Unit) {
        chatViewModel.getChats(token).also {
            loading.value = false
        }
    }

    if(createChat.value) {
        NewChatDialog(createChat, navController)
    }

    if(chatActions.value) {
        ChatActions(chat, chatActions)
    }

    Scaffold(
        topBar = {
            ListItem(
                headlineContent = {
                    Text("Chats")
                },
                trailingContent = {
                    IconButton(onClick = { createChat.value = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Create chat")
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = it.calculateTopPadding())) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                chats.value.forEach {
                    item(
                        key = it.id
                    ) {
                        ChatItem(it, openChat, chatActions, chat)
                    }
                }
            }
        }
    }
}

class ChatHomeViewModel : ViewModel() {
    fun getChats(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ChatStore.initializeChats(token)
        }
    }
}