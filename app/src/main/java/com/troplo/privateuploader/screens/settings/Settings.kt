package com.troplo.privateuploader.screens.settings

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.troplo.privateuploader.BuildConfig
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.user.UserBanner
import com.troplo.privateuploader.ui.theme.Primary

@Composable
@Preview
fun SettingsScreen(
    navigate: (String) -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        UserBanner()
        val context = LocalContext.current

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 208.dp)
        ) {
            item {
                SettingsItem(
                    Icons.Default.AccountBox,
                    "My TPU",
                    "Your TPU account settings",
                    onClick = { navigate("settings/account") })
            }

            item {
                SettingsItem(
                    Icons.Default.Upload,
                    "Auto-Upload",
                    "Options to automatically upload to TPU",
                    onClick = { navigate("settings/upload") }
                )
            }

            item {
                SettingsItem(
                    Icons.Default.MoreTime,
                    "Changelog",
                    "Recent updates to TPU Mobile",
                    onClick = { navigate("settings/changelog") }
                )
            }

            item {
                SettingsItem(
                    Icons.Default.Logout,
                    "Logout",
                    "Logout of TPU",
                    onClick = {
                        UserStore.logout(context)
                        navigate("login")
                    }
                )
            }

            if(UserStore.debug) {
                item {
                    SettingsItem(
                        Icons.Default.DeviceUnknown,
                        "Re-attempt device registration",
                        "Re-attempt device registration with TPU Firebase CM",
                        onClick = {
                            UserStore.registerFCMToken()
                        }
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val rippled = remember { mutableIntStateOf(0) }
                            Text(
                                text = "TPU",
                                style = MaterialTheme.typography.displayMedium,
                                color = Primary,
                                modifier = Modifier.clickable {
                                    rippled.value += 1
                                    if(rippled.value > 5) {
                                        SessionManager(context).setDebugMode(!UserStore.debug)
                                        UserStore.debug = !UserStore.debug
                                        Toast.makeText(context, "Debug mode: ${UserStore.debug}", Toast.LENGTH_SHORT).show()
                                        rippled.value = 0
                                    }
                                }
                            )
                            Text(
                                text = "Mobile Beta ${BuildConfig.BETA_VERSION}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Primary
                            )
                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            )
                            Text(
                                text = "Product name: TPUvNATIVE (android_kotlin)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Version: ${
                                    BuildConfig.VERSION_NAME
                                }", style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Build type: ${
                                    BuildConfig.BUILD_TYPE.uppercase()
                                }", style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "TPU Server: ${
                                    BuildConfig.SERVER_URL
                                }", style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "App ID: ${
                                    BuildConfig.APPLICATION_ID
                                }", style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Build time: ${
                                    BuildConfig.BUILD_TIME
                                }", style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

class SettingsViewModel : ViewModel()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit = {}, trailingContent: @Composable () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp).padding(start = 4.dp)
            )
            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.bodyMedium)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                trailingContent()
            }
        }

    }
}