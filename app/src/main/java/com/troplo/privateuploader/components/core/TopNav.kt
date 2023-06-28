package com.troplo.privateuploader.components.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.components.chat.dialogs.SearchDialog
import com.troplo.privateuploader.data.model.User

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun TopBarNav(navController: NavController, user: User?, panelState: OverlappingPanelsState) {
    val chatSearch = remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    if (currentRoute == null || currentRoute == NavRoute.Login.path) {
        return
    }
    val chats = ChatStore.chats.collectAsState()
    val chat =
        remember { derivedStateOf { chats.value.find { it.association?.id == ChatStore.associationId.value } } }

    val openPanel = remember { mutableStateOf(false) }

    if(chatSearch.value) {
        SearchDialog(chatSearch)
    }

    if(openPanel.value) {
        LaunchedEffect(Unit) {
            openPanel.value = false
            if(panelState.isStartPanelOpen) {
                panelState.closePanels()
            } else {
                panelState.openStartPanel()
            }
        }
    }

    TopAppBar(
        navigationIcon = {
            if (currentRoute != NavRoute.Home.path) {
                IconButton(onClick = {
                    openPanel.value = true
                }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Menu"
                    )
                }
            }
        },
        title = {
            if (currentRoute.startsWith("chat/") && chat.value != null) {
                Box {
                    UserAvatar(
                        avatar = ChatStore.getChat()?.icon
                            ?: ChatStore.getChat()?.recipient?.avatar,
                        username = ChatStore.getChat()?.name
                            ?: ChatStore.getChat()?.recipient?.username ?: "Deleted User",
                        showStatus = true
                    )
                    Text(
                        TpuFunctions.getChatName(ChatStore.getChat()),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 48.dp).align(Alignment.Center)
                    )
                }
            } else {
                Text(getCurrentRouteTitle(currentRoute))
            }
        },
        actions = {
            if(currentRoute.startsWith("chat/")) {
                IconButton(onClick = {
                    chatSearch.value = true
                }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                }
            }
            IconButton(onClick = { /* doSomething() */ }) {
                UserAvatar(
                    avatar = user?.avatar,
                    username = user?.username ?: "Deleted User"
                )
            }
        }
    )
}