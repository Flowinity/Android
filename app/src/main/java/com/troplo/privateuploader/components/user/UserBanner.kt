package com.troplo.privateuploader.components.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.data.model.User

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
@Preview
fun UserBanner(user: User? = UserStore.getUser()) {
    Box {
        GlideImage(
            model = TpuFunctions.image(user?.banner, null),
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxWidth()
              .height(200.dp),
            contentDescription = null
        )
    }
}