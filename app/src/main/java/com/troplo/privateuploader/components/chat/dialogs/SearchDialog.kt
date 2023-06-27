package com.troplo.privateuploader.components.chat.dialogs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDialog() {
    val content = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { },
        modifier = Modifier.fillMaxSize(),
        properties = DialogProperties()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TextField(value = content.value, onValueChange = { content.value = it })
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(top = it.calculateTopPadding())
            ) {
            }
        }
    }
}