package com.troplo.privateuploader.components.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.troplo.privateuploader.data.model.User
import com.troplo.privateuploader.screens.*
import com.troplo.privateuploader.screens.settings.SettingsScreen
import com.troplo.privateuploader.screens.settings.SettingsUploadScreen

@Composable
fun NavGraph(modifier: Modifier = Modifier, navController: NavHostController, user: User?) {
    // if user is null, start at login screen, if not start at home screen
    var startDestination = NavRoute.Login.path
    if (user != null) {
        startDestination = NavRoute.Home.path
    }
    println("startDestination: $startDestination, user: $user")
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        addLoginScreen(navController, this)
        addHomeScreen(navController, this)
        addGalleryScreen(navController, this)
        addSettingsScreen(navController, this, user)
        addChatScreen(navController, this)
        addSettingsUploadScreen(navController, this)
    }
}

private fun addLoginScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
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

private fun addHomeScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = NavRoute.Home.path) {
        HomeScreen(
            openChat = { chatId ->
                navController.navigate("${NavRoute.Chat.path}/$chatId")
            },
        )
    }
}

private fun addGalleryScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = NavRoute.Gallery.path) {
        GalleryScreen()
    }
}

private fun addSettingsScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    user: User?
) {
    navGraphBuilder.composable(route = NavRoute.Settings.path) {
        SettingsScreen(
            navigate = { subItem ->
                navController.navigate("${NavRoute.Settings.path}/$subItem")
            },
        )
    }
}

private fun addChatScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(
        route = NavRoute.Chat.withArgsFormat("chatId"),
        arguments = listOf(
            navArgument("chatId") {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->
        val chatId = backStackEntry.arguments?.getInt("chatId")
        ChatScreen(associationId = chatId!!)
    }
}

private fun addSettingsUploadScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder
) {
    navGraphBuilder.composable(route = NavRoute.SettingsUpload.path) {
        SettingsUploadScreen()
    }
}