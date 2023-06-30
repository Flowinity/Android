package com.troplo.privateuploader.components.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GifBox
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.troplo.privateuploader.components.chat.attachment.MyDevice
import com.troplo.privateuploader.screens.GalleryScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Attachment(openBottomSheet: MutableState<Boolean>) {
    val windowInsets = WindowInsets(0)
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val selectedTab = remember { mutableIntStateOf(0) }
    ModalBottomSheet(
        onDismissRequest = { openBottomSheet.value = false },
        sheetState = bottomSheetState,
        windowInsets = windowInsets,
        modifier = Modifier.defaultMinSize(minHeight = 400.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab.value,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Tab(
                selected = selectedTab.value == 0,
                onClick = { openBottomSheet.value = true },
                text = { Text("My Device") },
                icon = { Icon(Icons.Default.Smartphone, contentDescription = null) }
            )
            Tab(
                selected = selectedTab.value == 1,
                onClick = { openBottomSheet.value = true },
                text = { Text("Gallery") },
                icon = { Icon(Icons.Default.Image, contentDescription = null) }
            )
            Tab(
                selected = selectedTab.value == 2,
                onClick = { openBottomSheet.value = true },
                text = { Text("Starred") },
                icon = { Icon(Icons.Default.Image, contentDescription = null) }
            )
            Tab(
                selected = selectedTab.value == 3,
                onClick = { openBottomSheet.value = true },
                text = { Text("GIFs") },
                icon = { Icon(Icons.Default.GifBox, contentDescription = null) }
            )
        }
        Box(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
        ) {
            when (selectedTab.value) {
                0 -> {
                    MyDevice()
                }

                1 -> {
                    GalleryScreen()
                }

                2 -> {
                    GalleryScreen()
                }

                3 -> {
                    GalleryScreen()
                }
            }
        }
    }
}

@Preview
@Composable
fun AttachmentPreview() {
    Attachment(openBottomSheet = remember { mutableStateOf(true) })
}

class AttachmentViewModel : ViewModel()