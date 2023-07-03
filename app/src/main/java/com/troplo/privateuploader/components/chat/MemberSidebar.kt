package com.troplo.privateuploader.components.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.components.chat.dialogs.PinsDialog
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.components.user.PopupRequiredUser
import com.troplo.privateuploader.components.user.UserPopup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberSidebar() {
    val chatActions = remember { mutableStateOf(false) }
    val chatId = ChatStore.associationId.collectAsState()
    val chats = ChatStore.chats.collectAsState()
    val chat =
        remember { derivedStateOf { chats.value.find { it.association?.id == chatId.value } } }
    val user: MutableState<PopupRequiredUser?> = remember { mutableStateOf(null) }
    val popup = remember { mutableStateOf(false) }
    val pins = remember { mutableStateOf(false) }

    if (popup.value) {
        UserPopup(user = user, openBottomSheet = popup)
    }

    if (chatActions.value) {
        ChatActions(chat, chatActions)
    }

    if(pins.value) {
        PinsDialog(pins)
    }

    Column {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
        ) {
            IconButton(
                onClick = { ChatStore.searchPanel.value = true },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(
                onClick = { pins.value = true },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Pins"
                )
            }
            IconButton(onClick = {
                chatActions.value = !chatActions.value
            }, modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Options"
                )
            }
        }
        ListItem(
            headlineContent = { Text("Members") }
        )

        if (chat.value != null) {
            chat.value?.users?.forEach { association ->
                NavigationDrawerItem(
                    icon = {
                        UserAvatar(
                            avatar = association.user.avatar,
                            username = association.user.username
                        )
                    },
                    label = { Text(TpuFunctions.getName(association.user)) },
                    onClick = {
                        if (association.legacyUser == null) {
                            user.value = PopupRequiredUser(association.user.username)
                            popup.value = true
                        }
                    },
                    selected = false
                )
            }
        }
    }
}