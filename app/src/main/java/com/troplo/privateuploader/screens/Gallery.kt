package com.troplo.privateuploader.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.components.core.Paginate
import com.troplo.privateuploader.components.gallery.GalleryItem
import com.troplo.privateuploader.data.model.Gallery
import com.troplo.privateuploader.data.model.Pager
import com.troplo.privateuploader.data.model.TenorResponse
import com.troplo.privateuploader.data.model.Upload
import com.troplo.privateuploader.data.model.defaultUser
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
@Preview(showBackground = true)
fun GalleryScreen(
    // gallery or starred
    type: String = "gallery",
    inline: Boolean = false,
    onClick: (Upload) -> Unit = {}
) {
    val galleryViewModel = remember { GalleryViewModel() }
    val searchState = remember { mutableStateOf(galleryViewModel.search) }

    LaunchedEffect(Unit) {
        galleryViewModel.getGalleryItems(type)
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
                        galleryViewModel.getGalleryItems(type)
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
            if(galleryViewModel.loading.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            } else {
                Box {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = it.calculateTopPadding(),
                                bottom = it.calculateBottomPadding()
                            )
                    ) {
                        galleryViewModel.gallery.value?.gallery?.forEach {
                            item(
                                key = it.id
                            ) {
                                GalleryItem(it, inline, onClick = { onClick(it) })
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Paginate(modelValue = galleryViewModel.gallery.value?.pager?.currentPage ?: 1, totalPages = galleryViewModel.gallery.value?.pager?.totalPages ?: 1, onUpdateModelValue = {
                galleryViewModel.gallery.value?.pager?.currentPage = it
                galleryViewModel.getGalleryItems(type)
            })
        }
    )
}

class GalleryViewModel : ViewModel() {
    val gallery = mutableStateOf<Gallery?>(null)
    val search = mutableStateOf("")
    val loading = mutableStateOf(true)

    fun getGalleryItems(type: String = "gallery") {
        this.loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            if(type !== "tenor") {
                val response: Response<Gallery> =
                    if (type == "starred") TpuApi.retrofitService.getStarredGallery(
                        search = search.value,
                        page = gallery.value?.pager?.currentPage ?: 1
                    ).execute()
                    else TpuApi.retrofitService.getGallery(
                        search = search.value,
                        page = gallery.value?.pager?.currentPage ?: 1
                    ).execute()

                withContext(Dispatchers.Main) {
                    gallery.value = response.body()
                    loading.value = false
                }
            } else {
                val response: Response<TenorResponse> = TpuApi.retrofitService.getTenorGallery(next = "", search = search.value).execute()

                withContext(Dispatchers.Main) {
                    if(response.isSuccessful) {
                        val body = response.body()
                        gallery.value = body?.results?.map {
                            Upload(
                                id = it.created.toInt(),
                                name = it.title,
                                attachment = it.media_formats.gif.url,
                                type = "image-tenor",
                                collections = listOf(),
                                starred = null,
                                createdAt = "",
                                updatedAt = "",
                                data = null,
                                deletable = false,
                                fileSize = 0,
                                originalFilename = "",
                                textMetadata = "",
                                urlRedirect = "",
                                user = defaultUser(),
                                userId = 0
                            )
                        }.let {
                            Gallery(
                                pager = Pager(
                                    currentPage = 0,
                                    totalPages = 0,
                                    totalItems = 0,
                                    endIndex = 0,
                                    startIndex = 0,
                                    pageSize = 0,
                                    pages = listOf(),
                                    endPage = 0,
                                    startPage = 0
                                ),
                                gallery = it ?: listOf()
                            )
                        }
                        loading.value = false
                    }
                }
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