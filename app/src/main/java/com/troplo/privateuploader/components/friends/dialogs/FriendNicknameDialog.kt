package com.troplo.privateuploader.components.friends.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.FriendStore
import com.troplo.privateuploader.components.core.LoadingButton
import com.troplo.privateuploader.data.model.FriendNicknameRequest
import com.troplo.privateuploader.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendNicknameDialog(
    open: MutableState<Boolean>,
    user: User?
) {
    if(user == null) return
    val viewModel = remember { FriendNicknameViewModel() }
    val name = remember { mutableStateOf(
        FriendStore.friends.value.find { it.id == user.id }?.otherUser?.nickname?.nickname ?: ""
    ) }

    AlertDialog(
        onDismissRequest = {
            open.value = false
        },
        title = {
            Text(text = "Set a friend nickname for ${user.username}")
        },
        text = {
               OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Nickname") })
        },
        confirmButton = {
            LoadingButton(
                text = "Set",
                loading = viewModel.loading.value,
                onClick = {
                    viewModel.setFriendNickname(name.value, user.id, open)
                },
                enabled = name.value.isNotEmpty(),
                type = "text"
            )
        },
        dismissButton = {
            TextButton(
                onClick = {
                    open.value = false
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

class FriendNicknameViewModel: ViewModel() {
    val loading = mutableStateOf(false)

    fun setFriendNickname(name: String, userId: Int, open: MutableState<Boolean>) {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.updateNickname(userId, FriendNicknameRequest(name)).execute()
            if (response.isSuccessful) {
                FriendStore.updateFriendNickname(name, userId)
                open.value = false
            }
            loading.value = false
        }
    }
}
