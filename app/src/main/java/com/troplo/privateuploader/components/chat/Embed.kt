package com.troplo.privateuploader.components.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.data.model.Embed
import com.troplo.privateuploader.data.model.Upload

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Embed(embed: Embed) {
    if (embed.data != null) {
        when (embed.type) {
            "openGraph" -> {
                Card {
                    Column {
                        Text(
                            text = embed.data?.siteName.orEmpty(),
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = embed.data?.title.orEmpty(),
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = embed.data?.description.orEmpty(),
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            "image" -> {
                // TODO: Replace with custom instance link
                GlideImage(model = "https://privateuploader.com" + embed.data.url, contentDescription = embed.data.description, modifier = Modifier.heightIn(200.dp, 500.dp).fillMaxWidth())
            }
            "file" -> {
                Card(modifier = Modifier.height(500.dp)) {
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
                                text = embed.data?.upload?.name.orEmpty(),
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp)
                        ) {
                            Text(
                                text = embed.data.upload?.fileSize.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.weight(0.1f))
                            IconButton(
                                onClick = {
                                    val attachment = embed.data?.upload?.attachment
                                    if (attachment != null) {
                                        val url = TpuFunctions.image(attachment, null)
                                        // Open the URL in a browser
                                        // (you can use an appropriate library or API for this)
                                    }
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null)
                            }
                        }
                    }
                }
            }
            "video" -> {
                Card {
                    Box(modifier = Modifier.aspectRatio(16f / 9f)) {
                        // TODO: Replace with custom instance link, add VideoPlayer
                           GlideImage(model = TpuFunctions.image("https://privateuploader.com" + embed.data.url, null), contentDescription = embed.data.description, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            else -> {
                Card {
                    Text(
                        text = "You must upgrade your version of TPUvNATIVE to see the embed type ${embed.type}!",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    } else {
        Card(
            modifier = Modifier.width(300.dp)
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