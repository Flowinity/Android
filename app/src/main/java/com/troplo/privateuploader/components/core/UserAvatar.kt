package com.troplo.privateuploader.components.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.size.OriginalSize
import coil.size.Size
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.imageLoader
import kotlinx.coroutines.Dispatchers

@Composable
fun UserAvatar(
    avatar: String?,
    username: String,
    modifier: Modifier = Modifier,
) {
    if(avatar != null) {
        Image(
            contentDescription = "User profile picture",
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .dispatcher(Dispatchers.IO)
                    .data(data = TpuFunctions.image(avatar, null)).apply(block = fun ImageRequest.Builder.() {
                        size(Size.ORIGINAL)
                    }).build(), imageLoader = imageLoader(LocalContext.current)
            ),
            modifier = Modifier
                .size(40.dp)
                .height(40.dp)
                .clip(CircleShape)
                .then(modifier)
        )
    } else {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .clip(CircleShape)
                .then(modifier)
        ) {
            Text(
                text = username.first().toString(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}