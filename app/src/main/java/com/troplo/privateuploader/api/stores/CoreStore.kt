package com.troplo.privateuploader.api.stores

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.SocketHandlerService
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.Friend
import com.troplo.privateuploader.data.model.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


object CoreStore {
    var core: MutableState<State?> = mutableStateOf(null)

    fun initializeCore() {
        CoroutineScope(
            Dispatchers.IO
        ).launch {
            val data = TpuApi.retrofitService.getInstanceInfo().execute()
            launch(Dispatchers.Main) {
                if (data.isSuccessful) {
                    val body = data.body()!!
                    core.value = body
                }
            }
        }
    }
}