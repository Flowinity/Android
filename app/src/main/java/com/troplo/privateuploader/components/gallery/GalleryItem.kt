package com.troplo.privateuploader.components.gallery

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.imageLoader
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.data.model.Collection
import com.troplo.privateuploader.data.model.Upload
import com.troplo.privateuploader.data.model.defaultUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
@Preview
fun GalleryItem(@PreviewParameter(SampleUploadProvider::class) item: Upload) {
    val itemStarred = remember { mutableStateOf(item.starred) }

    Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(4.dp)
          .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
          ) {},
        onClick = {
            //
        },
    ) {
        Column {
            TopAppBar(
                title = { Text(text = item.attachment) },
                Modifier.background(color = Color.DarkGray)
            )
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                if (item.type == "image") {
                    Image(
                        contentDescription = item.name,
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(LocalContext.current)
                                .data(data = TpuFunctions.image(item.attachment, null))
                                .apply(block = fun ImageRequest.Builder.() {
                                    size(Size.ORIGINAL)
                                }).build(), imageLoader = imageLoader(LocalContext.current)
                        ),
                        modifier = Modifier
                            .heightIn(min = 0.dp, max = 200.dp)
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
            Column(modifier = Modifier.padding(start = 16.dp)) {
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
                Text(text = "File size: ${item.fileSize}")
            }
            Row(modifier = Modifier.padding(start = 16.dp)) {
                item.collections.forEach { collection ->
                    // Chip next to each other
                    SuggestionChip(
                        modifier = Modifier.padding(end = 4.dp),
                        onClick = {
                            // Open collection
                        },
                        label = {
                            Text(text = collection.name)
                        }
                    )
                }
            }
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilledTonalIconButton(
                    onClick = { /* Do something! */ },
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

class SampleUploadProvider : PreviewParameterProvider<Upload> {
    override val values: Sequence<Upload>
        get() = sequenceOf(
            Upload(
                id = 1,
                name = "Test",
                attachment = "aaa.png",
                type = "imagse",
                originalFilename = "Test.png",
                createdAt = "2021-08-01T00:00:00.000Z",
                fileSize = 1000,
                collections = listOf(
                    Collection(
                        id = 1,
                        name = "Test"
                    ),
                    Collection(
                        id = 2,
                        name = "Test2"
                    ),
                    Collection(
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
