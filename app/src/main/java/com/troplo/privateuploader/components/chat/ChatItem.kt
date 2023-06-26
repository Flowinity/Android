package com.troplo.privateuploader.components.chat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.data.model.Chat

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun ChatItem(
    chat: Chat,
    openChat: (Int) -> Unit,
) {
    val chatName = TpuFunctions.getChatName(chat)
    // track ChatStore.associationId, is mutableStateOf<Int>(0)
    val id = ChatStore.associationId.collectAsState()
    val unread = remember { mutableStateOf(chat.unread) }
    if (id.value == chat.association?.id) {
        unread.value = 0
    }
    NavigationDrawerItem(
        onClick = {
            chat.association?.let {
                openChat(it.id)
            }
        },
        badge = {
            if (unread.value!! > 0) {
                Badge(
                    content = {
                        Text(
                            text = unread.value.toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            }
        },
        modifier = Modifier
          .padding(8.dp)
          .fillMaxWidth(),
        label = {
            Text(
                text = chatName,
                style = MaterialTheme.typography.bodyLarge
            )
            /*     if (chat.recipient == null) {
                     Text(
                         text = "${chat.users.count()} members",
                         style = MaterialTheme.typography.bodyMedium,
                         textAlign = TextAlign.End,
                         modifier = Modifier.padding(top = 1.dp)
                     )
                 } else {
                     Text(
                         text = "Direct Message",
                         style = MaterialTheme.typography.bodyMedium,
                         textAlign = TextAlign.End
                     )
                 }*/
        },
        selected = id.value == chat.association?.id,
        icon = {
            UserAvatar(
                avatar = chat.icon ?: chat.recipient?.avatar,
                username = chatName
            )
        }
    )
}