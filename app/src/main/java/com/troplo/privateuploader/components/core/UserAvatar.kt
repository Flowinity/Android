package com.troplo.privateuploader.components.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.imageLoader
import com.troplo.privateuploader.api.stores.FriendStore
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.data.model.Friend
import com.troplo.privateuploader.data.model.PartialUser
import com.troplo.privateuploader.data.model.defaultPartialUser
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAvatar(
    avatar: String?,
    username: String,
    modifier: Modifier = Modifier,
    showStatus: Boolean = true,
    fake: Boolean = false,
) {
    Box {
        val imageSize = 40.dp
        val dotSize = 7.dp
        val friends = FriendStore.friends.collectAsState()
        var friend = friends.value.find { it.otherUser?.username == username }
        val currentUser = UserStore.user.collectAsState()
        if (fake && friend == null) {
            friend = Friend(
                otherUser = defaultPartialUser(id = 2, username = "Troplo"),
                user = defaultPartialUser(),
                createdAt = TpuFunctions.currentISODate(),
                updatedAt = TpuFunctions.currentISODate(),
                userId = 1,
                otherUserId = 2,
                status = "online",
                id = -1
            )
        } else if (friend == null && currentUser.value?.username == username) {
            friend = Friend(
                otherUser = PartialUser(
                    username = currentUser.value!!.username,
                    id = currentUser.value!!.id,
                    avatar = currentUser.value!!.avatar,
                    status = currentUser.value!!.storedStatus,
                    nickname = null,
                    banner = currentUser.value!!.banner,
                    description = currentUser.value!!.description,
                    administrator = currentUser.value!!.administrator,
                    moderator = currentUser.value!!.moderator,
                    platforms = currentUser.value!!.platforms,
                    plan = currentUser.value!!.plan
                ),
                user = defaultPartialUser(),
                createdAt = TpuFunctions.currentISODate(),
                updatedAt = TpuFunctions.currentISODate(),
                userId = 2,
                otherUserId = 1,
                status = "online",
                id = -1
            )
        }
        if (avatar != null) {
            Image(
                contentDescription = "User profile picture",
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .dispatcher(Dispatchers.IO)
                        .data(data = TpuFunctions.image(avatar, null))
                        .apply {
                            size(Size.ORIGINAL)
                        }
                        .build(),
                    imageLoader = imageLoader(LocalContext.current),
                    contentScale = ContentScale.FillWidth
                ),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (fake) MaterialTheme.colorScheme.surface else Color.Transparent)
                    .then(modifier)
                    .size(imageSize)
            )
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .clip(CircleShape)
                    .then(modifier)
                    .size(40.dp)
            ) {
                Text(
                    text = username.first().toString(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        if (friend != null && showStatus && friend.otherUser?.status !== null) {
            BoxWithConstraints(modifier = Modifier.matchParentSize()) {
                Row(
                    modifier = Modifier
                        .padding(2.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(text = "", modifier = Modifier.width(28.dp))
                    if (friend.otherUser?.platforms == null || friend.otherUser?.platforms!!.isEmpty() || friend.otherUser?.platforms?.get(
                            0
                        )?.platform !== "android_kotlin"
                    ) {
                        Box(
                            modifier = Modifier
                                .size(dotSize)
                                .clip(CircleShape)
                                .background(
                                    Color(
                                        TpuFunctions.getStatusDetails(
                                            friend.otherUser?.status ?: "offline"
                                        ).first
                                    )
                                )
                                .align(Alignment.Bottom)
                                .border(
                                    width = 0.3.dp,
                                    color = MaterialTheme.colorScheme.background,
                                    shape = CircleShape
                                )
                        )
                    } else {
                        // mobile Icon of Material Design
                        Icon(
                            imageVector = Icons.Filled.Smartphone,
                            contentDescription = "Mobile",
                            tint = Color(
                                TpuFunctions.getStatusDetails(
                                    friend.otherUser?.status ?: "offline"
                                ).first
                            ),
                            modifier = Modifier
                                .clip(CircleShape)
                                .align(Alignment.Bottom)
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewUserAvatar() {
    val friends = FriendStore.friends.collectAsState()
    friends.value.plus({
        Friend(
            otherUser = defaultPartialUser(id = 2, username = "Troplo"),
            user = defaultPartialUser(),
            createdAt = TpuFunctions.currentISODate(),
            updatedAt = TpuFunctions.currentISODate(),
            userId = 1,
            otherUserId = 2,
            status = "online",
            id = 1
        )
    })
    UserAvatar(avatar = "50ba79e4.png", username = "Troplo", fake = true)
}