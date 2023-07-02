package com.troplo.privateuploader.components.settings.dialogs

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.core.InteractionDialog
import com.troplo.privateuploader.components.core.LoadingButton
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.components.user.UserBanner
import com.troplo.privateuploader.data.model.PatchUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun StatusDialog(open: MutableState<Boolean> = mutableStateOf(true)) {
    val windowInsets = WindowInsets(0)
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val user = UserStore.user.collectAsState()
    val viewModel = remember { StatusViewModel() }

    ModalBottomSheet(
        onDismissRequest = { open.value = false },
        sheetState = bottomSheetState,
        windowInsets = windowInsets,
        dragHandle = { },
    ) {
        ListItem(
            modifier = Modifier.clickable {
                viewModel.updateStatus("online", open)
            },
            headlineContent =  {
                Row {
                    UserAvatar(avatar = user.value?.avatar, username = user.value?.username ?: "Deleted User", fakeStatus = "online")
                    Column(
                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp)
                    ) {
                        Text(text = "Online")
                        Text(text = "You will receive all notifications unless changed for each individual chat.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        )
        Divider()
        ListItem(
            modifier = Modifier.clickable {
                viewModel.updateStatus("busy", open)
            },
            headlineContent =  {
                Row {
                    UserAvatar(avatar = user.value?.avatar, username = user.value?.username ?: "Deleted User", fakeStatus = "busy")
                    Column(
                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp)
                    ) {
                        Text(text = "Do not Disturb")
                        Text(text = "You will not receive any notifications.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        )
        Divider()
        ListItem(
            modifier = Modifier.clickable {
                viewModel.updateStatus("idle", open)
            },
            headlineContent =  {
                Row {
                    UserAvatar(avatar = user.value?.avatar, username = user.value?.username ?: "Deleted User", fakeStatus = "idle")
                    Column(
                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp)
                    ) {
                        Text(text = "Idle")
                        Text(text = "You will appear idle.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        )
        Divider()
        ListItem(
            modifier = Modifier.clickable {
                viewModel.updateStatus("invisible", open)
            },
            headlineContent =  {
                Row {
                    UserAvatar(avatar = user.value?.avatar, username = user.value?.username ?: "Deleted User", fakeStatus = "offline")
                    Column(
                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp)
                    ) {
                        Text(text = "Invisible")
                        Text(text = "You will appear offline, read receipts, and the typing indicator will be disabled.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

class StatusViewModel : ViewModel() {
    val loading = mutableStateOf(false)

    fun updateStatus(
        status: String,
        open: MutableState<Boolean>
    ) {
        val patchUser = PatchUser(
            storedStatus = status
        )

        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            val response = TpuApi.retrofitService.updateUser(patchUser).execute()
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    open.value = false
                }
            } else {
                withContext(Dispatchers.Main) {
                    loading.value = false
                }
            }
        }
    }
}