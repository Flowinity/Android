package com.troplo.privateuploader.components.core

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBarNav(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    println("currentRoute: $currentRoute")
    if (currentRoute == null || currentRoute == NavRoute.Login.path || currentRoute.contains("chat/")) {
        return
    }

    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = NavRoute.Home.path
                )
            },
            selected = currentRoute == NavRoute.Home.path,
            onClick = {
                if(currentRoute != NavRoute.Home.path) {
                    navController.navigate(NavRoute.Home.path) {
                        popUpTo(NavRoute.Home.path) { inclusive = true }
                    }
                }
            },
            // TO LOCALIZE
            label = {Text("Chat")}
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
                if(currentRoute != NavRoute.Gallery.path) {
                    navController.navigate(NavRoute.Gallery.path) {
                        popUpTo(NavRoute.Gallery.path) { inclusive = true }
                    }
                }
            },
            // TO LOCALIZE
            label = { Text("Gallery") }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = NavRoute.Settings.path
                )
            },
            selected = currentRoute == NavRoute.Settings.path,
            onClick = {
                if(currentRoute != NavRoute.Settings.path) {
                    navController.navigate(NavRoute.Settings.path) {
                        popUpTo(NavRoute.Settings.path) { inclusive = true }
                    }
                }
            },
            // TO LOCALIZE
            label = { Text("Settings") }
        )
    }
}