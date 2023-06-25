package com.troplo.privateuploader.components.chat

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.components.core.UserAvatar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberSidebar() {
    val chat = ChatStore.getChat()
    if(chat != null) {
        chat.users.forEach { association ->
            NavigationDrawerItem(
                icon = {
                   UserAvatar(avatar = association.user.avatar, username = association.user.username)
                },
                label = { Text(association.user.username) },
                onClick = {
                    //
                },
                selected = false
            )
        }
    }
}