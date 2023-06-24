package com.troplo.privateuploader.components.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.data.model.Upload
import com.troplo.privateuploader.data.model.User
import com.troplo.privateuploader.data.model.Collection
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
@Preview
fun GalleryItem(@PreviewParameter(
    SampleUploadProvider::class
) item: Upload) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        onClick = {
            //
        }
    ) {
        Column {
            TopAppBar(
                title = { Text(text = item.attachment) },
                Modifier.background(color = Color.DarkGray)
            )
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                GlideImage(
                    model = TpuFunctions.image(item.attachment, null),
                    contentDescription = item.name
                )
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
            Column(modifier = Modifier.padding(start = 16.dp)) {
                    item.collections.forEach { collection ->
                        AssistChip(
                            onClick = {
                                // Open collection
                            },
                            label =  {
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
                FilledTonalButton(
                    modifier = Modifier.padding(end = 4.dp),
                    onClick = {
                        // Open collection
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(244, 67, 54, 30)
                    ),
                    content = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.padding(end = 4.dp),
                            tint = Color(244, 67, 54)
                        )
                    }
                )

                FilledTonalButton(
                    modifier = Modifier.padding(end = 4.dp),
                    onClick = {
                        // Open collection
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0, 150, 136, 30)
                    ),
                    content = {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Link",
                            modifier = Modifier.padding(end = 4.dp),
                            tint = Color(0, 150, 136, 255)
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
                type = "image",
                originalFilename = "Test.png",
                createdAt = Date("2021-08-01T00:00:00.000Z"),
                fileSize = 1000,
                collections = listOf(
                    Collection(
                        id = 1,
                        name = "Test"
                    )
                ),
                deletable = true,
                data = null,
                userId = 1,
                starred = false,
                textMetadata = "",
                updatedAt = "2021-08-01T00:00:00.000Z",
                urlRedirect = null,
                user = User(
                    id = 1,
                    username = "Test",
                    avatar = "a.png",
                    banner = "b.png",
                )
            )
        )
}
