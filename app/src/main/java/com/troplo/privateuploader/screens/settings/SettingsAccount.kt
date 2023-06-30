package com.troplo.privateuploader.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.settings.dialogs.ConfigureDialog

@Composable
@Preview
fun SettingsAccountScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current
        val dialog = remember { mutableStateOf(false) }
        val selected = remember { mutableStateOf("") }
        val selectedName = remember { mutableStateOf("") }

        if (dialog.value) {
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