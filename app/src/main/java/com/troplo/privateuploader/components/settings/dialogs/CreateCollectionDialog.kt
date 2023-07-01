package com.troplo.privateuploader.components.settings.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextInputService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.CollectionStore
import com.troplo.privateuploader.components.core.LoadingButton
import com.troplo.privateuploader.data.model.CollectivizeRequest
import com.troplo.privateuploader.data.model.CreateCollectionRequest
import com.troplo.privateuploader.data.model.Upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCollectionDialog(
    open: MutableState<Boolean>
) {
    val name = remember { mutableStateOf("") }
    val viewModel = remember { CreateCollectionViewModel() }

    AlertDialog(
        onDismissRequest = {
            open.value = false
        },
        title = {
            Text(text = "Create new collection")
        },
        text = {
               OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Collection name") })
        },
        confirmButton = {
            LoadingButton(
                text = "Create",
                loading = viewModel.loading.value,
                onClick = {
                    viewModel.createCollection(name.value, open)
                },
                enabled = name.value.isNotEmpty(),
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

class CreateCollectionViewModel: ViewModel() {
    val loading = mutableStateOf(false)

    fun createCollection(name: String, open: MutableState<Boolean>) {
        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.createCollection(CreateCollectionRequest(name)).execute()
            if (response.isSuccessful) {
                CollectionStore.initializeCollections()
                open.value = false
            }
            loading.value = false
        }
    }
}
