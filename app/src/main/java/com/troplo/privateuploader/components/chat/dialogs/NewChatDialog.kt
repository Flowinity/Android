package com.troplo.privateuploader.components.chat.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.FriendStore
import com.troplo.privateuploader.components.chat.Message
import com.troplo.privateuploader.components.core.InteractionDialog
import com.troplo.privateuploader.components.core.Paginate
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.data.model.ChatCreateRequest
import com.troplo.privateuploader.data.model.Message
import com.troplo.privateuploader.data.model.Pager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NewChatDialog(open: MutableState<Boolean>, navController: NavController) {
    val friends = FriendStore.friends.collectAsState()
    val chatViewModel = remember { NewChatViewModel() }
    val selected = remember { mutableStateListOf<Int>() }

    InteractionDialog(
        button = {
            Button(
                onClick = {
                    chatViewModel.createChat(selected, navController)
                    open.value = false
                },
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = selected.isNotEmpty()
            ) {
                Text("Create ${
                    if (selected.size <= 1) {
                        "DM"
                    } else {
                        "Group"
                    }
                }")
            }
        },
        header = {
            TopAppBar(
                title = {
                    Text("Create a new chat")
                },
                navigationIcon = {
                    IconButton(onClick = { open.value = false }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Close")
                    }
                }
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                friends.value.forEach {
                    item(
                        key = it.id,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                .clickable {
                                    if (selected.contains(it.otherUser?.id ?: 0)) {
                                        selected.remove(it.otherUser?.id ?: 0)
                                    } else {
                                        selected.add(it.otherUser?.id ?: 0)
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UserAvatar(
                                avatar = it.otherUser?.avatar,
                                username = it.otherUser?.username ?: "Deleted User"
                            )
                            Text(
                                text = it.otherUser?.username ?: "Deleted User",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            )
                            Checkbox(
                                checked = selected.contains(it.otherUser?.id ?: 0),
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selected.remove(it.otherUser?.id ?: 0)
                                    } else {
                                        selected.add(it.otherUser?.id ?: 0)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        open = open
    )
}

class NewChatViewModel : ViewModel() {
    val loading = mutableStateOf(false)

    fun createChat(members: List<Int>, navController: NavController) {
        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            val response = TpuApi.retrofitService.createChat(ChatCreateRequest(
                users = members
            )).execute()
            withContext(Dispatchers.Main) {
                loading.value = false
                if(response.isSuccessful) {
                    navController.navigate("chat/${response.body()?.association?.id}")
                }
            }
        }
    }
}
