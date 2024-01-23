package com.troplo.privateuploader.components.core

fun getCurrentRouteTitle(route: String): String {
    return when (route) {
        NavRoute.Home.path -> "Chat"
        NavRoute.Gallery.path -> "Gallery"
        NavRoute.Settings.path -> "Settings"
        NavRoute.SettingsUpload.path -> "Upload"
        NavRoute.Chat.path -> "Chat"
        NavRoute.SettingsChangelog.path -> "Changelog"
        NavRoute.SettingsAccount.path -> "My Account"
        NavRoute.SettingsPreferences.path -> "Preferences"
        NavRoute.Friends.path -> "Friends"
        NavRoute.SettingsCollections.path -> "Collections"
        NavRoute.Notifications.path -> "Notifications"
        else -> "Flowinity"
    }
}

sealed class NavRoute(val path: String) {
    object Login: NavRoute("login")
    object Home: NavRoute("home")
    object Gallery: NavRoute("gallery")
    object Settings: NavRoute("settings")
    object SettingsAccount: NavRoute("settings/account")
    object SettingsUpload: NavRoute("settings/upload")
    object SettingsChangelog: NavRoute("settings/changelog")
    object SettingsPreferences: NavRoute("settings/preferences")
    object SettingsCollections: NavRoute("settings/collections")
    object SettingsCollectionItem: NavRoute("settings/collection") {
        fun withArgs(collectionId: Int): String {
            return buildString {
                append(path)
                append("/$collectionId")
            }
        }
    }
    object Friends: NavRoute("friends")
    object Chat: NavRoute("chat") {
        fun withArgs(chatId: Int): String {
            return buildString {
                append(path)
                append("/$chatId")
            }
        }
    }
    object Register: NavRoute("register")
    object Notifications: NavRoute("notifications")


    // build navigation path (for screen navigation)
    fun withArgs(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

    // build and setup route format (in navigation graph)
    fun withArgsFormat(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/{$arg}")
            }
        }
    }
}

