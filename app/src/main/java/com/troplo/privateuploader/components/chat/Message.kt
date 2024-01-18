package com.troplo.privateuploader.components.chat

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.data.model.Embed
import com.troplo.privateuploader.data.model.EmbedData
import com.troplo.privateuploader.data.model.Message
import com.troplo.privateuploader.data.model.defaultUser
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme

@Composable
fun Message(
    modifier: Modifier = Modifier,
    message: Message,
    compact: String = "none",
    onClick: (() -> Unit)? = null,
    onReply: ((replyId: Int) -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (onClick != null) onClick()
                    }
                )
            }
            .then(modifier)
    ) {
        if (compact == "separator") {
            Column(
                modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Divider(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = TpuFunctions.formatDateDay(message.createdAt).toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Divider(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        val normal = compact == "separator" || compact == "none"

        if(message.replyId != null) {
            ReplyMessage(message.reply, onReply)
        }

        Row(
            modifier = if (normal && message.replyId == null) Modifier.padding(
                start = 12.dp,
                top = 16.dp,
                end = 16.dp
            ) else Modifier.padding(start = 12.dp, end = 16.dp)
        ) {
            if (normal) {
                Box(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .align(Alignment.Top)
                ) {
                    UserAvatar(
                        avatar = message.user?.avatar,
                        username = message.user?.username ?: "Deleted User",
                        showStatus = false
                    )
                }
            }
            if (!normal) {
                Spacer(modifier = Modifier.width(44.dp))
            }
            Column(modifier = Modifier.padding(start = 8.dp).fillMaxWidth()) {
                if (normal) {
                    Row {
                        Text(
                            text = TpuFunctions.getName(message.user),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = TpuFunctions.formatDate(message.createdAt).toString(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically)
                        )
                        if (message.edited) {
                            val context = LocalContext.current
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edited",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable {
                                        Toast
                                            .makeText(
                                                context,
                                                "${TpuFunctions.formatDate(message.editedAt)}",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                    .size(20.dp)
                                    .padding(start = 6.dp)
                                    .align(Alignment.CenterVertically)

                            )
                        }
                    }
                }

                val color = if (message.pending == false || message.pending == null) {
                    LocalContentColor.current
                } else if (message.error == true) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    ) {
                            MarkdownText(
                                content = message.content,
                                color = color,
                                onClick = {
                                    if (onClick != null) onClick()
                                },
                                modifier = Modifier.weight(1f),
                                onLongClick = onLongClick
                            )
                        if (message.edited && !normal) {
                            val context = LocalContext.current
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edited",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable {
                                        Toast
                                            .makeText(
                                                context,
                                                "${TpuFunctions.formatDate(message.editedAt)}",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                    .size(20.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if(message.readReceipts.size <= 2) {
                            message.readReceipts.forEach {
                                Box(modifier = Modifier.padding(start = 4.dp)) {
                                    UserAvatar(
                                        avatar = it.user?.avatar,
                                        username = it.user?.username ?: "Deleted User",
                                        showStatus = false,
                                        modifier = Modifier
                                            .size(20.dp)
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                UserAvatar(
                                    avatar = null,
                                    username = message.readReceipts.size.toString(),
                                    showStatus = false,
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                        }
                    }
                }

                message.embeds.forEach {
                    Embed(embed = it)
                }
            }
        }
    }
}

@Preview
@Composable
fun MessagePreview() {
    PrivateUploaderTheme(
        content = {
            Surface {
                Message(
                    message = Message(
                        id = 1,
                        user = defaultUser(),
                        chatId = 1,
                        content = "ioeajdoiaeduhyausdjhaosidhj8asduy89sady897asdy789sad7y8sasdhusa!",
                        createdAt = "2021-09-01T00:00:00.000Z",
                        updatedAt = "2021-09-01T00:00:00.000Z",
                        edited = true,
                        editedAt = null,
                        embeds = emptyList(),
                        error = false,
                        legacyUser = null,
                        legacyUserId = null,
                        pending = false,
                        pinned = false,
                        readReceipts = emptyList(),
                        reply = Message(
                            id = 1,
                            user = defaultUser(),
                            chatId = 1,
                            content = "Hello World!",
                            createdAt = "2021-09-01T00:00:00.000Z",
                            updatedAt = "2021-09-01T00:00:00.000Z",
                            edited = false,
                            editedAt = null,
                            embeds = emptyList(),
                            error = false,
                            legacyUser = null,
                            legacyUserId = null,
                            pending = false,
                            pinned = false,
                            readReceipts = emptyList(),
                            replyId = 1,
                            tpuUser = null,
                            userId = 1,
                            type = "message",
                            reply = null
                        ),
                        replyId = 1,
                        tpuUser = null,
                        userId = 1,
                        type = "message"
                    ),
                    compact = "none"
                )
            }
        }
    )
}

/*class SampleMessageProvider : PreviewParameterProvider<Message> {
    override val values: Sequence<Message>
        get() = sequenceOf(
            Message(
                id = 1,
                user = defaultUser(),
                chatId = 1,
                content = "Hello World!",
                createdAt = "2021-09-01T00:00:00.000Z",
                updatedAt = "2021-09-01T00:00:00.000Z",
                edited = false,
                editedAt = null,
                embeds = emptyList<Embed>(),
                error = false,
                legacyUser = null,
                legacyUserId = null,
                pending = false,
                pinned = false,
                readReceipts = emptyList<ChatAssociation>(),
                reply = null,
                replyId = null,
                tpuUser = null,
                userId = 1,
                type = "message"
            )
        )

    override val count: Int
        get() = values.count()
}*/