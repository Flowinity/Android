package com.troplo.privateuploader.components.chat

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.stores.CoreStore
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.chat.dialogs.ImageDialog
import com.troplo.privateuploader.data.model.Embed
import com.troplo.privateuploader.data.model.EmbedDataV2
import com.troplo.privateuploader.data.model.EmbedMedia
import com.troplo.privateuploader.data.model.EmbedMediaType

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Embed(embed: EmbedDataV2) {
    if (embed.metadata != null) {
        val context = LocalContext.current
        val coreStore = CoreStore.core.value
        if (embed.metadata.siteName?.isNotEmpty() == true) {
            Card {
                Column {
                    Text(
                        text = embed.metadata.siteName.orEmpty(),
                        modifier = Modifier.padding(top = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                    EmbedText(embed)
                }
            }
        }

            if (embed.metadata.siteName?.isEmpty() == true) {
                Card {
                    Column {
                        EmbedText(embed)
                    }
                }
            }

            for (media in embed.media ?: emptyList()) {
                if (media.type == 0) {
                    val url =
                        if (!media.isInternal) "https://${coreStore?.domain ?: "https://flowinity.com/"}${media.proxyUrl}" else "https://" + UserStore.getUser()?.domain?.domain + "/i/" + media.attachment
                    val expand = remember { mutableStateOf(false) }
                    if (expand.value) {
                        ImageDialog(
                            url ?: "",
                            media.upload?.name ?: media.attachment ?: media.url ?: "unknown.png",
                            expand
                        )
                    }

                    GlideImage(
                        imageModel = { url },
                        modifier = Modifier
                            .fillMaxSize()
                            .height(if (media.height!! > 300) 300.dp else media.height.dp)
                            .clickable {
                                expand.value = true
                            },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.FillWidth
                        )
                    )
                } else if ((media.type == 3 || media.type == 1) && media.upload != null) {
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
                                    text = media.upload.name,
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 16.dp)
                            ) {
                                Text(
                                    text = TpuFunctions.fileSize(media.upload.fileSize),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.weight(0.1f))
                                IconButton(
                                    onClick = {
                                        val attachment = media.upload.attachment
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.data =
                                            Uri.parse("https://${UserStore.getUser()?.domain?.domain}/i/${attachment}?force=true")
                                        context.startActivity(intent)
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
                } else if(media.type == 3 || media.type == 1) {
                    Card(
                        modifier = Modifier
                            .width(300.dp)
                            .heightIn(0.dp, 200.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "This file has been deleted by the owner.",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .width(300.dp)
                            .heightIn(0.dp, 200.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "This embed cannot be loaded.\n\nType: ${media.type}\n\nURL: ${media.url}",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .heightIn(0.dp, 200.dp)
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