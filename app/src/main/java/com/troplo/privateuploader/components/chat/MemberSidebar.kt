package com.troplo.privateuploader.components.chat

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.components.user.UserPopup
import com.troplo.privateuploader.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberSidebar() {
    ListItem(
        headlineContent = { Text("Members") }
    )
    val chatId = ChatStore.associationId.collectAsState()
    val chats = ChatStore.chats.collectAsState()
    val chat =
        remember { derivedStateOf { chats.value.find { it.association?.id == chatId.value } } }
    val user: MutableState<User?> = remember { mutableStateOf(null) }
    val popup = remember { mutableStateOf(false) }

    if (popup.value) {
        UserPopup(user = user, openBottomSheet = popup)
    }

    if (chat.value != null) {
        chat.value?.users?.forEach { association ->
            NavigationDrawerItem(
                icon = {
                    UserAvatar(
                        avatar = association.user.avatar,
                        username = association.user.username
                    )
                },
                label = { Text(association.user.username) },
                onClick = {
                    if (association.legacyUser == null) {
                        user.value = association.user
                        popup.value = true
                    }
                },
                selected = false
            )
        }
    }
}