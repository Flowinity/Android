package com.troplo.privateuploader.api.stores

import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.Collection
import com.troplo.privateuploader.data.model.Collections
import com.troplo.privateuploader.data.model.Pager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.net.URISyntaxException


object CollectionStore {
    var collections: MutableStateFlow<Collections> = MutableStateFlow(Collections(listOf(), Pager(0, 0, 0, 0, listOf(), 0, 0, 0, 0)))

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