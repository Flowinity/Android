import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.troplo.privateuploader.R
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.data.model.Upload

@OptIn(ExperimentalMaterialApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun GalleryItem(item: Upload) {
  Column(modifier = Modifier.fillMaxWidth()) {
    // Toolbar
    TopAppBar(
      title = {
        Text(
          text = "Gallery Item",
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          fontSize = MaterialTheme.typography.h6.fontSize
        )
      },
      modifier = Modifier.fillMaxWidth(),
      elevation = AppBarDefaults.TopAppBarElevation
    )

    GlideImage(model = TpuFunctions.image(item.attachment, null), contentDescription = item.name)

    // Item Description
    Text(
      text = "Placeholder",
      modifier = Modifier.padding(16.dp),
      style = MaterialTheme.typography.body2
    )

    // Action Buttons
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Chip(
        colors = ChipDefaults.chipColors(
          backgroundColor = Color.White,
          contentColor = Color.Red
        ),
        onClick = {

        }
      ) {
        Text(text = "Delete")
      }
    }
  }
}