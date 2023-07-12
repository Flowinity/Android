package com.troplo.privateuploader.components.chat

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.imageLoader
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.chat.dialogs.ImageDialog
import com.troplo.privateuploader.data.model.Embed
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Embed(embed: Embed) {
    if (embed.data != null) {
        val url =
            if (embed.data.type == "TPU_DIRECT") embed.data.url else "https://" + UserStore.getUser()?.domain?.domain + embed.data.url
        when (embed.type) {
            "openGraph" -> {
                Card {
                    Column {
                        Text(
                            text = embed.data.siteName.orEmpty(),
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = embed.data.title.orEmpty(),
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = embed.data.description.orEmpty(),
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            "image" -> {
                val expand = remember { mutableStateOf(false) }
                if (expand.value) {
                    ImageDialog(url ?: "", embed.data.upload?.name ?: "unknown.png", expand)
                }
                Image(
                    contentDescription = "Embed image (no alt text)",
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .dispatcher(Dispatchers.IO)
                            .data(data = url)
                            .apply(block = fun ImageRequest.Builder.() {
                                size(Size.ORIGINAL)
                            }).build(), imageLoader = imageLoader(LocalContext.current, false)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (embed.data.height!! > 300) 300.dp else embed.data.height.dp)
                        .clickable {
                            expand.value = true
                        }
                )

            }

            "file" -> {
                val context = LocalContext.current
                Card {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.InsertDriveFile,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = embed.data.upload?.name.orEmpty(),
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Text(
                                text = TpuFunctions.fileSize(embed.data.upload?.fileSize),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.weight(0.1f))
                            IconButton(
                                onClick = {
                                    val attachment = embed.data.upload?.attachment
                                    if (attachment != null) {
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.data =
                                            Uri.parse("https://${UserStore.getUser()?.domain?.domain}/i/${attachment}?force=true")
                                        context.startActivity(intent)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }

            else -> {
                Card(
                    modifier = Modifier.width(300.dp).heightIn(0.dp, 200.dp)
                ) {
                    Text(
                        text = "The version of TPUvNATIVE you are using does not yet support the embed type ${embed.type}!",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    } else {
        Card(
            modifier = Modifier.width(300.dp).heightIn(0.dp, 200.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "This embed cannot be loaded.", modifier = Modifier.padding(16.dp))
            }
        }
    }
}