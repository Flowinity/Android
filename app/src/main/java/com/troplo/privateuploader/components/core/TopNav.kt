package com.troplo.privateuploader.components.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.data.model.User

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun TopBarNav(navController: NavController, user: User?, panelState: OverlappingPanelsState) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    if (currentRoute == null || currentRoute == NavRoute.Login.path) {
        return
    }

    // if chat/ then ChatStore.getChat(0).name else title
    val title = if (currentRoute == NavRoute.Chat.path && ChatStore.getChat() != null) {
        TpuFunctions.getChatName(ChatStore.getChat())
    } else {
        getCurrentRouteTitle(currentRoute)
    }

    val openPanel = remember { mutableStateOf(false) }

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
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = { /* doSomething() */ }) {
                UserAvatar(
                    avatar = user?.avatar,
                    username = user?.username ?: "Deleted User"
                )
            }
        }
    )
}