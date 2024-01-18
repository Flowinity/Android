package com.troplo.privateuploader.components.chat

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.troplo.privateuploader.data.model.EmbedDataV2

@Composable
fun EmbedText(embed: EmbedDataV2) {
    for (text in embed.text ?: emptyList()) {
        Text(
            text = text.text,
        modifier = Modifier.padding(top = 8.dp),
        style = if(text.heading == true) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium
        )
    }
}