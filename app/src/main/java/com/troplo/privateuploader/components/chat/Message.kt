package com.troplo.privateuploader.components.chat

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.data.model.ChatAssociation
import com.troplo.privateuploader.data.model.Embed
import com.troplo.privateuploader.data.model.EmbedData
import com.troplo.privateuploader.data.model.Message
import com.troplo.privateuploader.data.model.defaultUser
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun Message(
    message: Message,
    compact: String = "none",
    messageCtx: MutableState<Boolean>?,
    messageCtxMessage: MutableState<Message?>?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onReply: ((replyId: Int) -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        if (messageCtx == null || messageCtxMessage == null) return@detectTapGestures
                        messageCtxMessage.value = message
                        messageCtx.value = true
                    },
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
                        modifier = Modifier.padding(horizontal = 8.dp)
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
                start = 16.dp,
                top = 16.dp,
                end = 16.dp
            ) else Modifier.padding(start = 16.dp, end = 16.dp)
        ) {
            if (normal) {
                UserAvatar(
                    avatar = message.user?.avatar,
                    username = message.user?.username ?: "Deleted User",
                    modifier = Modifier.align(Alignment.Top),
                    showStatus = false
                )
            }
            if (!normal) {
                Spacer(modifier = Modifier.width(40.dp))
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                if (normal) {
                    Row {
                        Text(
                            text = message.user?.username ?: "Deleted User",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = TpuFunctions.formatDate(message.createdAt).toString(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                val color = if (message.pending == false || message.pending == null) {
                    LocalContentColor.current
                } else if (message.error == true) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Row {
                    MarkdownText(
                        markdown = message.content,
                        color = color,
                        onLongClick = {
                            if (messageCtx == null || messageCtxMessage == null) return@MarkdownText
                            messageCtxMessage.value = message
                            messageCtx.value = true
                        },
                        onClick = {
                            if (onClick != null) onClick()
                        }
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
                                            "Edited at ${message.editedAt}",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                                .size(20.dp)
                                .padding(start = 4.dp)
                        )
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
                        content = "Hello World!",
                        createdAt = "2021-09-01T00:00:00.000Z",
                        updatedAt = "2021-09-01T00:00:00.000Z",
                        edited = false,
                        editedAt = null,
                        embeds = listOf(
                            Embed(
                                data = EmbedData(
                                    type = "image",
                                    description = "yes",
                                    height = 69,
                                    siteName = "TPU",
                                    title = "TPU",
                                    upload = null,
                                    url = "https://i.troplo.com",
                                    width = 420
                                ),
                                type = "image",
                            )
                        ),
                        error = false,
                        legacyUser = null,
                        legacyUserId = null,
                        pending = false,
                        pinned = false,
                        readReceipts = emptyList<ChatAssociation>(),
                        reply = Message(
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
                    compact = "none",
                    messageCtx = null,
                    messageCtxMessage = null
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