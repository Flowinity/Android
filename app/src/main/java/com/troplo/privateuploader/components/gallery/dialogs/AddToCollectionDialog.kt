package com.troplo.privateuploader.components.gallery.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextInputService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.CollectionStore
import com.troplo.privateuploader.components.core.LoadingButton
import com.troplo.privateuploader.data.model.Upload
import com.troplo.privateuploader.data.model.Collection
import com.troplo.privateuploader.data.model.CollectivizeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToCollectionDialog(open: MutableState<Boolean>, item: Upload) {
    val options = CollectionStore.collections.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }
    var selectedOptionId by remember { mutableIntStateOf(0) }
    val viewModel = remember { AddToCollectionViewModel() }

    AlertDialog(
        onDismissRequest = {
            open.value = false
        },
        title = {
            Text(text = "Add to collection")
        },
        text = {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                CompositionLocalProvider(
                    LocalTextInputService provides null
                ) {
                    TextField(
                        // The `menuAnchor` modifier must be passed to the text field for correctness.
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        value = selectedOptionText,
                        onValueChange = {},
                        label = { Text("Select a Collection") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        options.value.items.forEach { collection ->
                            DropdownMenuItem(
                                text = { Text(collection.name) },
                                onClick = {
                                    selectedOptionText = collection.name
                                    selectedOptionId = collection.id
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            LoadingButton(
                text = "Add",
                loading = viewModel.loading.value,
                onClick = {
                    viewModel.addToCollection(item, selectedOptionId, open)
                },
                enabled = selectedOptionId != 0,
                type = "text"
            )
        },
        dismissButton = {
            TextButton(
                onClick = {
                    open.value = false
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

class AddToCollectionViewModel: ViewModel() {
    val loading = mutableStateOf(false)

    fun addToCollection(item: Upload, collectionId: Int, open: MutableState<Boolean>) {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.collectivize(CollectivizeRequest(
                item.id,
                collectionId
            )).execute()
            if (response.isSuccessful) {
                loading.value = false
                open.value = false
            }
        }
    }
}
