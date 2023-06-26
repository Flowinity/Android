package com.troplo.privateuploader.screens.settings

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import com.troplo.privateuploader.MainActivity


@Composable
fun ChangelogLayout() {
    Card(
        modifier = Modifier
        .fillMaxSize()
    ) {
        Column() {
            ChangelogSection("Beta 2") {
                ChangelogItem("Remove Glide for Coil image rendering (0 width crash)")
                ChangelogItem("Added user avatar caching")
                ChangelogItem("Added HTTP error handling to prevent Retrofit2 crashes")
                ChangelogItem("Added changelog")
                ChangelogItem("Chat automatically re-opens on app launch")
                ChangelogItem("Added keyboard auto-open on chat focus")
                ChangelogItem("Keyboard now hides itself on SlideSwipe")
                ChangelogItem("You can now search with enter key on Gallery")
                ChangelogItem("Added search bar for Gallery")
                ChangelogItem("Read receipt events are now emitted")
                ChangelogItem("Client can understand typing events, and emit them")
                ChangelogItem("Client can understand edit, and messageDelete events")
                ChangelogItem("Added message deleting")
                ChangelogItem("You can now edit messages")
                ChangelogItem("SlideSwipe panels no longer re-render on open")
                ChangelogItem("Added overlapping side panels (SlideSwipe)")
                ChangelogItem("Added member sidebar")
                ChangelogItem("Disabled the ability to view sidebars on non-comms")
            }

            Spacer(modifier = Modifier.height(16.dp))

            ChangelogSection(title = "Beta 1") {
                ChangelogItem("Added initial communications")
                ChangelogItem("Added gallery")
                ChangelogItem("Initial release")
            }
        }
    }
}

@Composable
fun ChangelogSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        content()
    }
}

@Composable
fun ChangelogItem(text: String) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "\u2022",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview
@Composable
fun PreviewChangelogLayout() {
    ChangelogLayout()
}