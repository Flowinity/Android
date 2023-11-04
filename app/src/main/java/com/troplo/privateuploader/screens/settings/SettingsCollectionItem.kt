package com.troplo.privateuploader.screens.settings

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.CollectionStore
import com.troplo.privateuploader.components.core.AnyItem
import com.troplo.privateuploader.components.core.HyperlinkText
import com.troplo.privateuploader.components.core.Select
import com.troplo.privateuploader.components.core.dialogs.DeleteConfirmDialog
import com.troplo.privateuploader.components.settings.dialogs.RenameCollectionDialog
import com.troplo.privateuploader.components.user.UserBanner
import com.troplo.privateuploader.data.model.Collection
import com.troplo.privateuploader.data.model.ShareCollectionRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun SettingsCollectionItemScreen(id: Int? = 0, navigate: (String) -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize()) {
        val viewModel = remember { SettingsCollectionItemViewModel() }
        val collections = CollectionStore.collections.collectAsState()
        val collection = remember { mutableStateOf(collections.value.items.find { it.id == id }) }
        val privacyOption: MutableState<Int> = remember { mutableIntStateOf(
            if(collection.value?.shareLink != null) 1 else 0
        )}
        val privacyText = remember { mutableStateOf(
            if(collection.value?.shareLink != null) "On" else "Off"
        )}
        val visibleShareLink: MutableState<String?> = remember { mutableStateOf(collection.value?.shareLink) }
        val privacyOptions = listOf(
            AnyItem(
                id = 0,
                name = "Nobody"
            ),
            AnyItem(
                id = 1,
                name = "Anyone with the link"
            )
        )

        val renameCollection = remember { mutableStateOf(false) }
        val deleteCollection = remember { mutableStateOf(false) }

        if(renameCollection.value && id !== null) {
            RenameCollectionDialog(open = renameCollection, collectionId = id)
        }

        if(deleteCollection.value && id !== null) {
            DeleteConfirmDialog(
                open = deleteCollection,
                onConfirm = {
                    viewModel.deleteCollection(id, deleteCollection, navigate)
                },
                title = "Collection",
                name = collection.value?.name ?: "",
                important = true,
                loading = viewModel.loading
            )
        }

        if(collection.value == null) {
            Text("Collection not found")
            return
        }

        Scaffold(
            topBar = {
                UserBanner(collection.value?.image ?: collection.value?.preview?.attachment?.attachment)
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding() + 8.dp)
            ) {
                Column {
                    SettingsItem(
                        icon = Icons.Default.DriveFileRenameOutline,
                        title = "Rename collection",
                        subtitle = "Change the name of this collection",
                        onClick = {
                            renameCollection.value = true
                        }
                    )

                    Card(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Select(
                            selectedText = privacyText,
                            selectedId = privacyOption,
                            items = privacyOptions.toMutableList(),
                            onSelected = {
                                viewModel.updatePrivacy(
                                    id,
                                    privacyOption.value,
                                    collection,
                                    visibleShareLink
                                )
                            },
                            label = "Public accessibility"
                        )
                        Box(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            if(visibleShareLink.value != null) {
                                HyperlinkText(
                                    fullText = "ShareLink: https://privateuploader.com/collections/${visibleShareLink.value}",
                                    hyperLinks = mutableMapOf(
                                        "https://privateuploader.com/collections/${visibleShareLink.value}" to "https://privateuploader.com/collections/${visibleShareLink.value}"
                                    ),
                                )
                            } else {
                                Text("ShareLink: Disabled")
                            }
                        }
                    }

                    SettingsItem(
                        icon = Icons.Default.DeleteForever,
                        title = "Delete collection?",
                        subtitle = "This will not delete the files in the collection.",
                        onClick = {
                            deleteCollection.value = true
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    )
                    Text("More Collection settings are coming soon to PrivateUploader Mobile! You can see more settings on the web app.", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

class SettingsCollectionItemViewModel : ViewModel() {
    val loading = mutableStateOf(false)

    fun updatePrivacy(id: Int?, privacy: Int, collection: MutableState<Collection?>, visibleShareLink: MutableState<String?>) {
        if(collection.value == null || id == null) return
        val value = if(privacy == 0) "nobody" else "link"

        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.shareCollection(
                ShareCollectionRequest(
                    id = id,
                    type = value
                )
            ).execute()

            if(response.isSuccessful) {
                Log.d("SettingsCollectionItemViewModel", "updatePrivacy: ${response.body()?.shareLink}")
                collection.value?.shareLink = response.body()?.shareLink
                visibleShareLink.value = response.body()?.shareLink
                CollectionStore.initializeCollections()
            }
        }
    }

    fun deleteCollection(id: Int?, open: MutableState<Boolean>, navigate: (String) -> Unit = {}) {
        if(id == null) return
        loading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.deleteCollection(id).execute()

            if(response.isSuccessful) {
                CollectionStore.initializeCollections()
                open.value = false
                CoroutineScope(Dispatchers.Main).launch {
                    navigate("settings/collections")
                }
            }
            loading.value = false
        }
    }
}
