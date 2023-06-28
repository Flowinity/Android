package com.troplo.privateuploader.components.chat.dialogs

import android.content.Context
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
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
import com.troplo.privateuploader.components.chat.Message
import com.troplo.privateuploader.components.core.Paginate
import com.troplo.privateuploader.data.model.Message
import com.troplo.privateuploader.data.model.Pager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchDialog(open: MutableState<Boolean>) {
    val content = remember { mutableStateOf("") }
    val chats = ChatStore.chats.collectAsState()
    val chat = chats.value.find { it.association?.id == ChatStore.associationId.value }
    val searchViewModel = remember { SearchViewModel() }
    val kbController = LocalSoftwareKeyboardController.current

    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        content = {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = {
                            TextField(
                                value = content.value,
                                onValueChange = { content.value = it },
                                label = {
                                    Text("Search ${chat?.name}")
                                },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { searchViewModel.searchMessages(chat?.association?.id ?: 0, content.value, kbController = kbController) }
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .onKeyEvent {
                                        if (it.nativeKeyEvent.keyCode == 13) {
                                            searchViewModel.searchMessages(
                                                chat?.association?.id ?: 0, content.value, kbController = kbController
                                            )
                                            true
                                        } else {
                                            false
                                        }
                                    },
                                trailingIcon = {
                                    IconButton(onClick = { searchViewModel.searchMessages(chat?.association?.id ?: 0, content.value, kbController = kbController) }) {
                                        Icon(Icons.Filled.Search, contentDescription = "Search")
                                    }
                                }
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { open.value = false }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Close")
                            }
                        }
                    )
                    if(searchViewModel.messages.value != null) {
                        Text("Total results: ${searchViewModel.pager.value?.totalItems}", modifier = Modifier.padding(start = 16.dp, top = 69.dp))
                    } else {
                        Text("Search for some messages...", modifier = Modifier.padding(start = 16.dp, top = 64.dp))
                    }
                },
                bottomBar = {
                    if(searchViewModel.pager.value != null) {
                        Paginate(
                            modelValue = searchViewModel.pager.value?.currentPage ?: 0,
                            totalPages = searchViewModel.pager.value?.totalPages ?: 0,
                            onUpdateModelValue = { searchViewModel.searchMessages(chat?.association?.id ?: 0, content.value, it, kbController = kbController) },
                            modifier = Modifier.padding(bottom = 18.dp)
                        )
                    }
                }
            ) {
                if(searchViewModel.messages.value != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = it.calculateTopPadding(),
                                bottom = it.calculateBottomPadding()
                            )
                    ) {
                        searchViewModel.messages.value?.forEach { msg ->
                            item(
                                key = msg.id
                            ) {
                                Message(
                                    msg,
                                    "none",
                                    null,
                                    null,
                                    onClick = {
                                        open.value = false
                                        ChatStore.jumpToMessage.value = msg.id
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        onDismissRequest = { open.value = false }
    )
}

class SearchViewModel : ViewModel() {
    val messages = mutableStateOf<List<Message>?>(null)
    val pager = mutableStateOf<Pager?>(null)

    @OptIn(ExperimentalComposeUiApi::class)
    fun searchMessages(associationId: Int, content: String, page: Int = 1, kbController: SoftwareKeyboardController?) {
        kbController?.hide()
        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.searchMessages(chatId = associationId, query = content, page = page).execute()
            withContext(Dispatchers.Main) {
                messages.value = response.body()?.messages
                pager.value = response.body()?.pager
            }
        }
    }
}
