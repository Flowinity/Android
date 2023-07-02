package com.troplo.privateuploader.components.core.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
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
import com.troplo.privateuploader.components.core.LoadingButton

@Composable
fun DeleteConfirmDialog(open: MutableState<Boolean>, onConfirm: () -> Unit, title: String, name: String?, important: Boolean = false, loading: MutableState<Boolean> = mutableStateOf(false), terminology: String = "Delete", message: String? = null) {
    val confirm = remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {
            open.value = false
        },
        title = {
            Text(text = if(title == "") "$terminology?" else "$terminology $title?")
        },
        text = {
            Column {
                Text(text = message ?: "Are you sure you want to ${terminology.lowercase()} the $title? This is irreversible!")
                if (important) {
                    Text("Please type \"$name\" to confirm.")
                    OutlinedTextField(
                        value = confirm.value,
                        onValueChange = { confirm.value = it },
                        label = { Text("Confirmation text") })
                }
            }
        },
        confirmButton = {
            LoadingButton(
                text = terminology,
                loading = loading.value,
                onClick = onConfirm,
                enabled = !important || confirm.value == name,
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