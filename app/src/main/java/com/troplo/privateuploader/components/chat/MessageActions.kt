package com.troplo.privateuploader.components.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.data.model.Message as MessageModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageActions(
    message: MutableState<MessageModel?>,
    openBottomSheet: MutableState<Boolean>,
    editId: MutableState<Int>,
    messageInput: MutableState<String>,
) {
    val windowInsets = WindowInsets(0)
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = { openBottomSheet.value = false },
        sheetState = bottomSheetState,
        windowInsets = windowInsets
    ) {
        if (message.value != null) {
            Message(message.value!!, "none", null, null, modifier = Modifier.padding(bottom = 8.dp))
        }
        Column(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            ListItem(
                headlineContent = { Text("Reply") },
                leadingContent = {
                    Icon(
                        Icons.Default.Reply,
                        contentDescription = "Reply icon"
                    )
                }
            )

            if (message.value?.userId == UserStore.getUser()?.id) {
                ListItem(
                    headlineContent = { Text("Edit") },
                    modifier = Modifier.clickable {
                        editId.value = message.value?.id ?: 0
                        messageInput.value = message.value?.content ?: ""
                        openBottomSheet.value = false
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit icon"
                        )
                    }
                )
            }

            if (message.value?.userId == UserStore.getUser()?.id) {
                ListItem(
                    headlineContent = { Text("Delete") },
                    leadingContent = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete icon"
                        )
                    },
                    modifier = Modifier.clickable {
                        ChatStore.deleteMessage(message.value?.id ?: 0)
                        openBottomSheet.value = false
                    }
                )
            }
            val clipboardManager =
                LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            ListItem(
                headlineContent = { Text("Copy Text") },
                modifier = Modifier.clickable {
                    clipboardManager.setPrimaryClip(
                        ClipData.newPlainText(
                            "message",
                            message.value?.content
                        )
                    )
                    openBottomSheet.value = false
                },
                leadingContent = {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy icon"
                    )
                }
            )
        }
    }
}