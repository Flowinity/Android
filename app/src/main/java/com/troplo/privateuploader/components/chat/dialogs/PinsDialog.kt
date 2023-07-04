package com.troplo.privateuploader.components.chat.dialogs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.components.chat.Message
import com.troplo.privateuploader.components.core.Paginate
import com.troplo.privateuploader.data.model.Message
import com.troplo.privateuploader.data.model.Pager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PinsDialog(pins: MutableState<Boolean>) {
    val content = remember { mutableStateOf("") }
    val chats = ChatStore.chats.collectAsState()
    val chat = chats.value.find { it.association?.id == ChatStore.associationId.value }
    val viewModel = remember { PinsViewModel() }

    LaunchedEffect(Unit) {
        viewModel.getPins(chat?.association?.id ?: 0)
    }

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        content = {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            Text("Pins for ${TpuFunctions.getChatName(chat)}")
                        },
                        navigationIcon = {
                            IconButton(onClick = { pins.value = false }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Close")
                            }
                        }
                    )
                    if (viewModel.messages.value != null) {
                        Text(
                            "Total results: ${viewModel.pager.value?.totalItems}",
                            modifier = Modifier.padding(start = 16.dp, top = 69.dp)
                        )
                    } else {
                        Text(
                            "There are no pins yet.",
                            modifier = Modifier.padding(start = 16.dp, top = 64.dp)
                        )
                    }
                },
                bottomBar = {
                    if (viewModel.pager.value != null) {
                        Paginate(
                            modelValue = viewModel.pager.value?.currentPage ?: 0,
                            totalPages = viewModel.pager.value?.totalPages ?: 0,
                            onUpdateModelValue = {
                                viewModel.getPins(
                                    chat?.association?.id ?: 0,
                                    it
                                )
                            },
                            modifier = Modifier.padding(bottom = 18.dp)
                        )
                    }
                }
            ) {
                if (viewModel.messages.value != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = it.calculateTopPadding(),
                                bottom = it.calculateBottomPadding()
                            )
                    ) {
                        viewModel.messages.value?.forEach { msg ->
                            item(
                                key = msg.id
                            ) {
                                Message(
                                    message = msg,
                                    compact = "none",
                                    onReply = null,
                                    onClick = {
                                        pins.value = false
                                        ChatStore.jumpToMessage.value = msg.id
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        onDismissRequest = { pins.value = false }
    )
}

class PinsViewModel : ViewModel() {
    val messages = mutableStateOf<List<Message>?>(null)
    val pager = mutableStateOf<Pager?>(null)

    fun getPins(
        associationId: Int,
        page: Int = 1
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.getMessagesPaginate(
                id = associationId,
                mode = "paginate",
                type = "pins",
                page = page
            ).execute()
            withContext(Dispatchers.Main) {
                messages.value = response.body()?.messages
                pager.value = response.body()?.pager
            }
        }
    }
}
