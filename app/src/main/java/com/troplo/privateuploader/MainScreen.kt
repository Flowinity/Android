package com.troplo.privateuploader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.chat.MemberSidebar
import com.troplo.privateuploader.components.core.BottomBarNav
import com.troplo.privateuploader.components.core.ConnectingBanner
import com.troplo.privateuploader.components.core.NavGraph
import com.troplo.privateuploader.components.core.NavRoute
import com.troplo.privateuploader.components.core.OverlappingPanels
import com.troplo.privateuploader.components.core.PanelSurface
import com.troplo.privateuploader.components.core.TopBarNav
import com.troplo.privateuploader.components.core.rememberOverlappingPanelsState
import com.troplo.privateuploader.data.model.User
import com.troplo.privateuploader.screens.HomeScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen() {
    val user = UserStore.user.collectAsState()
    val context = LocalContext.current
    val navController = rememberNavController()
    val panelState = rememberOverlappingPanelsState()
    var closePanels by remember { mutableStateOf(false) }
    // track socket connection status
    val closePanelsFunc = {
        closePanels = true
    }
    if (closePanels) {
        LaunchedEffect(Unit) {
            panelState.closePanels()
            closePanels = false
        }
    }
    Scaffold(
        topBar = {
            if (!SocketHandler.connected.value) {
                ConnectingBanner()
            } else {
                TopBarNav(navController = navController, user = user.value, panelState = panelState)
            }
        },
        bottomBar = {
            BottomBarNav(
                navController = navController,
                panelState = panelState,
                closePanels = closePanelsFunc
            )
        },
    ) { paddingValues ->
        OverlappingPanels(
            modifier = Modifier.fillMaxSize(),
            panelsState = panelState,
            gesturesEnabled = navController.currentDestination?.route?.startsWith("chat/") == true,
            panelStart = {
                PanelSurface {
                    ModalDrawerSheet(
                        modifier = Modifier.padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        )
                    ) {
                        Spacer(Modifier.height(12.dp))
                        HomeScreen(
                            openChat = { chatId ->
                                ChatStore.setAssociationId(chatId, context)
                                navController.navigate("${NavRoute.Chat.path}/$chatId")
                                closePanels = true
                            },
                            panelState = panelState
                        )
                    }
                }
            },
            panelCenter = {
                PanelSurface {
                    NavGraph(
                        modifier = Modifier.padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        ),
                        navController = navController,
                        user = user.value,
                        context,
                        panelsState = panelState
                    )
                }
            },
            panelEnd = {
                PanelSurface {
                    ModalDrawerSheet(
                        modifier = Modifier.padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .weight(weight = 1f, fill = false)

                        ) {
                            Spacer(Modifier.height(12.dp))
                            MemberSidebar()
                        }
                    }
                }
            }
        )
    }
}