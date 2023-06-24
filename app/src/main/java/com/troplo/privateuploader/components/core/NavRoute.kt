package com.troplo.privateuploader.components.core

import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.data.model.Chat

fun getCurrentRouteTitle(route: String): String {
    return when (route) {
        NavRoute.Home.path -> "Chat"
        NavRoute.Gallery.path -> "Gallery"
        NavRoute.Settings.path -> "Settings"
        NavRoute.SettingsUpload.path -> "Upload"
        NavRoute.Chat.path -> "Chat"
        else -> "PrivateUploader"
    }
}

sealed class NavRoute(val path: String) {
    object Login: NavRoute("login")
    object Home: NavRoute("home")
    object Gallery: NavRoute("gallery")
    object Settings: NavRoute("settings")
    object SettingsUpload: NavRoute("settings/upload")

    object Chat: NavRoute("chat") {
        fun withArgs(chatId: Int): String {
            return buildString {
                append(path)
                append("/$chatId")
            }
        }
    }


    // build navigation path (for screen navigation)
    fun withArgs(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach{ arg ->
                append("/$arg")
            }
        }
    }

    // build and setup route format (in navigation graph)
    fun withArgsFormat(vararg args: String) : String {
        return buildString {
            append(path)
            args.forEach{ arg ->
                append("/{$arg}")
            }
        }
    }
}

