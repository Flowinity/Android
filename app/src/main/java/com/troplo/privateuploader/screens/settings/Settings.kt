package com.troplo.privateuploader.screens.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troplo.privateuploader.BuildConfig
import com.troplo.privateuploader.R
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.components.core.dialogs.DeleteConfirmDialog
import com.troplo.privateuploader.components.settings.dialogs.StatusDialog
import com.troplo.privateuploader.components.user.UserBanner
import com.troplo.privateuploader.ui.theme.Primary

@Composable
@Preview
fun SettingsScreen(
    navigate: (String) -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current
        val user = UserStore.user.collectAsState()
        val status = remember { mutableStateOf(false) }
        val logout = remember { mutableStateOf(false) }
        if (status.value) {
            StatusDialog(status)
        }
        if (logout.value) {
            DeleteConfirmDialog(
                open = logout,
                onConfirm = {
                    UserStore.logout(context)
                    navigate("login")
                },
                title = "",
                name = "",
                terminology = "Logout",
                important = false,
                message = "Are you sure you want to logout?"
            )
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = { paddingValues ->
                Column(
                    modifier = Modifier.padding(top = paddingValues.calculateTopPadding() + 8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        item {
                            UserBanner()
                            SettingsItem(
                                content = {
                                    Row(
                                        modifier = Modifier
                                            .padding(8.dp),
                                    ) {
                                        UserAvatar(
                                            avatar = user.value?.avatar,
                                            username = user.value?.username ?: "Deleted User"
                                        )
                                        Column(
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .align(Alignment.CenterVertically)
                                        ) {
                                            Text(
                                                text = user.value?.username ?: "Deleted User",
                                                style = MaterialTheme.typography.bodyLarge,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                            Text(
                                                "Click here to set your status.",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                },
                                title = "Account",
                                icon = null,
                                subtitle = null,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    status.value = true
                                }
                            )
                        }

                        item {
                            SettingsItem(
                                Icons.Default.AccountBox,
                                "My Flowinity",
                                "Change your username, email, and password.",
                                onClick = { navigate("settings/account") })
                        }

                        item {
                            SettingsItem(
                                Icons.Default.Palette,
                                "Preferences",
                                "Change your theme, language, and more.",
                                onClick = { navigate("settings/preferences") })
                        }

                        if (UserStore.debug) {
                            item {
                                SettingsItem(
                                    Icons.Default.Upload,
                                    "Auto-Upload",
                                    "Options to automatically upload to Flowinity.",
                                    onClick = { navigate("settings/upload") }
                                )
                            }
                        }

                        item {
                            SettingsItem(
                                Icons.Default.Collections,
                                "Collections",
                                "Create, edit, and delete Collections.",
                                onClick = { navigate("settings/collections") }
                            )
                        }

                        item {
                            SettingsItem(
                                Icons.Default.MoreTime,
                                "Changelog",
                                "Recent updates to Flowinity Mobile.",
                                onClick = { navigate("settings/changelog") }
                            )
                        }

                        item {
                            SettingsItem(
                                Icons.Default.OpenInBrowser,
                                "Can't find what you're looking for?",
                                "Visit Flowinity on the web.",
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://flowinity.com")
                                    )
                                    context.startActivity(intent)
                                }
                            )
                        }

                        item {
                            SettingsItem(
                                Icons.Default.Logout,
                                "Logout",
                                "Logout of Flowinity.",
                                onClick = {
                                    logout.value = true
                                }
                            )
                        }

                        if (UserStore.debug) {
                            item {
                                SettingsItem(
                                    Icons.Default.DeviceUnknown,
                                    "Re-attempt device registration",
                                    "Re-attempt device registration with Flowinity Firebase CM",
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
                                        val rippled: MutableState<Int> =
                                            remember { mutableIntStateOf(0) }
                                        Image(
                                            painter = painterResource(id = R.drawable.flowinity_full),
                                            contentDescription = "Flowinity Logo",
                                            modifier = Modifier
                                                .width(250.dp),
                                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                                        )
                                        Text(
                                            text = "Mobile Early Access",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
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
                                            text = "Flowinity Server: ${
                                                TpuApi.instance
                                            }", style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "App ID: ${
                                                BuildConfig.APPLICATION_ID
                                            }", style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "Build date: ${
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
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(
    icon: ImageVector?,
    title: String,
    subtitle: String?,
    onClick: () -> Unit = {},
    trailingContent: @Composable () -> Unit = {},
    iconComposable: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: CardColors = CardDefaults.cardColors(),
    content: @Composable() (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .then(modifier),
        enabled = enabled,
        colors = colors
    ) {
        if (content == null) {
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (iconComposable == null && icon != null) {
                    Icon(
                        icon,
                        contentDescription = title,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(start = 4.dp)
                    )
                } else if (iconComposable != null) {
                    iconComposable()
                }
                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitle != null) Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
        } else {
            content()
        }
    }
}