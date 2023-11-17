package com.troplo.privateuploader.components.chat.dialogs

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.troplo.privateuploader.api.imageLoader
import com.troplo.privateuploader.components.core.InteractionDialog
import com.troplo.privateuploader.components.core.ZoomableBox
import kotlinx.coroutines.Dispatchers


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDialog(url: String, name: String, open: MutableState<Boolean>) {
    val context = LocalContext.current
    InteractionDialog(
        open = open,
        content = {
            ZoomableBox {
                GlideImage(
                    imageModel = { url },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.FillWidth
                    ),
                    modifier = Modifier.fillMaxSize().graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    ),
                )
            }
        },
        button = {},
        header = {
            TopAppBar(
                title = { Text(text = name) },
                navigationIcon = {
                    IconButton(onClick = { open.value = false }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = url.toUri()
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Filled.Launch, contentDescription = "Open in browser")
                    }
                }
            )
        }
    )
}