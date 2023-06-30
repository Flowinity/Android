package com.troplo.privateuploader.components.friends.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.FriendStore
import com.troplo.privateuploader.components.core.InteractionDialog
import com.troplo.privateuploader.components.core.LoadingButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendDialog(open: MutableState<Boolean>) {
    val input = remember { mutableStateOf("") }
    val viewModel = remember { AddFriendViewModel() }

    InteractionDialog(
        header = {
            TopAppBar(
                title = {
                    Text("Add a new friend")
                },
                navigationIcon = {
                    IconButton(onClick = { open.value = false }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Close")
                    }
                }
            )
        },
        button = {
            LoadingButton(
                onClick = {
                    viewModel.addFriend(input.value)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = input.value.isNotEmpty() || !viewModel.loading.value,
                text = "Send friend request",
                loading = viewModel.loading.value
            )
        },
        open = open,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    label = {
                        Text("Username")
                    },
                    onValueChange = { input.value = it },
                    value = input.value
                )
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally),
                    text = if (viewModel.loading.value) {
                        "Loading..."
                    } else if (viewModel.success.value == true) {
                        "Friend request sent!"
                    } else if (viewModel.success.value == false) {
                        "User not found."
                    } else {
                        "Enter your friend's username to add them."
                    },
                    color = when (viewModel.success.value) {
                        false -> {
                            Color.Red
                        }

                        true -> {
                            Color.Green
                        }

                        else -> {
                            Color.Unspecified
                        }
                    }
                )
            }
        }
    )
}

class AddFriendViewModel : ViewModel() {
    val loading = mutableStateOf(false)
    val success = mutableStateOf<Boolean?>(null)

    fun addFriend(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            val response = TpuApi.retrofitService.addFriend(username, "send").execute()
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    loading.value = false
                    success.value = true
                    FriendStore.initializeFriends()
                }
            } else {
                withContext(Dispatchers.Main) {
                    loading.value = false
                    success.value = false
                }
            }
        }
    }
}


@Composable
@Preview
fun AddFriendDialogPreview() {
    val open = remember { mutableStateOf(true) }
    AddFriendDialog(open)
}