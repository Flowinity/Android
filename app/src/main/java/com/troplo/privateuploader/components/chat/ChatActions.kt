package com.troplo.privateuploader.components.chat

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.components.core.dialogs.DeleteConfirmDialog
import com.troplo.privateuploader.data.model.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatActions(
    chat: MutableState<Chat?>,
    openBottomSheet: MutableState<Boolean>,
) {
    val windowInsets = WindowInsets(0)
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val viewModel = remember { ChatActionsViewModel() }
    val leaveChat = remember { mutableStateOf(false) }

    if (leaveChat.value) {
        DeleteConfirmDialog(open = leaveChat, onConfirm = {
            viewModel.leaveChat(chat.value?.association?.id ?: 0)
        }, title = "chat", name = chat.value?.name, terminology = "Leave")
    }

    ModalBottomSheet(
        onDismissRequest = { openBottomSheet.value = false },
        sheetState = bottomSheetState,
        windowInsets = windowInsets
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (chat.value?.type == "group" && (chat.value?.association?.rank == "admin" || chat.value?.association?.rank == "owner")) {
                val context = LocalContext.current
                ListItem(
                    headlineContent = { Text("Group Settings") },
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "Coming soon to mobile!", Toast.LENGTH_SHORT).show()
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings icon"
                        )
                    }
                )
            }

            if (chat.value?.type == "group") {
                ListItem(
                    headlineContent = { Text("Leave Group") },
                    supportingContent = {
                        Text("You will not be able to rejoin unless invited back.")
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Leave icon"
                        )
                    },
                    modifier = Modifier.clickable {
                        leaveChat.value = true
                    }
                )
            } else {
                ListItem(
                    headlineContent = { Text("Leave DM") },
                    supportingContent = {
                        Text("The recipient will not be able to contact you unless you re-initiate the DM.")
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Leave icon"
                        )
                    }
                )
            }
        }
    }
}

class ChatActionsViewModel : ViewModel() {
    val loading = mutableStateOf(false)
    fun leaveChat(id: Int) {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.leaveChat(id).execute()

            loading.value = false
        }
    }
}