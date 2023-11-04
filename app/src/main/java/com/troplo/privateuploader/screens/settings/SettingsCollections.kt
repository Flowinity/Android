package com.troplo.privateuploader.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.troplo.privateuploader.api.stores.CollectionStore
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.components.settings.dialogs.CreateCollectionDialog

@Composable
@Preview
fun SettingsCollectionsScreen(navigate: (String) -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        val collections = CollectionStore.collections.collectAsState()
        var createCollection = remember { mutableStateOf(false) }

        if(createCollection.value) {
            CreateCollectionDialog(createCollection)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            item {
                SettingsItem(
                    null,
                    "Create a new collection",
                    "Collections are a way to organize and share files.",
                    trailingContent = {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Create")
                    },
                    iconComposable = {
                        Icon(Icons.Default.Collections, contentDescription = "Create", modifier = Modifier.padding(end = 6.dp, start = 7.dp))
                    },
                    onClick = {
                        createCollection.value = true
                    }
                )
            }

            collections.value.items.forEach { collection ->
                item(
                    key = collection.id
                ) {
                    val subtitle = if(collection.shared) {
                        "Shared to you by ${collection.user.username}"
                    } else if(collection.users.isNotEmpty()) {
                        "Shared with ${collection.users.size} other users"
                    } else if(collection.shareLink != null) {
                        "ShareLink"
                    } else {
                        "Private"
                    }

                    SettingsItem(
                        null,
                        collection.name,
                        subtitle,
                        onClick = {
                            navigate("settings/collection/${collection.id}")
                        },
                        enabled = collection.permissionsMetadata?.configure == true,
                        trailingContent = {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Edit collection")
                        },
                        iconComposable = {
                            UserAvatar(avatar = collection.image ?: collection.preview?.attachment?.attachment, username = collection.name, showStatus = false)
                        }
                    )
                }
            }
        }
    }
}