package com.troplo.privateuploader.components.core

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.stores.FriendStore
import com.troplo.privateuploader.api.stores.UserStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarNav(
    navController: NavController,
    panelState: OverlappingPanelsState,
    closePanels: () -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    if (currentRoute == null || currentRoute == NavRoute.Login.path || currentRoute == NavRoute.Register.path) {
        return
    }
    val user = UserStore.user.collectAsState()
    val friends = FriendStore.friends.collectAsState()

    AnimatedVisibility(
        visible = !currentRoute.contains("chat/") || panelState.isStartPanelPartialOpen,
        enter = expandVertically(animationSpec = tween(durationMillis = 200)) { fullWidth ->
            // Offsets the content by 1/3 of its width to the left, and slide towards right
            // Overwrites the default animation with tween for this slide animation.
            -fullWidth / 3
        } + fadeIn(
            // Overwrites the default animation with tween
            animationSpec = tween(durationMillis = 100)
        ),
        exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessHigh)) {
            // Overwrites the ending position of the slide-out to 200 (pixels) to the right
            0
        } + fadeOut()) {
            NavigationBar(
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = NavRoute.Home.path
                        )
                    },
                    selected = currentRoute.startsWith("chat/"),
                    onClick = {
                        if (!currentRoute.startsWith("chat/")) {
                            navController.navigate("${NavRoute.Chat.path}/${ChatStore.associationId.value}") {
                                popUpTo("chat/${ChatStore.associationId.value}") {
                                    inclusive = true
                                }
                            }
                        }
                    },
                    // TO LOCALIZE
                    //label = { Text("Chat") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = NavRoute.Gallery.path
                        )
                    },
                    selected = currentRoute == NavRoute.Gallery.path,
                    onClick = {
                        closePanels()
                        if (currentRoute != NavRoute.Gallery.path) {
                            navController.navigate(NavRoute.Gallery.path) {
                                popUpTo(NavRoute.Gallery.path) { inclusive = true }
                            }
                        }
                    },
                    // TO LOCALIZE
                    //label = { Text("Gallery") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = NavRoute.Friends.path
                        )
                        if(friends.value.any { it.status == "incoming" }) {
                            Badge(
                                modifier = Modifier
                                    .offset(x = 11.dp, y = (-4).dp)
                            ) {
                                Text(
                                    friends.value.filter { it.status == "incoming" }.size.toString()
                                )
                            }
                        }
                    },
                    selected = currentRoute == NavRoute.Friends.path,
                    onClick = {
                        closePanels()
                        if (currentRoute != NavRoute.Friends.path) {
                            navController.navigate(NavRoute.Friends.path) {
                                popUpTo(NavRoute.Friends.path) { inclusive = true }
                            }
                        }
                    },
                    // TO LOCALIZE
                    //label = { Text("Friends") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = NavRoute.Notifications.path
                        )
                        if(user.value?.notifications?.any { !it.dismissed } == true) {
                            Badge(
                                modifier = Modifier
                                    .offset(x = 11.dp, y = (-4).dp)
                            ) {
                                Text(
                                    user.value?.notifications?.filter { !it.dismissed }?.size.toString()
                                )
                            }
                        }
                    },
                    selected = currentRoute == NavRoute.Notifications.path,
                    onClick = {
                        closePanels()
                        if (currentRoute != NavRoute.Notifications.path) {
                            navController.navigate(NavRoute.Notifications.path) {
                                popUpTo(NavRoute.Notifications.path) { inclusive = true }
                            }
                        }
                    },
                    // TO LOCALIZE
                    //label = { Text("Notifications") }
                )

                NavigationBarItem(
                    icon = {
                        UserAvatar(
                            avatar = user.value?.avatar,
                            username = user.value?.username ?: "Deleted User",
                            modifier = Modifier.size(28.dp),
                            showStatus = false
                        )
                    },
                    selected = currentRoute == NavRoute.Settings.path,
                    onClick = {
                        closePanels()
                        if (currentRoute != NavRoute.Settings.path) {
                            navController.navigate(NavRoute.Settings.path) {
                                popUpTo(NavRoute.Settings.path) { inclusive = true }
                            }
                        }
                    },
                    // TO LOCALIZE
                    //label = { Text("Settings") }
                )
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BottomBarNavPreview() {
    val navController = NavController(LocalContext.current)
    val panelState = OverlappingPanelsState(OverlappingPanelsValue.Closed)
    BottomBarNav(navController = navController, panelState = panelState) {
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun TestNotificationBadge() {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = NavRoute.Home.path
                )
            },
            selected = false,
            onClick = {},
            // TO LOCALIZE
            label = { Text("Chat") }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = NavRoute.Gallery.path
                )
            },
            selected = false,
            onClick = {},
            // TO LOCALIZE
            label = { Text("Gallery") }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = NavRoute.Friends.path
                )
                Badge(
                    modifier = Modifier
                        .offset(x = 11.dp, y = (-4).dp)
                ) {
                    Text(
                       "333"
                    )
                }
            },
            selected = false,
            onClick = {},
            // TO LOCALIZE
            label = { Text("Friends") }
        )

        if(UserStore.debug) {
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = NavRoute.Friends.path
                    )
                },
                selected = false,
                onClick = {},
                // TO LOCALIZE
                label = { Text("Messages") }
            )
        }

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = NavRoute.Settings.path
                )
            },
            selected = false,
            onClick = {},
            // TO LOCALIZE
            label = { Text("Settings") }
        )
    }
}