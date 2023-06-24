package com.troplo.privateuploader

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.UserHandler
import com.troplo.privateuploader.components.core.BottomBarNav
import com.troplo.privateuploader.components.core.NavGraph
import com.troplo.privateuploader.components.core.TopBarNav
import com.troplo.privateuploader.data.model.User
import io.socket.client.IO
import io.socket.engineio.client.Socket
import kotlinx.coroutines.Dispatchers
import java.util.Collections

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(user: User?) {
    val context = LocalContext.current
    val token = SessionManager(context).fetchAuthToken()
    if (token != null) {
        SocketHandler.initializeSocket(token)
        UserHandler.initializeUser(token)
    }
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopBarNav(navController =  navController, user = user) },
        bottomBar = { BottomBarNav(navController = navController) }
    ) { paddingValues ->
        NavGraph(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()),
            navController = navController,
            user = user
        )
    }
}