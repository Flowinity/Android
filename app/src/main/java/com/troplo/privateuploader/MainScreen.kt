package com.troplo.privateuploader

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.troplo.privateuploader.components.core.BottomBarNav
import com.troplo.privateuploader.components.core.NavGraph
import com.troplo.privateuploader.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(user: User?) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomBarNav(navController = navController) }
    ) { paddingValues ->
        NavGraph(
            modifier = Modifier.padding(
                bottom = paddingValues.calculateBottomPadding()),
            navController = navController,
            user = user
        )
    }
}