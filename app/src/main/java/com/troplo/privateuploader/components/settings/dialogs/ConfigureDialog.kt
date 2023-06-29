package com.troplo.privateuploader.components.settings.dialogs

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.FriendStore
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.core.InteractionDialog
import com.troplo.privateuploader.components.core.LoadingButton
import com.troplo.privateuploader.data.model.PatchUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureDialog(open: MutableState<Boolean>, key: String, name: String) {
    val context = LocalContext.current
    val input = remember { mutableStateOf("") }
    val currentPassword = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val viewModel = remember { ConfigureViewModel() }

    if(key == "username") {
        input.value = UserStore.user.value?.username ?: ""
    } else if(key == "email") {
        input.value = UserStore.user.value?.email ?: ""
    }

    InteractionDialog(
        header = {
            TopAppBar(
                title = {
                    Text("Change your $name")
                },
                navigationIcon = {
                    IconButton(onClick = { open.value = false }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Close")
                    }
                }
            )
        },
        button = {
            LoadingButton(
                onClick = {
                    viewModel.changeSettings(key, input.value, currentPassword.value, open, confirmPassword.value, context)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = input.value.isNotEmpty() && currentPassword.value.isNotEmpty(),
                text = "Change $name",
                loading = viewModel.loading.value
            )
        },
        open = open,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    label = {
                        Text("New $name")
                    },
                    onValueChange = { input.value = it },
                    value = input.value
                )
                if(key == "password") {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        label = {
                            Text("Confirm $name")
                        },
                        onValueChange = { confirmPassword.value = it },
                        value = confirmPassword.value
                    )
                }
                OutlinedTextField(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    label = {
                        Text("Current password")
                    },
                    onValueChange = { currentPassword.value = it },
                    value = currentPassword.value
                )
            }
        }
    )
}

class ConfigureViewModel : ViewModel() {
    val loading = mutableStateOf(false)

    fun changeSettings(key: String, value: String, currentPassword: String?, open: MutableState<Boolean>, confirmPassword: String?, context: Context) {
        if(key == "password" && value != confirmPassword) {
            Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
            return
        }
        val patchUser = PatchUser(
            currentPassword = if(currentPassword != null && currentPassword !== "") currentPassword else null,
            username = if(key == "username") value else null,
            email = if(key == "email") value else null,
            password = if(key == "password") value else null
        )
        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            val response = TpuApi.retrofitService.updateUser(patchUser).execute()
            if(response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    loading.value = false
                    open.value = false
                    Toast.makeText(context, "Updated $key", Toast.LENGTH_SHORT).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    loading.value = false
                }
            }
        }
    }
}