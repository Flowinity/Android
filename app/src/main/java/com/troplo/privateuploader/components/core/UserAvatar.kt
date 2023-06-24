package com.troplo.privateuploader.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.troplo.privateuploader.api.TpuFunctions

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserAvatar(
    avatar: String?,
    username: String,
    modifier: Modifier = Modifier,
) {
    if(avatar != null) {
        GlideImage(
            model = TpuFunctions.image(avatar, null),
            contentDescription = "User menu",
            modifier = Modifier
                .size(40.dp)
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