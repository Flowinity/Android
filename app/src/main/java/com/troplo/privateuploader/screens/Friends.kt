package com.troplo.privateuploader.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.stores.FriendStore
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.components.friends.dialogs.AddFriendDialog
import com.troplo.privateuploader.components.user.PopupRequiredUser
import com.troplo.privateuploader.components.user.UserPopup
import com.troplo.privateuploader.data.model.Friend
import com.troplo.privateuploader.data.model.PartialUser
import com.troplo.privateuploader.data.model.User
import com.troplo.privateuploader.data.model.defaultPartialUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Friends() {
    val friends = FriendStore.friends.collectAsState()
    val addFriend = remember { mutableStateOf(false) }
    val viewModel = remember { FriendsViewModel() }
    val user: MutableState<PopupRequiredUser?> = remember { mutableStateOf(null) }
    val popup = remember { mutableStateOf(false) }

    if (popup.value) {
        UserPopup(user = user, openBottomSheet = popup)
    }
    if (addFriend.value) {
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
        if (friends.value.filter { it.status == "incoming" }.isNotEmpty()) {
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
                    FriendItem(friend, "incoming", viewModel, user, popup)
                }
            }
        }

        if (friends.value.filter { it.status == "outgoing" }.isNotEmpty()) {
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
                    FriendItem(friend, "outgoing", viewModel, user, popup)
                }
            }
        }

        if (friends.value.filter { it.status == "accepted" }.isNotEmpty()) {
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
                    FriendItem(friend, "accepted", viewModel, user, popup)
                }
            }
        }
    }
}

@Composable
fun FriendItem(friend: Friend, type: String = "incoming", viewModel: FriendsViewModel, user: MutableState<PopupRequiredUser?> = remember { mutableStateOf(null) }, popup: MutableState<Boolean> = remember { mutableStateOf(false) }) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                user.value = PopupRequiredUser(friend.otherUser?.username ?: "")
                popup.value = true
            }
    ) {
        UserAvatar(
            avatar = friend.otherUser?.avatar,
            username = friend.otherUser?.username ?: "Deleted User"
        )
        Column(Modifier.weight(1f)) {
            Text(
                text = friend.otherUser?.username ?: "Deleted User",
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (type == "incoming") {
            IconButton(onClick = {
                viewModel.actFriend(friend.otherUser?.username ?: "", "accept")
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Accept friend request",
                    tint = Color(0xFF4CAF50)
                )
            }
        }

        IconButton(
            onClick = {
                viewModel.actFriend(friend.otherUser?.username ?: "", "remove")
            },
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
        ),
        "accepted",
        FriendsViewModel()
    )
}

class FriendsViewModel: ViewModel() {
    val loading = mutableStateOf(false)

    // Action type: "accept" | "remove" | "send"
    fun actFriend(username: String, action: String) {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            TpuApi.retrofitService.addFriend(username, action).execute()
            loading.value = false
        }
    }
}