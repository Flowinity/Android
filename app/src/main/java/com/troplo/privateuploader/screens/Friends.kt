package com.troplo.privateuploader.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.stores.FriendStore
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.components.friends.dialogs.AddFriendDialog
import com.troplo.privateuploader.data.model.Friend
import com.troplo.privateuploader.data.model.defaultPartialUser
import com.troplo.privateuploader.data.model.defaultUser
import kotlinx.coroutines.delay

@Composable
fun Friends() {
    val friends = FriendStore.friends.collectAsState()
    val addFriend = remember { mutableStateOf(false) }

    if(addFriend.value) {
        AddFriendDialog(addFriend)
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            ListItem(
                headlineContent = {
                    Text("Friends")
                },
                trailingContent = {
                    IconButton(
                        onClick = {
                            addFriend.value = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Friend"
                        )
                    }
                }
            )
        }
        if(friends.value.filter { it.status == "incoming" }.isNotEmpty()) {
            item {
                ListItem(
                    headlineContent = {
                        Text("Incoming")
                    }
                )
            }

            friends.value.filter { it.status == "incoming" }.forEach { friend ->
                item(
                    key = friend.id
                ) {
                    FriendItem(friend, "incoming")
                }
            }
        }

        if(friends.value.filter { it.status == "outgoing" }.isNotEmpty()) {
            item {
                ListItem(
                    headlineContent = {
                        Text("Outgoing")
                    }
                )
            }

            friends.value.filter { it.status == "outgoing" }.forEach { friend ->
                item(
                    key = friend.id
                ) {
                    FriendItem(friend, "outgoing")
                }
            }
        }

        if(friends.value.filter { it.status == "accepted" }.isNotEmpty()) {
            item {
                ListItem(
                    headlineContent = {
                        Text("Accepted")
                    }
                )
            }

            friends.value.filter { it.status == "accepted" }.forEach { friend ->
                item(
                    key = friend.id
                ) {
                    FriendItem(friend, "accepted")
                }
            }
        }
    }
}

@Composable
fun FriendItem(friend: Friend, type: String = "incoming") {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        UserAvatar(avatar = friend.otherUser?.avatar, username = friend.otherUser?.username ?: "Deleted User")
        Column(Modifier.weight(1f)) {
            Text(
                text = friend.otherUser?.username ?: "Deleted User",
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if(type == "incoming") {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Accept friend request",
                    tint = Color(0xFF4CAF50)
                )
            }
        }

        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove friend",
                tint = Color(0xFFF44336)
            )
        }
    }
}

@Preview
@Composable
fun FriendItemPreview() {
    FriendItem(
        friend = Friend(
            id = 1,
            status = "accepted",
            otherUser = defaultPartialUser(),
            user = defaultPartialUser(),
            createdAt = TpuFunctions.currentISODate(),
            updatedAt = TpuFunctions.currentISODate(),
            otherUserId = 1,
            userId = 1
        )
    )
}