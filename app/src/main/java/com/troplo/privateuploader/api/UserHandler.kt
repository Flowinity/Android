package com.troplo.privateuploader.api

import android.content.Context
import com.troplo.privateuploader.data.model.User
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URI
import java.net.URISyntaxException
import java.util.Collections


object UserHandler {
  private var user: User? = null

  fun initializeUser(token: String) {
    try {
      CoroutineScope(
        Dispatchers.IO
      ).launch {
        user = TpuApi.retrofitService.getUser(token).execute().body()
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