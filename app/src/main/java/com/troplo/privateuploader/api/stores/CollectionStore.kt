package com.troplo.privateuploader.api.stores

import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.Collection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.net.URISyntaxException


object CollectionStore {
    var collections: MutableStateFlow<List<Collection>> = MutableStateFlow(listOf())

    fun initializeCollections() {
        try {
            CoroutineScope(
                Dispatchers.IO
            ).launch {
                val response = TpuApi.retrofitService.getCollections().execute()
                if (response.isSuccessful) {
                    collections.value = response.body()!!
                }
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }
}