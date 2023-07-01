package com.troplo.privateuploader.components.settings.dialogs

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.core.InteractionDialog
import com.troplo.privateuploader.components.core.LoadingButton
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.components.user.UserBanner
import com.troplo.privateuploader.data.model.PatchUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun StatusDialog(open: MutableState<Boolean> = mutableStateOf(true)) {
    val windowInsets = WindowInsets(0)
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = { open.value = false },
        sheetState = bottomSheetState,
        windowInsets = windowInsets,
        dragHandle = { }
    ) {
        ListItem(
            headlineContent =  {
                Row {
                    Text(text = "Online")
                }
            }
        )
        Divider()
        ListItem(
            headlineContent =  {
                Row {
                    Text(text = "Do not Disturb")
                }
            }
        )
        Divider()
        ListItem(
            headlineContent =  {
                Row {
                    Text(text = "Idle")
                }
            }
        )
        Divider()
        ListItem(
            headlineContent =  {
                Row {
                    Text(text = "Invisible")
                }
            }
        )
    }
}

class StatusViewModel : ViewModel() {
    val loading = mutableStateOf(false)

    fun changeSettings(
        key: String,
        value: String,
        currentPassword: String?,
        open: MutableState<Boolean>,
        confirmPassword: String?,
        context: Context,
    ) {
        if (key == "password" && value != confirmPassword) {
            Toast.makeText(context, "Passwords don't match", Toast.LENGTH_SHORT).show()
            return
        }
        val patchUser = PatchUser(
            currentPassword = if (currentPassword != null && currentPassword !== "") currentPassword else null,
            username = if (key == "username") value else null,
            email = if (key == "email") value else null,
            password = if (key == "password") value else null
        )
        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            val response = TpuApi.retrofitService.updateUser(patchUser).execute()
            if (response.isSuccessful) {
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