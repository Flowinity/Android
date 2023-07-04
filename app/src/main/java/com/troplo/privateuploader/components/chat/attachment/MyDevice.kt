package com.troplo.privateuploader.components.chat.attachment

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.stores.UploadStore
import com.troplo.privateuploader.data.model.UploadTarget

@OptIn(ExperimentalPermissionsApi::class, ExperimentalLayoutApi::class)
@Composable
@Preview
fun MyDevice() {
    val viewModel = remember { MyDeviceViewModel() }
    val context = LocalContext.current

    val mediaPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        rememberPermissionState(
            android.Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        rememberPermissionState(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    if (mediaPermissionState.status.isGranted) {
        viewModel.loadImages(context)
        LazyColumn {
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    viewModel.images.forEach { upload ->
                        Box(
                            modifier = Modifier.requiredHeight(120.dp).requiredWidth(120.dp)
                        ) {
                            UriPreview(upload, onClick = {
                                if (UploadStore.uploads.find { it.uri == upload.uri } != null) {
                                    Log.d(
                                        "MyDevice",
                                        "Removing ${upload.name} from attachments to upload."
                                    )
                                    UploadStore.uploads.removeIf { it.uri == upload.uri }
                                } else {
                                    Log.d(
                                        "MyDevice",
                                        "Adding ${upload.name} to attachments to upload."
                                    )
                                    UploadStore.uploads.add(upload)
                                    Log.d(
                                        "MyDevice",
                                        "Uploads: ${UploadStore.uploads.map { it.name }}"
                                    )
                                }
                            })
                        }
                    }
                }
            }
        }
        if (viewModel.images.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ImageNotSupported, contentDescription = "No images found.")
                Text("No files could be found on your device.")
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("This feature requires permission to access your local media files.")
            Button(onClick = { mediaPermissionState.launchPermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}

class MyDeviceViewModel : ViewModel() {
    val images = mutableStateListOf<UploadTarget>()

    fun loadImages(context: Context) {
        val contentResolver: ContentResolver = context.contentResolver
        val imagesUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        // Columns to retrieve from the query
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME)

        // Sorting by date modified in descending order
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        // Perform the query
        var cursor: Cursor? = null
        try {
            val bundle = Bundle()
            bundle.putInt(ContentResolver.QUERY_ARG_LIMIT, 20)
            cursor = contentResolver.query(imagesUri, projection, bundle, null)
            cursor?.let {
                while (cursor.moveToNext()) {
                    // Retrieve the image ID
                    val imageId =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))

                    // Create the content URI for the image
                    val imageUri = ContentUris.withAppendedId(imagesUri, imageId)

                    // Add the image URI to the list
                    Log.d("MediaStoreUtils", "Found image: $imageUri")
                    images.add(
                        UploadTarget(
                            uri = imageUri,
                            name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                                ?: "unknown.file"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("MediaStoreUtils", "Error retrieving images: ${e.message}")
        } finally {
            cursor?.close()
        }
    }
}