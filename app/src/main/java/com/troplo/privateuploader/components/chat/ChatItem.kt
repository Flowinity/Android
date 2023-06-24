package com.troplo.privateuploader.components.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.data.model.Chat
import com.troplo.privateuploader.data.model.User

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
@Preview
fun ChatItem(@PreviewParameter(
    SampleChatProvider::class
) chat: Chat, openChat: (Int) -> Unit
) {
    Card(
        onClick = { chat.association?.let { openChat(it.id) } },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        val chatName = TpuFunctions.getChatName(chat)
        Row {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                UserAvatar(
                    avatar = chat.icon ?: chat.recipient?.avatar,
                    username = chatName
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = chatName,
                    style = MaterialTheme.typography.bodyLarge
                )
                if(chat.recipient == null) {
                    Text(
                        text = "${chat.users.count()} members",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start
                    )
                } else {
                    Text(
                        text = "Direct Message",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

class SampleChatProvider : PreviewParameterProvider<Chat> {
    override val values: Sequence<Chat>
        get() = sequenceOf(
            Chat(
                id = 1,
                name = "Sample Chat",
                users = listOf(
                    User(1, "User 1", "avatar_url_1", ""),
                    User(2, "User 2", "avatar_url_2", "")
                ),
                recipient = null,
                icon = null,
                type = null,
                createdAt = null,
                updatedAt = null,
                legacyUserId = null,
                user = null,
                legacyUser = null,
                association = null,
                messages = null,
                unread = null,
                typers = null
            )
        )

    override val count: Int
        get() = values.count()
}