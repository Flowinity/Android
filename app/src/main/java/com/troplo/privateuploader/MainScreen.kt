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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.troplo.privateuploader.api.ChatStore
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
import com.troplo.privateuploader.screens.HomeScreen
import io.sentry.compose.SentryTraced

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterialApi::class
)

@Composable
fun MainScreen() {
    SentryTraced("main") {
        val user = UserStore.user.collectAsState()
        val context = LocalContext.current
        val navController = rememberNavController()
        val panelState = rememberOverlappingPanelsState()
        var closePanels by remember { mutableStateOf(false) }
        var openPanel by remember { mutableStateOf(false) }

        val closePanelsFunc = {
            closePanels = true
        }

        val openPanelFunc = {
            openPanel = true
        }

        LaunchedEffect(closePanels) {
            if(!closePanels) return@LaunchedEffect
            panelState.closePanels()
            closePanels = false
        }

        LaunchedEffect(openPanel) {
            if(!openPanel) return@LaunchedEffect
            if (!panelState.isPanelsClosed) {
                panelState.closePanels()
            } else {
                panelState.openStartPanel()
            }
            openPanel = false
        }

        Scaffold(
            topBar = {
                if (!SocketHandler.connected.value && user.value != null) {
                    ConnectingBanner()
                } else {
                    TopBarNav(
                        navController = navController,
                        openPanelFunc
                    )
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
                gesturesEnabled = navController.currentDestination?.route?.startsWith("chat/") == true || !panelState.isPanelsClosed,
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
                                    if(ChatStore.associationId.value != chatId) {
                                        ChatStore.setAssociationId(chatId, context)
                                        navController.navigate("${NavRoute.Chat.path}/$chatId")
                                    }
                                    closePanels = true
                                },
                                panelState = panelState,
                                navController = navController
                            )
                        }
                    }
                },
                panelCenter = {
                    PanelSurface {
                        NavGraph(
                            modifier = Modifier.padding(
                                top = paddingValues.calculateTopPadding(),
                                bottom = if (navController.currentDestination?.route?.startsWith("chat/") == true) 0.dp else paddingValues.calculateBottomPadding()
                            ),
                            navController = navController,
                            user = user.value,
                            context = context,
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
}