package com.troplo.privateuploader.components.gallery

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.imageLoader
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.core.dialogs.DeleteConfirmDialog
import com.troplo.privateuploader.components.gallery.dialogs.AddToCollectionDialog
import com.troplo.privateuploader.data.model.PartialCollection
import com.troplo.privateuploader.data.model.Upload
import com.troplo.privateuploader.data.model.defaultUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class,
    ExperimentalLayoutApi::class
)
@Composable
@Preview
fun GalleryItem(
    @PreviewParameter(SampleUploadProvider::class) item: Upload,
    hide: Boolean = false,
    onClick: () -> Unit = {},
    onDelete: (Upload) -> Unit = {},
    selectedCollectionId: MutableState<Int> = mutableIntStateOf(0),
    selectedCollectionText: MutableState<String> = mutableStateOf(""),
) {
    val itemStarred = remember { mutableStateOf(item.starred) }
    val addToCollection = remember { mutableStateOf(false) }
    val deleteItem = remember { mutableStateOf(false) }
    val userId = UserStore.user.value?.id ?: 0

    if (addToCollection.value) {
        AddToCollectionDialog(open = addToCollection, item = item)
    }

    if(deleteItem.value) {
        DeleteConfirmDialog(open = deleteItem, onConfirm = {
            onDelete(item)
        }, title = "upload", name = item.name )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            },
    ) {
        Column {
            if (item.type !== "image-tenor") {
                TopAppBar(
                    title = {
                        Text(
                            text = item.name,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                )
            }
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                if (item.type == "image" || item.type == "image-tenor") {
                    val url = if (item.type == "image-tenor") {
                        item.attachment
                    } else {
                        TpuFunctions.image(item.attachment, null)
                    }
                    Image(
                        contentDescription = item.name,
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = url)
                                .apply(block = fun ImageRequest.Builder.() {}).build(),
                            imageLoader = imageLoader(LocalContext.current)
                        ),
                        modifier = Modifier
                            .requiredHeight(200.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.InsertDriveFile,
                        contentDescription = "File",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(16.dp)
                    )
                }
            }

            if (!hide) {
                Column(modifier = Modifier.padding(start = 16.dp, top = 4.dp)) {
                    if(item.userId != userId) {
                        Text("Uploaded by: ${item.user?.username ?: "Unknown"}")
                    }
                    Text(
                        text = "Type: " + when (item.type) {
                            "paste" -> "Legacy Paste"
                            else -> item.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        }
                    )
                    Text(text = "Original name: ${item.originalFilename}")
                    Text(text = "Uploaded name: ${item.attachment}")
                    Text(
                        text = "Created at: ${
                            TpuFunctions.formatDate(item.createdAt)
                        }"
                    )
                    Text(text = "Size: ${TpuFunctions.fileSize(item.fileSize)}")
                }
                FlowRow(modifier = Modifier.padding(start = 16.dp)) {
                    if(item.userId == userId) {
                        SuggestionChip(
                            modifier = Modifier.padding(end = 4.dp),
                            onClick = {
                                addToCollection.value = true
                            },
                            label = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add to collection"
                                )
                            }
                        )
                    }
                    item.collections.forEach { collection ->
                        SuggestionChip(
                            modifier = Modifier.padding(end = 4.dp),
                            onClick = {
                                  if(selectedCollectionId.value == collection.id) {
                                      selectedCollectionId.value = 0
                                      selectedCollectionText.value = ""
                                  } else {
                                      selectedCollectionId.value = collection.id
                                      selectedCollectionText.value = collection.name
                                  }
                            },
                            label = {
                                Text(text = collection.name)
                            }
                        )
                    }
                }
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if(item.userId == userId) {
                        FilledTonalIconButton(
                            onClick = {
                                deleteItem.value = true
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(255, 67, 54, 30)
                            ),

                            content = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(255, 67, 54, 255)
                                )
                            }
                        )
                    }
                    val clipboardManager =
                        LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    FilledTonalIconButton(
                        onClick = {
                            clipboardManager.setPrimaryClip(
                                ClipData.newPlainText(
                                    "attachment",
                                    "https://${UserStore.getUser()?.domain?.domain}/i/${item.attachment}"
                                )
                            )
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(0, 150, 136, 30)
                        ),

                        content = {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color(0, 150, 136, 255)
                            )
                        }
                    )
                    val context = LocalContext.current
                    FilledTonalIconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data =
                                Uri.parse("https://${UserStore.getUser()?.domain?.domain}/i/${item.attachment}?force=true")
                            context.startActivity(intent)
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(1, 144, 234, 30)
                        ),

                        content = {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download/Open",
                                tint = Color(1, 144, 234, 255)
                            )
                        }
                    )

                    if (item.type == "image" && item.textMetadata != "") {
                        FilledTonalIconButton(
                            onClick = {
                                clipboardManager.setPrimaryClip(
                                    ClipData.newPlainText(
                                        "ocr",
                                        item.textMetadata
                                    )
                                )
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(76, 175, 80, 30)
                            ),
                            content = {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Copy OCR",
                                    tint = Color(
                                        76,
                                        175,
                                        80,
                                        255
                                    )
                                )
                            }
                        )
                    }

                    // Star icon
                    FilledTonalIconButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val response = TpuApi.retrofitService.star(
                                    item.attachment
                                ).execute()
                                if (response.isSuccessful) {
                                    val starred = response.body()?.star
                                    itemStarred.value = starred
                                }
                            }
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color(255, 160, 0, 30)
                        ),
                        content = {
                            Icon(
                                imageVector = if (itemStarred.value != null) Icons.Default.Star else Icons.Outlined.StarOutline,
                                contentDescription = "Star",
                                tint = Color(255, 160, 0, 255)
                            )
                        }
                    )
                }
            }
        }

    }
}

class SampleUploadProvider : PreviewParameterProvider<Upload> {
    override val values: Sequence<Upload>
        get() = sequenceOf(
            Upload(
                id = 1,
                name = "Test",
                attachment = "aaa.png",
                type = "text",
                originalFilename = "Test.png",
                createdAt = "2021-08-01T00:00:00.000Z",
                fileSize = 1024,
                collections = listOf(
                    PartialCollection(
                        id = 1,
                        name = "Test"
                    ),
                    PartialCollection(
                        id = 2,
                        name = "Test2"
                    ),
                    PartialCollection(
                        id = 3,
                        name = "Test3"
                    )
                ),
                deletable = true,
                data = null,
                userId = 1,
                starred = null,
                textMetadata = "4",
                updatedAt = "2021-08-01T00:00:00.000Z",
                urlRedirect = null,
                user = defaultUser()
            )
        )
}
