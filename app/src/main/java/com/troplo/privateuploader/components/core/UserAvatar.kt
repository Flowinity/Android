package com.troplo.privateuploader.components.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.imageLoader
import com.troplo.privateuploader.api.stores.FriendStore
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAvatar(
    avatar: String?,
    username: String,
    modifier: Modifier = Modifier,
    showStatus: Boolean = true
) {
    Box {
        val imageSize = 40.dp
        val dotSize = 7.dp
        val friends = FriendStore.friends.collectAsState()
        val friend = friends.value.find { it.otherUser?.username == username }

        if (avatar != null) {
            Image(
                contentDescription = "User profile picture",
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .dispatcher(Dispatchers.IO)
                        .data(data = TpuFunctions.image(avatar, null))
                        .apply {
                            size(Size.ORIGINAL)
                        }
                        .build(),
                    imageLoader = imageLoader(LocalContext.current)
                ),
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape)
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

        if(friend != null && showStatus) {
            BoxWithConstraints(modifier = Modifier.matchParentSize()) {
                Row(
                    modifier = Modifier
                        .padding(2.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Text(text = "", modifier = Modifier.width(28.dp))
                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .clip(CircleShape)
                            .background(
                                Color(
                                    TpuFunctions.getStatusDetails(
                                        friend?.otherUser?.status ?: "offline"
                                    ).first
                                )
                            )
                            .align(Alignment.Bottom)
                            .border(
                                width = 0.3.dp,
                                color = MaterialTheme.colorScheme.background,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}