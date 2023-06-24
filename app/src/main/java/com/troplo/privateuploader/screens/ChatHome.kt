package com.troplo.privateuploader.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.components.chat.ChatItem
import com.troplo.privateuploader.data.model.Chat
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    openChat: (Int) -> Unit = {},
) {
    val loading = remember { mutableStateOf(true) }
    val token = SessionManager(LocalContext.current).fetchAuthToken() ?: ""
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


@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    PrivateUploaderTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen()
        }
    }
}