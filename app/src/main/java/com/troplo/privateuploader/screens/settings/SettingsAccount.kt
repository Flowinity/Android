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
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import com.troplo.privateuploader.components.settings.dialogs.ConfigureDialog
import com.troplo.privateuploader.components.user.UserBanner
import com.troplo.privateuploader.ui.theme.Primary

@Composable
@Preview
fun SettingsAccountScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current
        val dialog = remember { mutableStateOf(false) }
        val selected = remember { mutableStateOf("") }
        val selectedName = remember { mutableStateOf("") }

        if(dialog.value) {
            ConfigureDialog(open = dialog, key = selected.value, name = selectedName.value)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            item {
                SettingsItem(
                    Icons.Default.Person,
                    "Change username",
                    UserStore.user.value?.username ?: "Change username",
                    trailingContent = {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Change username")
                    },
                    onClick = {
                        selected.value = "username"
                        selectedName.value = "Username"
                        dialog.value = true
                    }
                )
            }

            item {
                SettingsItem(
                    Icons.Default.Email,
                    "Change email",
                    UserStore.user.value?.email ?: "Change email",
                    trailingContent = {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Change email")
                    },
                    onClick = {
                        selected.value = "email"
                        selectedName.value = "email"
                        dialog.value = true
                    }
                )
            }

            item {
                SettingsItem(
                    Icons.Default.Password,
                    "Change password",
                    "Change your TPU password",
                    trailingContent = {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Change password")
                    },
                    onClick = {
                        selected.value = "password"
                        selectedName.value = "password"
                        dialog.value = true
                    }
                )
            }
        }
    }
}

class SettingsAccountViewModel : ViewModel()