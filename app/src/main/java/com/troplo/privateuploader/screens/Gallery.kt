package com.troplo.privateuploader.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.components.gallery.GalleryItem
import com.troplo.privateuploader.data.model.Gallery
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun GalleryScreen(
) {
    val loading = remember { mutableStateOf(true) }
    val token = SessionManager(LocalContext.current).getAuthToken() ?: ""
    val galleryViewModel = remember { GalleryViewModel() }
    val galleryItems = remember { mutableStateOf(galleryViewModel.gallery) }
    val searchState = remember { mutableStateOf(galleryViewModel.search) }

    LaunchedEffect(Unit) {
        galleryViewModel.getGalleryItems(token).also {
            loading.value = false
        }
    }

    Scaffold(
        topBar = {
            TextField(
                value = searchState.value.value,
                onValueChange = { searchState.value.value = it },
                label = { Text("Search") },
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(8.dp),
                trailingIcon = {
                    IconButton(onClick = {
                        galleryViewModel.getGalleryItems(token)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                },
                singleLine = true
            )
        },
        content = {
            Box {
                LazyColumn(
                    modifier = Modifier
                      .fillMaxSize()
                      .padding(top = it.calculateTopPadding())
                ) {
                    galleryItems.value.value?.gallery?.forEach {
                        item(
                            key = it.id
                        ) {
                            GalleryItem(it)
                        }
                    }
                }
            }
        }
    )
}

class GalleryViewModel : ViewModel() {
    val gallery = mutableStateOf<Gallery?>(null)
    val search = mutableStateOf("")

    fun getGalleryItems(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.getGallery(search = search.value).execute()
            Log.d("TPU.Untagged", response.body().toString())
            withContext(Dispatchers.Main) {
                gallery.value = response.body()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    PrivateUploaderTheme(
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                GalleryScreen()
            }
        }
    )
}