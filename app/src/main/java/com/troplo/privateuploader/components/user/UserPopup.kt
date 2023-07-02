package com.troplo.privateuploader.components.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.stores.FriendStore
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.components.friends.dialogs.FriendNicknameDialog
import com.troplo.privateuploader.data.model.User
import com.troplo.privateuploader.data.model.defaultUser
import com.troplo.privateuploader.screens.FriendsViewModel
import com.troplo.privateuploader.screens.settings.SettingsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class PopupRequiredUser(
    val username: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun UserPopup(
    @PreviewParameter(UserPreviewProvider::class)
    user: MutableState<PopupRequiredUser?>,
    openBottomSheet: MutableState<Boolean> = mutableStateOf(true),
) {
    val windowInsets = WindowInsets(0)
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val viewModel = remember { UserPopupViewModel() }
    val friendsViewModel = remember { FriendsViewModel() }

    if (viewModel.user.value == null) {
        viewModel.getFullUser(user.value?.username ?: "")
    }
    val friend = remember { mutableStateOf(FriendStore.friends.value.find { it.otherUser?.id == viewModel.user.value?.id }) }
    val friendNicknameDialog = remember { mutableStateOf(false) }

    if(friendNicknameDialog.value) {
        FriendNicknameDialog(
            open = friendNicknameDialog,
            user = viewModel.user.value
        )
    }

    LaunchedEffect(viewModel.user.value) {
        friend.value = FriendStore.friends.value.find { it.otherUser?.id == viewModel.user.value?.id }
    }

    if (viewModel.user.value !== null) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet.value = false },
            sheetState = bottomSheetState,
            windowInsets = windowInsets,
            dragHandle = { }
        ) {
            UserBanner(viewModel.user.value?.banner)
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    UserAvatar(
                        avatar = viewModel.user.value?.avatar,
                        username = viewModel.user.value?.username ?: "",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        if (friend.value?.otherUser?.nickname?.nickname != null) {
                            Column(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .align(alignment = Alignment.CenterVertically)
                            ) {
                                Text(
                                    text = friend.value?.otherUser?.nickname?.nickname ?: "",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = friend.value?.otherUser?.username ?: "",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            Text(
                                text = viewModel.user.value?.username ?: "",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                            )
                        }

                        Box {
                            if(friend.value !== null) {
                                if (friend.value?.status == "incoming") {
                                    IconButton(onClick = {
                                        friendsViewModel.actFriend(
                                            friend.value?.otherUser?.username ?: "",
                                            "accept"
                                        )
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
                                        friendsViewModel.actFriend(
                                            friend.value?.otherUser?.username ?: "",
                                            "remove"
                                        )
                                    },
                                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove friend",
                                        tint = Color(
                                            if(friend.value?.status == "outgoing") 0xFFFF9800 else 0xFFF44336
                                        )
                                    )
                                }
                            } else if(viewModel.user.value?.id !== UserStore.getUser()?.id) {
                                IconButton(
                                    onClick = {
                                        friendsViewModel.actFriend(
                                            viewModel.user.value?.username ?: "",
                                            "send"
                                        )
                                    },
                                    modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = "Add friend",
                                        tint = Color(0xFF4CAF50)
                                    )
                                }
                            }
                        }
                    }
                }
                Divider(modifier = Modifier.padding(8.dp))
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(viewModel.user.value?.description ?: "")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Account created: ${TpuFunctions.formatDate(viewModel.user.value?.createdAt)}")
                }
                if(friend.value !== null) {
                    Divider(modifier = Modifier.padding(8.dp))
                    SettingsItem(
                        icon = Icons.Default.DriveFileRenameOutline,
                        title = "Set Friend Nickname",
                        subtitle = "Set a nickname only visible to yourself.",
                        onClick = {
                            friendNicknameDialog.value = true
                        }
                    )
                }
            }
        }
    }
}

class UserPopupViewModel : ViewModel() {
    var user: MutableState<User?> = mutableStateOf(null)

    fun getFullUser(username: String): User? {
        viewModelScope.launch(Dispatchers.IO) {
            user.value = UserStore.getUserProfile(username).await()
        }
        return user.value
    }
}

class UserPreviewProvider : PreviewParameterProvider<MutableState<User>> {
    override val values: Sequence<MutableState<User>> = sequenceOf(
        mutableStateOf(defaultUser())
    )
}

fun getFriendStatusDetails(status: String): Map<String, String> {
    return when (status) {
        "accepted" -> {
            mapOf(
                "text" to "Remove Friend",
                "color" to "red",
                "icon" to "mdi-account-minus"
            )
        }
        "outgoing" -> {
            mapOf(
                "text" to "Cancel Request",
                "color" to "grey",
                "icon" to "mdi-account-minus"
            )
        }
        "incoming" -> {
            mapOf(
                "text" to "Accept Request",
                "color" to "green",
                "icon" to "mdi-account-plus"
            )
        }
        else -> {
            mapOf(
                "text" to "Add Friend",
                "color" to "green",
                "icon" to "mdi-account-plus"
            )
        }
    }
}