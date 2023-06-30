package com.troplo.privateuploader.components.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GifBox
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
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
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.chat.attachment.MyDevice
import com.troplo.privateuploader.data.model.Upload
import com.troplo.privateuploader.data.model.UploadTarget
import com.troplo.privateuploader.screens.GalleryScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Attachment(openBottomSheet: MutableState<Boolean>) {
    val windowInsets = WindowInsets(0)
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val selectedTab: MutableState<Int> = remember { mutableIntStateOf(0) }
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
                onClick = { selectedTab.value = 0 },
                text = { Text("My Device") },
                icon = { Icon(Icons.Default.Smartphone, contentDescription = "My Device") }
            )
            Tab(
                selected = selectedTab.value == 1,
                onClick = { selectedTab.value = 1 },
                text = { Text("Gallery") },
                icon = { Icon(Icons.Default.Image, contentDescription = "Gallery") }
            )
            Tab(
                selected = selectedTab.value == 2,
                onClick = { selectedTab.value = 2 },
                text = { Text("Starred") },
                icon = { Icon(Icons.Default.Star, contentDescription = "Starred") }
            )
            Tab(
                selected = selectedTab.value == 3,
                onClick = { selectedTab.value = 3 },
                text = { Text("GIFs") },
                icon = { Icon(Icons.Default.GifBox, contentDescription = "GIFs") }
            )
        }

        fun onClick(upload: Upload, tenor: Boolean = false) {
            openBottomSheet.value = false
            ChatStore.attachmentsToUpload.add(
                UploadTarget(
                    uri = if (tenor) {
                        upload.attachment.toUri()
                    } else {
                        "https://${UserStore.user.value?.domain?.domain}/i/${upload.attachment}".toUri()
                    },
                    started = true,
                    progress = 100f,
                    url = upload.attachment
                )
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
                    GalleryScreen("gallery", true, onClick = {
                        onClick(it)
                    })
                }

                2 -> {
                    GalleryScreen("starred", true, onClick = {
                        onClick(it)
                    })
                }

                3 -> {
                    GalleryScreen("tenor", true, onClick = {
                        onClick(it, true)
                    })
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