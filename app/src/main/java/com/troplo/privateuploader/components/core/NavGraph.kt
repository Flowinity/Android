package com.troplo.privateuploader.components.core

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.data.model.User
import com.troplo.privateuploader.screens.ChatScreen
import com.troplo.privateuploader.screens.Friends
import com.troplo.privateuploader.screens.GalleryScreen
import com.troplo.privateuploader.screens.HomeScreen
import com.troplo.privateuploader.screens.LoginScreen
import com.troplo.privateuploader.screens.settings.ChangelogLayout
import com.troplo.privateuploader.screens.settings.SettingsScreen
import com.troplo.privateuploader.screens.settings.SettingsUploadScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    user: User?,
    context: Context,
    panelsState: OverlappingPanelsState,
) {
    // if user is null, start at login screen, if not start at home screen
    var startDestination = NavRoute.Login.path
    val lastChatId = SessionManager(context).getLastChatId()
    if (user != null && lastChatId == 0) {
        startDestination = NavRoute.Home.path
    } else if (user != null) {
        startDestination = "${NavRoute.Chat.path}/0"
    }
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        print("NavGraph: startDestination: $startDestination")
        addLoginScreen(navController, this)
        addHomeScreen(navController, this, context)
        addGalleryScreen(navController, this)
        addSettingsScreen(navController, this, user)
        addChatScreen(navController, this, context, panelsState)
        addSettingsUploadScreen(navController, this)
        addSettingsChangelogScreen(navController, this)
        addFriendsScreen(navController, this)
    }
}

private fun addLoginScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
) {
    navGraphBuilder.composable(route = NavRoute.Login.path) {
        LoginScreen(
            onLoginSuccess = {
                println("LoginScreen: onLoginSuccess")
                navController.navigate(NavRoute.Home.path)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun addHomeScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    context: Context,
) {
    navGraphBuilder.composable(route = NavRoute.Home.path) {
        HomeScreen(
            openChat = { chatId ->
                ChatStore.setAssociationId(chatId, context)
                navController.navigate("${NavRoute.Chat.path}/$chatId")
            },
            panelState = null,
            navController = navController,
        )
    }
}

private fun addGalleryScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
) {
    navGraphBuilder.composable(route = NavRoute.Gallery.path) {
        GalleryScreen()
    }
}

private fun addSettingsScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    user: User?,
) {
    navGraphBuilder.composable(route = NavRoute.Settings.path) {
        SettingsScreen(
            navigate = { subItem ->
                navController.navigate("${NavRoute.Settings.path}/$subItem")
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun addChatScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    context: Context,
    panelsState: OverlappingPanelsState,
) {
    navGraphBuilder.composable(
        route = "${NavRoute.Chat.path}/{chatId}",
        arguments = listOf(
            navArgument("chatId") {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->
        val chatId = backStackEntry.arguments?.getInt("chatId")
        if (chatId == 0 || chatId == null) {
            LaunchedEffect(key1 = chatId) {
                panelsState.openStartPanel()
            }
        }
        ChatScreen(
            chatId = chatId,
            panelsState = panelsState
        )
    }
}

private fun addSettingsUploadScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
) {
    navGraphBuilder.composable(route = NavRoute.SettingsUpload.path) {
        SettingsUploadScreen()
    }
}

private fun addSettingsChangelogScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
) {
    navGraphBuilder.composable(route = NavRoute.SettingsChangelog.path) {
        ChangelogLayout()
    }
}


private fun addFriendsScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
) {
    navGraphBuilder.composable(route = NavRoute.Friends.path) {
        Friends()
    }
}