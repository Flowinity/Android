package com.troplo.privateuploader.components.chat.attachment

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.imageLoader
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.data.model.UploadTarget
import kotlinx.coroutines.Dispatchers

@Composable
fun UriPreview(file: UploadTarget, onClick: () -> Unit) {
    val store = ChatStore.attachmentsToUpload.find { it.uri == file.uri }
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(0.dp, 140.dp)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .dispatcher(Dispatchers.IO)
                        .data(file.uri)
                        .apply(block = fun ImageRequest.Builder.() {
                            size(Size.ORIGINAL)
                        }).build(), imageLoader = imageLoader(LocalContext.current, false)
                ),
                contentDescription = "Image",
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .fillMaxSize()
            )
            if (store != null) {
                Box(
                    contentAlignment = androidx.compose.ui.Alignment.TopEnd,
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = Color.White
                    )
                }
            }
        }
    }
}