package com.troplo.privateuploader.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.components.chat.ChatItem
import com.troplo.privateuploader.components.core.OverlappingPanelsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    openChat: (Int) -> Unit = {},
    panelState: OverlappingPanelsState?,
) {
    val loading = remember { mutableStateOf(true) }
    val token = SessionManager(LocalContext.current).getAuthToken() ?: ""
    val chatViewModel = remember { ChatHomeViewModel() }
    val chatStore = ChatStore
    val chats = chatStore.chats.collectAsState()
    LaunchedEffect(Unit) {
        chatViewModel.getChats(token).also {
            loading.value = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            chats.value.forEach {
                item(
                    key = it.id
                ) {
                    ChatItem(it, openChat)
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