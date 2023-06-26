package com.troplo.privateuploader.api.stores

import android.content.Context
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.net.URISyntaxException


object UserStore {
    var user: MutableStateFlow<User?> = MutableStateFlow(null)

    fun initializeUser(context: Context) {
        try {
            if (SessionManager(context).getUserCache() != null) {
                println("User cache ${SessionManager(context).getUserCache()}")
                user.value = SessionManager(context).getUserCache()
            }
            CoroutineScope(
                Dispatchers.IO
            ).launch {
                user.value = TpuApi.retrofitService.getUser().execute().body()
                SessionManager(context).setUserCache(user.value)
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun getUser(): User? {
        return user.value
    }

    fun resetUser() {
        user.value = null
    }
}