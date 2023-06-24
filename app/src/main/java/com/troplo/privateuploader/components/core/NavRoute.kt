package com.troplo.privateuploader.components.core

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

