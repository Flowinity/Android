package com.troplo.privateuploader.api.stores

import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URISyntaxException


object UserStore {
  private var user: User? = null

  fun initializeUser(token: String) {
    try {
      CoroutineScope(
        Dispatchers.IO
      ).launch {
        user = TpuApi.retrofitService.getUser().execute().body()
      }
    } catch (e: URISyntaxException) {
      e.printStackTrace()
    }
  }

  fun getUser(): User? {
    return user
  }

  fun resetUser() {
    user = null
  }
}