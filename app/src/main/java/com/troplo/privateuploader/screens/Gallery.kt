package com.troplo.privateuploader.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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

@Composable
fun GalleryScreen(
) {
    val loading = remember { mutableStateOf(true) }
    val token = SessionManager(LocalContext.current).fetchAuthToken() ?: ""
    val galleryViewModel = remember { GalleryViewModel() }
    val galleryItems = remember { mutableStateOf(galleryViewModel.gallery) }

    LaunchedEffect(Unit) {
        galleryViewModel.getGalleryItems(token).also {
            loading.value = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            galleryItems.value.value?.gallery?.forEach {
                item {
                    GalleryItem(it)
                }
            }
        }
    }
}

class GalleryViewModel : ViewModel() {
    val gallery = mutableStateOf<Gallery?>(null)

    fun getGalleryItems(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.getGallery(token).execute()
            println(response.body())
            withContext(Dispatchers.Main) {
                gallery.value = response.body()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    PrivateUploaderTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            GalleryScreen()
        }
    }
}