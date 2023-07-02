package com.troplo.privateuploader.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    chatActions: MutableState<Boolean>,
    chatCtx: MutableState<Chat?>,
) {
    val chatName = TpuFunctions.getChatName(chat)
    // track ChatStore.associationId, is mutableStateOf<Int>(0)
    val id = ChatStore.associationId.collectAsState()
    val unread = remember { mutableStateOf(chat.unread) }
    if (id.value == chat.association?.id) {
        unread.value = 0
    }
    NavigationItem(
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
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        chatCtx.value = chat
                        chatActions.value = true
                    },
                    onTap = {
                        openChat(chat.association?.id ?: 0)
                    }
                )
            },
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

@Composable
fun NavigationItem(
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    badge: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick?.invoke() }
            .padding(horizontal = 8.dp)
            .background(
                color = if (selected) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                label()
                if (subtitle !== null) subtitle()
            }
            badge?.invoke()
    }
}